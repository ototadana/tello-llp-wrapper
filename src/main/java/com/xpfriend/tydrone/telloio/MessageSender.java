package com.xpfriend.tydrone.telloio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageSender {
    private static final AtomicInteger sequence = new AtomicInteger();

    private final DatagramChannel channel;

    public MessageSender(DatagramChannel channel) {
        this.channel = channel;
    }

    protected void send(byte[] data, boolean addSequence) throws IOException {
        if (addSequence) {
            setPacketSequence(data);
        }
        setPacketCRCs(data);
        handleSend(data);
    }

    protected void handleSend(byte[] data) throws IOException {
        channel.send(ByteBuffer.wrap(data), MessageHandlerManager.droneAddress);
    }

    protected void setPacketSequence(byte[] data) {
        int num = sequence.getAndIncrement();
        data[7] = (byte) (num & 0xff);
        data[8] = (byte) ((num >> 8) & 0xff);
    }

    protected void setPacketCRCs(byte[] bytes) {
        int len = bytes.length;
        if (bytes.length <= 2) {
            return;
        }
        int i = CRC.crc16(bytes, len - 2);
        bytes[(len - 2)] = ((byte) (i & 0xFF));
        bytes[(len - 1)] = ((byte) ((i >> 8) & 0xFF));
    }
}
