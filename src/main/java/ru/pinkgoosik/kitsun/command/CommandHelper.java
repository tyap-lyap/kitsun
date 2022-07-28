package ru.pinkgoosik.kitsun.command;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;

public class CommandHelper {
	public ChatInputInteractionEvent event;

	public CommandHelper(ChatInputInteractionEvent event) {
		this.event = event;
	}

	public void reply(EmbedCreateSpec embed) {
		this.event.reply(InteractionApplicationCommandCallbackSpec.builder().addEmbed(embed).build()).block();
	}
}
