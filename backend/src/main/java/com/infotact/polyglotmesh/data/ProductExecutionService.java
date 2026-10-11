package com.infotact.polyglotmesh.data;

import java.util.HashMap;
import java.util.Map;

import com.infotact.polyglotmesh.runtime.GuestRuntime;

import org.springframework.stereotype.Service;

@Service
public class ProductExecutionService {

    private final MockProductCatalog catalog;
    private final GuestRuntime runtime;

    public ProductExecutionService(
            MockProductCatalog catalog,
            GuestRuntime runtime
    ) {
        this.catalog = catalog;
        this.runtime = runtime;
    }

    public ProductExecutionResult execute(
            String productId,
            String language,
            String code
    ) {
        Product product = catalog.findById(productId);

        Map<String, Object> pricing = new HashMap<>();
        pricing.put("basePrice", product.basePrice());
        pricing.put("discount", product.discount());
        pricing.put("finalPrice", product.basePrice());

        var execution = runtime.executeWithPricing(
                language,
                code,
                pricing
        );

        double finalPrice =
                ((Number) pricing.get("finalPrice")).doubleValue();

        return new ProductExecutionResult(
                product,
                finalPrice,
                "IN_MEMORY_MOCK_CATALOG",
                execution
        );
    }
}