package ru.pinkgoosik.kitsun.instance.config;

import ru.pinkgoosik.kitsun.instance.config.entity.ModChangelogPublisherConfig;

import java.util.ArrayList;
import java.util.List;

public class GeneralConfig {
    public boolean cosmeticaEnabled;
    public String memberRoleId;
    public String commandPrefix;
    public List<String> allowedCommandChannels;
    public List<ModChangelogPublisherConfig> publishers;

    public static final GeneralConfig EMPTY = new GeneralConfig(
            true,
            "",
            "!",
            new ArrayList<>(),
            new ArrayList<>());

    public GeneralConfig(
            boolean cosmeticaEnabled,
            String memberRoleId,
            String commandPrefix,
            List<String> allowedCommandChannels,
            List<ModChangelogPublisherConfig> publishers
    ) {
        this.cosmeticaEnabled = cosmeticaEnabled;
        this.memberRoleId = memberRoleId;
        this.commandPrefix = commandPrefix;
        this.allowedCommandChannels = allowedCommandChannels;
        this.publishers = publishers;
    }
}
