package com.infotact.polyglotmesh.runtime;

import jakarta.validation.Validation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExecutionPropertiesTest {

    @Test
    void acceptsDefaultDeadline() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var violations = factory.getValidator()
                    .validate(new ExecutionProperties(5000));

            assertThat(violations).isEmpty();
        }
    }

    @Test
    void rejectsDeadlineBelowSupportedMinimum() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var violations = factory.getValidator()
                    .validate(new ExecutionProperties(1));

            assertThat(violations).isNotEmpty();
        }
    }
}