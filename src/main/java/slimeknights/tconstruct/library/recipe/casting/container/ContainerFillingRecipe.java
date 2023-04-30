package slimeknights.tconstruct.library.recipe.casting.container;

import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.transfer.TransferUtil;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.casting.DisplayCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.ICastingContainer;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.util.Collections;
import java.util.List;

/**
 * Casting recipe that takes an arbitrary fluid for a given amount and fills a container
 */
@RequiredArgsConstructor
public abstract class ContainerFillingRecipe implements ICastingRecipe, IMultiRecipe<DisplayCastingRecipe> {
  @Getter
  protected final RecipeType<?> type;
  @Getter
  protected final ResourceLocation id;
  @Getter
  protected final String group;
  @Getter
  protected final int fluidAmount;
  @Getter
  protected final Item container;

  @Override
  public long getFluidAmount(ICastingContainer inv) {
    Fluid fluid = inv.getFluid();
    return TransferUtil.getFluidHandlerItem(inv.getStack())
              .map(handler -> handler.fill(new FluidStack(fluid, this.fluidAmount), true))
              .orElse(0L);
  }

  @Override
  public boolean isConsumed() {
    return true;
  }

  @Override
  public boolean switchSlots() {
    return false;
  }

  @Override
  public int getCoolingTime(ICastingContainer inv) {
    return 5;
  }

  @Override
  public boolean matches(ICastingContainer inv, Level worldIn) {
    ItemStack stack = inv.getStack();
    Fluid fluid = inv.getFluid();
    return stack.getItem() == this.container.asItem()
           && TransferUtil.getFluidHandlerItem(stack)
                   .filter(handler -> handler.fill(new FluidStack(fluid, this.fluidAmount), true) > 0)
                   .isPresent();
  }

  /** @deprecated use {@link ICastingRecipe#assemble(Container, RegistryAccess)} */
  @Override
  @Deprecated
  public ItemStack getResultItem(RegistryAccess registryAccess) {
    return new ItemStack(this.container);
  }

  @Override
  public ItemStack assemble(ICastingContainer inv, RegistryAccess registryAccess) {
    ItemStack stack = inv.getStack().copy();
    return TransferUtil.getFluidHandlerItem(stack).map(handler -> {
      handler.fill(new FluidStack(inv.getFluid(), this.fluidAmount, inv.getFluidTag()), false);
      return handler.getContainer();
    }).orElse(stack);
  }

  /* Display */
  /** Cache of items to display for this container */
  private List<DisplayCastingRecipe> displayRecipes = null;

  @Override
  public List<DisplayCastingRecipe> getRecipes() {
    if (displayRecipes == null) {
      List<ItemStack> casts = Collections.singletonList(new ItemStack(container));
      displayRecipes = Registry.FLUID.stream()
                                             .filter(fluid -> fluid.getBucket() != Items.AIR && fluid.isSource(fluid.defaultFluidState()))
                                             .map(fluid -> {
                                               FluidStack fluidStack = new FluidStack(fluid, fluidAmount);
                                               ItemStack stack = new ItemStack(container);
                                               stack = TransferUtil.getFluidHandlerItem(stack).map(handler -> {
                                                 handler.fill(fluidStack, false);
                                                 return handler.getContainer();
                                               }).orElse(stack);
                                               return new DisplayCastingRecipe(getType(), casts, Collections.singletonList(fluidStack), stack, 5, true);
                                             })
                                             .toList();
    }
    return displayRecipes;
  }

  /** Basin implementation */
  public static class Basin extends ContainerFillingRecipe {
    public Basin(ResourceLocation idIn, String groupIn, int fluidAmount, Item containerIn) {
      super(TinkerRecipeTypes.CASTING_BASIN.get(), idIn, groupIn, fluidAmount, containerIn);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.basinFillingRecipeSerializer.get();
    }
  }

  /** Table implementation */
  public static class Table extends ContainerFillingRecipe {

    public Table(ResourceLocation idIn, String groupIn, int fluidAmount, Item containerIn) {
      super(TinkerRecipeTypes.CASTING_TABLE.get(), idIn, groupIn, fluidAmount, containerIn);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.tableFillingRecipeSerializer.get();
    }
  }
}
