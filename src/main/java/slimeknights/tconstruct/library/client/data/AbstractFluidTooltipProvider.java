package slimeknights.tconstruct.library.client.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.data.DataGenerator;

/** @deprecated use {@link slimeknights.tconstruct.smeltery.block.AbstractCastingBlock} */
@Deprecated
public abstract class AbstractFluidTooltipProvider extends slimeknights.mantle.fluid.tooltip.AbstractFluidTooltipProvider {
  public AbstractFluidTooltipProvider(FabricDataGenerator output, String modId) {
    super(output, modId);
  }
}
