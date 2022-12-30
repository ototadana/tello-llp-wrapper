package com.xpfriend.tydrone.core;

import java.util.HashMap;
import java.util.Map;

public class Info {
    private final Map<String, String> states = new HashMap<>();
    private final Map<String, Boolean> enabled = new HashMap<>();
    private byte[] image;
    private boolean active = true;
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
        StringBuilder sb = new StringBuilder();
        states.forEach((k, v) -> sb.append(k).append(':').append(v).append(';'));
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public String getState(String key) {
        return states.get(key);
    }

    public void setState(String key, String value) {
        states.put(key, value);
    }

    public void setEnabled(String name, boolean enabled) {
        this.enabled.put(name, enabled);
    }

    public boolean isEnabled(String name) {
        return this.enabled.getOrDefault(name, false);
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
