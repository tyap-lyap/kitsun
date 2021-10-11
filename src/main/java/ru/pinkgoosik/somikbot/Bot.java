package ru.pinkgoosik.somikbot;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ConnectEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.RestClient;
import reactor.util.Logger;
import reactor.util.Loggers;
import ru.pinkgoosik.somikbot.command.Command;
import ru.pinkgoosik.somikbot.command.Commands;
import ru.pinkgoosik.somikbot.config.Config;
import ru.pinkgoosik.somikbot.cosmetica.PlayerCapes;
import ru.pinkgoosik.somikbot.feature.ChangelogPublisher;
import ru.pinkgoosik.somikbot.feature.FtpConnection;

public class Bot {

    public static final Logger LOGGER = Loggers.getLogger("Bot");

    public static void main(String[] args) {
        Commands.initCommands();
        Config.initConfig();
        PlayerCapes.fillFromUpstream();
        FtpConnection.connect();

        String token = Config.secrets.discordBotToken;
        DiscordClient client = DiscordClient.create(token);
        GatewayDiscordClient gateway = client.login().block();

        assert gateway != null;
        gateway.on(ConnectEvent.class).subscribe(event -> {
            RestClient restClient = event.getClient().getRestClient();
            new ChangelogPublisher(restClient, "490812");
            new ChangelogPublisher(restClient, "521126");
        });
        gateway.on(MessageCreateEvent.class).subscribe(event -> {
            Message message = event.getMessage();
            MessageChannel channel = message.getChannel().block();
            String stringMsg = message.getContent() + " empty empty empty empty empty";
            String[] words = stringMsg.split(" ");

            if(!(message.getAuthor().isPresent() && message.getAuthor().get().isBot())){
                for(Command command : Commands.COMMANDS){
                    if(words[0].equals("!" + command.getName())){
                        assert channel != null;
                        channel.createMessage(command.respond(words, message.getAuthor().get().getUsername())).block();
                    }
                }
            }
        });
        gateway.onDisconnect().block();
    }
}
