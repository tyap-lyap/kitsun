package ru.pinkgoosik.kitsun.permission;

import com.google.gson.*;
import discord4j.core.object.entity.Member;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.util.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static ru.pinkgoosik.kitsun.permission.Permissions.*;

public class AccessManager {
    private static final ArrayList<RolePermissions> DEFAULT = new ArrayList<>(
            List.of(
                    new RolePermissions("non-registered", false, new ArrayList<>(
                            List.of(HELP, REGISTER))),
                    new RolePermissions("registered", true, new ArrayList<>(
                            List.of(HELP, AVAILABLE_CLOAKS, AVAILABLE_ATTRIBUTES,
                                    AVAILABLE_COSMETICS, REDEEM, CLOAK_SET,
                                    CLOAK_REVOKE_SELF, UNREGISTER)))));


    private final ArrayList<RolePermissions> entries = new ArrayList<>();
    public final String serverID;

    public AccessManager(String serverID) {
        this.serverID = serverID;
    }

    public boolean hasAccessTo(Member member, boolean registered, String permission) {
        if (getDefaultPermissions(registered).contains(permission)) return true;
        ArrayList<String> roles = new ArrayList<>();
        member.getRoleIds().forEach(snowflake -> roles.add(snowflake.asString()));
        for (var entry : entries) {
            if(roles.contains(entry.role()) && entry.permissions().contains(permission)) return true;
        }
        return false;
    }

    public boolean hasAccessTo(Member member, String permission) {
        return hasAccessTo(member, true, permission);
    }

    public void grant(String role, boolean registered, String permission) {
        for (var entry : entries) {
            if (entry.role().equals(role)) {
                if (!entry.permissions().contains(permission)) {
                    entry.permissions().add(permission);
                    saveData();
                    return;
                }
            }
        }
        entries.add(new RolePermissions(role, registered, new ArrayList<>(List.of(permission))));
        saveData();
    }

    public List<String> getDefaultPermissions(boolean registered) {
        for (var entry : entries) {
            if (registered ? entry.role().equals("registered") : entry.role().equals("non-registered")) {
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
                boolean registered = object.get("registered").getAsBoolean();
                ArrayList<String> perms = new ArrayList<>();
                object.get("permissions").getAsJsonArray().forEach(perm -> perms.add(perm.getAsString()));
                entries.add(new RolePermissions(role, registered, perms));
            });
        } catch (FileNotFoundException e) {
            createEmpty();
            initPermissions();
        }
    }

    private void createEmpty() {
        try {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            FileUtils.createDir("config/" + serverID);
            FileWriter writer = new FileWriter("config/" + serverID + "/permissions.json");
            writer.write(gson.toJson(DEFAULT));
            writer.close();
        } catch (IOException e) {
            Bot.LOGGER.info("Failed to create empty permissions config due to an exception: " + e);
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
        } catch (IOException e) {
            Bot.LOGGER.info("Failed to save permissions due to an exception: " + e);
        }
    }
}
