package com.xpfriend.tydrone.core;

import java.io.Closeable;
import java.io.IOException;

public abstract class Facade implements Closeable {
    private Info info;

    protected abstract void handleRun(Info info) throws IOException;

    public synchronized void run() throws IOException {
        if (info != null && info.isActive()) {
            return;
        }
        info = new Info();
        handleRun(info);
    }

    public synchronized void stop() {
        info.stop();
        info = null;
    }

    public void entryCommand(String command) {
        info.entryCommand(command);
    }

    public String getSentCommand() {
        return info.getSentCommand();
    }

    public String getNotice() {
        return info.getNotice();
    }

    public String getStates() {
        return info.getStates();
    }

    public byte[] pickImage() {
        return info.pickImage();
    }

    public boolean isRecording() {
        return info.isRecording();
    }

    public void setRecording(boolean recording) {
        info.setRecording(recording);
    }

    public boolean isRichStates() {
        return info.isRichStates();
    }

    public void setRichStates(boolean fullStates) {
        info.setRichStates(fullStates);
    }

    @Override
    public void close() {
        stop();
    }
}
