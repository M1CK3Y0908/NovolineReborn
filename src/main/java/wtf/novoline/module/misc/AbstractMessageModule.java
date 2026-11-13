package wtf.novoline.module.misc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.tinylog.Logger;
import wtf.novoline.Novoline;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;

public abstract class AbstractMessageModule extends Module {
   protected final List<String> messages = new ArrayList<>();
   protected final Path path;

   public Path getPath() {
      return this.path;
   }

   protected String getRandomMessage() {
      int i = ThreadLocalRandom.current().nextInt(0, this.messages.size());
      return this.replacePlaceholders(this.messages.get(i));
   }

   public List<String> getMessages() {
      return this.messages;
   }

   public AbstractMessageModule(Category category, String text, String text2, Path path2, ResourceLocation resourceLocation) {
      super(category, text, text2);
      this.path = path2;
      if (resourceLocation != null) {
         try {
            String text3 = IOUtils.toString(minecraft.getResourceManager().getResource(resourceLocation).getInputStream(), StandardCharsets.UTF_8);
            Files.writeString(path2, text3);
            this.messages.addAll(Arrays.asList(text3.split("\n")));
         } catch (IOException ioException) {
            Logger.error(ioException, "An I/O error occurred while reading/writing default messages!");
         }
      } else {
         try {
            Files.readAllLines(path2).stream().filter(string -> !string.startsWith("#")).filter(string -> !string.isBlank()).forEach(this.messages::add);
         } catch (IOException ioException2) {
            Logger.error(ioException2, "An I/O error occurred while reading messages!");
         }
      }
   }

   protected String replacePlaceholders(String text) {
      return text.replace("{c_version}", "3.0").replace("{c_builds}", "030122").replace("{u_username}", Novoline.username).replace("{u_uid}", Novoline.userId);
   }

   public void saveMessages() {
      try {
         Files.write(this.path, this.messages);
      } catch (IOException ioException) {
         Novoline.taggedLogger.error(ioException, "Couldn't save messages");
      }
   }
}
