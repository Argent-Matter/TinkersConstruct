package slimeknights.tconstruct.plugin.rei.modifiers;

import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.entry.renderer.EntryRenderer;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.client.gui.widgets.TooltipContext;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;

import java.util.ArrayList;
import java.util.List;

public record ModifierEntryRenderer(int width, int height) implements EntryRenderer<ModifierEntry> {
  @Override
  public void render(EntryStack<ModifierEntry> entry, PoseStack matrices, Rectangle bounds, int mouseX, int mouseY, float delta) {
    matrices.pushPose();
    matrices.translate(bounds.getCenterX() - width / 2, bounds.getCenterY() - height / 2, 0);
    Component name = entry.getValue().getModifier().getDisplayName(entry.getValue().getLevel());
    Font fontRenderer = Minecraft.getInstance().font;
    int x = (width - fontRenderer.width(name)) / 2;
    fontRenderer.drawShadow(matrices, name, x, 1, -1);
    matrices.popPose();
  }

  @Override
  public @Nullable Tooltip getTooltip(EntryStack<ModifierEntry> entry, TooltipContext context) {
    List<Component> tooltip = entry.getValue().getModifier().getDescriptionList(entry.getValue().getLevel());
    if (context.getFlag().isAdvanced()) {
      tooltip = new ArrayList<>(tooltip);
      tooltip.add((Component.literal(entry.getValue().getId().toString())).withStyle(ChatFormatting.DARK_GRAY));
    }
    return Tooltip.create(tooltip);
  }
}
