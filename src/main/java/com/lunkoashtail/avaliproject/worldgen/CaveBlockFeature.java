package com.lunkoashtail.avaliproject.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;

public class CaveBlockFeature extends Feature<SimpleBlockConfiguration> {
    public CaveBlockFeature(Codec<SimpleBlockConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<SimpleBlockConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();

        if (!level.getBlockState(pos).canBeReplaced() || !level.getFluidState(pos).isEmpty()) return false;

        BlockState state = context.config().toPlace().getState(context.random(), pos);
        if (!state.canSurvive(level, pos)) return false;

        if (state.getBlock() instanceof DoublePlantBlock) {
            BlockPos above = pos.above();
            if (!level.getBlockState(above).canBeReplaced() || !level.getFluidState(above).isEmpty()) return false;
            DoublePlantBlock.placeAt(level, state, pos, 2);
        } else {
            level.setBlock(pos, state, 2);
        }
        return true;
    }
}
