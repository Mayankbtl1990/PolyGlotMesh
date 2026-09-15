package com.infotact.polyglotmesh.api;

import com.infotact.polyglotmesh.runtime.ExecutionResult;
import com.infotact.polyglotmesh.runtime.GuestRuntime;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@RestController
@RequestMapping("/api/executions")
public class ExecutionController {

    private final GuestRuntime runtime;
    private final Scheduler scheduler;

    public ExecutionController(
            GuestRuntime runtime,
            @Qualifier("guestExecutionScheduler") Scheduler scheduler
    ) {
        this.runtime = runtime;
        this.scheduler = scheduler;
    }

    @PostMapping
    public Mono<ExecutionResult> execute(
            @Valid @RequestBody ExecutionRequest request
    ) {
        return Mono.fromCallable(
                () -> runtime.execute(request.language(), request.code())
        ).subscribeOn(scheduler);
    }
}