package com.infotact.polyglotmesh.api;

import java.util.List;
import java.util.UUID;

import com.infotact.polyglotmesh.scripts.SavedScript;
import com.infotact.polyglotmesh.scripts.ScriptRepository;
import com.infotact.polyglotmesh.scripts.ScriptSummary;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@RestController
@RequestMapping("/api/scripts")
public class ScriptController {

    private final ScriptRepository repository;
    private final Scheduler scheduler;

    public ScriptController(
            ScriptRepository repository,
            @Qualifier("scriptStorageScheduler") Scheduler scheduler
    ) {
        this.repository = repository;
        this.scheduler = scheduler;
    }

    @GetMapping
    public Mono<List<ScriptSummary>> list() {
        return Mono.fromCallable(repository::list)
                .subscribeOn(scheduler);
    }

    @GetMapping("/{id}")
    public Mono<SavedScript> get(@PathVariable UUID id) {
        return Mono.fromCallable(() -> repository.find(id.toString()))
                .subscribeOn(scheduler);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<SavedScript> create(
            @Valid @RequestBody SaveScriptRequest request
    ) {
        return Mono.fromCallable(() -> repository.create(
                request.name(),
                request.language(),
                request.code()
        )).subscribeOn(scheduler);
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<SavedScript> update(
            @PathVariable UUID id,
            @Valid @RequestBody SaveScriptRequest request
    ) {
        return Mono.fromCallable(() -> repository.update(
                id.toString(),
                request.name(),
                request.language(),
                request.code()
        )).subscribeOn(scheduler);
    }
}