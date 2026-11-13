package wtf.novoline.module.movement;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import wtf.novoline.enums.ServerType;
import wtf.novoline.event.MoveEntityEvent2;
import wtf.novoline.event.UpdateEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.hypixel.HypixelState;
import wtf.novoline.utils.player.PlayerUtils;

public class Step extends Module {
   private final NumberSetting step = settingManager.register(new NumberSetting(this, "Speed", 0.3F, 0.1F, 1.5F));
   private final double[] doubleArray = new double[]{
      0.41999998688698, 0.7531999805212, 1.0 + Math.random() / 10000.0, 1.0 + Math.random() / 100.0, 0.98 + Math.random() / 1000.0
   };
   @EventLink
   private final Listener<UpdateEvent> onUpdate = updateEvent -> {
      minecraft.thePlayer.stepHeight = !PlayerUtils.isInsideLiquid()
            && !PlayerUtils.isStandingOnLiquid()
            && !ModuleManager.speed.isEnabled()
            && !ModuleManager.phase.isEnabled()
            && minecraft.thePlayer.onGround
         ? 1.5F
         : 0.6F;
      if (this.timerModified) {
         minecraft.timer.timerSpeed = 1.0F;
         this.timerModified = false;
      }
   };
   @EventLink
   private final Listener<MoveEntityEvent2> onMoveEntityEvent2 = moveEntityEvent2 -> {
      if (minecraft.thePlayer != null) {
         double d = minecraft.thePlayer.getEntityBoundingBox().minY - minecraft.thePlayer.posY;
         double posX2 = minecraft.thePlayer.posX;
         double posY2 = minecraft.thePlayer.posY;
         double posZ2 = minecraft.thePlayer.posZ;
         if (d > 0.6 && d <= 1.5) {
            if (HypixelState.isServerType(ServerType.HYPIXEL)) {
               this.sendPacketUnregistered(new C0BPacketEntityAction(minecraft.thePlayer, Action.START_SNEAKING));
               minecraft.thePlayer.setSprinting(false);
               PlayerUtils.setSpeed(0.0);
            }

            minecraft.timer.timerSpeed = this.step.getCurrentValue();
            this.timerModified = true;
            if (HypixelState.isServerType(ServerType.HYPIXEL)) {
               if (d <= 1.0) {
                  for (int i = 0; i < 4; i++) {
                     this.sendPacketUnregistered(new C03PacketPlayer(true));
                  }
               }

               if (d <= 1.5) {
                  for (double d2 : this.doubleArray) {
                     this.sendPacketUnregistered(new C04PacketPlayerPosition(posX2, posY2 + d2, posZ2, false));
                  }
               }
            }

            if (HypixelState.isServerType(ServerType.HYPIXEL)) {
               this.sendPacketUnregistered(new C0BPacketEntityAction(minecraft.thePlayer, Action.STOP_SNEAKING));
            }

            minecraft.thePlayer.stepHeight = 0.6F;
         }
      }
   };
   private boolean timerModified;

   public Step() {
      super(Category.MOVEMENT, "Step", "");
   }

   @Override
   protected void onDisable() {
      minecraft.timer.timerSpeed = 1.0F;
      minecraft.thePlayer.stepHeight = 0.6F;
   }
}
