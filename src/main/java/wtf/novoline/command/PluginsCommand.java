package wtf.novoline.command;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.Arrays;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.util.EnumChatFormatting;
import wtf.novoline.event.PacketReceiveEvent;
import wtf.novoline.event.RunTickEvent;
import wtf.novoline.utils.time.Stopwatch;

public class PluginsCommand extends Command {
   private final Minecraft minecraft = Minecraft.getMinecraft();
   @EventLink
   private final Listener<PacketReceiveEvent> onPacketReceive = packetReceiveEvent -> {
      Packet packet2 = packetReceiveEvent.packet;
      if (packet2 instanceof S3APacketTabComplete) {
         String text = Arrays.stream(((S3APacketTabComplete)packet2).getMatches2())
            .filter(string -> string.contains(":"))
            .map(string -> string.split(":")[0].replace("/", ""))
            .distinct()
            .collect(Collectors.joining(EnumChatFormatting.GRAY + ", " + EnumChatFormatting.RESET));
         if (text.isEmpty()) {
            this.sendLocalized("command.plugins.not_found");
         } else {
            this.sendLocalized("command.plugins.list", text);
         }

         this.novoline.getBus().unsubscribe(this);
      }
   };
   private final Stopwatch responseTimer = new Stopwatch();
   @EventLink
   private final Listener<RunTickEvent> onRunTick = runTickEvent -> {
      if (this.responseTimer.hasElapsed(2000L)) {
         this.sendLocalized("command.plugins.no_response");
         this.novoline.getBus().unsubscribe(this);
      }
   };

   @Override
   public void execute(String[] texts) {
      this.sendLocalized("command.plugins.req_sent");
      this.responseTimer.reset();
      this.minecraft.thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete("/"));
      this.novoline.getBus().subscribe(this);
   }

   public PluginsCommand() {
      super("attempts to find installed plugins on the server", "plugins", "pl");
   }
}
