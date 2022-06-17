package ru.pinkgoosik.kitsun.permission;

import java.util.ArrayList;
import java.util.List;

public class Permissions {
    public static final List<String> LIST = new ArrayList<>();

    public static final String HELP = add("command.help");
    public static final String REGISTER = add("command.register");
    public static final String UNREGISTER = add("command.unregister");

    public static final String AVAILABLE_CLOAKS = add("command.available.cloaks");
    public static final String AVAILABLE_COSMETICS = add("command.available.cosmetics");
    public static final String AVAILABLE_ATTRIBUTES = add("command.available.attributes");

    public static final String CLOAK_SET = add("command.cloak.change");
    public static final String CLOAK_REVOKE = add("command.cloak.revoke");
    public static final String CLOAK_INFO = add("command.info.cloak");

    public static final String COSMETICS_INFO = add("command.info.cosmetics");
    public static final String ATTRIBUTES_INFO = add("command.info.attributes");
    public static final String INFO = add("command.info");
    public static final String REDEEM = add("command.redeem");

    public static final String CHANGELOG_PUBLISHER_ADD = add("command.mod_updates_publisher.add");
    public static final String CHANGELOG_PUBLISHER_REMOVE = add("command.mod_updates_publisher.remove");

    public static final String MC_UPDATES_ENABLE = add("command.mc_updates.enable");
    public static final String MC_UPDATES_DISABLE = add("command.mc_updates.disable");

    public static final String LOGGER_ENABLE = add("command.logger.enable");
    public static final String LOGGER_DISABLE = add("command.logger.disable");

    public static final String PERMISSIONS = add("command.permissions");
    public static final String PERMISSION_GRANT = add("command.permission.grant");
    public static final String PERMISSION_REVOKE = add("command.permission.revoke");

    public static final String QUILT_UPDATES_ENABLE = add("command.quilt_updates.enable");
    public static final String QUILT_UPDATES_DISABLE = add("command.quilt_updates.disable");

    public static final String KITSUN_CMD_PREFIX = add("command.kitsun_cmd_prefix");

    public static final String AUTO_CHANNELS_ENABLE = add("command.auto_channels.enable");
    public static final String AUTO_CHANNELS_DISABLE = add("command.auto_channels.disable");


    private static String add(String permission) {
        LIST.add(permission);
        return permission;
    }

}
