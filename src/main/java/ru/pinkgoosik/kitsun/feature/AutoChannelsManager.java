package ru.pinkgoosik.kitsun.feature;

import discord4j.common.util.Snowflake;
import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.core.spec.GuildMemberEditSpec;
import discord4j.core.spec.VoiceChannelCreateSpec;
import discord4j.discordjson.json.ChannelData;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
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
			Channel chan = Bot.client.getChannelById(Snowflake.of(session.channel)).block();
			if(session.owner.equals(member.getId().asString()) && chan instanceof VoiceChannel channel) {
				var members = ChannelUtils.getMembers(channel);
				if(members.isPresent() && members.get() == 0) {
					member.edit(GuildMemberEditSpec.builder().newVoiceChannelOrNull(Snowflake.of(session.channel)).build()).block();
					return true;
				}
			}
		}
		return false;
	}

	public void createSession(Member member) {
		ChannelData channelData = Bot.rest.getChannelById(Snowflake.of(parentChannel)).getData().block();
		Guild guild = Bot.client.getGuildById(Snowflake.of(server)).block();
		if(channelData == null || guild == null) return;

		if(!channelData.parentId().isAbsent() && channelData.parentId().get().isPresent()) {
			String memberName = member.getDisplayName();
			String category = channelData.parentId().get().get().asString();
			VoiceChannel channel = guild.createVoiceChannel(VoiceChannelCreateSpec.builder().name(memberName + "'s lounge").parentId(Snowflake.of(category)).build()).block();

			if(channel != null) {
				Snowflake memberId = member.getId();
				channel.addMemberOverwrite(memberId, PermissionOverwrite.forMember(memberId, PermissionSet.of(Permission.MANAGE_CHANNELS), PermissionSet.none())).block();
				try {
					member.edit(GuildMemberEditSpec.builder().newVoiceChannelOrNull(channel.getId()).build()).block();
				}
				catch(Exception e) {
					KitsunDebugger.report("Failed to move member due to an exception:\n" + e);
				}
				this.sessions.add(new Session(member.getId().asString(), channel.getId().asString()));
				ServerData serverData = ServerData.get(server);
				serverData.logger.get().ifEnabled(log -> log.onAutoChannelCreate(member, channel));
				serverData.save();
			}
		}
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
