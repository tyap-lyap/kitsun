package ru.pinkgoosik.somikbot.command.moderation;

import ru.pinkgoosik.somikbot.command.Command;
import ru.pinkgoosik.somikbot.config.Config;

public class ConfigListCommand extends Command {

    @Override
    public String getName() {
        return "config list";
    }

    @Override
    public String getDescription() {
        return "Sends list of available options.";
    }

    @Override
    public String appendName() {
        return "**" + Config.general.prefix + this.getName() + "**";
    }
}
