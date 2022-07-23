package ru.pinkgoosik.kitsun.command;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;

public abstract class CommandNext {
    public abstract String getName();
    public abstract String getDescription();
    public abstract void build();
    public void respond(ChatInputInteractionEvent ctx, CommandHelper helper) {}
}
