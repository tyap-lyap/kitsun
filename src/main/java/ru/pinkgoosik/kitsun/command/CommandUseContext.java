package ru.pinkgoosik.kitsun.command;

import discord4j.core.object.entity.Member;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.kitsun.instance.AccessManager;
import ru.pinkgoosik.kitsun.instance.ServerData;
import ru.pinkgoosik.kitsun.instance.config.ServerConfig;

import java.util.ArrayList;

public final class CommandUseContext {
    public Member member;
    public RestChannel channel;
    public ArrayList<String> args;
    public ServerData serverData;
    public ServerConfig config;
    public AccessManager accessManager;
    public String memberId;

    public CommandUseContext(Member member, RestChannel channel, ArrayList<String> args, ServerData serverData) {
        this.member = member;
        this.channel = channel;
        this.args = args;
        this.serverData = serverData;
        this.config = serverData.config;
        this.accessManager = serverData.accessManager;
        this.memberId = member.getId().asString();
    }
}
