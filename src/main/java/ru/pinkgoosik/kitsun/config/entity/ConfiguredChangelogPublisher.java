package ru.pinkgoosik.kitsun.config.entity;

public class ConfiguredChangelogPublisher {
    public String mod;
    public String channel;

    public ConfiguredChangelogPublisher(String mod, String channel) {
        this.mod = mod;
        this.channel = channel;
    }
}
