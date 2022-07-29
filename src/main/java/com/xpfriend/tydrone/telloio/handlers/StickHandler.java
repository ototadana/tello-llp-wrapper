package com.xpfriend.tydrone.telloio.handlers;

import com.xpfriend.tydrone.core.Info;
import com.xpfriend.tydrone.telloio.Message;
import com.xpfriend.tydrone.telloio.MessageHandler;
import com.xpfriend.tydrone.telloio.TimerJob;

import java.io.IOException;
import java.util.Calendar;

public class StickHandler extends MessageHandler implements TimerJob {
    private float rx = 0f;
    private float ry = 0f;
    private float lx = 0f;
    private float ly = 0f;
    private short speed = 0;

    @Override
    public void sendRequest(String command, Info info) {
        String[] values = command.split(" ");
        if (values.length != 6) {
            loge("invalid command: " + command);
        } else {
            rx = toFloat(values[1]);
            ry = toFloat(values[2]);
            lx = toFloat(values[3]);
            ly = toFloat(values[4]);
            speed = values[5].equals("1") ? (short) 1 : 0;
        }
        info.setSentCommand(command);
    }

    private float toFloat(String value) {
        try {
            float f = Float.parseFloat(value);
            if (f > 1.0f) {
                return 1.0f;
            }

            return Math.max(f, -1.0f);
        } catch (NumberFormatException e) {
            loge(e);
            return 0f;
        }
    }

    @Override
    public void execute() throws IOException {
        byte[] data = new byte[]{(byte) 0xcc, // header (always 0xcc)
                (byte) 0xb0, 0x00,   // packet size
                0x7f,                // CRC-8
                0x60,                // packet type
                0x50, 0x00,          // message id
                0x00, 0x00,          // seq
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // axis
                0x00, 0x00, 0x00, 0x0e, 0x00,       // time
                0x00, 0x00           // CRC-16
        };

        short axis1 = (short) (660f * rx + 1024);     // RightX left < 0 < right
        short axis2 = (short) (660f * ry + 1024);     // RightY backward < 0 < forward
        short axis3 = (short) (660f * ly + 1024);     // LeftY down < 0 < up
        short axis4 = (short) (660f * lx + 1024);     // LeftX ccw < 0 < cw
        short axis5 = speed;                          // Speed 0: normal, 1: fast

        long packedAxis = ((long) axis1 & 0x7ff) | (((long) axis2 & 0x7ff) << 11) | (((long) axis3 & 0x7ff) << 22) | (((long) axis4 & 0x7ff) << 33) | ((long) axis5 << 44);
        data[9] = (byte) (packedAxis & 0xff);
        data[10] = (byte) ((packedAxis >> 8) & 0xff);
        data[11] = (byte) ((packedAxis >> 16) & 0xff);
        data[12] = (byte) ((packedAxis >> 24) & 0xff);
        data[13] = (byte) ((packedAxis >> 32) & 0xff);
        data[14] = (byte) ((packedAxis >> 40) & 0xff);

        Calendar cal = Calendar.getInstance();
        data[15] = (byte) cal.get(Calendar.HOUR_OF_DAY);
        data[16] = (byte) cal.get(Calendar.MINUTE);
        data[17] = (byte) cal.get(Calendar.SECOND);
        data[18] = (byte) (cal.get(Calendar.MILLISECOND) & 0xff);
        data[19] = (byte) (cal.get(Calendar.MILLISECOND) >> 8);

        send(data, false);
        //logd("stick " + rx + " " + ry + " " + lx + " " + ly + " " + speed);
    }

    @Override
    public long getInterval() {
        return 16;
    }

    @Override
    public void handleMessage(Message message, Info info) {
    }
}
