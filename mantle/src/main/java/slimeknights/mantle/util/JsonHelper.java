package slimeknights.mantle.util;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.network.NetworkWrapper;
import slimeknights.mantle.network.packet.ISimplePacket;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Utilities to help in parsing JSON
 */
@SuppressWarnings("unused")
public class JsonHelper {
  private JsonHelper() {}

  /**
   * Gets an element from JSON, throwing an exception if missing
   * @param json        Object parent
   * @param memberName  Name to get
   * @return  JsonElement found
   * @throws JsonSyntaxException if element is missing
   */
  public static JsonElement getElement(JsonObject json, String memberName) {
    if (json.has(memberName)) {
      return json.get(memberName);
    } else {
      throw new JsonSyntaxException("Missing " + memberName + "");
    }
  }

  /**
   * Parses a list from an JsonArray
   * @param array   Json array
   * @param name    Json key of the array
   * @param mapper  Mapper from the element object and name to new object
   * @param <T>     Output type
   * @return  List of output objects
   */
  public static <T> List<T> parseList(JsonArray array, String name, BiFunction<JsonElement,String,T> mapper) {
    if (array.size() == 0) {
      throw new JsonSyntaxException(name + " must have at least 1 element");
    }
    // build the list
    ImmutableList.Builder<T> builder = ImmutableList.builder();
    for (int i = 0; i < array.size(); i++) {
      builder.add(mapper.apply(array.get(i), name + "[" + i + "]"));
    }
    return builder.build();
  }

  /**
   * Parses a list from an JsonArray
   * @param array   Json array
   * @param name    Json key of the array
   * @param mapper  Mapper from the json object to new object
   * @param <T>     Output type
   * @return  List of output objects
   */
  public static <T> List<T> parseList(JsonArray array, String name, Function<JsonObject,T> mapper) {
    return parseList(array, name, (element, s) -> mapper.apply(GsonHelper.convertToJsonObject(element, s)));
  }

  /**
   * Parses a list from an JsonArray
   * @param parent  Parent JSON object
   * @param name    Json key of the array
   * @param mapper  Mapper from raw type to new object
   * @param <T>     Output type
   * @return  List of output objects
   */
  public static <T> List<T> parseList(JsonObject parent, String name, BiFunction<JsonElement,String,T> mapper) {
    return parseList(GsonHelper.getAsJsonArray(parent, name), name, mapper);
  }

  /**
   * Parses a list from an JsonArray
   * @param parent  Parent JSON object
   * @param name    Json key of the array
   * @param mapper  Mapper from json object to new object
   * @param <T>     Output type
   * @return  List of output objects
   */
  public static <T> List<T> parseList(JsonObject parent, String name, Function<JsonObject,T> mapper) {
    return parseList(GsonHelper.getAsJsonArray(parent, name), name, mapper);
  }

  /**
   * Gets a resource location from JSON, throwing a nice exception if invalid
   * @param json  JSON object
   * @param key   Key to fetch
   * @return  Resource location parsed
   */
  public static ResourceLocation getResourceLocation(JsonObject json, String key) {
    String text = GsonHelper.getAsString(json, key);
    ResourceLocation location = ResourceLocation.tryParse(text);
    if (location == null) {
      throw new JsonSyntaxException("Expected " + key + " to be a Resource location, was '" + text + "'");
    }
    return location;
  }

  /**
   * Gets a resource location from JSON, throwing a nice exception if invalid
   * @param json  JSON object
   * @param key   Key to fetch
   * @return  Resource location parsed
   */
  public static ResourceLocation convertToResourceLocation(JsonElement json, String key) {
    String text = GsonHelper.convertToString(json, key);
    ResourceLocation location = ResourceLocation.tryParse(text);
    if (location == null) {
      throw new JsonSyntaxException("Expected " + key + " to be a Resource location, was '" + text + "'");
    }
    return location;
  }

  /**
   * Parses a registry entry from JSON
   * @param registry  Registry
   * @param element   Element to deserialize
   * @param key       Json key
   * @param <T>  Object type
   * @return  Registry value
   * @throws JsonSyntaxException  If something failed to parse
   */
  public static <T> T convertToEntry(Registry<T> registry, JsonElement element, String key) {
    ResourceLocation name = JsonHelper.convertToResourceLocation(element, key);
    if (registry.containsKey(name)) {
      T value = registry.get(name);
      if (value != null) {
        return value;
      }
    }
    throw new JsonSyntaxException("Unknown " + registry + " " + name);
  }

