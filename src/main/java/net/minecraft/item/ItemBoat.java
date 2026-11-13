package net.minecraft.item;

import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemBoat extends Item {
   @Override
   public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
      float f = 1.0F;
      float f2 = playerIn.prevRotationPitch + (playerIn.rotationPitch - playerIn.prevRotationPitch) * f;
      float f3 = playerIn.prevRotationYaw + (playerIn.rotationYaw - playerIn.prevRotationYaw) * f;
      double d = playerIn.prevPosX + (playerIn.posX - playerIn.prevPosX) * f;
      double d2 = playerIn.prevPosY + (playerIn.posY - playerIn.prevPosY) * f + playerIn.getEyeHeight();
      double d3 = playerIn.prevPosZ + (playerIn.posZ - playerIn.prevPosZ) * f;
      Vec3 vec3 = new Vec3(d, d2, d3);
      float f4 = MathHelper.cos(-f3 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f5 = MathHelper.sin(-f3 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f6 = -MathHelper.cos(-f2 * (float) (Math.PI / 180.0));
      float f7 = MathHelper.sin(-f2 * (float) (Math.PI / 180.0));
      float f8 = f5 * f6;
      float f9 = f4 * f6;
      double d4 = 5.0;
      Vec3 vec32 = vec3.addVector(f8 * d4, f7 * d4, f9 * d4);
      MovingObjectPosition movingObjectPosition = worldIn.rayTraceBlocks(vec3, vec32, true);
      if (movingObjectPosition == null) {
         return itemStackIn;
      }

      Vec3 look = playerIn.getLook(f);
      boolean flag = false;
      float f10 = 1.0F;
      List entitiesWithinAABBExcludingEntity = worldIn.getEntitiesWithinAABBExcludingEntity(
         playerIn, playerIn.getEntityBoundingBox().addCoord(look.xCoord * d4, look.yCoord * d4, look.zCoord * d4).expand(f10, f10, f10)
      );

      for (int i = 0; i < entitiesWithinAABBExcludingEntity.size(); i++) {
         Entity entity2 = (Entity)entitiesWithinAABBExcludingEntity.get(i);
         if (entity2.canBeCollidedWith()) {
            float collisionBorderSize = entity2.getCollisionBorderSize();
            if (entity2.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize).isVecInside(vec3)) {
               flag = true;
            }
         }
      }

      if (flag) {
         return itemStackIn;
      }

      if (movingObjectPosition.typeOfHit == net.minecraft.util.MovingObjectPosition.MovingObjectType.BLOCK) {
         BlockPos blockPos = movingObjectPosition.getBlockPos();
         if (worldIn.getBlockState(blockPos).getBlock() == Blocks.snow_layer) {
            blockPos = blockPos.down();
         }

         EntityBoat entityBoat = new EntityBoat(worldIn, blockPos.getX() + 0.5F, blockPos.getY() + 1.0F, blockPos.getZ() + 0.5F);
         entityBoat.rotationYaw = ((MathHelper.floor_double(playerIn.rotationYaw * 4.0F / 360.0F + 0.5) & 3) - 1) * 90;
         if (!worldIn.getCollidingBoundingBoxes(entityBoat, entityBoat.getEntityBoundingBox().expand(-0.1, -0.1, -0.1)).isEmpty()) {
            return itemStackIn;
         }

         if (!worldIn.isRemote) {
            worldIn.spawnEntityInWorld(entityBoat);
         }

         if (!playerIn.capabilities.isCreativeMode) {
            itemStackIn.stackSize--;
         }

         playerIn.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
      }

      return itemStackIn;
   }

   public ItemBoat() {
      this.maxStackSize = 1;
      this.setCreativeTab(CreativeTabs.tabTransport);
   }
}
