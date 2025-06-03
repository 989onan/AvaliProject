package com.lunkoashtail.avaliproject.item;

import com.lunkoashtail.avaliproject.util.ModTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;

import static com.lunkoashtail.avaliproject.AvaliProject.MOD_ID;

public class ModToolTiers {
    public static final ToolMaterial AEROGEL = new ToolMaterial(ModTags.Blocks.INCORRECT_FOR_AEROGEL_TOOL,
            1996, 7f, 3f, 20, ModTags.Items.AEROGEL);
    public static final ToolMaterial HARDLIGHT = new ToolMaterial(ModTags.Blocks.INCORRECT_FOR_HARDLIGHT_TOOL,
            1996, 9f, 4f, 20, ModTags.Items.HARDLIGHT);// -> Ingredient.of(ModItems.PROTOSTEEL_INGOT));
    public static final ToolMaterial CERAMIC = new ToolMaterial(ModTags.Blocks.INCORRECT_FOR_CERAMIC_TOOL,
            1996, 8f, 5f, 20, ModTags.Items.CERAMIC);// () -> Ingredient.of(ModItems.VILOUS_CERAMIC_INGOT));
}