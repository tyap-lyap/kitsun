package ru.pinkgoosik.kitsun.feature;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import ru.pinkgoosik.kitsun.DiscordApp;
import ru.pinkgoosik.kitsun.cache.ServerData;
import ru.pinkgoosik.kitsun.util.ChannelUtils;
import ru.pinkgoosik.kitsun.util.DurationUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;

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

	public void ifEnabled(Consumer<AutoChannelsManager> consumer) {
		if(enabled) consumer.accept(this);
	}

	public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
		sessions.removeIf(session -> session.shouldBeRemoved || !ChannelUtils.exist(server, session.channel));

		var member = event.getMember();
		var joinedChannel = event.getChannelJoined();
		var leftChannel = event.getChannelLeft();

		if(joinedChannel != null && leftChannel == null) { // First ever channel join
			if(joinedChannel.getId().equals(this.parentChannel)) {
				this.createSession(member);
			}
			else this.onChannelJoin(member, joinedChannel);
		}
		else if(joinedChannel != null) { // Moved from channel to channel
			if(joinedChannel.getId().equals(this.parentChannel)) {
				if(!this.hasEmptySession(member)) {
					this.createSession(member);
					this.onChannelLeft(member, leftChannel);
				}
			}
			else if(!leftChannel.getId().equals(this.parentChannel)) this.onChannelJoin(member, joinedChannel);
		}
		else if(leftChannel != null) { // Completely left channel
			this.onChannelLeft(member, leftChannel);
		}
		ServerData.get(server).autoChannels.save();
	}

	void onChannelJoin(Member member, AudioChannelUnion channel) {
		getSession(channel.getId()).ifPresent(session -> {
			session.updateHistory("<@" + member.getId() + ">" + " joined");
		});
	}

	void onChannelLeft(Member member, AudioChannelUnion channel) {
		getSession(channel.getId()).ifPresent(session -> {
			session.updateHistory("<@" + member.getId() + ">" + " left");

			if(channel.getMembers().isEmpty()) {
				channel.delete().queue();
			}
		});
	}

	private boolean hasEmptySession(Member member) {
		for(var session : this.sessions) {
			if(session.owner.equals(member.getId()) && DiscordApp.jda.getGuildChannelById(session.channel) instanceof VoiceChannel channel) {
				var members = ChannelUtils.getMembers(channel);
				if(members.isPresent() && members.get() == 0) {
					member.getGuild().moveVoiceMember(member, channel).queue();
					return true;
				}
			}
		}
		return false;
	}

	public void createSession(Member member) {
		var parentChannel = DiscordApp.jda.getGuildChannelById(this.parentChannel);
		var guild = DiscordApp.jda.getGuildById(server);
		if(parentChannel == null || guild == null) return;

		if(parentChannel instanceof VoiceChannel vc) {
			var category = vc.getParentCategory();
			guild.createVoiceChannel("lounge | " + (this.sessions.size() + 1), category).queue(voiceChannel -> {
				if(voiceChannel != null) {
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
	}

	public Optional<Session> getSession(String channelID) {
		for(Session session : sessions) {
			if(session.channel.equals(channelID)) return Optional.of(session);
		}
		return Optional.empty();
	}

	public static class Session {
		public String created = Instant.now().toString();
		public String owner;
		public String channel;
		public boolean shouldBeRemoved = false;
		public String history = "";

		public Session(String owner, String channel) {
			this.owner = owner;
			this.channel = channel;
			this.updateHistory("<@" + owner + ">" + " joined");
		}

		public void updateHistory(String line) {
			this.history = history + (history.isEmpty() ? "" : "\n") + DurationUtils.format(Duration.between(Instant.parse(this.created), Instant.now())) + " - " + line;
		}
	}
}
