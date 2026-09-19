package com.infotact.polyglotmesh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.infotact.polyglotmesh.runtime.ExecutionProperties;

@SpringBootApplication
@EnableConfigurationProperties(ExecutionProperties.class)
public class PolyglotmeshApplication {

    public static void main(String[] args) {
        SpringApplication.run(PolyglotmeshApplication.class, args);
    }

}