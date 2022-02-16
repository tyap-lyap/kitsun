package ru.pinkgoosik.kitsun.command.cloak;

import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.cosmetica.Cloaks;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;

public class CloaksList extends Command {

    @Override
    public String getName() {
        return "cloaks";
    }

    @Override
    public String[] getAltNames() {
        return new String[]{"capes", "caps", "clocks", "cloacks", "cloks", "cocks"};
    }

    @Override
    public String getDescription() {
        return "Sends list of all available cloaks for use.";
    }

    @Override
    public void respond(CommandUseContext ctx) {
        if(disallowed(ctx, Permissions.AVAILABLE_CLOAKS)) return;
        ctx.channel.createMessage(Embeds.info("Available Cloaks", build())).block();
    }

    private String build() {
        String respond = "";

        StringBuilder textColored = new StringBuilder();
        for (String cloak : Cloaks.COLORED_CLOAKS) {
            textColored.append(cloak).append(", ");
        }
        respond = respond + "**Colored Cloaks**\n";

        String respondColored = textColored.deleteCharAt(textColored.length() - 1).deleteCharAt(textColored.length() - 1).append(".").toString();
        respond = respond + respondColored + "\n";

        StringBuilder textPride = new StringBuilder();
        for (String cloak : Cloaks.PRIDE_CLOAKS) {
            textPride.append(cloak).append(", ");
        }
        respond = respond + "**Pride Cloaks**\n";

        String respondPride = textPride.deleteCharAt(textPride.length() - 1).deleteCharAt(textPride.length() - 1).append(".").toString();
        respond = respond + respondPride + "\n";

        StringBuilder textFancy = new StringBuilder();
        for (String cloak : Cloaks.FANCY_CLOAKS) {
            textFancy.append(cloak).append(", ");
        }
        respond = respond + "**Fancy Cloaks**\n";

        String respondFancy = textFancy.deleteCharAt(textFancy.length() - 1).deleteCharAt(textFancy.length() - 1).append(".").toString();
        respond = respond + respondFancy + "\n";

        StringBuilder textPatterned = new StringBuilder();
        for (String cloak : Cloaks.PATTERNED_CLOAKS) {
            textPatterned.append(cloak).append(", ");
        }
        respond = respond + "**Patterned Cloaks**\n";

        String respondPatterned = textPatterned.deleteCharAt(textPatterned.length() - 1).deleteCharAt(textPatterned.length() - 1).append(".").toString();
        respond = respond + respondPatterned;

        return respond;
    }
}
