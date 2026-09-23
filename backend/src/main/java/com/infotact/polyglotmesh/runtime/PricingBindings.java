package com.infotact.polyglotmesh.runtime;

import java.util.Map;
import java.util.Set;

import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;

public final class PricingBindings implements ProxyObject {

    private static final Set<String> FIELDS = Set.of(
            "basePrice",
            "discount",
            "finalPrice"
    );

    private final Map<String, Object> backingMap;

    public PricingBindings(Map<String, Object> backingMap) {
        this.backingMap = backingMap;
    }

    @Override
    public Object getMember(String key) {
        if (!hasMember(key)) {
            return null;
        }

        return backingMap.get(key);
    }

    @Override
    public Object getMemberKeys() {
        return new String[]{
                "basePrice",
                "discount",
                "finalPrice"
        };
    }

    @Override
    public boolean hasMember(String key) {
        return FIELDS.contains(key);
    }

    @Override
    public void putMember(String key, Value value) {
        if (!"finalPrice".equals(key)) {
            throw new IllegalArgumentException(
                    "Only finalPrice can be updated"
            );
        }

        if (!value.fitsInDouble()) {
            throw new IllegalArgumentException(
                    "finalPrice must be numeric"
            );
        }

        double number = value.asDouble();

        if (!Double.isFinite(number) || number < 0) {
            throw new IllegalArgumentException(
                    "finalPrice must be finite and non-negative"
            );
        }

        backingMap.put("finalPrice", number);
    }
}