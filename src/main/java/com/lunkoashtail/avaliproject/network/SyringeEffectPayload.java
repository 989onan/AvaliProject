package com.lunkoashtail.avaliproject.network;

import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.component.ModDataComponents;
import com.lunkoashtail.avaliproject.component.SyringeContents;
import com.lunkoashtail.avaliproject.item.custom.DrugType;
import com.lunkoashtail.avaliproject.item.custom.SyringeItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Sent from the client when the syringe injection minigame succeeds.
 *
 * The server applies the drug effects (server-side, so they tick down and
 * their HUD icons disappear on time) AND deducts the injected amount from
 * the syringe's remaining dosage — any leftover stays loaded for next time.
 *
 * handOrdinal: 0 = MAIN_HAND, 1 = OFF_HAND — which hand holds the syringe.
 * drugTypeOrdinal: DrugType.values() ordinal.
 * injectedAmount: mL the client's minigame reports as injected. The server
 *   clamps this to what's actually left in the syringe before trusting it.
 */
public record SyringeEffectPayload(int handOrdinal, int drugTypeOrdinal, float injectedAmount)
        implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyringeEffectPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "syringe_effect"));

    public static final StreamCodec<ByteBuf, SyringeEffectPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, SyringeEffectPayload::handOrdinal,
                    ByteBufCodecs.INT, SyringeEffectPayload::drugTypeOrdinal,
                    ByteBufCodecs.FLOAT, SyringeEffectPayload::injectedAmount,
                    SyringeEffectPayload::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyringeEffectPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            InteractionHand hand = payload.handOrdinal() == 0 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
            ItemStack syringeStack = player.getItemInHand(hand);
            if (!(syringeStack.getItem() instanceof SyringeItem)) return;

            SyringeContents contents = syringeStack.get(ModDataComponents.SYRINGE_CONTENTS);
            if (contents == null) return;

            DrugType[] types = DrugType.values();
            if (payload.drugTypeOrdinal() < 0 || payload.drugTypeOrdinal() >= types.length) return;
            if (contents.drugType() != types[payload.drugTypeOrdinal()]) return;

            float amount = Math.max(0f, Math.min(payload.injectedAmount(), contents.dosage()));
            if (amount <= 0f) return;

            applyEffects(player, contents.drugType(), amount);

            float leftover = contents.dosage() - amount;
            if (leftover <= 0.5f) {
                syringeStack.remove(ModDataComponents.SYRINGE_CONTENTS);
            } else {
                syringeStack.set(ModDataComponents.SYRINGE_CONTENTS, contents.withDosage(leftover));
            }
        });
    }

    private static void applyEffects(Player player, DrugType drugType, float amount) {
        int amplifier = amount >= 300f ? 1 : 0;
        float scale = amount / 250f;

        switch (drugType) {
            case FENTANYL -> {
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, (int) (400 * scale), amplifier));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, (int) (900 * scale), amplifier));
                player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, (int) (80 * scale), 0));
            }
            case HEROIN -> {
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, (int) (300 * scale), amplifier));
                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, (int) (450 * scale), amplifier));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, (int) (600 * scale), amplifier));
            }
        }
    }
}
