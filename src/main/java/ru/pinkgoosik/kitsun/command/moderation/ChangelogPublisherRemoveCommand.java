package ru.pinkgoosik.kitsun.command.moderation;

import discord4j.core.object.entity.Member;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.instance.config.Config;
import ru.pinkgoosik.kitsun.permission.AccessManager;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;

public class ChangelogPublisherRemoveCommand extends Command {

    @Override
    public String getName() {
        return "changelog publisher remove";
    }

    @Override
    public String getDescription() {
        return "Removes changelog publisher of the certain Modrinth Project.";
    }

    @Override
    public String appendName(Config config) {
        return super.appendName(config) + " <slug>";
    }

    @Override
    public void respond(CommandUseContext context) {
        Member member = context.getMember();
        RestChannel channel = context.getChannel();
        String slug = context.getFirstArg();
        AccessManager accessManager = context.getServerData().accessManager;
        Config config = context.getServerData().config;

        if (!accessManager.hasAccessTo(member, Permissions.CHANGELOG_PUBLISHER_REMOVE)) {
            channel.createMessage(Embeds.error("Not enough permissions.")).block();
            return;
        }

        if (slug.equals("empty")) {
            channel.createMessage(Embeds.error("You have not specified a project slug!")).block();
            return;
        }

        if (hasPublishers(slug, config)) {
            config.general.changelogPublishers.removeIf(modChangelogPublisherConfig -> modChangelogPublisherConfig.mod.equals(slug));
            String text = "All publisher of the `" + slug + "` project got removed.";
            channel.createMessage(Embeds.success("Removing Changelog Publisher", text)).block();
        }else {
            channel.createMessage(Embeds.error("`" + slug + "` project doesn't have any publishers.")).block();
        }
    }

    private static boolean hasPublishers(String modSlug, Config config) {
        for (var publisher : config.general.changelogPublishers) {
            if(publisher.mod.equals(modSlug)) return true;
        }
        return false;
    }
}
