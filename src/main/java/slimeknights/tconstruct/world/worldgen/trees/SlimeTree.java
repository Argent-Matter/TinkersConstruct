package slimeknights.tconstruct.world.worldgen.trees;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.TinkerStructuresData;

import javax.annotation.Nullable;
import java.util.Random;

public class SlimeTree extends AbstractTreeGrower {

  private final SlimeType foliageType;

  public SlimeTree(SlimeType foliageType) {
    this.foliageType = foliageType;
  }

  @Deprecated
  @Nullable
  @Override
  protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource randomIn, boolean largeHive) {
    return switch (this.foliageType) {
      case EARTH -> TinkerStructuresData.earthSlimeTree;
      case SKY -> TinkerStructuresData.skySlimeTree;
      case ENDER -> TinkerStructuresData.enderSlimeTree;
      case BLOOD -> TinkerStructuresData.bloodSlimeFungus;
      case ICHOR -> TinkerStructuresData.ichorSlimeFungus;
    };  }

  /**
   * Get a {@link ConfiguredFeature} of tree
   */
  private ConfiguredFeature<?, ?> getSlimeTreeFeature() {
    return switch (this.foliageType) {
      case EARTH -> TinkerStructuresData.earthSlimeTree.value();
      case SKY -> TinkerStructuresData.skySlimeTree.value();
      case ENDER -> TinkerStructuresData.enderSlimeTree.value();
      case BLOOD -> TinkerStructuresData.bloodSlimeFungus.value();
      case ICHOR -> TinkerStructuresData.ichorSlimeFungus.value();
    };

  }

  @Override
  public boolean growTree(ServerLevel world, ChunkGenerator chunkGenerator, BlockPos pos, BlockState state, RandomSource rand) {
    ConfiguredFeature<?, ?> configuredFeature = this.getSlimeTreeFeature();
    world.setBlock(pos, Blocks.AIR.defaultBlockState(), 4);
    if (configuredFeature.place(world, chunkGenerator, rand, pos)) {
      return true;
    }
    else {
      world.setBlock(pos, state, 4);
      return false;
    }
  }
}
