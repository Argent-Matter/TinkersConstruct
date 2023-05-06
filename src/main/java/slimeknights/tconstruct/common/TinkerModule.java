package slimeknights.tconstruct.common;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.serialization.Codec;
import io.github.fabricators_of_create.porting_lib.PortingLibRegistries;
import io.github.fabricators_of_create.porting_lib.loot.IGlobalLootModifier;
import io.github.fabricators_of_create.porting_lib.loot.LootModifierManager;
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.server.command.ModIdArgument;
import slimeknights.mantle.item.BlockTooltipItem;
import slimeknights.mantle.item.TooltipItem;
import slimeknights.mantle.registration.deferred.BlockEntityTypeDeferredRegister;
import slimeknights.mantle.registration.deferred.EntityTypeDeferredRegister;
import slimeknights.mantle.registration.deferred.FluidDeferredRegister;
import slimeknights.mantle.registration.deferred.MenuTypeDeferredRegister;
import slimeknights.mantle.util.SupplierCreativeTab;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.registration.BlockDeferredRegisterExtension;
import slimeknights.tconstruct.common.registration.ConfiguredFeatureDeferredRegister;
import slimeknights.tconstruct.common.registration.ItemDeferredRegisterExtension;
import slimeknights.tconstruct.common.registration.PlacedFeatureDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.TinkerStructures;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Contains base helpers for all Tinker modules. Should not be extended by other mods, this is only for internal usage.
 */
public abstract class TinkerModule {
  protected TinkerModule() {
    TConstruct.sealTinkersClass(this, "TinkerModule", "This is a bug with the mod containing that class, they should create their own deferred registers.");
  }

  // deferred register instances
  // gameplay singleton
  protected static final BlockDeferredRegisterExtension BLOCKS = new BlockDeferredRegisterExtension(TConstruct.MOD_ID);
  protected static final BlockDeferredRegisterExtension BLOCKS_DEFFERED = new BlockDeferredRegisterExtension(TConstruct.MOD_ID);
  protected static final ItemDeferredRegisterExtension ITEMS = new ItemDeferredRegisterExtension(TConstruct.MOD_ID);
  protected static final ItemDeferredRegisterExtension ITEMS_DEFFERED = new ItemDeferredRegisterExtension(TConstruct.MOD_ID);
  protected static final FluidDeferredRegister FLUIDS = new FluidDeferredRegister(TConstruct.MOD_ID);
  protected static final LazyRegistrar<MobEffect> MOB_EFFECTS = LazyRegistrar.create(Registry.MOB_EFFECT, TConstruct.MOD_ID);
  protected static final LazyRegistrar<ParticleType<?>> PARTICLE_TYPES = LazyRegistrar.create(Registry.PARTICLE_TYPE, TConstruct.MOD_ID);
  protected static final ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(TConstruct.MOD_ID);
  // gameplay instances
  protected static final BlockEntityTypeDeferredRegister BLOCK_ENTITIES = new BlockEntityTypeDeferredRegister(TConstruct.MOD_ID);
  protected static final EntityTypeDeferredRegister ENTITIES = new EntityTypeDeferredRegister(TConstruct.MOD_ID);
  protected static final MenuTypeDeferredRegister MENUS = new MenuTypeDeferredRegister(TConstruct.MOD_ID);
  // datapacks
  protected static final LazyRegistrar<RecipeSerializer<?>> RECIPE_SERIALIZERS = LazyRegistrar.create(Registry.RECIPE_SERIALIZER, TConstruct.MOD_ID);
  protected static final LazyRegistrar<Codec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIERS = LazyRegistrar.create(PortingLibRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS.get(), TConstruct.MOD_ID);
  protected static final LazyRegistrar<LootItemConditionType> LOOT_CONDITIONS = LazyRegistrar.create(Registry.LOOT_ITEM_REGISTRY, TConstruct.MOD_ID);
  protected static final LazyRegistrar<LootItemFunctionType> LOOT_FUNCTIONS = LazyRegistrar.create(Registry.LOOT_FUNCTION_REGISTRY, TConstruct.MOD_ID);
  protected static final LazyRegistrar<LootPoolEntryType> LOOT_ENTRIES = LazyRegistrar.create(Registry.LOOT_ENTRY_REGISTRY, TConstruct.MOD_ID);
  // worldgen
  protected static final LazyRegistrar<Feature<?>> FEATURES = LazyRegistrar.create(Registry.FEATURE, TConstruct.MOD_ID);
  protected static final ConfiguredFeatureDeferredRegister CONFIGURED_FEATURES = new ConfiguredFeatureDeferredRegister(TConstruct.MOD_ID);
  protected static final LazyRegistrar<StructureType<?>> STRUCTURE_TYPES = LazyRegistrar.create(Registry.STRUCTURE_TYPES, TConstruct.MOD_ID);
  protected static final LazyRegistrar<StructurePieceType> STRUCTURE_PIECE = LazyRegistrar.create(Registry.STRUCTURE_PIECE, TConstruct.MOD_ID);
  protected static final LazyRegistrar<BlockStateProviderType<?>> BLOCK_STATE_PROVIDER_TYPES = LazyRegistrar.create(Registry.BLOCKSTATE_PROVIDER_TYPES, TConstruct.MOD_ID);


