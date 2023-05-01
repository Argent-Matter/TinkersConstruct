package slimeknights.tconstruct.world.worldgen.islands;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.worldgen.islands.variants.IslandVariants;

/** Rare island that spawns a random tree and a lake of clay */
public class ClayIslandStructure extends AbstractIslandStructure {
  public static final Codec<ClayIslandStructure> CODEC = simpleCodec(ClayIslandStructure::new);
  public ClayIslandStructure(Structure.StructureSettings structureSettings) {
    super(structureSettings, random -> IslandVariants.SKY_CLAY);
  }

  @Override
  public StructureType<ClayIslandStructure> type() {
    return TinkerStructures.clayIsland.get();
  }
}
