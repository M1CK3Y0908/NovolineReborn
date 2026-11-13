package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;

public abstract class BlockDirectional extends Block {
   public static final PropertyDirection FACING = PropertyDirection.create("facing", net.minecraft.util.EnumFacing.Plane.HORIZONTAL);

   protected BlockDirectional(Material material2, MapColor mapColor) {
      super(material2, mapColor);
   }

   protected BlockDirectional(Material materialIn) {
      super(materialIn);
   }
}
