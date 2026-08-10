package com.lunkoashtail.avaliproject.recipe.custom;

import com.lunkoashtail.avaliproject.component.FluidType;
import com.lunkoashtail.avaliproject.component.ModDataComponents;
import com.lunkoashtail.avaliproject.item.ModItems;
import com.lunkoashtail.avaliproject.recipe.ModRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class BiochemRecipe extends CustomRecipe {
    private static final float BIOCHEM_ADDED_ML = 10f;

    public BiochemRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return FluidRecipeHelper.countCanteenSlots(input) == 1
                && FluidRecipeHelper.countNonCanteenNonEmptySlots(input) == 1
                && FluidRecipeHelper.countItem(input, ModItems.GLOWPLANT_FRUIT.get()) == 1;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (FluidRecipeHelper.isCanteen(stack)) {
                ItemStack result = stack.copy();
                result.set(ModDataComponents.CANTEEN_CONTENTS,
                        FluidRecipeHelper.contentsOf(result).withAdded(FluidType.BIOCHEM, BIOCHEM_ADDED_ML));
                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.BIOCHEM.get();
    }
}
