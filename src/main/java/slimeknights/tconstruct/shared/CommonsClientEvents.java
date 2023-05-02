package slimeknights.tconstruct.shared;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.block.Block;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.library.client.book.TinkerBook;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.utils.DomainDisplayName;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock.GlassColor;
import slimeknights.tconstruct.shared.client.FluidParticle;

import java.util.function.Consumer;

public class CommonsClientEvents extends ClientEventBase {

  public static void init() {
    addResourceListeners();
    clientSetup();
    registerParticleFactories();
    CommonsClientEvents.registerColorHandlers();
  }

  static void addResourceListeners() {
    ResourceManagerHelper event = ResourceManagerHelper.get(PackType.CLIENT_RESOURCES);
    MaterialRenderInfoLoader.addResourceListener(event);
    DomainDisplayName.addResourceListener(event);
  }

  static void clientSetup() {
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerCommons.glow.get(), RenderType.translucent());

    // glass
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerCommons.clearGlass.get(), RenderType.cutout());
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerCommons.clearGlassPane.get(), RenderType.cutout());
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerCommons.clearTintedGlass.get(), RenderType.translucent());
    for (ClearStainedGlassBlock.GlassColor color : ClearStainedGlassBlock.GlassColor.values()) {
      BlockRenderLayerMap.INSTANCE.putBlock(TinkerCommons.clearStainedGlass.get(color), RenderType.translucent());
      BlockRenderLayerMap.INSTANCE.putBlock(TinkerCommons.clearStainedGlassPane.get(color), RenderType.translucent());
    }
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerCommons.soulGlass.get(), RenderType.translucent());
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerCommons.soulGlassPane.get(), RenderType.translucent());
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerMaterials.soulsteel.get(), RenderType.translucent());
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerMaterials.slimesteel.get(), RenderType.translucent());
    RenderType cutout = RenderType.cutout();
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerCommons.goldBars.get(), cutout);
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerCommons.goldPlatform.get(), cutout);
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerCommons.ironPlatform.get(), cutout);
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerCommons.cobaltPlatform.get(), cutout);
    Consumer<Block> setCutout = block -> BlockRenderLayerMap.INSTANCE.putBlock(block, cutout);
    TinkerCommons.copperPlatform.forEach(setCutout);
    TinkerCommons.waxedCopperPlatform.forEach(setCutout);

    Font unicode = unicodeFontRender();
    TinkerBook.MATERIALS_AND_YOU.fontRenderer = unicode;
    TinkerBook.TINKERS_GADGETRY.fontRenderer = unicode;
    TinkerBook.PUNY_SMELTING.fontRenderer = unicode;
    TinkerBook.MIGHTY_SMELTING.fontRenderer = unicode;
    TinkerBook.FANTASTIC_FOUNDRY.fontRenderer = unicode;
    TinkerBook.ENCYCLOPEDIA.fontRenderer = unicode;
  }

  static void registerColorHandlers() {
    // colors apply a constant tint to make models easier
    for (GlassColor color : GlassColor.values()) {
      Block block = TinkerCommons.clearStainedGlass.get(color);
      Block pane = TinkerCommons.clearStainedGlassPane.get(color);
      ColorProviderRegistry.BLOCK.register((state, reader, pos, index) -> color.getColor(), block, pane);
      registerBlockItemColorAlias(block);
      registerBlockItemColorAlias(pane);
    }
  }

  static void registerParticleFactories() {
    ParticleFactoryRegistry.getInstance().register(TinkerCommons.fluidParticle.get(), new FluidParticle.Factory());
  }

  private static Font unicodeRenderer;

  /** Gets the unicode font renderer */
  public static Font unicodeFontRender() {
    if (unicodeRenderer == null)
      unicodeRenderer = new Font(rl -> {
        FontManager resourceManager = Minecraft.getInstance().fontManager;
        return resourceManager.fontSets.get(Minecraft.UNIFORM_FONT);
      }, false);

    return unicodeRenderer;
  }
}
