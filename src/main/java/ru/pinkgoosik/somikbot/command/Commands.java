package ru.pinkgoosik.somikbot.command;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import ru.pinkgoosik.somikbot.config.Config;
import java.util.ArrayList;

public class Commands {
    public static final ArrayList<Command> COMMANDS = new ArrayList<>();

    public static void initCommands(){
        add(new HelpCommand());
        if(Config.general.cloaksEnabled){
            add(new CloaksCommand());
            add(new CloakGrantCommand());
            add(new CloakRevokeCommand());
        }
    }

    private static void add(Command command){
        COMMANDS.add(command);
    }

    public static void onMessageCreate(MessageCreateEvent event){
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
