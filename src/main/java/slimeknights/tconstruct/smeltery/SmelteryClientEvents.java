package slimeknights.tconstruct.smeltery;

import io.github.fabricators_of_create.porting_lib.event.client.RegisterGeometryLoadersCallback;
import io.github.fabricators_of_create.porting_lib.model.IGeometryLoader;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.client.model.FaucetFluidLoader;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.library.client.model.block.CastingModel;
import slimeknights.tconstruct.library.client.model.block.ChannelModel;
import slimeknights.tconstruct.library.client.model.block.FluidTextureModel;
import slimeknights.tconstruct.library.client.model.block.MelterModel;
import slimeknights.tconstruct.library.client.model.block.TankModel;
import slimeknights.tconstruct.smeltery.client.CopperCanModel;
import slimeknights.tconstruct.smeltery.client.render.CastingBlockEntityRenderer;
import slimeknights.tconstruct.smeltery.client.render.ChannelBlockEntityRenderer;
import slimeknights.tconstruct.smeltery.client.render.FaucetBlockEntityRenderer;
import slimeknights.tconstruct.smeltery.client.render.HeatingStructureBlockEntityRenderer;
import slimeknights.tconstruct.smeltery.client.render.MelterBlockEntityRenderer;
import slimeknights.tconstruct.smeltery.client.render.TankBlockEntityRenderer;
import slimeknights.tconstruct.smeltery.client.screen.AlloyerScreen;
import slimeknights.tconstruct.smeltery.client.screen.HeatingStructureScreen;
import slimeknights.tconstruct.smeltery.client.screen.MelterScreen;
import slimeknights.tconstruct.smeltery.client.screen.SingleItemScreenFactory;

import java.util.Map;

@SuppressWarnings("unused")
public class SmelteryClientEvents extends ClientEventBase {

  public static void init() {
    addResourceListener();
    registerRenderers();
    clientSetup();
    RegisterGeometryLoadersCallback.EVENT.register(SmelteryClientEvents::registerModelLoaders);
  }

  static void addResourceListener() {
    FaucetFluidLoader.initialize();
  }

  static void registerRenderers() {
    BlockEntityRenderers.register(TinkerSmeltery.tank.get(), TankBlockEntityRenderer::new);
    BlockEntityRenderers.register(TinkerSmeltery.faucet.get(), FaucetBlockEntityRenderer::new);
    BlockEntityRenderers.register(TinkerSmeltery.channel.get(), ChannelBlockEntityRenderer::new);
    BlockEntityRenderers.register(TinkerSmeltery.table.get(), CastingBlockEntityRenderer::new);
    BlockEntityRenderers.register(TinkerSmeltery.basin.get(), CastingBlockEntityRenderer::new);
    BlockEntityRenderers.register(TinkerSmeltery.melter.get(), MelterBlockEntityRenderer::new);
    BlockEntityRenderers.register(TinkerSmeltery.alloyer.get(), TankBlockEntityRenderer::new);
    BlockEntityRenderers.register(TinkerSmeltery.smeltery.get(), HeatingStructureBlockEntityRenderer::new);
    BlockEntityRenderers.register(TinkerSmeltery.foundry.get(), HeatingStructureBlockEntityRenderer::new);
  }

  static void clientSetup() {
    // render layers
    RenderType cutout = RenderType.cutout();
    // seared
    // casting
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerSmeltery.searedFaucet.get(), cutout);
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerSmeltery.searedBasin.get(), cutout);
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerSmeltery.searedTable.get(), cutout);
    // controller
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerSmeltery.searedMelter.get(), cutout);
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerSmeltery.smelteryController.get(), cutout);
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerSmeltery.foundryController.get(), cutout);
    // peripherals
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerSmeltery.searedDrain.get(), cutout);
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerSmeltery.searedDuct.get(), cutout);
    TinkerSmeltery.searedTank.forEach(tank -> BlockRenderLayerMap.INSTANCE.putBlock(tank, cutout));
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerSmeltery.searedLantern.get(), cutout);
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerSmeltery.searedGlass.get(), cutout);
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerSmeltery.searedGlassPane.get(), cutout);
    // scorched
    // casting
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerSmeltery.scorchedFaucet.get(), cutout);
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerSmeltery.scorchedBasin.get(), cutout);
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerSmeltery.scorchedTable.get(), cutout);
    // controller
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerSmeltery.scorchedAlloyer.get(), cutout);
    // peripherals
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerSmeltery.scorchedDrain.get(), cutout);
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerSmeltery.scorchedDuct.get(), cutout);
    TinkerSmeltery.scorchedTank.forEach(tank -> BlockRenderLayerMap.INSTANCE.putBlock(tank, cutout));
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerSmeltery.scorchedLantern.get(), cutout);
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerSmeltery.scorchedGlass.get(), cutout);
    BlockRenderLayerMap.INSTANCE.putBlock(TinkerSmeltery.scorchedGlassPane.get(), cutout);

    // screens
    ScreenRegistry.register(TinkerSmeltery.melterContainer.get(), MelterScreen::new);
    ScreenRegistry.register(TinkerSmeltery.smelteryContainer.get(), HeatingStructureScreen::new);
    ScreenRegistry.register(TinkerSmeltery.singleItemContainer.get(), new SingleItemScreenFactory());
    ScreenRegistry.register(TinkerSmeltery.alloyerContainer.get(), AlloyerScreen::new);
  }

  static void registerModelLoaders(Map<ResourceLocation, IGeometryLoader<?>> callback) {
    callback.put(TConstruct.getResource("tank"), TankModel.LOADER);
    callback.put(TConstruct.getResource("casting"), CastingModel.LOADER);
    callback.put(TConstruct.getResource("melter"), MelterModel.LOADER);
    callback.put(TConstruct.getResource("channel"), ChannelModel.LOADER);
    callback.put(TConstruct.getResource("fluid_texture"), FluidTextureModel.LOADER);
    callback.put(TConstruct.getResource("copper_can"), CopperCanModel.LOADER);
  }
}
