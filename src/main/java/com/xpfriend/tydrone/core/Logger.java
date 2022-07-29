package com.xpfriend.tydrone.core;

public abstract class Logger {
    public abstract void logd(String tag, String msg);

    public abstract void logi(String tag, String msg);

    public abstract void logw(String tag, String msg);

    public abstract void logw(String tag, String msg, Throwable tr);

    public abstract void loge(String tag, String msg);

    public abstract void loge(String tag, String msg, Throwable tr);
}
