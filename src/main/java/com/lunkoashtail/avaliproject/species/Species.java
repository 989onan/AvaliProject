package com.lunkoashtail.avaliproject.species;

import com.mojang.serialization.Codec;

public enum Species {
    HUMAN,
    EXPIE,
    AVALI,
    SERGAL,
    PROTOGEN;

    public static final Codec<Species> CODEC = Codec.STRING.xmap(Species::fromName, Species::name);

    public static Species fromName(String name) {
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return HUMAN;
        }
    }

    public int bloodColorRgb() {
        return switch (this) {
            case AVALI -> 0xB19CD9;
            case EXPIE -> 0xF4D63A;
            default -> 0xAA0000;
        };
    }

    public String bloodName() {
        return switch (this) {
            case AVALI -> "pale violet";
            case EXPIE -> "yellow";
            default -> "red";
        };
    }
}
