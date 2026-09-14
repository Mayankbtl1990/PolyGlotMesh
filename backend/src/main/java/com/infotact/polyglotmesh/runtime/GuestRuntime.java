package com.infotact.polyglotmesh.runtime;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.EnvironmentAccess;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.io.IOAccess;
import org.springframework.stereotype.Service;

@Service
public class GuestRuntime {

    private final Engine engine;

    public GuestRuntime(Engine engine) {
        this.engine = engine;
    }

    public ExecutionResult execute(String language, String code) {
        String languageId = resolveLanguage(language);

        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();

        long startedAt = System.nanoTime();

        try (Context context = Context.newBuilder(languageId)
                .engine(engine)
                .allowAllAccess(false)
                .allowHostAccess(HostAccess.NONE)
                .allowHostClassLookup(name -> false)
                .allowIO(IOAccess.NONE)
                .allowNativeAccess(false)
                .allowCreateThread(false)
                .allowCreateProcess(false)
                .allowEnvironmentAccess(EnvironmentAccess.NONE)
                .in(java.io.InputStream.nullInputStream())
                .out(stdout)
                .err(stderr)
                .build()) {

            context.eval(languageId, code);
        }

        double durationMs = (System.nanoTime() - startedAt) / 1_000_000.0;

        return new ExecutionResult(
                language,
                stdout.toString(StandardCharsets.UTF_8),
                stderr.toString(StandardCharsets.UTF_8),
                durationMs,
                false
        );
    }

    private String resolveLanguage(String language) {
        if ("python".equals(language)) {
            return "python";
        }

        throw new IllegalArgumentException("Unsupported language: " + language);
    }
}