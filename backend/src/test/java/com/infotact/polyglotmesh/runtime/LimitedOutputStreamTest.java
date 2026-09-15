package com.infotact.polyglotmesh.runtime;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LimitedOutputStreamTest {

    @Test
    void truncatesOutputAboveTheLimit() {
        LimitedOutputStream output = new LimitedOutputStream(5);

        byte[] bytes = "abcdefgh".getBytes(StandardCharsets.UTF_8);
        output.write(bytes, 0, bytes.length);

        assertThat(output.text()).isEqualTo("abcde");
        assertThat(output.truncated()).isTrue();
    }

    @Test
    void doesNotMarkExactLimitAsTruncated() {
        LimitedOutputStream output = new LimitedOutputStream(5);

        byte[] bytes = "abcde".getBytes(StandardCharsets.UTF_8);
        output.write(bytes, 0, bytes.length);

        assertThat(output.text()).isEqualTo("abcde");
        assertThat(output.truncated()).isFalse();
    }
}