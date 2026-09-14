package com.infotact.polyglotmesh.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Configuration
public class ExecutionSchedulingConfiguration {

    @Bean(name = "guestExecutionScheduler", destroyMethod = "dispose")
    public Scheduler guestExecutionScheduler() {
        return Schedulers.newBoundedElastic(
                2,
                16,
                "polyglot-execution"
        );
    }
}