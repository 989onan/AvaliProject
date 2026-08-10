package com.lunkoashtail.avaliproject.network;

import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.limb.ModAttachments;
import com.lunkoashtail.avaliproject.pack.PackMembership;
import com.lunkoashtail.avaliproject.pack.PackRecord;
import com.lunkoashtail.avaliproject.pack.PackSavedData;
import com.lunkoashtail.avaliproject.pack.PackUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;
import java.util.UUID;

public record PackInvitePayload(String targetPlayerName) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PackInvitePayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "pack_invite"));

    public static final StreamCodec<ByteBuf, PackInvitePayload> STREAM_CODEC =
            ByteBufCodecs.STRING_UTF8.map(PackInvitePayload::new, PackInvitePayload::targetPlayerName);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(PackInvitePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            ServerLevel level = (ServerLevel) player.level();
            UUID leaderUUID = PackUtil.getOrCreateLeaderUUID(player);
            if (!leaderUUID.equals(player.getUUID())) {
                player.displayClientMessage(Component.translatable("message.avaliproject.pack.not_leader"), true);
                return;
            }

            ServerPlayer target = level.getServer().getPlayerList().getPlayerByName(payload.targetPlayerName());
            if (target == null) {
                player.displayClientMessage(Component.translatable("message.avaliproject.pack.player_not_found"), true);
                return;
            }
            if (target.getUUID().equals(player.getUUID())) return;

            PackRecord record = PackSavedData.get(level).getOrCreate(leaderUUID, player.getName().getString() + "'s Pack");
            if (record.getMemberUUIDs().contains(target.getUUID())) return;

            if (PackUtil.totalPackSize(level, leaderUUID, record) >= PackRecord.MAX_TOTAL_MEMBERS) {
                player.displayClientMessage(Component.translatable("message.avaliproject.pack.full"), true);
                return;
            }

            PackMembership targetMembership = target.getData(ModAttachments.PACK_MEMBERSHIP);
            if (targetMembership.leaderUUID().isPresent()) {
                player.displayClientMessage(Component.translatable("message.avaliproject.pack.already_in_pack"), true);
                return;
            }

            record.getMemberUUIDs().add(target.getUUID());
            PackSavedData.get(level).setDirty();
            target.setData(ModAttachments.PACK_MEMBERSHIP, new PackMembership(Optional.of(leaderUUID)));

            player.displayClientMessage(Component.translatable("message.avaliproject.pack.invited", target.getName().getString()), true);
            target.displayClientMessage(Component.translatable("message.avaliproject.pack.joined", record.getName()), true);

            PackUtil.sendSync(player);
        });
    }
}
