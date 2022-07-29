package com.xpfriend.tydrone.core;

public class VideoFrame<T> {
    private final T frame;

    public VideoFrame(T frame) {
        this.frame = frame;
    }

    public T get() {
        return frame;
    }
}
