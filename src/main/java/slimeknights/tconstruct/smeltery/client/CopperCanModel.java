/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package slimeknights.tconstruct.smeltery.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import io.github.fabricators_of_create.porting_lib.model.CompositeModel;
import io.github.fabricators_of_create.porting_lib.model.CompositeModelState;
import io.github.fabricators_of_create.porting_lib.model.IGeometryBakingContext;
import io.github.fabricators_of_create.porting_lib.model.IGeometryLoader;
import io.github.fabricators_of_create.porting_lib.model.IUnbakedGeometry;
import io.github.fabricators_of_create.porting_lib.model.ItemLayerModel;
import io.github.fabricators_of_create.porting_lib.model.ItemTextureQuadConverter;
import io.github.fabricators_of_create.porting_lib.model.PerspectiveMapWrapper;
import io.github.fabricators_of_create.porting_lib.model.RenderTypeGroup;
import io.github.fabricators_of_create.porting_lib.model.SimpleModelState;
import io.github.fabricators_of_create.porting_lib.model.geometry.UnbakedGeometryHelper;
import lombok.RequiredArgsConstructor;
import lombok.With;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.smeltery.item.CopperCanItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Reimplementation of {@link io.github.fabricators_of_create.porting_lib.model.DynamicFluidContainerModel} as the forge one does not handle fluid NBT
 */
@SuppressWarnings("removal")
@RequiredArgsConstructor
public final class CopperCanModel implements IUnbakedGeometry<CopperCanModel> {
  public static final Loader LOADER = new Loader();

  private static final Transformation FLUID_TRANSFORM = new Transformation(Vector3f.ZERO, Quaternion.ONE, new Vector3f(1, 1, 1.002f), Quaternion.ONE);
  private static final Transformation COVER_TRANSFORM = new Transformation(Vector3f.ZERO, Quaternion.ONE, new Vector3f(1, 1, 1.004f), Quaternion.ONE);

  // minimal Z offset to prevent depth-fighting
  private static final float NORTH_Z_COVER = 7.496f / 16f;
  private static final float SOUTH_Z_COVER = 8.504f / 16f;
  private static final float NORTH_Z_FLUID = 7.498f / 16f;
  private static final float SOUTH_Z_FLUID = 8.502f / 16f;

  @Nonnull
  @With
  private final FluidStack fluid;
  private final boolean coverIsMask;
  private final boolean applyFluidLuminosity;

  @Override
  public BakedModel bake(IGeometryBakingContext owner, ModelBakery bakery, Function<Material,TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
    Material particleLocation = owner.hasMaterial("particle") ? owner.getMaterial("particle") : null;
    Material baseLocation = owner.hasMaterial("base") ? owner.getMaterial("base") : null;
    Material fluidMaskLocation = owner.hasMaterial("fluid") ? owner.getMaterial("fluid") : null;
    Material coverLocation = owner.hasMaterial("cover") ? owner.getMaterial("cover") : null;

    TextureAtlasSprite baseSprite = baseLocation != null ? spriteGetter.apply(baseLocation) : null;
    TextureAtlasSprite fluidSprite = FluidVariantRendering.getSprite(fluid.getType());
    TextureAtlasSprite coverSprite = (coverLocation != null && (!coverIsMask || baseLocation != null)) ? spriteGetter.apply(coverLocation) : null;

    TextureAtlasSprite particleSprite = particleLocation != null ? spriteGetter.apply(particleLocation) : null;

    if (particleSprite == null) particleSprite = fluidSprite;
    if (particleSprite == null) particleSprite = baseSprite;
    if (particleSprite == null && !coverIsMask) particleSprite = coverSprite;

    // If the fluid is lighter than air, rotate 180deg to turn it upside down
    if (!fluid.isEmpty() && FluidVariantAttributes.isLighterThanAir(fluid.getType()))
    {
      modelTransform = new SimpleModelState(
        modelTransform.getRotation().compose(
          new Transformation(null, new Quaternion(0, 0, 1, 0), null, null)));
    }

    // We need to disable GUI 3D and block lighting for this to render properly
    var modelBuilder = CompositeModel.Baked.builder(owner.useAmbientOcclusion(), false, owner.useBlockLight(), particleSprite, new ContainedFluidOverrideHandler(overrides, bakery, owner, this), owner.getTransforms());

    RenderTypeGroup group = new RenderTypeGroup(RenderType.solid(), RenderType.translucent());
    if (baseLocation != null && baseSprite != null)
    {
      // Base texture
      var unbaked = UnbakedGeometryHelper.createUnbakedItemElements(0, baseSprite);
      var quads = UnbakedGeometryHelper.bakeElements(unbaked, $ -> baseSprite, modelTransform, modelLocation);
      modelBuilder.addQuads(group, quads);
    }

    if (fluidMaskLocation != null && fluidSprite != null)
    {
      TextureAtlasSprite templateSprite = spriteGetter.apply(fluidMaskLocation);
      if (templateSprite != null)
      {
        // Fluid layer
        var transformedState = new SimpleModelState(modelTransform.getRotation().compose(FLUID_TRANSFORM), modelTransform.isUvLocked());
        var unbaked = UnbakedGeometryHelper.createUnbakedItemMaskElements(1, templateSprite); // Use template as mask
        var quads = UnbakedGeometryHelper.bakeElements(unbaked, $ -> fluidSprite, transformedState, modelLocation); // Bake with fluid texture

        modelBuilder.addQuads(group, quads);
      }
    }

    if (coverSprite != null)
    {
      var sprite = coverIsMask ? baseSprite : coverSprite;
      if (sprite != null)
      {
        // Cover/overlay
        var transformedState = new SimpleModelState(modelTransform.getRotation().compose(COVER_TRANSFORM), modelTransform.isUvLocked());
        var unbaked = UnbakedGeometryHelper.createUnbakedItemMaskElements(2, coverSprite); // Use cover as mask
        var quads = UnbakedGeometryHelper.bakeElements(unbaked, $ -> sprite, transformedState, modelLocation); // Bake with selected texture
        modelBuilder.addQuads(group, quads);
      }
    }

    modelBuilder.setParticle(particleSprite);

    return modelBuilder.build();
  }

