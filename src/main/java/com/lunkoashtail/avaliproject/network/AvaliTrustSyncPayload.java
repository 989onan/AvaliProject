package com.lunkoashtail.avaliproject.network;

import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.client.ClientPayloadHandlers;
import com.lunkoashtail.avaliproject.diplomacy.DiplomacyData;
import com.lunkoashtail.avaliproject.entity.custom.AvaliEntity;
import com.lunkoashtail.avaliproject.limb.ModAttachments;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record AvaliTrustSyncPayload(int entityId, int trust, int recruitCost, boolean tamed, boolean ownedByViewer) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AvaliTrustSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "avali_trust_sync"));

    public static final StreamCodec<ByteBuf, AvaliTrustSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, AvaliTrustSyncPayload::entityId,
            ByteBufCodecs.INT, AvaliTrustSyncPayload::trust,
            ByteBufCodecs.INT, AvaliTrustSyncPayload::recruitCost,
            ByteBufCodecs.BOOL, AvaliTrustSyncPayload::tamed,
            ByteBufCodecs.BOOL, AvaliTrustSyncPayload::ownedByViewer,
            AvaliTrustSyncPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void sendTo(ServerPlayer player, AvaliEntity avali) {
        DiplomacyData diplomacy = player.getData(ModAttachments.DIPLOMACY_DATA);
        int trust = avali.getTrustMemory().get(player.getUUID()).trust();
        int cost = avali.getScaledRecruitCost(diplomacy);
        PacketDistributor.sendToPlayer(player, new AvaliTrustSyncPayload(
                avali.getId(), trust, cost, avali.isTame(), avali.isTame() && avali.isOwnedBy(player)));
    }

    public static void handle(AvaliTrustSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ClientPayloadHandlers.handleTrustSync(payload));
    }
}
