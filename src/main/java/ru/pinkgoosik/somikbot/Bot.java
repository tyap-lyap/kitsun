package ru.pinkgoosik.somikbot;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import reactor.util.Logger;
import reactor.util.Loggers;
import ru.pinkgoosik.somikbot.command.Commands;
import ru.pinkgoosik.somikbot.config.Config;
import ru.pinkgoosik.somikbot.cosmetica.PlayerCapes;
import ru.pinkgoosik.somikbot.event.Events;
import ru.pinkgoosik.somikbot.feature.FtpConnection;

public class Bot {
    public static final Logger LOGGER = Loggers.getLogger("Bot");

    public static void main(String[] args) {
        Config.initConfig();
        FtpConnection.connect();
        Commands.initCommands();
        PlayerCapes.fillFromUpstream();

        String token = Config.secrets.discordBotToken;
        if(token.isBlank()){
            LOGGER.info("Token is blank");
            System.exit(0);
        }
        DiscordClient client = DiscordClient.create(token);
        GatewayDiscordClient gateway = client.login().block();
        if(gateway != null){
            Events.initEvents(gateway);
            gateway.onDisconnect().block();
        }
    }
}
