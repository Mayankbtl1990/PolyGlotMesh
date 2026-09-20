package com.infotact.polyglotmesh.runtime;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

class FilesystemRestrictionTest {

    @TempDir
    Path directory;

    @Test
    void pythonCannotReadAnExistingHostFile() throws Exception {
        Path secret = directory.resolve("host-secret.txt");
        Files.writeString(secret, "HOST_SECRET_MUST_NOT_LEAK");

        String escapedPath = secret.toAbsolutePath()
                .toString()
                .replace("\\", "\\\\")
                .replace("'", "\\'");

        String code = """
                try:
                    with open('%s', 'r') as source:
                        print("READ_SUCCEEDED")
                        print(source.read())
                except (PermissionError, OSError):
                    print("READ_DENIED")
                """.formatted(escapedPath);

        try (RuntimeFixture fixture = new RuntimeFixture()) {
            ExecutionResult result = fixture.runtime(30_000)
                    .execute("python", code);

            assertThat(result.stdout()).contains("READ_DENIED");
            assertThat(result.stdout()).doesNotContain("READ_SUCCEEDED");
            assertThat(result.stdout())
                    .doesNotContain("HOST_SECRET_MUST_NOT_LEAK");
        }
    }
}