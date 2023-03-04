package ru.pinkgoosik.kitsun.command.admin;

import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.ChannelUtils;
import ru.pinkgoosik.kitsun.util.Embeds;

public class MCUpdatesEnable extends Command {

	@Override
	public String getName() {
		return "mc-updates enable";
	}

	@Override
	public String getDescription() {
		return "Enables Minecraft updates publishing and links it to the specific channel.";
	}

	@Override
	public String appendArgs() {
		return " <channel id>";
	}

	@Override
	public void respond(CommandUseContext ctx) {
		if(disallowed(ctx, Permissions.MC_UPDATES_ENABLE)) return;

		String channelIdArg = ctx.args.get(0);

		if(ctx.serverData.mcUpdates.get().enabled) {
			ctx.channel.sendMessageEmbeds(Embeds.error("The Minecraft updates publishing is already enabled!")).queue();
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
			ctx.channel.sendMessageEmbeds(Embeds.error("You can't link mc updates to a voice channel!")).queue();
			return;
		}
		ctx.serverData.mcUpdates.get().enable(channelIdArg);
		ctx.serverData.mcUpdates.save();
		ctx.channel.sendMessageEmbeds(Embeds.success("Minecraft Updates Enabling", "The Minecraft updates publishing is now enabled! Make sure bot has permission to send messages in this channel otherwise it wont work.")).queue();
	}
}
