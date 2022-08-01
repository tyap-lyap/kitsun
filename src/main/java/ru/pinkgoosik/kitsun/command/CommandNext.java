package ru.pinkgoosik.kitsun.command;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ImmutableApplicationCommandRequest;

public abstract class CommandNext {
	public abstract String getName();

	public abstract String getDescription();

	public abstract ImmutableApplicationCommandRequest.Builder build(ImmutableApplicationCommandRequest.Builder builder);

	public boolean isTLExclusive() {
		return false;
	}

	public void respond(ChatInputInteractionEvent ctx, CommandHelper helper) {
	}
}
