package com.infotact.polyglotmesh.controller;

import com.infotact.polyglotmesh.dto.ExecuteRequest;
import com.infotact.polyglotmesh.service.PolyglotEngineService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/execute")
public class PolyglotController {

    private final PolyglotEngineService executionService;

    public PolyglotController(PolyglotEngineService executionService) {
        this.executionService = executionService;
    }

    @PostMapping
    public Mono<String> execute(@Valid @RequestBody ExecuteRequest request) {
        return executionService.executeScript(request.language(), request.script());
    }
}
