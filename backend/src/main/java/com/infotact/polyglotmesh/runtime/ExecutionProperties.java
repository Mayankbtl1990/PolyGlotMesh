package com.infotact.polyglotmesh.runtime;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "polyglotmesh.execution")
public record ExecutionProperties(

        @DefaultValue("5000")
        @Min(100)
        @Max(30000)
        long timeoutMs

) {
}