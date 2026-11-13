package wtf.novoline.module.combat;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import wtf.novoline.event.PacketSendEvent;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.utils.time.Stopwatch;

public class Criticals extends Module {
   private final ComboSetting criticals = settingManager.register(new ComboSetting(this, "Mode", 0, "Hypixel"));
   private final double[] doubleArray = new double[]{0.104080378093037, 0.105454222033912, 0.102888018147468, 0.099634532004642};
   private double lastPacketY = -1.0;
   public final Stopwatch criticalTimer = new Stopwatch();
   @EventLink
   public final Listener<PacketSendEvent> onPacketSend = packetSendEvent -> {
      if (packetSendEvent.packet instanceof C03PacketPlayer c03PacketPlayer) {
         this.lastPacketY = c03PacketPlayer.y;
      }
   };
   @EventLink
   public final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> this.setSuffix(this.criticals.getCurrentElementAsString());

   public void sendCriticalPackets() {
      this.criticalTimer.reset();
      if (this.lastPacketY - (int)this.lastPacketY == 0.0) {
         for (double d : new double[]{
            0.075
               + ThreadLocalRandom.current().nextDouble(0.008) * (new Random().nextBoolean() ? 0.96 : 0.97)
               + minecraft.thePlayer.ticksExisted % 0.0215 * 0.92,
            (new Random().nextBoolean() ? 0.010634691223 : 0.013999777)
               * (new Random().nextBoolean() ? 0.95 : 0.96)
               * this.doubleArray[new Random().nextInt(this.doubleArray.length)]
               * 9.5
         }) {
            this.sendPacketUnregistered(
               new C04PacketPlayerPosition(
                  minecraft.thePlayer.posX,
                  minecraft.thePlayer.posY + d * (1.0 + ThreadLocalRandom.current().nextDouble(-0.005, 0.005)),
                  minecraft.thePlayer.posZ,
                  false
               )
            );
         }
      }
   }

   public Criticals() {
      super(Category.COMBAT, "Criticals", "");
   }
}
