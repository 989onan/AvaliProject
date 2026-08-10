package com.lunkoashtail.avaliproject.pack;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class PackRecord {
    public static final int MAX_TOTAL_MEMBERS = 6;

    private String name;
    private final Set<UUID> memberUUIDs = new LinkedHashSet<>();

    public PackRecord(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<UUID> getMemberUUIDs() {
        return memberUUIDs;
    }
}
