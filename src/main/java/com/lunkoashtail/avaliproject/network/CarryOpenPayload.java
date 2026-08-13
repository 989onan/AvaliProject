package com.lunkoashtail.avaliproject.network;

import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.carry.CarryCandidateEntry;
import com.lunkoashtail.avaliproject.carry.CarryUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record CarryOpenPayload() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<CarryOpenPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "carry_open"));

    public static final StreamCodec<ByteBuf, CarryOpenPayload> STREAM_CODEC =
            StreamCodec.unit(new CarryOpenPayload());

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CarryOpenPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            List<CarryCandidateEntry> candidates = CarryUtil.findNearbyCandidates(player);
            PacketDistributor.sendToPlayer(player, new CarryCandidatesSyncPayload(candidates));
        });
    }
}
