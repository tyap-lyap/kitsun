package ru.pinkgoosik.kitsun.command;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.command.member.*;
import ru.pinkgoosik.kitsun.command.admin.*;

import java.util.ArrayList;
import java.util.List;

public class KitsunCommands {
	public static final List<KitsunCommand> COMMANDS = new ArrayList<>();

	public static void init() {
		add(RegisterCommands.reg());
		add(RegisterCommands.unreg());
		add(new ModUpdatesCommand());
		add(new ImportFabricCommand());
		add(new ImportQuiltCommand());
		add(new ModrinthCommand());
		add(new AutoChannelsCommand());
		add(new EmbedCommand());
		add(new ModCardCommand());
		add(new MCUpdatesCommand());
		add(new LoggerCommand());
		add(new QuiltUpdatesCommand());
		add(new AutoReactionCommand());
		add(new SayCommand());
	}

	private static void add(KitsunCommand command) {
		COMMANDS.add(command);
	}

	public static void onConnect() {
		KitsunCommands.init();

		ArrayList<CommandData> tlCommands = new ArrayList<>();
		ArrayList<CommandData> globalCommands = new ArrayList<>();

		KitsunCommands.COMMANDS.forEach(command -> {
			var data = Commands.slash(command.getName(), command.getDescription());
			command.build(data);

			if(command.isTLExclusive()) {
				tlCommands.add(data);
			}
			else {
				globalCommands.add(data);
			}
		});

		Bot.jda.updateCommands().addCommands(globalCommands).queue();
		var guild = Bot.getGuild("854349856164020244");

		if(guild != null) {
			guild.updateCommands().addCommands(tlCommands).queue();
		}
	}
}
