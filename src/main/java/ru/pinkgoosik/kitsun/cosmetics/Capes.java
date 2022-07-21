package ru.pinkgoosik.kitsun.cosmetics;

import java.util.ArrayList;
import java.util.List;

public class Capes {
    public static final List<String> COLORED_CLOAKS = new ArrayList<>(List.of("azure", "crimson", "flamingo", "golden", "lapis", "military", "mint", "mystic", "pumpkin", "smoky", "turtle", "violet", "void", "coffee"));

    public static boolean exist(String cloak) {
        return Capes.COLORED_CLOAKS.contains(cloak);
    }
}
