package com.lunkoashtail.avaliproject.network;

import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.limb.ModAttachments;
import com.lunkoashtail.avaliproject.pack.PackMembership;
import com.lunkoashtail.avaliproject.pack.PackRecord;
import com.lunkoashtail.avaliproject.pack.PackSavedData;
import com.lunkoashtail.avaliproject.pack.PackUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record PackKickPayload(UUID targetUUID) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PackKickPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "pack_kick"));

    public static final StreamCodec<ByteBuf, PackKickPayload> STREAM_CODEC =
            UUIDUtil.STREAM_CODEC.map(PackKickPayload::new, PackKickPayload::targetUUID);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(PackKickPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            ServerLevel level = (ServerLevel) player.level();
            UUID leaderUUID = PackUtil.getOrCreateLeaderUUID(player);
            if (!leaderUUID.equals(player.getUUID())) return;

            PackRecord record = PackSavedData.get(level).get(leaderUUID);
            if (record == null || !record.getMemberUUIDs().remove(payload.targetUUID())) return;
            PackSavedData.get(level).setDirty();

            ServerPlayer kicked = level.getServer().getPlayerList().getPlayer(payload.targetUUID());
            if (kicked != null) {
                kicked.setData(ModAttachments.PACK_MEMBERSHIP, PackMembership.NONE);
                kicked.displayClientMessage(Component.translatable("message.avaliproject.pack.kicked", record.getName()), true);
            }

            PackUtil.sendSync(player);
        });
    }
}
