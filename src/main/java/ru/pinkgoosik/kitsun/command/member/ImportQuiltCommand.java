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
import ru.pinkgoosik.kitsun.api.QuiltMeta;
import ru.pinkgoosik.kitsun.api.modrinth.ModrinthAPI;
import ru.pinkgoosik.kitsun.command.CommandHelper;
import ru.pinkgoosik.kitsun.command.CommandNext;
import ru.pinkgoosik.kitsun.util.Embeds;

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

		return ctx.createFollowup(InteractionFollowupCreateSpec.builder().addEmbed(Embeds.successSpec("Import Quilt", "minecraft_version = " + mcVersion + "\nquilt_mappings = " + qmVersion + "\nquilt_loader = " + quiltLoaderVersion + "\nqsl_version = " + qslVersion)).build());
	}
}
