package com.infotact.polyglotmesh.service;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;

@Service
public class PolyglotEngineService {

    public Mono<String> executeScript(String language, String script) {
        return Mono.fromCallable(() -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            
            try (Context context = Context.newBuilder(language)
                    .out(outputStream)
                    .err(outputStream)
                    .allowAllAccess(false)
                    .build()) {
                
                Value result = context.eval(language, script);
                String logs = outputStream.toString();
                
                return !logs.isEmpty() ? logs : result.toString();
            }
        });
    }
}
