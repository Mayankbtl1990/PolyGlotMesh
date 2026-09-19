package com.infotact.polyglotmesh.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class GuestRuntime {

    private static final Logger log =
            LoggerFactory.getLogger(GuestRuntime.class);

    private static final int RUNNING = 0;
    private static final int FINISHED = 1;
    private static final int TIMED_OUT = 2;

    private final Engine engine;
    private final RestrictedContextFactory contextFactory;
    private final ScheduledExecutorService deadlineExecutor;
    private final ExecutionProperties properties;

    public GuestRuntime(
            Engine engine,
            RestrictedContextFactory contextFactory,
            @Qualifier("guestDeadlineExecutor")
            ScheduledExecutorService deadlineExecutor,
            ExecutionProperties properties
    ) {
        this.engine = engine;
        this.contextFactory = contextFactory;
        this.deadlineExecutor = deadlineExecutor;
        this.properties = properties;
    }

    public ExecutionResult execute(String language, String code) {
        String languageId = resolveLanguage(language);

        LimitedOutputStream stdout = new LimitedOutputStream(64 * 1024);
        LimitedOutputStream stderr = new LimitedOutputStream(64 * 1024);

        long startedAt = System.nanoTime();
        AtomicInteger phase = new AtomicInteger(RUNNING);

        String filename = "python".equals(language)
                ? "script.py"
                : "script.js";

        Source source = Source.newBuilder(languageId, code, filename)
                .buildLiteral();

        try (Context context = contextFactory.create(
                engine,
                languageId,
                stdout,
                stderr
        )) {
            var deadline = deadlineExecutor.schedule(() -> {
                if (phase.compareAndSet(RUNNING, TIMED_OUT)) {
                    try {
                        context.close(true);
                    } catch (RuntimeException failure) {
                        log.warn("Guest context cancellation failed", failure);
                    }
                }
            }, properties.timeoutMs(), TimeUnit.MILLISECONDS);

            try {
                context.eval(source);
            } finally {
                phase.compareAndSet(RUNNING, FINISHED);
                deadline.cancel(false);
            }
        } catch (PolyglotException failure) {
            ExecutionResult partial = snapshot(
                    language, stdout, stderr, startedAt
            );

            if (phase.get() == TIMED_OUT) {
                throw timeout(partial);
            }

            if (failure.isGuestException() || failure.isSyntaxError()) {
                throw new ScriptExecutionException(
                        "SCRIPT_ERROR",
                        shorten(failure.getMessage(), 2000),
                        partial,
                        guestFrames(failure)
                );
            }

            throw failure;
        }

        ExecutionResult result = snapshot(
                language, stdout, stderr, startedAt
        );

        if (phase.get() == TIMED_OUT) {
            throw timeout(result);
        }

        return result;
    }

    private ScriptExecutionException timeout(ExecutionResult partial) {
        return new ScriptExecutionException(
                "EXECUTION_TIMEOUT",
                "Guest evaluation exceeded "
                        + properties.timeoutMs()
                        + " ms and cancellation was requested.",
                partial,
                List.of()
        );
    }

    private ExecutionResult snapshot(
            String language,
            LimitedOutputStream stdout,
            LimitedOutputStream stderr,
            long startedAt
    ) {
        return new ExecutionResult(
                language,
                stdout.text(),
                stderr.text(),
                (System.nanoTime() - startedAt) / 1_000_000.0,
                stdout.truncated() || stderr.truncated()
        );
    }

    private List<String> guestFrames(PolyglotException failure) {
        List<String> frames = new ArrayList<>();

        for (var frame : failure.getPolyglotStackTrace()) {
            if (!frame.isGuestFrame()) {
                continue;
            }

            var location = frame.getSourceLocation();

            String position = location == null
                    ? "unknown source"
                    : location.getSource().getName()
                            + ":" + location.getStartLine();

            frames.add(
                    shorten(frame.getRootName(), 120)
                            + " (" + shorten(position, 180) + ")"
            );

            if (frames.size() == 8) {
                break;
            }
        }

        return frames;
    }

    private String shorten(String value, int limit) {
        if (value == null || value.isBlank()) {
            return "Guest execution failed";
        }

        return value.length() > limit
                ? value.substring(0, limit) + "..."
                : value;
    }

    private String resolveLanguage(String language) {
        return switch (language) {
            case "python" -> "python";
            case "javascript" -> "js";
            default -> throw new IllegalArgumentException(
                    "Unsupported language: " + language
            );
        };
    }
}