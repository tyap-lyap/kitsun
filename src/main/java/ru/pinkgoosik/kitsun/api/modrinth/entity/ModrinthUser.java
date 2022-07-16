package ru.pinkgoosik.kitsun.api.modrinth.entity;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class ModrinthUser {
    public String id = "";
    @SerializedName("github_id")
    public String githubId = "";
    public String username = "";
    public String name = "";
    public String email = "";
    @SerializedName("avatar_url")
    public String avatarUrl = "";
    public String bio = "";
    public String created = "";
    public String role = "";
}
