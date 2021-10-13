package ru.pinkgoosik.somikbot.command;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;

public abstract class Command {

    public abstract String getName();

    public abstract String getDescription();

    public String appendName(){
        return "**!" + this.getName() + "**";
    }

    public void respond(MessageCreateEvent event, User user, MessageChannel channel){}
}
