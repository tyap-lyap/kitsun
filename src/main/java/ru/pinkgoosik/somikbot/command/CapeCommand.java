package ru.pinkgoosik.somikbot.command;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import ru.pinkgoosik.somikbot.cosmetica.PlayerCapes;
import ru.pinkgoosik.somikbot.feature.FtpConnection;
import ru.pinkgoosik.somikbot.util.UuidGetter;

public class CapeCommand extends Command {

    @Override
    public String getName() {
        return "cape";
    }

    @Override
    public String getDescription() {
        return "Gives and removes the cape.";
    }

    @Override
    public String appendName() {
        return "**!" + this.getName() + "** <grant|revoke> <nickname> [cape]";
    }

    @Override
    public void respond(MessageCreateEvent event, User user, MessageChannel channel) {
        String msg = event.getMessage().getContent() + " empty empty empty";
        String[] args = msg.split(" ");

        if(args[1].equals("revoke")) channel.createMessage(tryToRevoke(args[2])).block();
        if(args[1].equals("grant")) channel.createMessage(tryToGrant(args[2], args[3])).block();
    }

    private String tryToGrant(String nickname, String cape){
        if(PlayerCapes.hasCape(nickname)) return nickname + " already has a cape";
        if(!PlayerCapes.CAPES.contains(cape) || UuidGetter.getUuid(nickname) == null) return "Cape or Player not found";
        else {
            PlayerCapes.grantCape(nickname, UuidGetter.getUuid(nickname), cape);
            FtpConnection.updateCapesData();
            return nickname + " got successfully granted with the " + cape + " cape" + "\nRejoin the world to see changes";
        }
    }

    private String tryToRevoke(String nickname){
        if(PlayerCapes.hasCape(nickname)){
            PlayerCapes.revokeCape(nickname);
            FtpConnection.updateCapesData();
            return "Successfully revoked a cape from the player " + nickname;
        }
        else return nickname + " doesn't have a cape";
    }
}
