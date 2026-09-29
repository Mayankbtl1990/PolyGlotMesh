package com.infotact.polyglotmesh.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Configuration
public class StorageSchedulingConfiguration {

    @Bean(name = "scriptStorageScheduler", destroyMethod = "dispose")
    public Scheduler scriptStorageScheduler() {
        return Schedulers.newBoundedElastic(
                4,
                32,
                "script-storage"
        );
    }
}