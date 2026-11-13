package wtf.novoline.platform;

import com.viaversion.viabackwards.api.ViaBackwardsPlatform;
import java.io.File;
import java.util.logging.Logger;

public class ViaBackwardsPlatformImpl implements ViaBackwardsPlatform {
   private final File file;

   public ViaBackwardsPlatformImpl(File file2) {
      this.init(this.file = new File(file2, "ViaBackwards"));
   }

   @Override
   public Logger getLogger() {
      return ViaMCP.getViaMCP().getLogger();
   }

   @Override
   public boolean isOutdated() {
      return false;
   }

   @Override
   public void disable() {
   }

   @Override
   public File getDataFolder() {
      return new File(this.file, "config.yml");
   }
}
