package slimeknights.tconstruct.world.worldgen.islands;

import com.mojang.serialization.Codec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.worldgen.islands.variants.IIslandVariant;
import slimeknights.tconstruct.world.worldgen.islands.variants.IslandVariants;

import java.util.Random;

public class EarthSlimeIslandStructure extends AbstractIslandStructure {
  public static final Codec<EarthSlimeIslandStructure> CODEC = simpleCodec(EarthSlimeIslandStructure::new);
  public EarthSlimeIslandStructure(Structure.StructureSettings structureSettings) {
    super(structureSettings, new IIslandSettings() {
      @Override
      public IIslandVariant getVariant(RandomSource random) {
        return random.nextBoolean() ? IslandVariants.EARTH_BLUE : IslandVariants.EARTH_GREEN;
      }

      @Override
      public int getHeight(ChunkPos chunkPos, ChunkGenerator generator, LevelHeightAccessor pLevel, Rotation rotation, RandomSource random, RandomState randomState) {
        return Math.max(generator.getSeaLevel() - 7, 0);
      }
    });
  }

  @Override
  public StructureType<EarthSlimeIslandStructure> type() {
    return TinkerStructures.earthSlimeIsland.get();
  }
}
