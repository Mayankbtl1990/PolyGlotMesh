package com.infotact.polyglotmesh.scripts;

public record SavedScript(
        String id,
        String name,
        String language,
        String code,
        String updatedAt
) {
}