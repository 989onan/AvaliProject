package com.lunkoashtail.avaliproject.recipe.custom;

import com.lunkoashtail.avaliproject.component.CanteenContents;
import com.lunkoashtail.avaliproject.component.FluidType;
import com.lunkoashtail.avaliproject.component.ModDataComponents;
import com.lunkoashtail.avaliproject.recipe.ModRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class MorphineRecipe extends CustomRecipe {
    private static final float OPIUM_REQUIRED_ML = 75f;
    private static final float BIOCHEM_REQUIRED_ML = 10f;
    private static final float MORPHINE_ADDED_ML = 50f;

    public MorphineRecipe(CraftingBookCategory category) {
        super(category);
    }

    private static boolean eligible(CanteenContents contents) {
        return contents.get(FluidType.OPIUM) >= OPIUM_REQUIRED_ML && contents.get(FluidType.BIOCHEM) >= BIOCHEM_REQUIRED_ML;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (FluidRecipeHelper.countNonCanteenNonEmptySlots(input) != 0) return false;
        return FluidRecipeHelper.findSingleCanteenSlot(input, MorphineRecipe::eligible) != -1;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        int index = FluidRecipeHelper.findSingleCanteenSlot(input, MorphineRecipe::eligible);
        if (index == -1) return ItemStack.EMPTY;

        ItemStack result = input.getItem(index).copy();
        CanteenContents contents = FluidRecipeHelper.contentsOf(result)
                .withRemoved(FluidType.OPIUM, OPIUM_REQUIRED_ML)
                .withRemoved(FluidType.BIOCHEM, BIOCHEM_REQUIRED_ML)
                .withAdded(FluidType.MORPHINE, MORPHINE_ADDED_ML);
        result.set(ModDataComponents.CANTEEN_CONTENTS, contents);
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.MORPHINE.get();
    }
}
