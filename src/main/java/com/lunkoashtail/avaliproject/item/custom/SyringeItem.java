package com.lunkoashtail.avaliproject.item.custom;

import com.lunkoashtail.avaliproject.client.ClientPayloadHandlers;
import com.lunkoashtail.avaliproject.component.ModDataComponents;
import com.lunkoashtail.avaliproject.component.SyringeContents;
import com.lunkoashtail.avaliproject.item.ModItems;
import com.lunkoashtail.avaliproject.limb.LimbData;
import com.lunkoashtail.avaliproject.limb.ModAttachments;
import com.lunkoashtail.avaliproject.network.TargetLimbDataSyncPayload;
import com.lunkoashtail.avaliproject.species.Species;
import net.minecraft.ChatFormatting;
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

public class SyringeItem extends Item {

    public static final float CAPACITY = 500f;

    public SyringeItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            return InteractionResultHolder.sidedSuccess(stack, false);
        }

        SyringeContents contents = stack.get(ModDataComponents.SYRINGE_CONTENTS);

        if (contents == null && player.getOffhandItem().is(ModItems.EMPTY_BLOOD_BAG.get())) {
            ClientPayloadHandlers.openBloodDrawScreen();
            return InteractionResultHolder.sidedSuccess(stack, true);
        }

        if (player.getData(ModAttachments.SPECIES) != Species.EXPIE) {
            player.sendSystemMessage(Component.literal("You're not an expie"));
            return InteractionResultHolder.fail(stack);
        }

        if (contents == null || player.isShiftKeyDown()) {
            ClientPayloadHandlers.openSyringeDrawScreen(hand);
        } else {
            ClientPayloadHandlers.openSyringeLimbSelection(contents, hand);
        }

        return InteractionResultHolder.sidedSuccess(stack, true);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!(target instanceof Player targetPlayer) || targetPlayer == player) return InteractionResult.PASS;

        SyringeContents contents = stack.get(ModDataComponents.SYRINGE_CONTENTS);
        if (contents == null) return InteractionResult.PASS;

        if (!player.level().isClientSide()) {
            if (targetPlayer.getData(ModAttachments.SPECIES) != Species.EXPIE) {
                player.sendSystemMessage(Component.literal("They're not an expie"));
                return InteractionResult.FAIL;
            }
            LimbData data = targetPlayer.getData(ModAttachments.LIMB_DATA);
            PacketDistributor.sendToPlayer((ServerPlayer) player, TargetLimbDataSyncPayload.from(targetPlayer.getId(), data));
        } else {
            ClientPayloadHandlers.openSyringeLimbSelection(contents, hand, targetPlayer.getId());
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        SyringeContents contents = stack.get(ModDataComponents.SYRINGE_CONTENTS);
        if (contents != null) {
            String drugName = contents.drugType() == DrugType.FENTANYL ? "Fentanyl" : "Heroin";
            tooltipComponents.add(Component.literal(drugName + ": " + (int) contents.dosage() + " mL")
                    .withStyle(ChatFormatting.AQUA));
        } else {
            tooltipComponents.add(Component.literal("Empty").withStyle(ChatFormatting.GRAY));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
