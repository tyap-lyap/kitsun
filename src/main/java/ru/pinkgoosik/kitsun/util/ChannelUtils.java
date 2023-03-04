package ru.pinkgoosik.kitsun.util;

import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import ru.pinkgoosik.kitsun.Bot;

import java.util.Optional;

public class ChannelUtils {

	public static boolean exist(String serverId, String channelId) {
		var guild = Bot.jda.getGuildById(serverId);

		if(guild != null) {
			var channel = guild.getGuildChannelById(channelId);
			return channel != null;
		}
		return false;

//		AtomicBoolean channelExist = new AtomicBoolean(false);
//
//		Bot.rest.getGuildById(Snowflake.of(serverId)).getChannels().all(channelData -> {
//			if(channelData.id().asString().equals(channelId)) {
//				channelExist.set(true);
//			}
//			return true;
//		}).block();
//
//		return channelExist.get();
	}

	public static boolean isVoiceChannel(String serverId, String channelId) {
		var guild = Bot.jda.getGuildById(serverId);

		if(guild != null) {
			var channel = guild.getGuildChannelById(channelId);
			return channel instanceof VoiceChannel;
		}
		return false;

//		AtomicBoolean isVoiceChannel = new AtomicBoolean(false);

//		Bot.rest.getGuildById(Snowflake.of(serverId)).getChannels().all(channelData -> {
//			if(channelData.id().asString().equals(channelId) && !channelData.bitrate().isAbsent()) {
//				isVoiceChannel.set(true);
//			}
//			return true;
//		}).block();
//
//		return isVoiceChannel.get();
	}

	public static Optional<Integer> getMembers(VoiceChannel channel) {
		var members = channel.getMembers();
		int membersCount = 0;

		for (var member : members) {
			var state = member.getVoiceState();
			if (state != null) {
				var stateChannel = state.getChannel();
				if (stateChannel != null) {
					if (stateChannel.getId().equals(channel.getId())) {
						membersCount++;
					}
				}

			}
		}
		return Optional.of(membersCount);
	}
}
