package ru.pinkgoosik.kitsun.command.next;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
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
import java.util.Objects;

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
			public SlashCommandData build() {
				var data = Commands.slash(getName(), getDescription());
				data.addOption(OptionType.STRING, "slug", "Slug of the modrinth project", true);
				data.addOption(OptionType.CHANNEL, "channel", "Discord channel where updates should be published", true);
				return data;
			}

			@Override
			public void respond(SlashCommandInteractionEvent ctx, CommandHelper helper) {
				String slug = Objects.requireNonNull(ctx.getOption("slug")).getAsString();
				var channel = Objects.requireNonNull(ctx.getOption("channel")).getAsChannel();

				String channelId = channel.getId();
				ctx.deferReply().setEphemeral(true).queue();
				proceed(helper, slug, channelId);
			}

			private void proceed(CommandHelper helper, String slug, String channelId) {
				var guild = helper.event.getGuild();
				var member = helper.event.getInteraction().getMember();
				if(guild != null && member != null) {
					var data = ServerData.get(guild.getId());

					if(!data.permissions.get().hasAccessTo(member, Permissions.CHANGELOG_PUBLISHER_ADD)) {
						helper.ephemeral(Embeds.error("Not enough permissions."));
						return;
					}
					if(!ChannelUtils.exist(data.server, channelId)) {
						helper.ephemeral(Embeds.error("Such channel doesn't exist!"));
						return;
					}
					if(ChannelUtils.isVoiceChannel(data.server, channelId)) {
						helper.ephemeral(Embeds.error("You can't link publisher to a voice channel!"));
						return;
					}
					var project = ModrinthAPI.getProject(slug);
					if(project.isPresent()) {
						for(var publisher : data.publishers.get()) {
							if(publisher.channel.equals(channelId) && publisher.project.equals(project.get().id)) {
								helper.ephemeral(Embeds.error("This channel already has a publisher of the `" + slug + "` project."));
								return;
							}
						}
						var old = data.publishers.get();
						var newOnes = new ArrayList<>(List.of(data.publishers.get()));
						newOnes.add(new ModUpdatesPublisher(data.server, channelId, project.get().id));
						data.publishers.set(newOnes.toArray(old));
						data.publishers.save();

						String text = "Changelog publisher for the project `" + slug + "` got successfully created! Make sure bot has permission to send messages in this channel otherwise it wont work.";
						helper.ephemeral(Embeds.success("Creating Changelog Publisher", text));
					}
					else {
						helper.ephemeral(Embeds.error("Project `" + slug + "` is not found."));
					}
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
			public SlashCommandData build() {
				var data = Commands.slash(getName(), getDescription());
				data.addOption(OptionType.STRING, "slug", "Slug of the modrinth project", true);
				return data;
			}

			@Override
			public void respond(SlashCommandInteractionEvent ctx, CommandHelper helper) {
				String slug = Objects.requireNonNull(ctx.getOption("slug")).getAsString();
				ctx.deferReply().setEphemeral(true).queue();
				proceed(helper, slug);
			}

			private void proceed(CommandHelper helper, String slug) {
				var guild = helper.event.getGuild();
				var member = helper.event.getInteraction().getMember();
				if(guild != null && member != null) {
					var data = ServerData.get(guild.getId());
					var project = ModrinthAPI.getProject(slug);
					if(!data.permissions.get().hasAccessTo(member, Permissions.CHANGELOG_PUBLISHER_REMOVE)) {
						helper.ephemeral(Embeds.error("Not enough permissions."));
						return;
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
							helper.ephemeral(Embeds.success("Removing Changelog Publisher", text));

						}
						else {
							helper.ephemeral(Embeds.error("`" + slug + "` project doesn't have any publishers."));
						}
					}
					else {
						helper.ephemeral(Embeds.error("Project `" + slug + "` is not found."));
					}
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
