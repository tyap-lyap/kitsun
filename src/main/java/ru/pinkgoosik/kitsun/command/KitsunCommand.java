package ru.pinkgoosik.kitsun.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public abstract class KitsunCommand {
	public abstract String getName();

	public abstract String getDescription();

	public abstract SlashCommandData build();

	public boolean isTLExclusive() {
		return false;
	}

	public void respond(SlashCommandInteractionEvent ctx, CommandHelper helper) {
	}
}
