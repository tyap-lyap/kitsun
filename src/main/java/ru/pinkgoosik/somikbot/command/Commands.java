package ru.pinkgoosik.somikbot.command;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class Commands {

    private static final ArrayList<Command> COMMANDS = new ArrayList<>();

    public static final Command HELP = add(new HelpCommand());
    public static final Command CAPES = add(new CapesCommand());
    public static final Command CAPE = add(new CapeCommand());
    public static final Command UUID = add(new UuidCommand());

    public static Command add(Command command){
        COMMANDS.add(command);
        return command;
    }

    public static ArrayList<Command> getCommands() {
        return COMMANDS;
    }
}
