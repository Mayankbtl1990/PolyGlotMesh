package com.infotact.polyglotmesh.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProductExecutionRequest(

        @NotBlank(message = "productId is required")
        @Size(max = 40, message = "productId must not exceed 40 characters")
        String productId,

        @NotBlank(message = "language is required")
        @Pattern(
                regexp = "python|javascript",
                message = "language must be python or javascript"
        )
        String language,

        @NotBlank(message = "code must not be blank")
        @Size(
                max = 20_000,
                message = "code must not exceed 20000 characters"
        )
        String code
) {
}