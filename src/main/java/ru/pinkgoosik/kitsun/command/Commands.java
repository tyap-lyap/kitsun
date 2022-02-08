package ru.pinkgoosik.kitsun.command;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.kitsun.command.cloak.*;
import ru.pinkgoosik.kitsun.command.everyone.*;
import ru.pinkgoosik.kitsun.command.moderation.ChangelogPublisherAddCommand;
import ru.pinkgoosik.kitsun.command.moderation.ChangelogPublisherRemoveCommand;
import ru.pinkgoosik.kitsun.command.moderation.PermissionGrantCommand;
import ru.pinkgoosik.kitsun.command.moderation.PermissionsCommand;
import ru.pinkgoosik.kitsun.instance.ServerData;
import ru.pinkgoosik.kitsun.instance.ServerDataManager;

import java.util.ArrayList;
import java.util.List;

public class Commands {
    public static final List<Command> COMMANDS = new ArrayList<>();

    public static void init() {
        add(new HelpCommand());
        add(new RegisterCommand());

        add(new AvailableCloaksCommand());

        add(new CloakSetCommand());
        add(new CloakInformationCommand());
        add(new CloakRevokeCommand());

        add(new UnregisterCommand());

//        add(new AvailableAttributesCommand());
//        add(new AvailableCosmeticsCommand());
//        add(new RedeemCommand());

        add(new ChangelogPublisherAddCommand());
        add(new ChangelogPublisherRemoveCommand());

        add(new PermissionsCommand());
        add(new PermissionGrantCommand());
//        add(new ConfigListCommand());
    }

    private static void add(Command command) {
        COMMANDS.add(command);
    }

    public static void onMessageCreate(MessageCreateEvent event) {
        if(event.getGuildId().isEmpty()) {
            return;
        }

        Member member;
        Message message = event.getMessage();
        MessageChannel channel = message.getChannel().block();
        String content = message.getContent();
        RestChannel restChannel;
        String serverID = event.getGuildId().get().asString();
        ServerData serverData = ServerDataManager.getData(serverID);
        if(channel == null) return;
        else restChannel = event.getClient().getRestClient().getChannelById(channel.getId());
        if (event.getMember().isPresent()) member = event.getMember().get();
        else return;

        if(!(message.getAuthor().isPresent() && message.getAuthor().get().isBot())) {
            for(Command command : Commands.COMMANDS) {
                String commandPrefix = serverData.config.general.commandPrefix;
                String botPing = "<@!935826731925913630> ";

                if (iterateAltNames(content, command, commandPrefix)) {
                    String clean = getNameToClean(content, command, commandPrefix);
                    content = content.replace(clean, "");
                    content = content + " empty empty empty";
                    String[] args = content.split(" ");
                    CommandUseContext context = new CommandUseContext(member, restChannel, args, serverData);
                    command.respond(context);
                }
                else if(iterateAltNames(content, command, botPing)) {
                    String clean = getNameToClean(content, command, botPing);
                    content = content.replace(clean, "");
                    content = content + " empty empty empty";
                    String[] args = content.split(" ");
                    CommandUseContext context = new CommandUseContext(member, restChannel, args, serverData);
                    command.respond(context);
                }
            }
        }
    }

    public static boolean iterateAltNames(String content, Command command, String commandPrefix) {
        if (content.startsWith(commandPrefix + command.getName())) return true;
        for (String altName : command.getAltNames()) {
            String start = commandPrefix + altName;
            if(content.startsWith(start)) return true;
        }
        return false;
    }

    public static String getNameToClean(String content, Command command, String commandPrefix) {
        if(content.startsWith(commandPrefix + command.getName())) return commandPrefix + command.getName();
        for (String altName : command.getAltNames()) {
            String start = commandPrefix + altName;
            if(content.startsWith(start)) return start;
        }
        return commandPrefix + command.getName();
    }
}
