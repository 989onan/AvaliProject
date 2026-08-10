package com.lunkoashtail.avaliproject.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SwordCondition(int hitCount, boolean shattered) {

    public static final int SHATTER_THRESHOLD = 200;
    public static final SwordCondition FRESH = new SwordCondition(0, false);

    public static final Codec<SwordCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("hit_count").forGetter(SwordCondition::hitCount),
            Codec.BOOL.fieldOf("shattered").forGetter(SwordCondition::shattered)
    ).apply(instance, SwordCondition::new));

    public static final StreamCodec<ByteBuf, SwordCondition> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SwordCondition::hitCount,
            ByteBufCodecs.BOOL, SwordCondition::shattered,
            SwordCondition::new
    );

    public SwordCondition afterHit() {
        int newHitCount = hitCount + 1;
        return new SwordCondition(newHitCount, newHitCount >= SHATTER_THRESHOLD);
    }
}
