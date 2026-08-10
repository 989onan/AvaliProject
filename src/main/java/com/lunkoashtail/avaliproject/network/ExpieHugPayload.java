package com.lunkoashtail.avaliproject.network;

import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.entity.custom.ExpieEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record ExpieHugPayload(int entityId) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ExpieHugPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "expie_hug"));

    public static final StreamCodec<ByteBuf, ExpieHugPayload> STREAM_CODEC =
            ByteBufCodecs.INT.map(ExpieHugPayload::new, ExpieHugPayload::entityId);

    private static final List<String> HUG_LINES = List.of(
            "wraps their arms around you tightly.",
            "goes still for a moment, then hugs back.",
            "makes a soft, pleased chirring sound.",
            "squeezes back, clearly happy for the company."
    );

    private static final int CLINGY_CHANCE_PERCENT = 10;

    private static final List<String> CLINGY_LINES = List.of(
            "clings to you and refuses to let go.",
            "buries their face into you, trembling, and won't separate.",
            "grips onto you tightly - they don't want to be alone right now.",
            "latches on with a quiet, distressed whimper and won't budge."
    );

    private static final long HUG_COOLDOWN_TICKS = 10L * 60L * 20L;

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ExpieHugPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            Entity entity = player.level().getEntity(payload.entityId());
            if (!(entity instanceof ExpieEntity expie)) return;
            if (expie.distanceToSqr(player) > 64) return;

            long now = expie.level().getGameTime();
            if (expie.getLastHugTick() != 0 && now - expie.getLastHugTick() < HUG_COOLDOWN_TICKS) {
                player.displayClientMessage(Component.translatable("message.avaliproject.expie.hug_cooldown"), true);
                return;
            }
            expie.setLastHugTick(now);

            expie.touchInteraction();
            expie.playHugFeedback();
            if (player.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.HEART, expie.getX(), expie.getY() + expie.getBbHeight() + 0.2, expie.getZ(),
                        6, 0.3, 0.2, 0.3, 0.02);
            }

            String line;
            if (expie.getClingyTarget() == null && expie.getRandom().nextInt(100) < CLINGY_CHANCE_PERCENT) {
                expie.setClingyTarget(player.getUUID());
                line = CLINGY_LINES.get(expie.getRandom().nextInt(CLINGY_LINES.size()));
            } else {
                line = HUG_LINES.get(expie.getRandom().nextInt(HUG_LINES.size()));
            }
            player.displayClientMessage(Component.literal(expie.getDisplayName().getString() + " " + line), false);
        });
    }
}
