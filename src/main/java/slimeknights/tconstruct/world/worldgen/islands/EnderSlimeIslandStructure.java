package slimeknights.tconstruct.world.worldgen.islands;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.worldgen.islands.variants.IslandVariants;

public class EnderSlimeIslandStructure extends AbstractIslandStructure {
  public static final Codec<EnderSlimeIslandStructure> CODEC = simpleCodec(EnderSlimeIslandStructure::new);
  public EnderSlimeIslandStructure(Structure.StructureSettings structureSettings) {
    super(structureSettings, rand -> IslandVariants.ENDER);
  }

  @Override
  public StructureType<EnderSlimeIslandStructure> type() {
    return TinkerStructures.endSlimeIsland.get();
  }
}
