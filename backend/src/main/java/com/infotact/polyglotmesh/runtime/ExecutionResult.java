package com.infotact.polyglotmesh.runtime;

public record ExecutionResult(
        String language,
        String stdout,
        String stderr,
        double durationMs,
        boolean outputTruncated
) {
}