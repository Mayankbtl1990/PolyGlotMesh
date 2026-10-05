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
        properties = {
                "polyglotmesh.execution.timeout-ms=30000",
                "spring.datasource.url=jdbc:h2:mem:product-api"
        }
)
class ProductDataApiTest {

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
    void listsMockProducts() {
        client.get()
                .uri("/api/data/products")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(3);
    }

    @Test
    void runsPythonWithQueriedData() {
        client.post()
                .uri("/api/data/execute")
                .bodyValue(Map.of(
                        "productId", "keyboard",
                        "language", "python",
                        "code", """
                                pricing.finalPrice = (
                                    pricing.basePrice * (1 - pricing.discount)
                                )
                                print(pricing.finalPrice)
                                """
                ))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.finalPrice").isEqualTo(85.0)
                .jsonPath("$.dataSource")
                    .isEqualTo("IN_MEMORY_MOCK_CATALOG")
                .jsonPath("$.execution.language").isEqualTo("python");
    }

    @Test
    void returnsNotFoundForMissingProduct() {
        client.post()
                .uri("/api/data/execute")
                .bodyValue(Map.of(
                        "productId", "missing",
                        "language", "python",
                        "code", "print(1)"
                ))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.code").isEqualTo("NOT_FOUND");
    }

    @Test
    void rejectsJavaExecution() {
        client.post()
                .uri("/api/data/execute")
                .bodyValue(Map.of(
                        "productId", "keyboard",
                        "language", "java",
                        "code", "class Example {}"
                ))
                .exchange()
                .expectStatus().isBadRequest();
    }
}