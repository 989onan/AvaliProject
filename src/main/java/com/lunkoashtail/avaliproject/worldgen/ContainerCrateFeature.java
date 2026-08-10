package com.lunkoashtail.avaliproject.worldgen;

import com.lunkoashtail.avaliproject.block.ModBlocks;
import com.lunkoashtail.avaliproject.block.entity.custom.ContainerCrateBlockEntity;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class ContainerCrateFeature extends Feature<NoneFeatureConfiguration> {
    public ContainerCrateFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();

        if (level.getBlockState(pos.below()).isAir()) return false;
        if (!level.getBlockState(pos).canBeReplaced()) return false;

        level.setBlock(pos, ModBlocks.CONTAINER_CRATE.get().defaultBlockState(), 3);
        if (level.getBlockEntity(pos) instanceof ContainerCrateBlockEntity crate) {
            crate.setLootTable(ModLootTables.EXPIE_CONTAINER_CRATE, context.random().nextLong());
        }
        return true;
    }
}
