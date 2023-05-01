package slimeknights.tconstruct.smeltery.block.entity.component;


import io.github.fabricators_of_create.porting_lib.block.CustomUpdateTagHandlingBlockEntity;
import io.github.fabricators_of_create.porting_lib.model.data.ModelData;
import slimeknights.mantle.transfer.item.ItemTransferable;
import lombok.Getter;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.transfer.fluid.IFluidHandler;
import slimeknights.mantle.transfer.item.IItemHandler;
import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.entity.component.SmelteryInputOutputBlockEntity.SmelteryFluidIO;
import slimeknights.tconstruct.smeltery.block.entity.inventory.DuctItemHandler;
import slimeknights.tconstruct.smeltery.block.entity.inventory.DuctTankWrapper;
import slimeknights.tconstruct.smeltery.block.entity.tank.IDisplayFluidListener;
import slimeknights.tconstruct.smeltery.menu.SingleItemContainerMenu;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Filtered drain tile entity
 */
public class DuctBlockEntity extends SmelteryFluidIO implements MenuProvider, ItemTransferable, CustomUpdateTagHandlingBlockEntity, RenderAttachmentBlockEntity {
  private static final String TAG_ITEM = "item";
  private static final Component TITLE = TConstruct.makeTranslation("gui", "duct");

  @Getter
  private final DuctItemHandler itemHandler = new DuctItemHandler(this);
  private final LazyOptional<IItemHandler> itemCapability = LazyOptional.of(() -> itemHandler);
  @Getter
  private ModelData modelData = null;

  public DuctBlockEntity(BlockPos pos, BlockState state) {
    this(TinkerSmeltery.duct.get(), pos, state);
  }

  protected DuctBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }


  /* Container */

  @Override
  public Component getDisplayName() {
    return TITLE;
  }

  @Nullable
  @Override
  public AbstractContainerMenu createMenu(int id, Inventory inventory, Player playerEntity) {
    return new SingleItemContainerMenu(id, inventory, this);
  }


  /* Capability */

  @Nonnull
  @Override
  public LazyOptional<IItemHandler> getItemHandler(@org.jetbrains.annotations.Nullable Direction direction) {
    return itemCapability.cast();
  }

  @Override
  public void invalidateCaps() {
    super.invalidateCaps();
    itemCapability.invalidate();
  }

  @Override
  protected LazyOptional<IFluidHandler> makeWrapper(LazyOptional<IFluidHandler> capability) {
    return LazyOptional.of(() -> new DuctTankWrapper(capability.orElse(emptyInstance), itemHandler));
  }

  /** Updates the fluid in model data */
  public void updateFluid() {
    modelData = ModelData.builder().with(IDisplayFluidListener.PROPERTY, IDisplayFluidListener.normalizeFluid(itemHandler.getFluid())).build();
//    requestModelDataUpdate(); TODO: PORT?
    assert level != null;
    BlockState state = getBlockState();
    level.sendBlockUpdated(worldPosition, state, state, 48);
  }


  /* NBT */

  @Override
  protected boolean shouldSyncOnUpdate() {
    return true;
  }

  @Override
  public void load(CompoundTag tags) {
    super.load(tags);
    if (tags.contains(TAG_ITEM, Tag.TAG_COMPOUND)) {
      itemHandler.readFromNBT(tags.getCompound(TAG_ITEM));
    }
  }

  @Override
  public void handleUpdateTag(CompoundTag tag) {
    CustomUpdateTagHandlingBlockEntity.super.handleUpdateTag(tag);
    if (level != null && level.isClientSide) {
      updateFluid();
    }
  }

  @Override
  public void saveSynced(CompoundTag tags) {
    super.saveSynced(tags);
    tags.put(TAG_ITEM, itemHandler.writeToNBT());
  }

  @Override
  public Object getRenderAttachmentData() {
    return modelData;
  }
}
