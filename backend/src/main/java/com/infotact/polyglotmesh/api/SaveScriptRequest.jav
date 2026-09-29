package com.infotact.polyglotmesh.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SaveScriptRequest(

        @NotBlank(message = "name is required")
        @Size(max = 80, message = "name must not exceed 80 characters")
        String name,

        @NotBlank(message = "language is required")
        @Pattern(
                regexp = "python|javascript|java",
                message = "language must be python, javascript, or java"
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