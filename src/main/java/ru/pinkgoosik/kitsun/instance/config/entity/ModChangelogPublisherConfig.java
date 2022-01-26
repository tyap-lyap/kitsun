package ru.pinkgoosik.kitsun.instance.config.entity;

public class ModChangelogPublisherConfig {
    public String mod;
    public String channel;

    public ModChangelogPublisherConfig(String mod, String channel) {
        this.mod = mod;
        this.channel = channel;
    }
}
