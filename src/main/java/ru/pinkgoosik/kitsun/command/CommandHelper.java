package ru.pinkgoosik.kitsun.command;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionApplicationCommandCallbackSpec;
import discord4j.core.spec.InteractionFollowupCreateSpec;
import reactor.core.publisher.Mono;

public class CommandHelper {
	public ChatInputInteractionEvent event;

	public CommandHelper(ChatInputInteractionEvent event) {
		this.event = event;
	}

	public void reply(EmbedCreateSpec embed) {
		this.event.reply(InteractionApplicationCommandCallbackSpec.builder().addEmbed(embed).build()).block();
	}

	public Mono<Message> followup(EmbedCreateSpec embed) {
		return this.event.createFollowup(InteractionFollowupCreateSpec.builder().addEmbed(embed).build());
	}

	public Mono<Message> ephemeral(EmbedCreateSpec embed) {
		return this.event.createFollowup(InteractionFollowupCreateSpec.builder().ephemeral(true).addEmbed(embed).build());
	}
}
