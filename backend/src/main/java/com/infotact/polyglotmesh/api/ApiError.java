package com.infotact.polyglotmesh.api;

import java.util.List;

import com.infotact.polyglotmesh.runtime.ExecutionResult;

public record ApiError(
        String code,
        String message,
        ExecutionResult execution,
        List<String> guestStack
) {

    public ApiError(String code, String message) {
        this(code, message, null, List.of());
    }
}