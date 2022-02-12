package ru.pinkgoosik.kitsun.instance.config;

import java.util.ArrayList;

public class GeneralConfig {
    public boolean cosmeticaEnabled;
    public String memberRoleId;
    public String commandPrefix;
    public ArrayList<String> allowedCommandChannels;

    public static final GeneralConfig DEFAULT = new GeneralConfig(
            true,
            "",
            "!",
            new ArrayList<>()
    );

    public GeneralConfig(
            boolean cosmeticaEnabled,
            String memberRoleId,
            String commandPrefix,
            ArrayList<String> allowedCommandChannels
    ) {
        this.cosmeticaEnabled = cosmeticaEnabled;
        this.memberRoleId = memberRoleId;
        this.commandPrefix = commandPrefix;
        this.allowedCommandChannels = allowedCommandChannels;
    }
}
