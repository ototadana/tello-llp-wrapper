package com.xpfriend.tydrone.telloio;

import com.xpfriend.tydrone.core.Info;
import com.xpfriend.tydrone.core.Startable;

public class ChannelReceiver extends Startable {
    private final MessageHandlerManager handlerManager;

    public ChannelReceiver(MessageHandlerManager handlerManager) {
        this.handlerManager = handlerManager;
    }

    @Override
    public void start(Info info) throws Exception {
        super.start(info);

        while (info.isActive()) {
            try {
                handlerManager.receive(info);
            } catch (Exception e) {
                loge(e);
            }
        }
        logi("done");
    }
}
