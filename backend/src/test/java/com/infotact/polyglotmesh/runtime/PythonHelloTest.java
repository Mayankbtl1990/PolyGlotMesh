package com.infotact.polyglotmesh.runtime;

// import org.graalvm.polyglot.Engine;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PythonHelloTest {

    @Test
    void executesHelloFromPython() {
        try (RuntimeFixture fixture = new RuntimeFixture()) {
            GuestRuntime runtime = fixture.runtime(30_000);

            ExecutionResult result = runtime.execute(
                    "python",
                    "print('Hello from Python')"
            );

            assertThat(result.stdout()).contains("Hello from Python");
            assertThat(result.stderr()).isEmpty();
            assertThat(result.durationMs()).isGreaterThanOrEqualTo(0);
        }
    }
}