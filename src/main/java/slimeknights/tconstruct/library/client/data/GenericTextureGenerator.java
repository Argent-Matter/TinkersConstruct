package slimeknights.tconstruct.library.client.data;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.mojang.blaze3d.platform.NativeImage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import org.lwjgl.stb.STBImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Data generator to create png image files */
@RequiredArgsConstructor
@Log4j2
public abstract class GenericTextureGenerator implements DataProvider {
  private final FabricDataGenerator output;
  private final String folder;

  /** Saves the given image to the given location */
  @SuppressWarnings("UnstableApiUsage")
  protected void saveImage(CachedOutput cache, ResourceLocation location, NativeImage image) {
    try {
      Path path = this.output.getOutputFolder().resolve(
        Paths.get(PackType.CLIENT_RESOURCES.getDirectory(),
          location.getNamespace(), folder, location.getPath() + ".png"));
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      HashingOutputStream hashingOutputStream = new HashingOutputStream(Hashing.sha1(), byteArrayOutputStream);
      WritableByteChannel channel = Channels.newChannel(hashingOutputStream);
      if (!image.writeToChannel(channel)) {
        throw new IOException("Could not write image to the PNG file \"" + path.toAbsolutePath() + "\": " + STBImage.stbi_failure_reason());
      }

      cache.writeIfNeeded(path, byteArrayOutputStream.toByteArray(), hashingOutputStream.hash());
      channel.close();
    } catch (IOException e) {
      log.error("Couldn't create data for {}", location, e);
    }
  }
}
