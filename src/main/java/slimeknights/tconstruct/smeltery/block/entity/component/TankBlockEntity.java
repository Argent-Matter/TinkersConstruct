package slimeknights.tconstruct.smeltery.block.entity.component;

import dev.architectury.platform.Mod;
import io.github.fabricators_of_create.porting_lib.model.data.ModelData;
import io.github.fabricators_of_create.porting_lib.model.data.ModelProperty;
import net.minecraft.client.model.Model;
import slimeknights.mantle.transfer.fluid.FluidTransferable;
import slimeknights.mantle.transfer.fluid.IFluidHandler;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.library.client.model.ModelProperties;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.library.utils.NBTTags;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock.TankType;
import slimeknights.tconstruct.smeltery.block.entity.ITankBlockEntity;
import slimeknights.tconstruct.smeltery.item.TankItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TankBlockEntity extends SmelteryComponentBlockEntity implements ITankBlockEntity, FluidTransferable, RenderAttachmentBlockEntity {
  /** Max capacity for the tank */
  public static final long DEFAULT_CAPACITY = FluidConstants.BUCKET * 4;

  /**
   * Gets the capacity for the given block
   * @param block  block
   * @return  Capacity
   */
  public static long getCapacity(Block block) {
    if (block instanceof ITankBlock) {
      return ((ITankBlock) block).getCapacity();
    }
    return DEFAULT_CAPACITY;
  }

  /**
   * Gets the capacity for the given item
   * @param item  item
   * @return  Capacity
   */
  public static long getCapacity(Item item) {
    if (item instanceof BlockItem) {
      return getCapacity(((BlockItem)item).getBlock());
    }
    return DEFAULT_CAPACITY;
  }

  /** Internal fluid tank instance */
  @Getter
  protected final FluidTankAnimated tank;
  /** Capability holder for the tank */
  private final LazyOptional<IFluidHandler> holder;
  /** Tank data for the model */
  private final ModelData modelData;
  /** Last comparator strength to reduce block updates */
  @Getter @Setter
  private int lastStrength = -1;

  public TankBlockEntity(BlockPos pos, BlockState state) {
    this(pos, state, state.getBlock() instanceof ITankBlock tank
                     ? tank
                     : TinkerSmeltery.searedTank.get(TankType.FUEL_TANK));
  }

  /** Main constructor */
  public TankBlockEntity(BlockPos pos, BlockState state, ITankBlock block) {
    this(TinkerSmeltery.tank.get(), pos, state, block);
  }

  /** Extendable constructor */
  @SuppressWarnings("WeakerAccess")
  protected TankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, ITankBlock block) {
    super(type, pos, state);
    tank = new FluidTankAnimated(block.getCapacity(), this);
    holder = LazyOptional.of(() -> tank);
    modelData = ModelData.builder().with(ModelProperties.FLUID_TANK, tank).build();
  }


  /*
   * Tank methods
   */

  @Override
  @Nonnull
  public LazyOptional<IFluidHandler> getFluidHandler(@Nullable Direction direction) {
    return holder.cast();
  }

  @Override
  public void invalidateCaps() {
    super.invalidateCaps();
    holder.invalidate();
  }

  @Nonnull
  @Override
  public Object getRenderAttachmentData() {
    return modelData;
  }

  @Override
  public void onTankContentsChanged() {
    ITankBlockEntity.super.onTankContentsChanged();
    if (this.level != null) {
      level.getLightEngine().checkBlock(this.worldPosition);
    }
  }

  @Override
  public void updateFluidTo(FluidStack fluid) {
    ITankBlockEntity.super.updateFluidTo(fluid);
    // update light if the fluid changes
    if (this.level != null) {
      level.getLightEngine().checkBlock(this.worldPosition);
    }
  }


  /*
   * NBT
   */

  /**
   * Sets the tag on the stack based on the contained tank
   * @param stack  Stack
   */
  public void setTankTag(ItemStack stack) {
    TankItem.setTank(stack, tank);
  }

  /**
   * Updates the tank from an NBT tag, used in the block
   * @param nbt  tank NBT
   */
  public void updateTank(CompoundTag nbt) {
    if (nbt.isEmpty()) {
      tank.setFluid(FluidStack.EMPTY);
    } else {
      tank.readFromNBT(nbt);
      if (level != null) {
        level.getLightEngine().checkBlock(worldPosition);
      }
    }
  }

  @Override
  protected boolean shouldSyncOnUpdate() {
    return true;
  }

  @Override
  public void load(CompoundTag tag) {
    tank.setCapacity(getCapacity(getBlockState().getBlock()));
    updateTank(tag.getCompound(NBTTags.TANK));
    super.load(tag);
  }

  @Override
  public void saveSynced(CompoundTag tag) {
    super.saveSynced(tag);
    // want tank on the client on world load
    if (!tank.isEmpty()) {
      tag.put(NBTTags.TANK, tank.writeToNBT(new CompoundTag()));
    }
  }

  /** Interface for blocks to return their capacity */
  public interface ITankBlock {
    /** Gets the capacity for this tank */
    long getCapacity();
  }
}
