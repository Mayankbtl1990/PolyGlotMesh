package com.infotact.polyglotmesh.api;

import java.time.Duration;

import com.infotact.polyglotmesh.metrics.ExecutionMetrics;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/benchmarks")
public class MockBenchmarkController {

    private static final long MOCK_DELAY_MS = 50;

    private final ExecutionMetrics metrics;

    public MockBenchmarkController(ExecutionMetrics metrics) {
        this.metrics = metrics;
    }

    @PostMapping(
            value = "/mock-rest",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<MockResult> mockRest() {
        return Mono.defer(() -> {
            long startedAt = System.nanoTime();

            return Mono.delay(Duration.ofMillis(MOCK_DELAY_MS))
                    .map(ignored -> {
                        double observedMs =
                                (System.nanoTime() - startedAt) / 1_000_000.0;

                        metrics.recordMock(observedMs);

                        return new MockResult(
                                "SIMULATED_DELAY_ONLY",
                                MOCK_DELAY_MS,
                                observedMs,
                                false
                        );
                    });
        });
    }

    public record MockResult(
            String kind,
            long configuredDelayMs,
            double observedMs,
            boolean equivalentScriptExecuted
    ) {
    }
}