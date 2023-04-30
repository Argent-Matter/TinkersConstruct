package slimeknights.tconstruct.library.data.tinkering;

import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import slimeknights.tconstruct.library.data.AbstractTagProvider;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;

/** Tag provider to generate modifier tags */
public abstract class AbstractModifierTagProvider extends AbstractTagProvider<Modifier> {
  protected AbstractModifierTagProvider(FabricDataGenerator output, String modId) {
    super(output, modId, ModifierManager.TAG_FOLDER, Modifier::getId, id -> ModifierManager.INSTANCE.containsStatic(new ModifierId(id)), existingFileHelper);
  }
}
