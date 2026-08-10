package com.lunkoashtail.avaliproject.item.custom;

import com.lunkoashtail.avaliproject.component.DrugDosage;
import com.lunkoashtail.avaliproject.component.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class FentanylItem extends Item {

    public FentanylItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        stack.set(ModDataComponents.DRUG_DOSAGE, new DrugDosage((DrugType.MIN_DOSAGE + DrugType.MAX_DOSAGE) / 2f));
        return stack;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (!level.isClientSide() && !stack.has(ModDataComponents.DRUG_DOSAGE)) {
            float dosage = DrugType.MIN_DOSAGE + level.getRandom().nextFloat() * (DrugType.MAX_DOSAGE - DrugType.MIN_DOSAGE);
            stack.set(ModDataComponents.DRUG_DOSAGE, new DrugDosage(dosage));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        DrugDosage dosage = stack.get(ModDataComponents.DRUG_DOSAGE);
        if (dosage != null) {
            tooltipComponents.add(Component.literal((int) dosage.dosage() + " mL").withStyle(ChatFormatting.AQUA));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
