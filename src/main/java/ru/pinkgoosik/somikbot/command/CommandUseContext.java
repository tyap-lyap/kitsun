package ru.pinkgoosik.somikbot.command;

import discord4j.core.object.entity.Member;
import discord4j.rest.entity.RestChannel;

public class CommandUseContext {
    Member member;
    RestChannel channel;
    String[] args;

    public CommandUseContext(Member member, RestChannel channel, String[] args){
        this.member = member;
        this.channel = channel;
        this.args = args;
    }

    public Member getMember() {
        return member;
    }

    public RestChannel getChannel() {
        return channel;
    }

    public String getFirstArgument(){
        return args[1];
    }

    public String getSecondArgument(){
        return args[2];
    }

    public String getThirdArgument(){
        return args[3];
    }
}
