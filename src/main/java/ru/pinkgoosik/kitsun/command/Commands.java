package ru.pinkgoosik.kitsun.command;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.kitsun.command.everyone.*;
import ru.pinkgoosik.kitsun.command.moderation.ConfigListCommand;
import ru.pinkgoosik.kitsun.command.moderation.PermissionGrantCommand;
import ru.pinkgoosik.kitsun.command.moderation.PermissionsCommand;
import ru.pinkgoosik.kitsun.config.Config;

import java.util.ArrayList;
import java.util.List;

public class Commands {
    public static final List<Command> COMMANDS = new ArrayList<>();

    public static void initCommands(){
        add(new HelpCommand());
        if (Config.general.cloaksEnabled) {
            add(new RegisterCommand());

            add(new AvailableAttributesCommand());
            add(new AvailableCloaksCommand());
            add(new AvailableCosmeticsCommand());

            add(new CloakChangeCommand());
            add(new CloakGrantCommand());
            add(new CloakInformationCommand());
            add(new CloakRevokeCommand());

            add(new RedeemCommand());
        }
        add(new PermissionsCommand());
        add(new PermissionGrantCommand());
        add(new ConfigListCommand());
    }

    private static void add(Command command){
        COMMANDS.add(command);
    }

    public static void onMessageCreate(MessageCreateEvent event){
        if(event.getGuildId().isPresent()) {
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
                    String name = Config.general.prefix + command.getName();
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
