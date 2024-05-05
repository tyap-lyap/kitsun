package ru.pinkgoosik.kitsun.command.member;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;
import ru.pinkgoosik.kitsun.api.mojang.MojangAPI;
import ru.pinkgoosik.kitsun.api.mojang.PatchNotes;
import ru.pinkgoosik.kitsun.command.CommandHelper;
import ru.pinkgoosik.kitsun.command.KitsunCommand;
import ru.pinkgoosik.kitsun.feature.MCUpdatesPublisher;
import ru.pinkgoosik.kitsun.util.Embeds;
import ru.pinkgoosik.kitsun.util.KitsunColors;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class McPatchnoteCommand extends KitsunCommand {

	@Override
	public String getName() {
		return "mc-patchnote";
	}

	@Override
	public String getDescription() {
		return "Sends a short description of the patch notes of the given mc version.";
	}

	@Override
	public void build(SlashCommandData data) {
		data.addOption(OptionType.STRING, "version", "Minecraft version", true, true);
	}

	@Override
	public void respond(SlashCommandInteractionEvent ctx, CommandHelper helper) {
		String mcVersion = Objects.requireNonNull(ctx.getOption("version")).getAsString();
		ctx.deferReply().queue();

		var optional = PatchNotes.getEntry(mcVersion);

		if (optional.isPresent()) {
			helper.followup(createPatchNotesEmbed(optional.get()));
        }
		else {
			helper.followup(Embeds.error("Minecraft version not found."));
		}

	}

	public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
		if (event.getName().equals("mc-patchnote") && event.getFocusedOption().getName().equals("version")) {
			ArrayList<String> versions = MojangAPI.getMcVersionsCache();

			List<Command.Choice> options = Stream.of(versions.toArray(new String[]{}))
				.filter(word -> word.startsWith(event.getFocusedOption().getValue())) // only display words that start with the user's current input
				.map(word -> new Command.Choice(word, word)) // map the words to choices
				.collect(Collectors.toList());

			event.replyChoices(options.size() <= 20 ? options : options.subList(0, 20)).queue();
		}
	}

	private MessageEmbed createPatchNotesEmbed(PatchNotes.PatchNotesEntry entry) {
		return new EmbedBuilder().setTitle(entry.version + " Patch Notes")
			.setThumbnail(entry.image.getFullUrl())
			.setDescription(entry.summary() + "\n[Homepage](https://minecraft.net) | [Issue Tracker](https://bugs.mojang.com/issues) | [Full Patch Notes](" + MCUpdatesPublisher.QUILT_MC_PATCH_NOTES.replaceAll("%version%", entry.version) + ")")
			.setColor(KitsunColors.getCyan())
			.setTimestamp(Instant.now())
			.build();
	}
}

