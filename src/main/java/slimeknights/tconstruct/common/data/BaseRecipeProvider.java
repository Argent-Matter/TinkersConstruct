package slimeknights.tconstruct.common.data;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.recipe.data.IRecipeHelper;
import slimeknights.tconstruct.TConstruct;

import java.util.Set;
import java.util.function.Consumer;

/**
 * Shared logic for each module's recipe provider
 */
public abstract class BaseRecipeProvider extends RecipeProvider implements /*IConditionBuilder,*/ IRecipeHelper {
  private final DataGenerator.PathProvider recipePathProvider;
  private final DataGenerator.PathProvider advancementPathProvider;

  public BaseRecipeProvider(FabricDataGenerator generator) {
    super(generator);
    TConstruct.sealTinkersClass(this, "BaseRecipeProvider", "BaseRecipeProvider is trivial to recreate and directly extending can lead to addon recipes polluting our namespace.");
    this.recipePathProvider = generator.createPathProvider(DataGenerator.Target.DATA_PACK, "recipes");
    this.advancementPathProvider = generator.createPathProvider(DataGenerator.Target.DATA_PACK, "advancements");
  }

  @Override
  public void run(CachedOutput cachedOutput) {
    Set<ResourceLocation> set = Sets.newHashSet();
    generateRecipes((finishedRecipe) -> {
      if (!set.add(finishedRecipe.getId())) {
        throw new IllegalStateException("Duplicate recipe " + finishedRecipe.getId());
      } else {
        saveRecipe(cachedOutput, finishedRecipe.serializeRecipe(), this.recipePathProvider.json(finishedRecipe.getId()));
        JsonObject jsonObject = finishedRecipe.serializeAdvancement();
        if (jsonObject != null) {
          saveAdvancement(cachedOutput, jsonObject, this.advancementPathProvider.json(finishedRecipe.getAdvancementId()));
        }

      }
    });
    saveAdvancement(cachedOutput, Advancement.Builder.advancement().addCriterion("impossible", new ImpossibleTrigger.TriggerInstance()).serializeToJson(), this.advancementPathProvider.json(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT));
  }

  protected abstract void generateRecipes(Consumer<FinishedRecipe> consumer);

  @Override
  public abstract String getName();

  @Override
  public String getModId() {
    return TConstruct.MOD_ID;
  }
}
