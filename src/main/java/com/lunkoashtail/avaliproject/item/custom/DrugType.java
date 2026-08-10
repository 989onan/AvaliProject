package com.lunkoashtail.avaliproject.item.custom;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public enum DrugType {
    FENTANYL,
    HEROIN;

    public static final float MIN_DOSAGE = 50f;
    public static final float MAX_DOSAGE = 500f;

    public static final Codec<DrugType> CODEC = Codec.STRING.xmap(DrugType::valueOf, DrugType::name);

    public static final StreamCodec<ByteBuf, DrugType> STREAM_CODEC =
            ByteBufCodecs.VAR_INT.map(id -> DrugType.values()[id], DrugType::ordinal);
}
