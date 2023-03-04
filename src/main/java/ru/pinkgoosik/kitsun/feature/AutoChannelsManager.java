package ru.pinkgoosik.kitsun.feature;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.cache.ServerData;
import ru.pinkgoosik.kitsun.util.ChannelUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class AutoChannelsManager {
	public String server;
	public boolean enabled = false;
	public String parentChannel = "";
	public ArrayList<Session> sessions = new ArrayList<>();

	public AutoChannelsManager(String serverID) {
		this.server = serverID;
	}

	public void enable(String parentChannelID) {
		this.enabled = true;
		this.parentChannel = parentChannelID;
	}

	public void disable() {
		this.enabled = false;
		this.parentChannel = "";
	}

	public void onParentChannelJoin(Member member) {
		if(!this.hasEmptySession(member)) {
			this.createSession(member);
		}
	}

	private boolean hasEmptySession(Member member) {
		for(var session : this.sessions) {
			var chan = Bot.jda.getGuildChannelById(session.channel);
			if(session.owner.equals(member.getId()) && chan instanceof VoiceChannel channel) {
				var members = ChannelUtils.getMembers(channel);
				if(members.isPresent() && members.get() == 0) {
					member.getGuild().moveVoiceMember(member, channel).queue();
//					member.edit(GuildMemberEditSpec.builder().newVoiceChannelOrNull(Snowflake.of(session.channel)).build()).block();
					return true;
				}
			}
		}
		return false;
	}

	public void createSession(Member member) {
		var parentChannel = Bot.jda.getGuildChannelById(this.parentChannel);
		var guild = Bot.jda.getGuildById(server);
		if(parentChannel == null || guild == null) return;

		if(parentChannel instanceof VoiceChannel vc) {
			var category = vc.getParentCategory();
			guild.createVoiceChannel("lounge | " + (this.sessions.size() + 1), category).queue(voiceChannel -> {
				if(voiceChannel != null) {
					var memberId = member.getId();
					voiceChannel.upsertPermissionOverride(member).setAllowed(Permission.MANAGE_CHANNEL).queue(override -> {
						member.getGuild().moveVoiceMember(member, voiceChannel).queue();
						this.sessions.add(new Session(member.getId(), voiceChannel.getId()));
						ServerData serverData = ServerData.get(server);
						serverData.logger.get().ifEnabled(log -> log.onAutoChannelCreate(member, voiceChannel));
						serverData.save();
					});
				}
			});
		}

//		if(!channelData.parentId().isAbsent() && channelData.parentId().get().isPresent()) {
////			String memberName = member.getDisplayName();
//			String category = channelData.parentId().get().get().asString();
//			VoiceChannel channel = guild.createVoiceChannel(VoiceChannelCreateSpec.builder().name("lounge | " + (this.sessions.size() + 1)).parentId(Snowflake.of(category)).build()).block();
//
//			if(channel != null) {
//				Snowflake memberId = member.getId();
//				channel.addMemberOverwrite(memberId, PermissionOverwrite.forMember(memberId, PermissionSet.of(Permission.MANAGE_CHANNELS), PermissionSet.none())).block();
//				try {
//					member.edit(GuildMemberEditSpec.builder().newVoiceChannelOrNull(channel.getId()).build()).block();
//				}
//				catch(Exception e) {
//					KitsunDebugger.report("Failed to move member due to an exception:\n" + e);
//				}
//				this.sessions.add(new Session(member.getId().asString(), channel.getId().asString()));
//				ServerData serverData = ServerData.get(server);
//				serverData.logger.get().ifEnabled(log -> log.onAutoChannelCreate(member, channel));
//				serverData.save();
//			}
//		}
	}

	public Optional<Session> getSession(String channelID) {
		for(Session session : sessions) {
			if(session.channel.equals(channelID)) return Optional.of(session);
		}
		return Optional.empty();
	}

	public void refresh() {
		AtomicBoolean removedSomething = new AtomicBoolean(false);
		sessions.removeIf(session -> {
			if(session.shouldBeRemoved) {
				removedSomething.set(true);
				return true;
			}
			return false;
		});
		if(removedSomething.get()) {
			ServerData.get(server).save();
		}
	}

	public static class Session {
		public String created = Instant.now().toString();
		public String owner;
		public String channel;
		public boolean shouldBeRemoved = false;

		public Session(String owner, String channel) {
			this.owner = owner;
			this.channel = channel;
		}
	}
}
