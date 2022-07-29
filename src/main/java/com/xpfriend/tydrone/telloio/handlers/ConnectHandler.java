package com.xpfriend.tydrone.telloio.handlers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;

public class ConnectHandler {
    public void connect(DatagramChannel channel, InetSocketAddress droneAddress) throws IOException {
        byte[] data = "conn_req:  ".getBytes(StandardCharsets.US_ASCII);
        data[data.length - 2] = (byte) 0x96;
        data[data.length - 1] = (byte) 0x17;
        channel.send(ByteBuffer.wrap(data), droneAddress);
    }

    public boolean isConnected(ByteBuffer buffer) {
        return new String(buffer.array(), 0, buffer.limit(), StandardCharsets.UTF_8).startsWith("conn_ack:");
    }
}
