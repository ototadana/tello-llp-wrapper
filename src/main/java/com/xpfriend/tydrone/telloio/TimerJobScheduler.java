package com.xpfriend.tydrone.telloio;

import com.xpfriend.tydrone.core.Info;
import com.xpfriend.tydrone.core.Startable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TimerJobScheduler extends Startable {
    private final List<TimerJobManager> keepers = new ArrayList<>();

    public TimerJobScheduler(MessageHandlerManager manger) {
        for (TimerJob timerJob : manger.getTimerJobs()) {
            keepers.add(new TimerJobManager(timerJob));
        }
    }

    @Override
    public void start(Info info) throws Exception {
        super.start(info);

        while (info.isActive()) {
            sleep(10);
            long now = System.currentTimeMillis();
            for (TimerJobManager keeper : keepers) {
                if (keeper.isTime(now)) {
                    try {
                        keeper.execute(now);
                    } catch (Exception e) {
                        loge(e);
                    }
                }
            }
        }

        logi("done");
    }

    private static class TimerJobManager {
        private final long interval;
        private final TimerJob timerJob;
        private long lastExecutedTime;

        public TimerJobManager(TimerJob timerJob) {
            this.timerJob = timerJob;
            this.interval = timerJob.getInterval();
        }

        public boolean isTime(long now) {
            return now > lastExecutedTime + interval;
        }

        public void execute(long now) throws IOException {
            lastExecutedTime = now;
            timerJob.execute();
        }
    }
}
