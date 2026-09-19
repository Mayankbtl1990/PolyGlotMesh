package com.infotact.polyglotmesh.api;

import java.time.Duration;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "polyglotmesh.execution.timeout-ms=30000"
)
class ExecutionApiTest {

    @LocalServerPort
    private int port;

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        client = WebTestClient.bindToServer()
                .baseUrl("http://127.0.0.1:" + port)
                .responseTimeout(Duration.ofSeconds(120))
                .build();
    }

    @Test
    void returnsHealthStatus() {
        client.get()
                .uri("/api/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP");
    }

    @Test
    void executesPython() {
        client.post()
                .uri("/api/executions")
                .bodyValue(Map.of(
                        "language", "python",
                        "code", "print('API Python works')"
                ))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.language").isEqualTo("python")
                .jsonPath("$.stdout").isEqualTo("API Python works\n")
                .jsonPath("$.outputTruncated").isEqualTo(false)
                .jsonPath("$.durationMs").isNumber();
    }

    @Test
    void executesJavaScript() {
        client.post()
                .uri("/api/executions")
                .bodyValue(Map.of(
                        "language", "javascript",
                        "code", "console.log(6 * 7);"
                ))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.stdout").isEqualTo("42\n");
    }

    @Test
    void rejectsBlankCode() {
        client.post()
                .uri("/api/executions")
                .bodyValue(Map.of(
                        "language", "python",
                        "code", " "
                ))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("INVALID_REQUEST");
    }

    @Test
    void rejectsUnsupportedLanguage() {
        client.post()
                .uri("/api/executions")
                .bodyValue(Map.of(
                        "language", "ruby",
                        "code", "puts 1"
                ))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void rejectsOversizedCode() {
        client.post()
                .uri("/api/executions")
                .bodyValue(Map.of(
                        "language", "python",
                        "code", "x".repeat(20_001)
                ))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void returnsStructuredGuestError() {
        client.post()
                .uri("/api/executions")
                .bodyValue(Map.of(
                        "language", "python",
                        "code", "print(1 / 0)"
                ))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("$.code").isEqualTo("SCRIPT_ERROR");
    }

    @Test
    void preservesOutputBeforeGuestFailure() {
        client.post()
                .uri("/api/executions")
                .bodyValue(Map.of(
                        "language", "javascript",
                        "code", """
                                console.log("before failure");
                                throw new Error("expected failure");
                                """
                ))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("$.code").isEqualTo("SCRIPT_ERROR")
                .jsonPath("$.execution.stdout")
                    .isEqualTo("before failure\n")
                .jsonPath("$.guestStack").isArray();
    }
}