package com.lunkoashtail.avaliproject.item.custom;

import com.lunkoashtail.avaliproject.limb.LimbData;
import com.lunkoashtail.avaliproject.limb.ModAttachments;
import com.lunkoashtail.avaliproject.screen.custom.DressingMinigameScreen;
import com.lunkoashtail.avaliproject.screen.custom.LimbSelectionScreen;
import com.lunkoashtail.avaliproject.species.Species;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

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
            Minecraft.getInstance().setScreen(new LimbSelectionScreen(selectedLimb -> {
                int bleed = player.getData(ModAttachments.LIMB_DATA).getBleed(selectedLimb);
                Minecraft.getInstance().setScreen(new DressingMinigameScreen(selectedLimb, bleed, hand));
            }));
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.avaliproject.dressing.tooltip"));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
