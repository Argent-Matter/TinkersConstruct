package slimeknights.tconstruct.library.client.data.util;

import com.mojang.blaze3d.platform.NativeImage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Logic to read sprites from existing images and return native images which can later be modified
 */
@Log4j2
@RequiredArgsConstructor
public class DataGenSpriteReader extends AbstractSpriteReader {
  private final Path folder;

  @Override
  public boolean exists(ResourceLocation path) {
    var pathPath = Path.of(folder.toString(), "assets", path.toString(), ".png");
    return new File(pathPath.toUri()).exists();
  }

  @Override
  public NativeImage read(ResourceLocation path) throws IOException {
    try {
      Path path1 = Path.of(folder.toString() +  "assets/" + path.getNamespace() + "/" + path.getPath() + ".png");
      // Resource resource = new Resource("Default", () -> Files.newInputStream(path1));
      NativeImage image = NativeImage.read(Files.newInputStream(path1));
      openedImages.add(image);
      return image;
    } catch (IOException e) {
      log.error("Failed to read image at {}", path);
      throw e;
    }
  }
}
