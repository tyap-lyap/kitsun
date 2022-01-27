package ru.pinkgoosik.kitsun.instance.config;

import ru.pinkgoosik.kitsun.instance.config.entity.McUpdatesPublisherConfig;
import ru.pinkgoosik.kitsun.instance.config.entity.ChangelogPublisherConfig;

import java.util.ArrayList;

public class GeneralConfig {
    public boolean cosmeticaEnabled;
    public String memberRoleId;
    public String commandPrefix;
    public ArrayList<String> allowedCommandChannels;
    public ArrayList<ChangelogPublisherConfig> changelogPublishers;
    public McUpdatesPublisherConfig mcUpdatesPublisher;

    public static final GeneralConfig EMPTY = new GeneralConfig(
            true,
            "",
            "!",
            new ArrayList<>(),
            new ArrayList<>(),
            new McUpdatesPublisherConfig("")
    );

    public GeneralConfig(
            boolean cosmeticaEnabled,
            String memberRoleId,
            String commandPrefix,
            ArrayList<String> allowedCommandChannels,
            ArrayList<ChangelogPublisherConfig> changelogPublishers,
            McUpdatesPublisherConfig mcUpdatesPublisher
    ) {
        this.cosmeticaEnabled = cosmeticaEnabled;
        this.memberRoleId = memberRoleId;
        this.commandPrefix = commandPrefix;
        this.allowedCommandChannels = allowedCommandChannels;
        this.changelogPublishers = changelogPublishers;
        this.mcUpdatesPublisher = mcUpdatesPublisher;
    }
}
