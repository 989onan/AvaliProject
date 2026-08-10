package com.lunkoashtail.avaliproject.pack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;

public record PerPlayerTrust(int trust, long lastInteractionTick, boolean proposed, int trustAtLastProposal) {

    public static final PerPlayerTrust INITIAL = new PerPlayerTrust(0, 0L, false, 0);

    public static final Codec<PerPlayerTrust> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("trust").forGetter(PerPlayerTrust::trust),
            Codec.LONG.fieldOf("last_interaction_tick").forGetter(PerPlayerTrust::lastInteractionTick),
            Codec.BOOL.fieldOf("proposed").forGetter(PerPlayerTrust::proposed),
            Codec.INT.fieldOf("trust_at_last_proposal").forGetter(PerPlayerTrust::trustAtLastProposal)
    ).apply(instance, PerPlayerTrust::new));

    public PerPlayerTrust withTrustDelta(int delta, long tick) {
        return new PerPlayerTrust(Mth.clamp(trust + delta, 0, 100), tick, proposed, trustAtLastProposal);
    }

    public PerPlayerTrust markProposed() {
        return new PerPlayerTrust(trust, lastInteractionTick, true, trust);
    }
}
