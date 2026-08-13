package com.lunkoashtail.avaliproject.client;

import com.lunkoashtail.avaliproject.limb.Limb;
import com.lunkoashtail.avaliproject.network.TargetLimbDataSyncPayload;

import java.util.HashMap;
import java.util.Map;

public final class TargetDataCache {

    private TargetDataCache() {}

    private static final Map<Integer, int[]> BLEED_BY_ENTITY = new HashMap<>();

    public static void update(TargetLimbDataSyncPayload payload) {
        BLEED_BY_ENTITY.put(payload.targetEntityId(), new int[]{
                payload.head(), payload.leftArm(), payload.rightArm(),
                payload.back(), payload.leftLeg(), payload.rightLeg()
        });
    }

    public static int getBleed(int targetEntityId, Limb limb) {
        int[] values = BLEED_BY_ENTITY.get(targetEntityId);
        return values != null ? values[limb.ordinal()] : 0;
    }
}
