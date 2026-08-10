package com.lunkoashtail.avaliproject.network;

import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.limb.Limb;
import com.lunkoashtail.avaliproject.limb.LimbConditions;
import com.lunkoashtail.avaliproject.limb.LimbData;
import com.lunkoashtail.avaliproject.limb.ModAttachments;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ResetDislocationPayload(int limbOrdinal, int painPenalty) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ResetDislocationPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "reset_dislocation"));

    public static final StreamCodec<ByteBuf, ResetDislocationPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, ResetDislocationPayload::limbOrdinal,
                    ByteBufCodecs.INT, ResetDislocationPayload::painPenalty,
                    ResetDislocationPayload::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ResetDislocationPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            Limb[] limbs = Limb.values();
            if (payload.limbOrdinal() < 0 || payload.limbOrdinal() >= limbs.length) return;
            Limb limb = limbs[payload.limbOrdinal()];

            LimbConditions conditions = player.getData(ModAttachments.LIMB_CONDITIONS);
            conditions.setDislocated(limb, false);
            PacketDistributor.sendToPlayer(player, LimbConditionsSyncPayload.from(conditions));

            if (payload.painPenalty() > 0) {
                LimbData data = player.getData(ModAttachments.LIMB_DATA);
                data.addBleed(limb, payload.painPenalty());
                PacketDistributor.sendToPlayer(player, LimbDataSyncPayload.from(data));
            }
        });
    }
}
