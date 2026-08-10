package com.lunkoashtail.avaliproject.network;

import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.screen.custom.AugmentMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record AvaliOpenAugmentPayload() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AvaliOpenAugmentPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "avali_open_augment"));

    public static final StreamCodec<ByteBuf, AvaliOpenAugmentPayload> STREAM_CODEC =
            StreamCodec.unit(new AvaliOpenAugmentPayload());

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(AvaliOpenAugmentPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            player.openMenu(new SimpleMenuProvider(
                    (containerId, inv, p) -> new AugmentMenu(containerId, inv),
                    Component.translatable("screen.avaliproject.augment")
            ));
        });
    }
}
