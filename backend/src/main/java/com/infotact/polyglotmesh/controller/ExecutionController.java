package com.infotact.polyglotmesh.controller;

import com.infotact.polyglotmesh.runtime.ExecutionRequest;
import com.infotact.polyglotmesh.service.PolyglotEngineService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/executions")
public class ExecutionController {

    private final PolyglotEngineService executionService;

    public ExecutionController(PolyglotEngineService executionService) {
        this.executionService = executionService;
    }

    @PostMapping
    public Mono<String> execute(@Valid @RequestBody ExecutionRequest request) {
        return executionService.executeScript(request.language(), request.code());
    }
}