package slimeknights.tconstruct.smeltery.block.entity.tank;

import io.github.fabricators_of_create.porting_lib.model.data.ModelProperty;
import io.github.fabricators_of_create.porting_lib.util.FluidUnit;
import net.minecraft.core.BlockPos;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;

/**
 * Interface for blocks to be notified when the smeltery has a new bottommost fluid
 */
@SuppressWarnings("removal")
public interface IDisplayFluidListener {
  /** Property for fluid models */
  ModelProperty<FluidStack> PROPERTY = new ModelProperty<>();

  /**
   * Called when the display fluid changes0
   * @param fluid New display fluid, is safe to store (will not be modified)
   */
  void notifyDisplayFluidUpdated(FluidStack fluid);

  /**
   * Gets the position of the listener
   * @return  Position of listener
   */
  BlockPos getListenerPos();

  /** Makes the fluid contain 1000mb, or {@link FluidStack#EMPTY} if empty */
  static FluidStack normalizeFluid(FluidStack fluid) {
    if (fluid.isEmpty()) {
      return FluidStack.EMPTY;
    }
    fluid = fluid.copy();
    fluid.setAmount(FluidUnit.DROPLETS.getOneBucketAmount());
    return fluid;
  }
}
