package com.lunkoashtail.avaliproject.item.custom;

import com.lunkoashtail.avaliproject.item.ModToolTiers;
import com.lunkoashtail.avaliproject.util.AerogelDegradation;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class AeroAxeItem extends AxeItem {
    private static final double[] TIER_DAMAGE = {15.0, 10.0, 8.0};
    private static final double ATTACK_SPEED = -2.8;

    public AeroAxeItem() {
        super(ModToolTiers.AEROGEL, new Item.Properties()
                .attributes(AerogelDegradation.buildModifiers(TIER_DAMAGE[0], ATTACK_SPEED)));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.avaliproject.avali_axe.tooltip"));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        AerogelDegradation.onHit(stack, TIER_DAMAGE, ATTACK_SPEED);
        return true;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        if (!level.isClientSide() && state.getDestroySpeed(level, pos) != 0.0F) {
            AerogelDegradation.onHit(stack, TIER_DAMAGE, ATTACK_SPEED);
        }
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return AerogelDegradation.handleBatteryUse(level, player, hand, TIER_DAMAGE, ATTACK_SPEED);
    }
}
