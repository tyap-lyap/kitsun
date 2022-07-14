package ru.pinkgoosik.kitsun.command.admin;

import discord4j.common.util.Snowflake;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.api.curseforge.CurseForgeAPI;
import ru.pinkgoosik.kitsun.api.modrinth.ModrinthAPI;
import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandBuilder;
import ru.pinkgoosik.kitsun.feature.ModCard;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;

import java.util.ArrayList;
import java.util.List;

public class ModCardCommands {

    public static Command add() {
        return CommandBuilder.create("mod-card")
                .args("<modrinth slug> <curseforge id>")
                .description("Creates mod card of the of the certain project which updates every now and then.")
                .requires(Permissions.MOD_CARDS_MANAGEMENT)
                .respond(ctx -> {
                    String modrinthSlugArg = ctx.args.get(0);
                    String curseforgeIdArg = ctx.args.get(1);
                    String channelId = ctx.channel.getId().asString();
                    String serverId = ctx.serverData.server;

                    if (modrinthSlugArg.equals("empty")) {
                        ctx.channel.createMessage(Embeds.error("You have not specified a modrinth project slug!")).block();
                        return;
                    }
                    if (curseforgeIdArg.equals("empty")) {
                        ctx.channel.createMessage(Embeds.error("You have not specified a curseforge mod id!")).block();
                        return;
                    }
                    var curseforgeMod = CurseForgeAPI.getMod(curseforgeIdArg);
                    var modrinthProject = ModrinthAPI.getProject(modrinthSlugArg);

                    if(curseforgeMod.isEmpty()) {
                        ctx.channel.createMessage(Embeds.error("Such curseforge mod doesn't exist!")).block();
                        return;
                    }
                    if(modrinthProject.isEmpty()) {
                        ctx.channel.createMessage(Embeds.error("Such modrinth project doesn't exist!")).block();
                        return;
                    }

                    ModCard card = new ModCard(serverId, curseforgeMod.get(), modrinthProject.get(), channelId, "");
                    var messageData = Bot.rest.getChannelById(Snowflake.of(channelId)).createMessage(card.createEmbed(modrinthProject.get(), curseforgeMod.get())).block();
                    if(messageData != null) {
                        card.message = messageData.id().asString();
                        var old = ctx.serverData.modCards.get();
                        var newOnes = new ArrayList<>(List.of(ctx.serverData.modCards.get()));
                        newOnes.add(card);
                        ctx.serverData.modCards.set(newOnes.toArray(old));
                        ctx.serverData.modCards.save();

                        String text = "Mod card for the project `" + modrinthSlugArg + "` got successfully created!";
                        ctx.channel.createMessage(Embeds.success("Creating Mod Card", text)).block();
                    }
                })
                .build();
    }
}
