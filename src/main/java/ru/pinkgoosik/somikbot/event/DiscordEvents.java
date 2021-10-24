package ru.pinkgoosik.somikbot.event;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ConnectEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.rest.RestClient;
import ru.pinkgoosik.somikbot.command.Commands;
import ru.pinkgoosik.somikbot.feature.ChangelogPublisher;
import ru.pinkgoosik.somikbot.feature.MinecraftUpdates;
import ru.pinkgoosik.somikbot.util.BadWordsFilter;

public class DiscordEvents {

    public static void initEvents(GatewayDiscordClient gateway){
        gateway.on(ConnectEvent.class).subscribe(DiscordEvents::onConnect);
        gateway.on(MessageCreateEvent.class).subscribe(DiscordEvents::onMessageCreate);
    }

    private static void onConnect(ConnectEvent event){
        RestClient client = event.getClient().getRestClient();
        new ChangelogPublisher(client, "artifality");
        new ChangelogPublisher(client, "visuality");
        new MinecraftUpdates(client);
    }

    private static void onMessageCreate(MessageCreateEvent event){
        BadWordsFilter.onMessageCreate(event);
        Commands.onMessageCreate(event);
    }
}
