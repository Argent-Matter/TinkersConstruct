package slimeknights.tconstruct.fluids;

import io.github.fabricators_of_create.porting_lib.brewing.BrewingRecipe;
import io.github.fabricators_of_create.porting_lib.brewing.BrewingRecipeRegistry;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import io.github.tropheusj.milk.Milk;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.material.Material;
import slimeknights.mantle.fluid.UnplaceableFluid;
import slimeknights.mantle.fluid.attributes.FluidAttributes;
import slimeknights.mantle.registration.ItemProperties;
import slimeknights.mantle.registration.ModelFluidAttributes;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.mantle.util.SimpleFlowableFluid;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.fluids.fluids.DirectionalSlimeFluid;
import slimeknights.tconstruct.fluids.fluids.PotionFluidAttributes;
import slimeknights.tconstruct.fluids.fluids.SlimeFluid;
import slimeknights.tconstruct.fluids.item.BottleItem;
import slimeknights.tconstruct.fluids.item.ContainerFoodItem;
import slimeknights.tconstruct.fluids.item.ContainerFoodItem.FluidContainerFoodItem;
import slimeknights.tconstruct.fluids.item.PotionBucketItem;
import slimeknights.tconstruct.fluids.util.BottleBrewingRecipe;
import slimeknights.tconstruct.fluids.util.EmptyBottleIntoEmpty;
import slimeknights.tconstruct.fluids.util.EmptyBottleIntoWater;
import slimeknights.tconstruct.fluids.util.FillBottle;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.EnumMap;
import java.util.Map;

/**
 * Contains all fluids used throughout the mod
 */
public final class TinkerFluids extends TinkerModule {
  public TinkerFluids() {
    Milk.enableMilkFluid();
    commonSetup();
  }

