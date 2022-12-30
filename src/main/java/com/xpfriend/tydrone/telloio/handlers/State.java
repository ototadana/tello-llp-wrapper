package com.xpfriend.tydrone.telloio.handlers;

import com.xpfriend.tydrone.core.Info;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class State {
    private float ax = -1;
    private float ay = -1;
    private float az = -1;
    private short vx = -1;
    private short vy = -1;
    private short vz = -1;
    private byte lit = -1;  // light strength: 0 - light strength OK, 1 - poor light strength.
    private byte wifi = -1; // Wifi Strength (percentage)
    private byte bat = -1;  // Current battery percentage, %
    private short h = -1;    // Height, cm
    private int pitch = -1;
    private int roll = -1;
    private int yaw = -1;
    private short time = -1;
    private float px = -1;
    private float py = -1;
    private float pz = -1;
    private float gx = -1;
    private float gy = -1;
    private float gz = -1;
    private float vn = -1;
    private float ve = -1;
    private float vd = -1;

    public void setState(ByteBuffer buffer) {
        h = buffer.getShort(); // 0

        short northSpeed = buffer.getShort(); // 2
        short eastSpeed = buffer.getShort(); // 4
        short groundSpeed = buffer.getShort(); // 6
        time = buffer.getShort(); // 8

        byte tmp = buffer.get(); // 10
        /*
        boolean imuState = (tmp >> 0 & 0x1) == 1;
        boolean pressureState = (tmp >> 1 & 0x1) == 1;
        boolean downVisualState = (tmp >> 2 & 0x1) == 1;
        boolean powerState = (tmp >> 3 & 0x1) == 1;
        boolean batteryState = (tmp >> 4 & 0x1) == 1;
        boolean gravityState = (tmp >> 5 & 0x1) == 1;
        boolean windState = (tmp >> 7 & 0x1) == 1;
        */

        byte imuCalibrationState = buffer.get(); // 11
        bat = buffer.get(); // 12

        short droneBatteryLeft = buffer.getShort(); // 13
        short droneFlyTimeLeft = buffer.getShort(); // 15

        tmp = buffer.get(); // 17
        /*
        boolean emSky = (tmp >> 0 & 0x1) == 1;
        boolean emGround = (tmp >> 1 & 0x1) == 1;
        boolean emOpen = (tmp >> 2 & 0x1) == 1;
        boolean droneHover = (tmp >> 3 & 0x1) == 1;
        boolean outageRecording = (tmp >> 4 & 0x1) == 1;
        boolean batteryLow = (tmp >> 5 & 0x1) == 1;
        boolean batteryLower = (tmp >> 6 & 0x1) == 1;
        boolean factoryMode = (tmp >> 7 & 0x1) == 1;
        */

        byte flyMode = buffer.get(); // 18
        byte throwFlyTimer = buffer.get(); // 19
        byte cameraState = buffer.get(); // 20
        byte electricalMachineryState = buffer.get(); // 21

        tmp = buffer.get(); // 22
        boolean frontIn = (tmp >> 0 & 0x1) == 1;
        boolean frontOut = (tmp >> 1 & 0x1) == 1;
        boolean frontLSC = (tmp >> 2 & 0x1) == 1;

        byte temperature = buffer.get(); //23
    }

    public void setLogData(ByteBuffer buffer) throws IOException {
        buffer.get();
        buffer.limit(buffer.limit() - 2);
        while (buffer.hasRemaining()) {
            byte b = buffer.get();
            if (b != 0x55) {
                throw new IOException("setLogData: invalid magic byte");
            }
            int len = buffer.get() & 0xff;
            buffer.get();
            byte crc = buffer.get();
            short id = buffer.getShort();
            byte[] xorBuf = new byte[256];
            byte xorValue = buffer.get(); // 6
            buffer.getShort(); // 7,8
            buffer.get(); // 9
            if (id == 0x1d) { //29 new_mvo
                for (int i = 0; i < len - 10; i++) {
                    xorBuf[i] = (byte) (buffer.get() ^ xorValue);
                }
                ByteBuffer buff = ByteBuffer.wrap(xorBuf);
                buff.order(ByteOrder.LITTLE_ENDIAN);
                buff.getShort();
                vx = buff.getShort();
                vy = buff.getShort();
                vz = buff.getShort();

                px = buff.getFloat();
                py = buff.getFloat();
                pz = buff.getFloat();
            } else if (id == 0x0800) { // 2048 imu
                for (int i = 0; i < len - 10; i++) {
                    xorBuf[i] = (byte) (buffer.get() ^ xorValue);
                }
                ByteBuffer buff = ByteBuffer.wrap(xorBuf);
                buff.order(ByteOrder.LITTLE_ENDIAN);

                buff.position(20);
                ax = buff.getFloat();
                ay = buff.getFloat();
                az = buff.getFloat();

                buff.position(32);
                gx = buff.getFloat();
                gy = buff.getFloat();
                gz = buff.getFloat();

                buff.position(48);
                float qw = buff.getFloat();
                float qx = buff.getFloat();
                float qy = buff.getFloat();
                float qz = buff.getFloat();

                int[] euler = toEulerDegrees(qx, qy, qz, qw);
                pitch = euler[0];
                roll = euler[1];
                yaw = euler[2];

                buff.position(76);
                vn = buff.getFloat();
                ve = buff.getFloat();
                vd = buff.getFloat();
            } else {
                buffer.position(buffer.position() + (len - 10));
            }
        }
    }

    private int[] toEulerDegrees(float qX, float qY, float qZ, float qW) {
        double sqW = qW * qW;
        double sqX = qX * qX;
        double sqY = qY * qY;
        double sqZ = qZ * qZ;
        double yaw = 0;
        double roll = 0;
        double pitch = 0;
        int[] eulerDegrees = new int[3];
        double unit = sqX + sqY + sqZ + sqW;
        double test = qW * qX + qY * qZ;
        if (test > 0.499 * unit) {
            yaw = 2 * Math.atan2(qY, qW);
            pitch = Math.PI / 2;
            roll = 0;
        } else if (test < -0.499 * unit) {
            yaw = -2 * Math.atan2(qY, qW);
            pitch = -Math.PI / 2;
            roll = 0;
        } else {
            yaw = Math.atan2(2.0 * (qW * qZ - qX * qY), 1.0 - 2.0 * (sqZ + sqX));
            roll = Math.asin(2.0 * test / unit);
            pitch = Math.atan2(2.0 * (qW * qY - qX * qZ), 1.0 - 2.0 * (sqY + sqX));
        }
        eulerDegrees[0] = (int) Math.toDegrees(pitch);
        eulerDegrees[1] = (int) Math.toDegrees(roll);
        eulerDegrees[2] = (int) Math.toDegrees(yaw);
        return eulerDegrees;
    }

    public void setLight(ByteBuffer buffer) {
        lit = buffer.get();
    }

    public void setWifi(ByteBuffer buffer) {
        wifi = buffer.get();
        byte wil = buffer.get();  // Wifi Interference level (percentage)
    }
    
    public void updateStates(Info info) {
        info.setState("bat", Byte.toString(bat));
        info.setState("lit", Byte.toString(lit));
        info.setState("wifi", Byte.toString(wifi));
        info.setState("yaw", Integer.toString(yaw));
        if (!info.isRichStates()) {
            return;
        }

        info.setState("pitch", Integer.toString(pitch));
        info.setState("roll", Integer.toString(roll));
        info.setState("h", Short.toString(h));
        info.setState("time", Short.toString(time));

        info.setState("ax", Float.toString(ax));
        info.setState("ay", Float.toString(ay));
        info.setState("az", Float.toString(az));
        info.setState("gx", Float.toString(gx));
        info.setState("gy", Float.toString(gy));
        info.setState("gz", Float.toString(gz));
        info.setState("px", Float.toString(px));
        info.setState("py", Float.toString(py));
        info.setState("pz", Float.toString(pz));
        info.setState("vx", Short.toString(vx));
        info.setState("vy", Short.toString(vy));
        info.setState("vz", Short.toString(vz));
        info.setState("vn", Float.toString(vn));
        info.setState("ve", Float.toString(ve));
        info.setState("vd", Float.toString(vd));
    }
}
