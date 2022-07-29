package com.xpfriend.tydrone.core;

import java.util.List;

public class Runner {
    private final Logger logger;

    public Runner(Logger logger) {
        this.logger = logger;
    }

    public void run(Info info, List<Startable> startables) {
        for (Startable startable : startables) {
            startable.setLogger(logger);
            start(info, startable);
        }
    }

    private void start(Info info, Startable startable) {
        new Thread(() -> {
            try {
                startable.start(info);
            } catch (Exception e) {
                logger.loge("Runner", "start", e);
            }
        }).start();
    }
}
