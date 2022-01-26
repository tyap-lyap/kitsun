package ru.pinkgoosik.kitsun.command.moderation;

import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.config.Config;

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
