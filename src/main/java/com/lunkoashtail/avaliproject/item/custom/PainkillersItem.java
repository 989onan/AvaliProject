package com.lunkoashtail.avaliproject.item.custom;

import com.lunkoashtail.avaliproject.limb.ModAttachments;
import com.lunkoashtail.avaliproject.limb.PainData;
import com.lunkoashtail.avaliproject.network.PainSyncPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class PainkillersItem extends Item {

    private static final float REDUCTION_FRACTION = 0.33f;

    public PainkillersItem() {
        super(new Item.Properties().stacksTo(16));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            PainData pain = player.getData(ModAttachments.PAIN_DATA);
            pain.set(pain.get() * (1f - REDUCTION_FRACTION));
            PacketDistributor.sendToPlayer(serverPlayer, PainSyncPayload.from(pain));

            player.playSound(SoundEvents.GENERIC_DRINK, 1.0f, 1.0f);
            stack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.avaliproject.painkillers.tooltip"));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
