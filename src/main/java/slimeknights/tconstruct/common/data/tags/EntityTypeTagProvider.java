package slimeknights.tconstruct.common.data.tags;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.Registry;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.entity.EntityType;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.world.TinkerWorld;

@SuppressWarnings("unchecked")
public class EntityTypeTagProvider extends TagsProvider<EntityType<?>> {

  public EntityTypeTagProvider(FabricDataGenerator generatorIn) {
    super(generatorIn, Registry.ENTITY_TYPE);
  }

  @Override
  public void addTags() {
    this.tag(TinkerTags.EntityTypes.SLIMES)
        .add(EntityType.SLIME, TinkerWorld.earthSlimeEntity.get(), TinkerWorld.skySlimeEntity.get(), TinkerWorld.enderSlimeEntity.get(), TinkerWorld.terracubeEntity.get());
    this.tag(TinkerTags.EntityTypes.BACON_PRODUCER).add(EntityType.PIG, EntityType.PIGLIN, EntityType.HOGLIN);

    this.tag(TinkerTags.EntityTypes.MELTING_SHOW).add(EntityType.IRON_GOLEM, EntityType.SNOW_GOLEM, EntityType.VILLAGER, EntityType.PLAYER);
    this.tag(TinkerTags.EntityTypes.MELTING_HIDE).add(EntityType.GIANT);
    this.tag(TinkerTags.EntityTypes.PIGGYBACKPACK_BLACKLIST);

    this.tag(TinkerTags.EntityTypes.CREEPERS).add(EntityType.CREEPER);
    this.tag(TinkerTags.EntityTypes.RARE_MOBS).add(EntityType.WITHER, EntityType.ENDER_DRAGON, EntityType.ELDER_GUARDIAN, EntityType.EVOKER, EntityType.PLAYER);
    this.tag(TinkerTags.EntityTypes.VILLAGERS).add(EntityType.VILLAGER, EntityType.WANDERING_TRADER, EntityType.ZOMBIE_VILLAGER);
    this.tag(TinkerTags.EntityTypes.ILLAGERS).add(EntityType.EVOKER, EntityType.ILLUSIONER, EntityType.PILLAGER, EntityType.VINDICATOR, EntityType.WITCH);
    this.tag(TinkerTags.EntityTypes.KILLAGERS).addTag(TinkerTags.EntityTypes.VILLAGERS).addTag(TinkerTags.EntityTypes.ILLAGERS).add(EntityType.IRON_GOLEM, EntityType.RAVAGER);

    this.tag(TinkerTags.EntityTypes.SMALL_ARMOR).addTag(TinkerTags.EntityTypes.SLIMES);
  }

  @Override
  public String getName() {
    return "Tinkers Construct Entity Type TinkerTags";
  }

}
