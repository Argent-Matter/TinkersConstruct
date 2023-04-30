package slimeknights.tconstruct.library.json;

import com.google.common.collect.Iterables;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;

import java.util.Arrays;
import java.util.List;

/** Condition requiring that items exist in the intersection of all required item tags */
public class TagDifferencePresentCondition<T> implements ConditionJsonProvider {
  public static final ResourceLocation NAME = TConstruct.getResource("tag_difference_present");

  private final TagKey<Item> base;
  private final List<TagKey<Item>> subtracted;

  public TagDifferencePresentCondition(TagKey<Item> base, List<TagKey<Item>> subtracted) {
    if (subtracted.isEmpty()) {
      throw new IllegalArgumentException("Cannot create a condition with no subtracted");
    }
    this.base = base;
    this.subtracted = subtracted;
  }

  /** Creates a condition from a set of keys */
  @SafeVarargs
  public static TagDifferencePresentCondition<Item> ofKeys(TagKey<Item> base, TagKey<Item>... subtracted) {
    return new TagDifferencePresentCondition<>(base, Arrays.asList(subtracted));
  }

  /** Creates a condition from a registry and a set of names */
  public static TagDifferencePresentCondition ofNames(ResourceKey<? extends Registry<Item>> registry, ResourceLocation base, ResourceLocation... subtracted) {
    TagKey<Item> baseKey = TagKey.create(registry, base);
    return new TagDifferencePresentCondition<>(baseKey, Arrays.stream(subtracted).map(name -> TagKey.create(registry, name)).toList());
  }

  @Override
  public ResourceLocation getConditionId() {
    return NAME;
  }

  public boolean test() {
    // get the base tag
    Iterable<Holder<Item>> base = Registry.ITEM.getTagOrEmpty(this.base);
    if (base == null || Iterables.isEmpty(base)) {
      return false;
    }

    // get subtracted tags
    //List<Tag<Item>> subtracted = this.subtracted.stream().map(itemTags::getTag).filter(tag -> tag == null || tag.getValues().isEmpty()).toList();
    // none of the subtracted tags had anything? done
    if (subtracted.isEmpty()) {
      return true;
    }
    // all tags have something, so find the first item that is in all tags
    itemLoop:
    for (Holder<Item> entry : base) {
      // find the first item contained in no subtracted tags
      for (TagKey<Item> tag : subtracted) {
        // TODO: will this work?
        if (Iterables.contains(Registry.ITEM.getTagOrEmpty(tag), entry)) {
          continue itemLoop;
        }
      }
      // no subtracted contains the item? success
      return true;
    }
    // no item not in any subtracted
    return false;
  }

  @Override
  public void writeParameters(JsonObject json) {
    json.addProperty("registry", Registry.ITEM.location(_REGISTRY).toString());
    json.addProperty("base", this.base.location().toString());
    JsonArray names = new JsonArray();
    for (TagKey<?> name : this.subtracted) {
      names.add(name.location().toString());
    }
    json.add("subtracted", names);
  }

  public static TagDifferencePresentCondition readGeneric(JsonObject json) {
    ResourceKey<Registry<Item>> registry = ResourceKey.createRegistryKey(JsonHelper.getResourceLocation(json, "registry"));
    return new TagDifferencePresentCondition<>(
      TagKey.create(registry, JsonHelper.getResourceLocation(json, "base")),
      JsonHelper.parseList(json, "subtracted", (e, s) -> TagKey.create(registry, JsonHelper.convertToResourceLocation(e, s))));
  }
}