  /**
   * Parses a registry entry from JSON
   * @param registry  Registry
   * @param parent    Parent JSON object
   * @param key       Json key
   * @param <T>  Object type
   * @return  Registry value
   * @throws JsonSyntaxException  If something failed to parse
   */
  public static <T> T getAsEntry(Registry<T> registry, JsonObject parent, String key) {
    return convertToEntry(registry, JsonHelper.getElement(parent, key), key);
  }

  /** Parses an enum from its name */
  private static <T extends Enum<T>> T enumByName(String name, Class<T> enumClass) {
    for (T value : enumClass.getEnumConstants()) {
      if (value.name().toLowerCase(Locale.ROOT).equals(name)) {
        return value;
      }
    }
    throw new JsonSyntaxException("Invalid " + enumClass.getSimpleName() + " " + name);
  }

  /** Gets an enum value from its string name */
  public static <T extends Enum<T>> T convertToEnum(JsonElement element, String key, Class<T> enumClass) {
    String name = GsonHelper.convertToString(element, key);
    return enumByName(name, enumClass);
  }

  /** Gets an enum value from its string name */
  public static <T extends Enum<T>> T getAsEnum(JsonObject json, String key, Class<T> enumClass) {
    String name = GsonHelper.getAsString(json, key);
    return enumByName(name, enumClass);
  }

  /**
   * Parses a color as a string
   * @param color  Color to parse
   * @return  Parsed string
   */
  public static int parseColor(@Nullable String color) {
    if (color == null || color.isEmpty()) {
      return -1;
    }
    // two options, 6 character or 8 character, must not start with - sign
    if (color.charAt(0) != '-') {
      try {
        // length of 8 must parse as long, supports transparency
        int length = color.length();
        if (length == 8) {
          return (int)Long.parseLong(color, 16);
        }
        if (length == 6) {
          return 0xFF000000 | Integer.parseInt(color, 16);
        }
      } catch (NumberFormatException ex) {
        // NO-OP
      }
    }
    throw new JsonSyntaxException("Invalid color '" + color + "'");
  }


  /* Resource loaders */

  /**
   * Converts the resource into a JSON file
   * @param resource  Resource to read. Closed when done
   * @return  JSON object, or null if failed to parse
   */
  @Nullable
  public static JsonObject getJson(Resource resource) {
    try (BufferedReader reader = resource.openAsReader()) {
      return GsonHelper.parse(reader);
    } catch (JsonParseException | IOException e) {
      Mantle.logger.error("Failed to load JSON from resource " + resource.sourcePackId(), e);
      return null;
    }
  }

  /** Gets a list of JSON objects for a single path in all domains and packs, for a language file like loader */
  public static List<JsonObject> getFileInAllDomainsAndPacks(ResourceManager manager, String path, @Nullable String preferredPath) {
    return manager
      .getNamespaces().stream()
      .filter(ResourceLocation::isValidNamespace)
      .flatMap(namespace -> {
        ResourceLocation location = new ResourceLocation(namespace, path);
        return manager.getResourceStack(location).stream();
      })
      .map(preferredPath != null ? resource -> {
        String loaded = resource.source().packId();
        Mantle.logger.warn("Using deprecated path {} in pack {} - use {}:{} instead", loaded, resource.sourcePackId(), loaded, preferredPath);
        return getJson(resource);
      } : JsonHelper::getJson)
      .filter(Objects::nonNull).toList();
  }

  /** Sends the packet to the given player */
  private static void sendPackets(NetworkWrapper network, ServerPlayer player, ISimplePacket[] packets) {
    // on an integrated server, the modifier registries have a single instance on both the client and the server thread
    // this means syncing is unneeded, and has the side-effect of recreating all the modifier instances (which can lead to unexpected behavior)
    // as a result, integrated servers just mark fullyLoaded as true without syncing anything, side-effect is listeners may run twice on single player

    // on a dedicated server, the client is running a separate game instance, this is where we send packets, plus fully loaded should already be true
    // this event is not fired when connecting to a server
    if (!player.connection.connection.isMemoryConnection()) {
      for (ISimplePacket packet : packets) {
        network.sendTo(packet, player);
      }
    }
  }

  /** Called when the player logs in to send packets */
  public static void syncPackets(ServerPlayer targetedPlayer, boolean joined, NetworkWrapper network, ISimplePacket... packets) {
    // send to single player
    sendPackets(network, targetedPlayer, packets);
  }
}
