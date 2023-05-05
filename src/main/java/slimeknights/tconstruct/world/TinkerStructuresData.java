package slimeknights.tconstruct.world;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.data.BuiltinRegistries;
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
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.block.SlimeVineBlock;
import slimeknights.tconstruct.world.worldgen.islands.BloodSlimeIslandStructure;
import slimeknights.tconstruct.world.worldgen.islands.ClayIslandStructure;
import slimeknights.tconstruct.world.worldgen.islands.EarthSlimeIslandStructure;
import slimeknights.tconstruct.world.worldgen.islands.EnderSlimeIslandStructure;
import slimeknights.tconstruct.world.worldgen.islands.SkySlimeIslandStructure;
import slimeknights.tconstruct.world.worldgen.trees.config.SlimeFungusConfig;
import slimeknights.tconstruct.world.worldgen.trees.config.SlimeTreeConfig;
import slimeknights.tconstruct.world.worldgen.trees.feature.SlimeFungusFeature;
import slimeknights.tconstruct.world.worldgen.trees.feature.SlimeTreeFeature;

import java.util.Map;

import static slimeknights.tconstruct.world.TinkerStructures.*;

public class TinkerStructuresData {

  public static void init() {

  }

  /** Greenheart tree variant */
  public static final Holder<ConfiguredFeature<SlimeTreeConfig, SlimeTreeFeature>> earthSlimeTree = registerTree(
    "earth_slime_tree", slimeTree.get(),
    new SlimeTreeConfig.Builder()
      .planted()
      .trunk(() -> TinkerWorld.greenheart.getLog().defaultBlockState())
      .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.EARTH).defaultBlockState())
      .baseHeight(4).randomHeight(3)
      .build());
  /** Greenheart tree variant on islands */
  public static final Holder<ConfiguredFeature<SlimeTreeConfig,SlimeTreeFeature>> earthSlimeIslandTree = registerTree(
    "earth_slime_island_tree", slimeTree.get(),
    new SlimeTreeConfig.Builder()
      .trunk(() -> TinkerWorld.greenheart.getLog().defaultBlockState())
      .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.EARTH).defaultBlockState())
      .baseHeight(4).randomHeight(3)
      .build());

  /** Skyroot tree variant */
  public static final Holder<ConfiguredFeature<SlimeTreeConfig,SlimeTreeFeature>> skySlimeTree = registerTree(
    "sky_slime_tree", slimeTree.get(),
    new SlimeTreeConfig.Builder()
      .planted().canDoubleHeight()
      .trunk(() -> TinkerWorld.skyroot.getLog().defaultBlockState())
      .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.SKY).defaultBlockState())
      .build());
  /** Skyroot tree variant on islands */
  public static final Holder<ConfiguredFeature<SlimeTreeConfig,SlimeTreeFeature>> skySlimeIslandTree = registerTree(
    "sky_slime_island_tree", slimeTree.get(),
    new SlimeTreeConfig.Builder()
      .canDoubleHeight()
      .trunk(() -> TinkerWorld.skyroot.getLog().defaultBlockState())
      .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.SKY).defaultBlockState())
      .vines(() -> TinkerWorld.skySlimeVine.get().defaultBlockState().setValue(SlimeVineBlock.STAGE, SlimeVineBlock.VineStage.MIDDLE))
      .build());

  /** Enderslime island tree variant */
  public static final Holder<ConfiguredFeature<SlimeTreeConfig,SlimeTreeFeature>> enderSlimeTree = registerTree(
    "ender_slime_tree", slimeTree.get(),
    new SlimeTreeConfig.Builder()
      .planted()
      .trunk(() -> TinkerWorld.greenheart.getLog().defaultBlockState()) // TODO: temporary until we have proper green trees and ender shrooms
      .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.ENDER).defaultBlockState())
      .build());
  /** Enderslime island tree variant on islands */
  public static final Holder<ConfiguredFeature<SlimeTreeConfig,SlimeTreeFeature>> enderSlimeIslandTree = registerTree(
    "ender_slime_island_tree", slimeTree.get(),
    new SlimeTreeConfig.Builder()
      .trunk(() -> TinkerWorld.greenheart.getLog().defaultBlockState()) // TODO: temporary until we have proper green trees and ender shrooms
      .leaves(() -> TinkerWorld.slimeLeaves.get(SlimeType.ENDER).defaultBlockState())
      .vines(() -> TinkerWorld.enderSlimeVine.get().defaultBlockState().setValue(SlimeVineBlock.STAGE, SlimeVineBlock.VineStage.MIDDLE))
      .build());

  /** Bloodshroom tree variant */
  public static final Holder<ConfiguredFeature<HugeFungusConfiguration, SlimeFungusFeature>> bloodSlimeFungus = registerTree(
    "blood_slime_fungus", slimeFungus.get(),
    new SlimeFungusConfig(
      TinkerTags.Blocks.SLIMY_SOIL,
      TinkerWorld.bloodshroom.getLog().defaultBlockState(),
      TinkerWorld.slimeLeaves.get(SlimeType.BLOOD).defaultBlockState(),
      TinkerWorld.congealedSlime.get(SlimeType.ICHOR).defaultBlockState(),
      true));
  /** Bloodshroom island tree variant */
  public static final Holder<ConfiguredFeature<HugeFungusConfiguration,SlimeFungusFeature>> bloodSlimeIslandFungus = registerTree(
    "blood_slime_island_fungus", slimeFungus.get(),
    new SlimeFungusConfig(
      TinkerTags.Blocks.SLIMY_NYLIUM,
      TinkerWorld.bloodshroom.getLog().defaultBlockState(),
      TinkerWorld.slimeLeaves.get(SlimeType.BLOOD).defaultBlockState(),
      TinkerWorld.congealedSlime.get(SlimeType.ICHOR).defaultBlockState(),
      false));
  /* Deprecated ichor tree */
  public static final Holder<ConfiguredFeature<HugeFungusConfiguration,SlimeFungusFeature>> ichorSlimeFungus = registerTree(
    "ichor_slime_fungus", slimeFungus.get(),
    new SlimeFungusConfig(
      TinkerTags.Blocks.SLIMY_SOIL,
      TinkerWorld.bloodshroom.getLog().defaultBlockState(),
      TinkerWorld.slimeLeaves.get(SlimeType.ICHOR).defaultBlockState(),
      TinkerWorld.congealedSlime.get(SlimeType.ICHOR).defaultBlockState(),
      false));

  public static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<ConfiguredFeature<FC,F>> registerTree(String name, F key, FC feature) {
    return BuiltinRegistries.registerExact(BuiltinRegistries.CONFIGURED_FEATURE, TConstruct.resourceString(name), new ConfiguredFeature<>(key, feature));
  }


  /*public static final Holder<Structure> earthSlimeIslandStructure = BuiltinRegistries.register(BuiltinRegistries.STRUCTURES, earthSlimeIslandKey, new EarthSlimeIslandStructure(
    new Structure.StructureSettings(
      biomes(TinkerTags.Biomes.EARTHSLIME_ISLANDS), monsterOverride(TinkerWorld.earthSlimeEntity.get(), 4, 4),
      GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE))
  );
  public static final Holder<Structure> skySlimeIslandStructure = BuiltinRegistries.register(BuiltinRegistries.STRUCTURES, skySlimeIslandKey, new SkySlimeIslandStructure(
    new Structure.StructureSettings(
      biomes(TinkerTags.Biomes.SKYSLIME_ISLANDS), monsterOverride(TinkerWorld.skySlimeEntity.get(), 3, 4),
      GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE))
  );

  public static final Holder<Structure> clayIslandStructure = BuiltinRegistries.register(BuiltinRegistries.STRUCTURES, clayIslandKey, new ClayIslandStructure(
    new Structure.StructureSettings(
      biomes(TinkerTags.Biomes.CLAY_ISLANDS), monsterOverride(TinkerWorld.terracubeEntity.get(), 2, 4),
      GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE))
  );
  public static final Holder<Structure> bloodIslandStructure = BuiltinRegistries.register(BuiltinRegistries.STRUCTURES, bloodIslandKey, new BloodSlimeIslandStructure(
    new Structure.StructureSettings(
      biomes(TinkerTags.Biomes.BLOOD_ISLANDS), monsterOverride(EntityType.MAGMA_CUBE, 4, 6),
      GenerationStep.Decoration.UNDERGROUND_DECORATION, TerrainAdjustment.NONE))
  );
  public static final Holder<Structure> endSlimeIslandStructure = BuiltinRegistries.register(BuiltinRegistries.STRUCTURES, endSlimeIslandKey, new EnderSlimeIslandStructure(
    new Structure.StructureSettings(
      biomes(TinkerTags.Biomes.ENDERSLIME_ISLANDS), monsterOverride(TinkerWorld.enderSlimeEntity.get(), 4, 4),
      GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE))
  );*/

  private static HolderSet<Biome> biomes(TagKey<Biome> tagKey) {
    return BuiltinRegistries.BIOME.getOrCreateTag(tagKey);
  }

  /** Creates a spawn override for a single mob */
  private static Map<MobCategory, StructureSpawnOverride> monsterOverride(EntityType<?> entity, int min, int max) {
    return Map.of(MobCategory.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.STRUCTURE, WeightedRandomList.create(new MobSpawnSettings.SpawnerData(entity, 1, min, max))));
  }

}
