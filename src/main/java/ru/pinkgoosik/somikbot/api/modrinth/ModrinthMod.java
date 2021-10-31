package ru.pinkgoosik.somikbot.api.modrinth;

import java.util.ArrayList;

public record ModrinthMod(String modUrl, String iconUrl, String modId, String modSlug, String title, String shortDescription,
                          int downloads, int followers, ArrayList<ModVersion> versions) {}
