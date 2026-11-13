package wtf.novoline.utils.config;

import java.util.ArrayList;
import java.util.List;

public class ConfigRegistry {
   public static final FeaturesSection featuresWithHotkeys = new FeaturesSection(true);
   public static final MacrosSection macrosSection = new MacrosSection();
   private final List<ConfigSection> sections = new ArrayList<>();
   public static final OverlaySection overlaySection = new OverlaySection();
   public static final XRaySection xRaySection = new XRaySection();
   public static final PreferencesSection preferencesSection = new PreferencesSection();
   private final ConfigManager configManager = new ConfigManager(this);
   public static final ProxiesSection proxiesSection = new ProxiesSection();
   public static final FeaturesSection featuresWithoutHotkeys = new FeaturesSection(false);

   public List<ConfigSection> getSections() {
      return this.sections;
   }

   public ConfigRegistry() {
      this.register(featuresWithHotkeys);
      this.register(macrosSection);
      this.register(overlaySection);
      this.register(xRaySection);
      this.register(preferencesSection);
   }

   public void register(ConfigSection configSection) {
      this.sections.add(configSection);
   }

   public ConfigManager getConfigManager() {
      return this.configManager;
   }
}
