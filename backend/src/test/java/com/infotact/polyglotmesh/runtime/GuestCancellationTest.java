package com.infotact.polyglotmesh.runtime;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GuestCancellationTest {

    @Test
    void cancelsJavaScriptLoopAndAllowsNextExecution() {
        try (RuntimeFixture fixture = new RuntimeFixture()) {
            fixture.runtime(30_000).execute(
                    "javascript",
                    "console.log('warmup');"
            );

            GuestRuntime shortDeadlineRuntime = fixture.runtime(500);

            assertThatThrownBy(() -> shortDeadlineRuntime.execute(
                    "javascript",
                    "while (true) {}"
            )).isInstanceOfSatisfying(
                    ScriptExecutionException.class,
                    failure -> assertThat(failure.code())
                            .isEqualTo("EXECUTION_TIMEOUT")
            );

            ExecutionResult next = fixture.runtime(30_000).execute(
                    "javascript",
                    "console.log('recovered');"
            );

            assertThat(next.stdout()).contains("recovered");
        }
    }

    @Test
    void cancelsPythonLoopAndAllowsNextExecution() {
        try (RuntimeFixture fixture = new RuntimeFixture()) {
            fixture.runtime(30_000).execute(
                    "python",
                    "print('warmup')"
            );

            GuestRuntime shortDeadlineRuntime = fixture.runtime(500);

            assertThatThrownBy(() -> shortDeadlineRuntime.execute(
                    "python",
                    """
                    while True:
                        pass
                    """
            )).isInstanceOfSatisfying(
                    ScriptExecutionException.class,
                    failure -> assertThat(failure.code())
                            .isEqualTo("EXECUTION_TIMEOUT")
            );

            ExecutionResult next = fixture.runtime(30_000).execute(
                    "python",
                    "print('recovered')"
            );

            assertThat(next.stdout()).contains("recovered");
        }
    }
}