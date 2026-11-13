package net.minecraft.entity.boss;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;

public class EntityDragonPart extends Entity {
   public final IEntityMultiPart entityDragonObj;
   public final String partName;

   @Override
   public boolean canBeCollidedWith() {
      return true;
   }

   @Override
   protected void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
   }

   @Override
   protected void entityInit() {
   }

   @Override
   protected void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      return this.isEntityInvulnerable(source) ? false : this.entityDragonObj.attackEntityFromPart(this, source, amount);
   }

   @Override
   public boolean isEntityEqual(Entity entityIn) {
      return this == entityIn || this.entityDragonObj == entityIn;
   }

   public EntityDragonPart(IEntityMultiPart parent, String partName, float base, float sizeHeight) {
      super(parent.getWorld());
      this.setSize(base, sizeHeight);
      this.entityDragonObj = parent;
      this.partName = partName;
   }
}
