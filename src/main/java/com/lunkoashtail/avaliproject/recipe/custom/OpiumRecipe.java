package com.lunkoashtail.avaliproject.recipe.custom;

import com.lunkoashtail.avaliproject.component.CanteenContents;
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

public class OpiumRecipe extends CustomRecipe {
    private static final float BIOCHEM_REQUIRED_ML = 10f;
    private static final float OPIUM_ADDED_ML = 50f;

    public OpiumRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (FluidRecipeHelper.countNonCanteenNonEmptySlots(input) != 2) return false;
        if (FluidRecipeHelper.countItem(input, ModItems.NUMBERRY.get()) != 2) return false;
        return FluidRecipeHelper.findSingleCanteenSlot(input, c -> c.get(FluidType.BIOCHEM) >= BIOCHEM_REQUIRED_ML) != -1;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        int index = FluidRecipeHelper.findSingleCanteenSlot(input, c -> c.get(FluidType.BIOCHEM) >= BIOCHEM_REQUIRED_ML);
        if (index == -1) return ItemStack.EMPTY;

        ItemStack result = input.getItem(index).copy();
        CanteenContents contents = FluidRecipeHelper.contentsOf(result)
                .withRemoved(FluidType.BIOCHEM, BIOCHEM_REQUIRED_ML)
                .withAdded(FluidType.OPIUM, OPIUM_ADDED_ML);
        result.set(ModDataComponents.CANTEEN_CONTENTS, contents);
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.OPIUM.get();
    }
}
