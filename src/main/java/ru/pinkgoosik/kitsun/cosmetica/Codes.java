package ru.pinkgoosik.kitsun.cosmetica;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Codes {
    public static final List<Code> CODES = new ArrayList<>();

    public static Optional<Code> getCode(String name) {
        for (var code : CODES) {
            if (code.code.equals(name)) return Optional.of(code);
        }
        return Optional.empty();
    }
}
