package com.lunkoashtail.avaliproject.recipe.custom;

import com.lunkoashtail.avaliproject.component.CanteenContents;
import com.lunkoashtail.avaliproject.component.ModDataComponents;
import com.lunkoashtail.avaliproject.item.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;

final class FluidRecipeHelper {
    private FluidRecipeHelper() {}

    static boolean isCanteen(ItemStack stack) {
        return stack.is(ModItems.CANTEEN.get());
    }

    static CanteenContents contentsOf(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.CANTEEN_CONTENTS, CanteenContents.EMPTY);
    }

    static int countItem(CraftingInput input, Item item) {
        int count = 0;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.is(item)) count += stack.getCount();
        }
        return count;
    }

    static int findSingleCanteenSlot(CraftingInput input, java.util.function.Predicate<CanteenContents> predicate) {
        int found = -1;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;
            if (!isCanteen(stack)) continue;
            if (!predicate.test(contentsOf(stack))) continue;
            if (found != -1) return -1;
            found = i;
        }
        return found;
    }

    static int countNonCanteenNonEmptySlots(CraftingInput input) {
        int count = 0;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty() && !isCanteen(stack)) count++;
        }
        return count;
    }

    static int countCanteenSlots(CraftingInput input) {
        int count = 0;
        for (int i = 0; i < input.size(); i++) {
            if (isCanteen(input.getItem(i))) count++;
        }
        return count;
    }
}
