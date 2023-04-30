package slimeknights.tconstruct.library.modifiers.spilling.effects;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import io.github.fabricators_of_create.porting_lib.crafting.CraftingHelper;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import io.github.fabricators_of_create.porting_lib.util.PotionHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.spilling.ISpillingEffect;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.utils.JsonUtils;

import java.util.Objects;

/**
 * Effect to clear all milk based potion effects
 * @param stack  Stack used for curing, standard is milk bucket
 */
public record CureEffectsSpillingEffect(ItemStack stack) implements ISpillingEffect {
  public static final ResourceLocation ID = TConstruct.getResource("cure_effects");

  @Override
  public void applyEffects(FluidStack fluid, float scale, ToolAttackContext context) {
    LivingEntity target = context.getLivingTarget();
    if (target != null) {
      PotionHelper.curePotionEffects(target, stack);
    }
  }

  @Override
  public JsonObject serialize(JsonSerializationContext context) {
    JsonObject json = JsonUtils.withType(ID);
    json.addProperty("item", Registry.ITEM.getKey(stack.getItem()).toString());
    CompoundTag nbt = stack.getTag();
    if (nbt != null) {
      json.addProperty("nbt", nbt.toString());
    }
    return json;
  }

  public static final JsonDeserializer<CureEffectsSpillingEffect> LOADER = (element, type, context) ->
    new CureEffectsSpillingEffect(CraftingHelper.getItemStack(element.getAsJsonObject(), true));
}
