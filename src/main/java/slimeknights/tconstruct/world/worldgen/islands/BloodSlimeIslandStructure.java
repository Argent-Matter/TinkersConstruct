package slimeknights.tconstruct.world.worldgen.islands;

import com.mojang.serialization.Codec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.worldgen.islands.variants.IIslandVariant;
import slimeknights.tconstruct.world.worldgen.islands.variants.IslandVariants;

import java.util.Random;

public class BloodSlimeIslandStructure extends AbstractIslandStructure {
  public static final Codec<BloodSlimeIslandStructure> CODEC = simpleCodec(BloodSlimeIslandStructure::new);

  public BloodSlimeIslandStructure(Structure.StructureSettings structureSettings) {
    super(structureSettings, new IIslandSettings() {
      @Override
      public IIslandVariant getVariant(RandomSource random) {
        return IslandVariants.BLOOD;
      }

      @Override
      public int getHeight(ChunkPos chunkPos, ChunkGenerator generator, LevelHeightAccessor pLevel, Rotation rotation, RandomSource random, RandomState randomState) {
        return Math.max(generator.getSeaLevel() - 7, 0);
      }
    });
  }

  @Override
  public GenerationStep.Decoration step() {
    return GenerationStep.Decoration.UNDERGROUND_DECORATION;
  }

  @Override
  public StructureType<BloodSlimeIslandStructure> type() {
    return TinkerStructures.bloodIsland.get();
  }
}
