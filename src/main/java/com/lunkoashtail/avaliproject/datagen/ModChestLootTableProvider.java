package com.lunkoashtail.avaliproject.datagen;

import com.lunkoashtail.avaliproject.worldgen.ModLootTables;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;

public class ModChestLootTableProvider implements LootTableSubProvider {

    protected ModChestLootTableProvider(HolderLookup.Provider registries) {
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> writer) {
        writer.accept(ModLootTables.EXPIE_CONTAINER_CRATE, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(UniformGenerator.between(1, 3))
                        .when(LootItemRandomChanceCondition.randomChance(0.16f))
                        .add(TagEntry.expandTag(ModItemTagProvider.EXPIE_MEDICAL_TAG)))
                .withPool(LootPool.lootPool()
                        .setRolls(UniformGenerator.between(1, 3))
                        .when(LootItemRandomChanceCondition.randomChance(0.16f))
                        .add(TagEntry.expandTag(ModItemTagProvider.EXPIE_TOOLS_TAG)))
                .withPool(LootPool.lootPool()
                        .setRolls(UniformGenerator.between(1, 3))
                        .when(LootItemRandomChanceCondition.randomChance(0.33f))
                        .add(TagEntry.expandTag(ModItemTagProvider.EXPIE_UTILITY_TAG))));
    }
}
