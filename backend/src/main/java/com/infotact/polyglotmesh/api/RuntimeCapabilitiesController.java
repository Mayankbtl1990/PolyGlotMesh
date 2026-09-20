package com.infotact.polyglotmesh.api;

import java.util.List;

import com.infotact.polyglotmesh.runtime.ExecutionProperties;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/runtime")
public class RuntimeCapabilitiesController {

    private final ExecutionProperties properties;

    public RuntimeCapabilitiesController(ExecutionProperties properties) {
        this.properties = properties;
    }

    @GetMapping("/capabilities")
    public Capabilities capabilities() {
        return new Capabilities(
                List.of("python", "javascript"),
                properties.timeoutMs(),
                "guest evaluation; excludes queue wait and context construction",
                64 * 1024,
                false,
                false,
                false,
                List.of(
                        "Host class lookup disabled",
                        "Host member access disabled",
                        "Guest host-filesystem and socket IO disabled",
                        "Native access disabled",
                        "Process and thread creation disabled",
                        "Environment access disabled"
                )
        );
    }

    public record Capabilities(
            List<String> languages,
            long evaluationTimeoutMs,
            String timeoutScope,
            int capturedBytesPerStream,
            boolean hardMemoryLimit,
            boolean hardCpuQuota,
            boolean authenticated,
            List<String> restrictions
    ) {
    }
}