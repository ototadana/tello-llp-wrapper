package com.xpfriend.tydrone.telloio;

import java.nio.ByteBuffer;

public class Message {
    private final ByteBuffer buffer;
    private int id = -1;

    public Message(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public int getId() {
        if (id != -1) {
            return id;
        }

        buffer.position(5);
        id = buffer.getShort();
        buffer.position(9);
        return id;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }
}
