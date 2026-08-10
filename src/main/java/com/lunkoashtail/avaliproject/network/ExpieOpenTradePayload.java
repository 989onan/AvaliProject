package com.lunkoashtail.avaliproject.network;

import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.entity.custom.ExpieEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ExpieOpenTradePayload(int entityId) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ExpieOpenTradePayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "expie_open_trade"));

    public static final StreamCodec<ByteBuf, ExpieOpenTradePayload> STREAM_CODEC =
            ByteBufCodecs.INT.map(ExpieOpenTradePayload::new, ExpieOpenTradePayload::entityId);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ExpieOpenTradePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            Entity entity = player.level().getEntity(payload.entityId());
            if (!(entity instanceof ExpieEntity expie)) return;
            if (expie.distanceToSqr(player) > 64) return;
            expie.touchInteraction();
            expie.setTradingPlayer(player);
            expie.openTradingScreen(player, expie.getDisplayName(), 1);
        });
    }
}
