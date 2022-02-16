package ru.pinkgoosik.kitsun.cosmetica;

import java.util.ArrayList;
import java.util.List;

public class Cloaks {
    public static final String PREVIEW_CLOAK = "https://github.com/PinkGoosik/kitsun/blob/master/img/preview/cloak/%cloak%.png?raw=true";

    public static final List<String> COLORED_CLOAKS = new ArrayList<>(List.of("azure", "crimson", "flamingo", "golden",
            "lapis", "military", "mint", "mystic", "pumpkin", "smoky", "turtle", "violet", "void", "coffee"));

    public static final List<String> PRIDE_CLOAKS = new ArrayList<>(List.of("pride", "trans", "lesbian", "gay", "pan", "bi", "non-binary",
            "genderfluid", "aromantic", "demiromantic", "asexual", "demisexual"));

    public static final List<String> PATTERNED_CLOAKS = new ArrayList<>(List.of("space"));

    public static final List<String> FANCY_CLOAKS = new ArrayList<>(List.of("uni", "jeb", "enchanted-jeb", "cosmic", "swirly"));

    public static boolean exist(String cloak) {
        return Cloaks.COLORED_CLOAKS.contains(cloak) || Cloaks.PATTERNED_CLOAKS.contains(cloak) || Cloaks.PRIDE_CLOAKS.contains(cloak) || Cloaks.FANCY_CLOAKS.contains(cloak);
    }

}
