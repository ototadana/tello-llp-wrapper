package com.xpfriend.tydrone.telloio;

import java.io.IOException;

public interface TimerJob {
    void execute() throws IOException;

    long getInterval();
}
