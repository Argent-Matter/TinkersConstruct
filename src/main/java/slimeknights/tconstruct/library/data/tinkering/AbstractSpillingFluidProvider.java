package slimeknights.tconstruct.library.data.tinkering;

import com.google.common.collect.ImmutableList;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.minecraft.data.CachedOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.Fluid;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.library.json.JsonCondition;
import slimeknights.tconstruct.library.json.predicate.IJsonPredicate;
import slimeknights.tconstruct.library.json.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.modifiers.spilling.ISpillingEffect;
import slimeknights.tconstruct.library.modifiers.spilling.SpillingFluidManager;
import slimeknights.tconstruct.library.modifiers.spilling.effects.ConditionalSpillingEffect;
import slimeknights.tconstruct.library.modifiers.spilling.effects.DamageSpillingEffect;
import slimeknights.tconstruct.library.modifiers.spilling.effects.DamageSpillingEffect.DamageType;
import slimeknights.tconstruct.library.modifiers.spilling.effects.SetFireSpillingEffect;
import slimeknights.tconstruct.library.recipe.FluidValues;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/** Data provider for spilling fluids */
public abstract class AbstractSpillingFluidProvider extends GenericDataProvider {
  private final String modId;
  private final Map<ResourceLocation,Builder> entries = new HashMap<>();

  public AbstractSpillingFluidProvider(FabricDataGenerator output, String modId) {
    super(output, PackType.SERVER_DATA, SpillingFluidManager.FOLDER, SpillingFluidManager.GSON);
    this.modId = modId;
  }

  /** Adds the fluids to the map */
  protected abstract void addFluids();

  @Override
  public CompletableFuture<?> run(CachedOutput cache) {
    addFluids();
    List<CompletableFuture<?>> futures = new ArrayList<>();
    entries.forEach((id, data) -> futures.add(saveThing(cache, id, data.build())));
    return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
  }

  /* Helpers */

  /** Creates a new fluid builder for the given location */
  protected Builder addFluid(ResourceLocation id, FluidIngredient fluid) {
    Builder newBuilder = new Builder(fluid);
    Builder original = entries.put(id, newBuilder);
    if (original != null) {
      throw new IllegalArgumentException("Duplicate spilling fluid " + id);
    }
    return newBuilder;
  }

  /** Creates a new fluid builder for the given mod ID */
  protected Builder addFluid(String name, FluidIngredient fluid) {
    return addFluid(new ResourceLocation(modId, name), fluid);
  }

  /** Creates a builder for a fluid stack */
  protected Builder addFluid(FluidStack fluid) {
    return addFluid(Objects.requireNonNull(Registry.FLUID.getKey(fluid.getFluid())).getPath(), FluidIngredient.of(fluid));
  }

  /** Creates a builder for a fluid and amount */
  protected Builder addFluid(Fluid fluid, long amount) {
    return addFluid(Objects.requireNonNull(Registry.FLUID.getKey(fluid)).getPath(), FluidIngredient.of(fluid, amount));
  }

  /** Creates a builder for a tag and amount */
  protected Builder addFluid(String name, TagKey<Fluid> fluid, long amount) {
    return addFluid(name, FluidIngredient.of(fluid, amount));
  }

  /** Creates a builder for a tag and amount */
  protected Builder addFluid(TagKey<Fluid> fluid, long amount) {
    return addFluid(fluid.location().getPath(), fluid, amount);
  }

  /** Creates a builder for a fluid object */
  protected Builder addFluid(FluidObject<?> fluid, boolean forgeTag, long amount) {
    return addFluid(forgeTag ? fluid.getForgeTag() : fluid.getLocalTag(), amount);
  }

  /** Adds a builder for burning with a nugget amount */
  protected Builder burningFluid(TagKey<Fluid> tag, float damage, int time) {
    return burningFluid(tag.location().getPath(), tag, FluidValues.NUGGET, damage, time);
  }

  /** Adds a builder for burning */
  protected Builder burningFluid(String name, TagKey<Fluid> tag, long amount, float damage, int time) {
    Builder builder = addFluid(name, tag, amount)
      .addEffect(LivingEntityPredicate.FIRE_IMMUNE.inverted(), new DamageSpillingEffect(DamageType.FIRE, damage));
    if (time > 0) {
      builder.addEffect(new SetFireSpillingEffect(time));
    }
    return builder;
  }

  /** Builder class */
  @RequiredArgsConstructor
  protected static class Builder {
    @Setter @Accessors(fluent = true)
    private ConditionJsonProvider condition = null;
    private final FluidIngredient ingredient;
    private final ImmutableList.Builder<ISpillingEffect> effects = ImmutableList.builder();

    /** Adds an effect to the given fluid */
    public Builder addEffect(ISpillingEffect effect) {
      effects.add(effect);
      return this;
    }

    /** Adds a effect to the given fluid that only matches if the entity matches the predicate */
    public Builder addEffect(IJsonPredicate<LivingEntity> predicate, ISpillingEffect effect) {
      return addEffect(new ConditionalSpillingEffect(predicate, effect));
    }

    /** Builds the instance */
    private SpillingFluidJson build() {
      List<ISpillingEffect> effects = this.effects.build();
      if (effects.isEmpty()) {
        throw new IllegalStateException("Must have at least 1 effect");
      }
      return new SpillingFluidJson(new JsonCondition(condition), ingredient, effects);
    }
  }

  /** Class of built effect instance */
  @SuppressWarnings({"ClassCanBeRecord", "unused"}) // breaks GSON
  @RequiredArgsConstructor
  private static class SpillingFluidJson {
    private final JsonCondition condition;
    private final FluidIngredient fluid;
    private final List<ISpillingEffect> effects;
  }
}
