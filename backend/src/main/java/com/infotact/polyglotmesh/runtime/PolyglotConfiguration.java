package com.infotact.polyglotmesh.runtime;

import org.graalvm.polyglot.Engine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PolyglotConfiguration {

    @Bean(destroyMethod = "close")
    public Engine polyglotEngine() {
        return Engine.newBuilder().build();
    }
}