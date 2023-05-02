package slimeknights.tconstruct.smeltery.block.entity.tank;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import slimeknights.mantle.transfer.fluid.FluidTank;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.minecraft.nbt.CompoundTag;
import slimeknights.tconstruct.smeltery.block.entity.ChannelBlockEntity;

/** Tank for channel contents */
public class ChannelTank extends FluidTank {
	private static final String TAG_LOCKED = "locked";

	/**
	 * Amount of fluid that may not be extracted this tick
	 * Essentially, since we cannot guarantee tick order, this prevents us from having a net 0 fluid for the renderer
	 * if draining and filling at the same time
	 */
	private int locked;

	/** Tank owner */
	private final ChannelBlockEntity parent;

	public ChannelTank(int capacity, ChannelBlockEntity parent) {
		super(capacity, fluid -> !FluidVariantAttributes.isLighterThanAir(fluid.getType()));
		this.parent = parent;
	}

	/**
	 * Called on channel update to clear the lock, allowing this fluid to be drained
	 */
	public void freeFluid() {
		this.locked = 0;
	}

	/**
	 * Returns the maximum fluid that can be extracted from this tank
	 * @return  Max fluid that can be pulled
	 */
	public long getMaxUsable() {
		return Math.max(fluid.getAmount() - locked, 0);
	}

	@Override
	public long fill(FluidStack resource, boolean sim) {
		boolean wasEmpty = isEmpty();
		long amount = super.fill(resource, sim);
		if(!sim) {
			locked += amount;
			// if we added something, sync to client
			if (wasEmpty && !isEmpty()) {
				parent.sendFluidUpdate();
			}
		}
		return amount;
	}

	@Override
	public FluidStack drain(long maxDrain, boolean sim) {
		boolean wasEmpty = isEmpty();
		FluidStack stack = super.drain(maxDrain, sim);
		// if we removed something, sync to client
		if (!sim && !wasEmpty && isEmpty()) {
			parent.sendFluidUpdate();
		}
		return stack;
	}

	@Override
	public FluidTank readFromNBT(CompoundTag nbt) {
		this.locked = nbt.getInt(TAG_LOCKED);
		super.readFromNBT(nbt);
		return this;
	}

	@Override
	public CompoundTag writeToNBT(CompoundTag nbt) {
		nbt = super.writeToNBT(nbt);
		nbt.putInt(TAG_LOCKED, locked);
		return nbt;
	}
}
