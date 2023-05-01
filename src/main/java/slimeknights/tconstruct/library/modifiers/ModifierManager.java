package slimeknights.tconstruct.library.modifiers;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.github.fabricators_of_create.porting_lib.crafting.CraftingHelper;
import io.github.fabricators_of_create.porting_lib.event.BaseEvent;
import io.github.fabricators_of_create.porting_lib.event.common.ModsLoadedCallback;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import slimeknights.mantle.data.GenericLoaderRegistry;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.json.JsonRedirect;
import slimeknights.tconstruct.library.utils.GenericTagUtil;
import slimeknights.tconstruct.library.utils.JsonUtils;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.yaml.snakeyaml.tokens.Token.ID.Tag;

/** Modifier registry and JSON loader */
@Log4j2
public class ModifierManager extends SimpleJsonResourceReloadListener implements IdentifiableResourceReloadListener {
  /** Location of dynamic modifiers */
  public static final String FOLDER = "tinkering/modifiers";
  /** Location of modifier tags */
  public static final String TAG_FOLDER = "tinkering/tags/modifiers";
  /** Registry key to make tag keys */
  private static final ResourceKey<? extends Registry<Modifier>> REGISTRY_KEY = ResourceKey.createRegistryKey(TConstruct.getResource("modifiers"));

  /** GSON instance for loading dynamic modifiers */
  public static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

  /** ID of the default modifier */
  public static final ModifierId EMPTY = new ModifierId(TConstruct.MOD_ID, "empty");

  /** Singleton instance of the modifier manager */
  public static final ModifierManager INSTANCE = new ModifierManager();

  /** Default modifier to use when a modifier is not found */
  @Getter
  private final Modifier defaultValue;

  /** If true, static modifiers have been registered, so static modifiers can safely be fetched */
  @Getter
  private boolean modifiersRegistered = false;
  /** All modifiers registered directly with the manager */
  @VisibleForTesting
  final Map<ModifierId,Modifier> staticModifiers = new HashMap<>();
  /** Map of all modifier types that are expected to load in datapacks */
  private final Map<ModifierId,Class<?>> expectedDynamicModifiers = new HashMap<>();
  /** Map of all modifier types that are expected to load in datapacks */
  public static final GenericLoaderRegistry<Modifier> MODIFIER_LOADERS = new GenericLoaderRegistry<>();

  /** Modifiers loaded from JSON */
  private Map<ModifierId,Modifier> dynamicModifiers = Collections.emptyMap();
  /** Modifier tags loaded from JSON */
  private Map<ResourceLocation, Collection<Modifier>> tags = Collections.emptyMap();
  /** Map from modifier to tags on the modifier */
  private Map<ModifierId,Set<TagKey<Modifier>>> reverseTags = Collections.emptyMap();

  /** If true, dynamic modifiers have been loaded from datapacks, so its safe to fetch dynamic modifiers */
  @Getter
  boolean dynamicModifiersLoaded = false;
//  private IContext conditionContext = IContext.EMPTY;

  private ModifierManager() {
    super(GSON, FOLDER);
    // create the empty modifier
    defaultValue = new EmptyModifier();
    defaultValue.setId(EMPTY);
    staticModifiers.put(EMPTY, defaultValue);
  }

