package ru.pinkgoosik.kitsun.event;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.channel.VoiceChannelDeleteEvent;
import discord4j.core.event.domain.channel.VoiceChannelUpdateEvent;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.lifecycle.ConnectEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.MessageDeleteEvent;
import discord4j.core.event.domain.message.MessageUpdateEvent;
import discord4j.core.event.domain.role.RoleCreateEvent;
import discord4j.core.event.domain.role.RoleDeleteEvent;
import discord4j.core.event.domain.role.RoleUpdateEvent;
import discord4j.core.object.entity.channel.VoiceChannel;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.command.CommandHelper;
import ru.pinkgoosik.kitsun.command.CommandNext;
import ru.pinkgoosik.kitsun.command.Commands;
import ru.pinkgoosik.kitsun.feature.KitsunDebugger;
import ru.pinkgoosik.kitsun.schedule.Scheduler;
import ru.pinkgoosik.kitsun.util.ServerUtils;

import java.util.Optional;

public class DiscordEvents {

    public static void onConnect(ConnectEvent event) {
        Bot.rest = event.getClient().getRestClient();
        Bot.client = event.getClient();
        String note = Bot.secrets.get().note;
        KitsunDebugger.info(note.isEmpty() ? "Kitsun is now running!" : note);
        Scheduler.start();
        Commands.initNext();
        Commands.COMMANDS_NEXT.forEach(CommandNext::build);
    }

    public static void onCommandUse(ChatInputInteractionEvent event) {
        try {
            Commands.COMMANDS_NEXT.forEach(commandNext -> {
                if(event.getCommandName().equals(commandNext.getName())) {
                    commandNext.respond(event, new CommandHelper(event));
                }
            });
        }
        catch (Exception e) {
            KitsunDebugger.report("Failed to proceed chat input interaction event due to an exception:\n" + e);
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
                ServerUtils.runFor(guildId.get(), data -> data.logger.get().ifEnabled(log -> log.onMessageUpdate(oldMessage.get(), newMessage.get())));
            }
        }
        catch (Exception e) {
            KitsunDebugger.report("Failed to proceed message update event due to an exception:\n" + e);
        }
    }

    public static void onMessageDelete(MessageDeleteEvent event) {
        try {
            var guildId = event.getGuildId();
            var message = event.getMessage();

            if(guildId.isPresent() && message.isPresent()) {
                if (message.get().getAuthor().isPresent() && !message.get().getAuthor().get().isBot()) {
                    ServerUtils.runFor(guildId.get(), data -> data.logger.get().ifEnabled(log -> log.onMessageDelete(message.get())));
                }
            }
        }
        catch (Exception e) {
            KitsunDebugger.report("Failed to proceed message delete event due to an exception:\n" + e);
        }
    }

    public static void onMemberJoin(MemberJoinEvent event) {
        try {
            ServerUtils.runFor(event.getGuildId(), data -> {
                data.logger.get().ifEnabled(log -> log.onMemberJoin(event.getMember()));
                if(!data.config.get().general.memberRoleId.isBlank()) {
                    event.getMember().addRole(Snowflake.of(data.config.get().general.memberRoleId)).block();
                }
            });
        }
        catch (Exception e) {
            KitsunDebugger.report("Failed to proceed member join event due to an exception:\n" + e);
        }
    }

    public static void onMemberLeave(MemberLeaveEvent event) {
        try {
            event.getMember().ifPresent(member -> ServerUtils.runFor(event.getGuildId(), data -> data.logger.get().ifEnabled(log -> log.onMemberLeave(member))));
        }
        catch (Exception e) {
            KitsunDebugger.report("Failed to proceed member leave event due to an exception:\n" + e);
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
            old.ifPresent(oldChannel -> ServerUtils.runFor(current.getGuildId(), data -> {
                if (!oldChannel.getName().equals(current.getName())) {
                    data.logger.get().ifEnabled(log -> log.onVoiceChannelNameUpdate(oldChannel, current));
                }
            }));
        }
        catch (Exception e) {
            KitsunDebugger.report("Failed to proceed voice channel update event due to an exception:\n" + e);
        }
    }

    public static void onVoiceChannelDelete(VoiceChannelDeleteEvent event) {
        try {
            VoiceChannel channel = event.getChannel();
            String channelId = channel.getId().asString();

            ServerUtils.forEach(data -> data.autoChannels.modify(manager -> {
                manager.getSession(channelId).ifPresent(session -> {
                    var member = Bot.client.getMemberById(Snowflake.of(data.server), Snowflake.of(session.owner)).block();
                    data.logger.get().ifEnabled(log -> log.onVoiceChannelDelete(session, member, channel));
                    session.shouldBeRemoved = true;
                });

                if(manager.enabled) {
                    if(manager.parentChannel.equals(channelId)) {
                        manager.disable();
                    }
                }
                manager.refresh();
            }));
        }
        catch (Exception e) {
            KitsunDebugger.report("Failed to proceed voice channel delete event due to an exception:\n" + e);
        }
    }

    public static void onVoiceStateUpdate(VoiceStateUpdateEvent event) {

    }
}
