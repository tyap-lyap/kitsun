package ru.pinkgoosik.somikbot.command;

import java.util.ArrayList;

public class Commands {

    private static final ArrayList<Command> COMMANDS = new ArrayList<>();

    public static void initCommands(){
        add(new HelpCommand());
        add(new CapesCommand());
        add(new CapeCommand());
        add(new UuidCommand());
    }

    private static void add(Command command){
        COMMANDS.add(command);
    }

    public static ArrayList<Command> getCommands() {
        return COMMANDS;
    }
}
