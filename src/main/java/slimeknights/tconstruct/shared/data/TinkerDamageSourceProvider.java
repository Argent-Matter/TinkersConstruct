package slimeknights.tconstruct.shared.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import slimeknights.tconstruct.shared.TinkerDamageTypes;

import java.util.concurrent.CompletableFuture;

public class TinkerDamageSourceProvider extends FabricTagProvider<DamageType> {

  public TinkerDamageSourceProvider(FabricDataGenerator output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
    super(output, Registry.DAMAGE_TYPE_REGISTRY, registriesFuture);
  }

  @Override
  protected void addTags(HolderLookup.Provider arg) {
    getOrCreateTagBuilder(DamageTypeTags.BYPASSES_ARMOR).add(TinkerDamageTypes.SELF_DESTRUCT);
    getOrCreateTagBuilder(DamageTypeTags.IS_EXPLOSION).add(TinkerDamageTypes.SELF_DESTRUCT);

    getOrCreateTagBuilder(DamageTypeTags.BYPASSES_EFFECTS).add(TinkerDamageTypes.BLEEDING);

    getOrCreateTagBuilder(DamageTypeTags.IS_FIRE).add(TinkerDamageTypes.SMELTERY_DAMAGE);
  }
}
