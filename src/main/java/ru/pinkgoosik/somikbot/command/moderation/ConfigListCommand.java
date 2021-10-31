package ru.pinkgoosik.somikbot.command.moderation;

import ru.pinkgoosik.somikbot.command.Command;

public class ConfigListCommand extends Command {

    @Override
    public String getName() {
        return "config list";
    }

    @Override
    public String getDescription() {
        return "Sends list of available options.";
    }
}
