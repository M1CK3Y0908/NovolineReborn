package net.minecraft.block.material;

public class MaterialLogic extends Material {
   @Override
   public boolean blocksLight() {
      return false;
   }

   public MaterialLogic(MapColor color) {
      super(color);
      this.setAdventureModeExempt();
   }

   @Override
   public boolean isSolid() {
      return false;
   }

   @Override
   public boolean blocksMovement() {
      return false;
   }
}
