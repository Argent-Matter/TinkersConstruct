package slimeknights.tconstruct.tools.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.fabricators_of_create.porting_lib.client.armor.ArmorRenderer;
import io.github.fabricators_of_create.porting_lib.client.armor.ArmorRendererRegistry;
import io.github.fabricators_of_create.porting_lib.item.ArmorTextureItem;
import io.github.fabricators_of_create.porting_lib.item.WalkOnSnowItem;
import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;
import io.github.fabricators_of_create.porting_lib.util.client.ClientHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.item.ModifiableArmorItem;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.client.TravelersGearModel;

import javax.annotation.Nullable;

public class TravelersGearItem extends ModifiableArmorItem implements ArmorTextureItem, WalkOnSnowItem {
  /** Golden texture for armor */
  private static final String GOLDEN_ARMOR = TConstruct.resourceString("textures/models/armor/travelers_golden_1.png");
  /** Golden texture for leggings */
  private static final String GOLDEN_LEGS = TConstruct.resourceString("textures/models/armor/travelers_golden_2.png");

  public TravelersGearItem(ModifiableArmorMaterial material, ArmorSlotType slotType, Properties properties) {
    super(material, slotType, properties);
    EnvExecutor.runWhenOn(EnvType.CLIENT, () -> this::initializeClient);
  }

  @Override
  public boolean canWalkOnPowderedSnow(ItemStack stack, LivingEntity wearer) {
    return slot == EquipmentSlot.FEET;
  }

  @Nullable
  @Override
  public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
    if (ModifierUtil.getModifierLevel(stack, TinkerModifiers.golden.getId()) > 0) {
      return slot == EquipmentSlot.LEGS ? GOLDEN_LEGS : GOLDEN_ARMOR;
    }
    return null;
  }

  @Environment(EnvType.CLIENT)
  public void initializeClient() {
    ArmorRendererRegistry.register(new TravelersGearRenderer(), this);
  }

  @Environment(EnvType.CLIENT)
  private static final class TravelersGearRenderer implements ArmorRenderer {
    @Override
    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, ItemStack itemStack, LivingEntity entity, EquipmentSlot armorSlot, int light, HumanoidModel<LivingEntity> playerModel, HumanoidModel<LivingEntity> baseArmorModel) {
      playerModel.copyPropertiesTo(baseArmorModel);
      ClientHooks.setPartVisibility(baseArmorModel, armorSlot);
      Model armorModel = TravelersGearModel.getModel(itemStack, armorSlot, baseArmorModel);
      VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(vertexConsumers, RenderType.armorCutoutNoCull(ClientHooks.getArmorResource(entity, itemStack, armorSlot, null)), false, itemStack.hasFoil());
      armorModel.renderToBuffer(matrices, vertexconsumer, light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
    }
  }
}
