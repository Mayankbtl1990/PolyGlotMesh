package com.infotact.polyglotmesh.runtime;

import java.util.Map;

public record ExecutionRequest(
        String language,
        String code,
        Map<String, Object> bindings,
        long timeoutMs
) {
    public ExecutionRequest(String language, String code) {
        this(language, code, Map.of(), 30_000);
    }
}