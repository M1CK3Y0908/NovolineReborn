package wtf.novoline.module.misc;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.ConcurrentModificationException;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import org.apache.commons.codec.digest.DigestUtils;
import wtf.novoline.Novoline;
import wtf.novoline.event.PacketReceiveEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.ScheduledTask;

public class AutoRegister extends Module {
   private final NumberSetting autoRegister = settingManager.register(new NumberSetting(this, "Delay (ms)", 2000, 100, 5000));
   @EventLink
   public final Listener<PacketReceiveEvent> onPacketReceive = packetReceiveEvent -> {
      Packet packet2 = packetReceiveEvent.packet;
      if (packet2 instanceof S02PacketChat) {
         String formattedText = ((S02PacketChat)packet2).getChatComponent().getFormattedText();

         try {
            if (minecraft.theWorld != null
               && minecraft.theWorld.getLoadedEntityList().stream().filter(entity2 -> entity2 instanceof EntityPlayer).count() == 1L) {
               if (formattedText.contains("/reg")) {
                  this.runAfterDelay(() -> {
                     String text = this.generatePassword();
                     minecraft.thePlayer.sendChatMessage("/register " + text + " " + text);
                  });
               } else if (formattedText.contains("/l")) {
                  this.runAfterDelay(() -> minecraft.thePlayer.sendChatMessage("/login " + this.generatePassword()));
               }
            }
         } catch (ConcurrentModificationException concurrentModificationException) {
         }
      }
   };

   public AutoRegister() {
      super(Category.MISC, "AutoRegister", "Automatically register/login on cracked servers");
   }

   private void runAfterDelay(final Runnable runnable) {
      int i = (int)this.autoRegister.getCurrentValue();
      novoline.getTaskScheduler().schedule(new ScheduledTask(i) {
         @Override
         public void run() {
            if (AutoRegister.access$000().theWorld != null) {
               runnable.run();
            }
         }
      });
   }

   private String generatePassword() {
      return DigestUtils.sha1Hex(Novoline.username + "|" + Novoline.userId + "|" + minecraft.session.getUsername()).substring(0, 6);
   }

   static Minecraft access$000() {
      return minecraft;
   }
}
