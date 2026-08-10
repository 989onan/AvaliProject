package com.lunkoashtail.avaliproject.network;

import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.item.custom.HitscanWeapon;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record HitscanFirePayload(float xRot, float yRot) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<HitscanFirePayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "hitscan_fire"));

    public static final StreamCodec<ByteBuf, HitscanFirePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, HitscanFirePayload::xRot,
            ByteBufCodecs.FLOAT, HitscanFirePayload::yRot,
            HitscanFirePayload::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(HitscanFirePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            if (!(player.level() instanceof ServerLevel level)) return;
            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (!(stack.getItem() instanceof HitscanWeapon weapon)) return;
            if (player.getCooldowns().isOnCooldown(stack.getItem())) return;
            player.setXRot(payload.xRot());
            player.setYRot(payload.yRot());
            weapon.fire(level, player, stack);
        });
    }
}
