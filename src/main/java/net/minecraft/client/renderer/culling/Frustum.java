package net.minecraft.client.renderer.culling;

import net.minecraft.util.AxisAlignedBB;

public class Frustum implements ICamera {
   private double xPosition;
   private ClippingHelper clippingHelper;
   private double zPosition;
   private double yPosition;

   public Frustum(ClippingHelper clippingHelper2) {
      this.clippingHelper = clippingHelper2;
   }

   public boolean isBoxInFrustumFully(double arg1, double arg3, double arg5, double arg7, double arg9, double arg11) {
      return this.clippingHelper
         .isBoxInFrustumFully(
            arg1 - this.xPosition, arg3 - this.yPosition, arg5 - this.zPosition, arg7 - this.xPosition, arg9 - this.yPosition, arg11 - this.zPosition
         );
   }

   @Override
   public boolean isBoundingBoxInFrustum(AxisAlignedBB axisAlignedBB) {
      return this.isBoxInFrustum(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
   }

   @Override
   public void setPosition(double d, double d2, double d3) {
      this.xPosition = d;
      this.yPosition = d2;
      this.zPosition = d3;
   }

   public boolean isBoxInFrustum(double d, double d2, double d3, double d4, double d5, double d6) {
      return this.clippingHelper
         .isBoxInFrustum(
            d - this.xPosition,
            d2 - this.yPosition,
            d3 - this.zPosition,
            d4 - this.xPosition,
            d5 - this.yPosition,
            d6 - this.zPosition
         );
   }
}
