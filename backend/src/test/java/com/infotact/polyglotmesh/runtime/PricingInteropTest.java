package com.infotact.polyglotmesh.runtime;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PricingInteropTest {

    @Test
    void pythonUpdatesTheOriginalJavaMap() {
        Map<String, Object> pricing = new HashMap<>();
        pricing.put("basePrice", 100.0);
        pricing.put("discount", 0.15);
        pricing.put("finalPrice", 0.0);

        try (RuntimeFixture fixture = new RuntimeFixture()) {
            ExecutionResult result = fixture.runtime(30_000)
                    .executeWithPricing(
                            "python",
                            """
                            pricing.finalPrice = (
                                pricing.basePrice * (1 - pricing.discount)
                            )
                            print("Final price:", pricing.finalPrice)
                            """,
                            pricing
                    );

            assertThat(pricing.get("finalPrice")).isEqualTo(85.0);
            assertThat(pricing.get("finalPrice")).isInstanceOf(Double.class);
            assertThat(result.stdout()).contains("85");
        }
    }
}