package ru.pinkgoosik.kitsun.command.admin;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import ru.pinkgoosik.kitsun.api.Modrinth;
import ru.pinkgoosik.kitsun.api.curseforge.CurseForgeAPI;
import ru.pinkgoosik.kitsun.cache.ServerData;
import ru.pinkgoosik.kitsun.command.CommandHelper;
import ru.pinkgoosik.kitsun.command.KitsunCommand;
import ru.pinkgoosik.kitsun.feature.ModCard;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;

import java.util.ArrayList;
import java.util.List;

public class ModCardCommand extends KitsunCommand {

	@Override
	public String getName() {
		return "mod-card";
	}

	@Override
	public String getDescription() {
		return "Creates a mod card of the certain project which updates every now and then.";
	}

	@Override
	public SlashCommandData build() {
		var data = Commands.slash(getName(), getDescription());

		data.addOption(OptionType.STRING, "modrinth-slug", "Modrinth project slug. (Example: fabric-api).", false);
		data.addOption(OptionType.STRING, "curseforge-id", "CurseForge mod id (Example: 306612).", false);

		return data;
	}

	@Override
	public void respond(SlashCommandInteractionEvent ctx, CommandHelper helper) {
		ctx.deferReply().setEphemeral(true).queue();

		ServerData dat = helper.serverData;
		var modrinthSlug = ctx.getOption("modrinth-slug");
		var curseforgeId = ctx.getOption("curseforge-id");
		var member = ctx.getMember();
		var guild = ctx.getGuild();
		var channel = ctx.getChannel();

		if(member != null && guild != null) {
			if(!dat.permissions.get().hasAccessTo(member, Permissions.MOD_CARDS_MANAGEMENT)) {
				helper.ephemeral(Embeds.error("Not enough permissions."));
				return;
			}

			if(modrinthSlug == null && curseforgeId == null) {
				helper.ephemeral(Embeds.error("You should specify at least one platform."));
				return;
			}

			if(modrinthSlug != null && curseforgeId != null) {
				var curseforgeMod = CurseForgeAPI.getMod(curseforgeId.getAsString());
				var modrinthProject = Modrinth.getProject(modrinthSlug.getAsString());

				if(modrinthProject.isEmpty()) {
					helper.ephemeral(Embeds.error("Such Modrinth project doesn't exist!"));
					return;
				}

				if(curseforgeMod.isEmpty()) {
					helper.ephemeral(Embeds.error("Such CurseForge mod doesn't exist!"));
					return;
				}

				ModCard card = new ModCard(guild.getId(), curseforgeMod.get(), modrinthProject.get(), channel.getId(), "");

				channel.sendMessageEmbeds(ModCard.createEmbed(modrinthProject.get(), curseforgeMod.get())).queue(message -> {
					card.message = message.getId();
					var old = helper.serverData.modCards.get();
					var newOnes = new ArrayList<>(List.of(helper.serverData.modCards.get()));
					newOnes.add(card);
					helper.serverData.modCards.set(newOnes.toArray(old));
					helper.serverData.modCards.save();
				});
				helper.ephemeral(Embeds.success("Mod Card", "New mod card got successfully created."));
			}
			else if (modrinthSlug != null) {
				var modrinthProject = Modrinth.getProject(modrinthSlug.getAsString());

				if(modrinthProject.isEmpty()) {
					helper.ephemeral(Embeds.error("Such Modrinth project doesn't exist!"));
					return;
				}
				ModCard card = new ModCard(guild.getId(), null, modrinthProject.get(), channel.getId(), "");

				channel.sendMessageEmbeds(ModCard.createEmbed(modrinthProject.get(), null)).queue(message -> {
					card.message = message.getId();
					var old = helper.serverData.modCards.get();
					var newOnes = new ArrayList<>(List.of(helper.serverData.modCards.get()));
					newOnes.add(card);
					helper.serverData.modCards.set(newOnes.toArray(old));
					helper.serverData.modCards.save();
				});
				helper.ephemeral(Embeds.success("Mod Card", "New mod card got successfully created."));
			}
			else {
				var curseforgeMod = CurseForgeAPI.getMod(curseforgeId.getAsString());

				if(curseforgeMod.isEmpty()) {
					helper.ephemeral(Embeds.error("Such CurseForge mod doesn't exist!"));
					return;
				}
				ModCard card = new ModCard(guild.getId(), curseforgeMod.get(), null, channel.getId(), "");

				channel.sendMessageEmbeds(ModCard.createEmbed(null, curseforgeMod.get())).queue(message -> {
					card.message = message.getId();
					var old = helper.serverData.modCards.get();
					var newOnes = new ArrayList<>(List.of(helper.serverData.modCards.get()));
					newOnes.add(card);
					helper.serverData.modCards.set(newOnes.toArray(old));
					helper.serverData.modCards.save();
				});
				helper.ephemeral(Embeds.success("Mod Card", "New mod card got successfully created."));
			}

		}
	}
}
