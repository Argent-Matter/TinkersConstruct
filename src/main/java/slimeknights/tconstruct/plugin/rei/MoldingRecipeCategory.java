package slimeknights.tconstruct.plugin.rei;

import lombok.Getter;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.plugin.rei.widgets.WidgetHolder;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.util.Collections;
import java.util.List;

/** Recipe category for molding casts */
public class MoldingRecipeCategory implements TinkersCategory<MoldingRecipeDisplay> {
  private static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/casting.png");
  private static final Component TITLE = TConstruct.makeTranslation("jei", "molding.title");
  private static final Component TOOLTIP_PATTERN_CONSUMED = Component.translatable(TConstruct.makeTranslationKey("jei", "molding.pattern_consumed"));

  @Getter
  private final WidgetHolder background;
  @Getter
  private final Renderer icon;
  private final WidgetHolder table, basin, downArrow, upArrow;
  public MoldingRecipeCategory() {
    this.background = new WidgetHolder(BACKGROUND_LOC, 0, 55, 70, 57);
    this.icon = EntryStacks.of(TinkerSmeltery.blankSandCast.get());
    this.table = new WidgetHolder(BACKGROUND_LOC, 117, 0, 16, 16);
    this.basin = new WidgetHolder(BACKGROUND_LOC, 117, 16, 16, 16);
    this.downArrow = new WidgetHolder(BACKGROUND_LOC, 70, 55, 6, 6);
    this.upArrow = new WidgetHolder(BACKGROUND_LOC, 76, 55, 6, 6);
  }

  @Override
  public CategoryIdentifier<MoldingRecipeDisplay> getCategoryIdentifier() {
    return TConstructREIConstants.MOLDING;
  }

  @Override
  public Component getTitle() {
    return TITLE;
  }

  @Override
  public List<Component> getTooltipStrings(MoldingRecipeDisplay recipe, List<Widget> slots, double mouseX, double mouseY) {
    if (recipe.isPatternConsumed() && !recipe.getPattern().isEmpty() && GuiUtil.isHovered((int)mouseX, (int)mouseY, 50, 7, 18, 18)) {
      return Collections.singletonList(TOOLTIP_PATTERN_CONSUMED);
    }
    return Collections.emptyList();
  }

  @Override
  public void addWidgets(MoldingRecipeDisplay display, List<Widget> ingredients, Point origin, Rectangle bounds) {
    // basic input output
    ingredients.add(slot(3, 24, origin).markInput().entries(display.getInputEntries().get(0)));
    ingredients.add(slot(51, 24, origin).markOutput().entries(display.getOutputEntries().get(0)));

    // if we have a mold, we are pressing into the table, so draw pressed item on input and output
    Ingredient pattern = display.getPattern();
    if (!pattern.isEmpty()) {
      Slot inputSlot = slot(3, 1, origin).markInput().entries(EntryIngredients.ofIngredient(pattern));
      ingredients.add(inputSlot);
      if (!display.isPatternConsumed()) {
        Slot preservedSlot = slot(51, 8, origin).entries(EntryIngredients.ofIngredient(pattern));
//        builder.createFocusLink(inputSlot, preservedSlot);
        ingredients.add(preservedSlot);
      }
    }

    // draw the main block
    WidgetHolder block = display.getType() == TinkerRecipeTypes.MOLDING_BASIN.get() ? basin : table;
    ingredients.add(block.build(3, 40, origin));

    // if no mold, we "pickup" the item, so draw no table
    if (!display.getPattern().isEmpty()) {
      ingredients.add(block.build(51, 40, origin));
      ingredients.add(downArrow.build(8, 17, origin));
    } else {
      ingredients.add(upArrow.build(8, 17, origin));
    }
  }
}
