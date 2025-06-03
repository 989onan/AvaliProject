package com.lunkoashtail.avaliproject.item.backwardscompat;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.neoforge.common.SimpleTier;

public class SwordItem extends  Item{


    public SwordItem(SimpleTier tier, Properties properties) {

        super(properties);

    }

    public static ItemAttributeModifiers createAttributes(SimpleTier tier, float damage, float modifier){
        return new ItemAttribute.
    }
}
