package com.lunkoashtail.avaliproject.entity.ai;

import com.lunkoashtail.avaliproject.entity.custom.AvaliEntity;
import com.lunkoashtail.avaliproject.network.AvaliRecruitProposalPayload;
import com.lunkoashtail.avaliproject.pack.PerPlayerTrust;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class ProposeRecruitGoal extends Goal {
    private static final int CHECK_INTERVAL_TICKS = 100;
    private static final int PROPOSAL_TRUST_THRESHOLD = 90;
    private static final int REARM_TRUST_GAP = 15;
    private static final double SEARCH_RADIUS = 8.0;

    private final AvaliEntity avali;
    private int cooldown;

    public ProposeRecruitGoal(AvaliEntity avali) {
        this.avali = avali;
    }

    @Override
    public boolean canUse() {
        return !avali.isTame();
    }

    @Override
    public boolean isInterruptable() {
        return true;
    }

    @Override
    public void tick() {
        if (cooldown-- > 0)
            return;
        cooldown = CHECK_INTERVAL_TICKS;
        if (!(avali.level() instanceof ServerLevel serverLevel))
            return;

        List<Player> nearby = serverLevel.getEntitiesOfClass(Player.class, avali.getBoundingBox().inflate(SEARCH_RADIUS));
        for (Player player : nearby) {
            if (!(player instanceof ServerPlayer serverPlayer))
                continue;
            PerPlayerTrust trust = avali.getTrustMemory().get(player.getUUID());
            boolean canRearm = !trust.proposed() || trust.trust() - trust.trustAtLastProposal() >= REARM_TRUST_GAP;
            if (trust.trust() >= PROPOSAL_TRUST_THRESHOLD && canRearm) {
                avali.getTrustMemory().put(player.getUUID(), trust.markProposed());
                PacketDistributor.sendToPlayer(serverPlayer, new AvaliRecruitProposalPayload(avali.getId()));
            }
        }
    }
}
