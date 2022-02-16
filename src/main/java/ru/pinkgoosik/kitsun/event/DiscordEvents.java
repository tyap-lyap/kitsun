package ru.pinkgoosik.kitsun.event;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import discord4j.core.event.domain.lifecycle.ConnectEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.MessageDeleteEvent;
import discord4j.core.event.domain.message.MessageUpdateEvent;
import discord4j.core.event.domain.role.RoleCreateEvent;
import discord4j.core.event.domain.role.RoleDeleteEvent;
import discord4j.core.event.domain.role.RoleUpdateEvent;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.command.Commands;
import ru.pinkgoosik.kitsun.schedule.Scheduler;
import ru.pinkgoosik.kitsun.util.ServerUtils;

public class DiscordEvents {

    public static void onConnect(ConnectEvent event) {
        Bot.client = event.getClient().getRestClient();
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
                    if(serverData.logger.enabled) {
                        serverData.logger.onMessageUpdate(oldMessage.get(), newMessage.get());
                    }
                });
            }
        }catch (Exception e) {
            Bot.LOGGER.error("Failed to proceed message update event due to an exception:\n" + e);
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
        }catch (Exception e) {
            Bot.LOGGER.error("Failed to proceed message delete event due to an exception:\n" + e);
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
        }catch (Exception e) {
            Bot.LOGGER.error("Failed to proceed member join event due to an exception:\n" + e);
        }
    }

    public static void onMemberLeave(MemberLeaveEvent event) {
        try {
            event.getMember().ifPresent(member -> ServerUtils.runFor(event.getGuildId(), serverData -> {
                if(serverData.logger.enabled) {
                    serverData.logger.onMemberLeave(member);
                }
            }));

        }catch (Exception e) {
            Bot.LOGGER.error("Failed to proceed member leave event due to an exception:\n" + e);
        }
    }

    public static void onRoleCreate(RoleCreateEvent event) {
    }

    public static void onRoleDelete(RoleDeleteEvent event) {
    }

    public static void onRoleUpdate(RoleUpdateEvent event) {
    }
}
