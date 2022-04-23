package ru.pinkgoosik.kitsun.instance;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.discordjson.json.EmbedAuthorData;
import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.EmbedFieldData;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.util.GlobalColors;

import java.time.Instant;
import java.util.Objects;

public class ServerLogger {
    public String server;
    public boolean enabled = false;
    public String channel = "";

    public ServerLogger(String serverID) {
        this.server = serverID;
    }

    public void enable(String channelID) {
        this.enabled = true;
        this.channel = channelID;
    }

    public void disable() {
        this.enabled = false;
    }

    public void onMemberJoin(Member member) {
        var embed = EmbedData.builder();
        embed.author(EmbedAuthorData.builder().name(member.getTag() + " joined").iconUrl(member.getAvatarUrl()).build());
        embed.color(GlobalColors.GREEN.getRGB());
        embed.timestamp(Instant.now().toString());
        Bot.client.getChannelById(Snowflake.of(channel)).createMessage(embed.build()).block();
    }

    public void onMemberLeave(Member member) {
        var embed = EmbedData.builder();
        embed.author(EmbedAuthorData.builder().name(member.getTag() + " left").iconUrl(member.getAvatarUrl()).build());
        embed.color(GlobalColors.RED.getRGB());
        embed.timestamp(Instant.now().toString());
        Bot.client.getChannelById(Snowflake.of(channel)).createMessage(embed.build()).block();
    }

    public void onMessageUpdate(Message old, Message message) {
        if(!old.getContent().equals(message.getContent()) && message.getAuthor().isPresent() && message.getGuildId().isPresent()) {
            var embed = EmbedData.builder();
            embed.title("Message Updated");
            embed.url("https://discord.com/channels/" + message.getGuildId().get().asString() + "/" + message.getRestChannel().getId().asString() + "/" + message.getId().asString());
            embed.addField(EmbedFieldData.builder().name("Channel").value("<#" + Objects.requireNonNull(message.getRestChannel().getData().block()).id().asString() + ">").inline(true).build());
            embed.addField(EmbedFieldData.builder().name("Member").value("<@" + message.getAuthor().get().getId().asString() + ">").inline(true).build());
            embed.addField(EmbedFieldData.builder().name("Before").value(old.getContent()).inline(false).build());
            embed.addField(EmbedFieldData.builder().name("After").value(message.getContent()).inline(false).build());
            embed.color(GlobalColors.BLUE.getRGB());
            embed.timestamp(Instant.now().toString());
            Bot.client.getChannelById(Snowflake.of(channel)).createMessage(embed.build()).block();
        }
    }

    public void onMessageDelete(Message message) {
        if(message.getAuthor().isPresent()) {
            var embed = EmbedData.builder();
            embed.title("Message Deleted");
            embed.addField(EmbedFieldData.builder().name("Channel").value("<#" + Objects.requireNonNull(message.getRestChannel().getData().block()).id().asString() + ">").inline(true).build());
            embed.addField(EmbedFieldData.builder().name("Member").value("<@" + message.getAuthor().get().getId().asString() + ">").inline(true).build());
            embed.addField(EmbedFieldData.builder().name("Message").value(message.getContent()).inline(false).build());
            embed.color(GlobalColors.BLUE.getRGB());
            embed.timestamp(Instant.now().toString());
            Bot.client.getChannelById(Snowflake.of(channel)).createMessage(embed.build()).block();
        }

    }
}
