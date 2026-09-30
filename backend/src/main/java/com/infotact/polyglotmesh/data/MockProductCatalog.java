package com.infotact.polyglotmesh.data;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class MockProductCatalog {

    private final Map<String, Product> products = Map.of(
            "keyboard",
            new Product("keyboard", "Mechanical Keyboard", 100.0, 0.15),

            "headphones",
            new Product("headphones", "Wireless Headphones", 200.0, 0.10),

            "monitor",
            new Product("monitor", "Office Monitor", 300.0, 0.05)
    );

    public List<Product> findAll() {
        return products.values()
                .stream()
                .sorted(Comparator.comparing(Product::id))
                .toList();
    }

    public Product findById(String id) {
        Product product = products.get(id);

        if (product == null) {
            throw new ProductNotFoundException();
        }

        return product;
    }
}