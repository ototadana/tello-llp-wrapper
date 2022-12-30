package com.xpfriend.tydrone.telloio;

import com.xpfriend.tydrone.core.Info;
import com.xpfriend.tydrone.core.Startable;
import com.xpfriend.tydrone.core.VideoFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;

import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2RGB;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;

public class FFmpegVideoReceiver extends Startable {
    private final OpenCVFrameConverter.ToMat matConverter = new OpenCVFrameConverter.ToMat();

    @Override
    public void start(Info info) throws Exception {
        super.start(info);

        //avutil.setLogCallback(new FFmpegAndroidLogCallback());

        try (FFmpegFrameGrabber fg = new FFmpegFrameGrabber("udp://@0.0.0.0:11111?overrun_nonfatal=1")) {
            fg.start();

            int ignoreCount = 100;
            while (info.isActive()) {
                try {
                    Frame frame = fg.grabImage();
                    if (frame == null) {
                        continue;
                    }
                    if (ignoreCount > 0) {
                        ignoreCount--;
                        continue;
                    }

                    info.setFrame(new VideoFrame<>(frame.clone()));

                    if (!info.hasImage()) {
                        info.setImage(toByteArray(frame));
                    }
                } catch (Exception e) {
                    loge(e);
                }
            }

            fg.stop();
            fg.release();
        }
        logi("done");
    }

    private byte[] toByteArray(Frame frame) {
        if (frame == null) {
            return null;
        }

        Mat img = matConverter.convertToMat(frame);
        Mat mat = new Mat(img.rows(), img.cols(), img.type());
        cvtColor(img, mat, COLOR_BGR2RGB);
        byte[] array = new byte[mat.channels() * mat.cols() * mat.rows()];
        mat.data().get(array);
        return array;
    }

}
