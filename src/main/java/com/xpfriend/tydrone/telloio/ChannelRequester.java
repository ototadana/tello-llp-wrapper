package com.xpfriend.tydrone.telloio;

import com.xpfriend.tydrone.core.Info;
import com.xpfriend.tydrone.core.Startable;

public class ChannelRequester extends Startable {
    private final MessageHandlerManager handlerManager;

    public ChannelRequester(MessageHandlerManager handlerManager) {
        this.handlerManager = handlerManager;
    }

    @Override
    public void start(Info info) throws Exception {
        super.start(info);

        while (info.isActive()) {
            String msg = info.pickCommand();
            if (msg == null || msg.isEmpty()) {
                sleep(10);
                continue;
            }

            try {
                handlerManager.send(msg, info);
            } catch (Exception e) {
                loge(e);
            }
        }

        logi("done");
    }
}
