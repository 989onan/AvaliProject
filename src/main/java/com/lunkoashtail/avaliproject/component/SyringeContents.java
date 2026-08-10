package com.lunkoashtail.avaliproject.component;

import com.lunkoashtail.avaliproject.item.custom.DrugType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SyringeContents(DrugType drugType, float dosage) {

    public static final Codec<SyringeContents> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DrugType.CODEC.fieldOf("drug_type").forGetter(SyringeContents::drugType),
            Codec.FLOAT.fieldOf("dosage").forGetter(SyringeContents::dosage)
    ).apply(instance, SyringeContents::new));

    public static final StreamCodec<ByteBuf, SyringeContents> STREAM_CODEC = StreamCodec.composite(
            DrugType.STREAM_CODEC, SyringeContents::drugType,
            ByteBufCodecs.FLOAT, SyringeContents::dosage,
            SyringeContents::new
    );

    public SyringeContents withDosage(float newDosage) {
        return new SyringeContents(drugType, newDosage);
    }
}
