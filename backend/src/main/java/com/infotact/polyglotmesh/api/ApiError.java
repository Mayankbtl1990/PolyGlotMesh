package com.infotact.polyglotmesh.api;

public record ApiError(
        String code,
        String message
) {
}
