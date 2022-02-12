package ru.pinkgoosik.kitsun.instance;

import discord4j.core.object.entity.Member;
import discord4j.rest.util.Permission;
import reactor.core.publisher.Mono;
import ru.pinkgoosik.kitsun.permission.RolePermissions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static ru.pinkgoosik.kitsun.permission.Permissions.*;

public class AccessManager {
    private static final ArrayList<RolePermissions> DEFAULT = new ArrayList<>(
            List.of(
                    new RolePermissions("default", new ArrayList<>(
                            List.of(HELP, REGISTER, AVAILABLE_CLOAKS, AVAILABLE_ATTRIBUTES,
                                    AVAILABLE_COSMETICS, REDEEM, CLOAK_SET,
                                    CLOAK_REVOKE_SELF, UNREGISTER)))));

    public ArrayList<RolePermissions> entries = DEFAULT;
    public String serverId;

    public AccessManager(String serverId) {
        this.serverId = serverId;
    }

    public boolean hasAccessTo(Member member, String permission) {
        AtomicBoolean isAdmin = new AtomicBoolean(false);

        member.getBasePermissions().flatMap(permissions -> {
            if(permissions.contains(Permission.ADMINISTRATOR)) {
                isAdmin.set(true);
            }
            return Mono.empty();
        }).block();

        if (isAdmin.get()) return true;
        if (getPermissionsForEveryone().contains(permission)) return true;
        ArrayList<String> roles = new ArrayList<>();
        member.getRoleIds().forEach(snowflake -> roles.add(snowflake.asString()));
        for (var entry : entries) {
            if(roles.contains(entry.role()) && entry.permissions().contains(permission)) return true;
        }
        return false;
    }

    public void grant(String role, String permission) {
        for (var entry : entries) {
            if (entry.role().equals(role)) {
                if (!entry.permissions().contains(permission)) {
                    entry.permissions().add(permission);
                    ServerData.getData(serverId).saveData();
                    return;
                }
            }
        }
        entries.add(new RolePermissions(role, new ArrayList<>(List.of(permission))));
        ServerData.getData(serverId).saveData();
    }

    public List<String> getPermissionsForEveryone() {
        for (var entry : entries) {
            if (entry.role().equals("default")) {
                return entry.permissions();
            }
        }
        return new ArrayList<>();
    }
}
