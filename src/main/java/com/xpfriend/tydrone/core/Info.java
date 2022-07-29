package com.xpfriend.tydrone.core;

public class Info {
    private byte[] image;
    private boolean active = true;
    private String states;
    private String command;
    private String notice;
    private String sentCommand;
    private boolean recording = false;

    @SuppressWarnings("rawtypes")
    private VideoFrame frame;
    private boolean richStates;

    public boolean isRichStates() {
        return richStates;
    }

    public void setRichStates(boolean richStates) {
        this.richStates = richStates;
    }

    public boolean isActive() {
        return this.active;
    }

    public void stop() {
        this.active = false;
    }

    public String getStates() {
        return states;
    }

    public void setStates(String states) {
        this.states = states;
    }

    public boolean isRecording() {
        return recording;
    }

    public void setRecording(boolean recording) {
        this.recording = recording;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public boolean hasImage() {
        return image != null;
    }

    public byte[] pickImage() {
        if (image == null) {
            return null;
        }
        byte[] b = image;
        image = null;
        return b;
    }

    public String getSentCommand() {
        return sentCommand;
    }

    public void setSentCommand(String sentCommand) {
        this.sentCommand = sentCommand;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String result) {
        this.notice = result;
    }

    public void entryCommand(String command) {
        this.command = command;
    }

    public String pickCommand() {
        String command = this.command;
        this.command = "";
        return command;
    }

    @SuppressWarnings("unchecked")
    public <T> VideoFrame<T> getFrame() {
        return frame;
    }

    public <T> void setFrame(VideoFrame<T> frame) {
        this.frame = frame;
    }
}
