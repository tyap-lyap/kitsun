package ru.pinkgoosik.kitsun.command.moderation;

import discord4j.core.object.entity.Member;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.kitsun.api.modrinth.ModrinthAPI;
import ru.pinkgoosik.kitsun.api.modrinth.ModrinthMod;
import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.instance.config.Config;
import ru.pinkgoosik.kitsun.instance.config.entity.ChangelogPublisherConfig;
import ru.pinkgoosik.kitsun.permission.AccessManager;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;

import java.util.Optional;

public class ChangelogPublisherAddCommand extends Command {

    @Override
    public String getName() {
        return "changelog publisher add";
    }

    @Override
    public String getDescription() {
        return "Adds changelog publisher of the certain Modrinth mod.";
    }

    @Override
    public String appendName(Config config) {
        return super.appendName(config) + " <mod slug> <channel id>";
    }

    @Override
    public void respond(CommandUseContext context) {
        Member member = context.getMember();
        RestChannel channel = context.getChannel();
        String modSlug = context.getFirstArg();
        String channelID = context.getSecondArg();
        AccessManager accessManager = context.getServerData().accessManager;
        Config config = context.getServerData().config;

        if (!accessManager.hasAccessTo(member, Permissions.CHANGELOG_PUBLISHER_ADD)) {
            channel.createMessage(Embeds.error("Not enough permissions.")).block();
            return;
        }

        if (modSlug.equals("empty")) {
            channel.createMessage(Embeds.error("You have not specified a mod slug!")).block();
            return;
        }

        if (channelID.equals("empty")) {
            channel.createMessage(Embeds.error("You have not specified a channel id!")).block();
            return;
        }

        for (var publisher : config.general.changelogPublishers) {
            if(publisher.channel.equals(channelID) && publisher.mod.equals(modSlug)) {
                channel.createMessage(Embeds.error("This channel already has a publisher of the " + modSlug + " mod.")).block();
                return;
            }
        }

        Optional<ModrinthMod> optionalMod = ModrinthAPI.getMod(modSlug);

        if(optionalMod.isPresent()) {
            ChangelogPublisherConfig publisherConfig = new ChangelogPublisherConfig(modSlug, channelID);

            config.general.changelogPublishers.add(publisherConfig);
            config.saveConfig();

            String text = "Changelog publisher for the mod " + modSlug + " got successfully added!";
            channel.createMessage(Embeds.success("Adding Changelog Publisher", text)).block();
        }else {
            channel.createMessage(Embeds.error("Mod " + modSlug + " is not found.")).block();
        }
    }
}
