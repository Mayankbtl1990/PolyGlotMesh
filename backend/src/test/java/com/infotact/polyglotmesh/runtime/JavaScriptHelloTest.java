package com.infotact.polyglotmesh.runtime;

import org.graalvm.polyglot.Engine;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JavaScriptHelloTest {

    @Test
    void executesJavaScriptAndCapturesConsoleOutput() {
        try (Engine engine = Engine.create()) {
            GuestRuntime runtime = new GuestRuntime(engine);

            ExecutionResult result = runtime.execute(
                    "javascript",
                    "console.log('Hello from JavaScript');"
            );

            assertThat(result.language()).isEqualTo("javascript");
            assertThat(result.stdout()).contains("Hello from JavaScript");
            assertThat(result.stderr()).isEmpty();
        }
    }
}