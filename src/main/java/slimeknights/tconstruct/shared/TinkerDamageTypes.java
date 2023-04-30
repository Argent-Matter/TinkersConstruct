package slimeknights.tconstruct.shared;

import net.minecraft.core.RegistryAccess;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import slimeknights.tconstruct.TConstruct;

public class TinkerDamageTypes {
  /** Self damage source */
  public static final ResourceKey<DamageType> SELF_DESTRUCT = ResourceKey.create(Registry.DAMAGE_TYPE_REGISTRY, TConstruct.getResource("self_destruct"));
  public static final ResourceKey<DamageType> BLEEDING = ResourceKey.create(Registry.DAMAGE_TYPE_REGISTRY, TConstruct.getResource("bleed"));
  /** Standard damage source for melting most mobs */
  public static final ResourceKey<DamageType> SMELTERY_DAMAGE = ResourceKey.create(Registry.DAMAGE_TYPE_REGISTRY, TConstruct.getResource("smeltery_heat"));
  /** Special damage source for "absorbing" hot entities */
  public static final ResourceKey<DamageType> SMELTERY_MAGIC = ResourceKey.create(Registry.DAMAGE_TYPE_REGISTRY, TConstruct.getResource("smeltery_magic"));

  public static void bootstrap(BootstapContext<DamageType> context) {
    context.register(SELF_DESTRUCT, new DamageType(TConstruct.prefix("self_destruct"), 0.1F));
    context.register(BLEEDING, new DamageType(TConstruct.prefix("bleed"), 0.1F));
    context.register(SMELTERY_DAMAGE, new DamageType(TConstruct.prefix("smeltery_heat"), 0.1F));
    context.register(SMELTERY_MAGIC, new DamageType(TConstruct.prefix("smeltery_magic"), 0.1F));
  }

  public static DamageSource getSource(RegistryAccess registryAccess, ResourceKey<DamageType> damageType) {
    return new DamageSource(registryAccess.registryOrThrow(Registry.DAMAGE_TYPE_REGISTRY).getHolderOrThrow(damageType));
  }
}
