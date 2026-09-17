package com.infotact.polyglotmesh.runtime;

// import java.io.ByteArrayOutputStream;
// import java.nio.charset.StandardCharsets;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
// import org.graalvm.polyglot.EnvironmentAccess;
// import org.graalvm.polyglot.HostAccess;
// import org.graalvm.polyglot.io.IOAccess;
import org.springframework.stereotype.Service;

@Service
public class GuestRuntime {

    private final Engine engine;

    public GuestRuntime(Engine engine) {
        this.engine = engine;
    }

    public ExecutionResult execute(String language, String code) {
        String languageId = resolveLanguage(language);

        LimitedOutputStream stdout = new LimitedOutputStream(64 * 1024);
        LimitedOutputStream stderr = new LimitedOutputStream(64 * 1024);

        long startedAt = System.nanoTime();

        try (Context context = new RestrictedContextFactory().create(
            engine,
            languageId,
            stdout,
            stderr
        )) {
            context.eval(languageId, code);
        }

        double durationMs = (System.nanoTime() - startedAt) / 1_000_000.0;

        return new ExecutionResult(
         language,
            stdout.text(),
            stderr.text(),
            durationMs,
            stdout.truncated() || stderr.truncated()
        );
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