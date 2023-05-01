package slimeknights.tconstruct.plugin.rei.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.AbstractRenderer;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.util.ClientEntryStacks;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import slimeknights.mantle.fluid.tooltip.FluidTooltipHandler;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.plugin.jei.melting.MeltingFuelHandler;
import slimeknights.tconstruct.plugin.rei.IRecipeTooltipReplacement;
import slimeknights.tconstruct.plugin.rei.TConstructREIConstants;
import slimeknights.tconstruct.plugin.rei.TinkersCategory;
import slimeknights.tconstruct.plugin.rei.widgets.ArrowWidget;
import slimeknights.tconstruct.plugin.rei.widgets.WidgetHolder;

import java.awt.*;
import java.util.List;

/**
 * Entity melting display in REI
 */
public class EntityMeltingRecipeCategory implements TinkersCategory<EntityMeltingRecipeDisplay> {
  public static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/melting.png");
  private static final Component TITLE = TConstruct.makeTranslation("jei", "entity_melting.title");
  private static final String KEY_PER_HEARTS = TConstruct.makeTranslationKey("jei", "entity_melting.per_hearts");
  private static final Component TOOLTIP_PER_HEART = Component.translatable(TConstruct.makeTranslationKey("jei", "entity_melting.per_heart")).withStyle(ChatFormatting.GRAY);

  /** Map of damage value to tooltip callbacks */
  private static final Int2ObjectMap<IRecipeTooltipReplacement> TOOLTIP_MAP = new Int2ObjectOpenHashMap<>();

  /** Renderer instance to use in this category */
  private final EntityEntryRenderer entityRenderer = new EntityEntryRenderer(32);

  @Getter
  private final WidgetHolder background;
  private final WidgetHolder icon;
  private final WidgetHolder tank;

  public EntityMeltingRecipeCategory() {
    this.background = new WidgetHolder(BACKGROUND_LOC, 0, 41, 150, 62);
    this.icon = new WidgetHolder(BACKGROUND_LOC, 174, 41, 16, 16);
    this.tank = new WidgetHolder(BACKGROUND_LOC, 150, 74, 16, 16);
  }

  @Override
  public Renderer getIcon() {
    return new AbstractRenderer() {
      @Override
      public void render(PoseStack matrices, Rectangle bounds, int mouseX, int mouseY, float delta) {
        icon.build(0, 0, bounds.getLocation()).render(matrices, bounds, mouseX, mouseY, delta);
      }
    };
  }

  @Override
  public CategoryIdentifier<EntityMeltingRecipeDisplay> getCategoryIdentifier() {
    return TConstructREIConstants.ENTITY_MELTING;
  }

  @Override
  public Component getTitle() {
    return TITLE;
  }

  @Override
  public void draw(EntityMeltingRecipeDisplay display, PoseStack matrices, double mouseX, double mouseY) {
    // draw damage string next to the heart icon
    String damage = Float.toString(display.getDamage() / 2f);
    Font fontRenderer = Minecraft.getInstance().font;
    int x = 84 - fontRenderer.width(damage);
    fontRenderer.draw(matrices, damage, x, 8, Color.RED.getRGB());
  }

  @SuppressWarnings("rawtypes")
  @Override
  public void addWidgets(EntityMeltingRecipeDisplay display, List<Widget> ingredients, Point origin, Rectangle bounds) {
    // inputs, filtered by spawn egg item
    List<EntityType> displayTypes = EntityEntryDefinition.applyFocus(display.getRecipe().getEntityInputs());
    Slot input = slot(19, 11, origin).markInput()
      .entries(EntryIngredients.of(TConstructREIConstants.ENTITY_TYPE, displayTypes));
    input.getBounds().setSize(34, 34);
    ClientEntryStacks.setRenderer(input.getCurrentEntry(), entityRenderer);
    ingredients.add(input);
    // add spawn eggs as hidden inputs
//    builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStacks(display.getItemInputs());

    // output
    Slot output = slot(115, 11, origin).markOutput()
      .entries(display.getOutputEntries().get(0));
    output.getBounds().setSize(18, 34);
    output.getEntries().forEach(entryStack -> ClientEntryStacks.setFluidRenderRatio(entryStack.cast(), entryStack.<dev.architectury.fluid.FluidStack>castValue().getAmount() / (float) FluidValues.INGOT * 2));
    TinkersCategory.setEntryTooltip(output, TOOLTIP_MAP.computeIfAbsent(display.getDamage(), FluidTooltip::new));
    ingredients.add(output);

    // show fuels that are valid for this recipe
    Slot catalyst = slot(75, 43, origin)
      .entries(EntryIngredients.of(VanillaEntryTypes.FLUID, TinkersCategory.toREIFluids(MeltingFuelHandler.getUsableFuels(1))));
    catalyst.getBounds().setSize(18, 18);
    TinkersCategory.setEntryTooltip(catalyst, IRecipeTooltipReplacement.EMPTY);
    ingredients.add(catalyst);
    ingredients.add(tank.build(75, 43, origin));

    ingredients.add(new ArrowWidget(point(71, 21, origin), BACKGROUND_LOC, 150, 41).animationDurationTicks(200));
  }

  /** Tooltip for relevant damage on the fluid */
  private record FluidTooltip(int damage) implements IRecipeTooltipReplacement {
    @Override
    public void addMiddleLines(Slot recipeSlotView, List<Component> list) {
      // add fluid units
      if (recipeSlotView.getCurrentEntry().getType() == VanillaEntryTypes.FLUID)
        FluidTooltipHandler.appendMaterial(TinkersCategory.fromREIFluid(recipeSlotView.getCurrentEntry().castValue()), list);
      // output rate
      if (damage == 2) {
        list.add(TOOLTIP_PER_HEART);
      } else {
        list.add(Component.translatable(KEY_PER_HEARTS, damage / 2f).withStyle(ChatFormatting.GRAY));
      }
    }
  }
}

