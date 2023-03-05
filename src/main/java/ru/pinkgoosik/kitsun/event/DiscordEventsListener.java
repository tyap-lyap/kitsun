package ru.pinkgoosik.kitsun.event;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.collections4.map.HashedMap;
import org.jetbrains.annotations.NotNull;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.command.CommandHelper;
import ru.pinkgoosik.kitsun.command.Commands;
import ru.pinkgoosik.kitsun.feature.KitsunDebugger;
import ru.pinkgoosik.kitsun.schedule.Scheduler;
import ru.pinkgoosik.kitsun.util.ServerUtils;

import java.util.Map;

public class DiscordEventsListener extends ListenerAdapter {

	public static void onConnect(ReadyEvent event) {
		Bot.jda = event.getJDA();
		String note = Bot.secrets.get().note;
		KitsunDebugger.onConnect(event);
		KitsunDebugger.info(note.isEmpty() ? "Kitsun is now running!" : note);
		Scheduler.start();
		Commands.onConnect();
	}

	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
		try {
			Commands.COMMANDS_NEXT.forEach(commandNext -> {
				if(event.getInteraction().getName().equals(commandNext.getName())) {
					commandNext.respond(event, new CommandHelper(event));
				}
			});
		}
		catch(Exception e) {
			KitsunDebugger.report("Failed to proceed chat input interaction event due to an exception:\n" + e);
		}
	}

	static Map<String, CachedMessage> cachedMessages = new HashedMap<>();

	public record CachedMessage(String id, String memberId, String channelId, String contentRaw){}

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		cachedMessages.put(event.getMessageId(), new CachedMessage(event.getMessageId(), event.getAuthor().getId(), event.getChannel().getId(),event.getMessage().getContentRaw()));
		Commands.onMessageCreate(event);
	}

	@Override
	public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
		try {
			var newMessage = event.getMessage();
			var oldMessage = cachedMessages.get(event.getMessageId());
			if(oldMessage == null) return;
			var guildId = event.getGuild().getId();

			ServerUtils.runFor(guildId, data -> data.logger.get().ifEnabled(log -> log.onMessageUpdate(oldMessage, newMessage)));
		}
		catch(Exception e) {
			KitsunDebugger.report("Failed to proceed message update event due to an exception:\n" + e);
		}
	}

	public void onMessageDelete(@NotNull MessageDeleteEvent event) {
		try {
			var guildId = event.getGuild().getId();
			var messageId = event.getMessageId();

			ServerUtils.runFor(guildId, data -> data.logger.get().ifEnabled(log -> log.onMessageDelete(cachedMessages.get(messageId))));

//			if(guildId.isPresent() && message.isPresent()) {
//				if(message.get().getAuthor().isPresent() && !message.get().getAuthor().get().isBot()) {
//					ServerUtils.runFor(guildId.get(), data -> data.logger.get().ifEnabled(log -> log.onMessageDelete(message.get())));
//				}
//			}
		}
		catch(Exception e) {
			KitsunDebugger.report("Failed to proceed message delete event due to an exception:\n" + e);
		}
	}

	@Override
	public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
		try {
			ServerUtils.runFor(event.getGuild().getId(), data -> {
				data.logger.get().ifEnabled(log -> log.onMemberJoin(event.getMember()));
//				if(!data.config.get().general.memberRoleId.isBlank()) {
//					event.getMember().addRole(Snowflake.of(data.config.get().general.memberRoleId)).block();
//				}
			});
		}
		catch(Exception e) {
			KitsunDebugger.report("Failed to proceed member join event due to an exception:\n" + e);
		}
	}

	@Override
	public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
		try {
			ServerUtils.runFor(event.getGuild().getId(), data -> data.logger.get().ifEnabled(log -> log.onMemberLeave(event.getMember())));
		}
		catch(Exception e) {
			KitsunDebugger.report("Failed to proceed member leave event due to an exception:\n" + e);
		}
	}

	@Override
	public void onChannelUpdateName(@NotNull ChannelUpdateNameEvent event) {
		if(event.getChannelType().equals(ChannelType.VOICE)) {
			try {
				var currentName = event.getNewValue();
				var oldName = event.getOldValue();
				if(oldName != null) {
					ServerUtils.runFor(event.getGuild().getId(), data -> {
						if(!oldName.equals(currentName)) {
							data.logger.get().ifEnabled(log -> log.onVoiceChannelNameUpdate(oldName, currentName));
						}
					});
				}

			}
			catch(Exception e) {
				KitsunDebugger.report("Failed to proceed voice channel update event due to an exception:\n" + e);
			}
		}
	}

	@Override
	public void onChannelDelete(@NotNull ChannelDeleteEvent event) {
		super.onChannelDelete(event);
		if(event.getChannelType().equals(ChannelType.VOICE)) {
			try {
				var channel = event.getChannel();
				String channelId = channel.getId();

				ServerUtils.forEach(data -> data.autoChannels.modify(manager -> {
					manager.getSession(channelId).ifPresent(session -> {
						var guild = Bot.jda.getGuildById(data.server);
						if(guild != null) {
							var member = guild.getMemberById(session.owner);
							data.logger.get().ifEnabled(log -> log.onVoiceChannelDelete(session, member, channel));
							session.shouldBeRemoved = true;
						}
					});

					if(manager.enabled) {
						if(manager.parentChannel.equals(channelId)) {
							manager.disable();
						}
					}
					manager.refresh();
				}));
			}
			catch(Exception e) {
				KitsunDebugger.report("Failed to proceed voice channel delete event due to an exception:\n" + e);
			}
		}
	}
}
