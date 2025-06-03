package com.lunkoashtail.avaliproject.item.custom;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;


import java.util.List;
import java.util.function.Consumer;

public class Aerogel extends Item {
    public Aerogel(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        if(Screen.hasShiftDown()) {
            tooltipAdder.accept(Component.translatable("tooltip.avaliproject.aerogel.shift_down"));
        } else {
            tooltipAdder.accept(Component.translatable("tooltip.avaliproject.aerogel"));
        }

        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);
    }
}