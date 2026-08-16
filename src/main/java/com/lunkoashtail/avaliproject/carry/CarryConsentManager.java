package com.lunkoashtail.avaliproject.carry;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CarryConsentManager {

    private CarryConsentManager() {}

    private static final long TIMEOUT_TICKS = 20L * 30L;

    private record PendingRequest(UUID requesterUUID, long expiryGameTime) {}

    private static final Map<UUID, PendingRequest> PENDING_BY_TARGET = new HashMap<>();

    public static void request(ServerPlayer requester, ServerPlayer target) {
        PENDING_BY_TARGET.put(target.getUUID(),
                new PendingRequest(requester.getUUID(), target.level().getGameTime() + TIMEOUT_TICKS));
    }

    @Nullable
    public static ServerPlayer consumeIfValid(ServerPlayer target, int expectedRequesterEntityId) {
        PendingRequest pending = PENDING_BY_TARGET.remove(target.getUUID());
        if (pending == null) return null;
        if (target.level().getGameTime() > pending.expiryGameTime()) return null;

        Entity requesterEntity = target.level().getEntity(expectedRequesterEntityId);
        if (!(requesterEntity instanceof ServerPlayer requester)) return null;
        if (!requester.getUUID().equals(pending.requesterUUID())) return null;
        return requester;
    }

    public static void clear(ServerPlayer target) {
        PENDING_BY_TARGET.remove(target.getUUID());
    }
}
