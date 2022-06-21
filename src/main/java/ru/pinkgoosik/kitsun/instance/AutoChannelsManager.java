package ru.pinkgoosik.kitsun.instance;

import discord4j.common.util.Snowflake;
import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.core.spec.GuildMemberEditSpec;
import discord4j.core.spec.VoiceChannelCreateSpec;
import discord4j.discordjson.json.ChannelData;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.util.ServerUtils;

import java.util.ArrayList;
import java.util.Optional;

public class AutoChannelsManager {
    public String server;
    public boolean enabled = false;
    public String parentChannel;
    public ArrayList<Session> sessions = new ArrayList<>();

    public AutoChannelsManager(String serverID) {
        this.server = serverID;
    }

    public void enable(String parentChannelID) {
        this.enabled = true;
        this.parentChannel = parentChannelID;
    }

    public void disable() {
        this.enabled = false;
    }

    public void onVoiceChannelJoin(VoiceChannel voiceChannel, Member member) {
        Guild guild = Bot.client.getGuildById(Snowflake.of(server)).block();
        ServerData serverData = ServerData.get(server);
        String channelId = voiceChannel.getId().asString();
        if(guild == null) return;

        if(channelId.equals(serverData.autoChannelsManager.parentChannel)) {
            serverData.autoChannelsManager.createSession(guild, member);
        }
        this.getSession(channelId).ifPresent(session -> session.members = session.members + 1);
    }

    public void onVoiceChannelLeave(VoiceChannel voiceChannel, Member member) {
        ServerData serverData = ServerData.get(server);
        String channelId = voiceChannel.getId().asString();

        this.getSession(channelId).ifPresent(session -> {
            session.members = session.members - 1;
            if(session.members == 0) {
                var owner = Bot.client.getMemberById(Snowflake.of(serverData.server), Snowflake.of(session.owner)).block();
                if(serverData.logger.enabled) {
                    serverData.logger.onVoiceChannelDelete(member, owner, voiceChannel);
                }
                voiceChannel.delete().block();
                this.refresh();
            }
        });
    }

    public void createSession(Guild guild, Member member) {
        ChannelData channelData = Bot.rest.getChannelById(Snowflake.of(parentChannel)).getData().block();
        if(channelData == null) return;

        if(!channelData.parentId().isAbsent() && channelData.parentId().get().isPresent()) {
            String memberName = member.getDisplayName();
            String category = channelData.parentId().get().get().asString();
            VoiceChannel channel = guild.createVoiceChannel(VoiceChannelCreateSpec.builder().name(memberName + "'s lounge").parentId(Snowflake.of(category)).build()).block();

            if(channel != null) {
                Snowflake memberId = member.getId();
                channel.addMemberOverwrite(memberId, PermissionOverwrite.forMember(memberId, PermissionSet.of(Permission.MANAGE_CHANNELS), PermissionSet.none())).block();
                member.edit(GuildMemberEditSpec.builder().newVoiceChannelOrNull(channel.getId()).build()).block();
                this.sessions.add(new Session(member.getId().asString(), channel.getId().asString()));
                ServerData serverData = ServerData.get(server);
                if(serverData.logger.enabled) {
                    serverData.logger.onAutoChannelCreate(member, channel);
                }
                serverData.save();
            }
        }
    }

    public Optional<Session> getSession(String channelID) {
        for(Session session : sessions) {
            if(session.channel.equals(channelID)) return Optional.of(session);
        }
        return Optional.empty();
    }

    public boolean hasSession(String channelID) {
        for(Session session : sessions) {
            if(session.channel.equals(channelID)) return true;
        }
        return false;
    }

    public void refresh() {
        sessions.removeIf(session -> !ServerUtils.hasChannel(server, session.channel));
        ServerData.get(server).save();
    }

    public static class Session {
        public String owner;
        public String channel;
        public int members;

        public Session(String owner, String channel) {
            this.owner = owner;
            this.channel = channel;
            this.members = 0;
        }
    }
}