  @Override
  public Collection<Material> getMaterials(IGeometryBakingContext owner, Function<ResourceLocation,UnbakedModel> modelGetter, Set<Pair<String,String>> missingTextureErrors) {
    Set<Material> texs = Sets.newHashSet();
    if (owner.hasMaterial("particle")) texs.add(owner.getMaterial("particle"));
    if (owner.hasMaterial("base"))     texs.add(owner.getMaterial("base"));
    if (owner.hasMaterial("fluid"))    texs.add(owner.getMaterial("fluid"));
    if (owner.hasMaterial("cover"))    texs.add(owner.getMaterial("cover"));
    return texs;
  }

  private static class Loader implements IGeometryLoader<CopperCanModel> {

    @Override
    public CopperCanModel read(JsonObject modelContents, JsonDeserializationContext deserializationContext) {
      boolean coverIsMask = GsonHelper.getAsBoolean(modelContents, "coverIsMask", true);
      boolean applyFluidLuminosity = GsonHelper.getAsBoolean(modelContents, "applyFluidLuminosity", true);
      return new CopperCanModel(FluidStack.EMPTY, coverIsMask, applyFluidLuminosity);
    }
  }

  private static final class ContainedFluidOverrideHandler extends ItemOverrides {
    private static final ResourceLocation BAKE_LOCATION = TConstruct.getResource("copper_can_dynamic");
    private final Map<FluidStack,BakedModel> cache = Maps.newHashMap(); // contains all the baked models since they'll never change
    private final ItemOverrides nested;
    private final ModelBakery bakery;
    private final IGeometryBakingContext owner;
    private final CopperCanModel parent;

    private ContainedFluidOverrideHandler(ItemOverrides nested, ModelBakery bakery, IGeometryBakingContext owner, CopperCanModel parent) {
      this.nested = nested;
      this.bakery = bakery;
      this.owner = owner;
      this.parent = parent;
    }

    /** Gets the model directly, for creating the cached models */
    private BakedModel getUncahcedModel(FluidStack fluid) {
      return this.parent.withFluid(fluid).bake(owner, bakery, Material::sprite, BlockModelRotation.X0_Y0, ItemOverrides.EMPTY, BAKE_LOCATION);
    }

    @Override
    public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
      BakedModel overriden = nested.resolve(originalModel, stack, world, entity, seed);
      if (overriden != originalModel) return overriden;
      Fluid fluid = CopperCanItem.getFluid(stack.getTag());
      if (fluid != Fluids.EMPTY) {
        FluidStack fluidStack = new FluidStack(fluid, FluidValues.INGOT, CopperCanItem.getFluidTag(stack.getTag()));
        return cache.computeIfAbsent(fluidStack, this::getUncahcedModel);
      }
      return originalModel;
    }
  }
}
