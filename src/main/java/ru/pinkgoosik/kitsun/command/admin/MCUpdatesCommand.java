package ru.pinkgoosik.kitsun.command.admin;

import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import ru.pinkgoosik.kitsun.cache.ServerData;
import ru.pinkgoosik.kitsun.command.CommandHelper;
import ru.pinkgoosik.kitsun.command.KitsunCommand;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;

import java.util.Objects;

public class MCUpdatesCommand extends KitsunCommand {

	@Override
	public String getName() {
		return "mc-updates";
	}

	@Override
	public String getDescription() {
		return "Toggles Minecraft updates publishing and links it to a specified channel.";
	}

	@Override
	public SlashCommandData build() {
		var data = Commands.slash(getName(), getDescription());

		data.addSubcommands(new SubcommandData("enable", "Enables Minecraft updates publishing and links it to a specified channel.")
				.addOption(OptionType.CHANNEL, "channel", "Channel where updates gonna be published.", true));

		data.addSubcommands(new SubcommandData("disable", "Disables Minecraft updates publishing."));

		return data;
	}

	@Override
	public void respond(SlashCommandInteractionEvent ctx, CommandHelper helper) {
		String subcommand = Objects.requireNonNull(helper.event.getSubcommandName());

		if(subcommand.equals("enable")) {
			var channel = Objects.requireNonNull(ctx.getOption("channel")).getAsChannel();
			ctx.deferReply().setEphemeral(true).queue();

			var guild = helper.event.getGuild();
			var member = helper.event.getInteraction().getMember();
			if(guild != null && member != null) {
				var data = ServerData.get(guild.getId());

				if(!data.permissions.get().hasAccessTo(member, Permissions.MC_UPDATES_ENABLE)) {
					helper.ephemeral(Embeds.error("Not enough permissions."));
					return;
				}

				if(channel instanceof VoiceChannel) {
					helper.ephemeral(Embeds.error("You can't link publisher to a voice channel!"));
					return;
				}

				if(channel instanceof MessageChannel) {
					if(data.mcUpdates.get().enabled) {
						helper.ephemeral(Embeds.success("Minecraft Updates Enabling", "Publishing channel got successfully changed."));
					}
					else {
						helper.ephemeral(Embeds.success("Minecraft Updates Enabling", "The Minecraft updates publishing is now enabled! Make sure bot has permission to send messages in this channel otherwise it wont work."));
					}
					data.mcUpdates.get().enable(channel.getId());
					data.mcUpdates.save();
				}
			}
		}
		else if(subcommand.equals("disable")) {
			ctx.deferReply().setEphemeral(true).queue();

			var guild = helper.event.getGuild();
			var member = helper.event.getInteraction().getMember();
			if(guild != null && member != null) {
				var data = ServerData.get(guild.getId());

				if(!data.permissions.get().hasAccessTo(member, Permissions.MC_UPDATES_DISABLE)) {
					helper.ephemeral(Embeds.error("Not enough permissions."));
					return;
				}

				if(!data.mcUpdates.get().enabled) {
					helper.ephemeral(Embeds.error("The Minecraft updates publishing is already disabled!"));
					return;
				}
				data.mcUpdates.get().disable();
				data.mcUpdates.save();
				helper.ephemeral(Embeds.success("Minecraft Updates Disabling", "The Minecraft updates publishing is now disabled!"));
			}
		}
	}
}
