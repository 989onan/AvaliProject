package com.lunkoashtail.avaliproject.pack;

import com.mojang.serialization.Codec;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record AugmentInventoryData(List<ItemStack> items) {
    public static final int SLOT_COUNT = 8;

    public static final AugmentInventoryData EMPTY = new AugmentInventoryData(List.of());

    public static final Codec<AugmentInventoryData> CODEC =
            Codec.list(ItemStack.OPTIONAL_CODEC).xmap(AugmentInventoryData::new, AugmentInventoryData::items);

    public AugmentInventoryData(List<ItemStack> items) {
        List<ItemStack> padded = new ArrayList<>(SLOT_COUNT);
        for (int i = 0; i < SLOT_COUNT; i++) {
            padded.add(i < items.size() ? items.get(i) : ItemStack.EMPTY);
        }
        this.items = padded;
    }
}
