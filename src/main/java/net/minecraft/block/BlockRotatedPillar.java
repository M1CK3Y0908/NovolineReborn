package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;

public abstract class BlockRotatedPillar extends Block {
   public static final PropertyEnum<net.minecraft.util.EnumFacing.Axis> AXIS = PropertyEnum.create("axis", net.minecraft.util.EnumFacing.Axis.class);

   protected BlockRotatedPillar(Material material2, MapColor mapColor) {
      super(material2, mapColor);
   }

   protected BlockRotatedPillar(Material materialIn) {
      super(materialIn, materialIn.getMaterialMapColor());
   }
}
