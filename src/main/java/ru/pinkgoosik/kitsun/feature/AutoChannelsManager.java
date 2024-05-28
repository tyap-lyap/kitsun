package ru.pinkgoosik.kitsun.feature;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import ru.pinkgoosik.kitsun.DiscordApp;
import ru.pinkgoosik.kitsun.cache.ServerData;
import ru.pinkgoosik.kitsun.debug.KitsunDebugWebhook;
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

	// TODO: rewrite this disaster
	public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
		try {
			if(DiscordApp.jda.getGuildChannelById(this.parentChannel) instanceof VoiceChannel channel) {
				var members = channel.getMembers();
				for (var member : members) {
					var state = member.getVoiceState();
					if(state != null) {
						var stateChannel = state.getChannel();
						if(stateChannel != null) {
							if (stateChannel.getId().equals(channel.getId())) {
								this.onParentChannelJoin(member);
								return;
							}
						}

					}

				}
			}
		}
		catch(Exception e) {
			if(e.getMessage().contains("Unknown Channel")) {
				this.enabled = false;
				ServerData.get(this.server).autoChannels.save();
			}
			else {
				KitsunDebugWebhook.ping("Failed to get parent channel duo to an exception:\n" + e);
			}
		}
		this.sessions.forEach(session -> {
			try {
				if(DiscordApp.jda.getGuildChannelById(session.channel) instanceof VoiceChannel channel) {
					var members = ChannelUtils.getMembers(channel);
					if(members.isPresent() && members.get() == 0) {
						var guild = DiscordApp.jda.getGuildById(server);
						if (guild != null) {
							var member = guild.getMemberById(session.owner);
							ServerData.get(server).logger.get().ifEnabled(log -> log.onVoiceChannelDelete(session, member, channel));
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
					KitsunDebugWebhook.ping("Failed to delete auto channel duo to an exception:\n" + e);
				}
			}
		});
		this.refresh();
	}

	public void onParentChannelJoin(Member member) {
		if(!this.hasEmptySession(member)) {
			this.createSession(member);
		}
	}

	private boolean hasEmptySession(Member member) {
		for(var session : this.sessions) {
			var chan = DiscordApp.jda.getGuildChannelById(session.channel);
			if(session.owner.equals(member.getId()) && chan instanceof VoiceChannel channel) {
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
