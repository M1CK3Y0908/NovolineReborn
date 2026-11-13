package wtf.novoline.module.world;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.util.EnumChatFormatting;
import wtf.novoline.Novoline;
import wtf.novoline.event.PacketReceiveEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;

public class LightningTracker extends Module {
   @EventLink
   private final Listener<PacketReceiveEvent> onPacketReceive = packetReceiveEvent -> {
      if (packetReceiveEvent.packet instanceof S29PacketSoundEffect s29PacketSoundEffect && s29PacketSoundEffect.getSoundName().equals("ambient.weather.thunder")) {
         Novoline novoline2 = novoline;
         EnumChatFormatting enumChatFormatting = EnumChatFormatting.AQUA;
         EnumChatFormatting enumChatFormatting2 = EnumChatFormatting.WHITE;
         double x = s29PacketSoundEffect.getX();
         double z = s29PacketSoundEffect.getZ();
         double y = s29PacketSoundEffect.getY();
         double d = x;
         novoline2.sendChatMessage(enumChatFormatting + "Lightning detected!" + enumChatFormatting2 + " Coords: X: " + d + " Y: " + y + " Z: " + z);
      }
   };

   public LightningTracker() {
      super(Category.WORLD, "LightningTracker", "Alert you in the chat when a lightning strikes");
   }
}
