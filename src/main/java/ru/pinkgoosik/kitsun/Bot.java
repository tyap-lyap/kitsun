package ru.pinkgoosik.kitsun;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;
import ru.pinkgoosik.kitsun.cache.Cached;
import ru.pinkgoosik.kitsun.command.Commands;
import ru.pinkgoosik.kitsun.config.Secrets;
import ru.pinkgoosik.kitsun.cosmetics.CosmeticsData;
import ru.pinkgoosik.kitsun.event.DiscordEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class Bot {
	public static final Logger LOGGER = LoggerFactory.getLogger("Kitsun");
	public static Cached<Secrets> secrets = Cached.of("secrets", Secrets.class, () -> Secrets.DEFAULT);
	public static JDA jda;

	public static void main(String[] args) throws Exception {
		Commands.init();
		CosmeticsData.fetch();
		Bot.init();
	}

	public static void init() throws InterruptedException {
		String token = secrets.get().token;
		if(token.isBlank()) {
			LOGGER.error("Token is blank");
			secrets.save();
			System.exit(0);
		}

		// TODO: make DiscordEvents extend ListenerAdapter
		JDA jda = JDABuilder.createDefault(token)
				.addEventListeners(new EventListener() {
					@Override
					public void onEvent(@NotNull GenericEvent event) {
						if(event instanceof ReadyEvent readyEvent) {
							DiscordEvents.onConnect(readyEvent);
						}
					}
				})
				.addEventListeners(new ListenerAdapter() {
					@Override
					public void onMessageReceived(@NotNull MessageReceivedEvent event) {
						super.onMessageReceived(event);
						DiscordEvents.onMessageCreate(event);

					}

					@Override
					public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
						super.onMessageUpdate(event);
						DiscordEvents.onMessageUpdate(event);
					}

					@Override
					public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
						super.onGuildMemberJoin(event);
						DiscordEvents.onMemberJoin(event);
					}

					@Override
					public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
						super.onGuildMemberRemove(event);
						DiscordEvents.onMemberLeave(event);
					}

					@Override
					public void onChannelUpdateName(@NotNull ChannelUpdateNameEvent event) {
						super.onChannelUpdateName(event);
						if(event.getChannelType().equals(ChannelType.VOICE)) {
							DiscordEvents.onVoiceChannelNameUpdate(event);
						}
					}

					@Override
					public void onChannelDelete(@NotNull ChannelDeleteEvent event) {
						super.onChannelDelete(event);
						if(event.getChannelType().equals(ChannelType.VOICE)) {
							DiscordEvents.onVoiceChannelDelete(event);
						}
					}

					@Override
					public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
						super.onSlashCommandInteraction(event);
						DiscordEvents.onCommandUse(event);
					}
				})
				.enableIntents(Arrays.asList(GatewayIntent.values()))
				.build();

		jda.awaitReady();
	}
}
