package ru.pinkgoosik.somikbot.command.everyone;

import discord4j.core.object.entity.Member;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.somikbot.command.Command;
import ru.pinkgoosik.somikbot.command.CommandUseContext;
import ru.pinkgoosik.somikbot.command.Commands;
import ru.pinkgoosik.somikbot.permissons.AccessManager;
import ru.pinkgoosik.somikbot.permissons.Permissions;

public class HelpCommand extends Command {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Sends list of available commands.";
    }

    @Override
    public String appendName() {
        return "**!" + this.getName() + "** <page>";
    }

    @Override
    public void respond(CommandUseContext context) {
        Member member = context.getMember();
        RestChannel channel = context.getChannel();
        String page = context.getFirstArgument();

        if (!AccessManager.hasAccessTo(member, Permissions.HELP)){
            channel.createMessage(createErrorEmbed("Not enough permissions.")).block();
            return;
        }
        channel.createMessage(createInfoEmbed("Available Commands", getTextForPage(page))).block();
    }

    private String getTextForPage(String arg){
        int page = stringToInt(arg);
        int size = Commands.COMMANDS.size();
        int first = ((3 * page) - 3) + (page - 1);
        int last = 3 * page + (page - 1);
        StringBuilder text = new StringBuilder();

        for (int i = first; i <= last; i++){
            if(size >= i + 1){
                text.append(Commands.COMMANDS.get(i).appendName()).append("\n");
                text.append(Commands.COMMANDS.get(i).getDescription()).append("\n \n");
            }
        }
        return text.toString();
    }

    private int stringToInt(String arg){
        int page = 1;
        if (arg.equals("2") || arg.equals("two") || arg.equals("second")) page = 2;
        return page;
    }
}
