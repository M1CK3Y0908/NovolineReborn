package net.minecraft.client.renderer.block.model;

import org.lwjgl.util.vector.Vector3f;

public class BlockPartRotation {
   public final Vector3f origin;
   public final net.minecraft.util.EnumFacing.Axis axis;
   public final float angle;
   public final boolean rescale;

   public BlockPartRotation(Vector3f originIn, net.minecraft.util.EnumFacing.Axis axisIn, float angleIn, boolean rescaleIn) {
      this.origin = originIn;
      this.axis = axisIn;
      this.angle = angleIn;
      this.rescale = rescaleIn;
   }
}
