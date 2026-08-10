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

public record AvaliRecruitProposalDeclinePayload(int entityId) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AvaliRecruitProposalDeclinePayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "avali_recruit_proposal_decline"));

    public static final StreamCodec<ByteBuf, AvaliRecruitProposalDeclinePayload> STREAM_CODEC =
            ByteBufCodecs.INT.map(AvaliRecruitProposalDeclinePayload::new, AvaliRecruitProposalDeclinePayload::entityId);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(AvaliRecruitProposalDeclinePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            Entity entity = player.level().getEntity(payload.entityId());
            if (!(entity instanceof AvaliEntity avali)) return;
            avali.getTrustMemory().put(player.getUUID(), avali.getTrustMemory().get(player.getUUID()).markProposed());
        });
    }
}
