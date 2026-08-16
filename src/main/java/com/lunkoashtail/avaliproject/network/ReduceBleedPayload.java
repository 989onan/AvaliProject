package com.lunkoashtail.avaliproject.network;

import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.item.ModItems;
import com.lunkoashtail.avaliproject.limb.Limb;
import com.lunkoashtail.avaliproject.limb.LimbData;
import com.lunkoashtail.avaliproject.limb.ModAttachments;
import com.lunkoashtail.avaliproject.species.Species;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;






















public record ReduceBleedPayload(int limbOrdinal, int bleedAmount, int durabilityAmount, boolean mainHand, int targetEntityId) implements CustomPacketPayload {

    private static final double MAX_TREAT_DISTANCE_SQ = 64;

    public static final CustomPacketPayload.Type<ReduceBleedPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "reduce_bleed"));

    public static final StreamCodec<ByteBuf, ReduceBleedPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, ReduceBleedPayload::limbOrdinal,
                    ByteBufCodecs.INT, ReduceBleedPayload::bleedAmount,
                    ByteBufCodecs.INT, ReduceBleedPayload::durabilityAmount,
                    ByteBufCodecs.BOOL, ReduceBleedPayload::mainHand,
                    ByteBufCodecs.INT, ReduceBleedPayload::targetEntityId,
                    ReduceBleedPayload::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private static boolean isDressing(ItemStack stack) {
        return stack.is(ModItems.DRESSING.get()) || stack.is(ModItems.STERILIZED_DRESSING.get());
    }

    private static int spendDurability(ItemStack stack, int needed) {
        if (needed <= 0 || stack.isEmpty() || !isDressing(stack)) return 0;
        int maxDamage = stack.getMaxDamage();
        int available = maxDamage - stack.getDamageValue();
        if (available <= 0) return 0;

        int spend = Math.min(needed, available);
        stack.setDamageValue(stack.getDamageValue() + spend);
        if (stack.getDamageValue() >= maxDamage) {
            stack.shrink(1);
        }
        return spend;
    }

    public static void handle(ReduceBleedPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer wielder)) return;

            Limb[] limbs = Limb.values();
            if (payload.limbOrdinal() < 0 || payload.limbOrdinal() >= limbs.length) return;
            Limb limb = limbs[payload.limbOrdinal()];

            Entity targetEntity = wielder.level().getEntity(payload.targetEntityId());
            if (!(targetEntity instanceof ServerPlayer targetPlayer)) return;
            boolean self = targetPlayer == wielder;
            if (!self && targetPlayer.distanceToSqr(wielder) > MAX_TREAT_DISTANCE_SQ) return;
            if (!targetPlayer.isAlive()) return;
            if (targetPlayer.getData(ModAttachments.SPECIES) != Species.EXPIE) return;

            ItemStack stack = payload.mainHand() ? wielder.getMainHandItem() : wielder.getOffhandItem();
            int durabilitySpent = spendDurability(stack, payload.durabilityAmount());
            boolean fullyPaid = durabilitySpent >= payload.durabilityAmount();

            if (fullyPaid && payload.bleedAmount() > 0) {
                LimbData data = targetPlayer.getData(ModAttachments.LIMB_DATA);
                data.reduceBleed(limb, payload.bleedAmount());
                PacketDistributor.sendToPlayer(targetPlayer, LimbDataSyncPayload.from(data));
                if (!self) {
                    PacketDistributor.sendToPlayer(wielder, TargetLimbDataSyncPayload.from(targetPlayer.getId(), data));
                }
            }

            if (!fullyPaid) {
                PacketDistributor.sendToPlayer(wielder, new DressingDepletedPayload());
            }
        });
    }
}
