package com.lunkoashtail.avaliproject.network;

import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.carry.CarryConsentManager;
import com.lunkoashtail.avaliproject.carry.CarryUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CarryConsentAcceptPayload(int requesterEntityId) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<CarryConsentAcceptPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "carry_consent_accept"));

    public static final StreamCodec<ByteBuf, CarryConsentAcceptPayload> STREAM_CODEC =
            ByteBufCodecs.INT.map(CarryConsentAcceptPayload::new, CarryConsentAcceptPayload::requesterEntityId);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CarryConsentAcceptPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer target)) return;
            ServerPlayer requester = CarryConsentManager.consumeIfValid(target, payload.requesterEntityId());
            if (requester == null) return;
            if (!CarryUtil.canStartCarry(requester, target)) {
                target.displayClientMessage(Component.translatable("message.avaliproject.carry.no_longer_valid"), true);
                return;
            }
            target.startRiding(requester, true);
        });
    }
}
