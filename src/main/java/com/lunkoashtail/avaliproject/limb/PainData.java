package com.lunkoashtail.avaliproject.limb;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class PainData {
    public static final float MAX_PAIN = 100f;

    private float pain;

    public PainData() {
        this(0f);
    }

    public PainData(float pain) {
        this.pain = clamp(pain);
    }

    public float get() { return this.pain; }

    public void set(float value) { this.pain = clamp(value); }

    public void add(float amount) { set(this.pain + amount); }

    public void clear() { set(0f); }

    private static float clamp(float value) {
        return Math.max(0f, Math.min(MAX_PAIN, value));
    }

    public static final Codec<PainData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.FLOAT.optionalFieldOf("pain", 0f).forGetter(PainData::get)
        ).apply(instance, PainData::new)
    );
}
