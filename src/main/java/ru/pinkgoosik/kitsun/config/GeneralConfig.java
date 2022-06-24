package ru.pinkgoosik.kitsun.config;

import java.util.ArrayList;

public class GeneralConfig {
    public String memberRoleId;
    public String commandPrefix;
    public ArrayList<String> allowedCommandChannels;

    public static final GeneralConfig DEFAULT = new GeneralConfig(
            "",
            "!",
            new ArrayList<>()
    );

    public GeneralConfig(
            String memberRoleId,
            String commandPrefix,
            ArrayList<String> allowedCommandChannels
    ) {
        this.memberRoleId = memberRoleId;
        this.commandPrefix = commandPrefix;
        this.allowedCommandChannels = allowedCommandChannels;
    }
}
