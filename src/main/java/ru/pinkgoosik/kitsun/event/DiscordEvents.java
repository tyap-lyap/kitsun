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
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.command.Commands;
import ru.pinkgoosik.kitsun.config.Config;
import ru.pinkgoosik.kitsun.feature.*;

import java.util.Optional;

public class DiscordEvents {

    public static void onConnect(ConnectEvent event){
        Bot.client = event.getClient().getRestClient();
        Config.general.publishers.forEach(publisher -> Bot.publishers.add(new ModChangelogPublisher(publisher.mod, publisher.channel)));
        Bot.mcUpdatesPublisher = new MCUpdatesPublisher();
        DiscordLogger.INSTANCE = new DiscordLogger();
        Scheduler.start();
    }

    public static void onMessageCreate(MessageCreateEvent event){
        BadWordsFilter.onMessageCreate(event);
        Commands.onMessageCreate(event);
    }

    public static void onMessageUpdate(MessageUpdateEvent event){
        if(event.getGuildId().isPresent()){
            Message newMessage = event.getMessage().block();
            Optional<Message> oldMessage = event.getOld();
            if(oldMessage.isPresent() && newMessage != null){
                DiscordLogger.INSTANCE.messageUpdated(oldMessage.get(), newMessage);
            }
        }

    }

    public static void onMessageDelete(MessageDeleteEvent event){
        if(event.getGuildId().isPresent()){
            Optional<Message> optional = event.getMessage();
            optional.ifPresent(message -> {
                if (message.getAuthor().isPresent() && !message.getAuthor().get().isBot()){
                    DiscordLogger.INSTANCE.messageDeleted(message);
                }
            });
        }
    }

    public static void onMemberJoin(MemberJoinEvent event){
        DiscordLogger.INSTANCE.memberJoin(event.getMember());
        if(!Config.general.memberRoleId.isBlank()){
            event.getMember().addRole(Snowflake.of(Config.general.memberRoleId)).block();
        }
    }

    public static void onMemberLeave(MemberLeaveEvent event){
        Optional<Member> optional = event.getMember();
        optional.ifPresent(member -> DiscordLogger.INSTANCE.memberLeave(member));
    }

    public static void onRoleCreate(RoleCreateEvent event){
        Role role = event.getRole();
        DiscordLogger.INSTANCE.roleCreated(role);

    }

    public static void onRoleDelete(RoleDeleteEvent event){
        Optional<Role> optional = event.getRole();
        optional.ifPresent(role -> DiscordLogger.INSTANCE.roleDeleted(role));
    }

    public static void onRoleUpdate(RoleUpdateEvent event){
        Optional<Role> optionalOld = event.getOld();
        Role role = event.getCurrent();
    }
}
