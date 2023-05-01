package slimeknights.tconstruct.world.worldgen.islands;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.worldgen.islands.variants.IslandVariants;

/**
 * Overworld structure containing sky slimes, spawns in the sky
 */
public class SkySlimeIslandStructure extends AbstractIslandStructure {
  public static final Codec<SkySlimeIslandStructure> CODEC = simpleCodec(SkySlimeIslandStructure::new);
  public SkySlimeIslandStructure(Structure.StructureSettings structureSettings) {
    super(structureSettings, random -> random.nextBoolean() ? IslandVariants.SKY_BLUE : IslandVariants.SKY_GREEN);
  }

  @Override
  public StructureType<SkySlimeIslandStructure> type() {
    return TinkerStructures.skySlimeIsland.get();
  }
}
