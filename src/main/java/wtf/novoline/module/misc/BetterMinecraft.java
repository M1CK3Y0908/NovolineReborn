package wtf.novoline.module.misc;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.resources.EmbeddedResourcePack;
import wtf.novoline.setting.CheckboxSetting;

public class BetterMinecraft extends Module {
   private boolean refreshPending = false;
   private EmbeddedResourcePack embeddedResourcePack = new EmbeddedResourcePack(new ResourceLocation("novoline", "packs/1.14.zip"));
   private final CheckboxSetting textures = settingManager.register(new CheckboxSetting(this, "Textures", true) {
      @Override
      public void interactCallback() {
         if (this.getModule().isEnabled()) {
            List defaultResourcePacks = BetterMinecraft.access$000().getDefaultResourcePacks();
            if (BetterMinecraft.this.textures.isEnabled()) {
               defaultResourcePacks.add(BetterMinecraft.this.embeddedResourcePack);
            } else {
               defaultResourcePacks.remove(BetterMinecraft.this.embeddedResourcePack);
            }

            BetterMinecraft.this.refreshPending = true;
         }
      }
   });
   public final CheckboxSetting sneak = settingManager.register(new CheckboxSetting(this, "Sneak", true));
   @EventLink
   public final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      if (this.refreshPending) {
         this.refreshPending = false;
         minecraft.refreshResources();
      }
   };

   public BetterMinecraft() {
      super(Category.MISC, "BetterMinecraft", "Makes your minecraft better");
   }

   @Override
   protected void onDisable() {
      List defaultResourcePacks = minecraft.getDefaultResourcePacks();
      if (defaultResourcePacks.contains(this.embeddedResourcePack)) {
         defaultResourcePacks.remove(this.embeddedResourcePack);
         minecraft.refreshResources();
      }
   }

   @Override
   protected void onEnable() {
      List defaultResourcePacks = minecraft.getDefaultResourcePacks();
      if (!defaultResourcePacks.contains(this.embeddedResourcePack) && this.textures.isEnabled()) {
         defaultResourcePacks.add(this.embeddedResourcePack);
      }

      if (defaultResourcePacks.contains(this.embeddedResourcePack)) {
         this.refreshPending = true;
      }
   }

   static Minecraft access$000() {
      return minecraft;
   }
}
