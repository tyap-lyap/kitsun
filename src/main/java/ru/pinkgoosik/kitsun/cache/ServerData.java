package ru.pinkgoosik.kitsun.cache;

import java.util.ArrayList;

public class ServerData extends CachedServerData {
	private static final ArrayList<ServerData> DATA_CACHE = new ArrayList<>();

	public ServerData(String serverId) {
		super(serverId);
	}

	public static ServerData get(String serverId) {
		for(ServerData data : DATA_CACHE) {
			if(data.server.equals(serverId)) return data;
		}
		ServerData serverData = new ServerData(serverId);
		DATA_CACHE.add(serverData);
		return serverData;
	}

}
