package com.infotact.polyglotmesh.api;

import com.infotact.polyglotmesh.runtime.ExecutionResult;

public record PricingAuditResponse(
        double basePrice,
        double discount,
        double finalPrice,
        boolean backingMapUpdated,
        String bindingMode,
        ExecutionResult execution
) {
}