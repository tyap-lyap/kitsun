package ru.pinkgoosik.kitsun.command.admin;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import ru.pinkgoosik.kitsun.command.CommandHelper;
import ru.pinkgoosik.kitsun.command.KitsunCommand;
import ru.pinkgoosik.kitsun.util.Embeds;
import ru.pinkgoosik.kitsun.util.KitsunColors;

import java.awt.*;
import java.util.Objects;

public class EmbedCommand extends KitsunCommand {
	@Override
	public String getName() {
		return "embed";
	}

	@Override
	public String getDescription() {
		return "Creates embed with given color, title, and description.";
	}

	@Override
	public SlashCommandData build() {
		var data = Commands.slash(getName(), getDescription());
		data.addOption(OptionType.STRING, "description", "Embed description.", true);
		data.addOption(OptionType.STRING, "title", "Embed title.", false);
		data.addOption(OptionType.STRING, "color", "Embed color.", false);
		return data;
	}

	@Override
	public void respond(SlashCommandInteractionEvent ctx, CommandHelper helper) {
		var title = ctx.getOption("title");
		String description = Objects.requireNonNull(ctx.getOption("description")).getAsString();
		var color = ctx.getOption("color");
		var channel = helper.event.getChannel();
		var embed = new EmbedBuilder();
		var member = helper.event.getInteraction().getMember();
		ctx.deferReply().setEphemeral(true).queue();

		if(member != null) {
			if(!member.getPermissions().contains(Permission.ADMINISTRATOR)) {
				helper.ephemeral(Embeds.error("Not enough permissions."));
				return;
			}

			if(color != null) {
				try {
					embed.setColor(Color.decode(color.getAsString()));
				}
				catch (NumberFormatException e) {
					helper.ephemeral(Embeds.error("Color can't be decoded!"));
					return;
				}
			}
			else {
				embed.setColor(KitsunColors.getBlue());
			}

			if(title != null) {
				embed.setTitle(title.getAsString());
			}

			channel.sendMessageEmbeds(embed.setDescription(description).build()).queue();
			helper.ephemeral(Embeds.success("Embed Creation", "Success."));
		}
	}
}
