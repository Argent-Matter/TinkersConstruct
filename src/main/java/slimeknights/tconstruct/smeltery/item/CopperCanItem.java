package slimeknights.tconstruct.smeltery.item;

import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import slimeknights.tconstruct.library.recipe.FluidValues;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * Fluid container holding 1 ingot of fluid
 */
@SuppressWarnings("removal")
public class CopperCanItem extends Item {
  private static final String TAG_FLUID = "fluid";
  private static final String TAG_FLUID_TAG = "fluid_tag";

  public CopperCanItem(Properties properties) {
    super(properties);
    FluidStorage.ITEM.registerForItems((itemStack, context) -> new CopperCanFluidHandler(context), this);
  }

  @Override
  public ItemStack getRecipeRemainder(ItemStack stack) {
    Fluid fluid = getFluid(stack.getTag());
    if (fluid != Fluids.EMPTY) {
      return new ItemStack(this);
    }
    return ItemStack.EMPTY;
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
    Fluid fluid = getFluid(stack.getTag());
    if (fluid != Fluids.EMPTY) {
      CompoundTag fluidTag = getFluidTag(stack.getTag());
      MutableComponent text;
      if (fluidTag != null) {
        FluidStack displayFluid = new FluidStack(fluid, FluidValues.INGOT, fluidTag);
        text = displayFluid.getDisplayName().plainCopy();
      } else {
        text = Component.translatable(fluid.getAttributes().getTranslationKey());
      }
      tooltip.add(Component.translatable(this.getDescriptionId() + ".contents", text).withStyle(ChatFormatting.GRAY));
    } else {
      tooltip.add(Component.translatable(this.getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY));
    }
  }

  /** Sets the fluid on the given stack */
  public static ItemStack setFluid(ItemStack stack, FluidStack fluid) {
    // if empty, try to remove the NBT, helps with recipes
    if (fluid.isEmpty()) {
      CompoundTag nbt = stack.getTag();
      if (nbt != null) {
        nbt.remove(TAG_FLUID);
        nbt.remove(TAG_FLUID_TAG);
        if (nbt.isEmpty()) {
          stack.setTag(null);
        }
      }
    } else {
      CompoundTag nbt = stack.getOrCreateTag();
      nbt.putString(TAG_FLUID, Objects.requireNonNull(Registry.FLUID.getKey(fluid.getFluid())).toString());
      CompoundTag fluidTag = fluid.getTag();
      if (fluidTag != null) {
        nbt.put(TAG_FLUID_TAG, fluidTag.copy());
      } else {
        nbt.remove(TAG_FLUID_TAG);
      }
    }
    return stack;
  }

  /** Gets the fluid from the given stack */
  public static Fluid getFluid(CompoundTag nbt) {
    if (nbt != null) {
      ResourceLocation location = ResourceLocation.tryParse(nbt.getString(TAG_FLUID));
      if (location != null && Registry.FLUID.containsKey(location)) {
        Fluid fluid = Registry.FLUID.get(location);
        if (fluid != null) {
          return fluid;
        }
      }
    }
    return Fluids.EMPTY;
  }

  /** Gets the fluid NBT from the given stack */
  @Nullable
  public static CompoundTag getFluidTag(CompoundTag nbt) {
    if (nbt != null && nbt.contains(TAG_FLUID_TAG, Tag.TAG_COMPOUND)) {
      return nbt.getCompound(TAG_FLUID_TAG);
    }
    return null;
  }

  /**
   * Gets a string variant name for the given stack
   * @param stack  Stack instance to check
   * @return  String variant name
   */
  public static String getSubtype(ItemStack stack) {
    CompoundTag nbt = stack.getTag();
    if (nbt != null) {
      return nbt.getString(TAG_FLUID);
    }
    return "";
  }
}
