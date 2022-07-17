package ru.pinkgoosik.kitsun.util;

import discord4j.rest.util.Color;

import java.util.Random;

import static discord4j.rest.util.Color.of;

public class KitsunColors {
    private static final Random RANDOM = new Random();
    public static final Color BLUE = of(96, 141, 238);
    public static final Color GREEN = of(145, 219, 105);
    public static final Color RED = of(246, 129, 129);

    public static final Color[] RED_COLORS = new Color[]{of(193, 78, 101), of(215, 101, 113), of(246, 129, 129), of(255, 163, 156)};
    public static final Color[] GREEN_COLORS = new Color[]{of(74, 163, 104), of(92, 198, 98), of(145, 219, 105), of(188, 246, 131)};
    public static final Color[] BLUE_COLORS = new Color[]{of(103, 104, 198), of(116, 131, 224), of(123, 164, 255), of(149, 196, 255)};
	
	public static final Color[] CYAN_COLORS = new Color[]{of(58, 185, 131), of(74, 211, 139), of(103, 233, 165)};
	
    public static Color getRed() {
        return RED_COLORS[RANDOM.nextInt(RED_COLORS.length)];
    }

    public static Color getGreen() {
        return GREEN_COLORS[RANDOM.nextInt(GREEN_COLORS.length)];
    }

    public static Color getBlue() {
        return BLUE_COLORS[RANDOM.nextInt(BLUE_COLORS.length)];
    }
	
	public static Color getCyan() {
        return CYAN_COLORS[RANDOM.nextInt(CYAN_COLORS.length)];
    }

    public static String toHex(Color color) {
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        return String.format("#%02X%02X%02X", red, green, blue);
    }
}
