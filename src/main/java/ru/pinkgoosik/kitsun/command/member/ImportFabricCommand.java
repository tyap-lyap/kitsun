package ru.pinkgoosik.kitsun.command.member;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.InteractionFollowupCreateSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ImmutableApplicationCommandRequest;
import reactor.core.publisher.Mono;
import ru.pinkgoosik.kitsun.api.FabricMeta;
import ru.pinkgoosik.kitsun.api.modrinth.ModrinthAPI;
import ru.pinkgoosik.kitsun.command.CommandHelper;
import ru.pinkgoosik.kitsun.command.CommandNext;
import ru.pinkgoosik.kitsun.util.Embeds;

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
	public ImmutableApplicationCommandRequest.Builder build(ImmutableApplicationCommandRequest.Builder builder) {
		builder.addOption(ApplicationCommandOptionData.builder()
				.name("version")
				.description("Minecraft version")
				.type(ApplicationCommandOption.Type.STRING.getValue())
				.required(true)
				.build()
		);
		return builder;
	}

	@Override
	public void respond(ChatInputInteractionEvent ctx, CommandHelper helper) {
		String mcVersion = ctx.getOption("version")
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asString)
				.get();
		ctx.deferReply().then(proceed(mcVersion, ctx)).block();
	}

	public Mono<Message> proceed(String mcVersion, ChatInputInteractionEvent ctx) {
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
		return ctx.createFollowup(InteractionFollowupCreateSpec.builder().addEmbed(Embeds.successSpec("Import Fabric", "minecraft_version = " + mcVersion + "\nyarn_mappings = " + yarnVersion + "\nfabric_loader = " + fabricLoaderVersion + "\nfabric_api = " + fabricApiVersion)).build());
	}
}
