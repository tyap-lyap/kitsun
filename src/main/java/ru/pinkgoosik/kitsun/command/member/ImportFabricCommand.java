package ru.pinkgoosik.kitsun.command.member;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import ru.pinkgoosik.kitsun.api.FabricMeta;
import ru.pinkgoosik.kitsun.api.modrinth.ModrinthAPI;
import ru.pinkgoosik.kitsun.command.CommandHelper;
import ru.pinkgoosik.kitsun.command.CommandNext;
import ru.pinkgoosik.kitsun.util.Embeds;

import java.util.Objects;

public class ImportFabricCommand extends CommandNext {

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
		proceed(mcVersion, helper);

	}

	public void proceed(String mcVersion, CommandHelper helper) {
		String fabricApiVersion = "null";
		String fabricLoaderVersion = "null";
		String yarnVersion = "null";

		var fabricApi = ModrinthAPI.getVersions("fabric-api");
		if(fabricApi.isPresent()) {
			for(var ver : fabricApi.get()) {
				if(ver.gameVersions.contains(mcVersion)) {
					fabricApiVersion = ver.versionNumber;
					break;
				}
			}
		}
		var entries = FabricMeta.getFabricVersions(mcVersion);
		if(entries.isPresent()) {
			fabricLoaderVersion = entries.get().get(0).loader.version;
			yarnVersion = entries.get().get(0).mappings.version;
		}
		helper.followup(Embeds.success("Import Fabric", "minecraft_version = " + mcVersion + "\nyarn_mappings = " + yarnVersion + "\nfabric_loader = " + fabricLoaderVersion + "\nfabric_api = " + fabricApiVersion));
	}
}
