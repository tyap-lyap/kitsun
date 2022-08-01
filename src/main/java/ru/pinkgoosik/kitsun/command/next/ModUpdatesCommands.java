package ru.pinkgoosik.kitsun.command.next;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.Channel;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ImmutableApplicationCommandRequest;
import reactor.core.publisher.Mono;
import ru.pinkgoosik.kitsun.api.modrinth.ModrinthAPI;
import ru.pinkgoosik.kitsun.cache.ServerData;
import ru.pinkgoosik.kitsun.command.CommandHelper;
import ru.pinkgoosik.kitsun.command.CommandNext;
import ru.pinkgoosik.kitsun.feature.ModUpdatesPublisher;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.ChannelUtils;
import ru.pinkgoosik.kitsun.util.Embeds;

import java.util.ArrayList;
import java.util.List;

public class ModUpdatesCommands {

	public static CommandNext add() {
		return new CommandNext() {
			@Override
			public String getName() {
				return "mod-updates-add";
			}

			@Override
			public String getDescription() {
				return "Creates changelog publisher of the certain modrinth project.";
			}

			@Override
			public ImmutableApplicationCommandRequest.Builder build(ImmutableApplicationCommandRequest.Builder builder) {
				builder.addOption(ApplicationCommandOptionData.builder()
						.name("slug")
						.description("Slug of the modrinth project")
						.type(ApplicationCommandOption.Type.STRING.getValue())
						.required(true)
						.build()
				);
				builder.addOption(ApplicationCommandOptionData.builder()
						.name("channel")
						.description("Discord channel where updates should be published")
						.type(ApplicationCommandOption.Type.CHANNEL.getValue())
						.required(true)
						.build()
				);
				return builder;
			}

			@Override
			public void respond(ChatInputInteractionEvent ctx, CommandHelper helper) {
				String slug = ctx.getOption("slug")
						.flatMap(ApplicationCommandInteractionOption::getValue)
						.map(ApplicationCommandInteractionOptionValue::asString)
						.get();
				Channel channel = ctx.getOption("channel")
						.flatMap(ApplicationCommandInteractionOption::getValue)
						.map(ApplicationCommandInteractionOptionValue::asChannel)
						.get().block();
				if(channel != null) {
					String channelId = channel.getId().asString();
					ctx.deferReply().withEphemeral(true).then(proceed(helper, slug, channelId)).block();
				}
			}

			private Mono<Message> proceed(CommandHelper helper, String slug, String channelId) {
				var data = ServerData.get(helper.event.getInteraction().getGuildId().get().asString());
				var member = helper.event.getInteraction().getMember();
				if(member.isPresent()) {
					if(!data.permissions.get().hasAccessTo(member.get(), Permissions.CHANGELOG_PUBLISHER_ADD)) {
						return helper.ephemeral(Embeds.errorSpec("Not enough permissions."));
					}
				}
				if(!ChannelUtils.exist(data.server, channelId)) {
					return helper.ephemeral(Embeds.errorSpec("Such channel doesn't exist!"));
				}
				if(ChannelUtils.isVoiceChannel(data.server, channelId)) {
					return helper.ephemeral(Embeds.errorSpec("You can't link publisher to a voice channel!"));
				}
				var project = ModrinthAPI.getProject(slug);
				if(project.isPresent()) {
					for(var publisher : data.publishers.get()) {
						if(publisher.channel.equals(channelId) && publisher.project.equals(project.get().id)) {
							return helper.ephemeral(Embeds.errorSpec("This channel already has a publisher of the `" + slug + "` project."));
						}
					}
					var old = data.publishers.get();
					var newOnes = new ArrayList<>(List.of(data.publishers.get()));
					newOnes.add(new ModUpdatesPublisher(data.server, channelId, project.get().id));
					data.publishers.set(newOnes.toArray(old));
					data.publishers.save();

					String text = "Changelog publisher for the project `" + slug + "` got successfully created! Make sure bot has permission to send messages in this channel otherwise it wont work.";
					return helper.ephemeral(Embeds.successSpec("Creating Changelog Publisher", text));
				}
				else {
					return helper.ephemeral(Embeds.errorSpec("Project `" + slug + "` is not found."));
				}
			}
		};
	}

	public static CommandNext remove() {
		return new CommandNext() {
			@Override
			public String getName() {
				return "mod-updates-remove";
			}

			@Override
			public String getDescription() {
				return "Removes changelog publisher of the certain modrinth project.";
			}

			@Override
			public ImmutableApplicationCommandRequest.Builder build(ImmutableApplicationCommandRequest.Builder builder) {
				builder.addOption(ApplicationCommandOptionData.builder()
						.name("slug")
						.description("Slug of the modrinth project")
						.type(ApplicationCommandOption.Type.STRING.getValue())
						.required(true)
						.build()
				);
				return builder;
			}

			@Override
			public void respond(ChatInputInteractionEvent ctx, CommandHelper helper) {
				String slug = ctx.getOption("slug")
						.flatMap(ApplicationCommandInteractionOption::getValue)
						.map(ApplicationCommandInteractionOptionValue::asString)
						.get();
				ctx.deferReply().withEphemeral(true).then(proceed(helper, slug)).block();
			}

			private Mono<Message> proceed(CommandHelper helper, String slug) {
				var data = ServerData.get(helper.event.getInteraction().getGuildId().get().asString());
				var project = ModrinthAPI.getProject(slug);
				var member = helper.event.getInteraction().getMember();
				if(member.isPresent()) {
					if(!data.permissions.get().hasAccessTo(member.get(), Permissions.CHANGELOG_PUBLISHER_REMOVE)) {
						return helper.ephemeral(Embeds.errorSpec("Not enough permissions."));
					}
				}
				if(project.isPresent()) {
					if(hasPublishers(project.get().id, data)) {
						var old = new ArrayList<>(List.of(data.publishers.get()));
						old.removeIf(publisher -> publisher.project.equals(project.get().id));

						var newOnes = new ModUpdatesPublisher[old.size()];
						for(int i = 0; i < old.size(); i++) {
							newOnes[i] = old.get(i);
						}

						data.publishers.set(newOnes);
						data.publishers.save();
						String text = "All publisher of the `" + slug + "` project got removed.";
						return helper.ephemeral(Embeds.successSpec("Removing Changelog Publisher", text));
					}
					else {
						return helper.ephemeral(Embeds.errorSpec("`" + slug + "` project doesn't have any publishers."));
					}
				}
				else {
					return helper.ephemeral(Embeds.errorSpec("Project `" + slug + "` is not found."));
				}
			}

			private static boolean hasPublishers(String projectId, ServerData data) {
				for(var publisher : data.publishers.get()) {
					if(publisher.project.equals(projectId)) return true;
				}
				return false;
			}
		};
	}
}
