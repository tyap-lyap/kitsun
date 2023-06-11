package ru.pinkgoosik.kitsun.command.admin;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import ru.pinkgoosik.kitsun.cache.ServerData;
import ru.pinkgoosik.kitsun.command.CommandHelper;
import ru.pinkgoosik.kitsun.command.KitsunCommand;
import ru.pinkgoosik.kitsun.feature.AutoReaction;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AutoReactionCommand extends KitsunCommand {
	@Override
	public String getName() {
		return "auto-reaction";
	}

	@Override
	public String getDescription() {
		return "Automatic reactions under messages with matching regex.";
	}

	@Override
	public SlashCommandData build() {
		var data = Commands.slash(getName(), getDescription());

		data.addSubcommands(new SubcommandData("add", "Automatic reactions under messages with matching regex.")
			.addOption(OptionType.STRING,"regex", "The regex", true)
			.addOption(OptionType.STRING, "emoji", "Id of the custom emoji or unicode emoji code", true));

		data.addSubcommands(new SubcommandData("remove", "Automatic reactions under messages with matching regex.")
			.addOption(OptionType.STRING,"regex", "The regex", true, true));

		return data;
	}

	@Override
	public void respond(SlashCommandInteractionEvent ctx, CommandHelper helper) {
		String subcommand = Objects.requireNonNull(helper.event.getSubcommandName());

		if(subcommand.equals("add")) {
			String regex = Objects.requireNonNull(ctx.getOption("regex")).getAsString();
			String emojiOption = Objects.requireNonNull(ctx.getOption("emoji")).getAsString();
			ctx.deferReply().setEphemeral(true).queue();

			var guild = helper.event.getGuild();
			var member = helper.event.getInteraction().getMember();
			if(guild != null && member != null) {
				var data = ServerData.get(guild.getId());

				if(!data.permissions.get().hasAccessTo(member, Permissions.AUTO_REACTION_ADD)) {
					helper.ephemeral(Embeds.error("Not enough permissions."));
					return;
				}

				try {
					var customEmoji = guild.getEmojiById(emojiOption);
					if(customEmoji != null) {
						var old = data.autoReactions.get();
						var newOnes = new ArrayList<>(List.of(data.autoReactions.get()));
						newOnes.add(new AutoReaction(guild.getId(), regex, emojiOption));
						data.autoReactions.set(newOnes.toArray(old));
						data.autoReactions.save();

						String text = "The auto reaction got successful registered.";
						helper.ephemeral(Embeds.success("Auto Reactions", text));
					}
				}
				catch (Exception e) {
					var old = data.autoReactions.get();
					var newOnes = new ArrayList<>(List.of(data.autoReactions.get()));
					newOnes.add(new AutoReaction(guild.getId(), regex, emojiOption, true));
					data.autoReactions.set(newOnes.toArray(old));
					data.autoReactions.save();

					String text = "The auto reaction got successful registered.";
					helper.ephemeral(Embeds.success("Auto Reactions", text));
				}

			}


		}
		else if(subcommand.equals("remove")) {
			String regex = Objects.requireNonNull(ctx.getOption("regex")).getAsString();
			ctx.deferReply().setEphemeral(true).queue();

			var guild = helper.event.getGuild();
			var member = helper.event.getInteraction().getMember();

			if(guild != null && member != null) {
				var data = ServerData.get(guild.getId());

				if(!data.permissions.get().hasAccessTo(member, Permissions.AUTO_REACTION_REMOVE)) {
					helper.ephemeral(Embeds.error("Not enough permissions."));
					return;
				}

				boolean updateList = false;
				for(AutoReaction react : data.autoReactions.get()) {
					if(react.regex.equals(regex)) {
						react.shouldBeRemoved = true;
						updateList = true;
					}
				}
				if (updateList) {
					ArrayList<AutoReaction> autoReactions = new ArrayList<>(List.of(data.autoReactions.get()));
					autoReactions.removeIf(card -> card.shouldBeRemoved);
					data.autoReactions.set(autoReactions.toArray(new AutoReaction[0]));
					data.autoReactions.save();

					String text = "All auto reactions of the `" + regex + "` regex got removed.";
					helper.ephemeral(Embeds.success("Auto Reactions", text));
				}
				else {
					helper.ephemeral(Embeds.error("Regex not found."));
				}
			}
		}
	}
}
