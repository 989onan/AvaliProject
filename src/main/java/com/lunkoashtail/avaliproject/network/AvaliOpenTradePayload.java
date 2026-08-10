package com.lunkoashtail.avaliproject.network;

import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.entity.custom.AvaliEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record AvaliOpenTradePayload(int entityId) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AvaliOpenTradePayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "avali_open_trade"));

    public static final StreamCodec<ByteBuf, AvaliOpenTradePayload> STREAM_CODEC =
            ByteBufCodecs.INT.map(AvaliOpenTradePayload::new, AvaliOpenTradePayload::entityId);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(AvaliOpenTradePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            Entity entity = player.level().getEntity(payload.entityId());
            if (!(entity instanceof AvaliEntity avali)) return;
            if (avali.distanceToSqr(player) > 64) return;
            avali.touchInteraction();
            avali.setTradingPlayer(player);
            avali.openTradingScreen(player, avali.getDisplayName(), 1);
        });
    }
}