  /** For internal use only */
  @Deprecated
  public void init() {
    this.fireRegistryEvent();
    this.addDataPackListeners();
    ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> JsonUtils.syncPackets(player, joined, new UpdateModifiersPacket(this.dynamicModifiers, this.tags)));
  }

  /** Fires the modifier registry event */
  private void fireRegistryEvent() {
    ModsLoadedCallback.EVENT.register(envType -> new ModifierRegistrationEvent().sendEvent());
    modifiersRegistered = true;
  }

  /** Adds the managers as datapack listeners */
  private void addDataPackListeners() {
    ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(this);
//    conditionContext = event.getConditionContext();
  }

  @Override
  protected void apply(Map<ResourceLocation,JsonElement> splashList, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
    long time = System.nanoTime();

    // load modifiers from JSON
    Map<ModifierId,ModifierId> redirects = new HashMap<>();
    this.dynamicModifiers = splashList.entrySet().stream()
                                      .map(entry -> loadModifier(entry.getKey(), entry.getValue().getAsJsonObject(), redirects))
                                      .filter(Objects::nonNull)
                                      .collect(Collectors.toMap(Modifier::getId, mod -> mod));

    // process redirects
    Map<ModifierId,Modifier> resolvedRedirects = new HashMap<>(); // handled as a separate map to prevent redirects depending on order (no double redirects)
    for (Entry<ModifierId, ModifierId> redirect : redirects.entrySet()) {
      ModifierId from = redirect.getKey();
      ModifierId to = redirect.getValue();
      if (!contains(to)) {
        log.error("Invalid modifier redirect {} as modifier {} does not exist", from, to);
      } else {
        resolvedRedirects.put(from, get(to));
      }
    }
    int modifierSize = this.dynamicModifiers.size();
    this.dynamicModifiers.putAll(resolvedRedirects);

    // validate required modifiers
    for (Entry<ModifierId,Class<?>> entry : expectedDynamicModifiers.entrySet()) {
      Modifier modifier = dynamicModifiers.get(entry.getKey());
      if (modifier == null) {
        log.error("Missing expected modifier '" + entry.getKey() + "'");
      } else if (!entry.getValue().isInstance(modifier)) {
        log.error("Modifier '" + entry.getKey() + "' was loaded with the wrong class type. Expected " + entry.getValue().getName() + ", got " + modifier.getClass().getName());
      }
    }

    // TODO: this should be set back to false at some point
    dynamicModifiersLoaded = true;
    long timeStep = System.nanoTime();
    log.info("Loaded {} dynamic modifiers and {} modifier redirects in {} ms", modifierSize, redirects.size(), (timeStep - time) / 1000000f);

    // load modifier tags
    TagLoader<Modifier> tagLoader = new TagLoader<>(id -> {
      Modifier modifier = ModifierManager.getValue(new ModifierId(id));
      if (modifier == defaultValue) {
        return Optional.empty();
      }
      return Optional.of(modifier);
    }, TAG_FOLDER);
    this.tags = tagLoader.loadAndBuild(pResourceManager);
    this.reverseTags = GenericTagUtil.reverseTags(REGISTRY_KEY, Modifier::getId, tags);
    log.info("Loaded {} modifier tags for {} modifiers in {} ms", tags.size(), this.reverseTags.size(), (System.nanoTime() - timeStep) / 1000000f);

    new ModifiersLoadedEvent().sendEvent();
  }

  /** Loads a modifier from JSON */
  @Nullable
  private Modifier loadModifier(ResourceLocation key, JsonElement element, Map<ModifierId, ModifierId> redirects) {
    try {
      JsonObject json = GsonHelper.convertToJsonObject(element, "modifier");

      // processed first so a modifier can both conditionally redirect and fallback to a conditional modifier
      if (json.has("redirects")) {
        for (JsonRedirect redirect : JsonHelper.parseList(json, "redirects", JsonRedirect::fromJson)) {
          Predicate<JsonObject> redirectCondition = redirect.getConditionPredicate();
          if (redirectCondition == null || redirectCondition.test(json)) {
            ModifierId redirectTarget = new ModifierId(redirect.getId());
            log.debug("Redirecting modifier {} to {}", key, redirectTarget);
            redirects.put(new ModifierId(key), redirectTarget);
            return null;
          }
        }
      }

      // conditions
      if (json.has(ResourceConditions.CONDITIONS_KEY) && !CraftingHelper.getConditionPredicate(GsonHelper.getAsJsonObject(json, ResourceConditions.CONDITIONS_KEY)).test(json)) {
        return null;
      }

      // fallback to actual modifier
      Modifier modifier = MODIFIER_LOADERS.deserialize(json);
      modifier.setId(new ModifierId(key));
      return modifier;
    } catch (JsonSyntaxException e) {
      log.error("Failed to load modifier {}", key, e);
      return null;
    }
  }

  /** Updates the modifiers from the server */
  void updateModifiersFromServer(Map<ModifierId,Modifier> modifiers, Map<ResourceLocation,Collection<Modifier>> tags) {
    this.dynamicModifiers = modifiers;
    this.dynamicModifiersLoaded = true;
    this.tags = tags;
    this.reverseTags = GenericTagUtil.reverseTags(REGISTRY_KEY, Modifier::getId, tags);
    new ModifiersLoadedEvent().sendEvent();
  }


  /* Query the registry */

  /** Fetches a static modifier by ID, only use if you need access to modifiers before the world loads*/
  public Modifier getStatic(ModifierId id) {
    return staticModifiers.getOrDefault(id, defaultValue);
  }

  /** Checks if the given static modifier exists */
  public boolean containsStatic(ModifierId id) {
    return staticModifiers.containsKey(id) || expectedDynamicModifiers.containsKey(id);
  }

  /** Checks if the registry contains the given modifier */
  public boolean contains(ModifierId id) {
    return staticModifiers.containsKey(id) || dynamicModifiers.containsKey(id);
  }

  /** Gets the modifier for the given ID */
  public Modifier get(ModifierId id) {
    // highest priority is static modifiers, cannot be replaced
    Modifier modifier = staticModifiers.get(id);
    if (modifier != null) {
      return modifier;
    }
    // second priority is dynamic modifiers, fallback to the default
    return dynamicModifiers.getOrDefault(id, defaultValue);
  }

  /** Gets a list of all modifier IDs */
  public Stream<ResourceLocation> getAllLocations() {
    // filter out redirects (redirects are any modifiers where the ID does not match the key
    return Stream.concat(staticModifiers.entrySet().stream(), dynamicModifiers.entrySet().stream())
                 .filter(entry -> entry.getKey().equals(entry.getValue().getId()))
                 .map(Entry::getKey);
  }

  /** Gets a stream of all modifier values */
  public Stream<Modifier> getAllValues() {
    return Stream.concat(staticModifiers.values().stream(), dynamicModifiers.values().stream()).distinct();
  }


  /* Helpers */

  /** Gets the modifier for the given ID */
  public static Modifier getValue(ModifierId name) {
    return INSTANCE.get(name);
  }

  /**
   * Parses a modifier from JSON
   * @param element   Element to deserialize
   * @param key       Json key
   * @return  Registry value
   * @throws JsonSyntaxException  If something failed to parse
   */
  public static Modifier convertToModifier(JsonElement element, String key) {
    ModifierId name = new ModifierId(JsonHelper.convertToResourceLocation(element, key));
    if (INSTANCE.contains(name)) {
      return INSTANCE.get(name);
    }
    throw new JsonSyntaxException("Unknown modifier " + name);
  }

  /**
   * Parses a modifier from JSON
   * @param parent    Parent JSON object
   * @param key       Json key
   * @return  Registry value
   * @throws JsonSyntaxException  If something failed to parse
   */
  public static Modifier deserializeModifier(JsonObject parent, String key) {
    return convertToModifier(JsonHelper.getElement(parent, key), key);
  }

  /**
   * Reads a modifier from the buffer
   * @param buffer  Buffer instance
   * @return  Modifier instance
   */
  public static Modifier fromNetwork(FriendlyByteBuf buffer) {
    return INSTANCE.get(new ModifierId(buffer.readUtf(Short.MAX_VALUE)));
  }

  /**
   * Reads a modifier from the buffer
   * @param modifier  Modifier instance
   * @param buffer    Buffer instance
   */
  public static void toNetwork(Modifier modifier, FriendlyByteBuf buffer) {
    buffer.writeUtf(modifier.getId().toString());
  }

  @Override
  public ResourceLocation getFabricId() {
    return TConstruct.getResource("modifier_manager");
  }


  /* Tags */

  /** Creates a tag key for a modifier */
  public static TagKey<Modifier> getTag(ResourceLocation id) {
    return TagKey.create(REGISTRY_KEY, id);
  }

  /**
   * Checks if the given modifier is in the given tag
   * @return  True if the modifier is in the tag
   */
  public static boolean isInTag(ModifierId modifier, TagKey<Modifier> tag) {
    return INSTANCE.reverseTags.getOrDefault(modifier, Collections.emptySet()).contains(tag);
  }

  /**
   * Gets all values contained in the given tag
   * @param tag  Tag instance
   * @return  Contained values
   */
  public static List<Modifier> getTagValues(TagKey<Modifier> tag) {
    return INSTANCE.tags.getOrDefault(tag.location(), Collections.emptyList()).stream().toList();
  }


  /* Events */

  /** Event for registering modifiers */
  @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
  public class ModifierRegistrationEvent extends BaseEvent {

    public static Event<ModifierRegistrationCallback> EVENT = EventFactory.createArrayBacked(ModifierRegistrationCallback.class, callbacks -> event -> {
      for (ModifierRegistrationCallback e : callbacks)
        e.onRegistration(event);
    });

    /**
     * Registers a static modifier with the manager. Static modifiers cannot be configured by datapacks, so its generally encouraged to use dynamic modifiers
     * @param name      Modifier name
     * @param modifier  Modifier instance
     */
    public void registerStatic(ModifierId name, Modifier modifier) {
      // should not include under both types
      if (expectedDynamicModifiers.containsKey(name)) {
        throw new IllegalArgumentException(name + " is already expected as a dynamic modifier");
      }

      // set the name and register it
      modifier.setId(name);
      Modifier existing = staticModifiers.putIfAbsent(name, modifier);
      if (existing != null) {
        throw new IllegalArgumentException("Attempting to register a duplicate static modifier, this is not supported. Original value " + existing);
      }
    }

    /**
     * Registers that the given modifier is expected to be loaded in datapacks
     * @param name         Modifier name
     * @param classFilter  Class type the modifier is expected to have. Can be an interface
     */
    public void registerExpected(ModifierId name, Class<?> classFilter) {
      // should not include under both types
      if (staticModifiers.containsKey(name)) {
        throw new IllegalArgumentException(name + " is already registered as a static modifier");
      }

      // register it
      Class<?> existing = expectedDynamicModifiers.putIfAbsent(name, classFilter);
      if (existing != null) {
        throw new IllegalArgumentException("Attempting to register a duplicate expected modifier, this is not supported. Original value " + existing);
      }
    }

    @Override
    public void sendEvent() {
      EVENT.invoker().onRegistration(this);
    }
  }

  public interface ModifierRegistrationCallback {
    void onRegistration(ModifierRegistrationEvent event);
  }

  /** Event fired when modifiers reload */
  public static class ModifiersLoadedEvent extends BaseEvent {

    public static Event<ModifiersLoadedCallback> EVENT = EventFactory.createArrayBacked(ModifiersLoadedCallback.class, callbacks -> event -> {
      for (ModifiersLoadedCallback e : callbacks)
        e.onLoaded(event);
    });

    @Override
    public void sendEvent() {
      EVENT.invoker().onLoaded(this);
    }
  }

  public interface ModifiersLoadedCallback {
    void onLoaded(ModifiersLoadedEvent event);
  }

  /** Class for the empty modifier instance, mods should not need to extend this class */
  private static class EmptyModifier extends Modifier {
    @Override
    public boolean shouldDisplay(boolean advanced) {
      return false;
    }
  }
}
