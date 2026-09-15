package com.infotact.polyglotmesh.runtime;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class LimitedOutputStream extends OutputStream {

    private final int limit;
    private final ByteArrayOutputStream buffer;
    private boolean truncated;

    public LimitedOutputStream(int limit) {
        if (limit < 1) {
            throw new IllegalArgumentException("Output limit must be positive");
        }

        this.limit = limit;
        this.buffer = new ByteArrayOutputStream(Math.min(limit, 1024));
    }

    @Override
    public void write(int value) {
        if (buffer.size() < limit) {
            buffer.write(value);
        } else {
            truncated = true;
        }
    }

    @Override
    public void write(byte[] bytes, int offset, int length) {
        Objects.checkFromIndexSize(offset, length, bytes.length);

        int remaining = limit - buffer.size();
        int accepted = Math.min(remaining, length);

        buffer.write(bytes, offset, accepted);

        if (accepted < length) {
            truncated = true;
        }
    }

    public String text() {
        return buffer.toString(StandardCharsets.UTF_8);
    }

    public boolean truncated() {
        return truncated;
    }
}