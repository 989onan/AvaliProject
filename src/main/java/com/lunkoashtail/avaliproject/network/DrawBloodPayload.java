package com.lunkoashtail.avaliproject.network;

import com.lunkoashtail.avaliproject.component.BloodContents;
import com.lunkoashtail.avaliproject.component.ModDataComponents;
import com.lunkoashtail.avaliproject.item.ModItems;
import com.lunkoashtail.avaliproject.limb.ModAttachments;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import com.lunkoashtail.avaliproject.AvaliProject;

public record DrawBloodPayload(float amountMl) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<DrawBloodPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "draw_blood"));

    public static final StreamCodec<ByteBuf, DrawBloodPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.FLOAT, DrawBloodPayload::amountMl,
                    DrawBloodPayload::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(DrawBloodPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer serverPlayer)) return;

            ItemStack offhand = serverPlayer.getOffhandItem();
            if (!offhand.is(ModItems.EMPTY_BLOOD_BAG.get())) return;

            float amount = Math.max(0f, Math.min(payload.amountMl(), BloodContents.CAPACITY_ML));
            if (amount <= 0.5f) return;

            float damage = amount / BloodContents.CAPACITY_ML * 15f;
            if (damage >= serverPlayer.getHealth()) return;

            serverPlayer.hurt(serverPlayer.damageSources().generic(), damage);

            ItemStack bloodBag = new ItemStack(ModItems.BLOOD_BAG.get());
            bloodBag.set(ModDataComponents.BLOOD_CONTENTS, new BloodContents(amount, serverPlayer.getData(ModAttachments.SPECIES)));
            serverPlayer.setItemInHand(InteractionHand.OFF_HAND, bloodBag);
        });
    }
}
