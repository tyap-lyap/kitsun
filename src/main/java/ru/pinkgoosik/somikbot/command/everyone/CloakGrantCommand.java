package ru.pinkgoosik.somikbot.command.everyone;

import discord4j.core.object.entity.Member;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.somikbot.command.Command;
import ru.pinkgoosik.somikbot.command.CommandUseContext;
import ru.pinkgoosik.somikbot.permissons.AccessManager;
import ru.pinkgoosik.somikbot.cosmetica.PlayerCloaks;
import ru.pinkgoosik.somikbot.feature.FtpConnection;
import ru.pinkgoosik.somikbot.permissons.Permissions;
import ru.pinkgoosik.somikbot.api.MojangAPI;

import java.util.ArrayList;

public class CloakGrantCommand extends Command {
    private static final String PREVIEW_CLOAK = "https://raw.githubusercontent.com/PinkGoosik/somik-bot/master/src/main/resources/cloak/%cloak%_cloak_preview.png";

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
        return "**!" + this.getName() + "** <nickname> <cloak>";
    }

    @Override
    public void respond(CommandUseContext context) {
        RestChannel channel = context.getChannel();
        Member member = context.getMember();
        String nickname = context.getFirstArgument();
        String cloak = context.getSecondArgument();

        if (!AccessManager.hasAccessTo(member, Permissions.CLOAK_GRANT)){
            channel.createMessage(createErrorEmbed("Not enough permissions.")).block();
            return;
        }

        if(nickname.equals("empty")){
            channel.createMessage(createErrorEmbed("You have not specified a nickname!")).block();
            return;
        }
        if(cloak.equals("empty")){
            channel.createMessage(createErrorEmbed("You have not specified a cloak!")).block();
            return;
        }
        if(PlayerCloaks.hasCloak(nickname)){
            channel.createMessage(createErrorEmbed(nickname + " already has a cloak. Revoke it with the `!cloak revoke` command.")).block();
            return;
        }
        if(!PlayerCloaks.CLOAKS.contains(cloak)){
            channel.createMessage(createErrorEmbed("Cloak " + cloak + " is not found. Use `!cloaks` to see available cloaks.")).block();
            return;
        }
        if(MojangAPI.getUuid(nickname).isEmpty()){
            channel.createMessage(createErrorEmbed("Player " + nickname + " is not found. Write down your Minecraft nickname.")).block();
            return;
        }

        String discordId = member.getId().asString();

        if(!hasTwoCloaks(discordId)){
            PlayerCloaks.grantCloak(discordId, nickname, MojangAPI.getUuid(nickname).get(), cloak);
            FtpConnection.updateCapesData();
            String text = nickname + " got successfully granted with the " + cloak + " cloak." + "\nRejoin the world to see changes.";
            channel.createMessage(createSuccessfulEmbed("Cloak Granting", text, PREVIEW_CLOAK.replace("%cloak%", cloak))).block();
        }else {
            String text = "You can only have 2 cloaks, your nicknames: " + getNicknames(discordId);
            channel.createMessage(createErrorEmbed(text)).block();
        }
    }

    private static boolean hasTwoCloaks(String discord){
        int cloaks = 0;
        for(var entry : PlayerCloaks.ENTRIES){
            if(entry.discord().equals(discord)) cloaks++;
        }
        return cloaks >= 2;
    }

    private static ArrayList<String> getNicknames(String discord){
        ArrayList<String> nicknames = new ArrayList<>();
        for(var entry : PlayerCloaks.ENTRIES){
            if(entry.discord().equals(discord)) nicknames.add(entry.name());
        }
        return nicknames;
    }
}
