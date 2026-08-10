package com.lunkoashtail.avaliproject.recipe.custom;

import com.lunkoashtail.avaliproject.component.CanteenContents;
import com.lunkoashtail.avaliproject.component.DrugDosage;
import com.lunkoashtail.avaliproject.component.FluidEntry;
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

public class FentanylCraftRecipe extends CustomRecipe {
    private static final float BIOCHEM_REQUIRED_ML = 25f;
    private static final float MORPHINE_REQUIRED_ML = 50f;
    private static final float FENTANYL_DOSAGE_ML = 10f;

    public FentanylCraftRecipe(CraftingBookCategory category) {
        super(category);
    }

    private static int[] findCanteens(CraftingInput input) {
        int biochemIdx = -1, morphineIdx = -1;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!FluidRecipeHelper.isCanteen(stack)) continue;
            CanteenContents contents = FluidRecipeHelper.contentsOf(stack);
            if (biochemIdx == -1 && contents.get(FluidType.BIOCHEM) >= BIOCHEM_REQUIRED_ML) {
                biochemIdx = i;
            } else if (morphineIdx == -1 && contents.get(FluidType.MORPHINE) >= MORPHINE_REQUIRED_ML) {
                morphineIdx = i;
            }
        }
        return new int[]{biochemIdx, morphineIdx};
    }

    private static CanteenContents combine(CanteenContents a, CanteenContents b) {
        CanteenContents result = a;
        for (FluidEntry entry : b.fluids()) {
            result = result.withAdded(entry.type(), entry.amountMl());
        }
        return result;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (FluidRecipeHelper.countNonCanteenNonEmptySlots(input) != 0) return false;
        if (FluidRecipeHelper.countCanteenSlots(input) != 2) return false;
        int[] idx = findCanteens(input);
        return idx[0] != -1 && idx[1] != -1 && idx[0] != idx[1];
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        int[] idx = findCanteens(input);
        if (idx[0] == -1 || idx[1] == -1) return ItemStack.EMPTY;

        ItemStack fentanyl = new ItemStack(ModItems.FENTANYL.get());
        fentanyl.set(ModDataComponents.DRUG_DOSAGE, new DrugDosage(FENTANYL_DOSAGE_ML));
        return fentanyl;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(input.size(), ItemStack.EMPTY);
        int[] idx = findCanteens(input);
        if (idx[0] == -1 || idx[1] == -1) return remaining;

        CanteenContents leftoverA = FluidRecipeHelper.contentsOf(input.getItem(idx[0])).withRemoved(FluidType.BIOCHEM, BIOCHEM_REQUIRED_ML);
        CanteenContents leftoverB = FluidRecipeHelper.contentsOf(input.getItem(idx[1])).withRemoved(FluidType.MORPHINE, MORPHINE_REQUIRED_ML);
        CanteenContents combined = combine(leftoverA, leftoverB);

        ItemStack combinedCanteen = new ItemStack(ModItems.CANTEEN.get());
        if (!combined.isEmpty()) combinedCanteen.set(ModDataComponents.CANTEEN_CONTENTS, combined);
        ItemStack emptyCanteen = new ItemStack(ModItems.CANTEEN.get());

        remaining.set(idx[0], combinedCanteen);
        remaining.set(idx[1], emptyCanteen);
        return remaining;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.FENTANYL_CRAFT.get();
    }
}
