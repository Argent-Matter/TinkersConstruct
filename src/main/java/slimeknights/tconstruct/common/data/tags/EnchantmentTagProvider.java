package slimeknights.tconstruct.common.data.tags;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.data.ModifierIds;

import java.util.concurrent.CompletableFuture;

/*public class EnchantmentTagProvider extends FabricTagProvider.EnchantmentTagProvider {
  public EnchantmentTagProvider(FabricDataGenerator output) {
    super(output);
  }

  @Override
  protected void generateTags() {
    // upgrade
    modifierTag(TinkerModifiers.experienced.getId(), "cyclic:experience_boost", "ensorcellation:exp_boost");
    modifierTag(ModifierIds.killager, "ensorcellation:damage_illager");
    modifierTag(TinkerModifiers.magnetic.getId(), "cyclic:magnet");
    modifierTag(TinkerModifiers.necrotic.getId(), "cyclic:life_leech", "ensorcellation:leech");
    modifierTag(TinkerModifiers.severing.getId(), "cyclic:beheading", "ensorcellation:vorpal");
    modifierTag(ModifierIds.stepUp, "cyclic:step");
    modifierTag(TinkerModifiers.soulbound.getId(), "ensorcellation:soulbound");
    modifierTag(ModifierIds.trueshot, "ensorcellation:trueshot");

    // defense
    modifierTag(ModifierIds.knockbackResistance, "cyclic:steady");
    modifierTag(TinkerModifiers.magicProtection.getId(), "ensorcellation:magic_protection");
    modifierTag(ModifierIds.revitalizing, "ensorcellation:vitality");

    // ability
    modifierTag(TinkerModifiers.autosmelt.getId(), "cyclic:auto_smelt", "ensorcellation:smelting");
    modifierTag(TinkerModifiers.doubleJump.getId(), "cyclic:launch", "walljump:doublejump");
    modifierTag(TinkerModifiers.expanded.getId(), "cyclic:excavate", "ensorcellation:excavating", "ensorcellation:furrowing");
    modifierTag(ModifierIds.luck, "ensorcellation:hunter");
    modifierTag(TinkerModifiers.multishot.getId(), "cyclic:multishot", "ensorcellation:volley");
    modifierTag(ModifierIds.reach, "cyclic:reach", "ensorcellation:reach");
    modifierTag(TinkerModifiers.tilling.getId(), "ensorcellation:tilling");
    modifierTag(TinkerModifiers.reflecting.getId(), "parry:rebound");
  }

  /** Creates a builder for a tag for the given modifier *
  private void modifierTag(ModifierId modifier, String... ids) {
    TagsProvider.TagAppender<Enchantment> appender = tag(TagKey.create(Registry.ENCHANTMENT_REGISTRY, TConstruct.getResource("modifier_like/" + modifier.getPath())));
    for (String id : ids) {
      appender.addOptional(new ResourceLocation(id));
    }
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Block Enchantment Tags";
  }
}
*/
