package slimeknights.tconstruct.world;

import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.HugeFungusConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride.BoundingBoxType;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType.StructureTemplateType;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.units.qual.C;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.block.SlimeVineBlock;
import slimeknights.tconstruct.world.block.SlimeVineBlock.VineStage;
import slimeknights.tconstruct.world.worldgen.islands.BloodSlimeIslandStructure;
import slimeknights.tconstruct.world.worldgen.islands.ClayIslandStructure;
import slimeknights.tconstruct.world.worldgen.islands.EarthSlimeIslandStructure;
import slimeknights.tconstruct.world.worldgen.islands.EnderSlimeIslandStructure;
import slimeknights.tconstruct.world.worldgen.islands.SkySlimeIslandStructure;
import slimeknights.tconstruct.world.worldgen.islands.SlimeIslandPiece;
import slimeknights.tconstruct.world.worldgen.trees.SupplierBlockStateProvider;
import slimeknights.tconstruct.world.worldgen.trees.config.SlimeFungusConfig;
import slimeknights.tconstruct.world.worldgen.trees.config.SlimeTreeConfig;
import slimeknights.tconstruct.world.worldgen.trees.feature.SlimeFungusFeature;
import slimeknights.tconstruct.world.worldgen.trees.feature.SlimeTreeFeature;

import java.util.Map;
import java.util.Objects;

/**
 * Contains any logic relevant to structure generation, including trees and islands
 */
@SuppressWarnings("unused")
public final class TinkerStructures extends TinkerModule {
  static final Logger log = Util.getLogger("tinker_structures");

  /*
   * Misc
   */
  public static final RegistryObject<BlockStateProviderType<SupplierBlockStateProvider>> supplierBlockstateProvider = BLOCK_STATE_PROVIDER_TYPES.register("supplier_state_provider", () -> new BlockStateProviderType<>(SupplierBlockStateProvider.CODEC));

  /*
   * Features
   */
  /** Overworld variant of slimy trees */
  public static final RegistryObject<SlimeTreeFeature> slimeTree = FEATURES.register("slime_tree", () -> new SlimeTreeFeature(SlimeTreeConfig.CODEC));
  /** Nether variant of slimy trees */
  public static final RegistryObject<SlimeFungusFeature> slimeFungus = FEATURES.register("slime_fungus", () -> new SlimeFungusFeature(SlimeFungusConfig.CODEC));

  /*
   * Structures
   */
  public static final RegistryObject<StructurePieceType> slimeIslandPiece = STRUCTURE_PIECE.register("slime_island_piece", () -> SlimeIslandPiece::new);

  // earthslime
  public static final RegistryObject<StructureType<EarthSlimeIslandStructure>> earthSlimeIsland = STRUCTURE_TYPES.register("earth_slime_island", () -> () -> EarthSlimeIslandStructure.CODEC);
  public static final ResourceKey<Structure> earthSlimeIslandKey = registerKey("earth_slime_island");


  // skyslime
  public static final RegistryObject<StructureType<SkySlimeIslandStructure>> skySlimeIsland = STRUCTURE_TYPES.register("sky_slime_island", () -> () -> SkySlimeIslandStructure.CODEC);
  public static final ResourceKey<Structure> skySlimeIslandKey = registerKey("sky_slime_island");

  // clay
  public static final RegistryObject<StructureType<ClayIslandStructure>> clayIsland = STRUCTURE_TYPES.register("clay_island", () -> () -> ClayIslandStructure.CODEC);
  public static final ResourceKey<Structure> clayIslandKey = registerKey("clay_island");


  // nether
  public static final RegistryObject<StructureType<BloodSlimeIslandStructure>> bloodIsland = STRUCTURE_TYPES.register("blood_island", () -> () -> BloodSlimeIslandStructure.CODEC);
  public static final ResourceKey<Structure> bloodIslandKey = registerKey("blood_island");


  // end
  public static final RegistryObject<StructureType<EnderSlimeIslandStructure>> endSlimeIsland = STRUCTURE_TYPES.register("end_slime_island", () -> () -> EnderSlimeIslandStructure.CODEC);
  public static final ResourceKey<Structure> endSlimeIslandKey = registerKey("end_slime_island");


  public static ResourceKey<Structure> registerKey(String name) {
    return ResourceKey.create(Registry.STRUCTURE_REGISTRY, TConstruct.getResource(name));
  }
  public static void init() {

  }
}
