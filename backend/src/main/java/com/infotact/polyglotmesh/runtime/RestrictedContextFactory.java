package com.infotact.polyglotmesh.runtime;

import java.io.InputStream;
import java.io.OutputStream;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.EnvironmentAccess;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.io.IOAccess;
import org.springframework.stereotype.Component;

@Component
public class RestrictedContextFactory {

    public Context create(
            Engine engine,
            String languageId,
            OutputStream stdout,
            OutputStream stderr
    ) {
        return Context.newBuilder(languageId)
                .engine(engine)
                .allowAllAccess(false)
                .allowHostAccess(HostAccess.NONE)
                .allowHostClassLookup(className -> false)
                .allowIO(IOAccess.NONE)
                .allowNativeAccess(false)
                .allowCreateThread(false)
                .allowCreateProcess(false)
                .allowEnvironmentAccess(EnvironmentAccess.NONE)
                .in(InputStream.nullInputStream())
                .out(stdout)
                .err(stderr)
                .build();
    }
}