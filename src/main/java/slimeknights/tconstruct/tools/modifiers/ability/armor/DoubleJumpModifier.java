package slimeknights.tconstruct.tools.modifiers.ability.armor;

import io.github.fabricators_of_create.porting_lib.event.common.LivingEntityEvents.Fall.FallEvent;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import io.github.fabricators_of_create.porting_lib.event.common.LivingEntityEvents;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.modifiers.impl.TotalArmorLevelModifier;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;

import java.util.Random;

public class DoubleJumpModifier extends TotalArmorLevelModifier {
  private static final ResourceLocation JUMPS = TConstruct.getResource("jumps");
  private static final TinkerDataKey<Integer> EXTRA_JUMPS = TConstruct.createKey("extra_jumps");

  private Component levelOneName = null;
  private Component levelTwoName = null;

  public DoubleJumpModifier() {
    super(EXTRA_JUMPS);
    LivingEntityEvents.FALL.register(DoubleJumpModifier::onLand);
  }

  @Override
  public Component getDisplayName(int level) {
    if (level == 1) {
      if (levelOneName == null) {
        levelOneName = applyStyle(Component.translatable(getTranslationKey() + ".double"));
      }
      return levelOneName;
    }
    if (level == 2) {
      if (levelTwoName == null) {
        levelTwoName = applyStyle(Component.translatable(getTranslationKey() + ".triple"));
      }
      return levelTwoName;
    }
    return super.getDisplayName(level);
  }

  /**
   * Causes the player to jump an extra time, if possible
   * @param entity  Entity instance who wishes to jump again
   * @return  True if the entity jumpped, false if not
   */
  public static boolean extraJump(Player entity) {
    // validate preconditions, no using when swimming, elytra, or on the ground
    if (!entity.isOnGround() && !entity.onClimbable() && !entity.isInWaterOrBubble()) {
      // determine modifier level
      int maxJumps = TinkerDataCapability.CAPABILITY.maybeGet(entity).map(data -> data.get(EXTRA_JUMPS)).orElse(0);
      if (maxJumps > 0) {
        return PersistentDataCapability.CAPABILITY.maybeGet(entity).filter(data -> {
          int jumps = data.getInt(JUMPS);
          if (jumps < maxJumps) {
            entity.jumpFromGround();
            RandomSource random = entity.getCommandSenderWorld().getRandom();
            for (int i = 0; i < 4; i++) {
              entity.getCommandSenderWorld().addParticle(ParticleTypes.HAPPY_VILLAGER, entity.getX() - 0.25f + random.nextFloat() * 0.5f, entity.getY(), entity.getZ() - 0.25f + random.nextFloat() * 0.5f, 0, 0, 0);
            }
            entity.playSound(Sounds.EXTRA_JUMP.getSound(), 0.5f, 0.5f);
            data.putInt(JUMPS, jumps + 1);
            return true;
          }
          return false;
        }).isPresent();
      }
    }
    return false;
  }

  /** Event handler to reset the number of times we have jumpped in mid air */
  private static void onLand(FallEvent event) {
    PersistentDataCapability.CAPABILITY.maybeGet(event.getEntity()).ifPresent(data -> data.remove(JUMPS));
  }
}
