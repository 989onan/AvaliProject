package com.lunkoashtail.avaliproject.pack;

import com.mojang.serialization.Codec;
import net.minecraft.core.UUIDUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AvaliTrustMemory {
    private final Map<UUID, PerPlayerTrust> trust;

    public AvaliTrustMemory() {
        this.trust = new HashMap<>();
    }

    private AvaliTrustMemory(Map<UUID, PerPlayerTrust> trust) {
        this.trust = new HashMap<>(trust);
    }

    public PerPlayerTrust get(UUID player) {
        return trust.getOrDefault(player, PerPlayerTrust.INITIAL);
    }

    public void put(UUID player, PerPlayerTrust value) {
        trust.put(player, value);
    }

    public Map<UUID, PerPlayerTrust> asMap() {
        return trust;
    }

    public static final Codec<AvaliTrustMemory> CODEC =
            Codec.unboundedMap(UUIDUtil.STRING_CODEC, PerPlayerTrust.CODEC)
                    .xmap(AvaliTrustMemory::new, AvaliTrustMemory::asMap);
}
