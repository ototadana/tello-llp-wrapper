package com.xpfriend.tydrone.factory;

import com.xpfriend.tydrone.core.OutputStreamFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

class SimpleOutputStreamFactory extends OutputStreamFactory {

    @Override
    public OutputStream createOutputStream(String ext, String mimeType) throws IOException {
        String name = OutputStreamFactory.getName();
        String home = System.getProperty("user.home");
        String path = String.format("%s/Downloads/%s%s", home, name, ext);
        return Files.newOutputStream(Paths.get(path));
    }

    @Override
    public File createTempFile(String ext) throws IOException {
        File file = File.createTempFile("tydrone-", ext);
        file.deleteOnExit();
        return file;
    }
}
