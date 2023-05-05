package slimeknights.tconstruct.library.client.model.block;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import io.github.fabricators_of_create.porting_lib.model.geometry.IGeometryBakingContext;
import io.github.fabricators_of_create.porting_lib.model.geometry.IGeometryLoader;
import io.github.fabricators_of_create.porting_lib.model.geometry.IUnbakedGeometry;
import io.github.fabricators_of_create.porting_lib.model.data.ModelData;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import lombok.AllArgsConstructor;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.client.model.RetexturedModel;
import slimeknights.mantle.client.model.util.ColoredBlockModel;
import slimeknights.mantle.client.model.util.DynamicBakedWrapper;
import slimeknights.mantle.client.model.util.SimpleBlockModel;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.smeltery.block.entity.tank.IDisplayFluidListener;

import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Model that replaces fluid textures with the fluid from model data
 */
@AllArgsConstructor
public class FluidTextureModel implements IUnbakedGeometry<FluidTextureModel> {
  public static final Loader LOADER = new Loader();

  private final SimpleBlockModel model;
  private final Set<String> fluids;

  @Override
  public Collection<Material> getMaterials(IGeometryBakingContext owner, Function<ResourceLocation,UnbakedModel> modelGetter, Set<Pair<String,String>> missingTextureErrors) {
    return model.getMaterials(owner, modelGetter, missingTextureErrors);
  }

  /** Trims the # character off the beginning of a texture name (if present) */
  private static String trimTextureName(String name) {
    if (name.charAt(0) == '#') {
      return name.substring(1);
    }
    return name;
  }

  @Override
  public BakedModel bake(IGeometryBakingContext owner, ModelBakery bakery, Function<Material,TextureAtlasSprite> spriteGetter, ModelState transform, ItemOverrides overrides, ResourceLocation modelLocation) {
    BakedModel baked = model.bakeModel(owner, transform, overrides, spriteGetter, modelLocation);
    Set<String> fluidTextures = RetexturedModel.getAllRetextured(owner, model, this.fluids);

    // determine which block parts are fluids
    List<BlockElement> elements = model.getElements();
    int size = elements.size();
    BitSet fluidParts = new BitSet(size);
    for (int i = 0; i < size; i++) {
      BlockElement part = elements.get(i);
      long fluidFaces = part.faces.values().stream()
                                     .filter(face -> fluidTextures.contains(trimTextureName(face.texture)))
                                     .count();
      // for simplicity, each part is either a fluid or not. If for some reason it contains both we mark it as a fluid, meaning it may get colored
      // if this is undesired, just use separate elements
      if (fluidFaces > 0) {
        if (fluidFaces < part.faces.size()) {
          TConstruct.LOG.warn("Mixed fluid and non-fluid elements in model {}, may cause unexpected results", modelLocation);
        }
        fluidParts.set(i);
      }
    }
    return new Baked(baked, elements, owner, transform, fluidTextures, fluidParts);
  }

  /** Baked wrapper class */
  @SuppressWarnings("removal")
  private static class Baked extends DynamicBakedWrapper<BakedModel> {
    private final Map<FluidStack,BakedModel> cache = new ConcurrentHashMap<>();
    private final List<BlockElement> elements;
    private final IGeometryBakingContext owner;
    private final ModelState transform;
    private final Set<String> fluids;
    private final BitSet fluidParts;
    protected Baked(BakedModel originalModel, List<BlockElement> elements, IGeometryBakingContext owner, ModelState transform, Set<String> fluids, BitSet fluidParts) {
      super(originalModel);
      this.elements = elements;
      this.owner = owner;
      this.transform = transform;
      this.fluids = fluids;
      this.fluidParts = fluidParts;
    }

    /** Retextures a model for the given fluid */
    private BakedModel getRetexturedModel(FluidStack fluid) {
      // setup model baking
      Function<Material,TextureAtlasSprite> spriteGetter = Material::sprite;
      TextureAtlasSprite particle = spriteGetter.apply(owner.getMaterial("particle"));
      SimpleBakedModel.Builder builder = new SimpleBakedModel.Builder(owner.useAmbientOcclusion(), owner.isGui3d(), owner.useBlockLight(), owner.getTransforms(), ItemOverrides.EMPTY).particle(particle);

      // get fluid details
      int color = FluidVariantRendering.getColor(fluid.getType());
      int luminosity = FluidVariantAttributes.getLuminance(fluid.getType());
      IGeometryBakingContext textured = new RetexturedModel.RetexturedConfiguration(this.owner, this.fluids, FluidVariantRendering.getSprite(fluid.getType()).getName());

      // add in elements
      int size = elements.size();
      for (int i = 0; i < size; i++) {
        BlockElement element = elements.get(i);
        if (fluidParts.get(i)) {
          ColoredBlockModel.bakePart(builder, textured, element, color, luminosity, transform, spriteGetter, TankModel.BAKE_LOCATION);
        } else {
          SimpleBlockModel.bakePart(builder, owner, element, transform, spriteGetter, TankModel.BAKE_LOCATION);
        }
      }
      return builder.build();
    }

    /** Gets a retextured model for the given fluid, using the cached model if possible */
    private BakedModel getCachedModel(FluidStack fluid) {
      return this.cache.computeIfAbsent(fluid, this::getRetexturedModel);
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
      if(blockView instanceof RenderAttachedBlockView renderAttachedBlockView && renderAttachedBlockView.getBlockEntityRenderAttachment(pos) instanceof ModelData data) {
        FluidStack fluid = data.get(IDisplayFluidListener.PROPERTY);
        if (fluid != null && !fluid.isEmpty()) {
          ((FabricBakedModel)getCachedModel(fluid)).emitBlockQuads(blockView, state, pos, randomSupplier, context);
          return;
        }
      }
      super.emitBlockQuads(blockView, state, pos, randomSupplier, context);
    }
  }

  /** Model loader class */
  private static class Loader implements IGeometryLoader<FluidTextureModel> {

    @Override
    public FluidTextureModel read(JsonObject json, JsonDeserializationContext context) {
      SimpleBlockModel model = SimpleBlockModel.deserialize(context, json);
      Set<String> fluids = ImmutableSet.copyOf(JsonHelper.parseList(json, "fluids", GsonHelper::convertToString));
      return new FluidTextureModel(model, fluids);
    }
  }
}
