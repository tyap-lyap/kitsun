package ru.pinkgoosik.somikbot.command;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.somikbot.command.everyone.CloakGrantCommand;
import ru.pinkgoosik.somikbot.command.everyone.CloakRevokeCommand;
import ru.pinkgoosik.somikbot.command.everyone.CloaksCommand;
import ru.pinkgoosik.somikbot.command.everyone.HelpCommand;
import ru.pinkgoosik.somikbot.command.moderation.ConfigListCommand;
import ru.pinkgoosik.somikbot.command.moderation.PermissionGrantCommand;
import ru.pinkgoosik.somikbot.command.moderation.PermissionsCommand;
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
        add(new PermissionsCommand());
        add(new PermissionGrantCommand());
        add(new ConfigListCommand());
    }

    private static void add(Command command){
        COMMANDS.add(command);
    }

    public static void onMessageCreate(MessageCreateEvent event){
        if(event.getGuildId().isPresent()){
            Member member;
            Message message = event.getMessage();
            MessageChannel channel = message.getChannel().block();
            String content = message.getContent();
            RestChannel restChannel;
            if(channel == null) return;
            else restChannel = event.getClient().getRestClient().getChannelById(channel.getId());
            if (event.getMember().isPresent()) member = event.getMember().get();
            else return;

            if(!(message.getAuthor().isPresent() && message.getAuthor().get().isBot())){
                for(Command command : Commands.COMMANDS){
                    String name = "!" + command.getName();
                    if (content.startsWith(name)){
                        content = content.replace(name, "");
                        content = content + " empty empty empty";
                        String[] args = content.split(" ");
                        CommandUseContext context = new CommandUseContext(member, restChannel, args);
                        command.respond(context);
                    }
                }
            }
        }
    }
}
