package slimeknights.tconstruct.plugin.rei.modifiers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.entry.renderer.EntryRenderer;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.client.gui.widgets.TooltipContext;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import slimeknights.tconstruct.library.client.RenderUtils;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

import java.util.ArrayList;
import java.util.List;

/** Special modifier ingredient renderer used for ingredients in the bookmark menu */
public enum ModifierBookmarkEntryRenderer implements EntryRenderer<ModifierEntry> {
  INSTANCE;

  private static final String WRAPPER_KEY = "jei.tconstruct.modifier_ingredient";

  @Override
  public void render(EntryStack<ModifierEntry> entry, PoseStack matrixStack, Rectangle bounds, int mouseX, int mouseY, float delta) {
    matrixStack.pushPose();
    matrixStack.translate(bounds.getCenterX() - 16 / 2, bounds.getCenterY() - 16 / 2, 0);
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderTexture(0, ModifierRecipeCategory.BACKGROUND_LOC);
    Screen.blit(matrixStack, 0, 0, 224f, 0f, 16, 16, 256, 256);
    RenderUtils.setColorRGBA(0xFF000000 | entry.getValue().getModifier().getColor());
    Screen.blit(matrixStack, 0, 0, 240f, 0f, 16, 16, 256, 256);
    RenderUtils.setColorRGBA(-1);
    matrixStack.popPose();
  }

  @Override
  public Tooltip getTooltip(EntryStack<ModifierEntry> entry, TooltipContext context) {
    List<Component> list = new ArrayList<>();
    // not using the main method as that applies color
    list.add(Component.translatable(WRAPPER_KEY, Component.translatable(entry.getValue().getModifier().getTranslationKey())));
    if (context.getFlag().isAdvanced()) {
      list.add((Component.literal(entry.getValue().getId().toString())).withStyle(ChatFormatting.DARK_GRAY));
    }
    return Tooltip.create(list);
  }
}
