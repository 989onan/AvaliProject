package com.lunkoashtail.avaliproject.item.custom;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public interface HitscanWeapon {
    void fire(ServerLevel level, ServerPlayer player, ItemStack stack);
}
