package slimeknights.mantle.transfer.item;

import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import slimeknights.mantle.transfer.item.IItemHandlerModifiable;
import slimeknights.mantle.transfer.item.ItemHandlerHelper;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Wraps a Storage in an IItemHandler, for use in Create
 */
@SuppressWarnings("UnstableApiUsage")
public class ItemStorageHandler implements IItemHandlerModifiable {
	protected final Storage<ItemVariant> storage;
	protected long version;
	protected int slots;
	protected ItemStack[] stacks;
	protected Long[] capacities;

	public ItemStorageHandler(Storage<ItemVariant> storage) {
		this.storage = storage;
		this.version = 0;
		updateContents();
	}

	public boolean shouldUpdate() {
		return 1 != version;
	}

	private void updateContents() {
		List<ItemStack> stacks = new ArrayList<>();
		List<Long> capacities = new ArrayList<>();
		try (Transaction t = TransferUtil.getTransaction()) {
      for (Iterator<StorageView<ItemVariant>> it = storage.iterator(); it.hasNext(); ) {
        StorageView<ItemVariant> view = it.next();
        stacks.add(view.getResource().toStack((int) view.getAmount()));
        capacities.add(view.getCapacity());
      }
			t.abort();
		}
		this.stacks = stacks.toArray(ItemStack[]::new);
		this.capacities = capacities.toArray(Long[]::new);
		this.slots = stacks.size();
		this.version = 0;
	}

	private boolean validIndex(int slot) {
		return slot >= 0 && slot < slots;
	}

	@Override
	public int getSlots() {
		if (shouldUpdate())
			updateContents();
		return slots;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		if (validIndex(slot)) {
			if (shouldUpdate())
				updateContents();
			return stacks[slot].copy();
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean sim) {
		if (!validIndex(slot)) // first check valid slot index
			return stack;
		if (stack.isEmpty()) // check stack is not empty
			return stack;
		if (!isItemValid(slot, stack)) // make sure this stack can be stored
			return stack;
		if (!storage.supportsInsertion()) // make sure insertion is supported
			return stack;
		ItemStack current = getStackInSlot(slot);
		int limit = Math.min(getSlotLimit(slot), current.getMaxStackSize());
		if (limit <= 0 || !ItemHandlerHelper.canItemStacksStack(current, stack)) // make sure there's room
			return stack;
		// finally insert
		ItemStack finalVal = ItemStack.EMPTY;
		try (Transaction t = TransferUtil.getTransaction()) {
			// this technically breaks spec and ignores 'slot' but thanks FAPI, we literally have no choice!
			long remainder = stack.getCount() - storage.insert(ItemVariant.of(stack), stack.getCount(), t);
			if (remainder != 0) {
				finalVal = new ItemStack(stack.getItem(), (int) remainder);
			}

			if (sim) t.abort();
			else {
				t.commit();
				if (shouldUpdate())
					updateContents();
			}
		}
		return finalVal;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean sim) {
		if (amount <= 0)
			return ItemStack.EMPTY;
		if (!validIndex(slot)) // check valid slot index
			return ItemStack.EMPTY;
		if (!storage.supportsExtraction()) // make sure insertion is supported
			return ItemStack.EMPTY;

		ItemStack finalVal = ItemStack.EMPTY;
		try (Transaction t = TransferUtil.getTransaction()) {
			int index = 0;
      for (Iterator<StorageView<ItemVariant>> it = storage.iterator(); it.hasNext(); ) {
        StorageView<ItemVariant> view = it.next();
        if (index == slot) {
          ItemVariant variant = view.getResource();
          long extracted = view.isResourceBlank() ? 0 : view.extract(variant, amount, t);
          if (extracted != 0) {
            finalVal = variant.toStack((int) extracted);
          }
          break;
        }
        index++;
      }
			if (sim) t.abort();
			else {
				t.commit();
				if (shouldUpdate())
					updateContents();
			}
		}
		return finalVal;
	}

	@Override
	public int getSlotLimit(int slot) {
		if (validIndex(slot)) {
			if (shouldUpdate())
				updateContents();
			return (int) (long) capacities[slot];
		}
		return 0;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return true;
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		// jank
		extractItem(slot, getSlotLimit(slot), false);
		insertItem(slot, stack, false);
	}
}
