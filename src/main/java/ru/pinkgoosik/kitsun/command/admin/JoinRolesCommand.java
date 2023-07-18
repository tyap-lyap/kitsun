package ru.pinkgoosik.kitsun.command.admin;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import ru.pinkgoosik.kitsun.cache.ServerData;
import ru.pinkgoosik.kitsun.command.CommandHelper;
import ru.pinkgoosik.kitsun.command.KitsunCommand;
import ru.pinkgoosik.kitsun.util.Embeds;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JoinRolesCommand extends KitsunCommand {

	@Override
	public String getName() {
		return "join-roles";
	}

	@Override
	public String getDescription() {
		return "Join roles.";
	}

	@Override
	public void build(SlashCommandData data) {
		data.addSubcommands(new SubcommandData("add", "Adds join role")
			.addOption(OptionType.ROLE,"role", "The role", true));

		data.addSubcommands(new SubcommandData("remove", "Removes john role")
			.addOption(OptionType.ROLE,"role", "The role", true));
	}

	@Override
	public void respond(SlashCommandInteractionEvent ctx, CommandHelper helper) {
		String subcommand = Objects.requireNonNull(helper.event.getSubcommandName());
		ctx.deferReply().setEphemeral(true).queue();
		var guild = helper.guild;
		var member = helper.member;
		var data = ServerData.get(guild.getId());
		var role = Objects.requireNonNull(ctx.getOption("role")).getAsRole();

		if(!member.getPermissions().contains(Permission.ADMINISTRATOR)) {
			helper.ephemeral(Embeds.error("Not enough permissions."));
			return;
		}

		if(subcommand.equals("add")) {
			var joinRoles = new ArrayList<>(List.of(data.config.get().general.joinRoles));
			joinRoles.add(role.getId());
			data.config.get().general.joinRoles = joinRoles.toArray(new String[]{});
			data.config.save();

			String text = "The join role got successful registered.";
			helper.ephemeral(Embeds.success("Join Roles", text));
		}
		else if(subcommand.equals("remove")) {
			var joinRoles = new ArrayList<>(List.of(data.config.get().general.joinRoles));
			if(joinRoles.contains(role.getId())) {
				joinRoles.removeIf(r -> r.equals(role.getId()));
				data.config.get().general.joinRoles = joinRoles.toArray(new String[]{});
				data.config.save();
				String text = "The join role got successful removed.";
				helper.ephemeral(Embeds.success("Join Roles", text));
			}
			else {
				helper.ephemeral(Embeds.error("This role is not a join role."));
			}
		}
	}
}
