package slimeknights.tconstruct.tools.data;

import com.google.common.collect.Streams;
import io.github.fabricators_of_create.porting_lib.crafting.DifferenceIngredient;
import io.github.fabricators_of_create.porting_lib.crafting.IntersectionIngredient;
import me.alphamode.forgetags.Tags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.data.recipe.IMaterialRecipeHelper;
import slimeknights.tconstruct.library.data.recipe.IToolRecipeHelper;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.material.CompositeCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.partbuilder.PartRecipeBuilder;
import slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.tinkerstation.repairing.SpecializedRepairRecipeBuilder;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.data.material.MaterialIds;
import slimeknights.tconstruct.tools.item.ArmorSlotType;
import slimeknights.tconstruct.world.TinkerHeadType;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.Collections;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ToolsRecipeProvider extends BaseRecipeProvider implements IMaterialRecipeHelper, IToolRecipeHelper {
  public ToolsRecipeProvider(FabricDataOutput output) {
    super(output);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Tool Recipes";
  }

  @Override
  public void buildRecipes(Consumer<FinishedRecipe> consumer) {
    this.addToolBuildingRecipes(consumer);
    this.addPartRecipes(consumer);
  }

  private void addToolBuildingRecipes(Consumer<FinishedRecipe> consumer) {
    String folder = "tools/building/";
    String repairFolder = "tools/repair/";
    String armorFolder = "armor/building/";
    String armorRepairFolder = "armor/repair/";
    // stone
    toolBuilding(consumer, TinkerTools.pickaxe, folder);
    toolBuilding(consumer, TinkerTools.sledgeHammer, folder);
    toolBuilding(consumer, TinkerTools.veinHammer, folder);
    // dirt
    toolBuilding(consumer, TinkerTools.mattock, folder);
    toolBuilding(consumer, TinkerTools.pickadze, folder);
    toolBuilding(consumer, TinkerTools.excavator, folder);
    // wood
    toolBuilding(consumer, TinkerTools.handAxe, folder);
    toolBuilding(consumer, TinkerTools.broadAxe, folder);
    // plants
    toolBuilding(consumer, TinkerTools.kama, folder);
    toolBuilding(consumer, TinkerTools.scythe, folder);
    // sword
    ToolBuildingRecipeBuilder.toolBuildingRecipe(TinkerTools.dagger.get())
                             .outputSize(2)
                             .save(consumer, prefix(TinkerTools.dagger.getRegistryName(), folder));
    toolBuilding(consumer, TinkerTools.sword, folder);
    toolBuilding(consumer, TinkerTools.cleaver, folder);
    // bow
    toolBuilding(consumer, TinkerTools.crossbow, folder);
    toolBuilding(consumer, TinkerTools.longbow, folder);

    // specialized
    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, TinkerTools.flintAndBrick)
                          .requires(Items.FLINT)
                          .requires(Ingredient.of(TinkerSmeltery.searedBrick, TinkerSmeltery.scorchedBrick))
                          .unlockedBy("has_seared", has(TinkerSmeltery.searedBrick))
                          .unlockedBy("has_scorched", has(TinkerSmeltery.scorchedBrick))
                          .save(consumer, prefix(TinkerTools.flintAndBrick.getRegistryName(), folder));
    SpecializedRepairRecipeBuilder.repair(TinkerTools.flintAndBrick, MaterialIds.searedStone)
                                  .buildRepairKit(consumer, wrap(TinkerTools.flintAndBrick.getRegistryName(), repairFolder, "_seared_repair_kit"))
                                  .save(consumer, wrap(TinkerTools.flintAndBrick.getRegistryName(), repairFolder, "_seared_station"));
    SpecializedRepairRecipeBuilder.repair(TinkerTools.flintAndBrick, MaterialIds.scorchedStone)
                                  .buildRepairKit(consumer, wrap(TinkerTools.flintAndBrick.getRegistryName(), repairFolder, "_scorched_repair_kit"))
                                  .save(consumer, wrap(TinkerTools.flintAndBrick.getRegistryName(), repairFolder, "_scorched_station"));

    // staff
    ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TinkerTools.skyStaff)
                       .pattern("CWC")
                       .pattern(" I ")
                       .pattern(" W ")
                       .define('C', TinkerWorld.skyGeode)
                       .define('W', TinkerWorld.skyroot.getLogItemTag())
                       .define('I', TinkerMaterials.roseGold.getIngotTag())
                       .unlockedBy("has_wood", has(TinkerWorld.skyroot.getLogItemTag()))
                       .save(consumer, prefix(TinkerTools.skyStaff.getRegistryName(), folder));
    ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TinkerTools.earthStaff)
                       .pattern("CWC")
                       .pattern(" I ")
                       .pattern(" W ")
                       .define('C', TinkerWorld.earthGeode)
                       .define('W', TinkerWorld.greenheart.getLogItemTag())
                       .define('I', TinkerMaterials.cobalt.getIngotTag())
                       .unlockedBy("has_wood", has(TinkerWorld.greenheart.getLogItemTag()))
                       .save(consumer, prefix(TinkerTools.earthStaff.getRegistryName(), folder));
    ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TinkerTools.ichorStaff)
                       .pattern("CWC")
                       .pattern(" I ")
                       .pattern(" W ")
                       .define('C', TinkerWorld.ichorGeode)
                       .define('W', TinkerWorld.bloodshroom.getLogItemTag())
                       .define('I', TinkerMaterials.queensSlime.getIngotTag())
                       .unlockedBy("has_wood", has(TinkerWorld.bloodshroom.getLogItemTag()))
                       .save(consumer, prefix(TinkerTools.ichorStaff.getRegistryName(), folder));
    SpecializedRepairRecipeBuilder.repair(Ingredient.of(TinkerTools.skyStaff, TinkerTools.earthStaff, TinkerTools.ichorStaff), MaterialIds.slimewood)
                                  .buildRepairKit(consumer, modResource(repairFolder + "staff_repair_kit"))
                                  .save(consumer, modResource(repairFolder + "staff_station"));

    // travelers gear
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TinkerTools.travelersGear.get(ArmorSlotType.HELMET))
                       .pattern("l l")
                       .pattern("glg")
                       .pattern("c c")
                       .define('c', Tags.Items.INGOTS_COPPER)
                       .define('l', Tags.Items.LEATHER)
                       .define('g', Tags.Items.GLASS_PANES_COLORLESS)
                       .unlockedBy("has_item", has(Tags.Items.INGOTS_COPPER))
                       .save(consumer, modResource(armorFolder + "travelers_goggles"));
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TinkerTools.travelersGear.get(ArmorSlotType.CHESTPLATE))
                       .pattern("l l")
                       .pattern("lcl")
                       .pattern("lcl")
                       .define('c', Tags.Items.INGOTS_COPPER)
                       .define('l', Tags.Items.LEATHER)
                       .unlockedBy("has_item", has(Tags.Items.INGOTS_COPPER))
                       .save(consumer, modResource(armorFolder + "travelers_chestplate"));
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TinkerTools.travelersGear.get(ArmorSlotType.LEGGINGS))
                       .pattern("lll")
                       .pattern("c c")
                       .pattern("l l")
                       .define('c', Tags.Items.INGOTS_COPPER)
                       .define('l', Tags.Items.LEATHER)
                       .unlockedBy("has_item", has(Tags.Items.INGOTS_COPPER))
                       .save(consumer, modResource(armorFolder + "travelers_pants"));
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TinkerTools.travelersGear.get(ArmorSlotType.BOOTS))
                       .pattern("c c")
                       .pattern("l l")
                       .define('c', Tags.Items.INGOTS_COPPER)
                       .define('l', Tags.Items.LEATHER)
                       .unlockedBy("has_item", has(Tags.Items.INGOTS_COPPER))
                       .save(consumer, modResource(armorFolder + "travelers_boots"));
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, TinkerTools.travelersShield)
                       .pattern(" c ")
                       .pattern("cwc")
                       .pattern(" c ")
                       .define('c', Tags.Items.INGOTS_COPPER)
                       .define('w', DifferenceIngredient.of(Ingredient.of(ItemTags.PLANKS), Ingredient.of(TinkerTags.Items.SLIMY_PLANKS)))
                       .unlockedBy("has_item", has(Tags.Items.INGOTS_COPPER))
                       .save(consumer, modResource(armorFolder + "travelers_shield"));
    SpecializedRepairRecipeBuilder.repair(Ingredient.of(Streams.concat(TinkerTools.travelersGear.values().stream(), Stream.of(TinkerTools.travelersShield.get())).map(ItemStack::new)), MaterialIds.copper)
                                  .buildRepairKit(consumer, modResource(armorRepairFolder + "travelers_copper_repair_kit"))
                                  .save(consumer, modResource(armorRepairFolder + "travelers_copper_station"));
    SpecializedRepairRecipeBuilder.repair(Ingredient.of(TinkerTools.travelersGear.values().stream().map(ItemStack::new)), MaterialIds.leather)
                                  .buildRepairKit(consumer, modResource(armorRepairFolder + "travelers_leather_repair_kit"))
                                  .save(consumer, modResource(armorRepairFolder + "travelers_leather_station"));
    SpecializedRepairRecipeBuilder.repair(Ingredient.of(TinkerTools.travelersShield, TinkerTools.plateShield), MaterialIds.wood)
                                  .buildRepairKit(consumer, modResource(armorRepairFolder + "wood_repair_kit"))
                                  .save(consumer, modResource(armorRepairFolder + "wood_station"));

    // plate armor
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TinkerTools.plateArmor.get(ArmorSlotType.HELMET))
                       .pattern("mmm")
                       .pattern("ccc")
                       .define('m', TinkerMaterials.cobalt.getIngotTag())
                       .define('c', Items.CHAIN)
                       .unlockedBy("has_item", has(TinkerMaterials.cobalt.getIngotTag()))
                       .save(consumer, modResource(armorFolder + "plate_helmet"));
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TinkerTools.plateArmor.get(ArmorSlotType.CHESTPLATE))
                       .pattern("m m")
                       .pattern("mmm")
                       .pattern("cmc")
                       .define('m', TinkerMaterials.cobalt.getIngotTag())
                       .define('c', Items.CHAIN)
                       .unlockedBy("has_item", has(TinkerMaterials.cobalt.getIngotTag()))
                       .save(consumer, modResource(armorFolder + "plate_chestplate"));
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TinkerTools.plateArmor.get(ArmorSlotType.LEGGINGS))
                       .pattern("mmm")
                       .pattern("m m")
                       .pattern("c c")
                       .define('m', TinkerMaterials.cobalt.getIngotTag())
                       .define('c', Items.CHAIN)
                       .unlockedBy("has_item", has(TinkerMaterials.cobalt.getIngotTag()))
                       .save(consumer, modResource(armorFolder + "plate_leggings"));
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TinkerTools.plateArmor.get(ArmorSlotType.BOOTS))
                       .pattern("m m")
                       .pattern("m m")
                       .define('m', TinkerMaterials.cobalt.getIngotTag())
                       .unlockedBy("has_item", has(TinkerMaterials.cobalt.getIngotTag()))
                       .save(consumer, modResource(armorFolder + "plate_boots"));
    ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, TinkerTools.plateShield)
                       .pattern("ww")
                       .pattern("cc")
                       .pattern("ww")
                       .define('c', TinkerMaterials.cobalt.getIngotTag())
                       .define('w', DifferenceIngredient.of(IntersectionIngredient.of(Ingredient.of(ItemTags.PLANKS), Ingredient.of(ItemTags.NON_FLAMMABLE_WOOD)), Ingredient.of(TinkerTags.Items.SLIMY_PLANKS)))
                       .unlockedBy("has_item", has(TinkerMaterials.cobalt.getIngotTag()))
                       .save(consumer, modResource(armorFolder + "plate_shield"));
    SpecializedRepairRecipeBuilder.repair(Ingredient.of(Streams.concat(TinkerTools.plateArmor.values().stream(), Stream.of(TinkerTools.plateShield.asItem())).map(ItemStack::new)), MaterialIds.cobalt)
                                  .buildRepairKit(consumer, modResource(armorRepairFolder + "plate_repair_kit"))
                                  .save(consumer, modResource(armorRepairFolder + "plate_station"));

    // slimeskull
    slimeskullCasting(consumer, MaterialIds.glass,        Items.CREEPER_HEAD,          armorFolder);
    slimeskullCasting(consumer, MaterialIds.bone,         Items.SKELETON_SKULL,        armorFolder);
    slimeskullCasting(consumer, MaterialIds.necroticBone, Items.WITHER_SKELETON_SKULL, armorFolder);
    slimeskullCasting(consumer, MaterialIds.rottenFlesh,  Items.ZOMBIE_HEAD,           armorFolder);
    slimeskullCasting(consumer, MaterialIds.enderPearl,  TinkerWorld.heads.get(TinkerHeadType.ENDERMAN),         armorFolder);
    slimeskullCasting(consumer, MaterialIds.bloodbone,   TinkerWorld.heads.get(TinkerHeadType.STRAY),            armorFolder);
    slimeskullCasting(consumer, MaterialIds.string,      TinkerWorld.heads.get(TinkerHeadType.SPIDER),           armorFolder);
    slimeskullCasting(consumer, MaterialIds.darkthread,  TinkerWorld.heads.get(TinkerHeadType.CAVE_SPIDER),      armorFolder);
    slimeskullCasting(consumer, MaterialIds.iron,        TinkerWorld.heads.get(TinkerHeadType.HUSK),             armorFolder);
    slimeskullCasting(consumer, MaterialIds.copper,      TinkerWorld.heads.get(TinkerHeadType.DROWNED),          armorFolder);
    slimeskullCasting(consumer, MaterialIds.blazingBone, TinkerWorld.heads.get(TinkerHeadType.BLAZE),            armorFolder);
    slimeskullCasting(consumer, MaterialIds.gold,        TinkerWorld.heads.get(TinkerHeadType.PIGLIN),           armorFolder);
    slimeskullCasting(consumer, MaterialIds.roseGold,    TinkerWorld.heads.get(TinkerHeadType.PIGLIN_BRUTE),     armorFolder);
    slimeskullCasting(consumer, MaterialIds.pigIron,     TinkerWorld.heads.get(TinkerHeadType.ZOMBIFIED_PIGLIN), armorFolder);

    // slimelytra
    ItemCastingRecipeBuilder.basinRecipe(TinkerTools.slimesuit.get(ArmorSlotType.CHESTPLATE))
                            .setCast(Items.ELYTRA, true)
                            .setFluidAndTime(TinkerFluids.enderSlime, FluidValues.SLIME_CONGEALED * 8)
                            .save(consumer, modResource(armorFolder + "slimelytra"));
    SpecializedRepairRecipeBuilder.repair(Ingredient.of(TinkerTools.slimesuit.get(ArmorSlotType.CHESTPLATE)), MaterialIds.phantom)
                                  .buildRepairKit(consumer, modResource(armorRepairFolder + "slimelytra_repair_kit"))
                                  .save(consumer, modResource(armorRepairFolder + "slimelytra_station"));

    // slimeshell
    ItemCastingRecipeBuilder.basinRecipe(TinkerTools.slimesuit.get(ArmorSlotType.LEGGINGS))
                            .setCast(Items.SHULKER_SHELL, true)
                            .setFluidAndTime(TinkerFluids.enderSlime, FluidValues.SLIME_CONGEALED * 7)
                            .save(consumer, modResource(armorFolder + "slimeshell"));
    SpecializedRepairRecipeBuilder.repair(Ingredient.of(TinkerTools.slimesuit.get(ArmorSlotType.LEGGINGS)), MaterialIds.chorus)
                                  .buildRepairKit(consumer, modResource(armorRepairFolder + "slimeshell_repair_kit"))
                                  .save(consumer, modResource(armorRepairFolder + "slimeshell_station"));

    // boots
    ItemCastingRecipeBuilder.basinRecipe(TinkerTools.slimesuit.get(ArmorSlotType.BOOTS))
                            .setCast(Items.RABBIT_FOOT, true)
                            .setFluidAndTime(TinkerFluids.enderSlime, FluidValues.SLIME_CONGEALED * 4)
                            .save(consumer, modResource(armorFolder + "slime_boots"));
    SpecializedRepairRecipeBuilder.repair(Ingredient.of(TinkerTools.slimesuit.get(ArmorSlotType.BOOTS)), MaterialIds.leather)
                                  .buildRepairKit(consumer, modResource(armorRepairFolder + "slime_boots_repair_kit"))
                                  .save(consumer, modResource(armorRepairFolder + "slime_boots_station"));

    // general repair with enderslime
    SpecializedRepairRecipeBuilder.repair(Ingredient.of(TinkerTools.slimesuit.values().stream().map(ItemStack::new)), MaterialIds.enderslime)
                                  .buildRepairKit(consumer, modResource(armorRepairFolder + "slimesuit_repair_kit"))
                                  .save(consumer, modResource(armorRepairFolder + "slimesuit_station"));
  }

  private void addPartRecipes(Consumer<FinishedRecipe> consumer) {
    String partFolder = "tools/parts/";
    String castFolder = "smeltery/casts/";
    partRecipes(consumer, TinkerToolParts.repairKit, TinkerSmeltery.repairKitCast, 2, partFolder, castFolder);
    // head
    partRecipes(consumer, TinkerToolParts.pickHead,     TinkerSmeltery.pickHeadCast,     2, partFolder, castFolder);
    partRecipes(consumer, TinkerToolParts.hammerHead,   TinkerSmeltery.hammerHeadCast,   8, partFolder, castFolder);
    partRecipes(consumer, TinkerToolParts.smallAxeHead, TinkerSmeltery.smallAxeHeadCast, 2, partFolder, castFolder);
    partRecipes(consumer, TinkerToolParts.broadAxeHead, TinkerSmeltery.broadAxeHeadCast, 8, partFolder, castFolder);
    partRecipes(consumer, TinkerToolParts.smallBlade,   TinkerSmeltery.smallBladeCast,   2, partFolder, castFolder);
    partRecipes(consumer, TinkerToolParts.broadBlade,   TinkerSmeltery.broadBladeCast,   8, partFolder, castFolder);
    partRecipes(consumer, TinkerToolParts.bowLimb,      TinkerSmeltery.bowLimbCast,      2, partFolder, castFolder);
    partRecipes(consumer, TinkerToolParts.bowGrip,      TinkerSmeltery.bowGripCast,      2, partFolder, castFolder);
    // other parts
    partRecipes(consumer, TinkerToolParts.toolBinding, TinkerSmeltery.toolBindingCast, 1, partFolder, castFolder);
    partRecipes(consumer, TinkerToolParts.roundPlate,  TinkerSmeltery.roundPlateCast,  2, partFolder, castFolder);
    partRecipes(consumer, TinkerToolParts.largePlate,  TinkerSmeltery.largePlateCast,  4, partFolder, castFolder);
    partRecipes(consumer, TinkerToolParts.toolHandle,  TinkerSmeltery.toolHandleCast,  1, partFolder, castFolder);
    partRecipes(consumer, TinkerToolParts.toughHandle, TinkerSmeltery.toughHandleCast, 3, partFolder, castFolder);

    // bowstrings are not castable, part builder exclusive
    PartRecipeBuilder.partRecipe(TinkerToolParts.bowstring.get())
                     .setPattern(modResource("bowstring"))
                     .setPatternItem(Ingredient.of(TinkerTags.Items.DEFAULT_PATTERNS))
                     .setCost(1)
                     .save(consumer, modResource(partFolder + "builder/bowstring"));
    CompositeCastingRecipeBuilder.table(TinkerToolParts.bowstring.get(), 1)
                                 .save(consumer, modResource(partFolder + "casting//bowstring_composite"));
  }

  /** Helper to create a casting recipe for a slimeskull variant */
  private void slimeskullCasting(Consumer<FinishedRecipe> consumer, MaterialId material, ItemLike skull, String folder) {
    MaterialIdNBT nbt = new MaterialIdNBT(Collections.singletonList(material));
    ItemCastingRecipeBuilder.basinRecipe(ItemOutput.fromStack(nbt.updateStack(new ItemStack(TinkerTools.slimesuit.get(ArmorSlotType.HELMET)))))
                            .setCast(skull, true)
                            .setFluidAndTime(TinkerFluids.enderSlime, FluidValues.SLIME_CONGEALED * 5)
                            .save(consumer, modResource(folder + "slime_skull/" + material.getPath()));
  }
}
