package ru.pinkgoosik.somikbot.command.everyone;

import discord4j.core.object.entity.Member;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.somikbot.command.Command;
import ru.pinkgoosik.somikbot.command.CommandUseContext;
import ru.pinkgoosik.somikbot.config.Config;
import ru.pinkgoosik.somikbot.cosmetica.PlayerCloaks;
import ru.pinkgoosik.somikbot.feature.FtpConnection;
import ru.pinkgoosik.somikbot.permissons.AccessManager;
import ru.pinkgoosik.somikbot.permissons.Permissions;

import java.util.ArrayList;

public class RedeemCommand extends Command {

    @Override
    public String getName() {
        return "redeem";
    }

    @Override
    public String getDescription() {
        return "Redeems a cloak/cosmetic/attribute using a special code.";
    }

    @Override
    public String appendName() {
        return "**" + Config.general.prefix + this.getName() + "** <code>";
    }

    @Override
    public void respond(CommandUseContext context) {
        Member member = context.getMember();
        RestChannel channel = context.getChannel();
        String code = context.getFirstArgument();

        if (!AccessManager.hasAccessTo(member, Permissions.REDEEM)) {
            channel.createMessage(createErrorEmbed("Not enough permissions.")).block();
            return;
        }

        if(code.equals("empty")) {
            channel.createMessage(createErrorEmbed("You have not specified a code!")).block();
            return;
        }

        for(var code1 : PlayerCloaks.CODES){
            if (code1.code.equals(code)) {
                for (var entry : PlayerCloaks.ENTRIES) {
                    if (entry.user.discord.equals(member.getId().asString())) {
                        String username = getNicknames(member.getId().asString()).get(0);
                        String text = "";
                        if (code1.type.equals("cloak")) {
                            entry.cloak.name = code1.cloak.name;
                            text = String.format("%s have successfully redeemed a cloak", username);
                        }
                        if (code1.type.equals("cosmetic")) {
                            entry.cosmetics.addAll(code1.cosmetics);
                            FtpConnection.updateData();
                            text = String.format("%s have successfully redeemed %s", username, code1.cosmetics.size() > 1 ? "some cosmetics": "a cosmetic");
                        }
                        if (code1.type.equals("attribute")) {
                            entry.attributes.addAll(code1.attributes);
                            FtpConnection.updateData();
                            text = String.format("%s have successfully redeemed %s", username, code1.attributes.size() > 1 ? "some attributes": "an attribute");
                        }
                        channel.createMessage(createSuccessfulEmbed("Redeeming code", text)).block();
                    }
                }
            }
        }
    }

    private static ArrayList<String> getNicknames(String discord){
        ArrayList<String> nicknames = new ArrayList<>();
        for(var entry : PlayerCloaks.ENTRIES){
            if(entry.user.discord.equals(discord)) nicknames.add(entry.user.name);
        }
        return nicknames;
    }
}
