package com.lunkoashtail.avaliproject.worldgen;

import com.lunkoashtail.avaliproject.AvaliProject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(BuiltInRegistries.FEATURE, AvaliProject.MOD_ID);

    public static final DeferredHolder<Feature<?>, ContainerCrateFeature> CONTAINER_CRATE_FEATURE =
            FEATURES.register("container_crate_feature", () -> new ContainerCrateFeature(NoneFeatureConfiguration.CODEC));

    public static final DeferredHolder<Feature<?>, CaveBlockFeature> CAVE_BLOCK_FEATURE =
            FEATURES.register("cave_block_feature", () -> new CaveBlockFeature(SimpleBlockConfiguration.CODEC));

    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }
}
