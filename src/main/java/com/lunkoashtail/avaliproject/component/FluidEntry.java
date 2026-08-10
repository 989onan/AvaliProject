package com.lunkoashtail.avaliproject.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record FluidEntry(FluidType type, float amountMl) {

    public static final Codec<FluidEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FluidType.CODEC.fieldOf("type").forGetter(FluidEntry::type),
            Codec.FLOAT.fieldOf("amount_ml").forGetter(FluidEntry::amountMl)
    ).apply(instance, FluidEntry::new));

    public static final StreamCodec<ByteBuf, FluidEntry> STREAM_CODEC = StreamCodec.composite(
            FluidType.STREAM_CODEC, FluidEntry::type,
            ByteBufCodecs.FLOAT, FluidEntry::amountMl,
            FluidEntry::new
    );
}
