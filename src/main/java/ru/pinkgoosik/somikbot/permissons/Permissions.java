package ru.pinkgoosik.somikbot.permissons;

import java.util.ArrayList;

public class Permissions {
    public static final ArrayList<String> LIST = new ArrayList<>();

    public static final String HELP = add("command.help");
    public static final String CLOAKS = add("command.cloaks");
    public static final String CLOAK_GRANT = add("command.cloak.grant");
    public static final String CLOAK_REVOKE_SELF = add("command.cloak.revoke.self");
    public static final String CLOAK_REVOKE_OTHER = add("command.cloak.revoke.other");
    public static final String PERMISSIONS = add("command.permissions");
    public static final String PERMISSION_GRANT = add("command.permission.grant");
    public static final String CONFIG_LIST = add("command.config.list");

    private static String add(String permission){
        LIST.add(permission);
        return permission;
    }
}
