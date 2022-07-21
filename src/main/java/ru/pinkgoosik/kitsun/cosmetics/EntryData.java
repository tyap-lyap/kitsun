package ru.pinkgoosik.kitsun.cosmetics;

import java.util.ArrayList;
import java.util.List;

public class EntryData {
    public UserData user;
    public CapeData cape;
    public List<String> cosmetics = new ArrayList<>();

    public static class UserData {
        public String name, uuid, discord;
    }

    public static class CapeData {
        public String name;
        public boolean glint;
    }
}
