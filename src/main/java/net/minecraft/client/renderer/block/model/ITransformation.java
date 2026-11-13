package net.minecraft.client.renderer.block.model;

import javax.vecmath.Matrix4f;
import net.minecraft.util.EnumFacing;

public interface ITransformation {
   int rotate(EnumFacing enumFacing, int i);

   EnumFacing rotate(EnumFacing enumFacing);

   Matrix4f getMatrix4f();
}
