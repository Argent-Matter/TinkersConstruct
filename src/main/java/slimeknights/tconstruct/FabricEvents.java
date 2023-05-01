package slimeknights.tconstruct;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import slimeknights.tconstruct.fluids.FluidEvents;
import slimeknights.tconstruct.shared.AchievementEvents;
import slimeknights.tconstruct.shared.CommonsEvents;
import slimeknights.tconstruct.tools.logic.InteractionHandler;
import slimeknights.tconstruct.tools.logic.ToolEvents;

public class FabricEvents {
  public static void init() {
    FluidEvents.onFurnaceFuel();
    ToolEvents.init();
    CommonsEvents.init();
    AchievementEvents.init();
    InteractionHandler.init();


    
    if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && FabricLoader.getInstance().isDevelopmentEnvironment()) {
      ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
        stack.getTags().forEach(tagKey ->
          lines.add(Component.literal("#" + tagKey.location().toString()).withStyle(ChatFormatting.GRAY))
        );
      });
    }
  }
  
//  private static class Client {
//    @Environment(EnvType.CLIENT)
//    private static void init() {
//      WorldClientEvents.clientSetup();
//    }
//  }
}
