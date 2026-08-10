package com.lunkoashtail.avaliproject.pack;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record PackRankData(int xp) {

    public static final PackRankData INITIAL = new PackRankData(0);

    public static final Codec<PackRankData> CODEC = Codec.INT.xmap(PackRankData::new, PackRankData::xp);

    public static final StreamCodec<ByteBuf, PackRankData> STREAM_CODEC =
            ByteBufCodecs.INT.map(PackRankData::new, PackRankData::xp);

    public PackRank rank() {
        return PackRank.forXp(xp);
    }

    public PackRankData addXp(int amount) {
        return new PackRankData(Math.max(0, xp + amount));
    }
}
