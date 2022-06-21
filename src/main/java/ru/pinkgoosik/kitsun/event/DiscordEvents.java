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
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.VoiceChannel;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.command.Commands;
import ru.pinkgoosik.kitsun.feature.KitsunDebug;
import ru.pinkgoosik.kitsun.schedule.Scheduler;
import ru.pinkgoosik.kitsun.util.ServerUtils;

import java.util.ArrayList;
import java.util.Optional;

public class DiscordEvents {

    public static void onConnect(ConnectEvent event) {
        Bot.rest = event.getClient().getRestClient();
        Bot.client = event.getClient();
        KitsunDebug.info("Kitsun is now running!");
        Scheduler.start();

        // TODO: I should probably check if channel has members or not but I'm lazy ass
        try {
            ServerUtils.forEach(serverData -> {
                serverData.autoChannelsManager.sessions.forEach(session -> {
                    if(event.getClient().getChannelById(Snowflake.of(session.channel)).block() instanceof VoiceChannel voiceChannel) {
                        var member = Bot.client.getMemberById(Snowflake.of(serverData.server), Snowflake.of(session.owner)).block();
                        if(serverData.logger.enabled) {
                            serverData.logger.onVoiceChannelDelete(null, member, voiceChannel);
                        }
                        voiceChannel.delete("Refresh sessions data on bot reconnection.").block();
                    }
                });
                serverData.autoChannelsManager.sessions = new ArrayList<>();
                serverData.save();
            });
        }
        catch (Exception e) {
            String msg = "Failed to refresh sessions data due to an exception:\n" + e;
            Bot.LOGGER.error(msg);
            KitsunDebug.report(msg, e, true);
        }
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
                    if(serverData.logger.enabled) {
                        serverData.logger.onMessageUpdate(oldMessage.get(), newMessage.get());
                    }
                });
            }
        }
        catch (Exception e) {
            String msg = "Failed to proceed message update event due to an exception:\n" + e;
            Bot.LOGGER.error(msg);
            KitsunDebug.report(msg, e, false);
        }
    }

    public static void onMessageDelete(MessageDeleteEvent event) {
        try {
            var guildId = event.getGuildId();
            var message = event.getMessage();

            if(guildId.isPresent() && message.isPresent()) {
                if (message.get().getAuthor().isPresent() && !message.get().getAuthor().get().isBot()) {
                    ServerUtils.runFor(guildId.get(), serverData -> {
                        if(serverData.logger.enabled) {
                            serverData.logger.onMessageDelete(message.get());
                        }
                    });
                }
            }
        }
        catch (Exception e) {
            String msg = "Failed to proceed message delete event due to an exception:\n" + e;
            Bot.LOGGER.error(msg);
            KitsunDebug.report(msg, e, false);
        }
    }

    public static void onMemberJoin(MemberJoinEvent event) {
        try {
            ServerUtils.runFor(event.getGuildId(), (serverData) -> {
                if(serverData.logger.enabled) {
                    serverData.logger.onMemberJoin(event.getMember());
                }
                if(!serverData.config.general.memberRoleId.isBlank()) {
                    event.getMember().addRole(Snowflake.of(serverData.config.general.memberRoleId)).block();
                }
            });
        }
        catch (Exception e) {
            String msg = "Failed to proceed member join event due to an exception:\n" + e;
            Bot.LOGGER.error(msg);
            KitsunDebug.report(msg, e, false);
        }
    }

    public static void onMemberLeave(MemberLeaveEvent event) {
        try {
            event.getMember().ifPresent(member -> ServerUtils.runFor(event.getGuildId(), serverData -> {
                if(serverData.logger.enabled) {
                    serverData.logger.onMemberLeave(member);
                }
            }));
        }
        catch (Exception e) {
            String msg = "Failed to proceed member leave event due to an exception:\n" + e;
            Bot.LOGGER.error(msg);
            KitsunDebug.report(msg, e, false);
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
            old.ifPresent(oldChannel -> ServerUtils.forEach(serverData -> {
                if (serverData.logger.enabled) {
                    if (!oldChannel.getName().equals(current.getName())) {
                        serverData.logger.onVoiceChannelNameUpdate(oldChannel, current);
                    }
                }
            }));
        }
        catch (Exception e) {
            String msg = "Failed to proceed voice channel update event due to an exception:\n" + e;
            Bot.LOGGER.error(msg);
            KitsunDebug.report(msg, e, false);
        }
    }

    public static void onVoiceChannelDelete(VoiceChannelDeleteEvent event) {
        try {
            ServerUtils.forEach(serverData -> serverData.autoChannelsManager.getSession(event.getChannel().getId().asString()).ifPresent(session -> {
                serverData.autoChannelsManager.refresh();
                var member = Bot.client.getMemberById(Snowflake.of(serverData.server), Snowflake.of(session.owner)).block();
                if(serverData.logger.enabled) {
                    serverData.logger.onVoiceChannelDelete(null, member, event.getChannel());
                }
            }));
        }
        catch (Exception e) {
            String msg = "Failed to proceed voice channel delete event due to an exception:\n" + e;
            Bot.LOGGER.error(msg);
            KitsunDebug.report(msg, e, false);
        }
    }

    public static void onVoiceStateUpdate(VoiceStateUpdateEvent event) {
        if(event.isJoinEvent()) {
            DiscordEvents.onVoiceChannelJoin(event);
        }
        if (event.isLeaveEvent()) {
            DiscordEvents.onVoiceChannelLeave(event);
        }
        if (event.isMoveEvent()) {
            DiscordEvents.onVoiceChannelMove(event);
        }
    }

    public static void onVoiceChannelJoin(VoiceStateUpdateEvent event) {
        try {
            ServerUtils.forEach(serverData -> {
                if(serverData.autoChannelsManager.enabled) {
                    VoiceChannel channel = event.getCurrent().getChannel().block();
                    Member member = event.getCurrent().getMember().block();

                    if(member != null && channel != null) {
                        serverData.autoChannelsManager.onVoiceChannelJoin(channel, member);
                    }
                }
            });
        }
        catch (Exception e) {
            String msg = "Failed to proceed voice channel join event due to an exception:\n" + e;
            Bot.LOGGER.error(msg);
            KitsunDebug.report(msg, e, false);
        }
    }

    public static void onVoiceChannelLeave(VoiceStateUpdateEvent event) {
        try {
            ServerUtils.forEach(serverData -> {
                if(event.getOld().isPresent()) {
                    VoiceChannel channel = event.getOld().get().getChannel().block();
                    Member leaver = event.getOld().get().getMember().block();

                    if(channel != null && leaver != null) {
                        serverData.autoChannelsManager.onVoiceChannelLeave(channel, leaver);
                    }
                }
            });
        }
        catch (Exception e) {
            String msg = "Failed to proceed voice channel leave event due to an exception:\n" + e;
            Bot.LOGGER.error(msg);
            KitsunDebug.report(msg, e, false);
        }
    }

    public static void onVoiceChannelMove(VoiceStateUpdateEvent event) {
        try {
            ServerUtils.forEach(serverData -> {
                if(serverData.autoChannelsManager.enabled) {
                    VoiceChannel newChannel = event.getCurrent().getChannel().block();
                    Member member = event.getCurrent().getMember().block();

                    if(member != null && newChannel != null) {
                        serverData.autoChannelsManager.onVoiceChannelJoin(newChannel, member);
                    }
                }

                if(event.getOld().isPresent()) {
                    VoiceChannel oldChannel = event.getOld().get().getChannel().block();
                    Member leaver = event.getOld().get().getMember().block();

                    if(oldChannel != null && leaver != null) {
                        serverData.autoChannelsManager.onVoiceChannelLeave(oldChannel, leaver);
                    }
                }
            });
        }
        catch (Exception e) {
            String msg = "Failed to proceed voice channel move event due to an exception:\n" + e;
            Bot.LOGGER.error(msg);
            KitsunDebug.report(msg, e, false);
        }
    }
}
