package slimeknights.tconstruct.plugin.rei.casting;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.fluid.FluidStack;
import lombok.Getter;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.util.ClientEntryStacks;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import slimeknights.mantle.fluid.tooltip.FluidTooltipHandler;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import slimeknights.tconstruct.plugin.rei.TinkersCategory;
import slimeknights.tconstruct.plugin.rei.IRecipeTooltipReplacement;
import slimeknights.tconstruct.plugin.rei.widgets.ArrowWidget;
import slimeknights.tconstruct.plugin.rei.widgets.WidgetHolder;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public abstract class AbstractCastingCategory implements TinkersCategory<CastingDisplay>, IRecipeTooltipReplacement {
  private static final String KEY_COOLING_TIME = TConstruct.makeTranslationKey("jei", "time");
  private static final String KEY_CAST_KEPT = TConstruct.makeTranslationKey("jei", "casting.cast_kept");
  private static final String KEY_CAST_CONSUMED = TConstruct.makeTranslationKey("jei", "casting.cast_consumed");
  public static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/casting.png");

  @Getter
  private final WidgetHolder background;
  @Getter
  private final Renderer icon;
  private final WidgetHolder tankOverlay;
  private final WidgetHolder castConsumed;
  private final WidgetHolder castKept;
  private final WidgetHolder block;
//  private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;

  protected AbstractCastingCategory(Block icon, WidgetHolder block) {
    this.background = new WidgetHolder(BACKGROUND_LOC, 0, 0, 117, 54);
    this.icon = EntryStacks.of(icon);
    this.tankOverlay = new WidgetHolder(BACKGROUND_LOC, 133, 0, 32, 32);
    this.castConsumed = new WidgetHolder(BACKGROUND_LOC, 141, 32, 13, 11);
    this.castKept = new WidgetHolder(BACKGROUND_LOC, 141, 43, 13, 11);
    this.block = block;
  }

  @Override
  public void draw(CastingDisplay display, PoseStack matrixStack, double mouseX, double mouseY) {
    IDisplayableCastingRecipe recipe = display.getRecipe();

    int coolingTime = recipe.getCoolingTime() / 20;
    String coolingString = I18n.get(KEY_COOLING_TIME, coolingTime);
    Font fontRenderer = Minecraft.getInstance().font;
    int x = 72 - fontRenderer.width(coolingString) / 2;
    fontRenderer.draw(matrixStack, coolingString, x, 2, Color.GRAY.getRGB());
  }

  @Override
  public List<Component> getTooltipStrings(CastingDisplay display, List<Widget> widgets, double mouseX, double mouseY) {
    if (display.hasCast() && GuiUtil.isHovered((int)mouseX, (int)mouseY, 63, 39, 13, 11)) {
      return Collections.singletonList(Component.translatable(display.isConsumed() ? KEY_CAST_CONSUMED : KEY_CAST_KEPT));
    }
    return Collections.emptyList();
  }


  @Override
  public void addWidgets(CastingDisplay display, List<Widget> ingredients, Point origin, Rectangle bounds) {
    IDisplayableCastingRecipe recipe = display.getRecipe();
    // items
    List<ItemStack> casts = recipe.getCastItems();
    if (!casts.isEmpty()) {
      ingredients.add(slot(38, 19, origin).markInput().entries(EntryIngredients.ofItemStacks(casts)).disableBackground());
    }
    ingredients.add(slot(93, 18, origin).markOutput().entry(EntryStacks.of(recipe.getOutput())).disableBackground());

    // fluids
    // tank fluids
    long capacity = FluidValues.METAL_BLOCK;
    Slot input = slot(3, 3, origin).markInput()
      .disableBackground()
      .entries(EntryIngredients.of(VanillaEntryTypes.FLUID, TinkersCategory.toREIFluids(recipe.getFluids())));
    input.getEntries().forEach(entryStack -> ClientEntryStacks.setFluidRenderRatio(entryStack.cast(), entryStack.<FluidStack>castValue().getAmount() / (float) capacity));
    input.getBounds().setSize(34, 34);
    ingredients.add(input);
    ingredients.add(tankOverlay.build(3, 3, origin));
    // pouring fluid
    int h = 11;
    if (!display.hasCast()) {
      h += 16;
    }
    Slot renderInput = slot(43, 8, origin)
      .disableBackground()
      .entries(EntryIngredients.of(VanillaEntryTypes.FLUID, TinkersCategory.toREIFluids(recipe.getFluids())));
    renderInput.getBounds().setSize(8, h + 2);
    ingredients.add(renderInput);

    ingredients.add(new ArrowWidget(point(58, 18, origin), BACKGROUND_LOC, 117, 32).animationDurationTicks(Math.max(1, recipe.getCoolingTime())));
    ingredients.add(block.build(38, 35, origin));

    if (display.hasCast()) {
      ingredients.add((display.isConsumed() ? castConsumed : castKept).build(63, 39, origin));
    }
  }

  @Override
  public void addMiddleLines(Slot slot, List<Component> list) {
    if (slot.getCurrentEntry().getType() == VanillaEntryTypes.FLUID)
      FluidTooltipHandler.appendMaterial(TinkersCategory.fromREIFluid(slot.getCurrentEntry().castValue()), list);
  }
}
