package com.lunkoashtail.avaliproject.recipe;

import com.lunkoashtail.avaliproject.AvaliProject;
import com.lunkoashtail.avaliproject.recipe.custom.BiochemRecipe;
import com.lunkoashtail.avaliproject.recipe.custom.FentanylCraftRecipe;
import com.lunkoashtail.avaliproject.recipe.custom.MorphineRecipe;
import com.lunkoashtail.avaliproject.recipe.custom.OpiumRecipe;
import com.lunkoashtail.avaliproject.recipe.custom.SalineRecipe;
import com.lunkoashtail.avaliproject.recipe.custom.WaterBottleRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, AvaliProject.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<BiochemRecipe>> BIOCHEM =
            RECIPE_SERIALIZERS.register("biochem", () -> new SimpleCraftingRecipeSerializer<>(BiochemRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<OpiumRecipe>> OPIUM =
            RECIPE_SERIALIZERS.register("opium", () -> new SimpleCraftingRecipeSerializer<>(OpiumRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<MorphineRecipe>> MORPHINE =
            RECIPE_SERIALIZERS.register("morphine", () -> new SimpleCraftingRecipeSerializer<>(MorphineRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<SalineRecipe>> SALINE =
            RECIPE_SERIALIZERS.register("saline", () -> new SimpleCraftingRecipeSerializer<>(SalineRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<FentanylCraftRecipe>> FENTANYL_CRAFT =
            RECIPE_SERIALIZERS.register("fentanyl_craft", () -> new SimpleCraftingRecipeSerializer<>(FentanylCraftRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<WaterBottleRecipe>> WATER_BOTTLE =
            RECIPE_SERIALIZERS.register("water_bottle", () -> new SimpleCraftingRecipeSerializer<>(WaterBottleRecipe::new));

    public static void register(IEventBus eventBus) {
        RECIPE_SERIALIZERS.register(eventBus);
    }
}
