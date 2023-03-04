package ru.pinkgoosik.kitsun.command.admin;

import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandBuilder;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;

public class KitsunCmdPrefix {

	public static Command build() {
		return CommandBuilder.create("kitsun-cmd-prefix")
				.description("Changes Kitsun command prefix.")
				.args("<prefix>")
				.requires(Permissions.KITSUN_CMD_PREFIX)
				.respond(ctx -> {
					String prefixArg = ctx.args.get(0);
					if(ctx.serverData.config.get().general.commandPrefix.equals(prefixArg)) {
						ctx.channel.sendMessageEmbeds(Embeds.error("The prefix is " + prefixArg + " already!")).queue();
						return;
					}
					if(prefixArg.equals("empty")) {
						ctx.channel.sendMessageEmbeds(Embeds.error("You have not specified prefix!")).queue();
						return;
					}
					ctx.serverData.config.get().general.commandPrefix = prefixArg;
					ctx.serverData.config.save();
					ctx.channel.sendMessageEmbeds(Embeds.success("Prefix Changing", "Prefix successfully changed!")).queue();

				}).build();
	}
}
