package com.lunkoashtail.avaliproject.network;

import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.limb.ModAttachments;
import com.lunkoashtail.avaliproject.limb.PainData;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ShrapnelSlipPayload(float pain, float damage) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ShrapnelSlipPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "shrapnel_slip"));

    public static final StreamCodec<ByteBuf, ShrapnelSlipPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.FLOAT, ShrapnelSlipPayload::pain,
                    ByteBufCodecs.FLOAT, ShrapnelSlipPayload::damage,
                    ShrapnelSlipPayload::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ShrapnelSlipPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            PainData pain = player.getData(ModAttachments.PAIN_DATA);
            pain.add(payload.pain());
            PacketDistributor.sendToPlayer(player, PainSyncPayload.from(pain));

            player.hurt(player.damageSources().generic(), payload.damage());
        });
    }
}
