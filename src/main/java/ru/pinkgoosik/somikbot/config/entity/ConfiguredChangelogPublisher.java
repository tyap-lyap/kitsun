package ru.pinkgoosik.somikbot.config.entity;

import ru.pinkgoosik.somikbot.feature.ModChangelogPublisher;

public class ConfiguredChangelogPublisher {
    public String mod;
    public String channel;

    public ConfiguredChangelogPublisher(String mod, String channel){
        this.mod = mod;
        this.channel = channel;
    }

    public void start(){
        new ModChangelogPublisher(mod, channel).startScheduler();
    }
}
