package com.lunkoashtail.avaliproject.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record AerogelToolState(int hitCount, boolean batteryStored) {

    public static final AerogelToolState FRESH = new AerogelToolState(0, false);

    public static final Codec<AerogelToolState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("hit_count").forGetter(AerogelToolState::hitCount),
            Codec.BOOL.fieldOf("battery_stored").forGetter(AerogelToolState::batteryStored)
    ).apply(instance, AerogelToolState::new));

    public static final StreamCodec<ByteBuf, AerogelToolState> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, AerogelToolState::hitCount,
            ByteBufCodecs.BOOL, AerogelToolState::batteryStored,
            AerogelToolState::new
    );
}
