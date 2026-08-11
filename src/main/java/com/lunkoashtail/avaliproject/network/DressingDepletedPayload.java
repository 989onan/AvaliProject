package com.lunkoashtail.avaliproject.network;

import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.client.ClientPayloadHandlers;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record DressingDepletedPayload() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<DressingDepletedPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "dressing_depleted"));

    public static final StreamCodec<ByteBuf, DressingDepletedPayload> STREAM_CODEC =
            StreamCodec.unit(new DressingDepletedPayload());

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(DressingDepletedPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ClientPayloadHandlers.handleDressingDepleted(payload));
    }
}
