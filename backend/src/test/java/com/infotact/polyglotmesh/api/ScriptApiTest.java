package com.infotact.polyglotmesh.api;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import com.infotact.polyglotmesh.scripts.SavedScript;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:script-api;DB_CLOSE_DELAY=-1",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        }
)
class ScriptApiTest {

    @LocalServerPort
    private int port;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        client = WebTestClient.bindToServer()
                .baseUrl("http://127.0.0.1:" + port)
                .responseTimeout(Duration.ofSeconds(30))
                .build();
    }

    @Test
    void createsLoadsAndUpdatesScript() {
        SavedScript created = client.post()
                .uri("/api/scripts")
                .bodyValue(Map.of(
                        "name", "Pricing test",
                        "language", "python",
                        "code", "print(85)"
                ))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(SavedScript.class)
                .returnResult()
                .getResponseBody();

        assertThat(created).isNotNull();

        client.get()
                .uri("/api/scripts/" + created.id())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo("print(85)");

        client.put()
                .uri("/api/scripts/" + created.id())
                .bodyValue(Map.of(
                        "name", "Updated pricing",
                        "language", "python",
                        "code", "print(90)"
                ))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo("print(90)")
                .jsonPath("$.id").isEqualTo(created.id());

        client.get()
                .uri("/api/scripts/" + created.id())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Updated pricing")
                .jsonPath("$.code").isEqualTo("print(90)");
    }

    @Test
    void storesJavaAsReferenceCode() {
        client.post()
                .uri("/api/scripts")
                .bodyValue(Map.of(
                        "name", "Java reference",
                        "language", "java",
                        "code", "class Example {}"
                ))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.language").isEqualTo("java");
    }

    @Test
    void rejectsBlankName() {
        client.post()
                .uri("/api/scripts")
                .bodyValue(Map.of(
                        "name", " ",
                        "language", "python",
                        "code", "print(1)"
                ))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void returnsNotFoundForUnknownScript() {
        client.get()
                .uri("/api/scripts/" + UUID.randomUUID())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo("NOT_FOUND");
    }

    @Test
    void doesNotCreateMissingScriptDuringUpdate() {
        client.put()
                .uri("/api/scripts/" + UUID.randomUUID())
                .bodyValue(Map.of(
                        "name", "Missing script",
                        "language", "python",
                        "code", "print(1)"
                ))
                .exchange()
                .expectStatus().isNotFound();
    }
}