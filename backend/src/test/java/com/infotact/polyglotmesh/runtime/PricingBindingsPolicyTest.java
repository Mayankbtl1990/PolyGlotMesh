package com.infotact.polyglotmesh.runtime;

import java.util.HashMap;
import java.util.Map;

import org.graalvm.polyglot.Context;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PricingBindingsPolicyTest {

    private Map<String, Object> pricingMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("basePrice", 100.0);
        map.put("discount", 0.15);
        map.put("finalPrice", 0.0);
        return map;
    }

    @Test
    void rejectsChangesToReadOnlyPricingFields() {
        Map<String, Object> map = pricingMap();
        PricingBindings bindings = new PricingBindings(map);

        try (Context context = Context.create("js")) {
            var value = context.eval("js", "200");

            assertThatThrownBy(
                    () -> bindings.putMember("basePrice", value)
            ).isInstanceOf(IllegalArgumentException.class);
        }

        assertThat(map.get("basePrice")).isEqualTo(100.0);
    }

    @Test
    void rejectsNegativeFinalPrice() {
        Map<String, Object> map = pricingMap();
        PricingBindings bindings = new PricingBindings(map);

        try (Context context = Context.create("js")) {
            var value = context.eval("js", "-1");

            assertThatThrownBy(
                    () -> bindings.putMember("finalPrice", value)
            ).isInstanceOf(IllegalArgumentException.class);
        }

        assertThat(map.get("finalPrice")).isEqualTo(0.0);
    }

    @Test
    void doesNotExposeUnlistedMapFields() {
        Map<String, Object> map = pricingMap();
        map.put("databasePassword", "not-for-guests");

        PricingBindings bindings = new PricingBindings(map);

        assertThat(bindings.hasMember("databasePassword")).isFalse();
        assertThat(bindings.getMember("databasePassword")).isNull();
    }

    @Test
    void pricingBindingDoesNotLeakIntoNextExecution() {
        try (RuntimeFixture fixture = new RuntimeFixture()) {
            GuestRuntime runtime = fixture.runtime(30_000);

            runtime.executeWithPricing(
                    "python",
                    "pricing.finalPrice = 85",
                    pricingMap()
            );

            ExecutionResult result = runtime.execute(
                    "python",
                    "print('pricing' in globals())"
            );

            assertThat(result.stdout().trim()).isEqualTo("False");
        }
    }
}