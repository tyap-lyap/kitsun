package ru.pinkgoosik.kitsun.command;

import ru.pinkgoosik.kitsun.instance.config.Config;

public abstract class Command {
    public abstract String getName();

    public String[] getAltNames() {
        return new String[]{""};
    }

    public abstract String getDescription();

    public String appendName(Config config) {
        String name = "**" + config.general.commandPrefix + this.getName() + "**";

        if(!getAltNames()[0].isBlank()) {
            name = name + " or **" + config.general.commandPrefix + getAltNames()[0] + "**";
        }
        return name;
    }

    public void respond(CommandUseContext context) {}
}
