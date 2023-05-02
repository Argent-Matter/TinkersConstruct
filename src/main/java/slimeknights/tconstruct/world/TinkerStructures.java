package slimeknights.tconstruct.world;

import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
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
import net.minecraft.world.level.levelgen.feature.HugeFungusConfiguration;
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

  /** Greenheart tree variant */
  public static final RegistryObject<ConfiguredFeature<SlimeTreeConfig,SlimeTreeFeature>> earthSlimeTree = CONFIGURED_FEATURES.registerStatic(
    "earth_slime_tree", slimeTree,
    new SlimeTreeConfig.Builder()
      .planted()
      .trunk(() -> TinkerWorld.greenheart.getLog().defaultBlockState())
      .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.EARTH).defaultBlockState())
      .baseHeight(4).randomHeight(3)
      .build());
  /** Greenheart tree variant on islands */
  public static final RegistryObject<ConfiguredFeature<SlimeTreeConfig,SlimeTreeFeature>> earthSlimeIslandTree = CONFIGURED_FEATURES.registerStatic(
    "earth_slime_island_tree", slimeTree,
    new SlimeTreeConfig.Builder()
      .trunk(() -> TinkerWorld.greenheart.getLog().defaultBlockState())
      .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.EARTH).defaultBlockState())
      .baseHeight(4).randomHeight(3)
      .build());

  /** Skyroot tree variant */
  public static final RegistryObject<ConfiguredFeature<SlimeTreeConfig,SlimeTreeFeature>> skySlimeTree = CONFIGURED_FEATURES.registerStatic(
    "sky_slime_tree", slimeTree,
    new SlimeTreeConfig.Builder()
      .planted().canDoubleHeight()
      .trunk(() -> TinkerWorld.skyroot.getLog().defaultBlockState())
      .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.SKY).defaultBlockState())
      .build());
  /** Skyroot tree variant on islands */
  public static final RegistryObject<ConfiguredFeature<SlimeTreeConfig,SlimeTreeFeature>> skySlimeIslandTree = CONFIGURED_FEATURES.registerStatic(
    "sky_slime_island_tree", slimeTree,
    new SlimeTreeConfig.Builder()
      .canDoubleHeight()
      .trunk(() -> TinkerWorld.skyroot.getLog().defaultBlockState())
      .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.SKY).defaultBlockState())
      .vines(() -> TinkerWorld.skySlimeVine.get().defaultBlockState().setValue(SlimeVineBlock.STAGE, VineStage.MIDDLE))
      .build());

  /** Enderslime island tree variant */
  public static final RegistryObject<ConfiguredFeature<SlimeTreeConfig,SlimeTreeFeature>> enderSlimeTree = CONFIGURED_FEATURES.registerStatic(
    "ender_slime_tree", slimeTree,
    new SlimeTreeConfig.Builder()
      .planted()
      .trunk(() -> TinkerWorld.greenheart.getLog().defaultBlockState()) // TODO: temporary until we have proper green trees and ender shrooms
      .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.ENDER).defaultBlockState())
      .build());
  /** Enderslime island tree variant on islands */
  public static final RegistryObject<ConfiguredFeature<SlimeTreeConfig,SlimeTreeFeature>> enderSlimeIslandTree = CONFIGURED_FEATURES.registerStatic(
    "ender_slime_island_tree", slimeTree,
    new SlimeTreeConfig.Builder()
      .trunk(() -> TinkerWorld.greenheart.getLog().defaultBlockState()) // TODO: temporary until we have proper green trees and ender shrooms
      .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.ENDER).defaultBlockState())
      .vines(() -> TinkerWorld.enderSlimeVine.get().defaultBlockState().setValue(SlimeVineBlock.STAGE, VineStage.MIDDLE))
      .build());

  /** Bloodshroom tree variant */
  public static final RegistryObject<ConfiguredFeature<HugeFungusConfiguration,SlimeFungusFeature>> bloodSlimeFungus = CONFIGURED_FEATURES.registerSupplier(
    "blood_slime_fungus", slimeFungus,
    () -> new SlimeFungusConfig(
      TinkerTags.Blocks.SLIMY_SOIL,
      TinkerWorld.bloodshroom.getLog().defaultBlockState(),
      TinkerWorld.slimeLeaves.get(SlimeType.BLOOD).defaultBlockState(),
      TinkerWorld.congealedSlime.get(SlimeType.ICHOR).defaultBlockState(),
      true));
  /** Bloodshroom island tree variant */
  public static final RegistryObject<ConfiguredFeature<HugeFungusConfiguration,SlimeFungusFeature>> bloodSlimeIslandFungus = CONFIGURED_FEATURES.registerSupplier(
    "blood_slime_island_fungus", slimeFungus,
    () -> new SlimeFungusConfig(
      TinkerTags.Blocks.SLIMY_NYLIUM,
      TinkerWorld.bloodshroom.getLog().defaultBlockState(),
      TinkerWorld.slimeLeaves.get(SlimeType.BLOOD).defaultBlockState(),
      TinkerWorld.congealedSlime.get(SlimeType.ICHOR).defaultBlockState(),
      false));
  /* Deprecated ichor tree */
  public static final RegistryObject<ConfiguredFeature<HugeFungusConfiguration,SlimeFungusFeature>> ichorSlimeFungus = CONFIGURED_FEATURES.registerSupplier(
    "ichor_slime_fungus", slimeFungus,
    () -> new SlimeFungusConfig(
      TinkerTags.Blocks.SLIMY_SOIL,
      TinkerWorld.bloodshroom.getLog().defaultBlockState(),
      TinkerWorld.slimeLeaves.get(SlimeType.ICHOR).defaultBlockState(),
      TinkerWorld.congealedSlime.get(SlimeType.ICHOR).defaultBlockState(),
      false));

  /*
   * Structures
   */
  public static final RegistryObject<StructurePieceType> slimeIslandPiece = STRUCTURE_PIECE.register("slime_island_piece", () -> SlimeIslandPiece::new);

  // earthslime
  public static final RegistryObject<StructureType<EarthSlimeIslandStructure>> earthSlimeIsland = STRUCTURE_TYPES.register("earth_slime_island", () -> () -> EarthSlimeIslandStructure.CODEC);
  public static final ResourceKey<Structure> earthSlimeIslandKey = registerKey("earth_slime_island");
  public static final Holder<Structure> earthSlimeIslandStructure = BuiltinRegistries.register(BuiltinRegistries.STRUCTURES, earthSlimeIslandKey, new EarthSlimeIslandStructure(
    new Structure.StructureSettings(
      biomes(TinkerTags.Biomes.EARTHSLIME_ISLANDS), monsterOverride(TinkerWorld.earthSlimeEntity.get(), 4, 4),
      GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE))
  );

  // skyslime
  public static final RegistryObject<StructureType<SkySlimeIslandStructure>> skySlimeIsland = STRUCTURE_TYPES.register("sky_slime_island", () -> () -> SkySlimeIslandStructure.CODEC);
  public static final ResourceKey<Structure> skySlimeIslandKey = registerKey("sky_slime_island");
  public static final Holder<Structure> skySlimeIslandStructure = BuiltinRegistries.register(BuiltinRegistries.STRUCTURES, skySlimeIslandKey, new SkySlimeIslandStructure(
    new Structure.StructureSettings(
      biomes(TinkerTags.Biomes.SKYSLIME_ISLANDS), monsterOverride(TinkerWorld.skySlimeEntity.get(), 3, 4),
      GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE
    )
  ));

  // clay
  public static final RegistryObject<StructureType<ClayIslandStructure>> clayIsland = STRUCTURE_TYPES.register("clay_island", () -> () -> ClayIslandStructure.CODEC);
  public static final ResourceKey<Structure> clayIslandKey = registerKey("clay_island");
  public static final Holder<Structure> clayIslandStructure = BuiltinRegistries.register(BuiltinRegistries.STRUCTURES, clayIslandKey, new ClayIslandStructure(
    new Structure.StructureSettings(
      biomes(TinkerTags.Biomes.CLAY_ISLANDS), monsterOverride(TinkerWorld.terracubeEntity.get(), 2, 4),
      GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE
    )
  ));

  // nether
  public static final RegistryObject<StructureType<BloodSlimeIslandStructure>> bloodIsland = STRUCTURE_TYPES.register("blood_island", () -> () -> BloodSlimeIslandStructure.CODEC);
  public static final ResourceKey<Structure> bloodIslandKey = registerKey("blood_island");
  public static final Holder<Structure> bloodIslandStructure = BuiltinRegistries.register(BuiltinRegistries.STRUCTURES, bloodIslandKey, new BloodSlimeIslandStructure(
    new Structure.StructureSettings(
      biomes(TinkerTags.Biomes.BLOOD_ISLANDS), monsterOverride(EntityType.MAGMA_CUBE, 4, 6),
      GenerationStep.Decoration.UNDERGROUND_DECORATION, TerrainAdjustment.NONE
    )
  ));

  // end
  public static final RegistryObject<StructureType<EnderSlimeIslandStructure>> endSlimeIsland = STRUCTURE_TYPES.register("end_slime_island", () -> () -> EnderSlimeIslandStructure.CODEC);
  public static final ResourceKey<Structure> endSlimeIslandKey = registerKey("end_slime_island");
  public static final Holder<Structure> endSlimeIslandStructure = BuiltinRegistries.register(BuiltinRegistries.STRUCTURES, endSlimeIslandKey, new EnderSlimeIslandStructure(
    new Structure.StructureSettings(
      biomes(TinkerTags.Biomes.ENDERSLIME_ISLANDS), monsterOverride(TinkerWorld.enderSlimeEntity.get(), 4, 4),
      GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE
    )
  ));

  public static ResourceKey<Structure> registerKey(String name) {
    return ResourceKey.create(Registry.STRUCTURE_REGISTRY, TConstruct.getResource(name));
  }

  private static HolderSet<Biome> biomes(TagKey<Biome> tagKey) {
    return BuiltinRegistries.BIOME.getOrCreateTag(tagKey);
  }

  /** Creates a spawn override for a single mob */
  private static Map<MobCategory,StructureSpawnOverride> monsterOverride(EntityType<?> entity, int min, int max) {
    return Map.of(MobCategory.MONSTER, new StructureSpawnOverride(BoundingBoxType.STRUCTURE, WeightedRandomList.create(new MobSpawnSettings.SpawnerData(entity, 1, min, max))));
  }

  public static void init() {

  }
}
