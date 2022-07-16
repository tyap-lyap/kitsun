package ru.pinkgoosik.kitsun.feature;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.discordjson.json.EmbedAuthorData;
import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.EmbedFieldData;
import discord4j.discordjson.json.ImmutableEmbedData;
import org.checkerframework.checker.nullness.qual.Nullable;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.util.KitsunColors;

import java.time.Instant;
import java.util.Objects;
import java.util.function.Consumer;

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

    public void ifEnabled(Consumer<ServerLogger> consumer) {
        if(enabled) consumer.accept(this);
    }

    public void onMemberJoin(Member member) {
        var embed = EmbedData.builder();
        embed.author(EmbedAuthorData.builder().name(member.getTag() + " joined").iconUrl(member.getAvatarUrl()).build());
        embed.color(KitsunColors.getGreen().getRGB());
        embed.timestamp(Instant.now().toString());
        log(embed.build());
    }

    public void onMemberLeave(Member member) {
        var embed = EmbedData.builder();
        embed.author(EmbedAuthorData.builder().name(member.getTag() + " left").iconUrl(member.getAvatarUrl()).build());
        embed.color(KitsunColors.getRed().getRGB());
        embed.timestamp(Instant.now().toString());
        log(embed.build());
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
            embed.color(KitsunColors.getBlue().getRGB());
            embed.timestamp(Instant.now().toString());
            log(embed.build());
        }
    }

    public void onMessageDelete(Message message) {
        if(message.getAuthor().isPresent()) {
            var embed = EmbedData.builder();
            embed.title("Message Deleted");
            embed.addField(EmbedFieldData.builder().name("Channel").value("<#" + Objects.requireNonNull(message.getRestChannel().getData().block()).id().asString() + ">").inline(true).build());
            embed.addField(EmbedFieldData.builder().name("Member").value("<@" + message.getAuthor().get().getId().asString() + ">").inline(true).build());
            embed.addField(EmbedFieldData.builder().name("Message").value(message.getContent()).inline(false).build());
            embed.color(KitsunColors.getRed().getRGB());
            embed.timestamp(Instant.now().toString());
            log(embed.build());
        }
    }

    public void onVoiceChannelNameUpdate(VoiceChannel old, VoiceChannel current) {
        var embed = EmbedData.builder();
        embed.title("Voice Channel Updated");
        embed.addField(EmbedFieldData.builder().name("Before").value(old.getName()).inline(false).build());
        embed.addField(EmbedFieldData.builder().name("After").value(current.getName()).inline(false).build());
        embed.color(KitsunColors.getBlue().getRGB());
        embed.timestamp(Instant.now().toString());
        log(embed.build());
    }

    public void onVoiceChannelDelete(@Nullable Member owner, VoiceChannel voiceChannel) {
        var embed = EmbedData.builder();
        embed.title("Voice Channel Deleted");
        embed.addField(EmbedFieldData.builder().name("Channel").value(voiceChannel.getName()).inline(true).build());
        if(owner != null) {
            embed.addField(EmbedFieldData.builder().name("Owner").value("<@" + owner.getId().asString() + ">").inline(true).build());
        }
        embed.color(KitsunColors.getRed().getRGB());
        embed.timestamp(Instant.now().toString());
        log(embed.build());
    }

    public void onAutoChannelCreate(Member member, VoiceChannel voiceChannel) {
        var embed = EmbedData.builder();
        embed.title("Voice Channel Created");
        embed.addField(EmbedFieldData.builder().name("Channel").value(voiceChannel.getName()).inline(true).build());
        embed.addField(EmbedFieldData.builder().name("Member").value("<@" + member.getId().asString() + ">").inline(true).build());
        embed.color(KitsunColors.getGreen().getRGB());
        embed.timestamp(Instant.now().toString());
        log(embed.build());
    }

    private void log(ImmutableEmbedData embed) {
        Bot.rest.getChannelById(Snowflake.of(channel)).createMessage(embed).block();
    }
}
