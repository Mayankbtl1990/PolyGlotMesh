package com.infotact.polyglotmesh.data;

import com.infotact.polyglotmesh.runtime.ExecutionResult;

public record ProductExecutionResult(
        Product product,
        double finalPrice,
        String dataSource,
        ExecutionResult execution
) {
}