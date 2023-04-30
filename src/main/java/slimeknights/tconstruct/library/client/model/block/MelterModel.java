package slimeknights.tconstruct.library.client.model.block;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import io.github.fabricators_of_create.porting_lib.model.BlockGeometryBakingContext;
import io.github.fabricators_of_create.porting_lib.model.IGeometryBakingContext;
import io.github.fabricators_of_create.porting_lib.model.IGeometryLoader;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.model.inventory.ModelItem;
import slimeknights.mantle.client.model.util.SimpleBlockModel;

import java.util.List;
import java.util.function.Function;

/**
 * This model contains a list of items to display in the TESR, plus a single scalable fluid that can either be statically rendered or rendered in the TESR
 */
public class MelterModel extends TankModel {
  /** Shared loader instance */
  public static final Loader LOADER = new Loader();

  private final List<ModelItem> items;
  @SuppressWarnings("WeakerAccess")
  protected MelterModel(SimpleBlockModel model, @Nullable SimpleBlockModel gui, IncrementalFluidCuboid fluid, List<ModelItem> items) {
    super(model, gui, fluid, false);
    this.items = items;
  }

  @Override
  public BakedModel bake(IGeometryBakingContext owner, ModelBakery baker, Function<Material,TextureAtlasSprite> spriteGetter, ModelState transform, ItemOverrides overrides, ResourceLocation location) {
    BlockModel blockModel = ((BlockGeometryBakingContext)owner).owner;
    BakedModel baked = model.bakeModel(blockModel, transform, overrides, spriteGetter, location);
    // bake the GUI model if present
    BakedModel bakedGui = baked;
    if (gui != null) {
      bakedGui = gui.bakeModel(blockModel, transform, overrides, spriteGetter, location);
    }
    return new Baked(blockModel, transform, baked, bakedGui, this);
  }

  /** Baked variant to allow access to items */
  public static final class Baked extends TankModel.Baked<MelterModel> {
    private Baked(BlockModel owner, ModelState transforms, BakedModel baked, BakedModel gui, MelterModel original) {
      super(owner, transforms, baked, gui, original);
    }

    /**
     * Gets a list of items used in inventory display
     * @return  Item list
     */
    public List<ModelItem> getItems() {
      return this.original.items;
    }
  }

  /** Loader for this model */
  public static class Loader implements IGeometryLoader<TankModel> {
    @Override
    public TankModel read(JsonObject modelContents, JsonDeserializationContext deserializationContext) {
      SimpleBlockModel model = SimpleBlockModel.deserialize(deserializationContext, modelContents);
      SimpleBlockModel gui = null;
      if (modelContents.has("gui")) {
        gui = SimpleBlockModel.deserialize(deserializationContext, GsonHelper.getAsJsonObject(modelContents, "gui"));
      }
      IncrementalFluidCuboid fluid = IncrementalFluidCuboid.fromJson(GsonHelper.getAsJsonObject(modelContents, "fluid"));
      List<ModelItem> items = ModelItem.listFromJson(modelContents, "items");
      return new MelterModel(model, gui, fluid, items);
    }
  }
}
