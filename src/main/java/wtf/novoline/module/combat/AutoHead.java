package wtf.novoline.module.combat;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.item.ItemSkull;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import wtf.novoline.enums.HypixelGameType;
import wtf.novoline.enums.ServerType;
import wtf.novoline.event.OnUpdateWalkingPlayerEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.hypixel.HypixelState;
import wtf.novoline.utils.player.InventoryUtils;
import wtf.novoline.utils.time.Stopwatch;

public class AutoHead extends Module {
   private final NumberSetting autoHead = settingManager.register(new NumberSetting(this, "Delay", 500, 0, 3000));
   private final NumberSetting health = settingManager.register(new NumberSetting(this, "Health", 14, 1, 20) {
      @Override
      public boolean shouldShow() {
         return !AutoHead.this.forceAbsorption.isEnabled();
      }
   });
   private final CheckboxSetting forceAbsorption = settingManager.register(new CheckboxSetting(this, "Force absorption", false));
   private final CheckboxSetting checkRegen = settingManager.register(new CheckboxSetting(this, "Check regen", false) {
      @Override
      public boolean shouldShow() {
         return !AutoHead.this.forceAbsorption.isEnabled();
      }
   });
   private final Stopwatch delayTimer = new Stopwatch();
   @EventLink
   private final Listener<OnUpdateWalkingPlayerEvent> onOnUpdateWalkingPlayer = onUpdateWalkingPlayerEvent -> {
      if (!HypixelState.isServerType(ServerType.HYPIXEL) || !HypixelState.isGameType(HypixelGameType.PRE_GAME)) {
         int slotOfClass = InventoryUtils.findSlotOfClass(ItemSkull.class, true);
         if (slotOfClass != -1 && this.needsHealing() && this.delayTimer.hasElapsed((long)this.autoHead.getCurrentValue())) {
            this.sendPacketUnregistered(new C09PacketHeldItemChange(slotOfClass));
            this.sendPacketUnregistered(new C08PacketPlayerBlockPlacement(minecraft.thePlayer.getHeldItem()));
            this.sendPacketUnregistered(new C09PacketHeldItemChange(minecraft.thePlayer.inventory.currentItem));
            this.delayTimer.reset();
         }
      }
   };

   private boolean needsHealing() {
      if (this.forceAbsorption.isEnabled() && minecraft.thePlayer.getAbsorptionAmount() <= 0.0F) {
         return true;
      } else {
         return !(minecraft.thePlayer.getHealth() < this.health.getCurrentValue())
            ? false
            : !this.checkRegen.isEnabled() || minecraft.thePlayer.isPotionActive(Potion.regeneration);
      }
   }

   public AutoHead() {
      super(Category.COMBAT, "AutoHead", "");
   }
}
