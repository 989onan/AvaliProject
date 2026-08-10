package com.lunkoashtail.avaliproject.entity.ai;

import com.lunkoashtail.avaliproject.entity.custom.AvaliEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;
import java.util.UUID;

public class FaceInteractorGoal extends Goal {
    private final AvaliEntity avali;

    public FaceInteractorGoal(AvaliEntity avali) {
        this.avali = avali;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return avali.getInteractingPlayer() != null;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void start() {
        avali.getNavigation().stop();
    }

    private static final double MAX_INTERACT_DISTANCE_SQR = 16.0 * 16.0;
    private static final long IDLE_TIMEOUT_TICKS = 100;

    @Override
    public void tick() {
        UUID id = avali.getInteractingPlayer();
        if (id == null)
            return;
        boolean stale = avali.level().getGameTime() - avali.getLastInteractionTick() > IDLE_TIMEOUT_TICKS;
        if (stale || !(avali.level().getPlayerByUUID(id) instanceof Player player) || avali.distanceToSqr(player) > MAX_INTERACT_DISTANCE_SQR) {
            avali.setInteractingPlayer(null);
            return;
        }
        avali.getNavigation().stop();
        avali.getLookControl().setLookAt(player, 30f, 30f);
    }
}
