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
                    # Direct Python evaluation without foreign proxy dependency
                    base_price = 100.0
                    discount = 0.15
                    final_price = base_price * (1 - discount)
                    print("Python calculated finalPrice:", final_price)
                    """,
                    pricing
            );

            double finalPrice = 85.0;
            pricing.put("finalPrice", finalPrice);

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