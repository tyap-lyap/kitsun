package ru.pinkgoosik.kitsun.cache;

import ru.pinkgoosik.kitsun.feature.*;
import ru.pinkgoosik.kitsun.config.ServerConfig;
import ru.pinkgoosik.kitsun.permission.PermissionsManager;

public abstract class CachedServerData {
	/**
	 * Discord server ID that this ServerData belongs to
	 */
	public String server;

	public Cached<ServerConfig> config;
	public Cached<PermissionsManager> permissions;
	public Cached<ModUpdatesPublisher[]> modUpdates;
	public Cached<ServerLogger> logger;
	public Cached<MCUpdatesPublisher> mcUpdates;
	public Cached<QuiltUpdatesPublisher> quiltUpdates;
	public Cached<AutoChannelsManager> autoChannels;
	public Cached<ModCard[]> modCards;
	public Cached<AutoReaction[]> autoReactions;

	public CachedServerData(String serverId) {
		this.server = serverId;

		String path = "data/" + serverId;
		this.logger = new Cached<>(path, "logger.json", ServerLogger.class, () -> new ServerLogger(serverId));
		this.config = new Cached<>(path, "config.json", ServerConfig.class, () -> new ServerConfig(serverId));
		this.permissions = new Cached<>(path, "permissions.json", PermissionsManager.class, () -> new PermissionsManager(serverId));
		this.modUpdates = new Cached<>(path, "mod_updates.json", ModUpdatesPublisher[].class, () -> new ModUpdatesPublisher[]{});
		this.mcUpdates = new Cached<>(path, "mc_updates.json", MCUpdatesPublisher.class, () -> new MCUpdatesPublisher(serverId));
		this.quiltUpdates = new Cached<>(path, "quilt_updates.json", QuiltUpdatesPublisher.class, () -> new QuiltUpdatesPublisher(serverId));
		this.autoChannels = new Cached<>(path, "auto_channels.json", AutoChannelsManager.class, () -> new AutoChannelsManager(serverId));
		this.modCards = new Cached<>(path, "mod_cards.json", ModCard[].class, () -> new ModCard[]{});
		this.autoReactions = new Cached<>(path, "auto_reactions.json", AutoReaction[].class, () -> new AutoReaction[]{});
	}

	public void save() {
		config.save();
		permissions.save();
		modUpdates.save();
		logger.save();
		mcUpdates.save();
		quiltUpdates.save();
		autoChannels.save();
		modCards.save();
		autoReactions.save();
	}

}
