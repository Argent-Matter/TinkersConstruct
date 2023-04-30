package slimeknights.mantle.client.model;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Transformation;
import io.github.fabricators_of_create.porting_lib.model.BlockGeometryBakingContext;
import io.github.fabricators_of_create.porting_lib.model.IGeometryBakingContext;
import io.github.fabricators_of_create.porting_lib.model.IGeometryLoader;
import io.github.fabricators_of_create.porting_lib.model.IUnbakedGeometry;
import io.github.fabricators_of_create.porting_lib.model.SimpleModelState;
import io.github.fabricators_of_create.porting_lib.model.geometry.UnbakedGeometryHelper;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.client.model.util.BakedItemModel;
import slimeknights.mantle.client.model.util.ModelTextureIteratable;
import slimeknights.mantle.util.JsonHelper;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

@RequiredArgsConstructor
public class NBTKeyModel implements IUnbakedGeometry<NBTKeyModel> {
  /** Model loader instance */
  public static final Loader LOADER = new Loader();

  private static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();

  /** Map of statically registered extra textures, used for addon mods */
  private static final Multimap<ResourceLocation,Pair<String,ResourceLocation>> EXTRA_TEXTURES = HashMultimap.create();

  /**
   * Registers an extra variant texture for the model with the given key. Note that resource packs can override the extra texture
   * @param key          Model key, should be defined in the model JSON if supported
   * @param textureName  Name of the texture defined, corresponds to a possible value of the NBT key
   * @param texture      Texture to use, same format as in resource packs
   */
  public static void registerExtraTexture(ResourceLocation key, String textureName, ResourceLocation texture) {
    EXTRA_TEXTURES.put(key, Pair.of(textureName, texture));
  }

  /** Key to check in item NBT */
  private final String nbtKey;
  /** Key denoting which extra textures to fetch from the map */
  @Nullable
  private final ResourceLocation extraTexturesKey;

  /** Map of textures for the model */
  private Map<String,Material> textures = Collections.emptyMap();

  /** Bakes a model for the given texture */
  private static BakedModel bakeModel(BlockModel owner, Material texture, Function<Material,TextureAtlasSprite> spriteGetter, ItemOverrides overrides) {
    TextureAtlasSprite sprite = spriteGetter.apply(texture);
    List<BakedQuad> quads = UnbakedGeometryHelper.bakeElements(ITEM_MODEL_GENERATOR.processFrames(-1, sprite.getName().toString(), sprite), spriteGetter, new SimpleModelState(Transformation.identity()), sprite.getName());
    return new BakedItemModel(quads, sprite, owner.getTransforms(), overrides, true, owner.getGuiLight().lightLikeBlock());
  }

  @Override
  public BakedModel bake(IGeometryBakingContext owner, ModelBakery bakery, Function<Material,TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
    textures = new HashMap<>();
    // must have a default
    Material defaultTexture = owner.getMaterial("default");
    textures.put("default", defaultTexture);
    // fetch others
    BlockModel model = ((BlockGeometryBakingContext)owner).owner;
    ModelTextureIteratable iterable = new ModelTextureIteratable(null, model);
    for (Map<String,Either<Material,String>> map : iterable) {
      for (String key : map.keySet()) {
        if (!textures.containsKey(key) && owner.hasMaterial(key)) {
          textures.put(key, owner.getMaterial(key));
        }
      }
    }
    // fetch extra textures
    if (extraTexturesKey != null) {
      for (Pair<String,ResourceLocation> extra : EXTRA_TEXTURES.get(extraTexturesKey)) {
        String key = extra.getFirst();
        if (!textures.containsKey(key)) {
          textures.put(key, new Material(TextureAtlas.LOCATION_BLOCKS, extra.getSecond()));
        }
      }
    }
    ImmutableMap.Builder<String, BakedModel> variants = ImmutableMap.builder();
    for (Entry<String,Material> entry : textures.entrySet()) {
      String key = entry.getKey();
      if (!key.equals("default")) {
        variants.put(key, bakeModel(model, entry.getValue(), spriteGetter, ItemOverrides.EMPTY));
      }
    }
    return bakeModel(model, textures.get("default"), spriteGetter, new Overrides(nbtKey, textures, variants.build()));
  }

  @Override
  public Collection<Material> getMaterials(IGeometryBakingContext context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
    return null;
  }

  /** Overrides list for a tool slot item model */
  @RequiredArgsConstructor
  public static class Overrides extends ItemOverrides {
    private final String nbtKey;
    private final Map<String,Material> textures;
    private final Map<String,BakedModel> variants;

    @Override
    public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity livingEntity, int pSeed) {
      CompoundTag nbt = stack.getTag();
      if (nbt != null && nbt.contains(nbtKey)) {
        return variants.getOrDefault(nbt.getString(nbtKey), model);
      }
      return model;
    }

    /** Gets the given texture from the model */
    public Material getTexture(String name) {
      Material texture = textures.get(name);
      return texture != null ? texture : textures.get("default");
    }
  }

  /** Loader logic */
  private static class Loader implements IGeometryLoader<NBTKeyModel> {
    @Override
    public NBTKeyModel read(JsonObject modelContents, JsonDeserializationContext deserializationContext) {
      String key = GsonHelper.getAsString(modelContents, "nbt_key");
      ResourceLocation extraTexturesKey = null;
      if (modelContents.has("extra_textures_key")) {
        extraTexturesKey = JsonHelper.getResourceLocation(modelContents, "extra_textures_key");
      }
      return new NBTKeyModel(key, extraTexturesKey);
    }
  }
}
