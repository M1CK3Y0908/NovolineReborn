package net.minecraft.client.renderer.culling;

import net.minecraft.util.AxisAlignedBB;

public interface ICamera {
   void setPosition(double d, double d2, double d3);

   boolean isBoundingBoxInFrustum(AxisAlignedBB axisAlignedBB);
}
