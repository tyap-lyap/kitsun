package ru.pinkgoosik.somikbot.event;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ConnectEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.RestClient;
import ru.pinkgoosik.somikbot.command.Command;
import ru.pinkgoosik.somikbot.command.Commands;
import ru.pinkgoosik.somikbot.feature.ChangelogPublisher;

public class DiscordEvents {

    public static void initEvents(GatewayDiscordClient gateway){
        gateway.on(ConnectEvent.class).subscribe(DiscordEvents::onConnect);
        gateway.on(MessageCreateEvent.class).subscribe(DiscordEvents::onMessageCreate);
    }

    private static void onConnect(ConnectEvent event){
        RestClient client = event.getClient().getRestClient();
        new ChangelogPublisher(client, "artifality");
        new ChangelogPublisher(client, "visuality");
    }

    private static void onMessageCreate(MessageCreateEvent event){
        Message message = event.getMessage();
        MessageChannel channel = message.getChannel().block();
        String content = message.getContent();

        if(!(message.getAuthor().isPresent() && message.getAuthor().get().isBot())){
            for(Command command : Commands.COMMANDS){
                String name = "!" + command.getName();
                if (content.startsWith(name)){
                    content = content.replace(name, "");
                    content = content + " empty empty empty";
                    String[] args = content.split(" ");
                    assert channel != null;
                    command.respond(event, args);
                }
            }
        }
    }
}
