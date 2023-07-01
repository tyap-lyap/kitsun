package ru.pinkgoosik.kitsun.command;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

public abstract class KitsunCommand {
	public abstract String getName();

	public abstract String getDescription();

	public abstract void build(SlashCommandData data);

	public boolean isTLExclusive() {
		return false;
	}

	public abstract void respond(SlashCommandInteractionEvent ctx, CommandHelper helper);

	public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {}
}
