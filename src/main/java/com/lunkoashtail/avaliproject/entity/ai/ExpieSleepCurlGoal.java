package com.lunkoashtail.avaliproject.entity.ai;

import com.lunkoashtail.avaliproject.entity.custom.ExpieEntity;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;
import java.util.UUID;

public class ExpieSleepCurlGoal extends Goal {
    private static final double SETTLE_DISTANCE = 1.3;
    private static final double SPEED_MODIFIER = 1.0;

    private final ExpieEntity expie;
    private final PathNavigation navigation;
    private Player target;
    private int timeToRecalcPath;

    public ExpieSleepCurlGoal(ExpieEntity expie) {
        this.expie = expie;
        this.navigation = expie.getNavigation();
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        UUID clingyTo = expie.getClingyTarget();
        if (clingyTo == null) return false;
        if (!(expie.level().getPlayerByUUID(clingyTo) instanceof Player player)) return false;
        if (!player.isSleeping()) return false;

        this.target = player;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return target != null && target.isAlive() && target.isSleeping();
    }

    @Override
    public void start() {
        timeToRecalcPath = 0;
        expie.setSleepingNearPlayer(false);
    }

    @Override
    public void stop() {
        target = null;
        expie.setSleepingNearPlayer(false);
        navigation.stop();
    }

    @Override
    public void tick() {
        if (target == null) return;

        double distSqr = expie.distanceToSqr(target);
        if (distSqr <= SETTLE_DISTANCE * SETTLE_DISTANCE) {
            navigation.stop();
            expie.setSleepingNearPlayer(true);
            expie.getLookControl().setLookAt(target, 30f, 30f);
            return;
        }

        expie.setSleepingNearPlayer(false);
        if (--timeToRecalcPath <= 0) {
            timeToRecalcPath = adjustedTickDelay(5);
            navigation.moveTo(target, SPEED_MODIFIER);
        }
    }
}
