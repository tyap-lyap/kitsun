package ru.pinkgoosik.somikbot.api;

import java.util.ArrayList;

public record ModrinthMod(String modUrl, String iconUrl, String modId, String modSlug, String title, String shortDescription,
                          int downloads, int followers, ArrayList<ModVersion> versions) {

    public static final ModrinthMod EMPTY = new ModrinthMod("", "", "", "", "", "",
            0, 0, new ArrayList<>());
}
