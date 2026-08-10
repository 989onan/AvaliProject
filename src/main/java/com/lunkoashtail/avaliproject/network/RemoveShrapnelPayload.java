package com.lunkoashtail.avaliproject.network;

import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.limb.Limb;
import com.lunkoashtail.avaliproject.limb.LimbConditions;
import com.lunkoashtail.avaliproject.limb.LimbData;
import com.lunkoashtail.avaliproject.limb.ModAttachments;
import com.lunkoashtail.avaliproject.limb.PainData;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RemoveShrapnelPayload(int limbOrdinal, int mistakePenalty) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<RemoveShrapnelPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "remove_shrapnel"));

    public static final StreamCodec<ByteBuf, RemoveShrapnelPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, RemoveShrapnelPayload::limbOrdinal,
                    ByteBufCodecs.INT, RemoveShrapnelPayload::mistakePenalty,
                    RemoveShrapnelPayload::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(RemoveShrapnelPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            Limb[] limbs = Limb.values();
            if (payload.limbOrdinal() < 0 || payload.limbOrdinal() >= limbs.length) return;
            Limb limb = limbs[payload.limbOrdinal()];

            LimbConditions conditions = player.getData(ModAttachments.LIMB_CONDITIONS);
            conditions.setShrapnel(limb, 0);
            PacketDistributor.sendToPlayer(player, LimbConditionsSyncPayload.from(conditions));

            if (payload.mistakePenalty() > 0) {
                LimbData data = player.getData(ModAttachments.LIMB_DATA);
                data.addBleed(limb, payload.mistakePenalty());
                PacketDistributor.sendToPlayer(player, LimbDataSyncPayload.from(data));

                PainData pain = player.getData(ModAttachments.PAIN_DATA);
                pain.add(payload.mistakePenalty() * 0.5f);
                PacketDistributor.sendToPlayer(player, PainSyncPayload.from(pain));
            }
        });
    }
}
