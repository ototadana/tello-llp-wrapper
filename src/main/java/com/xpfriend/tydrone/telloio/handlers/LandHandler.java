package com.xpfriend.tydrone.telloio.handlers;

import com.xpfriend.tydrone.core.Info;
import com.xpfriend.tydrone.telloio.Message;
import com.xpfriend.tydrone.telloio.MessageHandler;

import java.io.IOException;

public class LandHandler extends MessageHandler {

    @Override
    public void sendRequest(String command, Info info) throws IOException {
        send(new byte[]{(byte) 0xcc, // header (always 0xcc)
                0x60, 0x00, // packet size
                0x27,       // CRC-8
                0x68,       // packet type
                0x55, 0x00, // message id
                0x00, 0x00, // seq
                0x00,       // payload
                0x00, 0x00  // CRC-16
        });
        info.setSentCommand("land");
    }

    @Override
    public void handleMessage(Message message, Info info) {
        String result = "OK (land)";

        logd(result);
        info.setNotice(result);
    }
}
