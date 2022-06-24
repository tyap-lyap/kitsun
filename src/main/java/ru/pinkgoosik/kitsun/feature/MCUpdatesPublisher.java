package ru.pinkgoosik.kitsun.feature;

import discord4j.common.util.Snowflake;
import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.EmbedThumbnailData;
import discord4j.rest.util.Color;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.api.mojang.PatchNotes;
import ru.pinkgoosik.kitsun.api.mojang.VersionManifest;
import ru.pinkgoosik.kitsun.cache.ServerData;

import java.time.Instant;

public class MCUpdatesPublisher {
    private static final String QUILT_MC_PATCH_NOTES = "https://quiltmc.org/mc-patchnotes/#%version%";

    public boolean enabled = false;
    /**
     * Discord server ID that this MCUpdatesPublisher belongs to
     */
    public String server;
    /**
     * Discord server's channel ID for this MCUpdatesPublisher publish to
     */
    public String channel = "";
    /**
     * Cached Minecraft latest release name
     */
    public String latestRelease = "";
    /**
     * Cached Minecraft latest snapshot name
     */
    public String latestSnapshot = "";
    /**
     * Minecraft version name that patch notes wasn't published,
     * the main reason for this is that the version, and it's
     * patch note is not publishes at the same time.
     */
    public String debtor = "";

    public MCUpdatesPublisher(String serverID) {
        this.server = serverID;
    }

    public void enable(String channelID) {
        this.enabled = true;
        this.channel = channelID;
    }

    public void disable() {
        this.enabled = false;
    }

    public void check(VersionManifest manifest) {
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
                ServerData.get(server).save();
            });
        }
    }

    private void publish(String version, String type) {
        ServerData.get(server).save();
        Bot.rest.getChannelById(Snowflake.of(channel)).createMessage(createEmbed(version, type)).block();
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
            ServerData.get(server).save();
        });
    }

    private void publishPatchNotesEntry(PatchNotes.PatchNotesEntry entry) {
        Bot.rest.getChannelById(Snowflake.of(channel)).createMessage(createPatchNotesEmbed(entry)).block();
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
