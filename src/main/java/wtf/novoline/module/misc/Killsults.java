package wtf.novoline.module.misc;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import wtf.novoline.Novoline.Directories;
import wtf.novoline.command.MessagesCommand;
import wtf.novoline.event.PlayerKillEvent;
import wtf.novoline.module.Category;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.ScheduledTask;

public class Killsults extends AbstractMessageModule {
   private final NumberSetting killsults = settingManager.register(new NumberSetting(this, "Delay (ms)", 150, 0, 1000));
   @EventLink
   private final Listener<PlayerKillEvent> onPlayerKill = playerKillEvent -> {
      if (this.messages.size() > 0) {
         final String text = this.getRandomMessage().replace("{player}", playerKillEvent.entityPlayer.getName());
         int i = (int)this.killsults.getCurrentValue();
         if (i > 0) {
            novoline.getTaskScheduler().schedule(new ScheduledTask(i) {
               @Override
               public void run() {
                  Killsults.access$000().thePlayer.sendChatMessage(text);
               }
            });
         } else {
            minecraft.thePlayer.sendChatMessage(text);
         }
      }
   };

   static Minecraft access$000() {
      return minecraft;
   }

   public Killsults() {
      super(
         Category.MISC,
         "Killsults",
         "Send custom message when you kill a player",
         Directories.STORAGE_DIR.resolve("killsults.novo"),
         new ResourceLocation("novoline", "default_killsults.novo")
      );
      MessagesCommand messagesCommand = new MessagesCommand(this, "manage killsults", "killsults", "ks") {
         @Override
         public void printUsage() {
            super.printUsage();
            this.sendLocalized("command.killsults.ph_1");
         }
      };
      novoline.getCommandManager().register(messagesCommand);
   }
}
