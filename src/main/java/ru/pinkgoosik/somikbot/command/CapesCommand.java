package ru.pinkgoosik.somikbot.command;

import ru.pinkgoosik.somikbot.cosmetica.PlayerCapes;

public class CapesCommand extends Command {

    @Override
    public String getName() {
        return "capes";
    }

    @Override
    public String getDescription() {
        return "Sends list of available capes.";
    }

    @Override
    public String respond(String[] args, String nickname) {
        StringBuilder string = new StringBuilder();
        string.append("Available Capes:\n");
        for (String cape : PlayerCapes.CAPES){
            string.append(cape).append(", ");
        }
        return string.deleteCharAt(string.length() - 1).deleteCharAt(string.length() - 1).append(".").toString();
    }
}
