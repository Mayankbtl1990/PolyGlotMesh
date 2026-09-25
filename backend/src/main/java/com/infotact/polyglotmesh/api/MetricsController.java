package com.infotact.polyglotmesh.api;

import com.infotact.polyglotmesh.metrics.ExecutionMetrics;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

    private final ExecutionMetrics metrics;

    public MetricsController(ExecutionMetrics metrics) {
        this.metrics = metrics;
    }

    @GetMapping
    public ExecutionMetrics.Snapshot metrics() {
        return metrics.snapshot();
    }
}
