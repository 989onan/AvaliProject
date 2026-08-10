package com.lunkoashtail.avaliproject.entity.client;

import java.util.Arrays;
import java.util.Comparator;

public enum ExpieVariant {
    NORMAL(0),
    WHITE(1);
    private static final ExpieVariant[] BY_ID = Arrays.stream(values()).sorted(
            Comparator.comparingInt(ExpieVariant::getId)).toArray(ExpieVariant[]::new);
    private final int id;
    ExpieVariant(int id) { this.id = id; }
    public int getId() { return id; }
    public static ExpieVariant byId(int id) { return BY_ID[id % BY_ID.length]; }
}
