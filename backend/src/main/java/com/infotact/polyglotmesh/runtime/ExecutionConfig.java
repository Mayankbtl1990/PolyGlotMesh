package com.infotact.polyglotmesh.runtime;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.graalvm.polyglot.Engine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExecutionConfig {

    @Bean(destroyMethod = "close")
    public Engine polyglotEngine() {
        return Engine.newBuilder().build();
    }

    @Bean(name = "guestDeadlineExecutor", destroyMethod = "shutdown")
    public ScheduledExecutorService guestDeadlineExecutor() {
        return Executors.newScheduledThreadPool(2);
    }
}