package ru.pinkgoosik.kitsun.command.member;

import ru.pinkgoosik.kitsun.command.KitsunCommands;
import ru.pinkgoosik.kitsun.config.ServerConfig;

public class Help {

//	@Override
//	public String getName() {
//		return "help";
//	}
//
//	@Override
//	public String[] getAltNames() {
//		return new String[]{"commands"};
//	}
//
//	@Override
//	public String getDescription() {
//		return "Sends list of available commands.";
//	}
//
//	@Override
//	public String appendName(ServerConfig config) {
//		String name = "**`" + config.general.commandPrefix + this.getName();
//		return name + " <page>`** or **`" + config.general.commandPrefix + "commands <page>`**";
//	}
//
//	@Override
//	public void respond(CommandUseContext ctx) {
//		if(disallowed(ctx, Permissions.HELP)) return;
//		String page = ctx.args.get(0);
//		ctx.channel.sendMessageEmbeds(Embeds.info("Available Commands", build(page, ctx.config))).queue();
//	}

	private String build(String arg, ServerConfig config) {
		int page = stringToInt(arg);
		int size = KitsunCommands.COMMANDS.size();
		int first = ((3 * page) - 3) + (page - 1);
		int last = 3 * page + (page - 1);
		StringBuilder text = new StringBuilder();

		for(int i = first; i <= last; i++) {
			if(size >= i + 1) {
				text.append("/").append(KitsunCommands.COMMANDS.get(i).getName()).append("\n");
				text.append(KitsunCommands.COMMANDS.get(i).getDescription()).append("\n \n");
			}
		}
		return text.toString();
	}

	private int stringToInt(String arg) {
		int page = 1;
		if(arg.equals("2") || arg.equals("two") || arg.equals("second")) page = 2;
		if(arg.equals("3") || arg.equals("three") || arg.equals("third")) page = 3;
		if(arg.equals("4") || arg.equals("four") || arg.equals("fourth")) page = 4;
		if(arg.equals("5") || arg.equals("five") || arg.equals("fifth")) page = 5;
		if(arg.equals("6") || arg.equals("six") || arg.equals("sixth")) page = 6;
		if(arg.equals("7") || arg.equals("seven") || arg.equals("seventh")) page = 7;
		return page;
	}
}
