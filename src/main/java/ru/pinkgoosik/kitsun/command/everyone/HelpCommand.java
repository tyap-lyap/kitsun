package ru.pinkgoosik.kitsun.command.everyone;

import discord4j.core.object.entity.Member;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.command.Commands;
import ru.pinkgoosik.kitsun.instance.config.Config;
import ru.pinkgoosik.kitsun.permission.AccessManager;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;

public class HelpCommand extends Command {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String[] getAltNames() {
        return new String[]{"hel", "hep", "hlp", "commands", "comands", "commans", "comans", "commas",
                "comas", "held", "помощь", "помощ", "помочь", "команды", "комады"};
    }

    @Override
    public String getDescription() {
        return "Sends list of available commands.";
    }

    @Override
    public String appendName(Config config) {
        return super.appendName(config) + " <page>";
    }

    @Override
    public void respond(CommandUseContext context) {
        Member member = context.getMember();
        RestChannel channel = context.getChannel();
        String page = context.getFirstArgument();
        Config config = context.getConfig();
        AccessManager accessManager = context.getAccessManager();

        if (!accessManager.hasAccessTo(member, Permissions.HELP)) {
            channel.createMessage(Embeds.error("Not enough permissions.")).block();
            return;
        }
        channel.createMessage(Embeds.info("Available Commands", getTextForPage(page, config))).block();
    }

    private String getTextForPage(String arg, Config config) {
        int page = stringToInt(arg);
        int size = Commands.COMMANDS.size();
        int first = ((3 * page) - 3) + (page - 1);
        int last = 3 * page + (page - 1);
        StringBuilder text = new StringBuilder();

        for (int i = first; i <= last; i++) {
            if(size >= i + 1) {
                text.append(Commands.COMMANDS.get(i).appendName(config)).append("\n");
                text.append(Commands.COMMANDS.get(i).getDescription()).append("\n \n");
            }
        }
        return text.toString();
    }

    private int stringToInt(String arg) {
        int page = 1;
        if (arg.equals("2") || arg.equals("two") || arg.equals("second")) page = 2;
        if (arg.equals("3") || arg.equals("three") || arg.equals("third")) page = 3;
        if (arg.equals("4") || arg.equals("four") || arg.equals("fourth")) page = 4;
        if (arg.equals("5") || arg.equals("five") || arg.equals("fifth")) page = 5;
        if (arg.equals("6") || arg.equals("six") || arg.equals("sixth")) page = 6;
        if (arg.equals("7") || arg.equals("seven") || arg.equals("seventh")) page = 7;
        return page;
    }
}
