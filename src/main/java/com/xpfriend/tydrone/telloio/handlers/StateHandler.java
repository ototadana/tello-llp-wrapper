package com.xpfriend.tydrone.telloio.handlers;

import com.xpfriend.tydrone.core.Info;
import com.xpfriend.tydrone.telloio.Message;
import com.xpfriend.tydrone.telloio.MessageHandler;

import java.io.IOException;
import java.nio.ByteBuffer;

public class StateHandler extends MessageHandler {
    private final State state = new State();

    @Override
    public void sendRequest(String command, Info info) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handleMessage(Message message, Info info) throws IOException {
        int id = message.getId();
        if (id == 4176) {
            sendAckLog(message.getBuffer().getShort());
            return;
        }

        if (id == 26) {
            state.setWifi(message.getBuffer());
        } else if (id == 86) {
            state.setState(message.getBuffer());
        } else if (id == 4177) {
            try {
                state.setLogData(message.getBuffer());
            } catch (IOException e) {
                loge(e);
            }
        } else if (id == 53) {
            state.setLight(message.getBuffer());
        }

        String states = info.isRichStates() ? state.getRichStates() : state.getDefaultStates();
        info.setStates(states);
    }

    private void sendAckLog(short id) throws IOException {
        byte[] packet = new byte[]{(byte) 0xcc, // header (always 0xcc)
                0x70, 0x00, // packet size
                (byte) 0xcb,// CRC-8
                0x50,       // packet type
                0x50, 0x10, // message id
                0x00, 0x00, // seq
                0x00,       // ?
                0x00, 0x00, // id
                0x00, 0x00  // CRC-16
        };

        ByteBuffer bb = wrap(packet);
        bb.putShort(10, id);

        send(packet);
    }

}
