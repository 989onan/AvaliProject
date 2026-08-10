package com.lunkoashtail.avaliproject.network;

import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.limb.Limb;
import com.lunkoashtail.avaliproject.limb.LimbConditions;
import com.lunkoashtail.avaliproject.limb.ModAttachments;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record LimbConditionsSyncPayload(
        int headShrapnel, int leftArmShrapnel, int rightArmShrapnel,
        int backShrapnel, int leftLegShrapnel, int rightLegShrapnel,
        boolean headDislocated, boolean leftArmDislocated, boolean rightArmDislocated,
        boolean backDislocated, boolean leftLegDislocated, boolean rightLegDislocated
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<LimbConditionsSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "limb_conditions_sync"));

    public static final StreamCodec<ByteBuf, LimbConditionsSyncPayload> STREAM_CODEC = StreamCodec.of(
            (buf, p) -> {
                buf.writeInt(p.headShrapnel());
                buf.writeInt(p.leftArmShrapnel());
                buf.writeInt(p.rightArmShrapnel());
                buf.writeInt(p.backShrapnel());
                buf.writeInt(p.leftLegShrapnel());
                buf.writeInt(p.rightLegShrapnel());
                buf.writeBoolean(p.headDislocated());
                buf.writeBoolean(p.leftArmDislocated());
                buf.writeBoolean(p.rightArmDislocated());
                buf.writeBoolean(p.backDislocated());
                buf.writeBoolean(p.leftLegDislocated());
                buf.writeBoolean(p.rightLegDislocated());
            },
            buf -> new LimbConditionsSyncPayload(
                    buf.readInt(), buf.readInt(), buf.readInt(),
                    buf.readInt(), buf.readInt(), buf.readInt(),
                    buf.readBoolean(), buf.readBoolean(), buf.readBoolean(),
                    buf.readBoolean(), buf.readBoolean(), buf.readBoolean()
            )
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static LimbConditionsSyncPayload from(LimbConditions data) {
        return new LimbConditionsSyncPayload(
                data.getShrapnel(Limb.HEAD), data.getShrapnel(Limb.LEFT_ARM), data.getShrapnel(Limb.RIGHT_ARM),
                data.getShrapnel(Limb.BACK), data.getShrapnel(Limb.LEFT_LEG), data.getShrapnel(Limb.RIGHT_LEG),
                data.isDislocated(Limb.HEAD), data.isDislocated(Limb.LEFT_ARM), data.isDislocated(Limb.RIGHT_ARM),
                data.isDislocated(Limb.BACK), data.isDislocated(Limb.LEFT_LEG), data.isDislocated(Limb.RIGHT_LEG)
        );
    }

    public static void handle(LimbConditionsSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player == null) return;
            LimbConditions data = player.getData(ModAttachments.LIMB_CONDITIONS);
            data.setShrapnel(Limb.HEAD,      payload.headShrapnel());
            data.setShrapnel(Limb.LEFT_ARM,  payload.leftArmShrapnel());
            data.setShrapnel(Limb.RIGHT_ARM, payload.rightArmShrapnel());
            data.setShrapnel(Limb.BACK,      payload.backShrapnel());
            data.setShrapnel(Limb.LEFT_LEG,  payload.leftLegShrapnel());
            data.setShrapnel(Limb.RIGHT_LEG, payload.rightLegShrapnel());
            data.setDislocated(Limb.HEAD,      payload.headDislocated());
            data.setDislocated(Limb.LEFT_ARM,  payload.leftArmDislocated());
            data.setDislocated(Limb.RIGHT_ARM, payload.rightArmDislocated());
            data.setDislocated(Limb.BACK,      payload.backDislocated());
            data.setDislocated(Limb.LEFT_LEG,  payload.leftLegDislocated());
            data.setDislocated(Limb.RIGHT_LEG, payload.rightLegDislocated());
        });
    }
}
