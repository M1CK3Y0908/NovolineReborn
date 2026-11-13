package wtf.novoline.utils.account;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

public final class WebViewLauncher {
   private static final Path path = Paths.get(System.getenv("APPDATA"), "Novoline", "WebView");

   public static void deleteDefaultProfile() throws IOException {
      Path path2 = path.resolve("default");
      if (Files.exists(path2)) {
         deleteRecursively(path2);
      }
   }

   private static void deleteRecursively(Path path2) throws IOException {
      Files.walk(path2).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
   }

   public static Process createProcess(String text, String text2, String text3) throws IOException {
      Path path2 = path.resolve("AuthWebView.exe");
      if (Files.notExists(path2)) {
         throw new IllegalArgumentException("Must call extract() before starting WebVew");
      } else {
         return new ProcessBuilder(path2.toAbsolutePath().toString(), text, text2, text3).inheritIO().start();
      }
   }

   public static void extract() throws IOException {
      ResourceLocation resourceLocation = new ResourceLocation("novoline", "webview.zip");
      IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation);
      if (Files.exists(path)) {
         deleteRecursively(path);
      }

      Files.createDirectories(path);

      ZipEntry zipEntry;
      try (ZipInputStream zipInputStream = new ZipInputStream(resource.getInputStream())) {
         for (; (zipEntry = zipInputStream.getNextEntry()) != null; zipInputStream.closeEntry()) {
            byte[] allBytes = zipInputStream.readAllBytes();
            Path path2 = path.resolve(zipEntry.getName());
            if (zipEntry.isDirectory()) {
               Files.createDirectory(path2);
            } else {
               Files.createDirectories(path2.getParent());
               Files.write(path2, allBytes);
            }
         }
      }
   }
}
