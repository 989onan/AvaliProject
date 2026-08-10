package com.lunkoashtail.avaliproject.network;

import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.item.ModItems;
import com.lunkoashtail.avaliproject.limb.Limb;
import com.lunkoashtail.avaliproject.limb.LimbData;
import com.lunkoashtail.avaliproject.limb.ModAttachments;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client → Server: request to reduce bleed on a specific limb.
 *
 * Sent progressively by DressingMinigameScreen as the player circles the wound. Healing and
 * durability spend are deliberately decoupled — one whole dressing (2 * LimbData.MAX_BLEED
 * durability) is calibrated to last a fixed number of clockwise revolutions, while bleed heals
 * at its own (faster) rate per revolution — so bleedAmount and durabilityAmount are not
 * necessarily equal on a given packet.
 *
 * The whole minigame session is bound to the exact stack in {@code mainHand ? main hand : off
 * hand} at the moment the screen was opened — like a pickaxe breaking, a depleted dressing is
 * NOT swapped for another one in the inventory, even if the player is carrying more. The server
 * only ever spends durabilityAmount out of that one stack; if it can't fully pay (durability hit
 * 0, or the stack is gone), bleedAmount is NOT granted for that packet (no healing without
 * material) and a DressingDepletedPayload is sent back so the screen can auto-close.
 *
 * limbOrdinal:      Limb.values()[limbOrdinal] — the target limb.
 * bleedAmount:       how many bleed points this increment is requesting to heal.
 * durabilityAmount:  how many durability points this increment costs.
 * mainHand:          true to spend from the main hand's stack, false for the off hand's.
 */
public record ReduceBleedPayload(int limbOrdinal, int bleedAmount, int durabilityAmount, boolean mainHand) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ReduceBleedPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "reduce_bleed"));

    public static final StreamCodec<ByteBuf, ReduceBleedPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, ReduceBleedPayload::limbOrdinal,
                    ByteBufCodecs.INT, ReduceBleedPayload::bleedAmount,
                    ByteBufCodecs.INT, ReduceBleedPayload::durabilityAmount,
                    ByteBufCodecs.BOOL, ReduceBleedPayload::mainHand,
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
            if (!(context.player() instanceof ServerPlayer serverPlayer)) return;

            Limb[] limbs = Limb.values();
            if (payload.limbOrdinal() < 0 || payload.limbOrdinal() >= limbs.length) return;
            Limb limb = limbs[payload.limbOrdinal()];

            ItemStack stack = payload.mainHand() ? serverPlayer.getMainHandItem() : serverPlayer.getOffhandItem();
            int durabilitySpent = spendDurability(stack, payload.durabilityAmount());
            boolean fullyPaid = durabilitySpent >= payload.durabilityAmount();

            if (fullyPaid && payload.bleedAmount() > 0) {
                LimbData data = serverPlayer.getData(ModAttachments.LIMB_DATA);
                data.reduceBleed(limb, payload.bleedAmount());
                PacketDistributor.sendToPlayer(serverPlayer, LimbDataSyncPayload.from(data));
            }

            if (!fullyPaid) {
                PacketDistributor.sendToPlayer(serverPlayer, new DressingDepletedPayload());
            }
        });
    }
}
