package com.lunkoashtail.avaliproject.network;

import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.diplomacy.DiplomacyData;
import com.lunkoashtail.avaliproject.limb.ModAttachments;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record DiplomacySyncPayload(int renown) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<DiplomacySyncPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "diplomacy_sync"));

    public static final StreamCodec<ByteBuf, DiplomacySyncPayload> STREAM_CODEC =
            ByteBufCodecs.INT.map(DiplomacySyncPayload::new, DiplomacySyncPayload::renown);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(DiplomacySyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player == null) return;
            player.setData(ModAttachments.DIPLOMACY_DATA, new DiplomacyData(payload.renown()));
        });
    }
}
