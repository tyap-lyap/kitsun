package ru.pinkgoosik.kitsun.schedule;

import java.util.*;

public class Scheduler {

    public static void start() {
        Timer minTimer = new Timer();
        minTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                PublishersScheduler.schedule();
                MCUpdatesScheduler.schedule();
                QuiltUpdatesScheduler.schedule();
            }
        }, 0, 60 * 1000);

        Timer secTimer = new Timer();
        secTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                AutoChannelsScheduler.schedule();
            }
        }, 0, 1000);
    }

}
