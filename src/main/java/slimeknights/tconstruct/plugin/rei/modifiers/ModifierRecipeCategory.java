package slimeknights.tconstruct.plugin.rei.modifiers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.fabricators_of_create.porting_lib.util.ForgeI18n;
import lombok.Getter;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.util.ClientEntryStacks;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.client.model.NBTKeyModel;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IDisplayModifierRecipe;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.SlotType.SlotCount;
import slimeknights.tconstruct.plugin.rei.TConstructREIConstants;
import slimeknights.tconstruct.plugin.rei.TinkersCategory;
import slimeknights.tconstruct.plugin.rei.widgets.WidgetHolder;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.item.CreativeSlotItem;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModifierRecipeCategory implements TinkersCategory<ModifierRecipeDisplay> {
  protected static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/tinker_station.png");
  private static final Component TITLE = TConstruct.makeTranslation("jei", "modifiers.title");

  // translation
  private static final List<Component> TEXT_FREE = Collections.singletonList(TConstruct.makeTranslation("jei", "modifiers.free"));
  private static final List<Component> TEXT_INCREMENTAL = Collections.singletonList(TConstruct.makeTranslation("jei", "modifiers.incremental"));
  private static final String KEY_SLOT = TConstruct.makeTranslationKey("jei", "modifiers.slot");
  private static final String KEY_SLOTS = TConstruct.makeTranslationKey("jei", "modifiers.slots");
  private static final String KEY_MAX = TConstruct.makeTranslationKey("jei", "modifiers.max");

  private final ModifierEntryRenderer modifierRenderer = new ModifierEntryRenderer(124, 10);

  @Getter
  private final WidgetHolder background;
  @Getter
  private final Renderer icon;
  private final String maxPrefix;
  private final WidgetHolder requirements, incremental;
  private final WidgetHolder[] slotIcons;
  private final Map<SlotType,TextureAtlasSprite> slotTypeSprites = new HashMap<>();
  public ModifierRecipeCategory() {
    this.maxPrefix = ForgeI18n.getPattern(KEY_MAX);
    this.background = new WidgetHolder(BACKGROUND_LOC, 0, 0, 128, 77);
    this.icon = EntryStacks.of(CreativeSlotItem.withSlot(new ItemStack(TinkerModifiers.creativeSlotItem), SlotType.UPGRADE));
    this.slotIcons = new WidgetHolder[6];
    for (int i = 0; i < 6; i++) {
      slotIcons[i] = new WidgetHolder(BACKGROUND_LOC, 128 + i * 16, 0, 16, 16);
    }
    this.requirements = new WidgetHolder(BACKGROUND_LOC, 128, 17, 16, 16);
    this.incremental = new WidgetHolder(BACKGROUND_LOC, 128, 33, 16, 16);
  }

  @Override
  public CategoryIdentifier<ModifierRecipeDisplay> getCategoryIdentifier() {
    return TConstructREIConstants.MODIFIERS;
  }

  @Override
  public Component getTitle() {
    return TITLE;
  }

  /** Draws a single slot icon */
  private void drawSlot(Point origin, List<Widget> ingredients, IDisplayModifierRecipe recipe, int slot, int x, int y) {
    List<ItemStack> stacks = recipe.getDisplayItems(slot);
    if (stacks.isEmpty()) {
      // -1 as the item list includes the output slot, we skip that
      ingredients.add(slotIcons[slot].build(x + 1, y + 1, origin));
    }
  }

  /** Draws the icon for the given slot type */
  private void drawSlotType(PoseStack matrices, @Nullable SlotType slotType, int x, int y) {
    Minecraft minecraft = Minecraft.getInstance();
    TextureAtlasSprite sprite;
    if (slotTypeSprites.containsKey(slotType)) {
      sprite = slotTypeSprites.get(slotType);
    } else {
      ModelManager modelManager = minecraft.getModelManager();
      // gets the model for the item, its a sepcial one that gives us texture info
      BakedModel model = minecraft.getItemRenderer().getItemModelShaper().getItemModel(TinkerModifiers.creativeSlotItem.get());
      if (model != null && model.getOverrides() instanceof NBTKeyModel.Overrides) {
        Material material = ((NBTKeyModel.Overrides)model.getOverrides()).getTexture(slotType == null ? "slotless" : slotType.getName());
        sprite = modelManager.getAtlas(material.atlasLocation()).getSprite(material.texture());
      } else {
        // failed to use the model, use missing texture
        sprite = modelManager.getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(MissingTextureAtlasSprite.getLocation());
      }
      slotTypeSprites.put(slotType, sprite);
    }
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

    Screen.blit(matrices, x, y, 0, 16, 16, sprite);
  }

  @Override
  public void draw(ModifierRecipeDisplay display, PoseStack matrices, double mouseX, double mouseY) {
    // draw max count
    Font fontRenderer = Minecraft.getInstance().font;
    int max = display.getMaxLevel();
    if (max > 0) {
      fontRenderer.draw(matrices, maxPrefix + max, 66, 16, Color.GRAY.getRGB());
    }

    // draw slot cost
    SlotCount slots = display.getSlots();
    if (slots == null) {
      drawSlotType(matrices, null, 110, 58);
    } else {
      drawSlotType(matrices, slots.getType(), 110, 58);
      String text = Integer.toString(slots.getCount());
      int x = 111 - fontRenderer.width(text);
      fontRenderer.draw(matrices, text, x, 63, Color.GRAY.getRGB());
    }
  }

  @Override
  public List<Component> getTooltipStrings(ModifierRecipeDisplay display, List<Widget> widgets, double mouseX, double mouseY) {
    int checkX = (int) mouseX;
    int checkY = (int) mouseY;
    if (display.hasRequirements() && GuiUtil.isHovered(checkX, checkY, 66, 58, 16, 16)) {
      return Collections.singletonList(Component.translatable(display.getRequirementsError()));
    } else if (display.isIncremental() && GuiUtil.isHovered(checkX, checkY, 83, 59, 16, 16)) {
      return TEXT_INCREMENTAL;
    } else if (GuiUtil.isHovered(checkX, checkY, 98, 58, 24, 16)) {
      // slot tooltip over icon
      SlotCount slots = display.getSlots();
      if (slots != null) {
        int count = slots.getCount();
        if (count == 1) {
          return Collections.singletonList(Component.translatable(KEY_SLOT, slots.getType().getDisplayName()));
        } else if (count > 1) {
          return Collections.singletonList(Component.translatable(KEY_SLOTS, slots, slots.getType().getDisplayName()));
        }
      } else {
        return TEXT_FREE;
      }
    }

    return Collections.emptyList();
  }

  @Override
  public void addWidgets(ModifierRecipeDisplay display, List<Widget> ingredients, Point origin, Rectangle bounds) {
    IDisplayModifierRecipe recipe = display.getRecipe();
    // inputs
    ingredients.add(slot( 3, 33, origin).markInput().entries(EntryIngredients.ofItemStacks(recipe.getDisplayItems(0))));
    ingredients.add(slot(25, 15, origin).markInput().entries(EntryIngredients.ofItemStacks(recipe.getDisplayItems(1))));
    ingredients.add(slot(47, 33, origin).markInput().entries(EntryIngredients.ofItemStacks(recipe.getDisplayItems(2))));
    ingredients.add(slot(43, 58, origin).markInput().entries(EntryIngredients.ofItemStacks(recipe.getDisplayItems(3))));
    ingredients.add(slot( 7, 58, origin).markInput().entries(EntryIngredients.ofItemStacks(recipe.getDisplayItems(4))));
    // modifiers
    Slot output = slot(3, 3, origin).markOutput()
      .entry(EntryStack.of(TConstructREIConstants.MODIFIER_TYPE, recipe.getDisplayResult()));
    ClientEntryStacks.setRenderer(output.getCurrentEntry(), modifierRenderer);
    output.getBounds().setSize(modifierRenderer.width(), modifierRenderer.height());
    ingredients.add(output);
    // tool
    ingredients.add(slot( 25, 38, origin).entries(EntryIngredients.ofItemStacks(recipe.getToolWithoutModifier())));
    ingredients.add(slot(105, 34, origin).entries(EntryIngredients.ofItemStacks(recipe.getToolWithModifier())));

    // TODO: still needed?
    // if focusing on a tool, filter out other tools
//    IFocus<ItemStack> focus = layout.getFocus(VanillaTypes.ITEM);
//    List<ItemStack> output = recipe.getToolWithModifier();
//    items.set(-1, output);
//    if (focus != null) {
//      Item item = focus.getValue().getItem();
//      if (TinkerTags.Items.MODIFIABLE.contains(item)) {
//        List<List<ItemStack>> allItems = recipe.getDisplayItems();
//        if (allItems.size() >= 1) {
//          allItems.get(0).stream().filter(stack -> stack.getItem() == item)
//                  .findFirst().ifPresent(stack -> items.set(0, stack));
//        }
//        output.stream().filter(stack -> stack.getItem() == item).findFirst().ifPresent(stack -> items.set(-1, stack));
//      }
//    }

    drawSlot(origin, ingredients, display.getRecipe(), 0,  2, 32);
    drawSlot(origin, ingredients, display.getRecipe(), 1, 24, 14);
    drawSlot(origin, ingredients, display.getRecipe(), 2, 46, 32);
    drawSlot(origin, ingredients, display.getRecipe(), 3, 42, 57);
    drawSlot(origin, ingredients, display.getRecipe(), 4,  6, 57);

    // draw info icons
    if (display.hasRequirements()) {
      ingredients.add(requirements.build(66, 58, origin));
    }
    if (display.isIncremental()) {
      ingredients.add(incremental.build(83, 59, origin));
    }
  }
}
