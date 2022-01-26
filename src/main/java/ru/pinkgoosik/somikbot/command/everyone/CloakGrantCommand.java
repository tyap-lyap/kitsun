package ru.pinkgoosik.somikbot.command.everyone;

import discord4j.core.object.entity.Member;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.somikbot.api.MojangAPI;
import ru.pinkgoosik.somikbot.command.Command;
import ru.pinkgoosik.somikbot.command.CommandUseContext;
import ru.pinkgoosik.somikbot.config.Config;
import ru.pinkgoosik.somikbot.cosmetica.PlayerCloaks;
import ru.pinkgoosik.somikbot.feature.FtpConnection;
import ru.pinkgoosik.somikbot.permissons.AccessManager;
import ru.pinkgoosik.somikbot.permissons.Permissions;

import java.util.ArrayList;

public class CloakGrantCommand extends Command {
    public static final String PREVIEW_CLOAK = "https://raw.githubusercontent.com/PinkGoosik/somik-bot/master/src/main/resources/cloak/%cloak%_cloak_preview.png";

    @Override
    public String getName() {
        return "cloak grant";
    }

    @Override
    public String getDescription() {
        return "Grants a cloak to the player.";
    }

    @Override
    public String appendName() {
        return "**" + Config.general.prefix + this.getName() + "** <cloak>";
    }

    @Override
    public void respond(CommandUseContext context) {
        RestChannel channel = context.getChannel();
        Member member = context.getMember();
        String cloak = context.getFirstArgument();
        String discordId = member.getId().asString();
        String username = getNicknames(discordId).get(0);

        if (!AccessManager.hasAccessTo(member, Permissions.CLOAK_GRANT)) {
            channel.createMessage(createErrorEmbed("Not enough permissions.")).block();
            return;
        }

        if (cloak.equals("empty")) {
            channel.createMessage(createErrorEmbed("You have not specified a cloak name!")).block();
            return;
        }

        if (PlayerCloaks.hasCloak(username)) {
            channel.createMessage(createErrorEmbed("You already has a cloak. Revoke it with the `!cloak revoke` command.")).block();
            return;
        }

        if (!PlayerCloaks.COLORED_CLOAKS.contains(cloak) && !PlayerCloaks.PATTERNED_CLOAKS.contains(cloak) && !PlayerCloaks.PRIDE_CLOAKS.contains(cloak)) {
            channel.createMessage(createErrorEmbed("The " + cloak + " cloak is not found. Use `!available cloaks` to see available cloaks.")).block();
            return;
        }

        if (MojangAPI.getUuid(username).isPresent()) {
            if (!hasTwoCloaks(discordId)) {
                PlayerCloaks.grantCloak(discordId, cloak);
                FtpConnection.updateData();
                String text = "You successfully got granted the " + cloak + " cloak." + "\nPlease rejoin the world to see changes.";
                channel.createMessage(createSuccessfulEmbed("Cloak Granting", text, PREVIEW_CLOAK.replace("%cloak%", cloak))).block();
            } else {
                String text = "You can only have 2 cloaks, your nicknames: " + getNicknames(discordId);
                channel.createMessage(createErrorEmbed(text)).block();
            }
        } else {
            channel.createMessage(createErrorEmbed(username + " is not found. Write down your Minecraft username.")).block();
        }
    }

    private static boolean hasTwoCloaks(String discord) {
        int cloaks = 0;
        for(var entry : PlayerCloaks.ENTRIES){
            if(entry.user.discord.equals(discord)) cloaks++;
        }
        return cloaks >= 2;
    }

    private static ArrayList<String> getNicknames(String discord){
        ArrayList<String> nicknames = new ArrayList<>();
        for(var entry : PlayerCloaks.ENTRIES){
            if(entry.user.discord.equals(discord)) nicknames.add(entry.user.name);
        }
        return nicknames;
    }
}
