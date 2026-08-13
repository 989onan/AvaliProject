package com.lunkoashtail.avaliproject.item.custom;

import com.lunkoashtail.avaliproject.client.ClientPayloadHandlers;
import com.lunkoashtail.avaliproject.limb.LimbData;
import com.lunkoashtail.avaliproject.limb.ModAttachments;
import com.lunkoashtail.avaliproject.network.TargetLimbDataSyncPayload;
import com.lunkoashtail.avaliproject.species.Species;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class DressingItem extends Item {

    public DressingItem() {
        super(new Item.Properties().durability(2 * LimbData.MAX_BLEED));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            if (player.getData(ModAttachments.SPECIES) != Species.EXPIE) {
                player.sendSystemMessage(Component.literal("You're not an expie"));
                return InteractionResultHolder.fail(player.getItemInHand(hand));
            }
            ClientPayloadHandlers.openDressingLimbSelection(player, hand);
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!(target instanceof Player targetPlayer) || targetPlayer == player) return InteractionResult.PASS;

        if (!player.level().isClientSide()) {
            if (targetPlayer.getData(ModAttachments.SPECIES) != Species.EXPIE) {
                player.sendSystemMessage(Component.literal("They're not an expie"));
                return InteractionResult.FAIL;
            }
            LimbData data = targetPlayer.getData(ModAttachments.LIMB_DATA);
            PacketDistributor.sendToPlayer((ServerPlayer) player, TargetLimbDataSyncPayload.from(targetPlayer.getId(), data));
        } else {
            ClientPayloadHandlers.openDressingLimbSelection(player, hand, targetPlayer.getId());
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.avaliproject.dressing.tooltip"));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
