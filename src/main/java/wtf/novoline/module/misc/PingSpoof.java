package wtf.novoline.module.misc;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import wtf.novoline.event.PacketSendEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.ScheduledTask;

public class PingSpoof extends Module {
   private final NumberSetting pingSpoof = settingManager.register(new NumberSetting(this, "Min delay (ms)", 500, 0, 10000));
   private final NumberSetting maxDelayMs = settingManager.register(new NumberSetting(this, "Max delay (ms)", 500, 0, 10000));
   private final ComboSetting mode = settingManager.register(new ComboSetting(this, "Mode", 0, "Specific packets", "All packets"));
   private final MultiComboSetting packets = settingManager.register(new PingSpoof.MultiComboSettingChild(this, "Packets"))
      .add("Keep Alive", true)
      .add("Confirm Transaction", false);
   @EventLink(0)
   private final Listener<PacketSendEvent> onPacketSend = packetSendEvent -> {
      if (this.mode.getCurrentElement() == 1
         || packetSendEvent.packet instanceof C00PacketKeepAlive && this.packets.isSelected(0)
         || packetSendEvent.packet instanceof C0FPacketConfirmTransaction && this.packets.isSelected(1)) {
         if (minecraft.isSingleplayer() || minecraft.thePlayer != null && (minecraft.thePlayer.isDead || minecraft.thePlayer.getHealth() <= 0.0F)) {
            return;
         }

         novoline.getTaskScheduler().schedule(new ScheduledTask(this.getDelayMs()) {
            @Override
            public void run() {
               if (PingSpoof.access$100().theWorld != null) {
                  PingSpoof.access$000(PingSpoof.this, packetSendEvent.packet);
               }
            }
         });
         packetSendEvent.cancel();
      }
   };

   static void access$000(PingSpoof pingSpoof2, Packet packet2) {
      pingSpoof2.sendPacketUnregistered(packet2);
   }

   static Minecraft access$100() {
      return minecraft;
   }

   public PingSpoof() {
      super(Category.MISC, "PingSpoof", "Delays certain packets to increase ping");
   }

   private int getDelayMs() {
      int i = (int)this.pingSpoof.getCurrentValue();
      int i2 = (int)this.maxDelayMs.getCurrentValue();
      if (i > i2) {
         i2 = i;
      }

      return i == i2 ? i : ThreadLocalRandom.current().nextInt(i, i2);
   }

   class MultiComboSettingChild extends MultiComboSetting {
      MultiComboSettingChild(Module module2, String text) {
         super(module2, text);
      }

      @Override
      public boolean shouldShow() {
         return PingSpoof.this.mode.getCurrentElement() == 0;
      }
   }
}
