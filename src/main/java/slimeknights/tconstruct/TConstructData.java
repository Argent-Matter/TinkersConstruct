package slimeknights.tconstruct;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.world.TinkerWorld;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

public class TConstructData implements DataGeneratorEntrypoint {

  @Override
  public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
    ExistingFileHelper helper = ExistingFileHelper.withResourcesFromArg();
    TConstruct.onInitializeDataGenerator(fabricDataGenerator);
    TinkerSmeltery.gatherData(fabricDataGenerator);
    TinkerModifiers.gatherData(fabricDataGenerator, helper);

    TinkerTools.gatherData(fabricDataGenerator, helper);
    TinkerFluids.gatherData(fabricDataGenerator);
    TinkerWorld.gatherData(fabricDataGenerator);
    TinkerGadgets.gatherData(fabricDataGenerator);
    TinkerCommons.gatherData(fabricDataGenerator);
    TinkerTables.gatherData(fabricDataGenerator);
  }
}
