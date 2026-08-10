package com.lunkoashtail.avaliproject.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record DrugDosage(float dosage) {

    public static final Codec<DrugDosage> CODEC = Codec.FLOAT.xmap(DrugDosage::new, DrugDosage::dosage);

    public static final StreamCodec<ByteBuf, DrugDosage> STREAM_CODEC =
            ByteBufCodecs.FLOAT.map(DrugDosage::new, DrugDosage::dosage);
}
