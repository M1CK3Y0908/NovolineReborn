package net.minecraft.entity;

import net.minecraft.block.BlockFence;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class EntityLeashKnot extends EntityHanging {
   @Override
   public boolean isInRangeToRenderDist(double distance) {
      return distance < 1024.0;
   }

   @Override
   public void onBroken(Entity brokenEntity) {
   }

   @Override
   public int getWidthPixels() {
      return 9;
   }

   public EntityLeashKnot(World worldIn, BlockPos hangingPositionIn) {
      super(worldIn, hangingPositionIn);
      this.setPosition(hangingPositionIn.getX() + 0.5, hangingPositionIn.getY() + 0.5, hangingPositionIn.getZ() + 0.5);
      this.setEntityBoundingBox(
         new AxisAlignedBB(this.posX - 0.1875, this.posY - 0.25 + 0.125, this.posZ - 0.1875, this.posX + 0.1875, this.posY + 0.25 + 0.125, this.posZ + 0.1875)
      );
   }

   public static EntityLeashKnot createKnot(World worldIn, BlockPos fence) {
      EntityLeashKnot entityLeashKnot = new EntityLeashKnot(worldIn, fence);
      entityLeashKnot.forceSpawn = true;
      worldIn.spawnEntityInWorld(entityLeashKnot);
      return entityLeashKnot;
   }

   public EntityLeashKnot(World worldIn) {
      super(worldIn);
   }

   @Override
   public boolean interactFirst(EntityPlayer playerIn) {
      ItemStack heldItem = playerIn.getHeldItem();
      boolean flag = false;
      if (heldItem != null && heldItem.getItem() == Items.lead && !this.worldObj.isRemote) {
         double d = 7.0;

         for (EntityLiving entityLiving : this.worldObj
            .getEntitiesWithinAABB(
               EntityLiving.class,
               new AxisAlignedBB(this.posX - d, this.posY - d, this.posZ - d, this.posX + d, this.posY + d, this.posZ + d)
            )) {
            if (entityLiving.getLeashed() && entityLiving.getLeashedToEntity() == playerIn) {
               entityLiving.setLeashedToEntity(this, true);
               flag = true;
            }
         }
      }

      if (!this.worldObj.isRemote && !flag) {
         this.setDead();
         if (playerIn.capabilities.isCreativeMode) {
            double d2 = 7.0;

            for (EntityLiving entityLiving2 : this.worldObj
               .getEntitiesWithinAABB(
                  EntityLiving.class,
                  new AxisAlignedBB(this.posX - d2, this.posY - d2, this.posZ - d2, this.posX + d2, this.posY + d2, this.posZ + d2)
               )) {
               if (entityLiving2.getLeashed() && entityLiving2.getLeashedToEntity() == this) {
                  entityLiving2.clearLeashed(true, false);
               }
            }
         }
      }

      return true;
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
   }

   @Override
   public boolean writeToNBTOptional(NBTTagCompound tagCompund) {
      return false;
   }

   @Override
   public void updateFacingWithBoundingBox(EnumFacing facingDirectionIn) {
   }

   @Override
   protected void entityInit() {
      super.entityInit();
   }

   @Override
   public float getEyeHeight() {
      return -0.0625F;
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
   }

   public static EntityLeashKnot getKnotForPosition(World worldIn, BlockPos pos) {
      int x = pos.getX();
      int y = pos.getY();
      int z = pos.getZ();

      for (EntityLeashKnot entityLeashKnot : worldIn.getEntitiesWithinAABB(
         EntityLeashKnot.class, new AxisAlignedBB(x - 1.0, y - 1.0, z - 1.0, x + 1.0, y + 1.0, z + 1.0)
      )) {
         if (entityLeashKnot.getHangingPosition().equals(pos)) {
            return entityLeashKnot;
         }
      }

      return null;
   }

   @Override
   public boolean onValidSurface() {
      return this.worldObj.getBlockState(this.hangingPosition).getBlock() instanceof BlockFence;
   }

   @Override
   public int getHeightPixels() {
      return 9;
   }
}
