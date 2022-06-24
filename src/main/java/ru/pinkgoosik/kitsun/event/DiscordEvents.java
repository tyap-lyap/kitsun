package ru.pinkgoosik.kitsun.event;

import discord4j.common.util.Snowflake;
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
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.rest.http.client.ClientException;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.command.Commands;
import ru.pinkgoosik.kitsun.feature.KitsunDebugger;
import ru.pinkgoosik.kitsun.schedule.Scheduler;
import ru.pinkgoosik.kitsun.util.ServerUtils;

import java.util.ArrayList;
import java.util.Optional;

public class DiscordEvents {

    public static void onConnect(ConnectEvent event) {
        Bot.rest = event.getClient().getRestClient();
        Bot.client = event.getClient();
        KitsunDebugger.info("Kitsun is now running!");

        // TODO: I should probably check if channel has members or not but I'm lazy ass
        try {
            ServerUtils.forEach(serverData -> {
                var manager = serverData.autoChannels;
                manager.get().sessions.forEach(session -> {
                    try {
                        if(event.getClient().getChannelById(Snowflake.of(session.channel)).block() instanceof VoiceChannel voiceChannel) {
                            var member = Bot.client.getMemberById(Snowflake.of(serverData.server), Snowflake.of(session.owner)).block();
                            if(serverData.logger.get().enabled) {
                                serverData.logger.get().onVoiceChannelDelete(null, member, voiceChannel);
                            }
                            voiceChannel.delete("Refresh sessions data on bot reconnection.").block();
                        }
                    }
                    catch (ClientException ignored) {}
                });
                manager.get().sessions = new ArrayList<>();
                manager.save();
            });
        }
        catch (Exception e) {
            String msg = "Failed to refresh sessions data due to an exception:\n" + e;
            Bot.LOGGER.error(msg);
            KitsunDebugger.report(msg, e, true);
        }
        Scheduler.start();
    }

    public static void onMessageCreate(MessageCreateEvent event) {
        Commands.onMessageCreate(event);
    }

    public static void onMessageUpdate(MessageUpdateEvent event) {
        try {
            var newMessage = event.getMessage().blockOptional();
            var oldMessage = event.getOld();
            var guildId = event.getGuildId();

            if(guildId.isPresent() && oldMessage.isPresent() && newMessage.isPresent()) {
                ServerUtils.runFor(guildId.get(), serverData -> {
                    if(serverData.logger.get().enabled) {
                        serverData.logger.get().onMessageUpdate(oldMessage.get(), newMessage.get());
                    }
                });
            }
        }
        catch (Exception e) {
            String msg = "Failed to proceed message update event due to an exception:\n" + e;
            Bot.LOGGER.error(msg);
            KitsunDebugger.report(msg, e, false);
        }
    }

    public static void onMessageDelete(MessageDeleteEvent event) {
        try {
            var guildId = event.getGuildId();
            var message = event.getMessage();

            if(guildId.isPresent() && message.isPresent()) {
                if (message.get().getAuthor().isPresent() && !message.get().getAuthor().get().isBot()) {
                    ServerUtils.runFor(guildId.get(), serverData -> {
                        if(serverData.logger.get().enabled) {
                            serverData.logger.get().onMessageDelete(message.get());
                        }
                    });
                }
            }
        }
        catch (Exception e) {
            String msg = "Failed to proceed message delete event due to an exception:\n" + e;
            Bot.LOGGER.error(msg);
            KitsunDebugger.report(msg, e, false);
        }
    }

    public static void onMemberJoin(MemberJoinEvent event) {
        try {
            ServerUtils.runFor(event.getGuildId(), (serverData) -> {
                if(serverData.logger.get().enabled) {
                    serverData.logger.get().onMemberJoin(event.getMember());
                }
                if(!serverData.config.get().general.memberRoleId.isBlank()) {
                    event.getMember().addRole(Snowflake.of(serverData.config.get().general.memberRoleId)).block();
                }
            });
        }
        catch (Exception e) {
            String msg = "Failed to proceed member join event due to an exception:\n" + e;
            Bot.LOGGER.error(msg);
            KitsunDebugger.report(msg, e, false);
        }
    }

    public static void onMemberLeave(MemberLeaveEvent event) {
        try {
            event.getMember().ifPresent(member -> ServerUtils.runFor(event.getGuildId(), serverData -> {
                if(serverData.logger.get().enabled) {
                    serverData.logger.get().onMemberLeave(member);
                }
            }));
        }
        catch (Exception e) {
            String msg = "Failed to proceed member leave event due to an exception:\n" + e;
            Bot.LOGGER.error(msg);
            KitsunDebugger.report(msg, e, false);
        }
    }

    public static void onRoleCreate(RoleCreateEvent event) {

    }

    public static void onRoleDelete(RoleDeleteEvent event) {

    }

    public static void onRoleUpdate(RoleUpdateEvent event) {

    }

    public static void onVoiceChannelUpdate(VoiceChannelUpdateEvent event) {
        try {
            VoiceChannel current = event.getCurrent();
            Optional<VoiceChannel> old = event.getOld();
            old.ifPresent(oldChannel -> ServerUtils.runFor(current.getGuildId(), serverData -> {
                if (serverData.logger.get().enabled) {
                    if (!oldChannel.getName().equals(current.getName())) {
                        serverData.logger.get().onVoiceChannelNameUpdate(oldChannel, current);
                    }
                }
            }));
        }
        catch (Exception e) {
            String msg = "Failed to proceed voice channel update event due to an exception:\n" + e;
            Bot.LOGGER.error(msg);
            KitsunDebugger.report(msg, e, false);
        }
    }

    public static void onVoiceChannelDelete(VoiceChannelDeleteEvent event) {
        try {
            ServerUtils.forEach(serverData -> {
                var manager = serverData.autoChannels;
                manager.get().getSession(event.getChannel().getId().asString()).ifPresent(session -> {
                    manager.get().refresh();
                    var member = Bot.client.getMemberById(Snowflake.of(serverData.server), Snowflake.of(session.owner)).block();
                    if(serverData.logger.get().enabled) {
                        serverData.logger.get().onVoiceChannelDelete(null, member, event.getChannel());
                    }
                });

                if(manager.get().enabled) {
                    if(manager.get().parentChannel.equals(event.getChannel().getId().asString())) {
                        manager.get().disable();
                        manager.save();
                    }
                }
            });
        }
        catch (Exception e) {
            String msg = "Failed to proceed voice channel delete event due to an exception:\n" + e;
            Bot.LOGGER.error(msg);
            KitsunDebugger.report(msg, e, false);
        }
    }

    public static void onVoiceStateUpdate(VoiceStateUpdateEvent event) {

    }
}
