package com.lunkoashtail.avaliproject.util;

import com.lunkoashtail.avaliproject.component.AerogelToolState;
import com.lunkoashtail.avaliproject.component.ModDataComponents;
import com.lunkoashtail.avaliproject.item.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;

public class AerogelDegradation {
    public static final int HITS_PER_TIER = 50;

    public static int tierForHitCount(int hitCount, int tierCount) {
        return Math.min(hitCount / HITS_PER_TIER, tierCount - 1);
    }

    public static ItemAttributeModifiers buildModifiers(double attackDamage, double attackSpeed) {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, attackDamage, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
                .build();
    }

    public static void onHit(ItemStack stack, double[] tierDamage, double attackSpeed) {
        AerogelToolState state = stack.getOrDefault(ModDataComponents.AEROGEL_TOOL_STATE.get(), AerogelToolState.FRESH);
        int oldTier = tierForHitCount(state.hitCount(), tierDamage.length);
        int newHitCount = state.hitCount() + 1;
        int newTier = tierForHitCount(newHitCount, tierDamage.length);
        stack.set(ModDataComponents.AEROGEL_TOOL_STATE.get(), new AerogelToolState(newHitCount, state.batteryStored()));
        if (newTier != oldTier) {
            stack.set(DataComponents.ATTRIBUTE_MODIFIERS, buildModifiers(tierDamage[newTier], attackSpeed));
        }
    }

    public static InteractionResultHolder<ItemStack> handleBatteryUse(Level level, Player player, InteractionHand hand, double[] tierDamage, double attackSpeed) {
        ItemStack stack = player.getItemInHand(hand);
        AerogelToolState state = stack.getOrDefault(ModDataComponents.AEROGEL_TOOL_STATE.get(), AerogelToolState.FRESH);

        if (player.isShiftKeyDown()) {
            if (!state.batteryStored()) {
                if (!level.isClientSide())
                    player.displayClientMessage(Component.translatable("message.avaliproject.aerogel_tool.no_battery"), true);
                return InteractionResultHolder.fail(stack);
            }
            if (!level.isClientSide()) {
                stack.set(ModDataComponents.AEROGEL_TOOL_STATE.get(), new AerogelToolState(0, false));
                stack.set(DataComponents.ATTRIBUTE_MODIFIERS, buildModifiers(tierDamage[0], attackSpeed));
                player.level().playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0f, 1.2f);
                player.displayClientMessage(Component.translatable("message.avaliproject.aerogel_tool.restored"), true);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        if (state.batteryStored()) {
            return InteractionResultHolder.pass(stack);
        }
        ItemStack offhand = player.getOffhandItem();
        if (!offhand.is(ModItems.AEROGEL_BATTERY.get())) {
            return InteractionResultHolder.pass(stack);
        }
        if (!level.isClientSide()) {
            offhand.shrink(1);
            stack.set(ModDataComponents.AEROGEL_TOOL_STATE.get(), new AerogelToolState(state.hitCount(), true));
            player.displayClientMessage(Component.translatable("message.avaliproject.aerogel_tool.battery_installed"), true);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
