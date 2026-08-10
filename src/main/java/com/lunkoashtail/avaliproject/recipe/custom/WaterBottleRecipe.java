package com.lunkoashtail.avaliproject.recipe.custom;

import com.lunkoashtail.avaliproject.component.CanteenContents;
import com.lunkoashtail.avaliproject.component.FluidAmount;
import com.lunkoashtail.avaliproject.component.FluidType;
import com.lunkoashtail.avaliproject.component.ModDataComponents;
import com.lunkoashtail.avaliproject.item.ModItems;
import com.lunkoashtail.avaliproject.recipe.ModRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class WaterBottleRecipe extends CustomRecipe {

    public WaterBottleRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (FluidRecipeHelper.countNonCanteenNonEmptySlots(input) != 1) return false;
        if (FluidRecipeHelper.countItem(input, ModItems.TEMPORARY_BOTTLE.get()) != 1) return false;
        return FluidRecipeHelper.findSingleCanteenSlot(input, c -> c.get(FluidType.WATER) > 0f) != -1;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        int index = FluidRecipeHelper.findSingleCanteenSlot(input, c -> c.get(FluidType.WATER) > 0f);
        if (index == -1) return ItemStack.EMPTY;

        float waterMl = FluidRecipeHelper.contentsOf(input.getItem(index)).get(FluidType.WATER);
        ItemStack waterBottle = new ItemStack(ModItems.WATER_BOTTLE.get());
        waterBottle.set(ModDataComponents.FLUID_AMOUNT, new FluidAmount(waterMl));
        return waterBottle;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(input.size(), ItemStack.EMPTY);
        int index = FluidRecipeHelper.findSingleCanteenSlot(input, c -> c.get(FluidType.WATER) > 0f);
        if (index == -1) return remaining;

        CanteenContents contents = FluidRecipeHelper.contentsOf(input.getItem(index));
        CanteenContents drained = contents.withRemoved(FluidType.WATER, contents.get(FluidType.WATER));

        ItemStack canteen = input.getItem(index).copy();
        if (drained.isEmpty()) {
            canteen.remove(ModDataComponents.CANTEEN_CONTENTS);
        } else {
            canteen.set(ModDataComponents.CANTEEN_CONTENTS, drained);
        }
        remaining.set(index, canteen);
        return remaining;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.WATER_BOTTLE.get();
    }
}
