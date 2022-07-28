package ru.pinkgoosik.kitsun.command.member;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.InteractionFollowupCreateSpec;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Mono;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.api.mojang.MojangAPI;
import ru.pinkgoosik.kitsun.command.CommandHelper;
import ru.pinkgoosik.kitsun.command.CommandNext;
import ru.pinkgoosik.kitsun.cosmetics.CosmeticsData;
import ru.pinkgoosik.kitsun.cosmetics.FtpConnection;
import ru.pinkgoosik.kitsun.util.Embeds;

public class RegisterCommands {

	public static CommandNext reg() {
		return new CommandNext() {
			@Override
			public String getName() {
				return "reg";
			}

			@Override
			public String getDescription() {
				return "Registers player in the cosmetics system.";
			}

			@Override
			public void build() {
				long application = Bot.rest.getApplicationId().block();
				long server = 854349856164020244L;

				ApplicationCommandRequest register = ApplicationCommandRequest.builder()
						.name(this.getName())
						.description(this.getDescription())
						.addOption(ApplicationCommandOptionData.builder()
								.name("nickname")
								.description("Your Minecraft nickname")
								.type(ApplicationCommandOption.Type.STRING.getValue())
								.required(true)
								.build()
						).build();

				Bot.rest.getApplicationService().createGuildApplicationCommand(application, server, register).subscribe();
			}

			@Override
			public void respond(ChatInputInteractionEvent ctx, CommandHelper helper) {
				String name = ctx.getOption("nickname")
						.flatMap(ApplicationCommandInteractionOption::getValue)
						.map(ApplicationCommandInteractionOptionValue::asString)
						.get().replaceAll("[^a-zA-Z0-9_]", "");

				ctx.getInteraction().getMember().ifPresent(member -> ctx.deferReply().then(proceed(name, member, ctx)).block());
			}

			public Mono<Message> proceed(String name, Member member, ChatInputInteractionEvent ctx) {
				if(CosmeticsData.getEntry(member.getId().asString()).isPresent()) {
					return ctx.createFollowup(InteractionFollowupCreateSpec.builder().addEmbed(Embeds.errorSpec("You already registered!")).build());
				}
				if(CosmeticsData.getEntryByName(name).isPresent()) {
					return ctx.createFollowup(InteractionFollowupCreateSpec.builder().addEmbed(Embeds.errorSpec("Player `" + name + "` is already registered!")).build());
				}
				if(MojangAPI.getUuid(name).isPresent()) {
					CosmeticsData.register(member.getId().asString(), name, MojangAPI.getUuid(name).get());
					FtpConnection.updateData();
					return ctx.createFollowup(InteractionFollowupCreateSpec.builder().addEmbed(Embeds.successSpec("Player Registering", "Player `" + name + "` is now registered! \nPlease checkout `!help` for more commands.")).build());
				}
				else {
					return ctx.createFollowup(InteractionFollowupCreateSpec.builder().addEmbed(Embeds.errorSpec("Player `" + name + "` is not found. Write down your Minecraft username.")).build());
				}
			}
		};
	}

	public static CommandNext unreg() {
		return new CommandNext() {
			@Override
			public String getName() {
				return "unreg";
			}

			@Override
			public String getDescription() {
				return "Unregisters player from the cosmetics system.";
			}

			@Override
			public void build() {
				long application = Bot.rest.getApplicationId().block();
				long server = 854349856164020244L;

				ApplicationCommandRequest unregister = ApplicationCommandRequest.builder()
						.name(this.getName())
						.description(this.getDescription())
						.build();

				Bot.rest.getApplicationService().createGuildApplicationCommand(application, server, unregister).subscribe();
			}

			@Override
			public void respond(ChatInputInteractionEvent ctx, CommandHelper helper) {
				ctx.getInteraction().getMember().ifPresent(member -> ctx.deferReply().then(proceed(member, ctx)).block());
			}

			public Mono<Message> proceed(Member member, ChatInputInteractionEvent ctx) {
				String memberId = member.getId().asString();
				var entry = CosmeticsData.getEntry(memberId);
				if(entry.isPresent()) {
					CosmeticsData.unregister(memberId);
					FtpConnection.updateData();
					String text = "Player " + entry.get().user.name + " is successfully unregistered. \nHope to see you soon later!";
					return ctx.createFollowup(InteractionFollowupCreateSpec.builder().addEmbed(Embeds.successSpec("Player Unregistering", text)).build());
				}
				else {
					return ctx.createFollowup(InteractionFollowupCreateSpec.builder().addEmbed(Embeds.errorSpec("You have not registered yet!")).build());
				}
			}
		};
	}
}
