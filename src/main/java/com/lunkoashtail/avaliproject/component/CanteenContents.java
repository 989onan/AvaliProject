package com.lunkoashtail.avaliproject.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;

public record CanteenContents(List<FluidEntry> fluids) {

    public static final float CAPACITY_ML = 300f;

    public static final Codec<CanteenContents> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FluidEntry.CODEC.listOf().fieldOf("fluids").forGetter(CanteenContents::fluids)
    ).apply(instance, CanteenContents::new));

    public static final StreamCodec<ByteBuf, CanteenContents> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, FluidEntry.STREAM_CODEC), CanteenContents::fluids,
            CanteenContents::new
    );

    public static final CanteenContents EMPTY = new CanteenContents(List.of());

    public float totalMl() {
        float total = 0f;
        for (FluidEntry entry : fluids) total += entry.amountMl();
        return total;
    }

    public float get(FluidType type) {
        for (FluidEntry entry : fluids) {
            if (entry.type() == type) return entry.amountMl();
        }
        return 0f;
    }

    public CanteenContents withAdded(FluidType type, float amountMl) {
        float room = CAPACITY_ML - totalMl();
        float toAdd = Math.max(0f, Math.min(amountMl, room));
        if (toAdd <= 0f) return this;

        List<FluidEntry> next = new ArrayList<>();
        boolean found = false;
        for (FluidEntry entry : fluids) {
            if (entry.type() == type) {
                next.add(new FluidEntry(type, entry.amountMl() + toAdd));
                found = true;
            } else {
                next.add(entry);
            }
        }
        if (!found) next.add(new FluidEntry(type, toAdd));
        return new CanteenContents(next);
    }

    public CanteenContents withRemoved(FluidType type, float amountMl) {
        List<FluidEntry> next = new ArrayList<>();
        for (FluidEntry entry : fluids) {
            if (entry.type() == type) {
                float remaining = entry.amountMl() - amountMl;
                if (remaining > 0.01f) next.add(new FluidEntry(type, remaining));
            } else {
                next.add(entry);
            }
        }
        return new CanteenContents(next);
    }

    public CanteenContents withSet(FluidType type, float targetMl) {
        float otherTotal = totalMl() - get(type);
        float capped = Math.max(0f, Math.min(targetMl, CAPACITY_ML - otherTotal));

        List<FluidEntry> next = new ArrayList<>();
        boolean found = false;
        for (FluidEntry entry : fluids) {
            if (entry.type() == type) {
                if (capped > 0.01f) {
                    next.add(new FluidEntry(type, capped));
                    found = true;
                }
            } else {
                next.add(entry);
            }
        }
        if (!found && capped > 0.01f) next.add(new FluidEntry(type, capped));
        return new CanteenContents(next);
    }

    public boolean isEmpty() {
        return fluids.isEmpty();
    }
}
