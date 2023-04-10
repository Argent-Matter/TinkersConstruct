package slimeknights.tconstruct.world.block;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import slimeknights.tconstruct.shared.block.SlimeType;

/** Simple block to hide ichor */
public class SlimeWartBlock extends Block {
  private final SlimeType foliageType;
  public SlimeWartBlock(Properties properties, SlimeType foliageType) {
    super(properties);
    this.foliageType = foliageType;
  }

//  @Override TODO: PORT
//  public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
//    if (foliageType != SlimeType.ICHOR) {
//      super.fillItemCategory(group, items);
//    }
//  }
}
