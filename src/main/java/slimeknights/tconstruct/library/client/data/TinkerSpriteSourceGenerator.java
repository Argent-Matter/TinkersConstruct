package slimeknights.tconstruct.library.client.data;

import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper;
import io.github.fabricators_of_create.porting_lib.data.SpriteSourceProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.modifiers.ModifierIconManager;
import slimeknights.tconstruct.tables.client.PatternGuiTextureLoader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public class TinkerSpriteSourceGenerator extends SpriteSourceProvider {
  private final ExistingFileHelper helper;

  public TinkerSpriteSourceGenerator(FabricDataGenerator output, ExistingFileHelper helper) {
    super(output, TConstruct.MOD_ID);
    this.helper = helper;
  }

  @Override
  protected void addSources() {
    ResourceManager resourceManager;
    try {
      Method method = helper.getClass().getDeclaredMethod("getManager", PackType.class);
      method.setAccessible(true);
      resourceManager = (ResourceManager) method.invoke(helper, PackType.CLIENT_RESOURCES);
      method.setAccessible(false);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }


    var sourceList = atlas(BLOCKS_ATLAS);
    ModifierIconManager.INSTANCE.onReloadSafe(resourceManager);
    ModifierIconManager.modifierIcons.values().forEach(list -> list.forEach(resourceLocation -> {
      sourceList.addSource(new SingleFile(new ResourceLocation(resourceLocation.toString().replace(".png", "").replace("textures/", "")), Optional.empty()));
    }));
    sourceList.addSource(new SingleFile(ModifierIconManager.DEFAULT_COVER, Optional.empty()));
    sourceList.addSource(new SingleFile(ModifierIconManager.DEFAULT_PAGES, Optional.empty()));
    PatternGuiTextureLoader.INSTANCE.onTextureStitch(resourceLocation -> sourceList.addSource(new SingleFile(new ResourceLocation(resourceLocation.toString().replace(".png", "").replace("textures/", "")), Optional.empty())), resourceManager);
  }
}
