package com.lunkoashtail.avaliproject.network;

import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.pack.PackRecord;
import com.lunkoashtail.avaliproject.pack.PackSavedData;
import com.lunkoashtail.avaliproject.pack.PackUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record PackRenamePayload(String newName) implements CustomPacketPayload {
    private static final int MAX_NAME_LENGTH = 32;

    public static final CustomPacketPayload.Type<PackRenamePayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "pack_rename"));

    public static final StreamCodec<ByteBuf, PackRenamePayload> STREAM_CODEC =
            ByteBufCodecs.STRING_UTF8.map(PackRenamePayload::new, PackRenamePayload::newName);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(PackRenamePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            String trimmed = payload.newName().trim();
            if (trimmed.isEmpty() || trimmed.length() > MAX_NAME_LENGTH) return;

            ServerLevel level = (ServerLevel) player.level();
            UUID leaderUUID = PackUtil.getOrCreateLeaderUUID(player);
            if (!leaderUUID.equals(player.getUUID())) return;

            PackRecord record = PackSavedData.get(level).getOrCreate(leaderUUID, trimmed);
            record.setName(trimmed);
            PackSavedData.get(level).setDirty();

            PackUtil.sendSync(player);
        });
    }
}
