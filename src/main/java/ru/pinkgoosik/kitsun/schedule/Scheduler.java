package ru.pinkgoosik.kitsun.schedule;

import java.util.*;

public class Scheduler {

    public static void start() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                PublishersScheduler.schedule();
                MCUpdatesScheduler.schedule();
                QuiltUpdatesScheduler.schedule();
            }
        }, 0, 60 * 1000);
    }

}
