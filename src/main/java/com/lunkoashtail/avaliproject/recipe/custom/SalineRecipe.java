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

public class SalineRecipe extends CustomRecipe {
    private static final float WATER_REQUIRED_ML = 150f;
    private static final float BIOCHEM_REQUIRED_ML = 10f;
    private static final float SALINE_ADDED_ML = 175f;

    public SalineRecipe(CraftingBookCategory category) {
        super(category);
    }

    private static boolean eligible(CanteenContents contents) {
        return contents.get(FluidType.WATER) >= WATER_REQUIRED_ML && contents.get(FluidType.BIOCHEM) >= BIOCHEM_REQUIRED_ML;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (FluidRecipeHelper.countNonCanteenNonEmptySlots(input) != 1) return false;
        if (FluidRecipeHelper.countItem(input, ModItems.EMPTY_BLOOD_BAG.get()) != 1) return false;
        return FluidRecipeHelper.findSingleCanteenSlot(input, SalineRecipe::eligible) != -1;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        int index = FluidRecipeHelper.findSingleCanteenSlot(input, SalineRecipe::eligible);
        if (index == -1) return ItemStack.EMPTY;

        ItemStack result = input.getItem(index).copy();
        CanteenContents contents = FluidRecipeHelper.contentsOf(result)
                .withRemoved(FluidType.WATER, WATER_REQUIRED_ML)
                .withRemoved(FluidType.BIOCHEM, BIOCHEM_REQUIRED_ML)
                .withAdded(FluidType.SALINE, SALINE_ADDED_ML);
        result.set(ModDataComponents.CANTEEN_CONTENTS, contents);
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.SALINE.get();
    }
}
