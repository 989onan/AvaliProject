package com.lunkoashtail.avaliproject.network;

import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.component.DrugDosage;
import com.lunkoashtail.avaliproject.component.ModDataComponents;
import com.lunkoashtail.avaliproject.component.SyringeContents;
import com.lunkoashtail.avaliproject.item.ModItems;
import com.lunkoashtail.avaliproject.item.custom.DrugType;
import com.lunkoashtail.avaliproject.item.custom.SyringeItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyringeLoadPayload(int handOrdinal, int slotIndex, int drugTypeOrdinal, float amount)
        implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyringeLoadPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "syringe_load"));

    public static final StreamCodec<ByteBuf, SyringeLoadPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, SyringeLoadPayload::handOrdinal,
                    ByteBufCodecs.INT, SyringeLoadPayload::slotIndex,
                    ByteBufCodecs.INT, SyringeLoadPayload::drugTypeOrdinal,
                    ByteBufCodecs.FLOAT, SyringeLoadPayload::amount,
                    SyringeLoadPayload::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyringeLoadPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            InteractionHand hand = payload.handOrdinal() == 0 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            ItemStack syringeStack = player.getItemInHand(hand);
            if (!(syringeStack.getItem() instanceof SyringeItem)) return;

            DrugType[] types = DrugType.values();
            if (payload.drugTypeOrdinal() < 0 || payload.drugTypeOrdinal() >= types.length) return;
            DrugType drugType = types[payload.drugTypeOrdinal()];

            if (payload.slotIndex() < 0 || payload.slotIndex() >= player.getInventory().getContainerSize()) return;
            ItemStack sourceStack = player.getInventory().getItem(payload.slotIndex());

            boolean itemMatches = (drugType == DrugType.FENTANYL && sourceStack.is(ModItems.FENTANYL.get()))
                    || (drugType == DrugType.HEROIN && sourceStack.is(ModItems.HEROIN.get()));
            if (!itemMatches) return;

            DrugDosage sourceDosage = sourceStack.get(ModDataComponents.DRUG_DOSAGE);
            if (sourceDosage == null || sourceDosage.dosage() <= 0f) return;

            SyringeContents existing = syringeStack.get(ModDataComponents.SYRINGE_CONTENTS);
            if (existing != null && existing.drugType() != drugType) return;

            float existingDosage = existing != null ? existing.dosage() : 0f;
            float capacityLeft = SyringeItem.CAPACITY - existingDosage;
            float actualDraw = Math.max(0f, Math.min(payload.amount(), Math.min(capacityLeft, sourceDosage.dosage())));
            if (actualDraw <= 0f) return;

            syringeStack.set(ModDataComponents.SYRINGE_CONTENTS,
                    new SyringeContents(drugType, existingDosage + actualDraw));

            float leftoverSource = sourceDosage.dosage() - actualDraw;
            if (leftoverSource <= 0.5f) {
                sourceStack.shrink(1);
            } else {
                sourceStack.set(ModDataComponents.DRUG_DOSAGE, new DrugDosage(leftoverSource));
            }
        });
    }
}
