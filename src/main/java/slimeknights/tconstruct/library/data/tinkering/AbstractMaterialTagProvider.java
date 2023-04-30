package slimeknights.tconstruct.library.data.tinkering;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.data.DataGenerator;
import slimeknights.tconstruct.library.data.AbstractTagProvider;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialManager;

/** Tag provider for materials */
public abstract class AbstractMaterialTagProvider extends AbstractTagProvider<IMaterial> {
  protected AbstractMaterialTagProvider(FabricDataGenerator output, String modId) {
    super(output, modId, MaterialManager.TAG_FOLDER, IMaterial::getIdentifier, id -> true);
  }
}
