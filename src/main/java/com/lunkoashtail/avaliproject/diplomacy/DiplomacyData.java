package com.lunkoashtail.avaliproject.diplomacy;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record DiplomacyData(int renown) {

    public static final DiplomacyData INITIAL = new DiplomacyData(0);

    public static final Codec<DiplomacyData> CODEC = Codec.INT.xmap(DiplomacyData::new, DiplomacyData::renown);

    public static final StreamCodec<ByteBuf, DiplomacyData> STREAM_CODEC =
            ByteBufCodecs.INT.map(DiplomacyData::new, DiplomacyData::renown);

    public DiplomacyRank rank() {
        return DiplomacyRank.forRenown(renown);
    }

    public DiplomacyData addRenown(int amount) {
        return new DiplomacyData(Math.max(0, renown + amount));
    }
}
