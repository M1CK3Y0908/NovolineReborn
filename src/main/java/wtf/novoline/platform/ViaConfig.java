package wtf.novoline.platform;

import com.viaversion.viaversion.configuration.AbstractViaConfig;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ViaConfig extends AbstractViaConfig {
   private static final List<String> unsupportedOptions = Arrays.asList(
      "anti-xray-patch",
      "bungee-ping-interval",
      "bungee-ping-save",
      "bungee-servers",
      "quick-move-action-fix",
      "nms-player-ticking",
      "velocity-ping-interval",
      "velocity-ping-save",
      "velocity-servers",
      "blockconnection-method",
      "change-1_9-hitbox",
      "change-1_14-hitbox"
   );

   public ViaConfig(File file) {
      super(file);
      this.reloadConfig();
   }

   @Override
   public boolean is1_12QuickMoveActionFix() {
      return false;
   }

   @Override
   protected void handleConfig(Map<String, Object> map) {
   }

   @Override
   public URL getDefaultConfigURL() {
      return this.getClass().getClassLoader().getResource("assets/viaversion/config.yml");
   }

   @Override
   public boolean is1_14HitboxFix() {
      return false;
   }

   @Override
   public List<String> getUnsupportedOptions() {
      return unsupportedOptions;
   }

   @Override
   public String getBlockConnectionMethod() {
      return "packet";
   }

   @Override
   public boolean isAntiXRay() {
      return false;
   }

   @Override
   public boolean is1_9HitboxFix() {
      return false;
   }

   @Override
   public boolean isNMSPlayerTicking() {
      return false;
   }
}
