package ru.pinkgoosik.kitsun.command.member;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import ru.pinkgoosik.kitsun.api.QuiltMeta;
import ru.pinkgoosik.kitsun.api.modrinth.ModrinthAPI;
import ru.pinkgoosik.kitsun.command.CommandHelper;
import ru.pinkgoosik.kitsun.command.CommandNext;
import ru.pinkgoosik.kitsun.util.Embeds;

import java.util.Objects;

public class ImportQuiltCommand extends CommandNext {

	@Override
	public String getName() {
		return "import-quilt";
	}

	@Override
	public String getDescription() {
		return "Fetches the latest versions of Quilt Loader, Quilt Mappings, and QSL.";
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
		proceed(mcVersion, helper);
	}

	public void proceed(String mcVersion, CommandHelper helper) {
		String qslVersion = "null";
		String quiltLoaderVersion = "null";
		String qmVersion = "null";

		var qsl = ModrinthAPI.getVersions("qsl");
		if(qsl.isPresent()) {
			for(var ver : qsl.get()) {
				if(ver.gameVersions.contains(mcVersion)) {
					qslVersion = ver.versionNumber;
					break;
				}
			}
		}
		var entries = QuiltMeta.getQuiltVersions(mcVersion);
		if(entries.isPresent()) {
			quiltLoaderVersion = entries.get().get(0).loader.version;
		}
		var mappings = QuiltMeta.getQuiltMappingsVersions(mcVersion);
		if(mappings.isPresent()) {
			qmVersion = mappings.get().get(0).version;
		}

		helper.followup(Embeds.success("Import Quilt", "minecraft_version = " + mcVersion + "\nquilt_mappings = " + qmVersion + "\nquilt_loader = " + quiltLoaderVersion + "\nqsl_version = " + qslVersion));

	}
}
