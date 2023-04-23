package ru.pinkgoosik.kitsun.feature;

import masecla.modrinth4j.model.project.Project;
import org.jetbrains.annotations.Nullable;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.api.curseforge.entity.CurseForgeMod;
import ru.pinkgoosik.kitsun.cache.Cached;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ModMilestones {
	public static final String CHANNEL = "883332880875528232";
	public static final String SERVER = "854349856164020244";

	public static final int[] DOWNLOAD_MILESTONES = new int[]{1000, 10000, 100000, 1000000, 2000000, 3000000, 4000000, 5000000, 6000000, 7000000, 8000000, 9000000, 10000000, 11000000, 12000000, 13000000, 14000000, 15000000, 16000000, 17000000, 18000000, 19000000, 20000000};

	public static Cached<Milestones[]> stack = Cached.of("milestones", Milestones[].class, () -> new Milestones[]{});

	public static void run(Project project, @Nullable CurseForgeMod mod) {
		getMilestones(project.getSlug()).run(project, mod);
	}

	public static Milestones getMilestones(String project) {
		for(Milestones ms : stack.get()) {
			if(ms.project.equals(project)) return ms;
		}
		Milestones milestones = new Milestones(project);

		var old = stack.get();
		var newOnes = new ArrayList<>(List.of(stack.get()));
		newOnes.add(milestones);
		stack.set(newOnes.toArray(old));
		return milestones;
	}

	public static class Milestones {
		public String project;
		public ArrayList<Integer> achievedDownloadMilestones = new ArrayList<>();
		public ArrayList<Integer> yearsMilestones = new ArrayList<>();

		public Milestones(String project) {
			this.project = project;
		}

		public void run(Project project, @Nullable CurseForgeMod mod) {
			for(int ms : DOWNLOAD_MILESTONES) {
				int downloads = project.getDownloads();
				if(mod != null)downloads = downloads + mod.data.downloadCount;

				if(!achievedDownloadMilestones.contains(ms) && downloads >= ms) {
					var announcements = Bot.getGuild(SERVER).getTextChannelById(CHANNEL);

					if(announcements != null) {
						announcements.sendMessage(project.getTitle() + " just achieved " + commas(ms) + " downloads milestone! Congrats :tada:").queue();
						achievedDownloadMilestones.add(ms);
						stack.save();
					}
				}
			}

			Instant created = project.getPublished();
			Instant now = Instant.now();
			int days = (int)ChronoUnit.DAYS.between(created, now);
			int years = days / 365;

			if(years != 0 && !yearsMilestones.contains(years)) {
				var announcements = Bot.getGuild(SERVER).getTextChannelById(CHANNEL);

				if(announcements != null) {
					String word = "years";
					if(years == 1) word = "year";
					announcements.sendMessage(project.getTitle() + " is now " + years + " " + word + " old! Congrats :tada:").queue();
					yearsMilestones.add(years);
					stack.save();
				}
			}
		}

		public static String commas(int value) {
			String num = Integer.toString(value);

			StringBuilder result = new StringBuilder();
			for(int i = 0; i < num.length(); i++) {
				if((num.length() - i - 1) % 3 == 0) {
					result.append(num.charAt(i)).append(",");
				}
				else {
					result.append(num.charAt(i));
				}
			}
			return result.deleteCharAt(result.length() - 1).toString();
		}
	}

}
