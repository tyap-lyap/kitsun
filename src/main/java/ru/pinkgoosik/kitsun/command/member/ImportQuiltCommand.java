package ru.pinkgoosik.kitsun.command.member;

import masecla.modrinth4j.model.version.ProjectVersion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import ru.pinkgoosik.kitsun.api.Modrinth;
import ru.pinkgoosik.kitsun.api.QuiltMeta;
import ru.pinkgoosik.kitsun.command.CommandHelper;
import ru.pinkgoosik.kitsun.command.KitsunCommand;
import ru.pinkgoosik.kitsun.util.Embeds;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Objects;

public class ImportQuiltCommand extends KitsunCommand {
	private static ArrayList<ProjectVersion> cachedVersions = null;
	private static Instant lastCacheUpdate = Instant.now();

	@Override
	public String getName() {
		return "import-quilt";
	}

	@Override
	public String getDescription() {
		return "Fetches the latest versions of Quilt Loader, Quilt Mappings, and QSL.";
	}

	@Override
	public void build(SlashCommandData data) {
		data.addOption(OptionType.STRING, "version", "Minecraft version", true, true);
	}

	@Override
	public void respond(SlashCommandInteractionEvent ctx, CommandHelper helper) {
		String mcVersion = Objects.requireNonNull(ctx.getOption("version")).getAsString();
		ctx.deferReply().queue();

		String qslVersion = "null";
		String quiltLoaderVersion = "null";
		String qmVersion = "null";

		var versions = getQslVersions();
		for(var ver : versions) {
			if(ver.getGameVersions().contains(mcVersion)) {
				qslVersion = ver.getVersionNumber();
				break;
			}
		}
		var entries = QuiltMeta.getQuiltVersions(mcVersion);
		if(entries.isPresent() && !entries.get().isEmpty()) {
			quiltLoaderVersion = entries.get().get(0).loader.version;
		}
		var mappings = QuiltMeta.getQuiltMappingsVersions(mcVersion);
		if(mappings.isPresent() && !mappings.get().isEmpty()) {
			qmVersion = mappings.get().get(0).version;
		}

		helper.followup(Embeds.success("Import Quilt", "minecraft_version = " + mcVersion + "\nquilt_mappings = " + qmVersion + "\nquilt_loader = " + quiltLoaderVersion + "\nqsl_version = " + qslVersion));
	}

	public static ArrayList<ProjectVersion> getQslVersions() {
		if (cachedVersions == null) {
			Modrinth.getVersions("qsl").ifPresentOrElse(versions -> cachedVersions = versions, () -> cachedVersions = new ArrayList<>());
		}
		else {
			var minutes = ChronoUnit.MINUTES.between(lastCacheUpdate, Instant.now());
			if(minutes >= 10) {
				Modrinth.getVersions("qsl").ifPresentOrElse(versions -> cachedVersions = versions, () -> cachedVersions = new ArrayList<>());
				lastCacheUpdate = Instant.now();
			}
		}
		return cachedVersions;
	}
}
