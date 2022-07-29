package com.xpfriend.tydrone.telloio;

import com.xpfriend.tydrone.core.Info;
import com.xpfriend.tydrone.core.Logger;
import com.xpfriend.tydrone.core.OutputStreamFactory;
import com.xpfriend.tydrone.telloio.handlers.ConnectHandler;
import com.xpfriend.tydrone.telloio.handlers.LandHandler;
import com.xpfriend.tydrone.telloio.handlers.PictureHandler;
import com.xpfriend.tydrone.telloio.handlers.StateHandler;
import com.xpfriend.tydrone.telloio.handlers.StickHandler;
import com.xpfriend.tydrone.telloio.handlers.TakeoffHandler;
import com.xpfriend.tydrone.telloio.handlers.TimeHandler;
import com.xpfriend.tydrone.telloio.handlers.VideoStartHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageHandlerManager {
    static final InetSocketAddress droneAddress = new InetSocketAddress("192.168.10.1", 8889);
    private static final int RETRY_OVER = 100;
    private final Logger logger;
    private final ByteBuffer buffer = ByteBuffer.allocate(2048);
    private final Map<String, MessageHandler> commandMap = new HashMap<>();
    private final Map<Integer, MessageHandler> messageIdMap = new HashMap<>();
    private final List<TimerJob> timerJobSet = new ArrayList<>();
    private DatagramChannel channel;

    public MessageHandlerManager(OutputStreamFactory outputStreamFactory, Logger logger) {
        this.logger = logger;
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        addHandler(new TakeoffHandler(), new String[]{"takeoff"}, new int[]{84});
        addHandler(new LandHandler(), new String[]{"land"}, new int[]{85});
        addHandler(new StateHandler(), new String[]{}, new int[]{26, 53, 86, 4176, 4177});
        addHandler(new PictureHandler(outputStreamFactory), new String[]{"picture"}, new int[]{48, 98, 99, 100});
        addHandler(new StickHandler(), new String[]{"stick"}, new int[]{});
        addHandler(new TimeHandler(), new String[]{"time"}, new int[]{70});
        addHandler(new VideoStartHandler(), new String[]{}, new int[]{});
    }

    private void addHandler(MessageHandler handler, String[] commands, int[] messageIds) {
        handler.setLogger(logger);
        for (String command : commands) {
            commandMap.put(command, handler);
        }
        for (int id : messageIds) {
            messageIdMap.put(id, handler);
        }
        if (handler instanceof TimerJob) {
            timerJobSet.add((TimerJob) handler);
        }
    }

    public DatagramChannel connect() throws IOException {
        channel = DatagramChannel.open().bind(new InetSocketAddress(9000));
        channel.socket().setSoTimeout(10000);

        ConnectHandler connectHandler = new ConnectHandler();
        connectHandler.connect(channel, droneAddress);

        for (int i = 0; i < RETRY_OVER; i++) {
            receive(channel, buffer);
            if (connectHandler.isConnected(buffer)) {
                send("time", null);
                return channel;
            }
        }

        throw new IOException("cannot connect");
    }

    public void receive(Info info) throws IOException {
        receive(channel, buffer);
        Message message = new Message(buffer);
        MessageHandler handler = messageIdMap.get(message.getId());
        if (handler != null) {
            handler.setChannel(channel);
            handler.handleMessage(message, info);
        } else {
            logger.logw("LLPMessageHandlerManager", "Unknown message: " + message.getId());
        }
    }

    private void receive(DatagramChannel channel, ByteBuffer buffer) throws IOException {
        buffer.clear();
        channel.receive(buffer);
        buffer.flip();
    }

    public void send(String command, Info info) throws IOException {
        String[] s = command.split(" ");
        MessageHandler handler = commandMap.get(s[0]);
        if (handler != null) {
            handler.setChannel(channel);
            handler.sendRequest(command, info);
        } else {
            logger.loge("LLPMessageHandlerManager", "Unknown command: " + command);
        }
    }

    public List<TimerJob> getTimerJobs() {
        for (TimerJob timerJob : timerJobSet) {
            ((MessageHandler) timerJob).setChannel(channel);
        }
        return timerJobSet;
    }
}
