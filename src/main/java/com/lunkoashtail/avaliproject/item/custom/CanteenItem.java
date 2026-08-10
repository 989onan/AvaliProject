package com.lunkoashtail.avaliproject.item.custom;

import com.lunkoashtail.avaliproject.component.CanteenContents;
import com.lunkoashtail.avaliproject.component.FluidEntry;
import com.lunkoashtail.avaliproject.component.FluidType;
import com.lunkoashtail.avaliproject.component.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;

public class CanteenItem extends Item {

    public static final int FILL_DURATION_TICKS = 40;

    public CanteenItem() {
        super(new Item.Properties().stacksTo(1));
    }

    private static BlockHitResult findWaterSource(Level level, Player player) {
        return getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
    }

    private static boolean isWaterSource(Level level, BlockHitResult hit) {
        if (hit.getType() != HitResult.Type.BLOCK) return false;
        FluidState fluidState = level.getFluidState(hit.getBlockPos());
        return fluidState.is(FluidTags.WATER) && fluidState.isSource();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        BlockHitResult hit = findWaterSource(level, player);
        if (!isWaterSource(level, hit)) {
            return InteractionResultHolder.pass(stack);
        }

        CanteenContents contents = stack.getOrDefault(ModDataComponents.CANTEEN_CONTENTS, CanteenContents.EMPTY);
        if (contents.totalMl() >= CanteenContents.CAPACITY_ML) {
            return InteractionResultHolder.pass(stack);
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return FILL_DURATION_TICKS;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (level.isClientSide() || !(livingEntity instanceof Player player)) return;

        BlockHitResult hit = findWaterSource(level, player);
        if (!isWaterSource(level, hit)) {
            player.stopUsingItem();
            return;
        }

        int elapsed = FILL_DURATION_TICKS - remainingUseDuration;
        float targetMl = CanteenContents.CAPACITY_ML * (elapsed / (float) FILL_DURATION_TICKS);
        CanteenContents contents = stack.getOrDefault(ModDataComponents.CANTEEN_CONTENTS, CanteenContents.EMPTY);
        stack.set(ModDataComponents.CANTEEN_CONTENTS, contents.withSet(FluidType.WATER, targetMl));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide() && entity instanceof Player player) {
            CanteenContents contents = stack.getOrDefault(ModDataComponents.CANTEEN_CONTENTS, CanteenContents.EMPTY);
            stack.set(ModDataComponents.CANTEEN_CONTENTS, contents.withSet(FluidType.WATER, CanteenContents.CAPACITY_ML));
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BUCKET_FILL, SoundSource.PLAYERS, 1.0f, 1.0f);
        }
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        CanteenContents contents = stack.get(ModDataComponents.CANTEEN_CONTENTS);
        if (contents == null || contents.isEmpty()) {
            tooltip.add(Component.literal("Empty").withStyle(ChatFormatting.GRAY));
        } else {
            for (FluidEntry entry : contents.fluids()) {
                tooltip.add(Component.empty()
                        .append(entry.type().displayName())
                        .append(Component.literal(": " + (int) entry.amountMl() + " mL"))
                        .withStyle(ChatFormatting.AQUA));
            }
        }
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
