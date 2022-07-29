package com.xpfriend.tydrone.factory;

import com.xpfriend.tydrone.core.Logger;
import com.xpfriend.tydrone.core.OutputStreamFactory;
import com.xpfriend.tydrone.core.Startable;
import com.xpfriend.tydrone.recorder.FFmpegVideoRecorder;
import com.xpfriend.tydrone.telloio.*;

import java.io.IOException;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.List;


public class SimpleStartableFactory {
    private static final SimpleStartableFactory instance = new SimpleStartableFactory(new SimpleLogger(), new SimpleOutputStreamFactory());

    private final Logger logger;
    private final OutputStreamFactory outputStreamFactory;

    private SimpleStartableFactory(Logger logger, OutputStreamFactory outputStreamFactory) {
        this.logger = logger;
        this.outputStreamFactory = outputStreamFactory;
    }

    public static SimpleStartableFactory getInstance() {
        return instance;
    }

    public Logger getLogger() {
        return logger;
    }

    public List<Startable> createStartables() throws IOException {
        MessageHandlerManager handlerManager = new MessageHandlerManager(outputStreamFactory, logger);
        DatagramChannel channel = handlerManager.connect();
        List<Startable> startables = new ArrayList<>();
        startables.add(new ChannelReceiver(handlerManager));
        startables.add(new ChannelRequester(handlerManager));
        startables.add(new TimerJobScheduler(handlerManager));
        startables.add(new VideoForwarder(channel));
        startables.add(new FFmpegVideoReceiver());
        startables.add(new FFmpegVideoRecorder(outputStreamFactory));
        return startables;
    }
}
