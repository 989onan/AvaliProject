package com.lunkoashtail.avaliproject.network;

import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.carry.CarryConsentManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CarryConsentDeclinePayload(int requesterEntityId) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<CarryConsentDeclinePayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "carry_consent_decline"));

    public static final StreamCodec<ByteBuf, CarryConsentDeclinePayload> STREAM_CODEC =
            ByteBufCodecs.INT.map(CarryConsentDeclinePayload::new, CarryConsentDeclinePayload::requesterEntityId);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CarryConsentDeclinePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer target)) return;
            CarryConsentManager.clear(target);

            Entity requesterEntity = target.level().getEntity(payload.requesterEntityId());
            if (requesterEntity instanceof ServerPlayer requester) {
                requester.displayClientMessage(Component.translatable("message.avaliproject.carry.declined"), true);
            }
        });
    }
}
