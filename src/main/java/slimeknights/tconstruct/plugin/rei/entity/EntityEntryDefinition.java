package slimeknights.tconstruct.plugin.rei.entity;

import me.shedaniel.rei.api.client.entry.renderer.EntryRenderer;
import me.shedaniel.rei.api.client.gui.screen.DisplayScreen;
import me.shedaniel.rei.api.client.view.ViewSearchBuilder;
import me.shedaniel.rei.api.client.view.Views;
import me.shedaniel.rei.api.common.entry.EntrySerializer;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.comparison.ComparisonContext;
import me.shedaniel.rei.api.common.entry.type.EntryDefinition;
import me.shedaniel.rei.api.common.entry.type.EntryType;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.plugin.rei.TConstructREIConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/** Handler for working with entity types as ingredients */
public class EntityEntryDefinition implements EntryDefinition<EntityType> {

  @Override
  public Class<EntityType> getValueType() {
    return EntityType.class;
  }

  @Override
  public EntryType<EntityType> getType() {
    return TConstructREIConstants.ENTITY_TYPE;
  }

  @Override
  public EntryRenderer<EntityType> getRenderer() {
    return EntityEntryRenderer.INSTANCE;
  }

  @Override
  public ResourceLocation getIdentifier(EntryStack<EntityType> entry, EntityType type) {
    return Objects.requireNonNull(Registry.ENTITY_TYPE.getKey(type));
  }

  @Override
  public boolean isEmpty(EntryStack<EntityType> entry, EntityType type) {
    return false;
  }

  @Override
  public EntityType copy(EntryStack<EntityType> entry, EntityType type) {
    return type;
  }

  @Override
  public EntityType normalize(EntryStack<EntityType> entry, EntityType type) {
    return type;
  }

  @Override
  public EntityType wildcard(EntryStack<EntityType> entry, EntityType type) {
    return type;
  }

  @Override
  public long hash(EntryStack<EntityType> entry, EntityType type, ComparisonContext context) {
    return hashCode(Objects.requireNonNull(Registry.ENTITY_TYPE.getKey(type)).toString());
  }

  private static long hashCode(String id) {
    long h = 0;
    for (int i = 0; i < id.length(); i++) {
      h = 31 * h + id.charAt(i);
    }
    return h;
  }

  @Override
  public boolean equals(EntityType type1, EntityType type2, ComparisonContext context) {
    return Objects.equals(Objects.requireNonNull(Registry.ENTITY_TYPE.getKey(type1)).toString(), Objects.requireNonNull(Registry.ENTITY_TYPE.getKey(type2)).toString());
  }

  @Override
  public @Nullable EntrySerializer<EntityType> getSerializer() {
    return null;
  }

  @Override
  public Component asFormattedText(EntryStack<EntityType> entry, EntityType type) {
    return type.getDescription();
  }

  @Override
  public Stream<? extends TagKey<?>> getTagsFor(EntryStack<EntityType> entry, EntityType type) {
    return Stream.empty();
  }

  /** Applies the item focuses to the list of entities */
  public static List<EntityType> applyFocus(List<EntityType> displayInputs) {
    List<EntryStack<?>> notice = new ArrayList<>();
    ViewSearchBuilder context = Views.getInstance().getContext();
    if (context != null)
      notice = context.getUsagesFor();
    else if (Minecraft.getInstance().screen instanceof DisplayScreen displayScreen)
      notice = displayScreen.getIngredientsToNotice();
    return notice.stream().filter(stack -> stack != null && !stack.isEmpty() && stack.getType() == VanillaEntryTypes.ITEM)
      .map(entryStack -> ((ItemStack) entryStack.getValue()).getItem())
      .filter(item -> item instanceof SpawnEggItem)
      .<EntityType>map(item -> ((SpawnEggItem) item).getType(null))
      .filter(displayInputs::contains)
      .map(Collections::singletonList)
      .findFirst()
      .orElse(displayInputs);
  }
}
