package ru.pinkgoosik.kitsun.util;

import discord4j.rest.util.Color;

import java.util.Random;

import static discord4j.rest.util.Color.of;

public class KitsunColors {
    private static final Random RANDOM = new Random();
    public static final Color BLUE = of(96,141,238);
    public static final Color GREEN = of(145,219,105);
    public static final Color RED = of(246,129,129);

    public static final Color[] RED_COLORS = new Color[]{of(246,129,129), of(211, 93, 106), of(189, 66, 91), of(224, 101, 99), of(202, 76, 79)};
    public static final Color[] GREEN_COLORS = new Color[]{of(145,219,105), of(98, 185, 79), of(68, 158, 71), of(92, 198, 98), of(76, 167, 107)};
    public static final Color[] BLUE_COLORS = new Color[]{of(123, 164, 255), of(111, 126, 220), of(106, 95, 198), of(97, 135, 220), of(76, 111, 189)};
    public static Color getRed() {
        return RED_COLORS[RANDOM.nextInt(RED_COLORS.length)];
    }

    public static Color getGreen() {
        return GREEN_COLORS[RANDOM.nextInt(GREEN_COLORS.length)];
    }

    public static Color getBlue() {
        return BLUE_COLORS[RANDOM.nextInt(BLUE_COLORS.length)];
    }

    public static String toHex(Color color) {
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        return String.format("#%02X%02X%02X", red, green, blue);
    }
}
