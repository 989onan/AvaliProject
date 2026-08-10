package com.lunkoashtail.avaliproject.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class ExpieMoodData {
    public static final float MIN_MOOD = -100f;
    public static final float MAX_MOOD = 100f;

    private static final float DEFAULT_MOOD = 20f;

    private float mood;

    public ExpieMoodData() {
        this(DEFAULT_MOOD);
    }

    public ExpieMoodData(float mood) {
        this.mood = clamp(mood);
    }

    public float get() { return this.mood; }

    public void set(float value) { this.mood = clamp(value); }

    public void add(float amount) { set(this.mood + amount); }

    public boolean isAtRockBottom() { return this.mood <= MIN_MOOD; }

    private static float clamp(float value) {
        return Math.max(MIN_MOOD, Math.min(MAX_MOOD, value));
    }

    public static final Codec<ExpieMoodData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.FLOAT.optionalFieldOf("mood", DEFAULT_MOOD).forGetter(ExpieMoodData::get)
        ).apply(instance, ExpieMoodData::new)
    );
}
