package com.lunkoashtail.avaliproject.network;

import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.client.TargetDataCache;
import com.lunkoashtail.avaliproject.limb.Limb;
import com.lunkoashtail.avaliproject.limb.LimbData;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TargetLimbDataSyncPayload(
        int targetEntityId, int head, int leftArm, int rightArm, int back, int leftLeg, int rightLeg
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<TargetLimbDataSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "target_limb_data_sync"));

    public static final StreamCodec<ByteBuf, TargetLimbDataSyncPayload> STREAM_CODEC = StreamCodec.of(
            (buf, p) -> {
                buf.writeInt(p.targetEntityId());
                buf.writeInt(p.head());
                buf.writeInt(p.leftArm());
                buf.writeInt(p.rightArm());
                buf.writeInt(p.back());
                buf.writeInt(p.leftLeg());
                buf.writeInt(p.rightLeg());
            },
            buf -> new TargetLimbDataSyncPayload(
                    buf.readInt(), buf.readInt(), buf.readInt(),
                    buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt()
            )
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static TargetLimbDataSyncPayload from(int targetEntityId, LimbData data) {
        return new TargetLimbDataSyncPayload(
                targetEntityId,
                data.getBleed(Limb.HEAD),
                data.getBleed(Limb.LEFT_ARM),
                data.getBleed(Limb.RIGHT_ARM),
                data.getBleed(Limb.BACK),
                data.getBleed(Limb.LEFT_LEG),
                data.getBleed(Limb.RIGHT_LEG)
        );
    }

    public static void handle(TargetLimbDataSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> TargetDataCache.update(payload));
    }
}
