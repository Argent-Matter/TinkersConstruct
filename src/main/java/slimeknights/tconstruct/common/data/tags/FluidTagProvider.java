package slimeknights.tconstruct.common.data.tags;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import slimeknights.mantle.datagen.MantleTags;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.fluids.TinkerFluids;

@SuppressWarnings("unchecked")
public class FluidTagProvider extends FabricTagProvider.FluidTagProvider {

  public FluidTagProvider(FabricDataGenerator generatorIn) {
    super(generatorIn);
  }

  @Override
  public void generateTags() {
    // first, register common tags
    // slime
    tagLocal(TinkerFluids.blood, FluidTags.WATER);
    tagAll(TinkerFluids.earthSlime);
    tagLocal(TinkerFluids.skySlime);
    tagLocal(TinkerFluids.enderSlime);
    tagAll(TinkerFluids.magma, FluidTags.LAVA);
    tagLocal(TinkerFluids.venom, FluidTags.WATER);
    // basic molten
    tagLocal(TinkerFluids.searedStone, FluidTags.LAVA);
    tagLocal(TinkerFluids.scorchedStone, FluidTags.LAVA);
    tagLocal(TinkerFluids.moltenClay, FluidTags.LAVA);
    tagLocal(TinkerFluids.moltenGlass, FluidTags.LAVA);
    tagLocal(TinkerFluids.liquidSoul, FluidTags.LAVA);
    tagLocal(TinkerFluids.moltenPorcelain, FluidTags.LAVA);
    // fancy molten
    tagLocal(TinkerFluids.moltenObsidian, FluidTags.LAVA);
    tagLocal(TinkerFluids.moltenEmerald, FluidTags.LAVA);
    tagLocal(TinkerFluids.moltenQuartz, FluidTags.LAVA);
    tagLocal(TinkerFluids.moltenDiamond, FluidTags.LAVA);
    tagLocal(TinkerFluids.moltenAmethyst, FluidTags.LAVA);
    tagAll(TinkerFluids.moltenEnder, FluidTags.LAVA);
    tagLocal(TinkerFluids.blazingBlood, FluidTags.LAVA);
    // ores
    tagAll(TinkerFluids.moltenIron, FluidTags.LAVA);
    tagAll(TinkerFluids.moltenGold, FluidTags.LAVA);
    tagAll(TinkerFluids.moltenCopper, FluidTags.LAVA);
    tagAll(TinkerFluids.moltenCobalt, FluidTags.LAVA);
    tagLocal(TinkerFluids.moltenDebris, FluidTags.LAVA);
    // alloys
    tagLocal(TinkerFluids.moltenSlimesteel, FluidTags.LAVA);
    tagAll(TinkerFluids.moltenAmethystBronze, FluidTags.LAVA);
    tagAll(TinkerFluids.moltenRoseGold, FluidTags.LAVA);
    tagLocal(TinkerFluids.moltenPigIron, FluidTags.LAVA);
    // nether alloys
    tagAll(TinkerFluids.moltenManyullyn, FluidTags.LAVA);
    tagAll(TinkerFluids.moltenHepatizon, FluidTags.LAVA);
    tagLocal(TinkerFluids.moltenQueensSlime, FluidTags.LAVA);
    tagLocal(TinkerFluids.moltenSoulsteel, FluidTags.LAVA);
    tagAll(TinkerFluids.moltenNetherite, FluidTags.LAVA);
    // end alloys
    tagLocal(TinkerFluids.moltenKnightslime, FluidTags.LAVA);
    // compat ores
    tagAll(TinkerFluids.moltenTin, FluidTags.LAVA);
    tagAll(TinkerFluids.moltenAluminum, FluidTags.LAVA);
    tagAll(TinkerFluids.moltenLead, FluidTags.LAVA);
    tagAll(TinkerFluids.moltenSilver, FluidTags.LAVA);
    tagAll(TinkerFluids.moltenNickel, FluidTags.LAVA);
    tagAll(TinkerFluids.moltenZinc, FluidTags.LAVA);
    tagAll(TinkerFluids.moltenPlatinum, FluidTags.LAVA);
    tagAll(TinkerFluids.moltenTungsten, FluidTags.LAVA);
    tagAll(TinkerFluids.moltenOsmium, FluidTags.LAVA);
    tagAll(TinkerFluids.moltenUranium, FluidTags.LAVA);
    // compat alloys
    tagAll(TinkerFluids.moltenBronze, FluidTags.LAVA);
    tagAll(TinkerFluids.moltenBrass, FluidTags.LAVA);
    tagAll(TinkerFluids.moltenElectrum, FluidTags.LAVA);
    tagAll(TinkerFluids.moltenInvar, FluidTags.LAVA);
    tagAll(TinkerFluids.moltenConstantan, FluidTags.LAVA);
    tagAll(TinkerFluids.moltenPewter, FluidTags.LAVA);
    tagAll(TinkerFluids.moltenSteel, FluidTags.LAVA);
    // thermal compat alloys
    tagAll(TinkerFluids.moltenEnderium, FluidTags.LAVA);
    tagAll(TinkerFluids.moltenLumium, FluidTags.LAVA);
    tagAll(TinkerFluids.moltenSignalum, FluidTags.LAVA);
    // mekanism compat alloys
    tagAll(TinkerFluids.moltenRefinedGlowstone, FluidTags.LAVA);
    tagAll(TinkerFluids.moltenRefinedObsidian, FluidTags.LAVA);
    // unplacable fluids
    tagAll(TinkerFluids.honey, FluidTags.WATER);
    tagAll(TinkerFluids.beetrootSoup, FluidTags.WATER);
    tagAll(TinkerFluids.mushroomStew, FluidTags.WATER);
    tagAll(TinkerFluids.rabbitStew, FluidTags.WATER);

    /* Normal tags */
    this.tag(TinkerTags.Fluids.SLIME)
        .addTag(TinkerFluids.earthSlime.getForgeTag())
        .addTag(TinkerFluids.skySlime.getLocalTag())
        .addTag(TinkerFluids.enderSlime.getLocalTag());

    this.tag(TinkerTags.Fluids.POTION).add(TinkerFluids.potion.get());

    // tooltips //
    this.tag(TinkerTags.Fluids.GLASS_TOOLTIPS).addTag(TinkerFluids.moltenGlass.getLocalTag()).addTag(TinkerFluids.liquidSoul.getLocalTag()).addTag(TinkerFluids.moltenObsidian.getLocalTag());
    this.tag(TinkerTags.Fluids.SLIME_TOOLTIPS).addTag(TinkerFluids.magma.getForgeTag()).addTag(TinkerFluids.blood.getLocalTag()).addTag(TinkerFluids.moltenEnder.getForgeTag()).addTag(TinkerTags.Fluids.SLIME);
    this.tag(TinkerTags.Fluids.CLAY_TOOLTIPS).addTag(TinkerFluids.moltenClay.getLocalTag()).addTag(TinkerFluids.moltenPorcelain.getLocalTag()).addTag(TinkerFluids.searedStone.getLocalTag()).addTag(TinkerFluids.scorchedStone.getLocalTag());
    this.tag(TinkerTags.Fluids.METAL_TOOLTIPS).addTag(
        // vanilla ores
        TinkerFluids.moltenIron.getForgeTag()).addTag(TinkerFluids.moltenGold.getForgeTag()).addTag(TinkerFluids.moltenCopper.getForgeTag()).addTag(TinkerFluids.moltenCobalt.getForgeTag()).addTag(TinkerFluids.moltenDebris.getLocalTag()).addTag(
        // base alloys
        TinkerFluids.moltenSlimesteel.getLocalTag()).addTag(TinkerFluids.moltenAmethystBronze.getLocalTag()).addTag(TinkerFluids.moltenRoseGold.getForgeTag()).addTag(TinkerFluids.moltenPigIron.getLocalTag()).addTag(
        TinkerFluids.moltenManyullyn.getForgeTag()).addTag(TinkerFluids.moltenHepatizon.getForgeTag()).addTag(TinkerFluids.moltenQueensSlime.getLocalTag()).addTag(TinkerFluids.moltenNetherite.getForgeTag()).addTag(
        TinkerFluids.moltenSoulsteel.getLocalTag()).addTag(TinkerFluids.moltenKnightslime.getLocalTag()).addTag(
        // compat ores
        TinkerFluids.moltenTin.getForgeTag()).addTag(TinkerFluids.moltenAluminum.getForgeTag()).addTag(TinkerFluids.moltenLead.getForgeTag()).addTag(TinkerFluids.moltenSilver.getForgeTag()).addTag(
        TinkerFluids.moltenNickel.getForgeTag()).addTag(TinkerFluids.moltenZinc.getForgeTag()).addTag(TinkerFluids.moltenPlatinum.getForgeTag()).addTag(
        TinkerFluids.moltenTungsten.getForgeTag()).addTag(TinkerFluids.moltenOsmium.getForgeTag()).addTag(TinkerFluids.moltenUranium.getForgeTag()).addTag(
        // compat alloys
        TinkerFluids.moltenBronze.getForgeTag()).addTag(TinkerFluids.moltenBrass.getForgeTag()).addTag(TinkerFluids.moltenElectrum.getForgeTag()).addTag(
        TinkerFluids.moltenInvar.getForgeTag()).addTag(TinkerFluids.moltenConstantan.getForgeTag()).addTag(TinkerFluids.moltenPewter.getForgeTag()).addTag(TinkerFluids.moltenSteel.getForgeTag()).addTag(
        // thermal alloys
        TinkerFluids.moltenEnderium.getForgeTag()).addTag(TinkerFluids.moltenLumium.getForgeTag()).addTag(TinkerFluids.moltenSignalum.getForgeTag()).addTag(
        // mekanism alloys
        TinkerFluids.moltenRefinedGlowstone.getForgeTag()).addTag(TinkerFluids.moltenRefinedObsidian.getForgeTag());

    this.tag(TinkerTags.Fluids.LARGE_GEM_TOOLTIPS).addTags(TinkerFluids.moltenEmerald.getLocalTag(), TinkerFluids.moltenDiamond.getLocalTag());
    this.tag(TinkerTags.Fluids.SMALL_GEM_TOOLTIPS).addTags(TinkerFluids.moltenQuartz.getLocalTag(), TinkerFluids.moltenAmethyst.getLocalTag());
    this.tag(TinkerTags.Fluids.SOUP_TOOLTIPS).addTags(TinkerFluids.beetrootSoup.getLocalTag(), TinkerFluids.mushroomStew.getLocalTag(), TinkerFluids.rabbitStew.getLocalTag());
    this.getOrCreateRawBuilder(TinkerTags.Fluids.WATER_TOOLTIPS).addOptionalTag(MantleTags.Fluids.WATER.location());

    // spilling tags - reduces the number of recipes generated //
    this.tag(TinkerTags.Fluids.CLAY_SPILLING)
        .addTag(TinkerFluids.moltenClay.getLocalTag())
        .addTag(TinkerFluids.moltenPorcelain.getLocalTag())
        .addTag(TinkerFluids.searedStone.getLocalTag())
        .addTag(TinkerFluids.scorchedStone.getLocalTag());
    this.tag(TinkerTags.Fluids.GLASS_SPILLING)
        .addTag(TinkerFluids.moltenGlass.getLocalTag())
        .addTag(TinkerFluids.moltenObsidian.getLocalTag());
    this.tag(TinkerTags.Fluids.CHEAP_METAL_SPILLING)
        .addTag(TinkerFluids.moltenPlatinum.getForgeTag())
        .addTag(TinkerFluids.moltenTungsten.getForgeTag())
        .addTag(TinkerFluids.moltenOsmium.getForgeTag())
        .addTag(TinkerFluids.moltenAmethyst.getLocalTag());
    this.tag(TinkerTags.Fluids.AVERAGE_METAL_SPILLING)
        .addTag(TinkerFluids.moltenQuartz.getLocalTag())
        .addTag(TinkerFluids.moltenEmerald.getLocalTag())
        .addTag(TinkerFluids.moltenRefinedGlowstone.getForgeTag());
    this.tag(TinkerTags.Fluids.EXPENSIVE_METAL_SPILLING)
        .addTag(TinkerFluids.moltenDiamond.getLocalTag())
        .addTag(TinkerFluids.moltenDebris.getLocalTag())
        .addTag(TinkerFluids.moltenEnderium.getForgeTag())
        .addTag(TinkerFluids.moltenLumium.getForgeTag())
        .addTag(TinkerFluids.moltenSignalum.getForgeTag())
        .addTag(TinkerFluids.moltenRefinedObsidian.getForgeTag());

    this.tag(FluidTags.WATER).addTag(TinkerTags.Fluids.SLIME);
  }

  /** Tags this fluid using local tags */
  private void tagLocal(FluidObject<?> fluid) {
    tag(fluid.getLocalTag()).add(fluid.getStill(), fluid.getFlowing());
  }

  private void tagLocal(FluidObject<?> fluid, TagKey<Fluid> extraTag) {
    tag(fluid.getLocalTag()).add(fluid.getStill(), fluid.getFlowing());
    tag(extraTag).addTag(fluid.getLocalTag());
  }

  /** Tags this fluid with local and forge tags */
  private void tagAll(FluidObject<?> fluid) {
    tagLocal(fluid);
    tag(fluid.getForgeTag()).addTag(fluid.getLocalTag());
  }

  private void tagAll(FluidObject<?> fluid, TagKey<Fluid> extraTag) {
    tagLocal(fluid, extraTag);
    tag(fluid.getForgeTag()).addTag(fluid.getLocalTag());
  }
}
