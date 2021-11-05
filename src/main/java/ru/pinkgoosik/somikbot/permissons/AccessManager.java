package ru.pinkgoosik.somikbot.permissons;

import com.google.gson.*;
import discord4j.core.object.entity.Member;
import ru.pinkgoosik.somikbot.Bot;
import ru.pinkgoosik.somikbot.util.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AccessManager {
    private static final ArrayList<RolePermissions> EMPTY = new ArrayList<>(List.of(new RolePermissions("default", new ArrayList<>(List.of(Permissions.HELP, Permissions.CLOAKS, Permissions.CLOAK_GRANT, Permissions.CLOAK_REVOKE_SELF)))));
    private static final ArrayList<RolePermissions> ENTRIES = new ArrayList<>();

    public static boolean hasAccessTo(Member member, String permission){
        if (getDefaultPermissions().contains(permission)) return true;
        ArrayList<String> roles = new ArrayList<>();
        member.getRoleIds().forEach(snowflake -> roles.add(snowflake.asString()));
        for(var entry : ENTRIES){
            if(roles.contains(entry.role()) && entry.permissions().contains(permission)) return true;
        }
        return false;
    }

    public static void grant(String role, String permission){
        for(var entry : ENTRIES){
            if (entry.role().equals(role)){
                if (!entry.permissions().contains(permission)){
                    entry.permissions().add(permission);
                    saveData();
                    return;
                }
            }
        }
        ENTRIES.add(new RolePermissions(role, new ArrayList<>(List.of(permission))));
        saveData();
    }

    public static ArrayList<String> getDefaultPermissions() {
        for (var entry : ENTRIES) {
            if (entry.role().equals("default")) {
                return entry.permissions();
            }
        }
        return new ArrayList<>();
    }

    public static void initPermissions(){
        try{
            BufferedReader reader = new BufferedReader(new FileReader("config/permissions.json"));
            JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
            array.forEach(element -> {
                JsonObject object = element.getAsJsonObject();
                String role = object.get("role").getAsString();
                ArrayList<String> perms = new ArrayList<>();
                object.get("permissions").getAsJsonArray().forEach(perm -> perms.add(perm.getAsString()));
                ENTRIES.add(new RolePermissions(role, perms));
            });
        } catch (FileNotFoundException e) {
            createEmpty();
            initPermissions();
        }
    }

    private static void createEmpty(){
        try {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            FileUtils.createDir("config");
            FileWriter writer = new FileWriter("config/permissions.json");
            writer.write(gson.toJson(EMPTY));
            writer.close();
        } catch (IOException e) {
            Bot.LOGGER.info("Failed to create empty general config due to an exception: " + e);
        }
    }

    private static void saveData(){
        try{
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            FileWriter writer = new FileWriter("config/permissions.json");
            writer.write(gson.toJson(ENTRIES));
            writer.close();
        } catch (IOException e) {
            Bot.LOGGER.info("Failed to save permissions due to an exception: " + e);
        }
    }
}