  /** Creative tab for items that do not fit in another tab */
  @SuppressWarnings("WeakerAccess")
  public static final CreativeModeTab TAB_GENERAL = new SupplierCreativeTab(TConstruct.MOD_ID, "general", () -> new ItemStack(TinkerCommons.slimeball.get(SlimeType.SKY)));

  // base item properties
  protected static final Item.Properties HIDDEN_PROPS = new Item.Properties();
  protected static final Item.Properties GENERAL_PROPS = new Item.Properties().tab(TAB_GENERAL);
  protected static final Function<Block,? extends BlockItem> HIDDEN_BLOCK_ITEM = (b) -> new BlockItem(b, HIDDEN_PROPS);
  protected static final Function<Block,? extends BlockItem> GENERAL_BLOCK_ITEM = (b) -> new BlockItem(b, GENERAL_PROPS);
  protected static final Function<Block,? extends BlockItem> GENERAL_TOOLTIP_BLOCK_ITEM = (b) -> new BlockTooltipItem(b, GENERAL_PROPS);
  protected static final Supplier<Item> TOOLTIP_ITEM = () -> new TooltipItem(GENERAL_PROPS);

  /** Called during construction to initialize the registers for this mod */
  public static void initRegisters() {
    // gameplay singleton
    BLOCKS.register();
    BLOCKS_DEFFERED.register();
    ITEMS.register();
    ITEMS_DEFFERED.register();
    FLUIDS.register();
    MOB_EFFECTS.register();
    PARTICLE_TYPES.register();
    MODIFIERS.register();
    // gameplay instance
    BLOCK_ENTITIES.register();
    ENTITIES.register();
    MENUS.register();
    // datapacks
    RECIPE_SERIALIZERS.register();
    GLOBAL_LOOT_MODIFIERS.register();
    LOOT_CONDITIONS.register();
    LOOT_FUNCTIONS.register();
    LOOT_ENTRIES.register();
    TinkerRecipeTypes.init();
    // worldgen
    FEATURES.register();
    CONFIGURED_FEATURES.register();
    STRUCTURE_TYPES.register();
    STRUCTURE_PIECE.register();
    BLOCK_STATE_PROVIDER_TYPES.register();

    ArgumentTypeInfos.register(Registry.COMMAND_ARGUMENT_TYPE, "forge:mod_id", ModIdArgument.class, SingletonArgumentInfo.contextFree(ModIdArgument::modIdArgument));
  }

  /**
   * We use this builder to ensure that our blocks all have the most important properties set.
   * This way it'll stick out if a block doesn't have a tooltype or sound set.
   * It may be a bit less clear at first, since the actual builder methods tell you what each value means,
   * but as long as we don't statically import the enums it should be just as readable.
   */
  protected static BlockBehaviour.Properties builder(Material material, SoundType soundType) {
    return Block.Properties.of(material).sound(soundType);
  }

  /** Same as above, but with a color */
  protected static BlockBehaviour.Properties builder(Material material, MaterialColor color, SoundType soundType) {
    return Block.Properties.of(material, color).sound(soundType);
  }

  /** Builder that pre-supplies metal properties */
  protected static BlockBehaviour.Properties metalBuilder(MaterialColor color) {
    return builder(Material.METAL, color, SoundType.METAL).requiresCorrectToolForDrops().strength(5.0f);
  }

  /** Builder that pre-supplies glass properties */
  protected static BlockBehaviour.Properties glassBuilder(MaterialColor color) {
    return builder(Material.GLASS, color, SoundType.GLASS)
      .strength(0.3F).noOcclusion().isValidSpawn(Blocks::never)
      .isRedstoneConductor(Blocks::never).isSuffocating(Blocks::never).isViewBlocking(Blocks::never);
  }

  /** Builder that pre-supplies glass properties */
  protected static BlockBehaviour.Properties woodBuilder(MaterialColor color) {
    return builder(Material.WOOD, color, SoundType.WOOD).requiresCorrectToolForDrops().strength(2.0F, 7.0F);
  }

  /**
   * Creates a Tinkers Construct resource location
   * @param path  Resource path
   * @return  Tinkers Construct resource location
   */
  protected static ResourceLocation resource(String path) {
    return TConstruct.getResource(path);
  }
}
