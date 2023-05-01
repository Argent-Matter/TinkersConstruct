package slimeknights.tconstruct.common.data.loot;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.slf4j.Logger;
import slimeknights.tconstruct.TConstruct;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TConstructLootTableProvider extends LootTableProvider {

  private static final Logger LOGGER = LogUtils.getLogger();
  private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

  private LootTableProvider x;
  private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> lootTables = ImmutableList.of(Pair.of(BlockLootTableProvider::new, LootContextParamSets.BLOCK), Pair.of(AdvancementLootTableProvider::new, LootContextParamSets.ADVANCEMENT_REWARD), Pair.of(EntityLootTableProvider::new, LootContextParamSets.ENTITY));

  private final FabricDataGenerator gen;

  public TConstructLootTableProvider(FabricDataGenerator gen) {
    super(gen);
    this.gen = gen;
  }

//  @Override
  protected void validate(Map<ResourceLocation,LootTable> map, ValidationContext validationtracker) {
    map.forEach((loc, table) -> LootTables.validate(validationtracker, loc, table));
    // Remove vanilla's tables, which we also loaded so we can redirect stuff to them.
    // This ensures the remaining generator logic doesn't write those to files.
    map.keySet().removeIf((loc) -> !loc.getNamespace().equals(TConstruct.MOD_ID));
  }

  @Override
  public void run(CachedOutput pCache) {
    Path path = this.gen.getOutputFolder();
    Map<ResourceLocation, LootTable> map = Maps.newHashMap();
    this.lootTables.forEach((p_124458_) -> {
      p_124458_.getFirst().get().accept((p_176077_, p_176078_) -> {
        if (map.put(p_176077_, p_176078_.setParamSet(p_124458_.getSecond()).build()) != null) {
          throw new IllegalStateException("Duplicate loot table " + p_176077_);
        }
      });
    });
    ValidationContext validationcontext = new ValidationContext(LootContextParamSets.ALL_PARAMS, (p_124465_) -> {
      return null;
    }, map::get);

    validate(map, validationcontext);

    Multimap<String, String> multimap = validationcontext.getProblems();
    if (!multimap.isEmpty()) {
      multimap.forEach((p_124446_, p_124447_) -> {
        LOGGER.warn("Found validation problem in {}: {}", p_124446_, p_124447_);
      });
      throw new IllegalStateException("Failed to validate loot tables, see logs");
    } else {
      map.forEach((p_124451_, p_124452_) -> {
        Path path1 = createPath(path, p_124451_);

        try {
          DataProvider.saveStable(pCache, LootTables.serialize(p_124452_), path1);
        } catch (IOException ioexception) {
          LOGGER.error("Couldn't save loot table {}", path1, ioexception);
        }

      });
    }
  }

  private static Path createPath(Path path, ResourceLocation resourceLocation) {
    String var10001 = resourceLocation.getNamespace();
    return path.resolve("data/" + var10001 + "/loot_tables/" + resourceLocation.getPath() + ".json");
  }

  /**
   * Gets a name for this provider, to use in logging.
   */
  @Override
  public String getName() {
    return "TConstruct LootTables";
  }
}
