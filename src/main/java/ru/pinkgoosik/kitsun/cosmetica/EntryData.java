package ru.pinkgoosik.kitsun.cosmetica;

import java.util.ArrayList;
import java.util.List;

public class EntryData {
    public UserData user;
    public CloakData cloak;
    public List<String> attributes = new ArrayList<>();
    public List<CosmeticElement> cosmetics = new ArrayList<>();

    public static class UserData {
        public String name, uuid, discord;
    }

    public static class CloakData {
        public String name, color;
        public boolean glint;
    }

    public static class CosmeticElement {
        public String name, placement, color;
    }
}
