package ru.pinkgoosik.kitsun.schedule;

import discord4j.common.util.Snowflake;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.rest.http.client.ClientException;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.feature.KitsunDebugger;
import ru.pinkgoosik.kitsun.util.ChannelUtils;
import ru.pinkgoosik.kitsun.util.ServerUtils;

import java.util.List;

public class AutoChannelsScheduler {

    public static void schedule() {
        try {
            ServerUtils.forEach(data -> data.autoChannels.get(manager -> {
                if(manager.enabled) {
                    if(Bot.client.getChannelById(Snowflake.of(manager.parentChannel)).block() instanceof VoiceChannel channel) {
                        List<VoiceState> states = channel.getVoiceStates().collectList().block();
                        if(states != null) {
                            for (var state : states) {
                                var member = state.getMember().block();

                                if(member != null && state.getChannelId().isPresent()) {
                                    if(state.getChannelId().get().asString().equals(channel.getId().asString())) {
                                        manager.onParentChannelJoin(member);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
                manager.sessions.forEach(session -> {
                    if(Bot.client.getChannelById(Snowflake.of(session.channel)).block() instanceof VoiceChannel channel) {
                        if (ChannelUtils.getMembers(channel) == 0) {
                            channel.delete("Empty auto channel.").block();
                        }
                    }
                });
            }));
        }
        catch(ClientException e) {
            if(e.getMessage().contains("Missing Permissions")) {
                //ignore
            }
            else {
                KitsunDebugger.ping("Failed to schedule auto channels invalidation duo to an exception:\n" + e);
            }
        }
        catch (Exception e) {
            KitsunDebugger.ping("Failed to schedule auto channels invalidation duo to an exception:\n" + e);
        }
    }
}
