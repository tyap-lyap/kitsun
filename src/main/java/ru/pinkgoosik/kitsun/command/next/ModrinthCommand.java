package ru.pinkgoosik.kitsun.command.next;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import ru.pinkgoosik.kitsun.api.Modrinth;
import ru.pinkgoosik.kitsun.command.CommandHelper;
import ru.pinkgoosik.kitsun.command.CommandNext;
import ru.pinkgoosik.kitsun.feature.ModCard;
import ru.pinkgoosik.kitsun.util.Embeds;

import java.util.Objects;

public class ModrinthCommand extends CommandNext {

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
		data.addOption(OptionType.STRING, "slug", "Slug of the Modrinth project", true);
		return data;
	}

	@Override
	public void respond(SlashCommandInteractionEvent ctx, CommandHelper helper) {
		String slug = Objects.requireNonNull(ctx.getOption("slug")).getAsString();

		ctx.deferReply().queue();
		proceed(helper, slug);
	}

	private void proceed(CommandHelper helper, String slug) {
		Modrinth.getProject(slug).ifPresentOrElse(project -> helper.followup(ModCard.createEmbed(project, null)),
				() -> helper.followup(Embeds.error("Project `" + slug + "` is not found.")));
	}
}
