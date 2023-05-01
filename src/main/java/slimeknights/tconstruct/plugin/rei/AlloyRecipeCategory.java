package slimeknights.tconstruct.plugin.rei;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import lombok.Getter;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.util.ClientEntryStacks;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.fluid.tooltip.FluidTooltipHandler;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.plugin.jei.melting.MeltingFuelHandler;
import slimeknights.tconstruct.plugin.rei.widgets.ArrowWidget;
import slimeknights.tconstruct.plugin.rei.widgets.WidgetHolder;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.awt.*;
import java.util.List;

/**
 * Alloy recipe category for REI display
 */
public class AlloyRecipeCategory implements TinkersCategory<AlloyDisplay> {
  private static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/alloy.png");
  private static final Component TITLE = TConstruct.makeTranslation("jei", "alloy.title");
  private static final String KEY_TEMPERATURE = TConstruct.makeTranslationKey("jei", "temperature");

  /** Tooltip for fluid inputs */
  private static final IRecipeTooltipReplacement FLUID_TOOLTIP = (slot, list) -> {
    if (slot.getCurrentEntry().getType() == VanillaEntryTypes.FLUID)
      FluidTooltipHandler.appendMaterial(TinkersCategory.fromREIFluid(slot.getCurrentEntry().castValue()), list);
  };

  /** Tooltip for fuel display */
  public static final IRecipeTooltipReplacement FUEL_TOOLTIP = (slot, tooltip) -> {
    //noinspection SimplifyOptionalCallChains  Not for int streams
    if (slot.getCurrentEntry().getType() == VanillaEntryTypes.FLUID)
      MeltingFuelHandler.getTemperature(slot.getCurrentEntry().<dev.architectury.fluid.FluidStack>castValue().getFluid())
        .ifPresent(temperature -> tooltip.add(Component.translatable(KEY_TEMPERATURE, temperature).withStyle(ChatFormatting.GRAY)));
  };

  @Getter
  private final WidgetHolder background;
  @Getter
  private final Renderer icon;
  private final WidgetHolder tank;

  public AlloyRecipeCategory() {
    this.background = new WidgetHolder(BACKGROUND_LOC, 0, 0, 172, 62);
    this.icon = EntryStacks.of(new ItemStack(TinkerSmeltery.smelteryController));
    this.tank = new WidgetHolder(BACKGROUND_LOC, 172, 17, 16, 16);
  }

  @SuppressWarnings("removal")
  @Override
  public CategoryIdentifier<AlloyDisplay> getCategoryIdentifier() {
    return TConstructREIConstants.ALLOY;
  }

  @Override
  public Component getTitle() {
    return TITLE;
  }

  @Override
  public void draw(AlloyDisplay display, PoseStack matrices, double mouseX, double mouseY) {
    // temperature info
    Font fontRenderer = Minecraft.getInstance().font;
    String tempString = I18n.get(KEY_TEMPERATURE, display.getTemperature());
    int x = 102 - (fontRenderer.width(tempString) / 2);
    fontRenderer.draw(matrices, tempString, x, 5, Color.GRAY.getRGB());
  }

  /**
   * Draws a variable number of fluids
   * @param widgets      Widgets
   * @param isInput      Role of the set of fluids in the recipe
   * @param x            X start
   * @param y            Y start
   * @param totalWidth   Total width
   * @param height       Tank height
   * @param fluids       List of fluids to draw
   * @param minAmount    Minimum tank size
   * @param tooltip      Tooltip callback
   * @return Max amount based on fluids
   */
  public static long drawVariableFluids(List<Widget> widgets, boolean isInput, int x, int y, Point origin, int totalWidth, int height, java.util.List<java.util.List<FluidStack>> fluids, long minAmount, IRecipeTooltipReplacement tooltip) {
    int count = fluids.size();
    long maxAmount = minAmount;
    if (count > 0) {
      // first, find maximum used amount in the recipe so relations are correct
      for(List<FluidStack> list : fluids) {
        for(FluidStack input : list) {
          if (input.getAmount() > maxAmount) {
            maxAmount = input.getAmount();
          }
        }
      }
      // next, draw all fluids but the last
      int w = totalWidth / count;
      int max = count - 1;
      for (int i = 0; i < max; i++) {
        int fluidX = x + i * w;
        Slot slot = TinkersCategory.slot(fluidX, y, origin, isInput)
          .disableBackground()
          .entries(EntryIngredients.of(VanillaEntryTypes.FLUID, TinkersCategory.toREIFluids(fluids.get(i))));
        long finalMaxAmount = maxAmount;
        slot.getEntries().forEach(entryStack -> ClientEntryStacks.setFluidRenderRatio(entryStack.cast(), entryStack.<dev.architectury.fluid.FluidStack>castValue().getAmount() / (float) finalMaxAmount));
        slot.getBounds().setSize(w + 2, height + 2);
        TinkersCategory.setEntryTooltip(slot, tooltip);
        widgets.add(slot);
      }
      // for the last, the width is the full remaining width
      int fluidX = x + max * w;
      Slot slot = TinkersCategory.slot(fluidX, y, origin, isInput)
        .disableBackground()
        .entries(EntryIngredients.of(VanillaEntryTypes.FLUID, TinkersCategory.toREIFluids(fluids.get(max))));
      long finalMaxAmount1 = maxAmount;
      slot.getEntries().forEach(entryStack -> ClientEntryStacks.setFluidRenderRatio(entryStack.cast(), entryStack.<dev.architectury.fluid.FluidStack>castValue().getAmount() / (float) finalMaxAmount1));
      slot.getBounds().setSize(totalWidth - (w * max) + 2, height + 2);
      TinkersCategory.setEntryTooltip(slot, tooltip);
      widgets.add(slot);
    }
    return maxAmount;
  }

  @Override
  public void addWidgets(AlloyDisplay display, List<Widget> widgets, Point origin, Rectangle bounds) {
    // inputs
    long maxAmount = drawVariableFluids(widgets, true, 19, 11, origin, 48, 32, display.getRecipe().getDisplayInputs(), display.getRecipe().getOutput().getAmount(), FLUID_TOOLTIP);

    // output
    Slot output = slot(137, 11, origin).markOutput()
      .entries(display.getOutputEntries().get(0));
    output.getEntries().forEach(entryStack -> ClientEntryStacks.setFluidRenderRatio(output.getCurrentEntry().cast(), entryStack.<dev.architectury.fluid.FluidStack>castValue().getAmount() / (float) maxAmount));
    TinkersCategory.setEntryTooltip(output, FLUID_TOOLTIP);
    output.getBounds().setSize(18, 34);
    widgets.add(output);

    // fuel
    Slot renderSlot = slot(94, 43, origin)
      .entries(EntryIngredients.of(VanillaEntryTypes.FLUID, TinkersCategory.toREIFluids(MeltingFuelHandler.getUsableFuels(display.getTemperature()))));
    TinkersCategory.setEntryTooltip(renderSlot, FUEL_TOOLTIP);
    renderSlot.getBounds().setSize(18, 18);
    widgets.add(renderSlot);

    widgets.add(tank.build(94, 43, origin));

    widgets.add(new ArrowWidget(point(90, 21, origin), BACKGROUND_LOC, 172, 0).animationDurationTicks(200));
  }
}
