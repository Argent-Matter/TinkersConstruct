package slimeknights.tconstruct.tables;

import io.github.fabricators_of_create.porting_lib.event.client.RegisterGeometryLoadersCallback;
import io.github.fabricators_of_create.porting_lib.model.IGeometryLoader;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.level.block.entity.BlockEntity;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.ClientEventBase;
import slimeknights.tconstruct.library.client.model.block.TableModel;
import slimeknights.tconstruct.shared.block.entity.TableBlockEntity;
import slimeknights.tconstruct.tables.block.entity.chest.TinkersChestBlockEntity;
import slimeknights.tconstruct.tables.client.TableTileEntityRenderer;
import slimeknights.tconstruct.tables.client.inventory.CraftingStationScreen;
import slimeknights.tconstruct.tables.client.inventory.ModifierWorktableScreen;
import slimeknights.tconstruct.tables.client.inventory.PartBuilderScreen;
import slimeknights.tconstruct.tables.client.inventory.TinkerChestScreen;
import slimeknights.tconstruct.tables.client.inventory.TinkerStationScreen;

import java.util.Map;

@SuppressWarnings("unused")
public class TableClientEvents extends ClientEventBase {

  public static void init() {
    RegisterGeometryLoadersCallback.EVENT.register(TableClientEvents::registerModelLoader);
    registerRenderers();
    setupClient();
    TableClientEvents.registerBlockColors();
    TableClientEvents.registerItemColors();
  }

  static void registerModelLoader(Map<ResourceLocation, IGeometryLoader<?>> callback) {
    callback.put(TConstruct.getResource("table"), TableModel.LOADER);
  }

  static void registerRenderers() {
    BlockEntityRendererProvider<TableBlockEntity> tableRenderer = TableTileEntityRenderer::new;
    BlockEntityRenderers.register(TinkerTables.craftingStationTile.get(), tableRenderer);
    BlockEntityRenderers.register(TinkerTables.tinkerStationTile.get(), tableRenderer);
    BlockEntityRenderers.register(TinkerTables.modifierWorktableTile.get(), tableRenderer);
    BlockEntityRenderers.register(TinkerTables.partBuilderTile.get(), tableRenderer);
  }

  static void setupClient() {
    MenuScreens.register(TinkerTables.craftingStationContainer.get(), CraftingStationScreen::new);
    MenuScreens.register(TinkerTables.tinkerStationContainer.get(), TinkerStationScreen::new);
    MenuScreens.register(TinkerTables.partBuilderContainer.get(), PartBuilderScreen::new);
    MenuScreens.register(TinkerTables.modifierWorktableContainer.get(), ModifierWorktableScreen::new);
    MenuScreens.register(TinkerTables.tinkerChestContainer.get(), TinkerChestScreen::new);
  }

  static void registerBlockColors() {
    ColorProviderRegistry.BLOCK.register((state, world, pos, index) -> {
      if (world != null && pos != null) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof TinkersChestBlockEntity) {
          return ((TinkersChestBlockEntity)te).getColor();
        }
      }
      return -1;
    }, TinkerTables.tinkersChest.get());
  }

  static void registerItemColors() {
    ColorProviderRegistry.ITEM.register((stack, index) -> ((DyeableLeatherItem)stack.getItem()).getColor(stack), TinkerTables.tinkersChest.asItem());
  }
}
