package slimeknights.mantle.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.fabricators_of_create.porting_lib.event.client.RegisterShadersCallback;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceManager;
import slimeknights.mantle.Mantle;

import java.io.IOException;

public class MantleShaders {

  private static ShaderInstance blockFullBrightShader;

  public static void registerShaders(ResourceManager manager, RegisterShadersCallback.ShaderRegistry registry) throws IOException {
    registry.registerShader(
      new ShaderInstance(manager, "block_fullbright", DefaultVertexFormat.BLOCK),
      shaderInstance -> blockFullBrightShader = shaderInstance
    );
  }

  public static ShaderInstance getBlockFullBrightShader() {
    return blockFullBrightShader;
  }
}
