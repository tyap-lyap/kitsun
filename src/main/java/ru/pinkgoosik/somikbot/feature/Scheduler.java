package ru.pinkgoosik.somikbot.feature;

import ru.pinkgoosik.somikbot.Bot;

import java.util.Timer;
import java.util.TimerTask;

public class Scheduler {

    public static void start(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Bot.publishers.forEach(ModChangelogPublisher::check);
                Bot.mcUpdatesPublisher.check();
            }
        }, 0, 180 * 1000);
    }
}
