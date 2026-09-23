package com.infotact.polyglotmesh.runtime;

import org.graalvm.polyglot.Engine;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RuntimeDiagnosticsTest {

    @Test
    void discoversPythonAndJavaScriptOnTheJvm() {
        try (Engine engine = Engine.create()) {
            var snapshot = new RuntimeDiagnostics(engine).snapshot();

            assertThat(snapshot.executionMode()).isEqualTo("JVM");
            assertThat(snapshot.engineVersion()).isNotBlank();

            assertThat(snapshot.installedLanguages())
                    .extracting(RuntimeDiagnostics.LanguageInfo::id)
                    .contains("python", "js");
        }
    }
}