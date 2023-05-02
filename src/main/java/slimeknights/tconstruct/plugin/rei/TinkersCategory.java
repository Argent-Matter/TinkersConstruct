package slimeknights.tconstruct.plugin.rei;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.fluid.FluidStack;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.client.util.ClientEntryStacks;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.CollectionUtils;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import slimeknights.tconstruct.plugin.rei.widgets.TooltipWidget;
import slimeknights.tconstruct.plugin.rei.widgets.WidgetHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public interface TinkersCategory<T extends Display> extends DisplayCategory<T> {

  default void draw(T display, PoseStack matrixStack, double mouseX, double mouseY) {}

  void addWidgets(T display, List<Widget> ingredients, Point origin, Rectangle bounds);

  WidgetHolder getBackground();

  default List<Component> getTooltipStrings(T display, List<Widget> widgets, double mouseX, double mouseY) {
    return Collections.emptyList();
  }

  @Override
  default int getDisplayHeight() {
    return getBackground().height() + 8;
  }

  @Override
  default int getDisplayWidth(T display) {
    return getBackground().width() + 8;
  }

  @Override
  default List<Widget> setupDisplay(T display, Rectangle bounds) {
    List<Widget> widgets = new ArrayList<>();
    Point origin = new Point(bounds.getX() + 5, bounds.getY() + 5);
    widgets.add(Widgets.createRecipeBase(bounds));
    widgets.add(getBackground().build(0, 0, origin));
    widgets.add(Widgets.createDrawableWidget((helper, poseStack, mouseX, mouseY, partialTick) -> {
      poseStack.pushPose();
      poseStack.translate(bounds.getX() + 5, bounds.getY() + 5, 0);
      draw(display, poseStack, mouseX, mouseY);
      poseStack.popPose();
    }));
    addWidgets(display, widgets, origin, bounds);
    if (this instanceof IRecipeTooltipReplacement replacement)
      widgets.forEach(widget -> {
        if (widget instanceof Slot slot) {
          for (EntryStack<?> entry : slot.getEntries()) {
            ClientEntryStacks.setTooltipProcessor(entry, (stack, tooltip) -> {
              List<Component> components = CollectionUtils.filterAndMap(tooltip.entries(), Tooltip.Entry::isText, Tooltip.Entry::getAsText);
              List<TooltipComponent> tooltipComponents = CollectionUtils.filterAndMap(tooltip.entries(), Tooltip.Entry::isTooltipComponent, Tooltip.Entry::getAsTooltipComponent);
              replacement.onTooltip(slot, components);
              tooltip.entries().clear();
              tooltip.addAllTexts(components);
              tooltip.addAllTooltipComponents(tooltipComponents);
              return tooltip;
            });
          }
        }
      });
    widgets.add(new TooltipWidget<>(this, widgets, display, bounds));
    return widgets;
  }

  default Slot slot(int x, int y, Point origin) {
    return Widgets.createSlot(new Point(origin.x + x, origin.y + y)).disableBackground();
  }

  default Point point(int x, int y, Point origin) {
    return new Point(origin.getX() + x, origin.getY() + y);
  }

  static List<FluidStack> toREIFluids(List<io.github.fabricators_of_create.porting_lib.util.FluidStack> fluids) {
    List<FluidStack> newFluids = new ArrayList<>();
    fluids.forEach(stack -> newFluids.add(toREIFluid(stack)));
    return newFluids;
  }

  static FluidStack toREIFluid(io.github.fabricators_of_create.porting_lib.util.FluidStack stack) {
    return FluidStack.create(stack.getFluid(), stack.getAmount(), stack.getTag());
  }

  static io.github.fabricators_of_create.porting_lib.util.FluidStack fromREIFluid(FluidStack stack) {
    return new io.github.fabricators_of_create.porting_lib.util.FluidStack(stack.getFluid(), stack.getAmount(), stack.getTag());
  }

  static Slot slot(int x, int y, Point origin, boolean isInput) {
    Slot slot = Widgets.createSlot(new Point(origin.x + x, origin.y + y));
    return isInput ? slot.markInput() : slot.markOutput();
  }

  static void setEntryTooltip(Slot slot, IRecipeTooltipReplacement replacement) {
    slot.getEntries().forEach(stack -> {
      ClientEntryStacks.setTooltipProcessor(stack, (entryStack, tooltip) -> {
        List<Component> components = CollectionUtils.filterAndMap(tooltip.entries(), Tooltip.Entry::isText, Tooltip.Entry::getAsText);
        List<TooltipComponent> tooltipComponents = CollectionUtils.filterAndMap(tooltip.entries(), Tooltip.Entry::isTooltipComponent, Tooltip.Entry::getAsTooltipComponent);
        replacement.onTooltip(slot, components);
        tooltip.entries().clear();
        tooltip.addAllTexts(components);
        tooltip.addAllTooltipComponents(tooltipComponents);
        return tooltip;
      });
    });
  }
}
