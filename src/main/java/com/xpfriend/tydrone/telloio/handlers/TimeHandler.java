package com.xpfriend.tydrone.telloio.handlers;

import com.xpfriend.tydrone.core.Info;
import com.xpfriend.tydrone.telloio.Message;
import com.xpfriend.tydrone.telloio.MessageHandler;

import java.io.IOException;
import java.util.Calendar;

public class TimeHandler extends MessageHandler {
    @Override
    public void sendRequest(String command, Info info) throws IOException {
        byte[] data = new byte[]{(byte) 0xcc,  // header (always 0xcc)
                (byte) 0x88, 0x00,  // packet size
                0x24,               // CRC-8
                0x50,               // packet type
                0x46, 0x00,         // message id
                0x00, 0x00,         // seq
                0x00,               // zero
                0x00, 0x00, 0x00, 0x0e, 0x00,   // time
                0x00, 0x00          // CRC-16
        };

        Calendar cal = Calendar.getInstance();
        data[10] = (byte) cal.get(Calendar.HOUR_OF_DAY);
        data[11] = (byte) cal.get(Calendar.MINUTE);
        data[12] = (byte) cal.get(Calendar.SECOND);
        data[13] = (byte) (cal.get(Calendar.MILLISECOND) & 0xff);
        data[14] = (byte) (cal.get(Calendar.MILLISECOND) >> 8);

        send(data);
    }

    @Override
    public void handleMessage(Message message, Info info) {
    }
}
