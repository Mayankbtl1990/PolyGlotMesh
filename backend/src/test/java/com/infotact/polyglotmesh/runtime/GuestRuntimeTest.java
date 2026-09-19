package com.infotact.polyglotmesh.runtime;

import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.PolyglotException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GuestRuntimeTest {

    private RuntimeFixture fixture;
    private GuestRuntime runtime;

    @BeforeAll
        void setUp() {
            fixture = new RuntimeFixture();
            runtime = fixture.runtime(30_000);
        }

     @AfterAll
        void tearDown() {
            fixture.close();
        }

    @Test
    void calculatesPythonPricingExample() {
        ExecutionResult result = runtime.execute(
                "python",
                """
                base_price = 100
                discount = 0.15
                print(base_price * (1 - discount))
                """
        );

        assertThat(result.stdout().trim()).isEqualTo("85.0");
    }

    @Test
    void usesFreshPythonGlobalsForEachExecution() {
        runtime.execute("python", "secret_value = 123");

        ExecutionResult result = runtime.execute(
                "python",
                "print('secret_value' in globals())"
        );

        assertThat(result.stdout().trim()).isEqualTo("False");
    }

    @Test
    void usesFreshJavaScriptGlobalsForEachExecution() {
        runtime.execute("javascript", "globalThis.secretValue = 123;");

        ExecutionResult result = runtime.execute(
                "javascript",
                "console.log(typeof globalThis.secretValue);"
        );

        assertThat(result.stdout().trim()).isEqualTo("undefined");
    }

    @Test
    void reportsGuestScriptFailures() {
        assertThatThrownBy(
                () -> runtime.execute("python", "print(1 / 0)")
        ).isInstanceOf(ScriptExecutionException.class);
    }

    @Test
    void truncatesLargeFiniteOutput() {
        ExecutionResult result = runtime.execute(
                "python",
                "print('x' * 70000)"
        );

        assertThat(result.stdout()).hasSize(64 * 1024);
        assertThat(result.outputTruncated()).isTrue();
    }

    @Test
    void rejectsUnsupportedLanguage() {
        assertThatThrownBy(
                () -> runtime.execute("ruby", "puts 1")
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported language");
    }
}