package slimeknights.tconstruct.tables.block.entity.inventory;

import io.github.fabricators_of_create.porting_lib.extensions.INBTSerializable;
import slimeknights.mantle.transfer.item.IItemHandlerModifiable;
import net.minecraft.nbt.CompoundTag;
import slimeknights.mantle.block.entity.MantleBlockEntity;

/** Interface for tinker chest TEs */
public interface IChestItemHandler extends IItemHandlerModifiable, INBTSerializable<CompoundTag>, IScalingContainer {
  /** Sets the parent of this block */
  void setParent(MantleBlockEntity parent);
}
