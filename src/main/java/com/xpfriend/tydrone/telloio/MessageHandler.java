package com.xpfriend.tydrone.telloio;

import com.xpfriend.tydrone.core.Info;
import com.xpfriend.tydrone.core.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;

public abstract class MessageHandler {
    protected Logger logger;
    private String tag;
    private MessageSender sender;

    void setChannel(DatagramChannel channel) {
        sender = new MessageSender(channel);
    }

    void setLogger(Logger logger) {
        this.logger = logger;
        this.tag = getClass().getSimpleName();
    }

    protected void send(byte[] data) throws IOException {
        send(data, true);
    }

    protected void send(byte[] data, boolean addSequence) throws IOException {
        sender.send(data, addSequence);
    }

    protected ByteBuffer wrap(byte[] data) {
        ByteBuffer bb = ByteBuffer.wrap(data);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb;
    }

    public abstract void sendRequest(String command, Info info) throws IOException;

    public abstract void handleMessage(Message message, Info info) throws IOException;

    protected void logd(String msg) {
        logger.logd(tag, tag + ":" + msg);
    }

    protected void logi(String msg) {
        logger.logi(tag, tag + ":" + msg);
    }

    protected void logw(String msg) {
        logger.logw(tag, tag + ":" + msg);
    }

    protected void logw(String msg, Throwable tr) {
        logger.logw(tag, tag + ":" + msg, tr);
    }

    protected void loge(String msg) {
        logger.loge(tag, tag + ":" + msg);
    }

    protected void loge(String msg, Throwable tr) {
        logger.loge(tag, tag + ":" + msg, tr);
    }

    protected void loge(Throwable tr) {
        logger.loge(tag, tag + ":" + tr.toString(), tr);
    }
}
