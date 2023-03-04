package ru.pinkgoosik.kitsun.permission;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import ru.pinkgoosik.kitsun.cache.ServerData;

import java.util.ArrayList;
import java.util.List;

import static ru.pinkgoosik.kitsun.permission.Permissions.*;

public class PermissionsManager {
	private static final ArrayList<RolePermissions> DEFAULT = new ArrayList<>(List.of(new RolePermissions("default", new ArrayList<>(List.of(HELP)))));

	public String server;
	public ArrayList<RolePermissions> entries = DEFAULT;

	public PermissionsManager(String serverID) {
		this.server = serverID;
	}

	public boolean hasAccessTo(Member member, String permission) {
		boolean isAdmin = false;
		var permissionSet = member.getPermissions();
		isAdmin = permissionSet.contains(Permission.ADMINISTRATOR);

		if(isAdmin) return true;
		if(getPermissionsForEveryone().contains(permission)) return true;

		ArrayList<String> roles = new ArrayList<>();
		member.getRoles().forEach(role -> roles.add(role.getId()));
		for(var entry : entries) {
			if(roles.contains(entry.role) && entry.permissions.contains(permission)) return true;
		}
		return false;
	}

	public void grant(String role, String permission) {
		for(var entry : entries) {
			if(entry.role.equals(role)) {
				if(!entry.permissions.contains(permission)) {
					entry.permissions.add(permission);
					ServerData.get(server).permissions.save();
					return;
				}
			}
		}
		entries.add(new RolePermissions(role, new ArrayList<>(List.of(permission))));
		ServerData.get(server).permissions.save();
	}

	public List<String> getPermissionsForEveryone() {
		for(var entry : entries) {
			if(entry.role.equals("default")) {
				return entry.permissions;
			}
		}
		return new ArrayList<>();
	}

}
