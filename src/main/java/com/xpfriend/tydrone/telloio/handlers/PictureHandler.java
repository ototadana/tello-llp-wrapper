package com.xpfriend.tydrone.telloio.handlers;

import com.xpfriend.tydrone.core.Info;
import com.xpfriend.tydrone.core.OutputStreamFactory;
import com.xpfriend.tydrone.telloio.Message;
import com.xpfriend.tydrone.telloio.MessageHandler;
import com.xpfriend.tydrone.telloio.TimerJob;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PictureHandler extends MessageHandler implements TimerJob {
    private final OutputStreamFactory outputStreamFactory;
    private final Map<Short, Picture> pictureMap = new HashMap<>();
    private Info info;
    private long requestedTime;

    public PictureHandler(OutputStreamFactory outputStreamFactory) {
        this.outputStreamFactory = outputStreamFactory;
    }

    @Override
    public void sendRequest(String command, Info info) throws IOException {
        this.info = info;
        if (isBusy()) {
            return;
        }
        requestedTime = System.currentTimeMillis();
        send(new byte[]{(byte) 0xcc, // header (always 0xcc)
                0x58, 0x00,     // packet size
                0x7c,           // CRC-8
                0x68,           // packet type
                0x30, 0x00,     // message id
                0x00, 0x00,     // seq
                0x00, 0x00      // CRC-16
        });
        info.setSentCommand(command);
    }

    private boolean isBusy() {
        return requestedTime + 1000 > System.currentTimeMillis();
    }

    @Override
    public void handleMessage(Message message, Info info) throws IOException {
        int id = message.getId();
        if (id == 48) {
            logd("started");
        } else if (id == 98) {
            ByteBuffer buffer = message.getBuffer();
            buffer.get(); // file type : always 1 (jpeg)
            int fileSize = buffer.getInt();
            short fileId = buffer.getShort();
            pictureMap.put(fileId, new Picture(fileSize));
            sendAckFileSize();
            logd("open");
        } else if (id == 99) {
            ByteBuffer buffer = message.getBuffer();
            short fileId = buffer.getShort();
            int pieceNum = buffer.getInt();
            int seqNum = buffer.getInt();
            int size = buffer.getShort();

            Picture picture = pictureMap.get(fileId);
            if (picture == null || picture.isClosed()) {
                logd("the message ignored: fileId:" + fileId + ", pieceNum:" + pieceNum);
                return;
            }

            picture.maxPieceNum = Math.max(pieceNum, picture.maxPieceNum);
            if (!picture.chunkState[seqNum]) {
                System.arraycopy(buffer.array(), buffer.position(), picture.buffer, seqNum * 1024, size);
                picture.receivedSize += size;
                picture.chunkState[seqNum] = true;

                for (int i = 0; i < picture.chunkState.length / 8; i++) {
                    if (isPieceCompleted(picture, i) && !picture.pieceState[i]) {
                        picture.pieceState[i] = true;
                        sendAckFilePiece((byte) 0, fileId, i);
                    }
                }

                if (picture.receivedSize >= picture.fileSize) {
                    sendAckFilePiece((byte) 1, fileId, picture.maxPieceNum);
                    sendAckFileDone(fileId, picture.fileSize);
                    picture.close(info);
                }
            }
        } else if (id == 100) {
            logd("completed");
        }
    }

    private boolean isPieceCompleted(Picture picture, int pieceNum) {
        for (int i = 0; i < 8; i++) {
            if (!picture.chunkState[(pieceNum * 8) + i]) {
                return false;
            }
        }
        return true;
    }

    private void sendAckFilePiece(byte endFlag, short fileId, int pieceId) throws IOException {
        byte[] data = new byte[]{(byte) 0xcc,            // Header (Always 0xCC)
                (byte) 0x90, 0x00,      // packet size
                (byte) 0xbe,            // CRC-8
                0x50,                   // packet type
                0x63, 0x00,             // message id
                0x00, 0x00,             // seq
                0x00,                   // endFlag
                0x00, 0x00,             // fileId
                0x00, 0x00, 0x00, 0x00, // pieceId
                0x00, 0x00              // CRC-16
        };

        ByteBuffer bb = wrap(data);
        bb.put(9, endFlag);
        bb.putShort(10, fileId);
        bb.putInt(12, pieceId);
        send(data);
    }

    private void sendAckFileSize() throws IOException {
        byte[] data = new byte[]{(byte) 0xcc, // Header (Always 0xCC)
                0x60, 0x00, // packet size
                0x27,       // CRC-8
                0x50,       // packet type
                0x62, 0x00, // message id
                0x00, 0x00, // seq
                0x00,       // single zero byte
                0x00, 0x00  // CRC-16
        };
        send(data);
    }

    private void sendAckFileDone(short fileId, int size) throws IOException {
        byte[] data = new byte[]{(byte) 0xcc,             // Header (Always 0xCC)
                (byte) 0x88, 0x00,       // packet size
                0x24,                   // CRC-8
                0x48,                   // packet type
                0x64, 0x00,             // message id
                0x00, 0x00,             // seq
                0x00, 0x00,             // file id
                0x00, 0x00, 0x00, 0x00, // size
                0x00, 0x00              // CRC-16
        };

        ByteBuffer bb = wrap(data);
        bb.putShort(9, fileId);
        bb.putInt(11, size);
        send(data);
    }

    @Override
    public void execute() {
        List<Short> removable = pictureMap.entrySet().stream().filter(e -> e.getValue().isExpired() || e.getValue().isClosed()).map(Map.Entry::getKey).collect(Collectors.toList());

        removable.forEach(key -> {
            Picture picture = pictureMap.remove(key);
            if (!picture.isClosed()) {
                picture.close(info);
                logw("a picture file is closed by timeout. started:" + picture.startTime);
            }
        });
    }

    @Override
    public long getInterval() {
        return 5000;
    }

    private class Picture {
        private final long startTime;
        private int fileSize;
        private int receivedSize;
        private boolean[] chunkState;
        private boolean[] pieceState;
        private int maxPieceNum;
        private byte[] buffer;

        public Picture(int size) {
            startTime = System.currentTimeMillis();
            fileSize = size;
            receivedSize = 0;
            chunkState = new boolean[(fileSize / 1024) + 1];
            pieceState = new boolean[(chunkState.length / 8) + 1];
            maxPieceNum = 0;
            buffer = new byte[size];
        }

        private boolean isClosed() {
            return buffer == null;
        }

        private boolean isExpired() {
            return startTime + 5000 < System.currentTimeMillis();
        }

        private void close(Info info) {
            try {
                OutputStream outputStream = outputStreamFactory.createOutputStreamForPicture();
                outputStream.write(buffer);
                outputStream.close();
                if (info != null) {
                    info.setNotice("OK - saved the picture");
                }
                logd("picture file closed");
            } catch (IOException e) {
                if (info != null) {
                    info.setNotice("Error - cannot saved the picture");
                }
                loge(e);
            } finally {
                buffer = null;
                fileSize = 0;
                receivedSize = 0;
                chunkState = null;
                pieceState = null;
                maxPieceNum = 0;
            }
        }
    }
}
