package ru.pinkgoosik.somikbot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.therandomlabs.curseapi.CurseAPI;
import com.therandomlabs.curseapi.CurseException;
import com.therandomlabs.curseapi.file.CurseFile;
import discord4j.common.util.Snowflake;
import discord4j.discordjson.json.*;
import discord4j.rest.RestClient;
import discord4j.rest.util.Color;

import java.io.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ChangelogPublisher {

    long delayInSec = 60;
    long channelId = 896632013556162580L;
    RestClient client;
    ArrayList<Integer> cachedFileIds;

    public ChangelogPublisher(RestClient client){
        this.client = client;
        this.cachedFileIds = loadCachedFileIds();
        this.startScheduler();
    }

    void startScheduler(){
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        createMessage();
                    }
                },
                delayInSec * 1000, delayInSec * 1000
        );
    }

    void createMessage(){
        ArrayList<Integer> fileIds = new ArrayList<>();
        String latestChangelog = "";
        CurseFile latestFile = null;

        try {
            if(CurseAPI.project("490812").isPresent()){
                CurseAPI.project("490812").get().files().forEach(curseFile -> fileIds.add(curseFile.id()));


                for(CurseFile curseFile : CurseAPI.project("490812").get().files()){
                    if(fileIds.get(0).equals(curseFile.id())){
                        latestFile = curseFile;
                        try {
                            latestChangelog = curseFile.changelogPlainText();
                        } catch (CurseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (CurseException e) {
            e.printStackTrace();
        }

        if(!cachedFileIds.equals(fileIds)){
            assert latestFile != null;
            client.getChannelById(Snowflake.of(channelId)).createMessage(createEmbed(latestFile, latestChangelog)).block();
            cachedFileIds = fileIds;
            saveCachedFileIds();
        }
    }

    EmbedData createEmbed(CurseFile curseFile, String latestChangelog){
        try {
            return EmbedData.builder()
                    .author(EmbedAuthorData.builder().name(curseFile.project().name()).build())
                    .title(curseFile.displayName())
                    .url("https://www.curseforge.com/minecraft/mc-mods/artifality")
                    .description("**Changes:**\n" + latestChangelog.replaceAll("\\*", "-"))
                    .color(Color.GREEN.getRGB())
                    .thumbnail(EmbedThumbnailData.builder().url("https://media.forgecdn.net/avatars/432/50/637668908001233569.png").build())
                    .build();
        } catch (CurseException e) {
            e.printStackTrace();
        }
        return EmbedData.builder().build();
    }

    private void saveCachedFileIds(){
        try {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            FileWriter writer = new FileWriter("cached_file_ids.json");
            writer.write(gson.toJson(cachedFileIds));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Integer> loadCachedFileIds(){
        try {
            ArrayList<Integer> fileId = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader("cached_file_ids.json"));
            JsonParser.parseReader(reader).getAsJsonArray().forEach(jsonElement -> fileId.add(jsonElement.getAsInt()));
            return fileId;
        } catch (FileNotFoundException ignored) {}
        return new ArrayList<>();
    }
}
