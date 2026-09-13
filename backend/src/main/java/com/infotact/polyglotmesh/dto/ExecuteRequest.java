package com.infotact.polyglotmesh.dto;

import jakarta.validation.constraints.NotBlank;

public record ExecuteRequest(
    @NotBlank(message = "Language is required") String language,
    @NotBlank(message = "Script content cannot be empty") String script
) {}