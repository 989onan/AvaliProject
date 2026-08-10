package com.lunkoashtail.avaliproject.entity.ai;

import com.lunkoashtail.avaliproject.entity.custom.ExpieEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;
import java.util.UUID;

public class ExpieFaceInteractorGoal extends Goal {
    private final ExpieEntity expie;

    public ExpieFaceInteractorGoal(ExpieEntity expie) {
        this.expie = expie;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return expie.getInteractingPlayer() != null;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void start() {
        expie.getNavigation().stop();
    }

    private static final double MAX_INTERACT_DISTANCE_SQR = 16.0 * 16.0;
    private static final long IDLE_TIMEOUT_TICKS = 100;

    @Override
    public void tick() {
        UUID id = expie.getInteractingPlayer();
        if (id == null) return;
        boolean stale = expie.level().getGameTime() - expie.getLastInteractionTick() > IDLE_TIMEOUT_TICKS;
        if (stale || !(expie.level().getPlayerByUUID(id) instanceof Player player) || expie.distanceToSqr(player) > MAX_INTERACT_DISTANCE_SQR) {
            expie.setInteractingPlayer(null);
            return;
        }
        expie.getNavigation().stop();
        expie.getLookControl().setLookAt(player, 30f, 30f);
    }
}
