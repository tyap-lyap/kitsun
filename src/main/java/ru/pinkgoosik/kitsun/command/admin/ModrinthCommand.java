package ru.pinkgoosik.kitsun.command.admin;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import ru.pinkgoosik.kitsun.api.Modrinth;
import ru.pinkgoosik.kitsun.command.CommandHelper;
import ru.pinkgoosik.kitsun.command.KitsunCommand;
import ru.pinkgoosik.kitsun.feature.ModCard;
import ru.pinkgoosik.kitsun.util.Embeds;

import java.util.Objects;

public class ModrinthCommand extends KitsunCommand {

	@Override
	public String getName() {
		return "modrinth";
	}

	@Override
	public String getDescription() {
		return "Fetches Modrinth project by given slug.";
	}

	@Override
	public SlashCommandData build() {
		var data = Commands.slash(getName(), getDescription());
		data.addOption(OptionType.STRING, "slug", "Slug of the Modrinth project.", true);
		return data;
	}

	@Override
	public void respond(SlashCommandInteractionEvent ctx, CommandHelper helper) {
		String slug = Objects.requireNonNull(ctx.getOption("slug")).getAsString();

		ctx.deferReply().queue();

		Modrinth.getProject(slug).ifPresentOrElse(project -> helper.followup(ModCard.createEmbed(project, null)),
				() -> helper.followup(Embeds.error("Project `" + slug + "` is not found.")));
	}
}
