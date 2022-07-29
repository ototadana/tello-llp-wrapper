package com.xpfriend.tydrone.telloio.handlers;

import com.xpfriend.tydrone.core.Info;
import com.xpfriend.tydrone.telloio.Message;
import com.xpfriend.tydrone.telloio.MessageHandler;
import com.xpfriend.tydrone.telloio.TimerJob;

import java.io.IOException;

public class VideoStartHandler extends MessageHandler implements TimerJob {

    @Override
    public void sendRequest(String command, Info info) throws IOException {
        execute();
    }

    @Override
    public void handleMessage(Message message, Info info) {
    }

    @Override
    public void execute() throws IOException {
        byte[] data = new byte[]{(byte) 0xcc, // header (always 0xcc)
                (byte) 0x58, 0x00,  // packet size
                0x7c,               // CRC-8
                0x60,               // packet type
                0x25, 0x00,         // message id
                0x00, 0x00,         // seq
                0x00, 0x00          // CRC-16
        };
        send(data);
        //logd("video start");
    }

    @Override
    public long getInterval() {
        return 2000;
    }
}
