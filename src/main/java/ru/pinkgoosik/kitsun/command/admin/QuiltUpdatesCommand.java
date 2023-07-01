package ru.pinkgoosik.kitsun.command.admin;

import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import ru.pinkgoosik.kitsun.cache.ServerData;
import ru.pinkgoosik.kitsun.command.CommandHelper;
import ru.pinkgoosik.kitsun.command.KitsunCommand;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;

import java.util.Objects;

public class QuiltUpdatesCommand extends KitsunCommand {

	@Override
	public String getName() {
		return "quilt-updates";
	}

	@Override
	public String getDescription() {
		return "Toggles Quilt Loader updates publishing and links it to a specified channel.";
	}

	@Override
	public void build(SlashCommandData data) {
		data.addSubcommands(new SubcommandData("enable", "Enables Quilt Loader updates publishing and links it to a specified channel.")
				.addOption(OptionType.CHANNEL, "channel", "Channel where updates gonna be published.", true));

		data.addSubcommands(new SubcommandData("disable", "Disables Quilt Loader updates."));
	}

	@Override
	public void respond(SlashCommandInteractionEvent ctx, CommandHelper helper) {
		String subcommand = Objects.requireNonNull(helper.event.getSubcommandName());

		if(subcommand.equals("enable")) {
			var channel = Objects.requireNonNull(ctx.getOption("channel")).getAsChannel();
			ctx.deferReply().setEphemeral(true).queue();

			var guild = helper.guild;
			var member = helper.member;
			var data = ServerData.get(guild.getId());

			if(!data.permissions.get().hasAccessTo(member, Permissions.QUILT_UPDATES_ENABLE)) {
				helper.ephemeral(Embeds.error("Not enough permissions."));
				return;
			}

			if(channel instanceof VoiceChannel) {
				helper.ephemeral(Embeds.error("You can't link quilt updates publishing to a voice channel!"));
				return;
			}

			if(channel instanceof MessageChannel) {
				if(data.quiltUpdates.get().enabled) {
					helper.ephemeral(Embeds.success("Quilt Loader Updates", "Publishing channel got successfully changed."));
				}
				else {
					helper.ephemeral(Embeds.success("Quilt Loader Updates", "Quilt Loader updates publishing is now enabled! Make sure bot has permission to send messages in this channel otherwise it wont work."));
				}
				data.quiltUpdates.get().enable(channel.getId());
				data.quiltUpdates.save();
			}
		}
		else if(subcommand.equals("disable")) {
			ctx.deferReply().setEphemeral(true).queue();

			var guild = helper.guild;
			var member = helper.member;
			var data = ServerData.get(guild.getId());

			if(!data.permissions.get().hasAccessTo(member, Permissions.QUILT_UPDATES_DISABLE)) {
				helper.ephemeral(Embeds.error("Not enough permissions."));
				return;
			}

			if(!data.quiltUpdates.get().enabled) {
				helper.ephemeral(Embeds.error("Quilt Loader updates publishing is already disabled!"));
				return;
			}
			data.quiltUpdates.get().disable();
			data.quiltUpdates.save();
			helper.ephemeral(Embeds.success("Quilt Loader Updates", "Quilt Loader updates publishing is now disabled!"));
		}
	}
}
