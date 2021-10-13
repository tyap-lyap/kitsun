package ru.pinkgoosik.somikbot.api;

import java.util.ArrayList;

public class ModrinthMod {
    public String modUrl = "";
    public String iconUrl = "";
    public String modId = "";
    public String modSlug = "";
    public String title = "";
    public String shortDescription = "";
    public int downloads = 0;
    public int followers = 0;
    public ArrayList<ModVersion> versions = new ArrayList<>();

    public boolean isEmpty(){
        return this.title.equals("");
    }
}
