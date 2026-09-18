package com.infotact.polyglotmesh.runtime;

import java.util.List;

public class ScriptExecutionException extends RuntimeException {

    private final String code;
    private final ExecutionResult execution;
    private final List<String> guestStack;

    public ScriptExecutionException(
            String code,
            String message,
            ExecutionResult execution,
            List<String> guestStack
    ) {
        super(message);
        this.code = code;
        this.execution = execution;
        this.guestStack = List.copyOf(guestStack);
    }

    public String code() {
        return code;
    }

    public ExecutionResult execution() {
        return execution;
    }

    public List<String> guestStack() {
        return guestStack;
    }
}