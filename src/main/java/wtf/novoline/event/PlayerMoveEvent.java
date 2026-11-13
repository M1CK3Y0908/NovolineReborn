package wtf.novoline.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;

public class PlayerMoveEvent extends Event {
   public double x;
   public double y;
   public double z;

   public PlayerMoveEvent(double d, double d2, double d3) {
      super(23992540611656L, true);
      this.x = d;
      this.y = d2;
      this.z = d3;
   }

   public void setMotionFromInput(double d, float f, float f2) {
      EntityPlayerSP thePlayer2 = Minecraft.getMinecraft().thePlayer;
      MovementInput movementInput2 = thePlayer2.movementInput;
      float moveForward2 = movementInput2.moveForward;
      float moveStrafe2 = movementInput2.moveStrafe;
      float f3 = moveStrafe2 * moveStrafe2 + moveForward2 * moveForward2;
      if (f3 >= 1.0E-4F) {
         f3 = MathHelper.sqrt_float(f3);
         if (f3 < 1.0F) {
            f3 = 1.0F;
         }

         f3 = f / f3;
         moveStrafe2 *= f3;
         moveForward2 *= f3;
         float f4 = MathHelper.sin(f2 * (float) Math.PI / 180.0F);
         float f5 = MathHelper.cos(f2 * (float) Math.PI / 180.0F);
         double d2 = moveStrafe2 * d * f5 - moveForward2 * d * f4;
         double d3 = moveForward2 * d * f5 + moveStrafe2 * d * f4;
         this.x = thePlayer2.motionX = d2;
         this.z = thePlayer2.motionZ = d3;
      }
   }

   public void setSpeed(double d) {
      EntityPlayerSP thePlayer2 = Minecraft.getMinecraft().thePlayer;
      this.setSpeedCustom(d, thePlayer2.rotationYaw, thePlayer2.moveStrafing, thePlayer2.moveForward, 45);
   }

   public void setSpeedCustom(double d, float f, double d2, double d3, int i) {
      double d4 = d3;
      double d5 = d2;
      float f2 = f;
      if (d3 != 0.0) {
         if (d2 > 0.0) {
            f2 = f + (d3 > 0.0 ? -i : i);
         } else if (d2 < 0.0) {
            f2 = f + (d3 > 0.0 ? i : -i);
         }

         d5 = 0.0;
         if (d3 > 0.0) {
            d4 = 1.0;
         } else if (d3 < 0.0) {
            d4 = -1.0;
         }
      }

      if (d5 > 0.0) {
         d5 = 1.0;
      } else if (d5 < 0.0) {
         d5 = -1.0;
      }

      double d6 = Math.cos(Math.toRadians(f2 + 90.0F));
      double d7 = Math.sin(Math.toRadians(f2 + 90.0F));
      this.x = d4 * d * d6 + d5 * d * d7;
      this.z = d4 * d * d7 - d5 * d * d6;
   }
}
