package slimeknights.tconstruct.library.modifiers.spilling.effects;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.spilling.ISpillingEffect;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.utils.JsonUtils;

/** Spilling effect to remove a specific effect */
public record RemoveEffectSpillingEffect(MobEffect effect) implements ISpillingEffect {
  public static final ResourceLocation ID = TConstruct.getResource("remove_effect");

  @Override
  public void applyEffects(FluidStack fluid, float scale, ToolAttackContext context) {
    LivingEntity living = context.getLivingTarget();
    if (living != null) {
      living.removeEffect(effect);
    }
  }

  @Override
  public JsonObject serialize(JsonSerializationContext context) {
    JsonObject json = JsonUtils.withType(ID);
    json.addProperty("effect", Registry.MOB_EFFECT.getKey(effect).toString());
    return json;
  }

  /** Loader instance */
  public static final JsonDeserializer<RemoveEffectSpillingEffect> LOADER = (json, typeOfT, context) ->
    new RemoveEffectSpillingEffect(JsonHelper.getAsEntry(Registry.MOB_EFFECT, json.getAsJsonObject(), "effect"));
}
