package wtf.novoline.platform;

import com.viaversion.viaversion.api.Via;
import de.gerrygames.viarewind.api.ViaRewindConfigImpl;
import de.gerrygames.viarewind.api.ViaRewindPlatform;
import java.io.File;
import java.util.logging.Logger;

public class ViaRewindPlatformImpl implements ViaRewindPlatform {
   @Override
   public Logger getLogger() {
      return Via.getPlatform().getLogger();
   }

   public ViaRewindPlatformImpl(File file) {
      ViaRewindConfigImpl viaRewindConfigImpl = new ViaRewindConfigImpl(file.toPath().resolve("ViaRewind").resolve("config.yml").toFile());
      viaRewindConfigImpl.reloadConfig();
      this.init(viaRewindConfigImpl);
   }
}
