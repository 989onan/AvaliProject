package com.lunkoashtail.avaliproject.network;

import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.carry.CarryCandidateEntry;
import com.lunkoashtail.avaliproject.client.ClientPayloadHandlers;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record CarryCandidatesSyncPayload(List<CarryCandidateEntry> candidates) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<CarryCandidatesSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AvaliProject.MOD_ID, "carry_candidates_sync"));

    private static final StreamCodec<ByteBuf, CarryCandidateEntry> ENTRY_CODEC = StreamCodec.of(
            (buf, entry) -> {
                ByteBufCodecs.INT.encode(buf, entry.entityId());
                UUIDUtil.STREAM_CODEC.encode(buf, entry.uuid());
                ByteBufCodecs.STRING_UTF8.encode(buf, entry.name());
                ByteBufCodecs.BOOL.encode(buf, entry.isPlayer());
                ByteBufCodecs.DOUBLE.encode(buf, entry.distance());
            },
            buf -> new CarryCandidateEntry(
                    ByteBufCodecs.INT.decode(buf),
                    UUIDUtil.STREAM_CODEC.decode(buf),
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.BOOL.decode(buf),
                    ByteBufCodecs.DOUBLE.decode(buf))
    );

    public static final StreamCodec<ByteBuf, CarryCandidatesSyncPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                ByteBufCodecs.INT.encode(buf, payload.candidates().size());
                for (CarryCandidateEntry entry : payload.candidates()) {
                    ENTRY_CODEC.encode(buf, entry);
                }
            },
            buf -> {
                int count = ByteBufCodecs.INT.decode(buf);
                List<CarryCandidateEntry> candidates = new ArrayList<>(count);
                for (int i = 0; i < count; i++) candidates.add(ENTRY_CODEC.decode(buf));
                return new CarryCandidatesSyncPayload(candidates);
            }
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CarryCandidatesSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ClientPayloadHandlers.handleCarryCandidatesSync(payload));
    }
}
