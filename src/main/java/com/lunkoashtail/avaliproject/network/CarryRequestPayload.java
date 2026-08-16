package com.lunkoashtail.avaliproject.network;

import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.carry.CarryConsentManager;
import com.lunkoashtail.avaliproject.carry.CarryUtil;
import com.lunkoashtail.avaliproject.entity.custom.ExpieEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CarryRequestPayload(int targetEntityId) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<CarryRequestPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "carry_request"));

    public static final StreamCodec<ByteBuf, CarryRequestPayload> STREAM_CODEC =
            ByteBufCodecs.INT.map(CarryRequestPayload::new, CarryRequestPayload::targetEntityId);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CarryRequestPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer requester)) return;
            Entity target = requester.level().getEntity(payload.targetEntityId());
            if (target == null || !CarryUtil.canStartCarry(requester, target)) return;

            if (target instanceof ExpieEntity expie) {
                expie.startRiding(requester, true);
            } else if (target instanceof ServerPlayer targetPlayer) {
                CarryConsentManager.request(requester, targetPlayer);
                requester.displayClientMessage(Component.translatable("message.avaliproject.carry.request_sent"), true);
                PacketDistributor.sendToPlayer(targetPlayer, new CarryConsentRequestPayload(requester.getId()));
            }
        });
    }
}
