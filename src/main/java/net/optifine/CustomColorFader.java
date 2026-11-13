package net.optifine;

import net.minecraft.src.Config;
import net.minecraft.util.Vec3;

public class CustomColorFader {
   private Vec3 color = null;
   private long timeUpdate = System.currentTimeMillis();

   public Vec3 getColor(double x, double y, double z) {
      if (this.color == null) {
         this.color = new Vec3(x, y, z);
         return this.color;
      }

      long l = System.currentTimeMillis();
      long l2 = l - this.timeUpdate;
      if (l2 == 0L) {
         return this.color;
      }

      this.timeUpdate = l;
      if (Math.abs(x - this.color.xCoord) < 0.004 && Math.abs(y - this.color.yCoord) < 0.004 && Math.abs(z - this.color.zCoord) < 0.004) {
         return this.color;
      }

      double d = l2 * 0.001;
      d = Config.limit(d, 0.0, 1.0);
      double d2 = x - this.color.xCoord;
      double d3 = y - this.color.yCoord;
      double d4 = z - this.color.zCoord;
      double d5 = this.color.xCoord + d2 * d;
      double d6 = this.color.yCoord + d3 * d;
      double d7 = this.color.zCoord + d4 * d;
      this.color = new Vec3(d5, d6, d7);
      return this.color;
   }
}
