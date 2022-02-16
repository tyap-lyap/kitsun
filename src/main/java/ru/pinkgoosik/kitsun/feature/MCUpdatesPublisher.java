package ru.pinkgoosik.kitsun.feature;

import discord4j.common.util.Snowflake;
import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.EmbedThumbnailData;
import discord4j.rest.util.Color;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.api.mojang.MojangAPI;
import ru.pinkgoosik.kitsun.api.mojang.PatchNotes;
import ru.pinkgoosik.kitsun.instance.ServerData;

import java.time.Instant;

public class MCUpdatesPublisher {
    private static final String QUILT_MC_PATCH_NOTES = "https://quiltmc.org/mc-patchnotes/#%version%";

    public boolean enabled = false;
    //Discord server id
    public String serverId;
    //Discord channel id
    public String channel = "";
    public String latestRelease = "";
    public String latestSnapshot = "";
    public String debtor = "";

    public MCUpdatesPublisher(String serverId) {
        this.serverId = serverId;
    }

    public void enable(String channelId) {
        this.enabled = true;
        this.channel = channelId;
    }

    public void disable() {
        this.enabled = false;
    }

    public void check() {
        MojangAPI.getManifest().ifPresent(manifest -> {
            if(!manifest.latest.release.equals(latestRelease)) {
                this.latestRelease = manifest.latest.release;
                publish(manifest.latest.release, "Release");
            }
            if(!manifest.latest.snapshot.equals(latestSnapshot)) {
                this.latestSnapshot = manifest.latest.snapshot;
                publish(manifest.latest.snapshot, "Snapshot");
            }
            if (!debtor.isBlank()) {
                PatchNotes.getEntry(debtor).ifPresent(entry -> {
                    publishPatchNotesEntry(entry);
                    this.debtor = "";
                    ServerData.get(serverId).saveData();
                });
            }
        });
    }

    private void publish(String version, String type) {
        ServerData.get(serverId).saveData();
        Bot.client.getChannelById(Snowflake.of(channel)).createMessage(createEmbed(version, type)).block();
        tryToPublishPatchNotes(version);
    }

    private EmbedData createEmbed(String version, String type) {
        return EmbedData.builder().title("New Minecraft Version")
                .description(type + " " + version + " just got released!")
                .color(Color.of(48,178,123).getRGB())
                .timestamp(Instant.now().toString())
                .build();
    }

    private void tryToPublishPatchNotes(String version) {
        PatchNotes.getEntry(version).ifPresentOrElse(this::publishPatchNotesEntry, () -> {
            this.debtor = version;
            ServerData.get(serverId).saveData();
        });
    }

    private void publishPatchNotesEntry(PatchNotes.PatchNotesEntry entry) {
        Bot.client.getChannelById(Snowflake.of(channel)).createMessage(createPatchNotesEmbed(entry)).block();
    }

    private EmbedData createPatchNotesEmbed(PatchNotes.PatchNotesEntry entry) {
        return EmbedData.builder().title(entry.version + " Patch Notes")
                .thumbnail(EmbedThumbnailData.builder().url(entry.image.getFullUrl()).build())
                .description(entry.summary() + "\n[Full Patch Notes](" + QUILT_MC_PATCH_NOTES.replaceAll("%version%", entry.version) + ")")
                .color(Color.of(48,178,123).getRGB())
                .timestamp(Instant.now().toString())
                .build();
    }
}
