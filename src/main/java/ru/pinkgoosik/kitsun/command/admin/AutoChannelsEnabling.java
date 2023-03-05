package ru.pinkgoosik.kitsun.command.admin;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandBuilder;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.ChannelUtils;
import ru.pinkgoosik.kitsun.util.Embeds;

import java.util.Objects;

@Deprecated
public class AutoChannelsEnabling {

	public static Command enable() {
		return CommandBuilder.create("auto-channels enable")
				.description("Enables auto-channels and links it to the specific voice channel.")
				.args("<channel id>")
				.requires(Permissions.AUTO_CHANNELS_ENABLE)
				.respond(ctx -> {
					String channelIdArg = ctx.args.get(0);
					if(ctx.serverData.autoChannels.get().enabled) {
						ctx.channel.sendMessageEmbeds(Embeds.error("Auto channels are already enabled!")).queue();
						return;
					}
					if(channelIdArg.equals("empty")) {
						ctx.channel.sendMessageEmbeds(Embeds.error("You have not specified a channel id!")).queue();
						return;
					}
					if(!ChannelUtils.exist(ctx.serverData.server, channelIdArg)) {
						ctx.channel.sendMessageEmbeds(Embeds.error("Such channel doesn't exist!")).queue();
						return;
					}
					if(Bot.jda.getGuildChannelById(channelIdArg) instanceof VoiceChannel vc) {
						var category = vc.getParentCategory();
						if(category != null) {
							var perms = category.getPermissionOverride(Objects.requireNonNull(Objects.requireNonNull(Bot.jda.getGuildById(ctx.serverData.server)).getMemberById(Bot.jda.getSelfUser().getId())));
							if(perms != null && !perms.getAllowed().contains(Permission.ADMINISTRATOR)) {
								ctx.channel.sendMessageEmbeds(Embeds.error("Bot doesn't have permission of administrator! It required for auto channels to work properly.")).queue();
								return;
							}
						}
					}
					if(!ChannelUtils.isVoiceChannel(ctx.serverData.server, channelIdArg)) {
						ctx.channel.sendMessageEmbeds(Embeds.error("A channel you specified isn't a voice channel!")).queue();
						return;
					}
					ctx.serverData.autoChannels.get().enable(channelIdArg);
					ctx.serverData.autoChannels.save();
					ctx.channel.sendMessageEmbeds(Embeds.success("Auto Channels Enabling", "Auto channels are now enabled!")).queue();
				}).build();
	}

	public static Command disable() {
		return CommandBuilder.create("auto-channels disable")
				.description("Disables auto channels.")
				.requires(Permissions.AUTO_CHANNELS_DISABLE)
				.respond(ctx -> {
					if(!ctx.serverData.autoChannels.get().enabled) {
						ctx.channel.sendMessageEmbeds(Embeds.error("Auto channels are already disabled!")).queue();
					}
					else {
						ctx.serverData.autoChannels.get().disable();
						ctx.serverData.autoChannels.save();
						ctx.channel.sendMessageEmbeds(Embeds.success("Auto Channels Disabling", "Auto channels are now disabled!")).queue();
					}
				}).build();
	}
}
