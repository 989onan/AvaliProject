package com.lunkoashtail.avaliproject.network;

import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.limb.ModAttachments;
import com.lunkoashtail.avaliproject.limb.PainData;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PainSyncPayload(float pain) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PainSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "pain_sync"));

    public static final StreamCodec<ByteBuf, PainSyncPayload> STREAM_CODEC = StreamCodec.of(
            (buf, p) -> buf.writeFloat(p.pain()),
            buf -> new PainSyncPayload(buf.readFloat())
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static PainSyncPayload from(PainData data) {
        return new PainSyncPayload(data.get());
    }

    public static void handle(PainSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player == null) return;
            player.getData(ModAttachments.PAIN_DATA).set(payload.pain());
        });
    }
}
