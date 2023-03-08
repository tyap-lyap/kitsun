package ru.pinkgoosik.kitsun.command.member;

import masecla.modrinth4j.model.version.ProjectVersion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import ru.pinkgoosik.kitsun.api.FabricMeta;
import ru.pinkgoosik.kitsun.api.Modrinth;
import ru.pinkgoosik.kitsun.command.CommandHelper;
import ru.pinkgoosik.kitsun.command.KitsunCommand;
import ru.pinkgoosik.kitsun.util.Embeds;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Objects;

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
	public SlashCommandData build() {
		var data = Commands.slash(getName(), getDescription());
		data.addOption(OptionType.STRING, "version", "Minecraft version", true);
		return data;
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
		if(entries.isPresent()) {
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
}
