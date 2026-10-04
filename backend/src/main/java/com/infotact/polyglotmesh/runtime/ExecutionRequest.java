package com.infotact.polyglotmesh.runtime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;

public record ExecutionRequest(
        @NotBlank(message = "Language cannot be blank")
        String language,

        @NotBlank(message = "Code cannot be blank")
        @Size(max = 65536, message = "Code size exceeds maximum limit")
        String code,

        Map<String, Object> bindings,
        long timeoutMs
) {
    public ExecutionRequest(String language, String code) {
        this(language, code, Map.of(), 30_000);
    }
}