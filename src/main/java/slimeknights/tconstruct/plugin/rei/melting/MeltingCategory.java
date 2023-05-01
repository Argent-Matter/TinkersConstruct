package slimeknights.tconstruct.plugin.rei.melting;

import dev.architectury.fluid.FluidStack;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.util.ClientEntryStacks;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.material.Fluid;
import slimeknights.mantle.fluid.tooltip.FluidTooltipHandler;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer.OreRateType;
import slimeknights.tconstruct.plugin.jei.melting.MeltingFuelHandler;
import slimeknights.tconstruct.plugin.rei.IRecipeTooltipReplacement;
import slimeknights.tconstruct.plugin.rei.TConstructREIConstants;
import slimeknights.tconstruct.plugin.rei.TinkersCategory;
import slimeknights.tconstruct.plugin.rei.widgets.WidgetHolder;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.entity.module.FuelModule;

import java.util.List;

/** Shared by melter and smeltery */
public class MeltingCategory extends AbstractMeltingCategory {
  private static final Component TITLE = TConstruct.makeTranslation("jei", "melting.title");
  private static final String KEY_TEMPERATURE = TConstruct.makeTranslationKey("jei", "temperature");
  private static final String KEY_MULTIPLIER = TConstruct.makeTranslationKey("jei", "melting.multiplier");
  private static final Component SOLID_TEMPERATURE = Component.translatable(KEY_TEMPERATURE, FuelModule.SOLID_TEMPERATURE).withStyle(ChatFormatting.GRAY);
  private static final Component SOLID_MULTIPLIER = Component.translatable(KEY_MULTIPLIER, FuelModule.SOLID_TEMPERATURE / 1000f).withStyle(ChatFormatting.GRAY);
  private static final Component TOOLTIP_SMELTERY = TConstruct.makeTranslation("jei", "melting.smeltery").withStyle(ChatFormatting.GRAY, ChatFormatting.UNDERLINE);
  private static final Component TOOLTIP_MELTER = TConstruct.makeTranslation("jei", "melting.melter").withStyle(ChatFormatting.GRAY, ChatFormatting.UNDERLINE);

  /** Tooltip callback for items */
  private static final IRecipeTooltipReplacement ITEM_FUEL_TOOLTIP = (slot, list) -> {
    list.add(1, SOLID_TEMPERATURE);
    list.add(2, SOLID_MULTIPLIER);
  };

  /** Tooltip callback for ores */
  private static final IRecipeTooltipReplacement METAL_ORE_TOOLTIP = new MeltingFluidCallback(OreRateType.METAL);
  private static final IRecipeTooltipReplacement GEM_ORE_TOOLTIP = new MeltingFluidCallback(OreRateType.GEM);

  @Getter
  private final Renderer icon;
  private final WidgetHolder solidFuel;

  public MeltingCategory() {
    super();
    this.icon = EntryStacks.of(TinkerSmeltery.searedMelter);
    this.solidFuel = new WidgetHolder(BACKGROUND_LOC, 164, 0, 18, 20);
  }

  @Override
  public CategoryIdentifier<MeltingDisplay> getCategoryIdentifier() {
    return TConstructREIConstants.MELTING;
  }

  @Override
  public Component getTitle() {
    return TITLE;
  }

  @Override
  public void addWidgets(MeltingDisplay display, List<Widget> ingredients, Point origin, Rectangle bounds) {
    super.addWidgets(display, ingredients, origin, bounds);

    // solid fuel slot
    int temperature = display.getTemperature();
    if (temperature <= FuelModule.SOLID_TEMPERATURE) {
      ingredients.add(solidFuel.build(1, 19, origin));
    }

    // input
    ingredients.add(slot(24, 18, origin).markInput().disableBackground().entries(display.getInputEntries().get(0)));

    // output
    OreRateType oreType = display.getOreType();
    IRecipeTooltipReplacement tooltip;
    if (oreType == OreRateType.METAL) {
      tooltip = METAL_ORE_TOOLTIP;
    } else if (oreType == OreRateType.GEM) {
      tooltip = GEM_ORE_TOOLTIP;
    } else {
      tooltip = MeltingFluidCallback.INSTANCE;
    }
    Slot output = slot(96, 4, origin).markOutput()
      .disableBackground()
      .entries(display.getOutputEntries().get(0));
    EntryStack<FluidStack> stack = output.getCurrentEntry().cast();
    ClientEntryStacks.setFluidRenderRatio(stack, stack.getValue().getAmount() / (float) FluidValues.METAL_BLOCK);
    TinkersCategory.setEntryTooltip(output, tooltip);
    output.getBounds().setSize(34, 34);
    ingredients.add(output);
    ingredients.add(tankOverlay.build(96, 4, origin));

    // show fuels that are valid for this recipe
    int fuelHeight = 32;
    // solid fuel
    if (display.getTemperature() <= FuelModule.SOLID_TEMPERATURE) {
      fuelHeight = 15;
      Slot renderSlot = slot(2, 22, origin)
        .disableBackground()
        .entries(EntryIngredients.ofItemStacks(MeltingFuelHandler.SOLID_FUELS.get()));
      TinkersCategory.setEntryTooltip(renderSlot, ITEM_FUEL_TOOLTIP);
      ingredients.add(renderSlot);
    }

    // liquid fuel
    Slot renderSlot = slot(4, 4, origin)
      .disableBackground()
      .entries(EntryIngredients.of(VanillaEntryTypes.FLUID, TinkersCategory.toREIFluids(MeltingFuelHandler.getUsableFuels(display.getTemperature()))));
    TinkersCategory.setEntryTooltip(renderSlot, FUEL_TOOLTIP);
    renderSlot.getBounds().setSize(14, fuelHeight + 2);
    ingredients.add(renderSlot);
  }

  /** Adds amounts to outputs and temperatures to fuels */
  @RequiredArgsConstructor
  private static class MeltingFluidCallback extends AbstractMeltingCategory.MeltingFluidCallback {
    @Getter
    private final OreRateType oreType;

    @Override
    protected boolean appendMaterial(FluidStack stack, List<Component> list) {
      Fluid fluid = stack.getFluid();
      long amount = stack.getAmount();
      long smelteryAmount = Config.COMMON.smelteryOreRate.applyOreBoost(oreType, amount);
      long melterAmount = Config.COMMON.melterOreRate.applyOreBoost(oreType, amount);
      if (smelteryAmount != melterAmount) {
        list.add(TOOLTIP_MELTER);
        boolean shift = FluidTooltipHandler.appendMaterialNoShift(fluid, melterAmount, list);
        list.add(TextComponent.EMPTY);
        list.add(TOOLTIP_SMELTERY);
        shift = FluidTooltipHandler.appendMaterialNoShift(fluid, smelteryAmount, list) || shift;
        return shift;
      } else {
        return FluidTooltipHandler.appendMaterialNoShift(fluid, smelteryAmount, list);
      }
    }
  }
}
