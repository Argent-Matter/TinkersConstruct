package slimeknights.tconstruct.library.data.tinkering;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

/** @deprecated use {@link slimeknights.mantle.fluid.transfer.AbstractFluidContainerTransferProvider} */
@Deprecated
public abstract class AbstractFluidContainerTransferProvider extends slimeknights.mantle.fluid.transfer.AbstractFluidContainerTransferProvider {
  public AbstractFluidContainerTransferProvider(FabricDataGenerator output, String modId) {
    super(output, modId);
  }
}
