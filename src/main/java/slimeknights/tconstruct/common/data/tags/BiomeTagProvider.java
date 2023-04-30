package slimeknights.tconstruct.common.data.tags;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.impl.datagen.FabricTagBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import slimeknights.tconstruct.common.TinkerTags;

import java.util.concurrent.CompletableFuture;

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
public class BiomeTagProvider extends FabricTagProvider<Biome> {

  public BiomeTagProvider(FabricDataGenerator output) {
    super(output, BuiltinRegistries.BIOME/*, "worldgen.biome.islands", "Tinkers' Biomes"*/);
  }

  @Override
  protected void generateTags() {
    this.tag(TinkerTags.Biomes.CLAY_ISLANDS).forceAddTag(IS_DEEP_OCEAN).forceAddTag(IS_OCEAN).forceAddTag(IS_BEACH).forceAddTag(IS_RIVER).forceAddTag(IS_MOUNTAIN).forceAddTag(IS_BADLANDS).forceAddTag(IS_HILL);
    this.tag(TinkerTags.Biomes.EARTHSLIME_ISLANDS).forceAddTag(IS_DEEP_OCEAN).forceAddTag(IS_OCEAN);
    this.tag(TinkerTags.Biomes.SKYSLIME_ISLANDS).forceAddTag(IS_DEEP_OCEAN).forceAddTag(IS_OCEAN).forceAddTag(IS_BEACH).forceAddTag(IS_RIVER).forceAddTag(IS_MOUNTAIN).forceAddTag(IS_BADLANDS).forceAddTag(IS_HILL).forceAddTag(IS_TAIGA).forceAddTag(IS_FOREST);
    this.tag(TinkerTags.Biomes.BLOOD_ISLANDS).forceAddTag(IS_NETHER);
    this.tag(TinkerTags.Biomes.ENDERSLIME_ISLANDS).add(END_HIGHLANDS, END_MIDLANDS, SMALL_END_ISLANDS, END_BARRENS);
  }

  public FabricTagBuilder tag(TagKey<Biome> tag) {
    return getOrCreateTagBuilder(tag);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Biome Tags";
  }
}
