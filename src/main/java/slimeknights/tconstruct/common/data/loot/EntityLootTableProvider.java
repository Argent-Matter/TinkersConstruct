package slimeknights.tconstruct.common.data.loot;

import io.github.fabricators_of_create.porting_lib.data.ModdedEntityLootSubProvider;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.stream.Stream;

public class EntityLootTableProvider extends ModdedEntityLootSubProvider {

  public EntityLootTableProvider() {
    super(FeatureFlags.REGISTRY.allFlags());
  }

  @Override
  protected Stream<EntityType<?>> getKnownEntityTypes() {
    return Registry.ENTITY_TYPE.stream()
      // remove earth slime entity, we redirect to the vanilla loot table
      .filter((entity) -> TConstruct.MOD_ID.equals(Registry.ENTITY_TYPE.getKey(entity).getNamespace())
        && entity != TinkerWorld.earthSlimeEntity.get());
  }

  @Override
  public void generate() {
    this.add(TinkerWorld.skySlimeEntity.get(), dropSlimeballs(SlimeType.SKY));
    this.add(TinkerWorld.enderSlimeEntity.get(), dropSlimeballs(SlimeType.ENDER));
    this.add(TinkerWorld.terracubeEntity.get(),
      LootTable.lootTable().withPool(LootPool.lootPool()
        .setRolls(ConstantValue.exactly(1))
        .add(LootItem.lootTableItem(Items.CLAY_BALL)
          .apply(SetItemCountFunction.setCount(UniformGenerator.between(-2.0F, 1.0F)))
          .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))
          .apply(SmeltItemFunction.smelted().when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, ENTITY_ON_FIRE))))));
  }

  private static LootTable.Builder dropSlimeballs(SlimeType type) {
    return LootTable.lootTable()
      .withPool(LootPool.lootPool()
        .setRolls(ConstantValue.exactly(1))
        .add(LootItem.lootTableItem(TinkerCommons.slimeball.get(type))
          .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
          .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))));
  }
}
