package ru.pinkgoosik.kitsun.schedule;

import ru.pinkgoosik.kitsun.feature.KitsunDebugger;

import java.util.*;

public class Scheduler {

    public static void start() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                MCUpdatesScheduler.schedule();
            }
        }, 0, 5 * (60 * 1000));

        Timer secTimer = new Timer();
        secTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                AutoChannelsScheduler.schedule();
            }
        }, 0, 1000);

        Timer timer1 = new Timer();
        timer1.schedule(new TimerTask() {
            @Override
            public void run() {
                PublishersScheduler.schedule();
                QuiltUpdatesScheduler.schedule();
                ModCardsScheduler.schedule();
                KitsunDebugger.CACHE.clear();
            }
        }, 0, 60 * (60 * 1000));

        Timer timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            @Override
            public void run() {
                ModCardsScheduler.schedule();
            }
        }, 0, (60 * 4) * (60 * 1000));
    }

}
