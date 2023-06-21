package ru.pinkgoosik.kitsun.command.admin;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import ru.pinkgoosik.kitsun.cache.ServerData;
import ru.pinkgoosik.kitsun.command.CommandHelper;
import ru.pinkgoosik.kitsun.command.KitsunCommand;
import ru.pinkgoosik.kitsun.util.Embeds;

import java.util.Objects;

public class SayCommand extends KitsunCommand {

	@Override
	public String getName() {
		return "say";
	}

	@Override
	public String getDescription() {
		return "Sends a message on behalf of a bot";
	}

	@Override
	public SlashCommandData build() {
		var data = Commands.slash(getName(), getDescription());
		data.addOption(OptionType.STRING, "message", "The message", true);
		return data;
	}

	@Override
	public void respond(SlashCommandInteractionEvent ctx, CommandHelper helper) {
		String message = Objects.requireNonNull(ctx.getOption("message")).getAsString();
		var channel = helper.event.getChannel();
		var member = helper.event.getInteraction().getMember();
		var guild = helper.event.getGuild();
		ctx.deferReply().setEphemeral(true).queue();

		if(member != null && guild != null) {
			if(!member.getPermissions().contains(Permission.ADMINISTRATOR)) {
				helper.ephemeral(Embeds.error("Not enough permissions."));
				return;
			}

			channel.sendMessage(message).queue();
			ServerData.get(guild.getId()).logger.get(serverLogger -> {
				if(serverLogger.enabled) {
					serverLogger.log(Embeds.info(member.getEffectiveName() + " used the say command and said", message));
				}
			});
			helper.ephemeral(Embeds.success("Success", ""));
		}
	}
}
