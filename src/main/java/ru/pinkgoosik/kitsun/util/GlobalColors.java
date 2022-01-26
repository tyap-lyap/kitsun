package ru.pinkgoosik.kitsun.util;

import discord4j.rest.util.Color;

public class GlobalColors {
    public static final Color BLUE = Color.of(96,141,238);
    public static final Color GREEN = Color.of(145,219,105);
    public static final Color RED = Color.of(246,129,129);

    public static String toHex(Color color) {
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        return String.format("#%02X%02X%02X", red, green, blue);
    }
}
