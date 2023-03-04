package ru.pinkgoosik.kitsun.schedule;

import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.cache.ServerData;
import ru.pinkgoosik.kitsun.feature.AutoChannelsManager;
import ru.pinkgoosik.kitsun.feature.KitsunDebugger;
import ru.pinkgoosik.kitsun.util.ChannelUtils;
import ru.pinkgoosik.kitsun.util.ServerUtils;

public class AutoChannelsScheduler {

	public static void schedule() {
		try {
			ServerUtils.forEach(data -> data.autoChannels.get(manager -> {
				if(manager.enabled) {
					proceed(data, manager);
				}
			}));
		}
		catch(Exception e) {
			KitsunDebugger.report("Failed to schedule auto channels invalidation duo to an exception:\n" + e);
		}
	}

	private static void proceed(ServerData data, AutoChannelsManager manager) {
		try {
			if(Bot.jda.getGuildChannelById(manager.parentChannel) instanceof VoiceChannel channel) {
				var members = channel.getMembers();
				for (var member : members) {
					var state = member.getVoiceState();
					if(state != null) {
						var stateChannel = state.getChannel();
						if(stateChannel != null) {
							if (stateChannel.getId().equals(channel.getId())) {
								manager.onParentChannelJoin(member);
								return;
							}
						}

					}

				}
			}
		}
		catch(Exception e) {
			if(e.getMessage().contains("Unknown Channel")) {
				manager.enabled = false;
				ServerData.get(manager.server).autoChannels.save();
			}
			else {
				KitsunDebugger.ping("Failed to get parent channel duo to an exception:\n" + e);
			}
		}
		manager.sessions.forEach(session -> {
			try {
				if(Bot.jda.getGuildChannelById(session.channel) instanceof VoiceChannel channel) {
					var members = ChannelUtils.getMembers(channel);
					if(members.isPresent() && members.get() == 0) {
						var guild = Bot.jda.getGuildById(data.server);
						if (guild != null) {
							var member = guild.getMemberById(session.owner);
							data.logger.get().ifEnabled(log -> log.onVoiceChannelDelete(session, member, channel));
							channel.delete().queue();
							session.shouldBeRemoved = true;
						}
					}
				}
			}
			catch(Exception e) {
				if(e.getMessage().contains("Unknown Channel")) {
					session.shouldBeRemoved = true;
				}
				else {
					KitsunDebugger.ping("Failed to delete auto channel duo to an exception:\n" + e);
				}
			}
		});
		manager.refresh();
	}
}
