package slimeknights.tconstruct.library.data.tinkering;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.data.CachedOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayout;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayoutLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/** Base data generator to generate station slot layouts */
public abstract class AbstractStationSlotLayoutProvider extends GenericDataProvider {

  /** Sort index for weapons */
  protected static final int SORT_WEAPON = 3;
  /** Sort index for harvest */
  protected static final int SORT_HARVEST = 6;
  /** Sort index for ranged */
  protected static final int SORT_RANGED = 8;
  /** Index for large tools, add to either weapon or harvest */
  protected static final int SORT_LARGE = 6;

  private final Map<ResourceLocation,StationSlotLayout.Builder> allLayouts = new HashMap<>();

  public AbstractStationSlotLayoutProvider(FabricDataGenerator output) {
    super(output, PackType.SERVER_DATA, StationSlotLayoutLoader.FOLDER, StationSlotLayoutLoader.GSON);
  }

  /**
   * Function to add all relevant layouts
   */
  protected abstract void addLayouts();

  /** Defines the given ID as a general layout */
  protected StationSlotLayout.Builder define(ResourceLocation id) {
    return allLayouts.computeIfAbsent(id, i -> StationSlotLayout.builder());
  }

  /** Defines the given ID as a item layout */
  protected StationSlotLayout.Builder define(ItemLike item) {
    return define(Registry.ITEM.getKey(item.asItem()));
  }

  /** Defines the given ID as a tool layout, sets icon and name */
  protected StationSlotLayout.Builder defineModifiable(IModifiableDisplay item) {
    return define(Registry.ITEM.getKey(item.asItem()))
      .translationKey(item.asItem().getDescriptionId())
      .icon(item.getRenderTool());
  }

  /** Defines the given ID as a tool layout, sets icon and name */
  protected StationSlotLayout.Builder defineModifiable(Supplier<? extends IModifiableDisplay> item) {
    return defineModifiable(item.get());
  }

  @Override
  public CompletableFuture<?> run(CachedOutput cache) {
    addLayouts();
    List<CompletableFuture<?>> futures = new ArrayList<>();
    allLayouts.forEach((id, builder) -> futures.add(saveThing(cache, id, builder.build())));
    return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
  }
}
