package com.lunkoashtail.avaliproject.pack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;
import java.util.UUID;

public record PackMembership(Optional<UUID> leaderUUID) {

    public static final PackMembership NONE = new PackMembership(Optional.empty());

    public static final Codec<PackMembership> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.optionalFieldOf("leader_uuid").forGetter(PackMembership::leaderUUID)
    ).apply(instance, PackMembership::new));

    public static final StreamCodec<ByteBuf, PackMembership> STREAM_CODEC =
            ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC).map(PackMembership::new, PackMembership::leaderUUID);

    public boolean isLeaderOf(UUID viewer) {
        return leaderUUID.isPresent() && leaderUUID.get().equals(viewer);
    }
}
