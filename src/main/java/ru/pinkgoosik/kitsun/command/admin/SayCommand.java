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
		data.addOption(OptionType.STRING, "reference", "The message's id to reply to", false);
		return data;
	}

	@Override
	public void respond(SlashCommandInteractionEvent ctx, CommandHelper helper) {
		String message = Objects.requireNonNull(ctx.getOption("message")).getAsString();
		var reference = ctx.getOption("reference");
		var channel = helper.event.getChannel();
		var member = helper.event.getInteraction().getMember();
		var guild = helper.event.getGuild();
		ctx.deferReply().setEphemeral(true).queue();

		if(member != null && guild != null) {
			if(!member.getPermissions().contains(Permission.ADMINISTRATOR)) {
				helper.ephemeral(Embeds.error("Not enough permissions."));
				return;
			}
			if(reference != null) {
				var history = channel.getHistoryAround(reference.getAsString(), 10).complete();
				var messageToReply = history.getMessageById(reference.getAsString());
				if(messageToReply != null) {
					messageToReply.reply(message).queue();
					ServerData.get(guild.getId()).logger.get(serverLogger -> {
						if(serverLogger.enabled) {
							serverLogger.log(Embeds.info(member.getEffectiveName() + " used the say command and said", message));
						}
					});
					helper.ephemeral(Embeds.success("Success", ""));
				}
				else {
					helper.ephemeral(Embeds.error(""));
				}
			}
			else {
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
}
