package wtf.novoline.module;

import com.google.common.base.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import wtf.novoline.Novoline;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.setting.SettingManager;
import wtf.novoline.setting.MultiComboSetting.Option;
import wtf.novoline.utils.language.Language;
import wtf.novoline.utils.notification.Notification;
import wtf.novoline.utils.notification.NotificationType;

public abstract class Module {
   private final List<Module> autoDisableTargets = new ArrayList<>();
   private boolean enabled;
   private boolean enabledAtInit;
   private float arrayListOffsetX;
   private final List<Module> disabledByThis = new ArrayList<>();
   private String suffix = "";
   protected static final Minecraft minecraft = Minecraft.getMinecraft();
   private int keyBind;
   private Module.PressType pressType = Module.PressType.PRESS;
   private CheckboxSetting checkboxSetting;
   protected static final Novoline novoline = Novoline.getNovoline();
   private final Category category;
   private float arrayListOffsetY;
   private final String name;
   protected static final SettingManager settingManager = Novoline.getNovoline().getSettingManager();
   private MultiComboSetting multiComboSetting;
   private final String description;

   public boolean wasEnabledAtInit() {
      return this.enabledAtInit;
   }

   public void setPressType(Module.PressType pressType2) {
      this.pressType = pressType2;
   }

   public float getArrayListOffsetX() {
      return this.arrayListOffsetX;
   }

   protected void updateAutoDisable() {
      this.autoDisableTargets.clear();
      this.autoDisableTargets
         .addAll(
            this.multiComboSetting
               .getElements()
               .stream()
               .filter(Option::isSelected)
               .map(option -> option.getName().toLowerCase(Locale.ROOT))
               .map(string -> ModuleManager.modulesByName.get(string))
               .collect(Collectors.toList())
         );
   }

   public void setEnabled(boolean flag) {
      if (this.enabled != flag) {
         this.enabled = flag;
         if (this.enabled) {
            this.onEnable();
            Novoline.getNovoline().getBus().subscribe(this);
            this.disabledByThis.clear();
            ModuleManager.modules
               .stream()
               .filter(module2 -> !module2.equals(this))
               .filter(
                  module2 -> module2.isEnabled()
                     && this.autoDisableTargets.contains(module2)
                     && this.multiComboSetting
                        .getElements()
                        .stream()
                        .anyMatch(option -> option.getName().equals(module2.getName()) && option.isSelected())
               )
               .forEach(
                  module2 -> {
                     module2.disable();
                     this.disabledByThis.add(module2);
                     this.showNotification(
                        NotificationType.INFO, module2.getName(), Language.translate("feature.auto_disable.notification.disabled_due", this.getName()), 1500L
                     );
                  }
               );
         } else {
            Novoline.getNovoline().getBus().unsubscribe(this);
            this.onDisable();
            if (this.checkboxSetting != null && this.checkboxSetting.isEnabled()) {
               for (Module module3 : this.disabledByThis) {
                  module3.enable();
                  this.showNotification(
                     NotificationType.INFO, module3.getName(), Language.translate("feature.auto_disable.notification.enabled_due", this.getName()), 1500L
                  );
               }
            }
         }

         if (Novoline.initialized
            && ModuleManager.notifications.isEnabled()
            && ModuleManager.notifications.rules.isSelected(0)
            && this != ModuleManager.clickGui) {
            String text2 = this.enabled ? "feature_enabled" : "feature_disabled";
            novoline.getNotificationManager()
               .show(
                  new Notification(
                     NotificationType.INFO,
                     Language.translate("notification.type.info"),
                     Language.translate("notification.text." + text2, this.getName()),
                     1500L
                  )
               );
         }

         this.onToggled();
         if (Novoline.initialized) {
            Novoline.getNovoline().getModuleManager().updateCachedFeatures();
         }
      }
   }

   protected void onDisable() {
   }

   protected void sendPacketUnregistered(Packet packet) {
      minecraft.thePlayer.sendQueue.addToSendQueueUnregistered(packet);
   }

   public Module.PressType getPressType() {
      return this.pressType;
   }

   public void disable() {
      this.setEnabled(false);
   }

   protected void showNotification(NotificationType notificationType, String text2, String text3, long l) {
      novoline.getNotificationManager().show(new Notification(notificationType, text2, text3, l));
   }

   public void enable() {
      this.setEnabled(true);
   }

   protected void registerAutoDisable() {
      this.multiComboSetting = new MultiComboSetting(this, "Disables modules") {
         @Override
         public void interactCallback() {
            Module.this.updateAutoDisable();
         }
      };
      this.checkboxSetting = new CheckboxSetting(this, "Re-enable on toggle", false);
      this.autoDisableTargets.stream().map(Module::getName).forEach(string -> this.multiComboSetting.add(string, false));
      settingManager.register(this.multiComboSetting);
      settingManager.register(this.checkboxSetting);
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (object != null && this.getClass() == object.getClass()) {
         Module module3 = (Module)object;
         return Objects.equal(this.name, module3.name);
      } else {
         return false;
      }
   }

   protected void addAutoDisableTargets(Module... modules2) {
      this.autoDisableTargets.addAll(List.of(modules2));
      this.registerAutoDisable();
   }

   public Category getCategory() {
      return this.category;
   }

   protected void sendPacket(Packet packet) {
      minecraft.thePlayer.sendQueue.addToSendQueue(packet);
   }

   public String getSuffix() {
      return this.suffix;
   }

   public void toggle() {
      this.setEnabled(!this.isEnabled());
   }

   public List<Module> getAutoDisableTargets() {
      return this.autoDisableTargets;
   }

   public MultiComboSetting getMultiComboSetting() {
      return this.multiComboSetting;
   }

   public String getName() {
      return this.name;
   }

   public String getDescription() {
      return this.description;
   }

   public CheckboxSetting getCheckboxSetting() {
      return this.checkboxSetting;
   }

   public void setArrayListOffsetX(float f) {
      this.arrayListOffsetX = f;
   }

   @Override
   public String toString() {
      return this.name + " - " + this.description;
   }

   protected void onInitialized() {
      this.enabledAtInit = this.isEnabled();
   }

   public int getKeyBind() {
      return this.keyBind;
   }

   public void setArrayListOffsetY(float f) {
      this.arrayListOffsetY = f;
   }

   public float getArrayListOffsetY() {
      return this.arrayListOffsetY;
   }

   protected void onToggled() {
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setSuffix(String text2) {
      boolean flag = false;
      if (text2 != null && !text2.isEmpty()) {
         String text3 = " " + text2;
         if (!this.suffix.equals(text3)) {
            this.suffix = text3;
            flag = true;
         }
      } else if (this.suffix != null || !this.suffix.isEmpty()) {
         this.suffix = "";
         flag = true;
      }

      if (flag) {
         Novoline.getNovoline().getModuleManager().updateCachedFeatures();
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(new Object[]{this.name});
   }

   public Module(Category category2, String text2, String text3) {
      this.category = category2;
      this.name = text2;
      this.description = text3;
   }

   protected void onEnable() {
   }

   public void setKeyBind(int i) {
      this.keyBind = i;
   }

   public enum PressType {
      PRESS,
      HOLD;
   }
}
