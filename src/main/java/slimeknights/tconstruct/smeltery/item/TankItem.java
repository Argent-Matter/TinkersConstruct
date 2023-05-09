package slimeknights.tconstruct.smeltery.item;

import io.github.fabricators_of_create.porting_lib.item.CustomMaxCountItem;
import io.github.fabricators_of_create.porting_lib.transfer.fluid.item.FluidHandlerItemStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.fluid.tooltip.FluidTooltipHandler;
import slimeknights.mantle.item.BlockTooltipItem;
import slimeknights.mantle.transfer.fluid.FluidTank;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.utils.NBTTags;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock;
import slimeknights.tconstruct.smeltery.block.entity.component.TankBlockEntity;

import javax.annotation.Nullable;
import java.util.List;

public class TankItem extends BlockTooltipItem implements CustomMaxCountItem {
  private static final String KEY_FLUID = TConstruct.makeTranslationKey("block", "tank.fluid");
  private static final String KEY_MB = TConstruct.makeTranslationKey("block", "tank.mb");
  private static final String KEY_INGOTS = TConstruct.makeTranslationKey("block", "tank.ingots");
  private static final String KEY_MIXED = TConstruct.makeTranslationKey("block", "tank.mixed");

  private final boolean limitStackSize;
  @SuppressWarnings("UnstableApiUsage")
  public TankItem(Block blockIn, Properties builder, boolean limitStackSize) {
    super(blockIn, builder);
    this.limitStackSize = limitStackSize;
    FluidStorage.combinedItemApiProvider(this).register(ctx -> new FluidHandlerItemStack(ctx, TankBlockEntity.getCapacity(ctx.getItemVariant().getItem())));
  }

  /** Checks if the tank item is filled */
  private static boolean isFilled(ItemStack stack) {
    // has a container if not empty
    CompoundTag nbt = stack.getTag();
    return nbt != null && nbt.contains(NBTTags.TANK, Tag.TAG_COMPOUND);
  }

  @Override
  public ItemStack getRecipeRemainder(ItemStack stack) {
    return isFilled(stack) ? new ItemStack(this) : ItemStack.EMPTY;
  }

  @Override
  public int getItemStackLimit(ItemStack stack) {
    if (!limitStackSize) {
      return 64;
    }
    return isFilled(stack) ? 16: 64;
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
    if (stack.hasTag()) {
      FluidTank tank = getFluidTank(stack);
      if (tank.getFluidAmount() > 0) {
        // TODO: migrate to a fluid tooltip JSON?
        tooltip.add(Component.translatable(KEY_FLUID, tank.getFluid().getDisplayName()).withStyle(ChatFormatting.GRAY));
        long amount = tank.getFluidAmount();
        TooltipKey key = SafeClientAccess.getTooltipKey();
        if (tank.getCapacity() % FluidValues.INGOT != 0 || key == TooltipKey.SHIFT) {
          tooltip.add(Component.translatable(KEY_MB, amount).withStyle(ChatFormatting.GRAY));
        } else {
          long ingots = amount / FluidValues.INGOT;
          long mb = amount % FluidValues.INGOT;
          if (mb == 0) {
            tooltip.add(Component.translatable(KEY_INGOTS, ingots).withStyle(ChatFormatting.GRAY));
          } else {
            tooltip.add(Component.translatable(KEY_MIXED, ingots, mb).withStyle(ChatFormatting.GRAY));
          }
          if (key != TooltipKey.UNKNOWN) {
            tooltip.add(FluidTooltipHandler.HOLD_SHIFT);
          }
        }

      }
    }
    else {
      super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
  }

//  @Nullable
//  @Override
//  public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
//    return new TankItemFluidHandler(stack);
//  }

  public static void initCapabilities() {
    for (SearedTankBlock block : TinkerSmeltery.searedTank.values()) {
    }
  }

  /**
   * Sets the tank to the given stack
   * @param stack  Stack
   * @param tank   Tank instance
   * @return  Stack with tank
   */
  public static ItemStack setTank(ItemStack stack, FluidTank tank) {
    if (tank.isEmpty()) {
      CompoundTag nbt = stack.getTag();
      if (nbt != null) {
        nbt.remove(NBTTags.TANK);
        if (nbt.isEmpty()) {
          stack.setTag(null);
        }
      }
    } else {
      stack.getOrCreateTag().put(NBTTags.TANK, tank.writeToNBT(new CompoundTag()));
    }
    return stack;
  }

  /**
   * Gets the tank for the given stack
   * @param stack  Tank stack
   * @return  Tank stored in the stack
   */
  public static FluidTank getFluidTank(ItemStack stack) {
    FluidTank tank = new FluidTank(TankBlockEntity.getCapacity(stack.getItem()));
    if (stack.hasTag()) {
      assert stack.getTag() != null;
      tank.readFromNBT(stack.getTag().getCompound(NBTTags.TANK));
    }
    return tank;
  }
}
