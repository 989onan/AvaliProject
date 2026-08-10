package com.lunkoashtail.avaliproject.component;

import com.lunkoashtail.avaliproject.species.Species;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record BloodContents(float amountMl, Species species) {

    public static final float CAPACITY_ML = 750f;

    public static final Codec<BloodContents> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("amount_ml").forGetter(BloodContents::amountMl),
            Species.CODEC.fieldOf("species").forGetter(BloodContents::species)
    ).apply(instance, BloodContents::new));

    public static final StreamCodec<ByteBuf, BloodContents> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, BloodContents::amountMl,
            ByteBufCodecs.STRING_UTF8.map(Species::fromName, Species::name), BloodContents::species,
            BloodContents::new
    );
}
