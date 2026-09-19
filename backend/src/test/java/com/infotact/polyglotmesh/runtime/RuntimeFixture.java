package com.infotact.polyglotmesh.runtime;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.graalvm.polyglot.Engine;

public final class RuntimeFixture implements AutoCloseable {

    private final Engine engine = Engine.create();

    private final ScheduledExecutorService deadlines =
            Executors.newScheduledThreadPool(2);

    public GuestRuntime runtime(long timeoutMs) {
        return new GuestRuntime(
                engine,
                new RestrictedContextFactory(),
                deadlines,
                new ExecutionProperties(timeoutMs)
        );
    }

    @Override
    public void close() {
        deadlines.shutdownNow();
        engine.close();
    }
}