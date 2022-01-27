package ru.pinkgoosik.kitsun.instance.config.entity;

public class ChangelogPublisherConfig {
    public String mod;
    public String channel;

    public ChangelogPublisherConfig(String mod, String channel) {
        this.mod = mod;
        this.channel = channel;
    }
}