  // basic
  public static final FluidObject<SimpleFlowableFluid> blood = FLUIDS.register("blood", coolBuilder().density(1200).viscosity(1200).temperature(336), Material.WATER, 0);
  public static final FluidObject<SimpleFlowableFluid> venom = FLUIDS.register("venom", coolBuilder().density(1400).viscosity(1300).temperature(310), Material.WATER, 0);
  public static final ItemObject<Item> venomBottle = ITEMS.register("venom_bottle", () -> new FluidContainerFoodItem(
    new Item.Properties().food(new FoodProperties.Builder().alwaysEat()
      .effect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1800), 1.0f)
      .effect(new MobEffectInstance(MobEffects.POISON, 450), 1.0f)
      .build()).tab(TAB_GENERAL).stacksTo(1).craftRemainder(Items.GLASS_BOTTLE),
    () -> new FluidStack(venom.get(), FluidValues.BOTTLE))
  );

  // slime -  note second name parameter is forge tag name
  public static final FluidObject<SimpleFlowableFluid> earthSlime = FLUIDS.register("earth_slime", "slime",  coolBuilder().density(1400).viscosity(1400).temperature(350), SlimeFluid.Source::new, SlimeFluid.Flowing::new, Material.WATER, 0);
  public static final FluidObject<SimpleFlowableFluid> skySlime   = FLUIDS.register("sky_slime",             coolBuilder().density(1500).viscosity(1500).temperature(310), SlimeFluid.Source::new, SlimeFluid.Flowing::new, Material.WATER, 0);
  public static final FluidObject<SimpleFlowableFluid> enderSlime = FLUIDS.register("ender_slime",           coolBuilder().density(1600).viscosity(1600).temperature(370), SlimeFluid.Source::new, SlimeFluid.Flowing::new, Material.WATER, 0);
  public static final FluidObject<SimpleFlowableFluid> magma      = FLUIDS.register("magma",                 coolBuilder().density(1900).viscosity(1900).temperature(600), SlimeFluid.Source::new, SlimeFluid.Flowing::new, Material.WATER, 3);
  public static final FluidObject<DirectionalSlimeFluid> ichor    = FLUIDS.registerUpsideDown("ichor",       hotBuilder().density(-1200).viscosity(1900).temperature(1000), DirectionalSlimeFluid.Source::new, DirectionalSlimeFluid.Flowing::new, Material.WATER, 3);
  public static final Map<SlimeType, FluidObject<SimpleFlowableFluid>> slime;
  static {
    slime = new EnumMap<>(SlimeType.class);
    slime.put(SlimeType.EARTH, earthSlime);
    slime.put(SlimeType.SKY, skySlime);
    slime.put(SlimeType.ENDER, enderSlime);
    slime.put(SlimeType.BLOOD, blood);
  }
  // bottles of slime
  public static final EnumObject<SlimeType, Item> slimeBottle = new EnumObject.Builder<SlimeType,Item>(SlimeType.class)
    .put(SlimeType.EARTH, ITEMS.register("earth_slime_bottle", () -> new FluidContainerFoodItem(
      new Item.Properties().food(new FoodProperties.Builder().alwaysEat()
        .effect(new MobEffectInstance(MobEffects.LUCK, 1500), 1.0f)
        .effect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 900), 1.0f)
        .build()).tab(TAB_GENERAL).stacksTo(1).craftRemainder(Items.GLASS_BOTTLE),
      () -> new FluidStack(earthSlime.get(), FluidValues.BOTTLE))))
    .put(SlimeType.SKY, ITEMS.register("sky_slime_bottle", () -> new FluidContainerFoodItem(
      new Item.Properties().food(new FoodProperties.Builder().alwaysEat()
        .effect(new MobEffectInstance(MobEffects.JUMP, 1800), 1.0f)
        .effect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 900), 1.0f)
        .build()).tab(TAB_GENERAL).stacksTo(1).craftRemainder(Items.GLASS_BOTTLE),
      () -> new FluidStack(skySlime.get(), FluidValues.BOTTLE))))
    .put(SlimeType.ENDER, ITEMS.register("ender_slime_bottle", () -> new FluidContainerFoodItem(
      new Item.Properties().food(new FoodProperties.Builder().alwaysEat()
        .effect(new MobEffectInstance(MobEffects.LEVITATION, 450), 1.0f)
        .effect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 900), 1.0f)
        .build()).tab(TAB_GENERAL).stacksTo(1).craftRemainder(Items.GLASS_BOTTLE),
      () -> new FluidStack(enderSlime.get(), FluidValues.BOTTLE))))
    .put(SlimeType.ICHOR, ITEMS.register("ichor_bottle", () -> new ContainerFoodItem(
      new Item.Properties().food(new FoodProperties.Builder().alwaysEat()
        .effect(new MobEffectInstance(MobEffects.ABSORPTION, 500), 1.0f)
        .effect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 900), 1.0f)
        .build()).tab(TAB_GENERAL).stacksTo(1).craftRemainder(Items.GLASS_BOTTLE))))
    .put(SlimeType.BLOOD, ITEMS.register("blood_bottle", () -> new FluidContainerFoodItem(
      new Item.Properties().food(new FoodProperties.Builder()
        .nutrition(6).saturationMod(0.1F)
        .effect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 600), 0.8f)
        .build()).tab(TAB_GENERAL).stacksTo(16).craftRemainder(Items.GLASS_BOTTLE),
      () -> new FluidStack(blood.get(), FluidValues.BOTTLE))))
    .build();
  public static final ItemObject<Item> magmaBottle = ITEMS.register("magma_bottle", () -> new FluidContainerFoodItem(
    new Item.Properties().food(new FoodProperties.Builder().alwaysEat()
      .effect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 3600), 1.0f)
      .build()).tab(TAB_GENERAL).stacksTo(1).craftRemainder(Items.GLASS_BOTTLE),
    () -> new FluidStack(magma.get(), FluidValues.BOTTLE)));

  // foods
  public static FluidObject<SimpleFlowableFluid> honey        = FLUIDS.register("honey",         coolBuilder().temperature(301), Material.WATER, 0);
  public static FluidObject<SimpleFlowableFluid> beetrootSoup = FLUIDS.register("beetroot_soup", coolBuilder().temperature(400), Material.WATER, 0);
  public static FluidObject<SimpleFlowableFluid> mushroomStew = FLUIDS.register("mushroom_stew", coolBuilder().temperature(400), Material.WATER, 0);
  public static FluidObject<SimpleFlowableFluid> rabbitStew   = FLUIDS.register("rabbit_stew",   coolBuilder().temperature(400), Material.WATER, 0);

  // potion
  public static final ItemObject<PotionBucketItem> potionBucket = ITEMS.register("potion_bucket", () -> new PotionBucketItem(TinkerFluids.potion, ItemProperties.BUCKET_PROPS));
  public static final RegistryObject<UnplaceableFluid> potion = FLUIDS.registerFluid("potion", () -> new UnplaceableFluid(potionBucket, PotionFluidAttributes.builder(TConstruct.getResource("block/fluid/potion/")).sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY).density(1100).viscosity(1100).temperature(315)));
  public static final ItemObject<Item> splashBottle = ITEMS.register("splash_bottle", () -> new BottleItem(Items.SPLASH_POTION, GENERAL_PROPS));
  public static final ItemObject<Item> lingeringBottle = ITEMS.register("lingering_bottle", () -> new BottleItem(Items.LINGERING_POTION, GENERAL_PROPS));

  // base molten fluids
  public static final FluidObject<SimpleFlowableFluid> searedStone   = FLUIDS.register("seared_stone",   hotBuilder().temperature( 900), Material.LAVA,  6);
  public static final FluidObject<SimpleFlowableFluid> scorchedStone = FLUIDS.register("scorched_stone", hotBuilder().temperature( 800), Material.LAVA,  4);
  public static final FluidObject<SimpleFlowableFluid> moltenClay    = FLUIDS.register("molten_clay",    hotBuilder().temperature( 750), Material.LAVA,  3);
  public static final FluidObject<SimpleFlowableFluid> moltenGlass   = FLUIDS.register("molten_glass",   hotBuilder().temperature(1050), Material.LAVA,  1);
  public static final FluidObject<SimpleFlowableFluid> liquidSoul    = FLUIDS.register("liquid_soul",    hotBuilder().temperature( 700), Material.LAVA,  2);
  // ceramics compat
  public static final FluidObject<SimpleFlowableFluid> moltenPorcelain = FLUIDS.register("molten_porcelain", hotBuilder().temperature(1000), Material.LAVA, 2);
  // fancy molten fluids
  public static final FluidObject<SimpleFlowableFluid> moltenObsidian = FLUIDS.register("molten_obsidian", hotBuilder().temperature(1300), Material.LAVA, 3);
  public static final FluidObject<SimpleFlowableFluid> moltenEnder    = FLUIDS.register("molten_ender", "ender", hotBuilder().temperature( 777), Material.LAVA, 5);
  public static final FluidObject<SimpleFlowableFluid> blazingBlood   = FLUIDS.register("blazing_blood",   hotBuilder().temperature(1800).density(3500), Material.LAVA, 15);

  // ores
  public static final FluidObject<SimpleFlowableFluid> moltenEmerald  = FLUIDS.register("molten_emerald",  hotBuilder().temperature(1234), Material.LAVA,  9);
  public static final FluidObject<SimpleFlowableFluid> moltenQuartz   = FLUIDS.register("molten_quartz",   hotBuilder().temperature( 937), Material.LAVA,  6);
  public static final FluidObject<SimpleFlowableFluid> moltenAmethyst = FLUIDS.register("molten_amethyst", hotBuilder().temperature(1250), Material.LAVA, 11);
  public static final FluidObject<SimpleFlowableFluid> moltenDiamond  = FLUIDS.register("molten_diamond",  hotBuilder().temperature(1750), Material.LAVA, 13);
  public static final FluidObject<SimpleFlowableFluid> moltenDebris   = FLUIDS.register("molten_debris",   hotBuilder().temperature(1475), Material.LAVA, 14);
  // metal ores
  public static final FluidObject<SimpleFlowableFluid> moltenIron   = FLUIDS.register("molten_iron",   hotBuilder().temperature(1100), Material.LAVA, 12);
  public static final FluidObject<SimpleFlowableFluid> moltenGold   = FLUIDS.register("molten_gold",   hotBuilder().temperature(1000), Material.LAVA, 12);
  public static final FluidObject<SimpleFlowableFluid> moltenCopper = FLUIDS.register("molten_copper", hotBuilder().temperature( 800), Material.LAVA, 12);
  public static final FluidObject<SimpleFlowableFluid> moltenCobalt = FLUIDS.register("molten_cobalt", hotBuilder().temperature(1250), Material.LAVA,  8);
  // alloys
  public static final FluidObject<SimpleFlowableFluid> moltenSlimesteel     = FLUIDS.register("molten_slimesteel",      hotBuilder().temperature(1200), Material.LAVA, 10);
  public static final FluidObject<SimpleFlowableFluid> moltenAmethystBronze = FLUIDS.register("molten_amethyst_bronze", hotBuilder().temperature(1120), Material.LAVA, 12);
  public static final FluidObject<SimpleFlowableFluid> moltenRoseGold       = FLUIDS.register("molten_rose_gold",       hotBuilder().temperature( 850), Material.LAVA, 12);
  public static final FluidObject<SimpleFlowableFluid> moltenPigIron        = FLUIDS.register("molten_pig_iron",        hotBuilder().temperature(1111), Material.LAVA, 10);

  public static final FluidObject<SimpleFlowableFluid> moltenManyullyn   = FLUIDS.register("molten_manyullyn",    hotBuilder().temperature(1500), Material.LAVA, 11);
  public static final FluidObject<SimpleFlowableFluid> moltenHepatizon   = FLUIDS.register("molten_hepatizon",    hotBuilder().temperature(1700), Material.LAVA,  8);
  public static final FluidObject<SimpleFlowableFluid> moltenQueensSlime = FLUIDS.register("molten_queens_slime", hotBuilder().temperature(1450), Material.LAVA,  9);
  public static final FluidObject<SimpleFlowableFluid> moltenSoulsteel   = FLUIDS.register("molten_soulsteel",    hotBuilder().temperature(1500), Material.LAVA,  6);
  public static final FluidObject<SimpleFlowableFluid> moltenNetherite   = FLUIDS.register("molten_netherite",    hotBuilder().temperature(1550), Material.LAVA, 14);
  public static final FluidObject<SimpleFlowableFluid> moltenKnightslime = FLUIDS.register("molten_knightslime",  hotBuilder().temperature(1425), Material.LAVA, 12);

  // compat ores
  public static final FluidObject<SimpleFlowableFluid> moltenTin      = FLUIDS.register("molten_tin",      hotBuilder().temperature( 525), Material.LAVA, 12);
  public static final FluidObject<SimpleFlowableFluid> moltenAluminum = FLUIDS.register("molten_aluminum", hotBuilder().temperature( 725), Material.LAVA, 12);
  public static final FluidObject<SimpleFlowableFluid> moltenLead     = FLUIDS.register("molten_lead",     hotBuilder().temperature( 630), Material.LAVA, 12);
  public static final FluidObject<SimpleFlowableFluid> moltenSilver   = FLUIDS.register("molten_silver",   hotBuilder().temperature(1090), Material.LAVA, 12);
  public static final FluidObject<SimpleFlowableFluid> moltenNickel   = FLUIDS.register("molten_nickel",   hotBuilder().temperature(1250), Material.LAVA, 12);
  public static final FluidObject<SimpleFlowableFluid> moltenZinc     = FLUIDS.register("molten_zinc",     hotBuilder().temperature( 720), Material.LAVA, 12);
  public static final FluidObject<SimpleFlowableFluid> moltenPlatinum = FLUIDS.register("molten_platinum", hotBuilder().temperature(1270), Material.LAVA, 12);
  public static final FluidObject<SimpleFlowableFluid> moltenTungsten = FLUIDS.register("molten_tungsten", hotBuilder().temperature(1250), Material.LAVA, 12);
  public static final FluidObject<SimpleFlowableFluid> moltenOsmium   = FLUIDS.register("molten_osmium",   hotBuilder().temperature(1275), Material.LAVA,  4);
  public static final FluidObject<SimpleFlowableFluid> moltenUranium  = FLUIDS.register("molten_uranium",  hotBuilder().temperature(1130), Material.LAVA, 15);

  // compat alloys
  public static final FluidObject<SimpleFlowableFluid> moltenBronze     = FLUIDS.register("molten_bronze",     hotBuilder().temperature(1000), Material.LAVA, 10);
  public static final FluidObject<SimpleFlowableFluid> moltenBrass      = FLUIDS.register("molten_brass",      hotBuilder().temperature( 905), Material.LAVA, 10);
  public static final FluidObject<SimpleFlowableFluid> moltenElectrum   = FLUIDS.register("molten_electrum",   hotBuilder().temperature(1060), Material.LAVA, 10);
  public static final FluidObject<SimpleFlowableFluid> moltenInvar      = FLUIDS.register("molten_invar",      hotBuilder().temperature(1200), Material.LAVA, 10);
  public static final FluidObject<SimpleFlowableFluid> moltenConstantan = FLUIDS.register("molten_constantan", hotBuilder().temperature(1220), Material.LAVA, 10);
  public static final FluidObject<SimpleFlowableFluid> moltenPewter     = FLUIDS.register("molten_pewter",     hotBuilder().temperature( 700), Material.LAVA, 10);
  public static final FluidObject<SimpleFlowableFluid> moltenSteel      = FLUIDS.register("molten_steel",      hotBuilder().temperature(1250), Material.LAVA, 13);

  // mod-specific compat alloys
  // thermal
  public static final FluidObject<SimpleFlowableFluid> moltenEnderium = FLUIDS.register("molten_enderium", hotBuilder().temperature(1650), Material.LAVA, 12);
  public static final FluidObject<SimpleFlowableFluid> moltenLumium   = FLUIDS.register("molten_lumium",   hotBuilder().temperature(1350), Material.LAVA, 15);
  public static final FluidObject<SimpleFlowableFluid> moltenSignalum = FLUIDS.register("molten_signalum", hotBuilder().temperature(1425), Material.LAVA, 13);
  // mekanism
  public static final FluidObject<SimpleFlowableFluid> moltenRefinedGlowstone = FLUIDS.register("molten_refined_glowstone", hotBuilder().temperature(1125), Material.LAVA, 15);
  public static final FluidObject<SimpleFlowableFluid> moltenRefinedObsidian  = FLUIDS.register("molten_refined_obsidian",  hotBuilder().temperature(1775), Material.LAVA,  7);


  /** Creates a builder for a cool fluid with textures */
  private static FluidAttributes.Builder coolBuilder() {
    return ModelFluidAttributes.builder().sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY);
  }

  /** Creates a builder for a hot fluid */
  private static FluidAttributes.Builder hotBuilder() {
    return ModelFluidAttributes.builder().density(2000).viscosity(10000).temperature(1000).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA);
  }

  public static void gatherData(final FabricDataGenerator datagenerator) {
    datagenerator.addProvider(true, new FluidTooltipProvider(datagenerator));
  }

  void commonSetup() {
    CauldronInteraction.WATER.put(splashBottle.get(), new FillBottle(Items.SPLASH_POTION));
    CauldronInteraction.WATER.put(lingeringBottle.get(), new FillBottle(Items.LINGERING_POTION));
    CauldronInteraction.WATER.put(Items.SPLASH_POTION,    new EmptyBottleIntoWater(splashBottle,    CauldronInteraction.WATER.get(Items.SPLASH_POTION)));
    CauldronInteraction.WATER.put(Items.LINGERING_POTION, new EmptyBottleIntoWater(lingeringBottle, CauldronInteraction.WATER.get(Items.LINGERING_POTION)));
    CauldronInteraction.EMPTY.put(Items.SPLASH_POTION,    new EmptyBottleIntoEmpty(splashBottle,    CauldronInteraction.EMPTY.get(Items.SPLASH_POTION)));
    CauldronInteraction.EMPTY.put(Items.LINGERING_POTION, new EmptyBottleIntoEmpty(lingeringBottle, CauldronInteraction.EMPTY.get(Items.LINGERING_POTION)));
    // brew bottles into each other, bit weird but feels better than shapeless
    BrewingRecipeRegistry.addRecipe(new BottleBrewingRecipe(Ingredient.of(Items.GLASS_BOTTLE), Items.POTION, Items.SPLASH_POTION, new ItemStack(splashBottle)));
    BrewingRecipeRegistry.addRecipe(new BottleBrewingRecipe(Ingredient.of(TinkerTags.Items.SPLASH_BOTTLE), Items.SPLASH_POTION, Items.LINGERING_POTION, new ItemStack(lingeringBottle)));

    // dispense buckets
    DispenseItemBehavior dispenseBucket = new DefaultDispenseItemBehavior() {
      private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

      @Override
      public ItemStack execute(BlockSource source, ItemStack stack) {
        DispensibleContainerItem container = (DispensibleContainerItem)stack.getItem();
        BlockPos blockpos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
        Level level = source.getLevel();
        if (container.emptyContents(null, level, blockpos, null)) {
          container.checkExtraContent(null, level, stack, blockpos);
          return new ItemStack(Items.BUCKET);
        } else {
          return this.defaultDispenseItemBehavior.dispense(source, stack);
        }
      }
    };
    // slime
    DispenserBlock.registerBehavior(blood, dispenseBucket);
    DispenserBlock.registerBehavior(venom, dispenseBucket);
    DispenserBlock.registerBehavior(earthSlime, dispenseBucket);
    DispenserBlock.registerBehavior(skySlime, dispenseBucket);
    DispenserBlock.registerBehavior(enderSlime, dispenseBucket);
    DispenserBlock.registerBehavior(magma, dispenseBucket);
    // foods
    DispenserBlock.registerBehavior(honey, dispenseBucket);
    DispenserBlock.registerBehavior(beetrootSoup, dispenseBucket);
    DispenserBlock.registerBehavior(mushroomStew, dispenseBucket);
    DispenserBlock.registerBehavior(rabbitStew, dispenseBucket);
    // base molten fluids
    DispenserBlock.registerBehavior(searedStone, dispenseBucket);
    DispenserBlock.registerBehavior(scorchedStone, dispenseBucket);
    DispenserBlock.registerBehavior(moltenClay, dispenseBucket);
    DispenserBlock.registerBehavior(moltenGlass, dispenseBucket);
    DispenserBlock.registerBehavior(liquidSoul, dispenseBucket);
    DispenserBlock.registerBehavior(moltenPorcelain, dispenseBucket);
    DispenserBlock.registerBehavior(moltenObsidian, dispenseBucket);
    DispenserBlock.registerBehavior(moltenEnder, dispenseBucket);
    DispenserBlock.registerBehavior(blazingBlood, dispenseBucket);
    // ores
    DispenserBlock.registerBehavior(moltenEmerald, dispenseBucket);
    DispenserBlock.registerBehavior(moltenQuartz, dispenseBucket);
    DispenserBlock.registerBehavior(moltenAmethyst, dispenseBucket);
    DispenserBlock.registerBehavior(moltenDiamond, dispenseBucket);
    DispenserBlock.registerBehavior(moltenDebris, dispenseBucket);
    // metal ores
    DispenserBlock.registerBehavior(moltenIron, dispenseBucket);
    DispenserBlock.registerBehavior(moltenGold, dispenseBucket);
    DispenserBlock.registerBehavior(moltenCopper, dispenseBucket);
    DispenserBlock.registerBehavior(moltenCobalt, dispenseBucket);
    // alloys
    DispenserBlock.registerBehavior(moltenSlimesteel, dispenseBucket);
    DispenserBlock.registerBehavior(moltenAmethystBronze, dispenseBucket);
    DispenserBlock.registerBehavior(moltenRoseGold, dispenseBucket);
    DispenserBlock.registerBehavior(moltenPigIron, dispenseBucket);
    DispenserBlock.registerBehavior(moltenManyullyn, dispenseBucket);
    DispenserBlock.registerBehavior(moltenHepatizon, dispenseBucket);
    DispenserBlock.registerBehavior(moltenQueensSlime, dispenseBucket);
    DispenserBlock.registerBehavior(moltenSoulsteel, dispenseBucket);
    DispenserBlock.registerBehavior(moltenNetherite, dispenseBucket);
    DispenserBlock.registerBehavior(moltenKnightslime, dispenseBucket);
    // compat ores
    DispenserBlock.registerBehavior(moltenTin, dispenseBucket);
    DispenserBlock.registerBehavior(moltenAluminum, dispenseBucket);
    DispenserBlock.registerBehavior(moltenLead, dispenseBucket);
    DispenserBlock.registerBehavior(moltenSilver, dispenseBucket);
    DispenserBlock.registerBehavior(moltenNickel, dispenseBucket);
    DispenserBlock.registerBehavior(moltenZinc, dispenseBucket);
    DispenserBlock.registerBehavior(moltenPlatinum, dispenseBucket);
    DispenserBlock.registerBehavior(moltenTungsten, dispenseBucket);
    DispenserBlock.registerBehavior(moltenOsmium, dispenseBucket);
    DispenserBlock.registerBehavior(moltenUranium, dispenseBucket);
    // compat alloys
    DispenserBlock.registerBehavior(moltenBronze, dispenseBucket);
    DispenserBlock.registerBehavior(moltenBrass, dispenseBucket);
    DispenserBlock.registerBehavior(moltenElectrum, dispenseBucket);
    DispenserBlock.registerBehavior(moltenInvar, dispenseBucket);
    DispenserBlock.registerBehavior(moltenConstantan, dispenseBucket);
    DispenserBlock.registerBehavior(moltenPewter, dispenseBucket);
    DispenserBlock.registerBehavior(moltenSteel, dispenseBucket);
    // mod-specific compat alloys
    DispenserBlock.registerBehavior(moltenEnderium, dispenseBucket);
    DispenserBlock.registerBehavior(moltenLumium, dispenseBucket);
    DispenserBlock.registerBehavior(moltenSignalum, dispenseBucket);
    DispenserBlock.registerBehavior(moltenRefinedGlowstone, dispenseBucket);
    DispenserBlock.registerBehavior(moltenRefinedObsidian, dispenseBucket);

    // brew congealed slime into bottles to get slime bottles, easy melting
    for (SlimeType slime : SlimeType.values()) {
      BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(Items.GLASS_BOTTLE), Ingredient.of(TinkerWorld.congealedSlime.get(slime)), new ItemStack(TinkerFluids.slimeBottle.get(slime))));
    }
    BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(Items.GLASS_BOTTLE), Ingredient.of(Blocks.MAGMA_BLOCK), new ItemStack(TinkerFluids.magmaBottle)));
  }
}
