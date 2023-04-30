package slimeknights.tconstruct.fixture;

import net.minecraft.core.Registry;
import slimeknights.tconstruct.library.TinkerRegistries;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;

public class ModifierFixture {
  public static final ModifierId EMPTY_ID = new ModifierId("test", "empty");
  public static final ModifierId TEST_1 = new ModifierId("test", "modifier_1");
  public static final ModifierId TEST_2 = new ModifierId("test", "modifier_2");


  protected static final Registry<Modifier> MODIFIER_REGISTRY = TinkerRegistry.MODIFIERS_REGISTRY;/*new RegistryBuilder<Modifier>()
			.setType(Modifier.class).setName(TConstruct.getResource("modifiers")).setDefaultKey(EMPTY_ID).create();*/

  public static final Modifier EMPTY = new Modifier();
  public static final Modifier TEST_MODIFIER_1 = new Modifier();
  public static final Modifier TEST_MODIFIER_2 = new Modifier();

  private static boolean init = false;

  public static void init() {
    if (init) {
      return;
    }
    init = true;
    Registry.register(MODIFIER_REGISTRY, EMPTY_ID, EMPTY);
    Registry.register(MODIFIER_REGISTRY, TEST_1, TEST_MODIFIER_1);
    Registry.register(MODIFIER_REGISTRY, TEST_2, TEST_MODIFIER_2);
  }
}
