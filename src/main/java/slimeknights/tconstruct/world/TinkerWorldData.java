package slimeknights.tconstruct.world;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.registration.GeodeItemObject;
import slimeknights.tconstruct.world.worldgen.trees.SupplierBlockStateProvider;


import java.util.Arrays;

import static slimeknights.tconstruct.world.TinkerWorld.cobaltOre;
import static slimeknights.tconstruct.world.TinkerWorld.earthGeode;
import static slimeknights.tconstruct.world.TinkerWorld.skyGeode;
import static slimeknights.tconstruct.world.TinkerWorld.ichorGeode;
import static slimeknights.tconstruct.world.TinkerWorld.enderGeode;

public class TinkerWorldData {

  public static void init() {

  }

  public static final Holder<ConfiguredFeature<GeodeConfiguration, Feature<GeodeConfiguration>>> configuredEarthGeode = registerGeodeConfig(
    "earth_geode", earthGeode, BlockStateProvider.simple(Blocks.CALCITE), BlockStateProvider.simple(Blocks.CLAY),
    new GeodeLayerSettings(1.7D, 2.2D, 3.2D, 5.2D), new GeodeCrackSettings(0.95D, 2.0D, 2), UniformInt.of(6, 9), UniformInt.of(3, 4), UniformInt.of(1, 2), 16, 1);
  public static ResourceKey<PlacedFeature> placedEarthGeodeKey = ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, TConstruct.getResource("earth_geode"));
  public static final Holder<PlacedFeature> placedEarthGeode = registerPlaced("earth_geode", configuredEarthGeode, RarityFilter.onAverageOnceEvery(128), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(6), VerticalAnchor.aboveBottom(54)));

  public static final Holder<ConfiguredFeature<GeodeConfiguration,Feature<GeodeConfiguration>>> configuredSkyGeode = registerGeodeConfig(
    "sky_geode", skyGeode, BlockStateProvider.simple(Blocks.CALCITE), BlockStateProvider.simple(Blocks.MOSSY_COBBLESTONE),
    new GeodeLayerSettings(1.5D, 2.0D, 3.0D, 4.5D), new GeodeCrackSettings(0.55D, 0.5D, 2), UniformInt.of(3, 4), ConstantInt.of(2), ConstantInt.of(1), 8, 3);
  public static ResourceKey<PlacedFeature> placedSkyGeodeKey = ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, TConstruct.getResource("sky_geode"));
  public static final Holder<PlacedFeature> placedSkyGeode = registerPlaced("sky_geode", configuredSkyGeode, RarityFilter.onAverageOnceEvery(64), HeightRangePlacement.uniform(VerticalAnchor.absolute(16), VerticalAnchor.absolute(54)));

  public static final Holder<ConfiguredFeature<GeodeConfiguration,Feature<GeodeConfiguration>>> configuredIchorGeode = registerGeodeConfig(
    "ichor_geode", ichorGeode, BlockStateProvider.simple(Blocks.CALCITE), BlockStateProvider.simple(Blocks.NETHERRACK),
    new GeodeLayerSettings(1.7D, 2.2D, 3.2D, 4.2D), new GeodeCrackSettings(0.75D, 2.0D, 2), UniformInt.of(4, 6), UniformInt.of(3, 4), UniformInt.of(1, 2), 24, 20);
  public static ResourceKey<PlacedFeature> placedIchorGeodeKey = ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, TConstruct.getResource("ichor_geode"));
  public static final Holder<PlacedFeature> placedIchorGeode = registerPlaced("ichor_geode", configuredIchorGeode, RarityFilter.onAverageOnceEvery(52), HeightRangePlacement.uniform(VerticalAnchor.belowTop(48), VerticalAnchor.belowTop(16)));

  public static final Holder<ConfiguredFeature<GeodeConfiguration,Feature<GeodeConfiguration>>> configuredEnderGeode = registerGeodeConfig(
    "ender_geode", enderGeode, BlockStateProvider.simple(Blocks.CALCITE), BlockStateProvider.simple(Blocks.END_STONE),
    new GeodeLayerSettings(1.7D, 2.2D, 3.2D, 5.2D), new GeodeCrackSettings(0.45, 1.0D, 2), UniformInt.of(4, 10), UniformInt.of(3, 4), UniformInt.of(1, 2), 16, 10000);
  public static ResourceKey<PlacedFeature> placedEnderGeodeKey = ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, TConstruct.getResource("ender_geode"));
  public static final Holder<PlacedFeature> placedEnderGeode = registerPlaced("ender_geode", configuredEnderGeode, RarityFilter.onAverageOnceEvery(256), HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(16), VerticalAnchor.aboveBottom(64)));

  public static Holder<ConfiguredFeature<GeodeConfiguration,Feature<GeodeConfiguration>>> registerGeodeConfig(String name, GeodeItemObject geode,
                                                                                                              BlockStateProvider middleLayer, BlockStateProvider outerLayer, GeodeLayerSettings layerSettings, GeodeCrackSettings crackSettings,
                                                                                                              IntProvider outerWall, IntProvider distributionPoints, IntProvider pointOffset, int genOffset, int invalidBlocks) {
    return BuiltinRegistries.registerExact(BuiltinRegistries.CONFIGURED_FEATURE, TConstruct.resourceString(name), new ConfiguredFeature<>(Feature.GEODE, new GeodeConfiguration(
      new GeodeBlockSettings(BlockStateProvider.simple(Blocks.AIR),
        BlockStateProvider.simple(geode.getBlock()),
        SupplierBlockStateProvider.ofBlock(geode::getBudding),
        middleLayer, outerLayer,
        Arrays.stream(GeodeItemObject.BudSize.values()).map(type -> geode.getBud(type).defaultBlockState()).toList(),
        BlockTags.FEATURES_CANNOT_REPLACE, BlockTags.GEODE_INVALID_BLOCKS),
      layerSettings, crackSettings, 0.335, 0.083, true, outerWall, distributionPoints, pointOffset, -genOffset, genOffset, 0.05D, invalidBlocks)));
  }

  public static Holder<PlacedFeature> registerPlaced(String name, Holder<ConfiguredFeature<GeodeConfiguration,Feature<GeodeConfiguration>>> geode, RarityFilter rarity, HeightRangePlacement height) {
    return registerPlaced(name, geode, rarity, InSquarePlacement.spread(), height, BiomeFilter.biome());
  }

  public static Holder<PlacedFeature> registerPlaced(String name, Holder<? extends ConfiguredFeature<?,?>> feature, PlacementModifier... placement) {
    return BuiltinRegistries.registerExact(BuiltinRegistries.PLACED_FEATURE, TConstruct.resourceString(name), new PlacedFeature(Holder.hackyErase(feature), Arrays.stream(placement).toList()));
  }

  /*
   * Features
   */
  // small veins, standard distribution
  public static Holder<ConfiguredFeature<OreConfiguration, Feature<OreConfiguration>>> configuredSmallCobaltOre = registerOre("cobalt_ore_small", Feature.ORE, new OreConfiguration(OreFeatures.NETHERRACK, cobaltOre.get().defaultBlockState(), 4));

  public static Holder<PlacedFeature> placedSmallCobaltOre = registerPlaced("cobalt_ore_small", configuredSmallCobaltOre, CountPlacement.of(5), InSquarePlacement.spread(), PlacementUtils.RANGE_8_8, BiomeFilter.biome());
  // large veins, around y=16, up to 48
  public static Holder<ConfiguredFeature<OreConfiguration,Feature<OreConfiguration>>> configuredLargeCobaltOre = registerOre("cobalt_ore_large", Feature.ORE, new OreConfiguration(OreFeatures.NETHERRACK, cobaltOre.get().defaultBlockState(), 6));
  public static Holder<PlacedFeature> placedLargeCobaltOre = registerPlaced("cobalt_ore_large", configuredSmallCobaltOre, CountPlacement.of(3), InSquarePlacement.spread(), HeightRangePlacement.triangle(VerticalAnchor.absolute(8), VerticalAnchor.absolute(32)), BiomeFilter.biome());

  public static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<ConfiguredFeature<FC, F>> registerOre(String name, F feature, FC featureConfiguration) {
    return BuiltinRegistries.registerExact(BuiltinRegistries.CONFIGURED_FEATURE, TConstruct.resourceString(name), new ConfiguredFeature<>(feature, featureConfiguration));
  }
}
