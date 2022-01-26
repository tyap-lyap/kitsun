package ru.pinkgoosik.kitsun.cosmetica;

import java.util.ArrayList;
import java.util.List;

public class Entry {
    public User user;
    public Cloak cloak;
    public List<String> attributes = new ArrayList<>();
    public List<CosmeticElement> cosmetics = new ArrayList<>();

    public static class User {
        public String name, uuid, discord;
    }
    public static class Cloak {
        public String name, color;
        public boolean glint;
    }
    public static class CosmeticElement {
        public String name, placement, color;
    }
}
