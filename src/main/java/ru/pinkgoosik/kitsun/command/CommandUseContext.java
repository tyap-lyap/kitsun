package ru.pinkgoosik.kitsun.command;

import discord4j.core.object.entity.Member;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.kitsun.instance.ServerData;

public final class CommandUseContext {
    Member member;
    RestChannel channel;
    String[] args;
    ServerData serverData;

    public CommandUseContext(Member member, RestChannel channel, String[] args, ServerData serverData) {
        this.member = member;
        this.channel = channel;
        this.args = args;
        this.serverData = serverData;
    }

    public Member getMember() {
        return member;
    }

    public RestChannel getChannel() {
        return channel;
    }

    public ServerData getServerData() {
        return serverData;
    }

    public String getFirstArg() {
        return args[1];
    }

    public String getSecondArg() {
        return args[2];
    }

    public String getThirdArg() {
        return args[3];
    }
}
