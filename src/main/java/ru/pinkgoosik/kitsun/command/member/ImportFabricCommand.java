package ru.pinkgoosik.kitsun.command.member;

import masecla.modrinth4j.model.version.ProjectVersion;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;
import ru.pinkgoosik.kitsun.api.FabricMeta;
import ru.pinkgoosik.kitsun.api.Modrinth;
import ru.pinkgoosik.kitsun.api.mojang.MojangAPI;
import ru.pinkgoosik.kitsun.command.CommandHelper;
import ru.pinkgoosik.kitsun.command.KitsunCommand;
import ru.pinkgoosik.kitsun.util.Embeds;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImportFabricCommand extends KitsunCommand {
	private static ArrayList<ProjectVersion> cachedVersions = null;
	private static Instant lastCacheUpdate = Instant.now();

	@Override
	public String getName() {
		return "import-fabric";
	}

	@Override
	public String getDescription() {
		return "Fetches the latest versions of Fabric Loader, Yarn, and Fabric API.";
	}

	@Override
	public void build(SlashCommandData data) {
		data.addOption(OptionType.STRING, "version", "Minecraft version", true, true);
	}

	@Override
	public void respond(SlashCommandInteractionEvent ctx, CommandHelper helper) {
		String mcVersion = Objects.requireNonNull(ctx.getOption("version")).getAsString();
		ctx.deferReply().queue();

		String fabricApiVersion = "null";
		String fabricLoaderVersion = "null";
		String yarnVersion = "null";

		var versions = getFabricApiVersions();
		for(var ver : versions) {
			if(ver.getGameVersions().contains(mcVersion)) {
				fabricApiVersion = ver.getVersionNumber();
				break;
			}
		}
		var entries = FabricMeta.getFabricVersions(mcVersion);
		if(entries.isPresent() && !entries.get().isEmpty()) {
			fabricLoaderVersion = entries.get().get(0).loader.version;
			yarnVersion = entries.get().get(0).mappings.version;
		}
		helper.followup(Embeds.success("Import Fabric", "minecraft_version = " + mcVersion + "\nyarn_mappings = " + yarnVersion + "\nfabric_loader = " + fabricLoaderVersion + "\nfabric_api = " + fabricApiVersion));

	}

	public static ArrayList<ProjectVersion> getFabricApiVersions() {
		if (cachedVersions == null) {
			Modrinth.getVersions("fabric-api").ifPresentOrElse(versions -> cachedVersions = versions, () -> cachedVersions = new ArrayList<>());
		}
		else {
			var minutes = ChronoUnit.MINUTES.between(lastCacheUpdate, Instant.now());
			if(minutes >= 10) {
				Modrinth.getVersions("fabric-api").ifPresentOrElse(versions -> cachedVersions = versions, () -> cachedVersions = new ArrayList<>());
				lastCacheUpdate = Instant.now();
			}
		}
		return cachedVersions;
	}

	public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
		if (event.getName().equals("import-fabric") || event.getName().equals("import-quilt") && event.getFocusedOption().getName().equals("version")) {
			ArrayList<String> versions = MojangAPI.getMcVersionsCache();

			List<Command.Choice> options = Stream.of(versions.toArray(new String[]{}))
				.filter(word -> word.startsWith(event.getFocusedOption().getValue())) // only display words that start with the user's current input
				.map(word -> new Command.Choice(word, word)) // map the words to choices
				.collect(Collectors.toList());

			event.replyChoices(options.size() <= 20 ? options : options.subList(0, 20)).queue();
		}
	}
}
