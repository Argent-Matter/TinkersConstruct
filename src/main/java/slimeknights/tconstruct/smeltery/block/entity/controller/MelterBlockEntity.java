package slimeknights.tconstruct.smeltery.block.entity.controller;

import dev.architectury.platform.Mod;
import io.github.fabricators_of_create.porting_lib.block.ChunkUnloadListeningBlockEntity;
import io.github.fabricators_of_create.porting_lib.model.data.ModelData;
import slimeknights.mantle.transfer.fluid.FluidTransferable;
import slimeknights.mantle.transfer.fluid.IFluidHandler;
import slimeknights.mantle.transfer.item.IItemHandler;
import slimeknights.mantle.transfer.item.ItemTransferable;
import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.block.entity.NameableBlockEntity;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.model.ModelProperties;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.utils.NBTTags;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.controller.ControllerBlock;
import slimeknights.tconstruct.smeltery.block.controller.MelterBlock;
import slimeknights.tconstruct.smeltery.block.entity.ITankBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.module.FuelModule;
import slimeknights.tconstruct.smeltery.block.entity.module.MeltingModuleInventory;
import slimeknights.tconstruct.smeltery.menu.MelterContainerMenu;

import javax.annotation.Nullable;
import java.util.Collections;

public class MelterBlockEntity extends NameableBlockEntity implements ITankBlockEntity, FluidTransferable, ItemTransferable, ChunkUnloadListeningBlockEntity, RenderAttachmentBlockEntity {

  /** Max capacity for the tank */
  private static final long TANK_CAPACITY = FluidValues.INGOT * 12;
  /* tags */
  private static final String TAG_INVENTORY = "inventory";
  /** Name of the GUI */
  private static final MutableComponent NAME = TConstruct.makeTranslation("gui", "melter");

  public static final BlockEntityTicker<MelterBlockEntity> SERVER_TICKER = (level, pos, state, self) -> self.tick(level, pos, state);

  /* Tank */
  /** Internal fluid tank output */
  @Getter
  protected final FluidTankAnimated tank = new FluidTankAnimated(TANK_CAPACITY, this);
  /** Capability holder for the tank */
  private final LazyOptional<IFluidHandler> tankHolder = LazyOptional.of(() -> tank);
  /** Tank data for the model */
  @Getter
  private final ModelData modelData = ModelData.builder().with(ModelProperties.FLUID_TANK, tank).build();
  /** Last comparator strength to reduce block updates */
  @Getter @Setter
  private int lastStrength = -1;

  /** Internal tick counter */
  private int tick;

  /* Heating */
  /** Handles all the melting needs */
  @Getter
  private final MeltingModuleInventory meltingInventory = new MeltingModuleInventory(this, tank, Config.COMMON.melterOreRate, 3);
  /** Capability holder for the tank */
  private final LazyOptional<IItemHandler> inventoryHolder = LazyOptional.of(() -> meltingInventory);

  /** Fuel handling logic */
  @Getter
  private final FuelModule fuelModule = new FuelModule(this, () -> Collections.singletonList(this.worldPosition.below()));

  /** Main constructor */
  public MelterBlockEntity(BlockPos pos, BlockState state) {
    this(TinkerSmeltery.melter.get(), pos, state);
  }

  /** Extendable constructor */
  @SuppressWarnings("WeakerAccess")
  protected MelterBlockEntity(BlockEntityType<? extends MelterBlockEntity> type, BlockPos pos, BlockState state) {
    super(type, pos, state, NAME);
  }

  @Nullable
  @Override
  public AbstractContainerMenu createMenu(int id, Inventory inv, Player playerEntity) {
    return new MelterContainerMenu(id, inv, this);
  }

  /*
   * Tank methods
   */

//  @Nonnull
//  @Override
//  public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
//    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
//      return tankHolder.cast();
//    }
//    if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
//      return inventoryHolder.cast();
//    }
//    return super.getCapability(capability, facing);
//  }
  
  @Nullable
  @Override
  public LazyOptional<IFluidHandler> getFluidHandler(@Nullable Direction direction) {
    return tankHolder;
  }
  
  @Nullable
  @Override
  public LazyOptional<IItemHandler> getItemHandler(@Nullable Direction direction) {
    return inventoryHolder;
  }
  
  @Override
  public void setRemoved() {
    super.setRemoved();
    invalidateCaps();
  }
  
  @Override
  public void onChunkUnloaded() {
    invalidateCaps();
  }
  
//  @Override
  public void invalidateCaps() {
//    super.invalidateCaps();
    this.tankHolder.invalidate();
    this.inventoryHolder.invalidate();
  }

  /*
   * Melting
   */

  /** Checks if the tile entity is active */
  private boolean isFormed() {
    BlockState state = this.getBlockState();
    return state.hasProperty(MelterBlock.IN_STRUCTURE) && state.getValue(MelterBlock.IN_STRUCTURE);
  }

  /** Ticks the TE on the server */
  private void tick(Level level, BlockPos pos, BlockState state) {
    // are we fully formed?
    if (isFormed()) {
      switch (tick) {
        // tick 0: find fuel
        case 0:
          if (!fuelModule.hasFuel() && meltingInventory.canHeat(fuelModule.findFuel(false))) {
            fuelModule.findFuel(true);
          }
        // tick 2: heat items and consume fuel
        case 2: {
          boolean hasFuel = fuelModule.hasFuel();
          // update the active state
          if (state.getValue(ControllerBlock.ACTIVE) != hasFuel) {
            level.setBlockAndUpdate(pos, state.setValue(ControllerBlock.ACTIVE, hasFuel));
            // update the heater below
            BlockPos down = pos.below();
            BlockState downState = level.getBlockState(down);
            if (downState.is(TinkerTags.Blocks.FUEL_TANKS) && downState.hasProperty(ControllerBlock.ACTIVE) && downState.getValue(ControllerBlock.ACTIVE) != hasFuel) {
              level.setBlockAndUpdate(down, downState.setValue(ControllerBlock.ACTIVE, hasFuel));
            }
          }
          // heat items
          if (hasFuel) {
            meltingInventory.heatItems(fuelModule.getTemperature());
            fuelModule.decreaseFuel(1);
          } else {
            meltingInventory.coolItems();
          }
        }
      }
      tick = (tick + 1) % 4;
    }
  }


  /*
   * NBT
   */

  @Override
  protected boolean shouldSyncOnUpdate() {
    return true;
  }

  @Override
  public void load(CompoundTag tag) {
    super.load(tag);
    tank.readFromNBT(tag.getCompound(NBTTags.TANK));
    fuelModule.readFromTag(tag);
    if (tag.contains(TAG_INVENTORY, Tag.TAG_COMPOUND)) {
      meltingInventory.readFromTag(tag.getCompound(TAG_INVENTORY));
    }
  }

  @Override
  public void saveSynced(CompoundTag tag) {
    super.saveSynced(tag);
    tag.put(NBTTags.TANK, tank.writeToNBT(new CompoundTag()));
    tag.put(TAG_INVENTORY, meltingInventory.writeToTag());
  }

  @Override
  public void saveAdditional(CompoundTag tag) {
    super.saveAdditional(tag);
    fuelModule.writeToTag(tag);
  }

  @Override
  public Object getRenderAttachmentData() {
    return modelData;
  }
}
