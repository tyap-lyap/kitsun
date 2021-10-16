package ru.pinkgoosik.somikbot.command;

import ru.pinkgoosik.somikbot.config.Config;
import java.util.ArrayList;

public class Commands {
    public static final ArrayList<Command> COMMANDS = new ArrayList<>();

    public static void initCommands(){
        add(new HelpCommand());
//        add(new UuidCommand());
        if(Config.general.cloaksEnabled){
            add(new CloaksCommand());
            add(new CloakGrantCommand());
            add(new CloakRevokeCommand());
        }
//        add(new ModrinthCommand());
    }

    private static void add(Command command){
        COMMANDS.add(command);
    }
}
