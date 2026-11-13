package wtf.novoline.module.hud;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.gui.GuiScreen;
import wtf.novoline.Novoline;
import wtf.novoline.gui.BackgroundManager;
import wtf.novoline.gui.BackgroundManager.Entry;
import wtf.novoline.gui.BackgroundManager.Record;
import wtf.novoline.gui.clickgui.ClickGuiScreen;
import wtf.novoline.gui.menu.MenuClickGuiScreen;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ColorSetting;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.time.Stopwatch;

public class ClickGui extends Module {
   public Entry entry;
   public final ComboSetting clickGui = settingManager.register(new ComboSetting(this, "Mode", 0, "Dropdown", "NeverLose") {
      @Override
      public void interactCallback() {
         ClickGui.this.onEnable();
      }
   });
   public final MultiComboSetting glowSettings = settingManager.register((new MultiComboSetting(this, "Glow settings") {
      @Override
      public boolean shouldShow() {
         return !ClickGui.this.performanceMode.isEnabled() && ClickGui.this.clickGui.getCurrentElement() == 0;
      }
   }).add("Features", true).add("Settings", true).add("Panels", true));
   public final MultiComboSetting panelsSettings = settingManager.register((new MultiComboSetting(this, "Panels settings") {
      @Override
      public boolean shouldShow() {
         return !ClickGui.this.performanceMode.isEnabled() && ClickGui.this.clickGui.getCurrentElement() == 0;
      }
   }).add("Blur", true).add("Shadow", true));
   public final MultiComboSetting backgroundSettings = settingManager.register((new MultiComboSetting(this, "Background settings") {
      @Override
      public boolean shouldShow() {
         return !ClickGui.this.performanceMode.isEnabled() && ClickGui.this.clickGui.getCurrentElement() == 0;
      }
   }).add("Blur", true).add("Desaturation", true).add("Obscuration", true).add("Anime", true));
   public NumberSetting imageScale = new NumberSetting(this, "Image Scale", 1.2F, 1.0F, 2.0F) {
      @Override
      public boolean shouldShow() {
         return ClickGui.this.backgroundSettings.isSelected(3) && ClickGui.this.clickGui.getCurrentElement() == 0;
      }
   };
   public CheckboxSetting randomImage = new CheckboxSetting(this, "Random Image", false) {
      @Override
      public boolean shouldShow() {
         return ClickGui.this.backgroundSettings.isSelected(3) && ClickGui.this.clickGui.getCurrentElement() == 0;
      }
   };
   public CheckboxSetting changeAccentColor = new CheckboxSetting(this, "Change accent color", false) {
      @Override
      public boolean shouldShow() {
         return ClickGui.this.backgroundSettings.isSelected(3) && ClickGui.this.clickGui.getCurrentElement() == 0;
      }
   };
   public final CheckboxSetting performanceMode = settingManager.register(new CheckboxSetting(this, "Performance mode", false) {
      @Override
      public boolean shouldShow() {
         return ClickGui.this.clickGui.getCurrentElement() == 0;
      }
   });
   public final CheckboxSetting astolfoMode = settingManager.register(new CheckboxSetting(this, "Astolfo mode", false) {
      @Override
      public boolean shouldShow() {
         return ClickGui.this.clickGui.getCurrentElement() == 0;
      }
   });
   public final NumberSetting astolfoCycleTime = settingManager.register(new NumberSetting(this, "Astolfo cycle time", 10000, 1, 20000) {
      @Override
      public boolean shouldShow() {
         return ClickGui.this.astolfoMode.isEnabled() && ClickGui.this.clickGui.getCurrentElement() == 0;
      }
   });
   public final ColorSetting primary = settingManager.register(new ColorSetting(this, "Primary", -8286984) {
      @Override
      public boolean shouldShow() {
         return !ClickGui.this.astolfoMode.isEnabled() && ClickGui.this.clickGui.getCurrentElement() == 0;
      }
   });
   public final ColorSetting extra = settingManager.register(new ColorSetting(this, "Extra", -10262799) {
      @Override
      public boolean shouldShow() {
         return !ClickGui.this.astolfoMode.isEnabled() && ClickGui.this.clickGui.getCurrentElement() == 0;
      }
   });
   public final ColorSetting desaturation = settingManager.register(new ColorSetting(this, "Desaturation", -13554047) {
      @Override
      public boolean shouldShow() {
         return !ClickGui.this.astolfoMode.isEnabled() && ClickGui.this.clickGui.getCurrentElement() == 0;
      }
   });
   private final Stopwatch toggleTimer = new Stopwatch();
   public BackgroundManager backgroundManager;
   public ComboSetting image;

   @Override
   protected void onEnable() {
      this.toggleTimer.reset();

      GuiScreen guiScreen = switch (this.clickGui.getCurrentElement()) {
         case 0 -> {
            MenuClickGuiScreen menuClickGuiScreen = Novoline.getNovoline().getMenuClickGuiScreen();
            menuClickGuiScreen.open();
            yield menuClickGuiScreen;
         }
         case 1 -> {
            ClickGuiScreen clickGuiScreen = Novoline.getNovoline().getClickGuiScreen();
            clickGuiScreen.reset();
            yield clickGuiScreen;
         }
         default -> throw new IllegalArgumentException("Unsupported GUI mode");
      };
      if (this.randomImage.isEnabled()) {
         int i = ThreadLocalRandom.current().nextInt(0, this.backgroundManager.getLinkedHashMap().size() - 1);
         this.entry = this.backgroundManager.getEntryAt(i);
      } else {
         this.entry = this.backgroundManager.byAlias(this.image.getCurrentElementAsString());
      }

      if (this.changeAccentColor.isEnabled()) {
         Record colors = this.entry.getMetadata().getColors();
         this.primary.setColor(colors.getColor().getRGB());
         this.extra.setColor(colors.getColor2().getRGB());
         this.desaturation.setColor(colors.getColor3().getRGB());
      }

      minecraft.displayGuiScreen(guiScreen);
   }

   public ClickGui() {
      super(Category.HUD, "ClickGui", "");
      this.setKeyBind(54);

      try {
         this.backgroundManager = new BackgroundManager();
         this.image = new ComboSetting(
            this,
            "Image",
            0,
            this.backgroundManager.getLinkedHashMap().values().stream().map(entry2 -> entry2.getMetadata().getAlias()).toArray(String[]::new)
         ) {
            @Override
            public boolean shouldShow() {
               return ClickGui.this.backgroundSettings.isSelected(3)
                  && !ClickGui.this.randomImage.isEnabled()
                  && ClickGui.this.clickGui.getCurrentElement() == 0;
            }
         };
         settingManager.register(this.image);
         settingManager.register(this.imageScale);
         settingManager.register(this.randomImage);
         settingManager.register(this.changeAccentColor);
      } catch (Throwable throwable) {
         Novoline.taggedLogger.error(throwable, "Couldn't load anime images");
         throw new RuntimeException(throwable);
      }
   }

   @Override
   protected void onDisable() {
      minecraft.displayGuiScreen(null);
   }
}
