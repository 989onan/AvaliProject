package com.lunkoashtail.avaliproject.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record FluidAmount(float amountMl) {

    public static final Codec<FluidAmount> CODEC = Codec.FLOAT.xmap(FluidAmount::new, FluidAmount::amountMl);

    public static final StreamCodec<ByteBuf, FluidAmount> STREAM_CODEC =
            ByteBufCodecs.FLOAT.map(FluidAmount::new, FluidAmount::amountMl);
}
