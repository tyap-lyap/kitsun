package ru.pinkgoosik.kitsun.command.admin;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import ru.pinkgoosik.kitsun.api.Modrinth;
import ru.pinkgoosik.kitsun.cache.ServerData;
import ru.pinkgoosik.kitsun.command.CommandHelper;
import ru.pinkgoosik.kitsun.command.KitsunCommand;
import ru.pinkgoosik.kitsun.feature.ModUpdatesPublisher;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.ChannelUtils;
import ru.pinkgoosik.kitsun.util.Embeds;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModUpdatesCommand extends KitsunCommand {

	@Override
	public String getName() {
		return "mod-updates";
	}

	@Override
	public String getDescription() {
		return "Creates/removes changelog publisher of the certain Modrinth project.";
	}

	@Override
	public void build(SlashCommandData data) {
		data.addSubcommands(new SubcommandData("add", "Creates mod updates publisher of the certain Modrinth project.")
				.addOption(OptionType.STRING,"slug", "Slug of the Modrinth project", true)
				.addOption(OptionType.CHANNEL, "channel", "Discord channel where updates should be published", true)
			    .addOption(OptionType.BOOLEAN, "manual-check", "Option if this publisher's check should be called manually", false));

		data.addSubcommands(new SubcommandData("remove", "Removes mod updates publisher.")
				.addOption(OptionType.STRING,"slug", "Slug of the Modrinth project", true));
	}

	@Override
	public void respond(SlashCommandInteractionEvent ctx, CommandHelper helper) {
		String subcommand = Objects.requireNonNull(helper.event.getSubcommandName());

		if(subcommand.equals("add")) {
			String slug = Objects.requireNonNull(ctx.getOption("slug")).getAsString();
			var channel = Objects.requireNonNull(ctx.getOption("channel")).getAsChannel();
			var manualCheckOption = ctx.getOption("manual-check");
			boolean manualCheck = manualCheckOption != null ? manualCheckOption.getAsBoolean() : false;

			String channelId = channel.getId();
			ctx.deferReply().setEphemeral(true).queue();

			var guild = helper.guild;
			var member = helper.member;
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
			var project = Modrinth.getProject(slug);
			if(project.isPresent()) {
				for(var publisher : data.modUpdates.get()) {
					if(publisher.channel.equals(channelId) && publisher.project.equals(project.get().getId())) {
						helper.ephemeral(Embeds.error("This channel already has a publisher of the `" + slug + "` project."));
						return;
					}
				}
				var old = data.modUpdates.get();
				var newOnes = new ArrayList<>(List.of(data.modUpdates.get()));
				newOnes.add(new ModUpdatesPublisher(data.server, channelId, project.get().getId(), manualCheck));
				data.modUpdates.set(newOnes.toArray(old));
				data.modUpdates.save();

				String text = "Changelog publisher for the project `" + slug + "` got successfully created! Make sure bot has permission to send messages in this channel otherwise it wont work.";
				helper.ephemeral(Embeds.success("Creating Changelog Publisher", text));
			}
			else {
				helper.ephemeral(Embeds.error("Project `" + slug + "` is not found."));
			}

		}
		else if(subcommand.equals("remove")) {
			String slug = Objects.requireNonNull(ctx.getOption("slug")).getAsString();
			ctx.deferReply().setEphemeral(true).queue();

			var guild = helper.guild;
			var member = helper.member;
			var data = ServerData.get(guild.getId());
			var project = Modrinth.getProject(slug);

			if(!data.permissions.get().hasAccessTo(member, Permissions.CHANGELOG_PUBLISHER_REMOVE)) {
				helper.ephemeral(Embeds.error("Not enough permissions."));
				return;
			}
			if(project.isPresent()) {
				if(hasPublishers(project.get().getId(), data)) {
					var old = new ArrayList<>(List.of(data.modUpdates.get()));
					old.removeIf(publisher -> publisher.project.equals(project.get().getId()));

					var newOnes = new ModUpdatesPublisher[old.size()];
					for(int i = 0; i < old.size(); i++) {
						newOnes[i] = old.get(i);
					}

					data.modUpdates.set(newOnes);
					data.modUpdates.save();
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
		for(var publisher : data.modUpdates.get()) {
			if(publisher.project.equals(projectId)) return true;
		}
		return false;
	}
}
