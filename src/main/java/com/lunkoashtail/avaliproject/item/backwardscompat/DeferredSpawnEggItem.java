package com.lunkoashtail.avaliproject.item.backwardscompat;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

import java.util.function.Supplier;

public class DeferredSpawnEggItem<T extends Mob> extends SpawnEggItem{
    public DeferredSpawnEggItem(Supplier<EntityType<T>> entityType, int color1, int color2, Item.Properties properties){
        super(entityType.get(), properties);
    }
}
