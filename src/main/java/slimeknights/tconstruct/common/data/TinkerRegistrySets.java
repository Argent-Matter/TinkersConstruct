package slimeknights.tconstruct.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.shared.TinkerDamageTypes;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class TinkerRegistrySets extends DatapackBuiltinEntriesProvider {
  public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
    .add(Registry.STRUCTURE_REGISTRY, TinkerStructures::bootstrap)
    .add(Registry.CONFIGURED_FEATURE_REGISTRY, TinkerWorld::bootstrapConfigured)
    .add(Registry.PLACED_FEATURE_REGISTRY, TinkerWorld::bootstrap)
    .add(Registry.DAMAGE_TYPE_REGISTRY, TinkerDamageTypes::bootstrap);

  public TinkerRegistrySets(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
    super(output, registries, BUILDER, Collections.singleton(TConstruct.MOD_ID));
  }

  public static HolderLookup.Provider createLookup() {
    return BUILDER.buildPatch(RegistryAccess.fromRegistryOfRegistries(Registry.REGISTRY), VanillaRegistry.createLookup(_REGISTRY));
  }
}
