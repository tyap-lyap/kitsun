package ru.pinkgoosik.kitsun.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import ru.pinkgoosik.kitsun.permission.PermissionsManager;
import ru.pinkgoosik.kitsun.cache.ServerData;
import ru.pinkgoosik.kitsun.config.ServerConfig;

import java.util.ArrayList;

public final class CommandUseContext {
	public Member member;
	public MessageChannelUnion channel;
	public ArrayList<String> args;
	public ServerData serverData;
	public ServerConfig config;
	public PermissionsManager accessManager;
	public String memberId;

	public CommandUseContext(Member member, MessageChannelUnion channel, ArrayList<String> args, ServerData serverData) {
		this.member = member;
		this.channel = channel;
		this.args = args;
		this.serverData = serverData;
		this.config = serverData.config.get();
		this.accessManager = serverData.permissions.get();
		this.memberId = member.getId();
	}
}
