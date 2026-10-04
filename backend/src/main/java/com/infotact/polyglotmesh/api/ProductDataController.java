package com.infotact.polyglotmesh.api;

import java.util.List;

import com.infotact.polyglotmesh.data.MockProductCatalog;
import com.infotact.polyglotmesh.data.Product;
import com.infotact.polyglotmesh.data.ProductExecutionResult;
import com.infotact.polyglotmesh.data.ProductExecutionService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@RestController
@RequestMapping("/api/data")
public class ProductDataController {

    private final MockProductCatalog catalog;
    private final ProductExecutionService executionService;
    private final Scheduler scheduler;

    public ProductDataController(
            MockProductCatalog catalog,
            ProductExecutionService executionService,
            @Qualifier("guestExecutionScheduler") Scheduler scheduler
    ) {
        this.catalog = catalog;
        this.executionService = executionService;
        this.scheduler = scheduler;
    }

    @GetMapping("/products")
    public List<Product> products() {
        return catalog.findAll();
    }

    @PostMapping(
            value = "/execute",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ProductExecutionResult> execute(
            @Valid @RequestBody ProductExecutionRequest request
    ) {
        return Mono.fromCallable(() -> executionService.execute(
                request.productId(),
                request.language(),
                request.code()
        )).subscribeOn(scheduler);
    }
}