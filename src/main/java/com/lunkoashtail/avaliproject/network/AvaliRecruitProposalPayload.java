package com.lunkoashtail.avaliproject.network;

import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.client.ClientPayloadHandlers;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record AvaliRecruitProposalPayload(int entityId) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AvaliRecruitProposalPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "avali_recruit_proposal"));

    public static final StreamCodec<ByteBuf, AvaliRecruitProposalPayload> STREAM_CODEC =
            ByteBufCodecs.INT.map(AvaliRecruitProposalPayload::new, AvaliRecruitProposalPayload::entityId);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(AvaliRecruitProposalPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ClientPayloadHandlers.handleRecruitProposal(payload));
    }
}
