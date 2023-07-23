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

		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				KitsunDebugger.CACHE.clear();
				ModUpdatesScheduler.schedule();
				QuiltUpdatesScheduler.schedule();
			}
		}, 0, 60 * (60 * 1000));

		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				ModCardsScheduler.schedule();
			}
		}, 0, (60 * 4) * (60 * 1000));
	}

}
