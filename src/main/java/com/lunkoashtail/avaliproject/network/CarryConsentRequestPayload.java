package com.lunkoashtail.avaliproject.network;

import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.client.ClientPayloadHandlers;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CarryConsentRequestPayload(int requesterEntityId) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<CarryConsentRequestPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "carry_consent_request"));

    public static final StreamCodec<ByteBuf, CarryConsentRequestPayload> STREAM_CODEC =
            ByteBufCodecs.INT.map(CarryConsentRequestPayload::new, CarryConsentRequestPayload::requesterEntityId);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CarryConsentRequestPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ClientPayloadHandlers.handleCarryConsentRequest(payload));
    }
}
