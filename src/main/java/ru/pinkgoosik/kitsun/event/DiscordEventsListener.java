package ru.pinkgoosik.kitsun.event;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.apache.commons.collections4.map.HashedMap;
import org.jetbrains.annotations.NotNull;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.api.mojang.MojangAPI;
import ru.pinkgoosik.kitsun.cache.ServerData;
import ru.pinkgoosik.kitsun.command.CommandHelper;
import ru.pinkgoosik.kitsun.command.KitsunCommands;
import ru.pinkgoosik.kitsun.feature.AutoReaction;
import ru.pinkgoosik.kitsun.feature.KitsunDebugger;
import ru.pinkgoosik.kitsun.schedule.Scheduler;
import ru.pinkgoosik.kitsun.util.ServerUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DiscordEventsListener extends ListenerAdapter {

	public static void onConnect(ReadyEvent event) {
		Bot.jda = event.getJDA();
		String note = Bot.secrets.get().note;
		KitsunDebugger.onConnect(event);
		KitsunDebugger.info(note.isEmpty() ? "Kitsun is now running!" : note);
		Scheduler.start();
		KitsunCommands.onConnect();
	}

	@Override
	public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
		ServerUtils.runFor(event.getGuild().getId(), data -> {
			data.autoChannels.get(manager -> {
				if(manager.enabled) {
					manager.onGuildVoiceUpdate(event);
				}
			});
		});
	}

	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
		try {
			KitsunCommands.COMMANDS.forEach(commandNext -> {
				if(event.getInteraction().getName().equals(commandNext.getName()) && event.getGuild() != null) {
					commandNext.respond(event, new CommandHelper(event, ServerData.get(event.getGuild().getId())));
				}
			});
		}
		catch(Exception e) {
			KitsunDebugger.report("Failed to proceed chat input interaction event due to an exception:\n" + e);
		}
	}

	@Override
	public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
		if (event.getName().equals("import-fabric") || event.getName().equals("import-quilt") && event.getFocusedOption().getName().equals("version")) {
			ArrayList<String> versions = MojangAPI.getMcVersionsCache();

			List<Command.Choice> options = Stream.of(versions.toArray(new String[]{}))
				.filter(word -> word.startsWith(event.getFocusedOption().getValue())) // only display words that start with the user's current input
				.map(word -> new Command.Choice(word, word)) // map the words to choices
				.collect(Collectors.toList());

			event.replyChoices(options.size() <= 20 ? options : options.subList(0, 20)).queue();
		}
		if (event.getName().equals("auto-reaction") && event.getSubcommandName().equals("remove") && event.getFocusedOption().getName().equals("regex")) {

			if(event.getGuild() != null) {
				var data = ServerData.get(event.getGuild().getId());
				ArrayList<String> regexes = new ArrayList<>();
				for(var react : data.autoReactions.get()) {
					if(!regexes.contains(react.regex))regexes.add(react.regex);
				}

				List<Command.Choice> options = Stream.of(regexes.toArray(new String[]{}))
					.filter(word -> word.startsWith(event.getFocusedOption().getValue())) // only display words that start with the user's current input
					.map(word -> new Command.Choice(word, word)) // map the words to choices
					.collect(Collectors.toList());

				event.replyChoices(options.size() <= 20 ? options : options.subList(0, 20)).queue();
			}

		}
	}

	static Map<String, CachedMessage> cachedMessages = new HashedMap<>();

	public record CachedMessage(String id, String memberId, String channelId, String contentRaw){}

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		var content = event.getMessage().getContentRaw().toLowerCase();

		ServerUtils.runFor(event.getGuild().getId(), serverData -> {
			boolean updateList = false;
			for(AutoReaction react : serverData.autoReactions.get()) {
				if(Pattern.compile(react.regex.toLowerCase()).matcher(content).find()) {

					if(react.unicode) {
						event.getMessage().addReaction(Emoji.fromUnicode(react.emoji)).queue();
					}
					else {
						var emoji = event.getMessage().getGuild().getEmojiById(react.emoji);
						if(emoji != null) {
							event.getMessage().addReaction(emoji).queue();
						}
						else {
							react.shouldBeRemoved = true;
							updateList = true;
						}
					}
				}
			}
			if (updateList) {
				ArrayList<AutoReaction> autoReactions = new ArrayList<>(List.of(serverData.autoReactions.get()));
				autoReactions.removeIf(card -> card.shouldBeRemoved);
				serverData.autoReactions.set(autoReactions.toArray(new AutoReaction[0]));
				serverData.autoReactions.save();
			}
		});
		cachedMessages.put(event.getMessageId(), new CachedMessage(event.getMessageId(), event.getAuthor().getId(), event.getChannel().getId(),event.getMessage().getContentRaw()));
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
			});
		}
		catch(Exception e) {
			KitsunDebugger.report("Failed to proceed member join event due to an exception:\n" + e);
		}
	}

	@Override
	public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
		try {
			ServerUtils.runFor(event.getGuild().getId(), data -> data.logger.get().ifEnabled(log -> log.onMemberLeave(event.getUser())));
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
