package com.infotact.polyglotmesh.metrics;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ExecutionMetrics {

    private static final int RECENT_LIMIT = 50;

    private final ArrayDeque<Sample> recent = new ArrayDeque<>();

    private long guestCount;
    private long guestFailures;
    private double guestTotalMs;

    private long mockCount;
    private double mockTotalMs;

    public synchronized void recordGuest(
            String language,
            boolean success,
            double durationMs
    ) {
        validateDuration(durationMs);

        guestCount++;
        guestTotalMs += durationMs;

        if (!success) {
            guestFailures++;
        }

        append(new Sample(
                "GUEST",
                language,
                success,
                durationMs,
                Instant.now().toString()
        ));
    }

    public synchronized void recordMock(double durationMs) {
        validateDuration(durationMs);

        mockCount++;
        mockTotalMs += durationMs;

        append(new Sample(
                "MOCK_DELAY",
                "simulated-rest",
                true,
                durationMs,
                Instant.now().toString()
        ));
    }

    public synchronized Snapshot snapshot() {
        return new Snapshot(
                guestCount,
                guestFailures,
                guestCount == 0 ? 0 : guestTotalMs / guestCount,
                mockCount,
                mockCount == 0 ? 0 : mockTotalMs / mockCount,
                List.copyOf(recent)
        );
    }

    private void append(Sample sample) {
        if (recent.size() == RECENT_LIMIT) {
            recent.removeFirst();
        }

        recent.addLast(sample);
    }

    private void validateDuration(double durationMs) {
        if (!Double.isFinite(durationMs) || durationMs < 0) {
            throw new IllegalArgumentException(
                    "Duration must be finite and non-negative"
            );
        }
    }

    public record Snapshot(
            long guestCount,
            long guestFailures,
            double guestMeanMs,
            long mockCount,
            double mockMeanMs,
            List<Sample> recent
    ) {
    }

    public record Sample(
            String category,
            String language,
            boolean success,
            double durationMs,
            String recordedAt
    ) {
    }
}