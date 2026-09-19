package com.infotact.polyglotmesh.api;

import java.util.HashMap;
import java.util.Map;

import com.infotact.polyglotmesh.runtime.GuestRuntime;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@RestController
@RequestMapping("/api/audits")
public class PricingAuditController {

    private final GuestRuntime runtime;
    private final Scheduler scheduler;

    public PricingAuditController(
            GuestRuntime runtime,
            @Qualifier("guestExecutionScheduler") Scheduler scheduler
    ) {
        this.runtime = runtime;
        this.scheduler = scheduler;
    }

    @PostMapping(
            value = "/pricing",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<PricingAuditResponse> pricingAudit() {
        return Mono.fromCallable(() -> {
            Map<String, Object> pricing = new HashMap<>();
            pricing.put("basePrice", 100.0);
            pricing.put("discount", 0.15);
            pricing.put("finalPrice", 0.0);

            var execution = runtime.executeWithPricing(
                    "python",
                    """
                    pricing.finalPrice = (
                        pricing.basePrice * (1 - pricing.discount)
                    )
                    print("Python updated finalPrice:", pricing.finalPrice)
                    """,
                    pricing
            );

            double finalPrice =
                    ((Number) pricing.get("finalPrice")).doubleValue();

            return new PricingAuditResponse(
                    100.0,
                    0.15,
                    finalPrice,
                    finalPrice == 85.0,
                    "Controlled proxy backed by the original Java HashMap",
                    execution
            );
        }).subscribeOn(scheduler);
    }
}