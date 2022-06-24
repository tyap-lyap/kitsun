package ru.pinkgoosik.kitsun.schedule;

import discord4j.common.util.Snowflake;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.channel.VoiceChannel;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.feature.KitsunDebugger;
import ru.pinkgoosik.kitsun.util.ChannelUtils;
import ru.pinkgoosik.kitsun.util.ServerUtils;

import java.util.List;

public class AutoChannelsScheduler {

    public static void schedule() {
        try {
            ServerUtils.forEach(serverData -> {
                var manager = serverData.autoChannels;

                if(manager.get().enabled) {
                    if(Bot.client.getChannelById(Snowflake.of(manager.get().parentChannel)).block() instanceof VoiceChannel channel) {
                        List<VoiceState> states = channel.getVoiceStates().collectList().block();
                        if(states != null) {
                            for (var state : states) {
                                var member = state.getMember().block();

                                if(member != null && state.getChannelId().isPresent()) {
                                    if(state.getChannelId().get().asString().equals(channel.getId().asString())) {
                                        manager.get().onParentChannelJoin(member);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
                manager.get().sessions.forEach(session -> {
                    if(Bot.client.getChannelById(Snowflake.of(session.channel)).block() instanceof VoiceChannel channel) {
                        if (ChannelUtils.getMembers(channel) == 0) {
                            channel.delete("Empty auto channel.").block();
                        }
                    }
                });
            });
        }
        catch (Exception e) {
            String msg = "Failed to schedule auto channels invalidation duo to an exception:\n" + e;
            Bot.LOGGER.error(msg);
            KitsunDebugger.report(msg, e, false);
        }
    }
}
