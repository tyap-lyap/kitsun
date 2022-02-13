package ru.pinkgoosik.kitsun.api.modrinth.entity;

import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings("unused")
public class GalleryEntry {
    public String url = "";
    public boolean featured = false;
    @Nullable
    public String title = "";
    @Nullable
    public String description = "";
    public String created = "";
}
