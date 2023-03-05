package ru.pinkgoosik.kitsun.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.command.member.*;
import ru.pinkgoosik.kitsun.command.admin.*;
import ru.pinkgoosik.kitsun.command.next.ModUpdatesCommands;
import ru.pinkgoosik.kitsun.command.next.ModrinthCommand;
import ru.pinkgoosik.kitsun.feature.KitsunDebugger;
import ru.pinkgoosik.kitsun.cache.ServerData;
import ru.pinkgoosik.kitsun.util.Embeds;
import ru.pinkgoosik.kitsun.util.SelfUtils;

import java.util.ArrayList;
import java.util.List;

public class Commands {
	public static final List<Command> COMMANDS = new ArrayList<>();
	public static final List<CommandNext> COMMANDS_NEXT = new ArrayList<>();

	public static void init() {
//		add(new Help());

//		add(KitsunCmdPrefix.build());

		add(new PublisherAdd());
		add(new PublisherRemove());

		add(new MCUpdatesEnable());
		add(new MCUpdatesDisable());

		add(LoggerEnabling.enable());
		add(LoggerEnabling.disable());

//        add(AutoChannelsEnabling.enable());
//        add(AutoChannelsEnabling.disable());

		add(QuiltUpdatesEnabling.enable());
		add(QuiltUpdatesEnabling.disable());
		add(ModCardCommands.add());
		add(ModCardCommands.curseforge());
		add(ModCardCommands.modrinth());

//        add(new PermissionsList());
//        add(new PermissionGrant());
	}

	public static void initNext() {
		add(RegisterCommands.reg());
		add(RegisterCommands.unreg());
		add(ModUpdatesCommands.add());
		add(ModUpdatesCommands.remove());
		add(new ImportFabricCommand());
		add(new ImportQuiltCommand());
		add(new ModrinthCommand());
	}

	private static void add(Command command) {
		COMMANDS.add(command);
	}

	private static void add(CommandNext command) {
		COMMANDS_NEXT.add(command);
	}

	public static void onConnect() {
		Commands.initNext();

		ArrayList<CommandData> tlCommands = new ArrayList<>();
		ArrayList<CommandData> globalCommands = new ArrayList<>();

		Commands.COMMANDS_NEXT.forEach(command -> {
			var data = command.build();
			if(command.isTLExclusive()) {
				tlCommands.add(data);
			}
			else {
				globalCommands.add(data);
			}
		});

		Bot.jda.updateCommands().addCommands(globalCommands).queue();
		var guild = Bot.jda.getGuildById(854349856164020244L);

		if(guild != null) {
			guild.updateCommands().addCommands(tlCommands).queue();
		}
	}

	public static void onMessageCreate(MessageReceivedEvent event) {
		try {
			var serverId = event.getGuild().getId();
			var member = event.getMember();
			var message = event.getMessage();
			var channel = event.getChannel();
			ServerData serverData = ServerData.get(serverId);

			if(!event.getAuthor().isBot()) {
				proceed(message.getContentDisplay(), member, channel, serverData);
			}
		}
		catch(Exception e) {
			KitsunDebugger.ping("Failed to proceed commands event duo to an exception:\n" + e);
		}
	}

	private static void proceed(String content, Member member, MessageChannelUnion restChannel, ServerData serverData) {
		String commandPrefix = serverData.config.get().general.commandPrefix;
		String botPing = "<@" + SelfUtils.getId() + "> ";
		for(Command command : Commands.COMMANDS) {
			String replace = iterate(content, command, new String[]{commandPrefix, botPing});
			if(!replace.isBlank()) {
				String split = content.replace(replace, "");
				CommandUseContext context = new CommandUseContext(member, restChannel, splitToArgs(split), serverData);
				try {
					command.respond(context);
				}
				catch(Exception e) {
					if(e.getMessage().contains("Missing Permissions")) {
						member.getUser().openPrivateChannel().queue(privateChannel -> {
							if(privateChannel != null) {
								try {
									privateChannel.sendMessageEmbeds(Embeds.error("Bot doesn't have permission to send messages in this channel! If you are a server admin provide bot required permission.")).queue();
								}
								catch(Exception b) {
									KitsunDebugger.report("Failed to report to an user about missing permission duo to an exception:\n" + b);
								}
							}
						});

					}
					else {
						KitsunDebugger.ping("Failed to respond to command duo to an exception:\n" + e + "\nCommand: " + command.getName() + "\nArguments: " + context.args);
					}
				}
				return;
			}
		}
	}

	private static String iterate(String content, Command command, String[] prefixes) {
		for(String prefix : prefixes) {
			if(content.startsWith(prefix + command.getName())) return prefix + command.getName();
			for(String altName : command.getAltNames()) {
				if(!altName.isBlank()) {
					String start = prefix + altName;
					if(content.startsWith(start)) return start;
				}
			}
		}
		return "";
	}

	private static ArrayList<String> splitToArgs(String str) {
		str = str + " empty empty empty";
		String[] words = str.split(" ");
		ArrayList<String> filtered = new ArrayList<>();
		for(String word : words) {
			if(!word.isBlank()) filtered.add(word);
		}
		return filtered;
	}

}
