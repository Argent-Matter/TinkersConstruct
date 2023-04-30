package slimeknights.tconstruct.tools.item;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;

import java.util.Locale;

/** Enum to aid in armor registraton */
@RequiredArgsConstructor
@Getter
public enum ArmorSlotType implements StringRepresentable {
  BOOTS(EquipmentSlot.FEET),
  LEGGINGS(EquipmentSlot.LEGS),
  CHESTPLATE(EquipmentSlot.CHEST),
  HELMET(EquipmentSlot.HEAD);

  private final EquipmentSlot armorType;
  private final String serializedName = toString().toLowerCase(Locale.ROOT);
  private final int index = ordinal();

  /** Gets an equipment slot for the given armor slot */
  public static ArmorSlotType fromType(EquipmentSlot slotType) {
    return switch (slotType) {
      case FEET -> BOOTS;
      case LEGS -> LEGGINGS;
      case CHEST -> CHESTPLATE;
      case HEAD -> HELMET;
      default -> null;
    };
  }

  public static EquipmentSlot equiptmentSlotToType(EquipmentSlot slot) {
    return switch (slot) {
      case FEET -> EquipmentSlot.FEET;
      case LEGS -> EquipmentSlot.LEGS;
      case CHEST -> EquipmentSlot.CHEST;
      case HEAD -> EquipmentSlot.HEAD;
      default -> null;
    };
  }
}
