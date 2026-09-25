package com.infotact.polyglotmesh.metrics;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExecutionMetricsTest {

    @Test
    void computesGuestMeanAndFailureCount() {
        ExecutionMetrics metrics = new ExecutionMetrics();

        metrics.recordGuest("python", true, 10);
        metrics.recordGuest("javascript", false, 30);

        var snapshot = metrics.snapshot();

        assertThat(snapshot.guestCount()).isEqualTo(2);
        assertThat(snapshot.guestFailures()).isEqualTo(1);
        assertThat(snapshot.guestMeanMs()).isEqualTo(20);
    }

    @Test
    void keepsMockMeasurementsSeparate() {
        ExecutionMetrics metrics = new ExecutionMetrics();

        metrics.recordGuest("python", true, 5);
        metrics.recordMock(50);
        metrics.recordMock(60);

        var snapshot = metrics.snapshot();

        assertThat(snapshot.guestMeanMs()).isEqualTo(5);
        assertThat(snapshot.mockCount()).isEqualTo(2);
        assertThat(snapshot.mockMeanMs()).isEqualTo(55);
    }

    @Test
    void boundsRecentSamplesWithoutResettingTotals() {
        ExecutionMetrics metrics = new ExecutionMetrics();

        for (int index = 0; index < 70; index++) {
            metrics.recordGuest("python", true, index);
        }

        var snapshot = metrics.snapshot();

        assertThat(snapshot.guestCount()).isEqualTo(70);
        assertThat(snapshot.recent()).hasSize(50);
        assertThat(snapshot.recent().getFirst().durationMs())
                .isEqualTo(20);
    }

    @Test
    void snapshotsDoNotChangeWhenNewSamplesArrive() {
        ExecutionMetrics metrics = new ExecutionMetrics();
        metrics.recordMock(50);

        var original = metrics.snapshot();

        metrics.recordMock(70);

        assertThat(original.recent()).hasSize(1);
        assertThat(metrics.snapshot().recent()).hasSize(2);
    }

    @Test
    void rejectsInvalidDurations() {
        ExecutionMetrics metrics = new ExecutionMetrics();

        assertThatThrownBy(() -> metrics.recordMock(Double.NaN))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> metrics.recordMock(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }
}