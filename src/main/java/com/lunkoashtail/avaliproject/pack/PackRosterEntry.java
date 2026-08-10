package com.lunkoashtail.avaliproject.pack;

import java.util.UUID;

public record PackRosterEntry(UUID id, String name, boolean isAvali, boolean online,
                               boolean male, boolean baby, int health, int maxHealth, int trust) {

    public static PackRosterEntry forPlayer(UUID id, String name, boolean online) {
        return new PackRosterEntry(id, name, false, online, true, false, 0, 0, 0);
    }

    public static PackRosterEntry forAvali(UUID id, String name, boolean male, boolean baby, int health, int maxHealth, int trust) {
        return new PackRosterEntry(id, name, true, true, male, baby, health, maxHealth, trust);
    }
}
