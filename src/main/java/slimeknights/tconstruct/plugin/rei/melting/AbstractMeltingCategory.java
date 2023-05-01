package slimeknights.tconstruct.plugin.rei.melting;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.fluid.FluidStack;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.fluid.tooltip.FluidTooltipHandler;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.plugin.jei.melting.MeltingFuelHandler;
import slimeknights.tconstruct.plugin.rei.IRecipeTooltipReplacement;
import slimeknights.tconstruct.plugin.rei.TinkersCategory;
import slimeknights.tconstruct.plugin.rei.widgets.ArrowWidget;
import slimeknights.tconstruct.plugin.rei.widgets.WidgetHolder;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public abstract class AbstractMeltingCategory implements TinkersCategory<MeltingDisplay> {
  protected static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/melting.png");
  protected static final String KEY_COOLING_TIME = TConstruct.makeTranslationKey("jei", "melting.time");
  protected static final String KEY_TEMPERATURE = TConstruct.makeTranslationKey("jei", "temperature");
  protected static final String KEY_MULTIPLIER = TConstruct.makeTranslationKey("jei", "melting.multiplier");
  protected static final Component TOOLTIP_ORE = Component.translatable(TConstruct.makeTranslationKey("jei", "melting.ore"));

  /** Tooltip for fuel display */
  public static final IRecipeTooltipReplacement FUEL_TOOLTIP = (slot, tooltip) -> {
    //noinspection SimplifyOptionalCallChains  Not for int streams
    EntryStack<FluidStack> stack = slot.getCurrentEntry().cast();
    MeltingFuelHandler.getTemperature(stack.getValue().getFluid()).ifPresent(temperature -> {
      tooltip.add(Component.translatable(KEY_TEMPERATURE, temperature).withStyle(ChatFormatting.GRAY));
      tooltip.add(Component.translatable(KEY_MULTIPLIER, temperature / 1000f).withStyle(ChatFormatting.GRAY));
    });
  };

  @Getter
  private final WidgetHolder background;
  protected final WidgetHolder tankOverlay;
  protected final WidgetHolder plus;

  public AbstractMeltingCategory() {
    this.background = new WidgetHolder(BACKGROUND_LOC, 0, 0, 132, 40);
    this.tankOverlay = new WidgetHolder(BACKGROUND_LOC, 132, 0, 32, 32);
    this.plus = new WidgetHolder(BACKGROUND_LOC, 132, 34, 6, 6);
  }

  @Override
  public void draw(MeltingDisplay display, PoseStack matrices, double mouseX, double mouseY) {
    // temperature
    int temperature = display.getTemperature();
    Font fontRenderer = Minecraft.getInstance().font;
    String tempString = I18n.get(KEY_TEMPERATURE, temperature);
    int x = 56 - fontRenderer.width(tempString) / 2;
    fontRenderer.draw(matrices, tempString, x, 3, Color.GRAY.getRGB());
  }

  @Override
  public void addWidgets(MeltingDisplay display, List<Widget> ingredients, Point origin, Rectangle bounds) {
    // draw the arrow
    ingredients.add(new ArrowWidget(point(56, 18, origin), BACKGROUND_LOC, 150, 41).animationDurationTicks(display.getTime() * 5));

    if (display.getOreType() != null)
      ingredients.add(plus.build(87, 31, origin));
  }

  @Override
  public List<Component> getTooltipStrings(MeltingDisplay display, List<Widget> widgets, double mouseXD, double mouseYD) {
    int mouseX = (int)mouseXD;
    int mouseY = (int)mouseYD;
    if (display.getOreType() != null && GuiUtil.isHovered(mouseX, mouseY, 87, 31, 16, 16)) {
      return Collections.singletonList(TOOLTIP_ORE);
    }
    // time tooltip
    if (GuiUtil.isHovered(mouseX, mouseY, 56, 18, 24, 17)) {
      return Collections.singletonList(Component.translatable(KEY_COOLING_TIME, display.getTime() / 4));
    }
    return Collections.emptyList();
  }

  /** Adds amounts to outputs and temperatures to fuels */
  @RequiredArgsConstructor
  public static class MeltingFluidCallback implements IRecipeTooltipReplacement {
    public static final MeltingFluidCallback INSTANCE = new MeltingFluidCallback();

    /**
     * Adds teh tooltip for ores
     *
     * @param stack  Fluid to draw
     * @param list   Tooltip so far
     * @return true if the amount is not in buckets
     */
    protected boolean appendMaterial(FluidStack stack, List<Component> list) {
      return FluidTooltipHandler.appendMaterialNoShift(stack.getFluid(), stack.getAmount(), list);
    }

    @Override
    public void addMiddleLines(Slot slot, List<Component> list) {
      EntryStack<FluidStack> stack = slot.getCurrentEntry().cast();
      if (appendMaterial(stack.getValue(), list)) {
        FluidTooltipHandler.appendShift(list);
      }
    }
  }
}
