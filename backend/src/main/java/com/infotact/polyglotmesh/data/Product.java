package com.infotact.polyglotmesh.data;

public record Product(
        String id,
        String name,
        double basePrice,
        double discount
) {
}