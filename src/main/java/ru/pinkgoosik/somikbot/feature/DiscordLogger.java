package ru.pinkgoosik.somikbot.feature;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.discordjson.json.EmbedAuthorData;
import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.EmbedFieldData;
import ru.pinkgoosik.somikbot.Bot;
import ru.pinkgoosik.somikbot.util.GlobalColors;

import java.util.Objects;

public class DiscordLogger {
    Snowflake channel = Snowflake.of("854362907167686687");
    public static DiscordLogger INSTANCE;

    public DiscordLogger(){
    }

    public void messageDeleted(Message message, String reason){
        var embed = EmbedData.builder();
        embed.title("Message Deleted");
        embed.addField(EmbedFieldData.builder().name("Channel").value(Objects.requireNonNull(message.getRestChannel().getData().block()).name().get()).inline(true).build());
        if (message.getAuthor().isPresent()) embed.addField(EmbedFieldData.builder().name("Member").value(message.getAuthor().get().getTag()).inline(true).build());
        embed.addField(EmbedFieldData.builder().name("Message").value(message.getContent()).inline(false).build());
        if (!reason.isBlank()) embed.addField(EmbedFieldData.builder().name("Reason").value(reason).inline(false).build());
        embed.color(GlobalColors.BLUE.getRGB());
        Bot.client.getChannelById(channel).createMessage(embed.build()).block();
    }

    public void messageUpdated(Message old, Message message){
        var embed = EmbedData.builder();
        embed.title("Message Updated");
        embed.addField(EmbedFieldData.builder().name("Channel").value(Objects.requireNonNull(message.getRestChannel().getData().block()).name().get()).inline(true).build());
        if (message.getAuthor().isPresent()){
            embed.addField(EmbedFieldData.builder().name("Member").value(message.getAuthor().get().getTag()).inline(true).build());
        }
        embed.addField(EmbedFieldData.builder().name("Before").value(old.getContent()).inline(false).build());
        embed.addField(EmbedFieldData.builder().name("After").value(message.getContent()).inline(false).build());
        embed.color(GlobalColors.BLUE.getRGB());
        Bot.client.getChannelById(channel).createMessage(embed.build()).block();
    }

    public void memberJoin(Member member){
        var embed = EmbedData.builder();
        embed.author(EmbedAuthorData.builder()
                .name(member.getTag() + " joined")
                .iconUrl(member.getAvatarUrl()).build());
        embed.color(GlobalColors.GREEN.getRGB());
        Bot.client.getChannelById(channel).createMessage(embed.build()).block();
    }

    public void memberLeave(Member member){
        var embed = EmbedData.builder();
        embed.author(EmbedAuthorData.builder()
                .name(member.getTag() + " left")
                .iconUrl(member.getAvatarUrl()).build());
        embed.color(GlobalColors.RED.getRGB());
        Bot.client.getChannelById(channel).createMessage(embed.build()).block();
    }

    public void roleCreate(Role role){
        var embed = EmbedData.builder();
        embed.title("Role Created");
        embed.addField(EmbedFieldData.builder().name("Name").value(role.getName()).inline(true).build());
        embed.addField(EmbedFieldData.builder().name("Color").value(GlobalColors.toHex(role.getColor())).inline(true).build());
        embed.addField(EmbedFieldData.builder().name("Mentionable").value(Boolean.toString(role.isMentionable())).inline(true).build());
        embed.addField(EmbedFieldData.builder().name("Separated").value(Boolean.toString(role.isHoisted())).inline(true).build());
        embed.addField(EmbedFieldData.builder().name("Position").value(String.valueOf(role.getRawPosition())).inline(true).build());
        embed.color(GlobalColors.GREEN.getRGB());
        Bot.client.getChannelById(channel).createMessage(embed.build()).block();
    }

    public void roleDelete(Role role){
        var embed = EmbedData.builder();
        embed.title("Role Deleted");
        embed.addField(EmbedFieldData.builder().name("Name").value(role.getName()).inline(true).build());
        embed.addField(EmbedFieldData.builder().name("Color").value(GlobalColors.toHex(role.getColor())).inline(true).build());
        embed.addField(EmbedFieldData.builder().name("Mentionable").value(Boolean.toString(role.isMentionable())).inline(true).build());
        embed.addField(EmbedFieldData.builder().name("Separated").value(Boolean.toString(role.isHoisted())).inline(true).build());
        embed.addField(EmbedFieldData.builder().name("Position").value(String.valueOf(role.getRawPosition())).inline(true).build());
        embed.color(GlobalColors.RED.getRGB());
        Bot.client.getChannelById(channel).createMessage(embed.build()).block();
    }
}
