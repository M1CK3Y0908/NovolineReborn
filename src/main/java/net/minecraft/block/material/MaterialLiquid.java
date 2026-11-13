package net.minecraft.block.material;

public class MaterialLiquid extends Material {
   @Override
   public boolean isLiquid() {
      return true;
   }

   @Override
   public boolean blocksMovement() {
      return false;
   }

   public MaterialLiquid(MapColor color) {
      super(color);
      this.setReplaceable();
      this.setNoPushMobility();
   }

   @Override
   public boolean isSolid() {
      return false;
   }
}
