package ru.pinkgoosik.kitsun.feature;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.api.mojang.MojangAPI;
import ru.pinkgoosik.kitsun.api.mojang.PatchNotes;
import ru.pinkgoosik.kitsun.api.mojang.VersionManifest;
import ru.pinkgoosik.kitsun.cache.ServerData;
import ru.pinkgoosik.kitsun.util.KitsunColors;

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
			MojangAPI.setMcVersionsCache(manifest);
		}
		if(!manifest.latest.snapshot.equals(latestSnapshot)) {
			this.latestSnapshot = manifest.latest.snapshot;
			publish(manifest.latest.snapshot, "Snapshot");
			MojangAPI.setMcVersionsCache(manifest);
		}
		if(!debtor.isBlank()) {
			PatchNotes.getEntry(debtor).ifPresent(entry -> {
				publishPatchNotesEntry(entry);
				this.debtor = "";
				ServerData.get(server).save();
			});
		}
	}

	private void publish(String version, String type) {
		ServerData.get(server).save();
		if(Bot.jda.getGuildChannelById(channel) instanceof TextChannel textChannel) {
			textChannel.sendMessageEmbeds(createEmbed(version, type)).queue();
		}
		tryToPublishPatchNotes(version);
	}

	private MessageEmbed createEmbed(String version, String type) {
		return new EmbedBuilder().setTitle("New Minecraft Version")
				.setDescription(type + " " + version + " just got released!")
				.setColor(KitsunColors.getCyan())
				.setTimestamp(Instant.now())
				.build();
	}

	private void tryToPublishPatchNotes(String version) {
		PatchNotes.getEntry(version).ifPresentOrElse(this::publishPatchNotesEntry, () -> {
			this.debtor = version;
			ServerData.get(server).save();
		});
	}

	private void publishPatchNotesEntry(PatchNotes.PatchNotesEntry entry) {
		if(Bot.jda.getGuildChannelById(channel) instanceof TextChannel textChannel) {
			textChannel.sendMessageEmbeds(createPatchNotesEmbed(entry)).queue();
		}
	}

	private MessageEmbed createPatchNotesEmbed(PatchNotes.PatchNotesEntry entry) {
		return new EmbedBuilder().setTitle(entry.version + " Patch Notes")
				.setThumbnail(entry.image.getFullUrl())
				.setDescription(entry.summary() + "\n[Homepage](https://minecraft.net) | [Issue Tracker](https://bugs.mojang.com/issues) | [Full Patch Notes](" + QUILT_MC_PATCH_NOTES.replaceAll("%version%", entry.version) + ")")
				.setColor(KitsunColors.getCyan())
				.setTimestamp(Instant.now())
				.build();
	}
}
