package ru.pinkgoosik.somikbot.permissons;

import java.util.ArrayList;
import java.util.List;

public class Permissions {
    public static final List<String> LIST = new ArrayList<>();

    public static final String HELP = add("command.help");
    public static final String REGISTER = add("command.register");
    public static final String AVAILABLE_CLOAKS = add("command.available.cloaks");
    public static final String AVAILABLE_COSMETICS = add("command.available.cosmetics");
    public static final String AVAILABLE_ATTRIBUTES = add("command.available.attributes");
    public static final String CLOAK_GRANT = add("command.cloak.grant");
    public static final String CLOAK_CHANGE = add("command.cloak.change");
    public static final String CLOAK_INFORMATION = add("command.information.cloak");
    public static final String COSMETICS_INFORMATION = add("command.information.cosmetics");
    public static final String ATTRIBUTES_INFORMATION = add("command.information.attributes");
    public static final String INFORMATION = add("command.information");
    public static final String REDEEM = add("command.redeem");
    public static final String CLOAK_REVOKE_SELF = add("command.cloak.revoke.self");
    public static final String CLOAK_REVOKE_OTHER = add("command.cloak.revoke.other");
    public static final String PERMISSIONS = add("command.permissions");
    public static final String PERMISSION_GRANT = add("command.permission.grant");
    public static final String CONFIG_LIST = add("command.config.list");

    private static String add(String permission) {
        LIST.add(permission);
        return permission;
    }
}
