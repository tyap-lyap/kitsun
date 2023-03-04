package ru.pinkgoosik.kitsun.command.admin;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.api.curseforge.CurseForgeAPI;
import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandBuilder;
import ru.pinkgoosik.kitsun.feature.ModCard;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;
import ru.pinkgoosik.kitsun.api.Modrinth;

import java.util.ArrayList;
import java.util.List;

public class ModCardCommands {

	public static Command add() {
		return CommandBuilder.create("mod-card create")
				.args("<modrinth slug> <curseforge id>")
				.description("Creates a mod card of the certain project which updates every now and then.")
				.requires(Permissions.MOD_CARDS_MANAGEMENT)
				.respond(ctx -> {
					String modrinthSlugArg = ctx.args.get(0);
					String curseforgeIdArg = ctx.args.get(1);
					String channelId = ctx.channel.getId();
					String serverId = ctx.serverData.server;

					if(modrinthSlugArg.equals("empty")) {
						ctx.channel.sendMessageEmbeds(Embeds.error("You have not specified a modrinth project slug!")).queue();
						return;
					}
					if(curseforgeIdArg.equals("empty")) {
						ctx.channel.sendMessageEmbeds(Embeds.error("You have not specified a curseforge mod id!")).queue();
						return;
					}
					var curseforgeMod = CurseForgeAPI.getMod(curseforgeIdArg);
					var modrinthProject = Modrinth.getProject(modrinthSlugArg);

					if(modrinthProject.isEmpty()) {
						ctx.channel.sendMessageEmbeds(Embeds.error("Such modrinth project doesn't exist!")).queue();
						return;
					}

					if(curseforgeMod.isEmpty()) {
						ctx.channel.sendMessageEmbeds(Embeds.error("Such curseforge mod doesn't exist!")).queue();
						return;
					}

					ModCard card = new ModCard(serverId, curseforgeMod.get(), modrinthProject.get(), channelId, "");

					if(Bot.jda.getGuildChannelById(channelId) instanceof TextChannel channel) {
						channel.sendMessageEmbeds(card.createEmbed(modrinthProject.get(), curseforgeMod.get())).queue(message -> {
							card.message = message.getId();
							var old = ctx.serverData.modCards.get();
							var newOnes = new ArrayList<>(List.of(ctx.serverData.modCards.get()));
							newOnes.add(card);
							ctx.serverData.modCards.set(newOnes.toArray(old));
							ctx.serverData.modCards.save();
						});
					}
				})
				.build();
	}

	public static Command curseforge() {
		return CommandBuilder.create("curseforge-card create")
				.args("<curseforge id>")
				.description("Creates a mod card of the certain curseforge mod which updates every now and then.")
				.requires(Permissions.MOD_CARDS_MANAGEMENT)
				.respond(ctx -> {
					String curseforgeIdArg = ctx.args.get(0);
					String channelId = ctx.channel.getId();
					String serverId = ctx.serverData.server;

					if(curseforgeIdArg.equals("empty")) {
						ctx.channel.sendMessageEmbeds(Embeds.error("You have not specified a curseforge mod id!")).queue();
						return;
					}
					var curseforgeMod = CurseForgeAPI.getMod(curseforgeIdArg);

					if(curseforgeMod.isEmpty()) {
						ctx.channel.sendMessageEmbeds(Embeds.error("Such curseforge mod doesn't exist!")).queue();
						return;
					}

					ModCard card = new ModCard(serverId, curseforgeMod.get(), null, channelId, "");

					if(Bot.jda.getGuildChannelById(channelId) instanceof TextChannel channel) {
						channel.sendMessageEmbeds(card.createEmbed(null, curseforgeMod.get())).queue(message -> {
							card.message = message.getId();
							var old = ctx.serverData.modCards.get();
							var newOnes = new ArrayList<>(List.of(ctx.serverData.modCards.get()));
							newOnes.add(card);
							ctx.serverData.modCards.set(newOnes.toArray(old));
							ctx.serverData.modCards.save();
						});
					}
				})
				.build();
	}

	public static Command modrinth() {
		return CommandBuilder.create("modrinth-card create")
				.args("<modrinth slug>")
				.description("Creates a mod card of the certain modrinth project which updates every now and then.")
				.requires(Permissions.MOD_CARDS_MANAGEMENT)
				.respond(ctx -> {
					String modrinthSlugArg = ctx.args.get(0);
					String channelId = ctx.channel.getId();
					String serverId = ctx.serverData.server;

					if(modrinthSlugArg.equals("empty")) {
						ctx.channel.sendMessageEmbeds(Embeds.error("You have not specified a modrinth project slug!")).queue();
						return;
					}
					var modrinthProject = Modrinth.getProject(modrinthSlugArg);

					if(modrinthProject.isEmpty()) {
						ctx.channel.sendMessageEmbeds(Embeds.error("Such modrinth project doesn't exist!")).queue();
						return;
					}

					ModCard card = new ModCard(serverId, null, modrinthProject.get(), channelId, "");

					if(Bot.jda.getGuildChannelById(channelId) instanceof TextChannel channel) {
						channel.sendMessageEmbeds(card.createEmbed(modrinthProject.get(), null)).queue(message -> {
							card.message = message.getId();
							var old = ctx.serverData.modCards.get();
							var newOnes = new ArrayList<>(List.of(ctx.serverData.modCards.get()));
							newOnes.add(card);
							ctx.serverData.modCards.set(newOnes.toArray(old));
							ctx.serverData.modCards.save();
						});
					}
				})
				.build();
	}
}
