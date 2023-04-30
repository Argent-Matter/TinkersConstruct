package slimeknights.tconstruct.library.modifiers.dynamic;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot.Type;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Objects;

/** Modifier that makes the player less visible to a certain mob type */
@RequiredArgsConstructor
public class MobDisguiseModifier extends NoLevelsModifier {
  /** Loader instance */
  public static final IGenericLoader<MobDisguiseModifier> LOADER = new IGenericLoader<MobDisguiseModifier>() {
    @Override
    public MobDisguiseModifier deserialize(JsonObject json) {
      return new MobDisguiseModifier(JsonHelper.getAsEntry(Registry.ENTITY_TYPE, json, "entity"));
    }

    @Override
    public void serialize(MobDisguiseModifier object, JsonObject json) {
      json.addProperty("entity", Objects.requireNonNull(Registry.ENTITY_TYPE.getKey(object.type)).toString());
    }

    @Override
    public MobDisguiseModifier fromNetwork(FriendlyByteBuf buffer) {
      return new MobDisguiseModifier(Registry.ENTITY_TYPE.get(buffer.readResourceLocation()));
    }

    @Override
    public void toNetwork(MobDisguiseModifier object, FriendlyByteBuf buffer) {
      buffer.writeResourceLocation(Registry.ENTITY_TYPE.getKey(object.type));
    }
  };
  public static final TinkerDataKey<Multiset<EntityType<?>>> DISGUISES = TConstruct.createKey("mob_disguise");

  private final EntityType<?> type;

  @Override
  public IGenericLoader<? extends Modifier> getLoader() {
    return LOADER;
  }

  @Override
  public void onEquip(IToolStackView tool, int level, EquipmentChangeContext context) {
    if (context.getChangedSlot().getType() == Type.ARMOR) {
      context.getTinkerData().ifPresent(data -> {
        Multiset<EntityType<?>> disguises = data.get(DISGUISES);
        if (disguises == null) {
          disguises = HashMultiset.create();
          data.put(DISGUISES, disguises);
        }
        disguises.add(type);
      });
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, int level, EquipmentChangeContext context) {
    if (context.getChangedSlot().getType() == Type.ARMOR) {
      context.getTinkerData().ifPresent(data -> {
        Multiset<EntityType<?>> disguises = data.get(DISGUISES);
        if (disguises != null) {
          disguises.remove(type);
        }
      });
    }
  }
}
