package ru.pinkgoosik.somikbot.command;

import ru.pinkgoosik.somikbot.util.UuidGetter;

public class UuidCommand extends Command {

    @Override
    public String getName() {
        return "uuid";
    }

    @Override
    public String getDescription() {
        return "Sends UUID of the Player.";
    }

    @Override
    public String appendDescription() {
        return "`!" + this.getName() + " <self|player>` " + " - " + this.getDescription();
    }

    @Override
    public String respond(String[] args, String nickname) {
        if(args[1].equals("self")) args[1] = nickname;
        if(UuidGetter.getUuid(args[1]) == null) return "Player not found";
        return UuidGetter.getUuid(args[1]);
    }
}
