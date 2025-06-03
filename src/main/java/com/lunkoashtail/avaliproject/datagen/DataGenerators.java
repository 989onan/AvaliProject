package com.lunkoashtail.avaliproject.datagen;

import com.lunkoashtail.avaliproject.AvaliProject;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = AvaliProject.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherDataServer(GatherDataEvent.Server event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        generator.addProvider(true, new LootTableProvider(packOutput, Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(ModBlockLootTableProvider::new, LootContextParamSets.BLOCK)), lookupProvider));
        generator.addProvider(true, new com.lunkoashtail.avaliproject.datagen.ModRecipeProvider(packOutput, lookupProvider));

        BlockTagsProvider blockTagsProvider = new com.lunkoashtail.avaliproject.datagen.ModBlockTagProvider(packOutput, lookupProvider);
        generator.addProvider(true, blockTagsProvider);
        generator.addProvider(true, new com.lunkoashtail.avaliproject.datagen.ModItemTagProvider(packOutput, lookupProvider, blockTagsProvider.contentsGetter()));

        generator.addProvider(false, new com.lunkoashtail.avaliproject.datagen.ModItemModelProvider(packOutput));
        generator.addProvider(false, new com.lunkoashtail.avaliproject.datagen.ModBlockStateProvider(packOutput));
        generator.addProvider(true, new ModWorldGenProvider(packOutput, lookupProvider));
    }

    @SubscribeEvent
    public static void gatherDataClient(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        generator.addProvider(false, new LootTableProvider(packOutput, Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(ModBlockLootTableProvider::new, LootContextParamSets.BLOCK)), lookupProvider));
        generator.addProvider(false, new com.lunkoashtail.avaliproject.datagen.ModRecipeProvider(packOutput, lookupProvider));

        BlockTagsProvider blockTagsProvider = new com.lunkoashtail.avaliproject.datagen.ModBlockTagProvider(packOutput, lookupProvider, existingFileHelper);
        generator.addProvider(false, blockTagsProvider);
        generator.addProvider(false, new com.lunkoashtail.avaliproject.datagen.ModItemTagProvider(packOutput, lookupProvider, blockTagsProvider.contentsGetter(), existingFileHelper));

        generator.addProvider(true, () -> new com.lunkoashtail.avaliproject.datagen.ModItemModelProvider(packOutput));
        generator.addProvider(true, () -> new com.lunkoashtail.avaliproject.datagen.ModBlockStateProvider(packOutput));
    }
}
