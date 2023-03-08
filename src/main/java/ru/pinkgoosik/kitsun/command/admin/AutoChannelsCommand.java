package ru.pinkgoosik.kitsun.command.admin;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.cache.ServerData;
import ru.pinkgoosik.kitsun.command.CommandHelper;
import ru.pinkgoosik.kitsun.command.KitsunCommand;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;

import java.util.Objects;

public class AutoChannelsCommand extends KitsunCommand {

	@Override
	public String getName() {
		return "auto-channels";
	}

	@Override
	public String getDescription() {
		return "Enables auto-channels and links it to the specific voice channel.";
	}

	@Override
	public SlashCommandData build() {
		var data = Commands.slash(getName(), getDescription());
		data.addOption(OptionType.CHANNEL, "channel", "Voice channel that members should join to create a new channel.", true);
		return data;
	}

	@Override
	public void respond(SlashCommandInteractionEvent ctx, CommandHelper helper) {
		var channel = Objects.requireNonNull(ctx.getOption("channel")).getAsChannel();

		ctx.deferReply().setEphemeral(true).queue();

		ServerData dat = helper.serverData;
		var guild = helper.event.getGuild();
		var member = helper.event.getInteraction().getMember();

		if(guild != null && member != null) {
			if(!dat.permissions.get().hasAccessTo(member, Permissions.AUTO_CHANNELS_ENABLE)) {
				helper.ephemeral(Embeds.error("Not enough permissions."));
				return;
			}

			if(channel instanceof VoiceChannel vc) {
				var category = vc.getParentCategory();
				if(category != null) {
					var perms = category.getPermissionOverride(Objects.requireNonNull(Objects.requireNonNull(Bot.jda.getGuildById(dat.server)).getMemberById(Bot.jda.getSelfUser().getId())));
					if(perms != null && !perms.getAllowed().contains(Permission.ADMINISTRATOR)) {
						helper.ephemeral(Embeds.error("Bot doesn't have permission of administrator! It required for auto channels to work properly."));
						return;
					}
				}
				if(dat.autoChannels.get().enabled) {
					dat.autoChannels.get().enable(channel.getId());
					dat.autoChannels.save();
					helper.ephemeral(Embeds.success("Auto Channels Enabling", "Auto channels parent channel successfully changed!"));
				}
				else {
					dat.autoChannels.get().enable(channel.getId());
					dat.autoChannels.save();
					helper.ephemeral(Embeds.success("Auto Channels Enabling", "Auto channels are now enabled!"));
				}
			}
			else {
				helper.ephemeral(Embeds.error("Channel you specified isn't a voice channel!"));
			}
		}
	}

}
