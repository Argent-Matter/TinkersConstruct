package slimeknights.tconstruct.library.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.fabricmc.fabric.impl.resource.conditions.ResourceConditionsImpl;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/** Condition requiring that items exist in the intersection of all required item tags */
public class TagIntersectionPresentCondition<T> implements ConditionJsonProvider {
  public static final ResourceLocation NAME = TConstruct.getResource("tag_intersection_present");

  private final List<TagKey<T>> names;

  public TagIntersectionPresentCondition(List<TagKey<T>> names) {
    if (names.isEmpty()) {
      throw new IllegalArgumentException("Cannot create a condition with no names");
    }
    this.names = names;
  }

  /** Creates a condition from a set of keys */
  @SafeVarargs
  public static <T> TagIntersectionPresentCondition<T> ofKeys(TagKey<T>... names) {
    return new TagIntersectionPresentCondition<>(Arrays.asList(names));
  }

  /** Creates a condition from a registry and a set of names */
  public static <T> TagIntersectionPresentCondition<T> ofNames(ResourceKey<? extends Registry<T>> registry, ResourceLocation... names) {
    return new TagIntersectionPresentCondition<>(Arrays.stream(names).map(name -> TagKey.create(registry, name)).toList());
  }

  @Override
  public ResourceLocation getConditionId() {
    return NAME;
  }

  @Override
  public void writeParameters(JsonObject json) {
    JsonArray names = new JsonArray();
    json.addProperty("registry", this.names.get(0).registry().location().toString());
    for (TagKey<?> name : this.names) {
      names.add(name.location().toString());
    }
    json.add("tags", names);
  }

  public boolean test() {
    // if there is just one tag, just needs to be filled
    List<Collection<?>> tags = names.stream().map(tTagKey -> {
      for (Map.Entry<ResourceKey<?>, Map<ResourceLocation, Collection<Holder<?>>>> entry : ResourceConditionsImpl.LOADED_TAGS.get().entrySet()) {
        if (entry.getKey() == Registry.ITEM && entry.getValue(_REGISTRY).get(tTagKey.location()) != null)
          return entry.getValue().get(tTagKey.location());
      }
      return List.of();
    }).toList();
    if (tags.size() == 1) {
      return !tags.get(0).isEmpty();
    }
    // if any remaining tag is empty, give up
    int count = tags.size();
    for (int i = 1; i < count; i++) {
      if (tags.get(i).isEmpty()) {
        return false;
      }
    }

    // all tags have something, so find the first item that is in all tags
    itemLoop:
    for (Object entry : tags.get(0)) {
      // find the first item contained in all other intersection tags
      for (int i = 1; i < count; i++) {
        if (!tags.get(i).contains(entry)) {
          continue itemLoop;
        }
      }
      // all tags contain the item? success
      return true;
    }
    // no item in all tags
    return false;
  }

  public static <T> TagIntersectionPresentCondition<T> readGeneric(JsonObject json) {
    ResourceKey<Registry<T>> registry = ResourceKey.createRegistryKey(JsonHelper.getResourceLocation(json, "registry"));
    return new TagIntersectionPresentCondition<>(JsonHelper.parseList(json, "tags", (element, s) -> TagKey.create(registry, JsonHelper.convertToResourceLocation(element, s))));
  }
}
