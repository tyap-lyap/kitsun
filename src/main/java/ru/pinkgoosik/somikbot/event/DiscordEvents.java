package ru.pinkgoosik.somikbot.event;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.lifecycle.ConnectEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.RestClient;
import ru.pinkgoosik.somikbot.command.Command;
import ru.pinkgoosik.somikbot.command.Commands;
import ru.pinkgoosik.somikbot.config.Config;
import ru.pinkgoosik.somikbot.feature.ChangelogPublisher;

public class DiscordEvents {

    public static void initEvents(GatewayDiscordClient gateway){
        gateway.on(ConnectEvent.class).subscribe(DiscordEvents::onConnect);
        gateway.on(MessageCreateEvent.class).subscribe(DiscordEvents::onMessageCreate);
        gateway.on(MemberJoinEvent.class).subscribe(DiscordEvents::onMemberJoin);
    }

    private static void onConnect(ConnectEvent event){
        RestClient client = event.getClient().getRestClient();
        new ChangelogPublisher(client, "artifality");
        new ChangelogPublisher(client, "visuality");
    }

    private static void onMemberJoin(MemberJoinEvent event){
        if(!Config.general.memberRoleId.isBlank()){
            event.getMember().addRole(Snowflake.of(Config.general.memberRoleId)).block();
        }
    }

    private static void onMessageCreate(MessageCreateEvent event){
        Message message = event.getMessage();
        MessageChannel channel = message.getChannel().block();
        String[] words = message.getContent().split(" ");

        if(message.getContent().equals("somik pray")){
            assert channel != null;
            channel.createMessage(":pray:").block();
        }

        if(!(message.getAuthor().isPresent() && message.getAuthor().get().isBot())){
            for(Command command : Commands.COMMANDS){
                if(words[0].equals("!" + command.getName())){
                    assert channel != null;
                    command.respond(event, message.getAuthor().get(), channel);
                }
            }
        }
    }
}
