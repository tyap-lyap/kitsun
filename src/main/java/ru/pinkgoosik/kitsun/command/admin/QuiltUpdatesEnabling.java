package ru.pinkgoosik.kitsun.command.admin;

import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandBuilder;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.ChannelUtils;
import ru.pinkgoosik.kitsun.util.Embeds;

public class QuiltUpdatesEnabling {

	public static Command enable() {
		return CommandBuilder.create("quilt-updates enable")
				.description("Enables Quilt Loader updates publishing and links it to the specific channel.")
				.args("<channel id>")
				.requires(Permissions.QUILT_UPDATES_ENABLE)
				.respond(ctx -> {
					String channelIdArg = ctx.args.get(0);
					if(ctx.serverData.quiltUpdates.get().enabled) {
						ctx.channel.sendMessageEmbeds(Embeds.error("Quilt Loader updates publishing is already enabled!")).queue();
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
					if(ChannelUtils.isVoiceChannel(ctx.serverData.server, channelIdArg)) {
						ctx.channel.sendMessageEmbeds(Embeds.error("You can't link quilt updates to a voice channel!")).queue();
						return;
					}
					ctx.serverData.quiltUpdates.get().enable(channelIdArg);
					ctx.serverData.quiltUpdates.save();
					ctx.channel.sendMessageEmbeds(Embeds.success("Quilt Loader Updates", "Quilt Loader updates publishing is now enabled! Make sure bot has permission to send messages in this channel otherwise it wont work.")).queue();
				}).build();
	}

	public static Command disable() {
		return CommandBuilder.create("quilt-updates disable")
				.description("Disables Quilt Loader updates publishing.")
				.requires(Permissions.QUILT_UPDATES_DISABLE)
				.respond(ctx -> {
					if(!ctx.serverData.quiltUpdates.get().enabled) {
						ctx.channel.sendMessageEmbeds(Embeds.error("Quilt Loader updates publishing is already disabled!")).queue();
					}
					else {
						ctx.serverData.quiltUpdates.get().disable();
						ctx.serverData.quiltUpdates.get().latestVersion = "";
						ctx.serverData.quiltUpdates.save();
						ctx.channel.sendMessageEmbeds(Embeds.success("Quilt Loader Updates", "Quilt Loader updates publishing is now disabled!")).queue();
					}
				}).build();
	}

}
