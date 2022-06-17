package ru.pinkgoosik.kitsun;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.channel.VoiceChannelDeleteEvent;
import discord4j.core.event.domain.channel.VoiceChannelUpdateEvent;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import discord4j.core.event.domain.lifecycle.ConnectEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.MessageDeleteEvent;
import discord4j.core.event.domain.message.MessageUpdateEvent;
import discord4j.core.event.domain.role.RoleCreateEvent;
import discord4j.core.event.domain.role.RoleDeleteEvent;
import discord4j.core.event.domain.role.RoleUpdateEvent;
import discord4j.gateway.intent.IntentSet;
import discord4j.rest.RestClient;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;
import ru.pinkgoosik.kitsun.cache.CachedData;
import ru.pinkgoosik.kitsun.command.Commands;
import ru.pinkgoosik.kitsun.config.Secrets;
import ru.pinkgoosik.kitsun.event.DiscordEvents;

public class Bot {
    public static Secrets secrets;
    public static CachedData<Secrets> secretsCache = new CachedData<>("", "secrets.json", () -> Secrets.DEFAULT);
    public static final Logger LOGGER = Loggers.getLogger("Kitsun");
    public static RestClient rest;
    public static GatewayDiscordClient client;

    public static void main(String[] args) {
        secrets = secretsCache.read(Secrets.class);
        Commands.init();
        initDiscordClient();
    }

    public static void initDiscordClient() {
        String token = secrets.discordBotToken;
        if (token.isBlank()) {
            LOGGER.error("Token is blank");
            System.exit(0);
        }
        DiscordClientBuilder.create(token)
                .build().gateway()
                .setEnabledIntents(IntentSet.all())
                .withGateway(gateway -> {
                    Mono<Void> connect = gateway.on(ConnectEvent.class, event -> {
                        DiscordEvents.onConnect(event);
                        return Mono.empty();
                    }).then();
                    Mono<Void> memberJoin = gateway.on(MemberJoinEvent.class, event -> {
                        DiscordEvents.onMemberJoin(event);
                        return Mono.empty();
                    }).then();
                    Mono<Void> memberLeave = gateway.on(MemberLeaveEvent.class, event -> {
                        DiscordEvents.onMemberLeave(event);
                        return Mono.empty();
                    }).then();
                    Mono<Void> messageCreate = gateway.on(MessageCreateEvent.class, event -> {
                        DiscordEvents.onMessageCreate(event);
                        return Mono.empty();
                    }).then();
                    Mono<Void> messageUpdate = gateway.on(MessageUpdateEvent.class, event -> {
                        DiscordEvents.onMessageUpdate(event);
                        return Mono.empty();
                    }).then();
                    Mono<Void> messageDelete = gateway.on(MessageDeleteEvent.class, event -> {
                        DiscordEvents.onMessageDelete(event);
                        return Mono.empty();
                    }).then();
                    Mono<Void> roleCreate = gateway.on(RoleCreateEvent.class, event -> {
                        DiscordEvents.onRoleCreate(event);
                        return Mono.empty();
                    }).then();
                    Mono<Void> roleDelete = gateway.on(RoleDeleteEvent.class, event -> {
                        DiscordEvents.onRoleDelete(event);
                        return Mono.empty();
                    }).then();
                    Mono<Void> roleUpdate = gateway.on(RoleUpdateEvent.class, event -> {
                        DiscordEvents.onRoleUpdate(event);
                        return Mono.empty();
                    }).then();
                    Mono<Void> voiceChannelUpdate = gateway.on(VoiceChannelUpdateEvent.class, event -> {
                        DiscordEvents.onVoiceChannelUpdate(event);
                        return Mono.empty();
                    }).then();
                    Mono<Void> voiceChannelDelete = gateway.on(VoiceChannelDeleteEvent.class, event -> {
                        DiscordEvents.onVoiceChannelDelete(event);
                        return Mono.empty();
                    }).then();
                    Mono<Void> voiceStateUpdate = gateway.on(VoiceStateUpdateEvent.class, event -> {
                        DiscordEvents.onVoiceStateUpdate(event);
                        return Mono.empty();
                    }).then();
                    return Mono.when(connect, memberJoin, memberLeave, messageCreate,
                            messageUpdate, messageDelete, roleCreate, roleDelete,
                            roleUpdate, voiceChannelUpdate, voiceChannelDelete, voiceStateUpdate);
                })
                .block();
    }
}
