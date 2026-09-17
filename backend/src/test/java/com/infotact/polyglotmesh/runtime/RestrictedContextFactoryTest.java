package com.infotact.polyglotmesh.runtime;

import java.io.ByteArrayOutputStream;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.PolyglotException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RestrictedContextFactoryTest {

    @Test
    void blocksJavaClassLookupFromJavaScript() {
        RestrictedContextFactory factory = new RestrictedContextFactory();

        try (Engine engine = Engine.create();
             Context context = factory.create(
                     engine,
                     "js",
                     new ByteArrayOutputStream(),
                     new ByteArrayOutputStream()
             )) {

            assertThatThrownBy(() -> context.eval(
                    "js",
                    "Java.type('java.lang.System')"
            )).isInstanceOf(PolyglotException.class);
        }
    }
}