package com.xpfriend.tydrone.core;

public abstract class Startable {
    private final String tag;
    private Info info;
    private Logger logger;

    protected Startable() {
        this.tag = getClass().getSimpleName();
    }

    public void start(Info info) throws Exception {
        this.info = info;
        logi("start");
    }

    protected Info getInfo() {
        return info;
    }

    protected String getTag() {
        return tag;
    }

    protected void sleep(long mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            loge(e);
        }
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

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
