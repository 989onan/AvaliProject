package com.lunkoashtail.avaliproject.carry;

import com.lunkoashtail.avaliproject.entity.custom.ExpieEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class CarryUtil {

    private CarryUtil() {}

    public static final double SEARCH_RADIUS = 8.0;
    public static final double MAX_CARRY_DISTANCE_SQ = 64.0;

    public static boolean isUnencumbered(Entity entity) {
        return !entity.isPassenger() && entity.getPassengers().isEmpty();
    }

    public static List<CarryCandidateEntry> findNearbyCandidates(ServerPlayer requester) {
        ServerLevel level = (ServerLevel) requester.level();
        AABB area = requester.getBoundingBox().inflate(SEARCH_RADIUS);
        List<CarryCandidateEntry> result = new ArrayList<>();

        for (Player player : level.getEntitiesOfClass(Player.class, area)) {
            if (player == requester || !isUnencumbered(player)) continue;
            result.add(new CarryCandidateEntry(player.getId(), player.getUUID(), player.getName().getString(),
                    true, Math.sqrt(player.distanceToSqr(requester))));
        }
        for (ExpieEntity expie : level.getEntitiesOfClass(ExpieEntity.class, area)) {
            if (!isUnencumbered(expie)) continue;
            result.add(new CarryCandidateEntry(expie.getId(), expie.getUUID(), expie.getDisplayName().getString(),
                    false, Math.sqrt(expie.distanceToSqr(requester))));
        }

        result.sort(Comparator.comparingDouble(CarryCandidateEntry::distance));
        return result;
    }

    public static boolean canStartCarry(Entity carrier, Entity passenger) {
        return carrier.isAlive() && passenger.isAlive()
                && carrier.level() == passenger.level()
                && isUnencumbered(carrier) && isUnencumbered(passenger)
                && carrier.distanceToSqr(passenger) <= MAX_CARRY_DISTANCE_SQ;
    }
}
