package slimeknights.tconstruct.tools.client;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

import java.util.List;

import slimeknights.mantle.client.gui.GuiElement;
import slimeknights.mantle.client.gui.GuiElementScalable;
import slimeknights.mantle.client.gui.GuiModule;
import slimeknights.mantle.client.gui.GuiMultiModule;
import slimeknights.mantle.client.gui.GuiWidgetSlider;
import slimeknights.tconstruct.tools.client.module.GuiGeneric;

/**
 * Uses its area to display an inventory.
 * Adds a slider to scroll if needed.
 * Columns and Rows displayed are calculated from the space it takes up
 */
public class GuiDynInventory extends GuiModule {

  // Graphic Resources
  protected static final GuiElementScalable slot = GuiGeneric.slot;
  protected static final GuiElementScalable slotEmpty = GuiGeneric.slotEmpty;

  protected static final GuiElement sliderNormal = GuiGeneric.sliderNormal;
  protected static final GuiElement sliderLow = GuiGeneric.sliderLow;
  protected static final GuiElement sliderHigh = GuiGeneric.sliderHigh;
  protected static final GuiElement sliderTop = GuiGeneric.sliderTop;
  protected static final GuiElement sliderBottom = GuiGeneric.sliderBottom;
  protected static final GuiElementScalable sliderBackground = GuiGeneric.sliderBackground;

  private GuiWidgetSlider slider = new GuiWidgetSlider(sliderNormal, sliderHigh, sliderLow, sliderTop, sliderBottom, sliderBackground);

  // Logic
  private int columns; // columns displayed
  private int rows; // rows displayed
  private int slotCount;
  private boolean sliderActive;

  private int firstSlotId;
  private int lastSlotId;

  // Container containing the slots to display
  protected final Container container;

  public GuiDynInventory(GuiMultiModule parent, Container container) {
    super(parent, container, false, false);
    this.container = container;

    // default parameters.
    // These corresponds to a regular inventory
    xOffset = 7;
    yOffset = 17;
    xSize = 162;
    ySize = 54;

    slotCount = container.inventorySlots.size();
    firstSlotId = 0;
    lastSlotId = slotCount;
  }

  @Override
  public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
    this.guiLeft = parentX + xOffset;
    this.guiTop = parentY + yOffset;

    // calculate rows and columns from space
    columns = xSize / slot.w;
    rows = ySize / slot.h;

    sliderActive = slotCount > columns * rows;

    updateSlider();

    // recalculate columns with slider
    if(sliderActive) {
      columns = (xSize - slider.width) / slot.w;
    }

    updateSlots();
  }

  protected void updateSlider() {
    if(sliderActive) {
      slider.show();
    }
    else {
      slider.hide();
    }

    slider.setPosition(guiLeft + xSize - slider.width, guiTop);
    slider.setSize(ySize);
    slider.setSliderParameters(0, slotCount/columns - rows + 1, 1);
  }

  public void update(int mouseX, int mouseY) {
    if(!sliderActive) {
      return;
    }

    slider.update(mouseX, mouseY, !isMouseOverFullSlot(mouseX, mouseY) && isMouseInModule(mouseX, mouseY));
    updateSlots();
  }

  @Override
  public boolean shouldDrawSlot(Slot slot) {
    // all visible
    if(!slider.isEnabled()) {
      return true;
    }

    int index = slot.getSlotIndex();

    return (firstSlotId <= index && lastSlotId > index); // inside visible area
    //|| indexStart > index || indexEnd <= index; // or not our concern
  }

  // updates slot visibility
  public void updateSlots() {
    // calculate displayed slots
    firstSlotId = slider.getValue() * columns;
    lastSlotId = Math.min(slotCount, firstSlotId + rows * columns);

    for(Slot slot : (List<Slot>) container.inventorySlots) {
      if(shouldDrawSlot(slot)) {
        // calc position of the slot
        int offset = slot.getSlotIndex() - firstSlotId;
        int x = (offset % columns) * GuiDynInventory.slot.w;
        int y = (offset / columns) * GuiDynInventory.slot.h;

        slot.xDisplayPosition = xOffset + x + 1;
        slot.yDisplayPosition = yOffset + y + 1;
      }
      else {
        slot.xDisplayPosition = 0;
        slot.yDisplayPosition = 0;
      }
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    this.mc.getTextureManager().bindTexture(GuiGeneric.LOCATION);
    if(sliderActive) {
      slider.draw();

      updateSlots();
    }
    // fully filled rows
    int fullRows = (lastSlotId - firstSlotId) / columns;
    int w = columns * slot.w;
    int y;
    for(y = 0; y < fullRows * slot.h && y < ySize; y += slot.h) {
      slot.drawScaledX(guiLeft, guiTop + y, w);
    }

    // draw partial row and unused slots
    int slotsLeft = (lastSlotId - firstSlotId) % columns;
    if(slotsLeft > 0) {
      slot.drawScaledX(guiLeft, guiTop + y, slotsLeft * slot.w);
      // empty slots that don't exist
      slotEmpty.drawScaledX(guiLeft + slotsLeft * slot.w, guiTop + y, w - slotsLeft * slot.w);
    }
  }
}
