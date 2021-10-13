package ru.pinkgoosik.somikbot.command;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.EmbedFieldData;
import discord4j.discordjson.json.EmbedThumbnailData;
import discord4j.rest.entity.RestChannel;
import discord4j.rest.util.Color;
import ru.pinkgoosik.somikbot.api.ModrinthAPI;
import ru.pinkgoosik.somikbot.api.ModrinthMod;

public class ModrinthCommand extends Command {

    @Override
    public String getName() {
        return "modrinth";
    }

    @Override
    public String getDescription() {
        return "Sends statistics of modrinth's mod.";
    }

    @Override
    public String appendName() {
        return "**!" + this.getName() + "** <slug>";
    }

    @Override
    public void respond(MessageCreateEvent event, User user, MessageChannel channel) {
        String msg = event.getMessage().getContent() + " empty";
        String[] args = msg.split(" ");
        RestChannel restChannel = event.getClient().getRestClient().getChannelById(channel.getId());
        ModrinthMod mod = ModrinthAPI.getModBySlug(args[1].toLowerCase());

        if (!mod.isEmpty()){
            restChannel.createMessage(createEmbed(mod)).block();
        }else if(mod.isEmpty()){
            restChannel.createMessage(createErrorEmbed()).block();
        }
    }

    private EmbedData createEmbed(ModrinthMod mod){
        return EmbedData.builder()
                .title(mod.title)
                .url(mod.modUrl)
                .description("**Description**\n" + mod.shortDescription)
                .color(Color.of(48,178,123).getRGB())
                .thumbnail(EmbedThumbnailData.builder().url(mod.iconUrl).build())
                .addField(EmbedFieldData.builder().name("Downloads").value(Integer.toString(mod.downloads)).inline(true).build())
                .addField(EmbedFieldData.builder().name("Followers").value(Integer.toString(mod.followers)).inline(true).build())
                .build();
    }

    private EmbedData createErrorEmbed(){
        return EmbedData.builder()
                .title("Error")
                .description("Mod not found.")
                .color(Color.of(246,129,129).getRGB())
                .build();
    }
}
