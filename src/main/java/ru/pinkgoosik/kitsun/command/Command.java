package ru.pinkgoosik.kitsun.command;

import ru.pinkgoosik.kitsun.instance.config.Config;

public abstract class Command {
    public abstract String getName();

    public String[] getAltNames() {
        return new String[]{};
    }

    public abstract String getDescription();

    public String appendName(Config config) {
        return "**" + config.general.commandPrefix + this.getName() + "**";
    }

    public void respond(CommandUseContext context) {}
}
