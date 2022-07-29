package com.xpfriend.tydrone.telloio;

import com.xpfriend.tydrone.core.Info;
import com.xpfriend.tydrone.core.Startable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class VideoForwarder extends Startable {
    private final MessageSender videoStartSender;

    public VideoForwarder(DatagramChannel commandChannel) {
        this.videoStartSender = new MessageSender(commandChannel);
    }

    @Override
    public void start(Info info) throws Exception {
        super.start(info);

        try (DatagramChannel channel = DatagramChannel.open(); DatagramChannel forward = DatagramChannel.open()) {
            channel.bind(new InetSocketAddress(6038));
            channel.socket().setSoTimeout(1000);
            channel.setOption(StandardSocketOptions.SO_RCVBUF, 512 * 104);
            ByteBuffer receiveBuffer = ByteBuffer.allocate(2046);

            sendVideoBitrate();

            InetSocketAddress videoPort = new InetSocketAddress("127.0.0.1", 11111);

            boolean firstFrame = true;
            while (info.isActive()) {
                try {
                    receiveBuffer.clear();
                    channel.receive(receiveBuffer);
                    receiveBuffer.flip();

                    boolean nal = isNal(receiveBuffer);
                    if (firstFrame) {
                        if (nal) {
                            firstFrame = false;
                        } else {
                            continue;
                        }
                    }


                    forward.send(receiveBuffer, videoPort);
                } catch (Exception e) {
                    loge(e);
                }
            }
        }
        logi("done");
    }

    private boolean isNal(ByteBuffer receiveBuffer) {
        receiveBuffer.position(2);
        boolean isNal = receiveBuffer.get() == (byte) 0 && receiveBuffer.get() == (byte) 0 && receiveBuffer.get() == (byte) 0 && receiveBuffer.get() == (byte) 1;
        receiveBuffer.position(2);
        return isNal;
    }

    private void sendVideoBitrate() throws IOException {
        byte[] data = new byte[]{(byte) 0xcc, // header (always 0xcc)
                (byte) 0x60, 0x00,   // packet size
                0x27,                // CRC-8
                0x68,                // packet type
                0x20, 0x00,          // message id
                0x00, 0x00,          // seq
                (byte) 0x04,         // bitrate (4)
                0x00, 0x00           // CRC-16
        };
        videoStartSender.send(data, true);
    }
}
