package com.xpfriend.tydrone.core;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class OutputStreamFactory {
    protected static String getName() {
        return "tydrone-" + new SimpleDateFormat("yyyy-MM-dd-HHmmss").format(new Date());
    }

    public OutputStream createOutputStreamForPicture() throws IOException {
        return createOutputStream(".jpg", "image/jpeg");
    }

    public OutputStream createOutputStreamForVideo() throws IOException {
        return createOutputStream(".mp4", "video/mp4");
    }

    public File createTempFileForVideo() throws IOException {
        return createTempFile(".mp4");
    }

    public abstract OutputStream createOutputStream(String ext, String mimeType) throws IOException;
    public abstract File createTempFile(String ext) throws IOException;

}
