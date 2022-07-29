package com.xpfriend.tydrone.recorder;

import com.xpfriend.tydrone.core.Info;
import com.xpfriend.tydrone.core.OutputStreamFactory;
import com.xpfriend.tydrone.core.Startable;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class FFmpegVideoRecorder extends Startable {

    private final OutputStreamFactory outputStreamFactory;
    private File outputFile;
    private FFmpegFrameRecorder recorder;
    private long startTime;
    private int frameNumber;

    public FFmpegVideoRecorder(OutputStreamFactory outputStreamFactory) {
        this.outputStreamFactory = outputStreamFactory;
    }

    @Override
    public void start(Info info) throws Exception {
        super.start(info);

        while (info.isActive()) {
            try {
                if (info.isRecording()) {
                    record(info.<Frame>getFrame().get());
                } else {
                    stopRecorder();
                    sleep(100);
                }
            } catch (Exception e) {
                loge(e);
            }
        }

        stopRecorder();
    }

    public synchronized void record(Frame frame) throws IOException {
        if (frame == null || frame.timestamp != 0) {
            return;
        }

        if (recorder == null) {
            outputFile = outputStreamFactory.createTempFileForVideo();
            recorder = startRecorder(outputFile);
        }

        long currentTime = System.nanoTime() / 1000;
        if (startTime == 0) {
            startTime = currentTime;
        }
        frame.timestamp = currentTime - startTime;

        recorder.setFrameNumber(++frameNumber);
        recorder.record(frame);
    }

    private synchronized void stopRecorder() {
        try {
            if (recorder != null) {
                recorder.close();
                try (OutputStream os = outputStreamFactory.createOutputStreamForVideo()) {
                    Files.copy(outputFile.toPath(), os);
                } finally {
                    //noinspection ResultOfMethodCallIgnored
                    outputFile.delete();
                }
                outputFile = null;
                recorder = null;
            }
        } catch (IOException e) {
            loge(e);
        }
    }

    private FFmpegFrameRecorder startRecorder(File outputFile) throws FFmpegFrameRecorder.Exception {
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, 960, 720, 0);
        recorder.setVideoQuality(1);
        recorder.setTimestamp(0);
        recorder.setVideoBitrate(2048 * 1024);
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        recorder.setFrameRate(30);
        recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
        recorder.start();
        return recorder;
    }
}
