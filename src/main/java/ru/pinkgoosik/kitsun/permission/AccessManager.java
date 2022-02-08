package ru.pinkgoosik.kitsun.permission;

import com.google.gson.*;
import discord4j.core.object.entity.Member;
import discord4j.rest.util.Permission;
import reactor.core.publisher.Mono;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.util.FileUtils;

import java.io.*;
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


    private final ArrayList<RolePermissions> entries = new ArrayList<>();
    public final String serverID;

    public AccessManager(String serverID) {
        this.serverID = serverID;
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
        if (getDefaultPermissions().contains(permission)) return true;
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
                    saveData();
                    return;
                }
            }
        }
        entries.add(new RolePermissions(role, new ArrayList<>(List.of(permission))));
        saveData();
    }

    public List<String> getDefaultPermissions() {
        for (var entry : entries) {
            if (entry.role().equals("default")) {
                return entry.permissions();
            }
        }
        return new ArrayList<>();
    }

    public void initPermissions() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("config/" + serverID + "/permissions.json"));
            JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
            array.forEach(element -> {
                JsonObject object = element.getAsJsonObject();
                String role = object.get("role").getAsString();
                ArrayList<String> perms = new ArrayList<>();
                object.get("permissions").getAsJsonArray().forEach(perm -> perms.add(perm.getAsString()));
                entries.add(new RolePermissions(role, perms));
            });
        } catch (Exception e) {
            createDefault();
            initPermissions();
        }
    }

    private void createDefault() {
        try {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            FileUtils.createDir("config/" + serverID);
            FileWriter writer = new FileWriter("config/" + serverID + "/permissions.json");
            writer.write(gson.toJson(DEFAULT));
            writer.close();
        } catch (Exception e) {
            Bot.LOGGER.info("Failed to create default permissions config due to an exception: " + e);
        }
    }

    private void saveData() {
        try{
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            FileWriter writer = new FileWriter("config/" + serverID + "/permissions.json");
            writer.write(gson.toJson(entries));
            writer.close();
        } catch (Exception e) {
            Bot.LOGGER.info("Failed to save permissions due to an exception: " + e);
        }
    }
}
