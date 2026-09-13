package com.infotact.polyglotmesh.runtime;

import org.graalvm.polyglot.Engine;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PythonHelloTest {

    @Test
    void executesHelloFromPython() {
        try (Engine engine = Engine.create()) {
            GuestRuntime runtime = new GuestRuntime(engine);

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