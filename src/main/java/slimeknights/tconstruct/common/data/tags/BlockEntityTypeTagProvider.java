package slimeknights.tconstruct.common.data.tags;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.Registry;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.entity.BlockEntityType;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tables.TinkerTables;

public class BlockEntityTypeTagProvider extends FabricTagProvider<BlockEntityType<?>> {
  public BlockEntityTypeTagProvider(FabricDataGenerator generatorIn) {
    super(generatorIn, Registry.BLOCK_ENTITY_TYPE);
  }

  @Override
  protected void generateTags() {
    this.tag(TinkerTags.TileEntityTypes.CRAFTING_STATION_BLACKLIST)
        .add(TinkerTables.craftingStationTile.get(), TinkerTables.tinkerStationTile.get(), TinkerTables.partBuilderTile.get(),
						 TinkerTables.partChestTile.get(), TinkerTables.tinkersChestTile.get(), TinkerTables.castChestTile.get(),
						 TinkerSmeltery.basin.get(), TinkerSmeltery.table.get(), TinkerSmeltery.smeltery.get());

  }

}
