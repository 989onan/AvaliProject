package com.lunkoashtail.avaliproject.network;

import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.entity.custom.AvaliEntity;
import com.lunkoashtail.avaliproject.screen.custom.AvaliEquipMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record AvaliOpenEquipPayload(int entityId) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AvaliOpenEquipPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "avali_open_equip"));

    public static final StreamCodec<ByteBuf, AvaliOpenEquipPayload> STREAM_CODEC =
            ByteBufCodecs.INT.map(AvaliOpenEquipPayload::new, AvaliOpenEquipPayload::entityId);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(AvaliOpenEquipPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            Entity entity = player.level().getEntity(payload.entityId());
            if (!(entity instanceof AvaliEntity avali)) return;
            if (!avali.isTame() || !avali.isOwnedBy(player) || avali.distanceToSqr(player) > 64) return;
            avali.touchInteraction();

            player.openMenu(new SimpleMenuProvider(
                    (containerId, inv, p) -> new AvaliEquipMenu(containerId, inv, avali),
                    avali.getDisplayName()
            ), buf -> buf.writeVarInt(avali.getId()));
        });
    }
}
