package ru.pinkgoosik.kitsun.command;

import discord4j.core.object.entity.Member;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.kitsun.instance.config.Config;
import ru.pinkgoosik.kitsun.permission.AccessManager;

public final class CommandUseContext {
    Member member;
    RestChannel channel;
    String[] args;
    Config config;
    AccessManager accessManager;

    public CommandUseContext(Member member, RestChannel channel, String[] args, Config config, AccessManager accessManager) {
        this.member = member;
        this.channel = channel;
        this.args = args;
        this.config = config;
        this.accessManager = accessManager;
    }

    public Member getMember() {
        return member;
    }

    public RestChannel getChannel() {
        return channel;
    }

    public Config getConfig() {
        return config;
    }

    public AccessManager getAccessManager() {
        return accessManager;
    }

    public String getFirstArgument() {
        return args[1];
    }

    public String getSecondArgument() {
        return args[2];
    }

    public String getThirdArgument() {
        return args[3];
    }
}
