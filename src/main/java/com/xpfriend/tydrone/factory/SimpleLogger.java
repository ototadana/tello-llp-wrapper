package com.xpfriend.tydrone.factory;

import com.xpfriend.tydrone.core.Logger;

public class SimpleLogger extends Logger {

    private final long startTime = System.currentTimeMillis();

    @Override
    public void logd(String tag, String msg) {
        log("DEBUG", tag, msg);
    }

    @Override
    public void logi(String tag, String msg) {
        log(" INFO", tag, msg);
    }

    @Override
    public void logw(String tag, String msg) {
        log(" WARN", tag, msg);
    }

    @Override
    public void logw(String tag, String msg, Throwable tr) {
        log(" WARN", tag, msg);
        tr.printStackTrace();
    }

    @Override
    public void loge(String tag, String msg) {
        log("ERROR", tag, msg);
    }

    @Override
    public void loge(String tag, String msg, Throwable tr) {
        log("ERROR", tag, msg);
        tr.printStackTrace();
    }

    private void log(String level, String tag, String msg) {
        System.out.printf("%s %7.3f [%s] %s%n", level, getTime(startTime), tag, msg);
    }

    private float getTime(long startTime) {
        return ((float) (System.currentTimeMillis() - startTime)) / 1000;
    }
}
