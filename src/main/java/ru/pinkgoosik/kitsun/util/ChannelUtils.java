package ru.pinkgoosik.kitsun.util;

import discord4j.common.util.Snowflake;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.channel.VoiceChannel;
import ru.pinkgoosik.kitsun.Bot;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChannelUtils {

    public static boolean exist(String serverId, String channelId) {
        AtomicBoolean channelExist = new AtomicBoolean(false);

        Bot.rest.getGuildById(Snowflake.of(serverId)).getChannels().all(channelData -> {
            if(channelData.id().asString().equals(channelId)) {
                channelExist.set(true);
            }
            return true;
        }).block();

        return channelExist.get();
    }

    public static boolean isVoiceChannel(String serverId, String channelId) {
        AtomicBoolean isVoiceChannel = new AtomicBoolean(false);

        Bot.rest.getGuildById(Snowflake.of(serverId)).getChannels().all(channelData -> {
            if(channelData.id().asString().equals(channelId) && !channelData.bitrate().isAbsent()) {
                isVoiceChannel.set(true);
            }
            return true;
        }).block();

        return isVoiceChannel.get();
    }

    public static int getMembers(VoiceChannel channel) {
        List<VoiceState> states = channel.getVoiceStates().collectList().block();
        int members = 0;
        if(states != null) {
            for(var state : states) {
                if(state.getChannelId().isPresent()) {
                    if(state.getChannelId().get().asString().equals(channel.getId().asString())) {
                        members++;
                    }
                }
            }
        }
        return members;
    }


}
