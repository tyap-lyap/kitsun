package ru.pinkgoosik.kitsun.command;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class CommandHelper {
	public SlashCommandInteractionEvent event;

	public CommandHelper(SlashCommandInteractionEvent event) {
		this.event = event;
	}

	public void reply(MessageEmbed embed) {
		event.reply(MessageCreateData.fromEmbeds(embed)).queue();
//		this.event.reply(InteractionApplicationCommandCallbackSpec.builder().addEmbed(embed).build()).block();
	}

	public void followup(MessageEmbed embed) {
		this.event.getInteraction().getHook().editOriginalEmbeds(embed).queue();
//		return this.event.createFollowup(InteractionFollowupCreateSpec.builder().addEmbed(embed).build());
	}

	public void ephemeral(MessageEmbed embed) {
		event.getInteraction().getHook().setEphemeral(true).editOriginalEmbeds(embed).queue();
//		return this.event.createFollowup(InteractionFollowupCreateSpec.builder().ephemeral(true).addEmbed(embed).build());
	}
}
