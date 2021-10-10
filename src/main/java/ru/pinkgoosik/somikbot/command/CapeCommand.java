package ru.pinkgoosik.somikbot.command;

import ru.pinkgoosik.somikbot.Bot;
import ru.pinkgoosik.somikbot.cosmetica.PlayerCapes;
import ru.pinkgoosik.somikbot.util.PlayerUuid;

public class CapeCommand extends Command {

    @Override
    public String getName() {
        return "cape";
    }

    @Override
    public String getDescription() {
        return "Grants and revokes a cape from a Player.";
    }

    @Override
    public String appendDescription() {
        return "`!" + this.getName() + " <grant|revoke> <self|player> [cape]` " + " - " + this.getDescription();
    }

    @Override
    public String respond(String[] args, String nickname) {
        if(args[2].equals("self")) args[2] = nickname;
        if(args[1].equals("revoke")) return tryToRevoke(args[2]);
        if(args[1].equals("grant")) return tryToGrant(args[2], args[3]);
        return "Failed";
    }

    private String tryToGrant(String nickname, String cape){
        if(PlayerCapes.hasCape(nickname)) return nickname + " already has a cape";
        if(!PlayerCapes.CAPES.contains(cape) || PlayerUuid.getUuid(nickname) == null) return "Cape or Player not found";
        else {
            PlayerCapes.grantCape(nickname, PlayerUuid.getUuid(nickname), cape);
            Bot.ftpConnection.updateCapesData();
            return nickname + " got successfully granted with the " + cape + " cape" + "\nRejoin the world to see changes";
        }
    }

    private String tryToRevoke(String nickname){
        if(PlayerCapes.hasCape(nickname)){
            PlayerCapes.revokeCape(nickname);
            Bot.ftpConnection.updateCapesData();
            return "Successfully revoked a cape from the player " + nickname;
        }
        else return nickname + " doesn't have a cape";
    }
}
