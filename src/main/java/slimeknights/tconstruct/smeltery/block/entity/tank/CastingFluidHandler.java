package slimeknights.tconstruct.smeltery.block.entity.tank;

import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import slimeknights.mantle.transfer.fluid.IFluidHandler;
import slimeknights.tconstruct.smeltery.block.entity.CastingBlockEntity;

import javax.annotation.Nonnull;
import java.util.Objects;

@RequiredArgsConstructor
public class CastingFluidHandler implements IFluidHandler {
  private final CastingBlockEntity tile;
  @Getter @Setter
  private FluidStack fluid = FluidStack.EMPTY;
  @Setter
  private long capacity = 0;
  private Fluid filter = Fluids.EMPTY;

  /** Checks if the given fluid is valid */
  public boolean isFluidValid(FluidStack stack) {
    return !stack.isEmpty() && (filter == Fluids.EMPTY || stack.getFluid() == filter);
  }

  /** Checks if the fluid is empty */
  public boolean isEmpty() {
    return fluid.isEmpty();
  }

  /** Gets the current capacity of this fluid handler */
  public long getCapacity() {
    if (capacity == 0) {
      return fluid.getAmount();
    }
    return capacity;
  }

  /** Resets the tanks filter */
  public void reset() {
    capacity = 0;
    fluid = FluidStack.EMPTY;
    filter = Fluids.EMPTY;
  }

  @Override
  public long fill(FluidStack resource, boolean sim) {
    if (resource.isEmpty() || !isFluidValid(resource)) {
      return 0;
    }

    // update filter and capacity
    long capacity = this.capacity;
    if (filter == null || this.capacity == 0) {
      Fluid fluid = resource.getFluid();
      capacity = tile.initNewCasting(resource, sim);
      if (capacity <= 0) {
        return 0;
      }
      if (!sim) {
        this.capacity = capacity;
        this.filter = fluid;
      }
    }

    // if no fluid yet, copy it in
    if (fluid.isEmpty()) {
      long amount = Math.min(capacity, resource.getAmount());
      if (!sim) {
        fluid = new FluidStack(resource, amount);
        tile.onContentsChanged();
      }
      return amount;
    }

    // safety: should never be false, but good to check
    if (!resource.isFluidEqual(fluid)) {
      return 0;
    }

    // if full, nothing to do
    long space = capacity - fluid.getAmount();
    if (space <= 0) {
      return 0;
    }
    // if it fits, it grows
    long amount = resource.getAmount();
    if (amount < space) {
      if (!sim) {
        fluid.grow(amount);
        tile.onContentsChanged();
      }
      return amount;
    } else {
      // too much? set to max
      if (!sim) {
        fluid.setAmount(capacity);
        tile.onContentsChanged();
      }
      return space;
    }
  }

  @Nonnull
  @Override
  public FluidStack drain(FluidStack resource, boolean sim) {
    if (resource.isEmpty() || !resource.isFluidEqual(fluid)) {
      return FluidStack.EMPTY;
    }
    return this.drain(resource.getAmount(), sim);
  }

  @Nonnull
  @Override
  public FluidStack drain(long maxDrain, boolean sim) {
    long drained = Math.min(fluid.getAmount(), maxDrain);
    if (drained <= 0) {
      return FluidStack.EMPTY;
    }

    FluidStack stack = new FluidStack(fluid, drained);
    if (!sim) {
      fluid.shrink(drained);
      if (fluid.isEmpty()) {
        // since empty, assume the current recipe is invalid now
        // fixes some odd behavior with capacity and recipes going out of sync
        tile.reset();
      } else {
        // called in reset
        tile.onContentsChanged();
      }
    }
    return stack;
  }

  /* Required */

  @Nonnull
  @Override
  public FluidStack getFluidInTank(int tank) {
    if (tank == 0) {
      return fluid;
    }
    return FluidStack.EMPTY;
  }

  @Override
  public int getTanks() {
    return 1;
  }

  @Override
  public long getTankCapacity(int tank) {
    return getCapacity();
  }

  @Override
  public boolean isFluidValid(int tank, FluidStack stack) {
    return tank == 0 && isFluidValid(stack);
  }

  /* Tag */
  private static final String TAG_FLUID = "fluid";
  private static final String TAG_FILTER = "filter";
  private static final String TAG_CAPACITY = "capacity";

  /** Reads the tank from Tag */
  public void readFromTag(CompoundTag nbt) {
    capacity = nbt.getLong(TAG_CAPACITY);
    if (nbt.contains(TAG_FLUID, Tag.TAG_COMPOUND)) {
      setFluid(FluidStack.loadFluidStackFromNBT(nbt.getCompound(TAG_FLUID)));
    }
    if (nbt.contains(TAG_FILTER, Tag.TAG_STRING)) {
      Fluid fluid = Registry.FLUID.get(new ResourceLocation(nbt.getString(TAG_FILTER)));
      if (fluid != null) {
        filter = fluid;
      }
    }
  }

  /** Write the tank from NBT */
  public CompoundTag writeToTag(CompoundTag nbt) {
    nbt.putLong(TAG_CAPACITY, capacity);
    if (!fluid.isEmpty()) {
      nbt.put(TAG_FLUID, fluid.writeToNBT(new CompoundTag()));
    }
    if (filter != Fluids.EMPTY) {
      nbt.putString(TAG_FILTER, Objects.requireNonNull(Registry.FLUID.getKey(filter)).toString());
    }
    return nbt;
  }
}
