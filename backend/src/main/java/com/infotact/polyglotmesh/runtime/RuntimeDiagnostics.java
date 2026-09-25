package com.infotact.polyglotmesh.runtime;

import java.util.List;

import org.graalvm.polyglot.Engine;
import org.springframework.core.NativeDetector;
import org.springframework.stereotype.Service;

@Service
public class RuntimeDiagnostics {

    private final Engine engine;

    public RuntimeDiagnostics(Engine engine) {
        this.engine = engine;
    }

    public Snapshot snapshot() {
        List<LanguageInfo> languages = engine.getLanguages()
                .entrySet()
                .stream()
                .map(entry -> new LanguageInfo(
                        entry.getKey(),
                        entry.getValue().getName(),
                        entry.getValue().getVersion()
                ))
                .sorted((left, right) -> left.id().compareTo(right.id()))
                .toList();

        return new Snapshot(
                NativeDetector.inNativeImage() ? "NATIVE_IMAGE" : "JVM",
                System.getProperty("java.version"),
                System.getProperty("java.vendor"),
                System.getProperty("os.name"),
                System.getProperty("os.arch"),
                engine.getVersion(),
                languages
        );
    }

    public record Snapshot(
            String executionMode,
            String javaVersion,
            String javaVendor,
            String operatingSystem,
            String architecture,
            String engineVersion,
            List<LanguageInfo> installedLanguages
    ) {
    }

    public record LanguageInfo(
            String id,
            String name,
            String version
    ) {
    }
}