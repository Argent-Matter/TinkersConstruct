package slimeknights.mantle.recipe.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.fabricators_of_create.porting_lib.crafting.AbstractIngredient;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import io.github.tropheusj.serialization_hooks.ingredient.IngredientDeserializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.transfer.TransferUtil;
import slimeknights.mantle.transfer.item.ItemHandlerHelper;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.mantle.util.JsonHelper;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.stream.Stream;

/** Ingredient that matches a container of fluid */
public class FluidContainerIngredient extends AbstractIngredient {
  public static final ResourceLocation ID = Mantle.getResource("fluid_container");
  public static final Serializer SERIALIZER = new Serializer();

  /** Ingredient to use for matching */
  private final FluidIngredient fluidIngredient;
  /** Internal ingredient to display the ingredient recipe viewers */
  @Nullable
  private final Ingredient display;
  private ItemStack[] displayStacks;
  protected FluidContainerIngredient(FluidIngredient fluidIngredient, @Nullable Ingredient display) {
    super(Stream.of());
    this.fluidIngredient = fluidIngredient;
    this.display = display;
  }

  /** Creates an instance from a fluid ingredient with a display container */
  public static FluidContainerIngredient fromIngredient(FluidIngredient ingredient, Ingredient display) {
    return new FluidContainerIngredient(ingredient, display);
  }

  /** Creates an instance from a fluid ingredient with no display, not recommended */
  public static FluidContainerIngredient fromIngredient(FluidIngredient ingredient) {
    return new FluidContainerIngredient(ingredient, null);
  }

  /** Creates an instance from a fluid ingredient with a display container */
  public static FluidContainerIngredient fromFluid(FluidObject<?> fluid, boolean forgeTag) {
    return fromIngredient(FluidIngredient.of(forgeTag ? fluid.getForgeTag() : fluid.getLocalTag(), FluidConstants.BUCKET), Ingredient.of(fluid));
  }

  @Override
  public boolean test(@Nullable ItemStack stack) {
    // first, must have a fluid capability
    return stack != null && !stack.isEmpty() && TransferUtil.getFluidHandlerItem(stack).resolve().flatMap(cap -> {
      // second, must contain enough fluid
      if (cap.getTanks() == 1) {
        FluidStack contained = cap.getFluidInTank(0);
        if (!contained.isEmpty() && fluidIngredient.getAmount(contained.getFluid()) == contained.getAmount() && fluidIngredient.test(contained.getFluid())) {
          // so far so good, from this point on we are forced to make copies as we need to try draining, so copy and fetch the copy's cap
          ItemStack copy = ItemHandlerHelper.copyStackWithSize(stack, 1);
          return TransferUtil.getFluidHandlerItem(copy).resolve();
        }
      }
      return Optional.empty();
    }).filter(cap -> {
      // alright, we know it has the fluid, the question is just whether draining the fluid will give us the desired result
      Fluid fluid = cap.getFluidInTank(0).getFluid();
      long amount = fluidIngredient.getAmount(fluid);
      FluidStack drained = cap.drain(amount, false);
      // we need an exact match, and we need the resulting container item to be the same as the item stack's container item
      return drained.getFluid() == fluid && drained.getAmount() == amount && ItemStack.matches(stack.getItem().getCraftingRemainingItem().getDefaultInstance(), cap.getContainer());
    }).isPresent();
  }

  @Override
  public ItemStack[] getItems() {
    if (displayStacks == null) {
      // no container? unfortunately hard to display this recipe so show nothing
      if (display == null) {
        displayStacks = new ItemStack[0];
      } else {
        displayStacks = display.getItems();
      }
    }
    return displayStacks;
  }

  @Override
  public JsonElement toJson() {
    JsonElement element = fluidIngredient.serialize();
    JsonObject json;
    if (element.isJsonObject()) {
      json = element.getAsJsonObject();
    } else {
      json = new JsonObject();
      json.add("fluid", element);
    }
    json.addProperty("type", ID.toString());
    if (display != null) {
      json.add("display", display.toJson());
    }
    return json;
  }

  @Override
  public IngredientDeserializer getDeserializer() {
    return SERIALIZER;
  }

  @Override
  public void toNetwork(FriendlyByteBuf buffer) {
    this.fluidIngredient.write(buffer);
    if (this.display != null) {
      buffer.writeBoolean(true);
      this.display.toNetwork(buffer);
    } else {
      buffer.writeBoolean(false);
    }
  }

  /** Serializer logic */
  private static class Serializer implements IngredientDeserializer {
    @Override
    public Ingredient fromJson(JsonObject json) {
      FluidIngredient fluidIngredient;
      // if we have fluid, its a nested ingredient. Otherwise this object itself is the ingredient
      if (json.has("fluid")) {
        fluidIngredient = FluidIngredient.deserialize(json, "fluid");
      } else {
        fluidIngredient = FluidIngredient.deserialize((JsonElement) json, "fluid");
      }
      Ingredient display = null;
      if (json.has("display")) {
        display = Ingredient.fromJson(JsonHelper.getElement(json, "display"));
      }
      return new FluidContainerIngredient(fluidIngredient, display);
    }

    @Override
    public Ingredient fromNetwork(FriendlyByteBuf buffer) {
      FluidIngredient fluidIngredient = FluidIngredient.read(buffer);
      Ingredient display = null;
      if (buffer.readBoolean()) {
        display = Ingredient.fromNetwork(buffer);
      }
      return new FluidContainerIngredient(fluidIngredient, display);
    }
  }
}
