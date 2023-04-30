package slimeknights.tconstruct.gadgets.data;

import me.alphamode.forgetags.Tags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.gadgets.entity.FrameType;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.function.Consumer;

public class GadgetRecipeProvider extends BaseRecipeProvider {
  public GadgetRecipeProvider(FabricDataGenerator output) {
    super(output);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Gadget Recipes";
  }

  @Override
  public void buildRecipes(Consumer<FinishedRecipe> consumer) {
    // slime
    String folder = "gadgets/slimesling/";
    for (SlimeType slime : SlimeType.TRUE_SLIME) {
      ResourceLocation name = modResource(folder + slime.getSerializedName());
      ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TinkerGadgets.slimeSling.get(slime))
                         .group("tconstruct:slimesling")
                         .define('#', Items.STRING)
                         .define('X', TinkerWorld.congealedSlime.get(slime))
                         .define('L', slime.getSlimeballTag())
                         .pattern("#X#")
                         .pattern("L L")
                         .pattern(" L ")
                         .unlockedBy("has_item", has(slime.getSlimeballTag()))
                         .save(consumer, name);
    }

    // throw balls
    folder = "gadgets/throwball/";
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TinkerGadgets.efln.get())
                       .define('#', Tags.Items.GUNPOWDER)
                       .define('X', Items.FLINT)
                       .pattern(" # ")
                       .pattern("#X#")
                       .pattern(" # ")
                       .unlockedBy("has_item", has(Tags.Items.DUSTS_GLOWSTONE))
                       .save(consumer, prefix(TinkerGadgets.efln.getRegistryName(), folder));
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TinkerGadgets.glowBall.get(), 8)
                       .define('#', Items.SNOWBALL)
                       .define('X', Tags.Items.DUSTS_GLOWSTONE)
                       .pattern("###")
                       .pattern("#X#")
                       .pattern("###")
                       .unlockedBy("has_item", has(Tags.Items.DUSTS_GLOWSTONE))
                       .save(consumer, prefix(TinkerGadgets.glowBall.getRegistryName(), folder));

    // Shurikens
    folder = "gadgets/shuriken/";
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TinkerGadgets.flintShuriken.get(), 4)
                        .define('X', Items.FLINT)
                        .pattern(" X ")
                        .pattern("X X")
                        .pattern(" X ")
                        .unlockedBy("has_item", has(Items.FLINT))
                        .save(consumer, prefix(TinkerGadgets.flintShuriken.getRegistryName(), folder));
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TinkerGadgets.quartzShuriken.get(), 4)
                        .define('X', Items.QUARTZ)
                        .pattern(" X ")
                        .pattern("X X")
                        .pattern(" X ")
                        .unlockedBy("has_item", has(Items.QUARTZ))
                        .save(consumer, prefix(TinkerGadgets.quartzShuriken.getRegistryName(), folder));

    // piggybackpack
    folder = "gadgets/";
    ItemCastingRecipeBuilder.tableRecipe(TinkerGadgets.piggyBackpack)
                            .setCast(Items.SADDLE, true)
                            .setFluidAndTime(TinkerFluids.blood, false, FluidValues.SLIME_CONGEALED)
                            .save(consumer, prefix(TinkerGadgets.piggyBackpack.getRegistryName(), folder));
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TinkerGadgets.punji)
                       .define('b', Items.BAMBOO)
                       .pattern(" b ")
                       .pattern("bbb")
                       .unlockedBy("has_item", has(Items.BAMBOO))
                       .save(consumer, prefix(TinkerGadgets.punji.getRegistryName(), folder));

    // frames
    folder = "gadgets/fancy_frame/";
    frameCrafting(consumer, Tags.Items.NUGGETS_GOLD, FrameType.GOLD);
    frameCrafting(consumer, TinkerMaterials.manyullyn.getNuggetTag(), FrameType.MANYULLYN);
    frameCrafting(consumer, TinkerTags.Items.NUGGETS_NETHERITE, FrameType.NETHERITE);
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TinkerGadgets.itemFrame.get(FrameType.DIAMOND))
                       .define('e', TinkerCommons.obsidianPane)
                       .define('M', Tags.Items.GEMS_DIAMOND)
                       .pattern(" e ")
                       .pattern("eMe")
                       .pattern(" e ")
                       .unlockedBy("has_item", has(Tags.Items.GEMS_DIAMOND))
                       .group(modPrefix("fancy_item_frame"))
                       .save(consumer, modResource("gadgets/frame/" + FrameType.DIAMOND.getSerializedName()));
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TinkerGadgets.itemFrame.get(FrameType.CLEAR))
                       .define('e', Tags.Items.GLASS_PANES_COLORLESS)
                       .define('M', Tags.Items.GLASS_COLORLESS)
                       .pattern(" e ")
                       .pattern("eMe")
                       .pattern(" e ")
                       .unlockedBy("has_item", has(Tags.Items.GLASS_PANES_COLORLESS))
                       .group(modPrefix("fancy_item_frame"))
                       .save(consumer, modResource(folder + FrameType.CLEAR.getSerializedName()));
    Item goldFrame = TinkerGadgets.itemFrame.get(FrameType.GOLD);
    Item reversedFrame = TinkerGadgets.itemFrame.get(FrameType.REVERSED_GOLD);
    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, reversedFrame)
                          .requires(goldFrame)
                          .requires(Items.REDSTONE_TORCH)
                          .unlockedBy("has_item", has(goldFrame))
                          .group(modPrefix("reverse_fancy_item_frame"))
                          .save(consumer, modResource(folder + FrameType.REVERSED_GOLD.getSerializedName()));
    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, goldFrame)
                          .requires(reversedFrame)
                          .requires(Items.REDSTONE_TORCH)
                          .unlockedBy("has_item", has(reversedFrame))
                          .group(modPrefix("reverse_fancy_item_frame"))
                          .save(consumer, modResource(folder + "reversed_reversed_gold"));

    String cakeFolder = "gadgets/cake/";
    TinkerGadgets.cake.forEach((slime, cake) -> {
      Item bucket = TinkerFluids.slime.get(slime).asItem();
      ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, cake)
                         .define('M', bucket)
                         .define('S', slime == SlimeType.BLOOD ? Ingredient.of(Tags.Items.DUSTS_GLOWSTONE) : Ingredient.of(Items.SUGAR))
                         .define('E', Items.EGG)
                         .define('W', TinkerWorld.slimeTallGrass.get(slime))
                         .pattern("MMM").pattern("SES").pattern("WWW")
                         .unlockedBy("has_slime", has(bucket))
                         .save(consumer, modResource(cakeFolder + slime.getSerializedName()));
    });
    Item bucket = TinkerFluids.magma.asItem();
    ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, TinkerGadgets.magmaCake)
                       .define('M', bucket)
                       .define('S', Ingredient.of(Tags.Items.DUSTS_GLOWSTONE))
                       .define('E', Items.EGG)
                       .define('W', Blocks.CRIMSON_ROOTS)
                       .pattern("MMM").pattern("SES").pattern("WWW")
                       .unlockedBy("has_slime", has(bucket))
                       .save(consumer, modResource(cakeFolder + "magma"));
  }


  /* Helpers */

  /**
   * Adds a campfire cooking recipe
   * @param consumer    Recipe consumer
   * @param input       Recipe input
   * @param output      Recipe output
   * @param experience  Experience for the recipe
   * @param folder      Folder to store the recipe
   */
  private void campfireCooking(Consumer<FinishedRecipe> consumer, ItemLike input, ItemLike output, float experience, String folder) {
    SimpleCookingRecipeBuilder.generic(Ingredient.of(input), RecipeCategory.MISC, output, experience, 600, RecipeSerializer.CAMPFIRE_COOKING_RECIPE)
                              .unlockedBy("has_item", has(input))
                              .save(consumer, wrap(output.asItem(), folder, "_campfire"));
  }

  /**
   * Adds a recipe to the campfire, furnace, and smoker
   * @param consumer    Recipe consumer
   * @param input       Recipe input
   * @param output      Recipe output
   * @param experience  Experience for the recipe
   * @param folder      Folder to store the recipe
   */
  private void foodCooking(Consumer<FinishedRecipe> consumer, ItemLike input, ItemLike output, float experience, String folder) {
    campfireCooking(consumer, input, output, experience, folder);
    // furnace is 200 ticks
    InventoryChangeTrigger.TriggerInstance criteria = has(input);
    SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), RecipeCategory.MISC, output, experience, 200)
                              .unlockedBy("has_item", criteria)
                              .save(consumer, wrap(output.asItem(), folder, "_furnace"));
    // smoker 100 ticks
    SimpleCookingRecipeBuilder.generic(Ingredient.of(input), RecipeCategory.MISC, output, experience, 100, RecipeSerializer.SMOKING_RECIPE)
                              .unlockedBy("has_item", criteria)
                              .save(consumer, wrap(output.asItem(), folder, "_smoker"));
  }

  /**
   * Adds a recipe for an item frame type
   * @param consumer  Recipe consumer
   * @param edges     Edge item
   * @param type      Frame type
   */
  private void frameCrafting(Consumer<FinishedRecipe> consumer, TagKey<Item> edges, FrameType type) {
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TinkerGadgets.itemFrame.get(type))
                       .define('e', edges)
                       .define('M', TinkerCommons.obsidianPane)
                       .pattern(" e ")
                       .pattern("eMe")
                       .pattern(" e ")
                       .unlockedBy("has_item", has(edges))
                       .group(modPrefix("fancy_item_frame"))
                       .save(consumer, modResource("gadgets/frame/" + type.getSerializedName()));
  }
}
