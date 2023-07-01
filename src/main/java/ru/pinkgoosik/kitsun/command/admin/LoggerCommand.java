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

public class LoggerCommand extends KitsunCommand {

	@Override
	public String getName() {
		return "logger";
	}

	@Override
	public String getDescription() {
		return "Toggles logger and links it to a specified channel.";
	}

	@Override
	public void build(SlashCommandData data) {
		data.addSubcommands(new SubcommandData("enable", "Enables logger and links it to a specified channel.")
				.addOption(OptionType.CHANNEL, "channel", "Channel where logs gonna be sent.", true));

		data.addSubcommands(new SubcommandData("disable", "Disables logger."));
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

			if(!data.permissions.get().hasAccessTo(member, Permissions.LOGGER_ENABLE)) {
				helper.ephemeral(Embeds.error("Not enough permissions."));
				return;
			}

			if(channel instanceof VoiceChannel) {
				helper.ephemeral(Embeds.error("You can't link logger to a voice channel!"));
				return;
			}

			if(channel instanceof MessageChannel) {
				if(data.logger.get().enabled) {
					helper.ephemeral(Embeds.success("Logger Enabling", "Logger channel got successfully changed."));
				}
				else {
					helper.ephemeral(Embeds.success("Logger Enabling", "The logger is now enabled! Make sure bot has permission to send messages in this channel otherwise it wont work."));
				}
				data.logger.get().enable(channel.getId());
				data.logger.save();
			}
		}
		else if(subcommand.equals("disable")) {
			ctx.deferReply().setEphemeral(true).queue();

			var guild = helper.guild;
			var member = helper.member;
			var data = ServerData.get(guild.getId());

			if(!data.permissions.get().hasAccessTo(member, Permissions.LOGGER_DISABLE)) {
				helper.ephemeral(Embeds.error("Not enough permissions."));
				return;
			}

			if(!data.logger.get().enabled) {
				helper.ephemeral(Embeds.error("The logger is already disabled!"));
				return;
			}
			data.logger.get().disable();
			data.logger.save();
			helper.ephemeral(Embeds.success("Logger Disabling", "The logger is now disabled!"));
		}
	}
}
