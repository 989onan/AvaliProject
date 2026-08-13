package com.lunkoashtail.avaliproject.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;









public class WoundDataHelper {

    private static final String ROOT_TAG = "avaliproject_wounds";

    public static int getBleed(Player player, String limb) {
        CompoundTag root = player.getPersistentData();
        if (!root.contains(ROOT_TAG, CompoundTag.TAG_COMPOUND)) return 0;
        return root.getCompound(ROOT_TAG).getInt(limb + "_bleed");
    }

    public static void setBleed(Player player, String limb, int value) {
        CompoundTag root = player.getPersistentData();
        CompoundTag wounds = root.contains(ROOT_TAG, CompoundTag.TAG_COMPOUND)
                ? root.getCompound(ROOT_TAG)
                : new CompoundTag();
        wounds.putInt(limb + "_bleed", Math.max(0, value));
        root.put(ROOT_TAG, wounds);
    }

    public static void reduceBleed(Player player, String limb, int amount) {
        setBleed(player, limb, getBleed(player, limb) - amount);
    }

     
    public static void debugSetLeftArmBleed(Player player, int value) {
        setBleed(player, "left_arm", value);
    }
}