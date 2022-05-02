package ru.pinkgoosik.kitsun.command.moderation;

import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;

public class MCUpdatesDisable extends Command {

    @Override
    public String getName() {
        return "mc-updates disable";
    }

    @Override
    public String getDescription() {
        return "Disables Minecraft updates publishing.";
    }

    @Override
    public void respond(CommandUseContext ctx) {
        if(disallowed(ctx, Permissions.MC_UPDATES_DISABLE)) return;

        if (!ctx.serverData.mcUpdatesPublisher.enabled) {
            ctx.channel.createMessage(Embeds.error("The Minecraft updates publishing is already disabled!")).block();
            return;
        }
        ctx.serverData.mcUpdatesPublisher.disable();
        ctx.serverData.save();
        ctx.channel.createMessage(Embeds.success("Minecraft Updates Disabling", "The Minecraft updates publishing is now disabled!")).block();
    }
}