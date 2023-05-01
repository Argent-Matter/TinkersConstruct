package slimeknights.tconstruct.common.data.tags;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.impl.datagen.FabricTagBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import slimeknights.tconstruct.common.TinkerTags;

import static net.minecraft.tags.BiomeTags.IS_BADLANDS;
import static net.minecraft.tags.BiomeTags.IS_BEACH;
import static net.minecraft.tags.BiomeTags.IS_DEEP_OCEAN;
import static net.minecraft.tags.BiomeTags.IS_FOREST;
import static net.minecraft.tags.BiomeTags.IS_HILL;
import static net.minecraft.tags.BiomeTags.IS_MOUNTAIN;
import static net.minecraft.tags.BiomeTags.IS_NETHER;
import static net.minecraft.tags.BiomeTags.IS_OCEAN;
import static net.minecraft.tags.BiomeTags.IS_RIVER;
import static net.minecraft.tags.BiomeTags.IS_TAIGA;
import static net.minecraft.world.level.biome.Biomes.END_BARRENS;
import static net.minecraft.world.level.biome.Biomes.END_HIGHLANDS;
import static net.minecraft.world.level.biome.Biomes.END_MIDLANDS;
import static net.minecraft.world.level.biome.Biomes.SMALL_END_ISLANDS;

@SuppressWarnings("unchecked")
public class BiomeTagProvider extends TagsProvider<Biome> {

  public BiomeTagProvider(FabricDataGenerator generatorIn) {
    super(generatorIn, BuiltinRegistries.BIOME);
  }
  @Override
  protected void addTags() {
    this.tag(TinkerTags.Biomes.CLAY_ISLANDS).addOptionalTag(IS_DEEP_OCEAN.location()).addOptionalTag(IS_OCEAN.location()).addOptionalTag(IS_BEACH.location()).addOptionalTag(IS_RIVER.location()).addOptionalTag(IS_MOUNTAIN.location()).addOptionalTag(IS_BADLANDS.location()).addOptionalTag(IS_HILL.location());
    this.tag(TinkerTags.Biomes.EARTHSLIME_ISLANDS).addOptionalTag(IS_DEEP_OCEAN.location()).addOptionalTag(IS_OCEAN.location());
    this.tag(TinkerTags.Biomes.SKYSLIME_ISLANDS).addOptionalTag(IS_DEEP_OCEAN.location()).addOptionalTag(IS_OCEAN.location()).addOptionalTag(IS_BEACH.location()).addOptionalTag(IS_RIVER.location()).addOptionalTag(IS_MOUNTAIN.location()).addOptionalTag(IS_BADLANDS.location()).addOptionalTag(IS_HILL.location()).addOptionalTag(IS_TAIGA.location()).addOptionalTag(IS_FOREST.location());
    this.tag(TinkerTags.Biomes.BLOOD_ISLANDS).addOptionalTag(IS_NETHER.location());
    this.tag(TinkerTags.Biomes.ENDERSLIME_ISLANDS).add(END_HIGHLANDS, END_MIDLANDS, SMALL_END_ISLANDS, END_BARRENS);
  }
}
