package wtf.novoline.module.movement;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.util.MathHelper;
import wtf.novoline.event.BlockCollisionEvent;
import wtf.novoline.event.OnUpdateWalkingPlayerEvent;
import wtf.novoline.event.PlayerMoveEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.utils.player.PlayerUtils;

public class Phase extends Module {
   @EventLink
   public final Listener<BlockCollisionEvent> onBlockCollision = blockCollisionEvent -> {
      if (minecraft.thePlayer != null && PlayerUtils.isInsideBlock()) {
         blockCollisionEvent.axisAlignedBB = null;
      }
   };
   @EventLink
   public final Listener<PlayerMoveEvent> onPlayerMove = playerMoveEvent -> {
      if (minecraft.thePlayer != null && PlayerUtils.isInsideBlock()) {
         if (minecraft.gameSettings.keyBindJump.isKeyDown()) {
            playerMoveEvent.y = minecraft.thePlayer.motionY += 0.09F;
         } else if (minecraft.gameSettings.keyBindSneak.isKeyDown()) {
            playerMoveEvent.y = minecraft.thePlayer.motionY -= 0.09F;
         } else {
            playerMoveEvent.y = minecraft.thePlayer.motionY = 0.0;
         }

         playerMoveEvent.setSpeed(PlayerUtils.getDefaultMoveSpeed());
      }
   };
   @EventLink
   public final Listener<OnUpdateWalkingPlayerEvent> onOnUpdateWalkingPlayer = onUpdateWalkingPlayerEvent -> {
      if (minecraft.thePlayer.stepHeight > 0.0F) {
         minecraft.thePlayer.stepHeight = 0.0F;
      }

      float moveStrafing2 = minecraft.thePlayer.moveStrafing;
      float moveForward2 = minecraft.thePlayer.moveForward;
      float rotationYaw2 = minecraft.thePlayer.rotationYaw;
      double d = 0.3;
      double d2 = -MathHelper.sin((float)Math.toRadians(rotationYaw2));
      double d3 = MathHelper.cos((float)Math.toRadians(rotationYaw2));
      double d4 = moveForward2 * d * d2 + moveStrafing2 * d * d3;
      double d5 = moveForward2 * d * d3 - moveStrafing2 * d * d2;
      if (minecraft.thePlayer.isCollidedHorizontally && !minecraft.thePlayer.isOnLadder() && minecraft.thePlayer.onGround) {
         double posX2 = minecraft.thePlayer.posX;
         double posY2 = minecraft.thePlayer.posY;
         double posZ2 = minecraft.thePlayer.posZ;
         this.sendPacketUnregistered(new C04PacketPlayerPosition(posX2 + d4, posY2, posZ2 + d5, true));
         this.sendPacketUnregistered(new C04PacketPlayerPosition(posX2, posY2 + 3.0, posZ2, true));
         minecraft.thePlayer.setPosition(posX2 + d4, posY2, posZ2 + d5);
      }
   };

   public Phase() {
      super(Category.MOVEMENT, "Phase", "");
   }

   @Override
   protected void onDisable() {
      minecraft.thePlayer.stepHeight = 0.6F;
   }
}
