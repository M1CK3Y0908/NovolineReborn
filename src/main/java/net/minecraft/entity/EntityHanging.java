package net.minecraft.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;

public abstract class EntityHanging extends Entity {
   public EnumFacing facingDirection;
   private int tickCounter1;
   protected BlockPos hangingPosition;

   public EntityHanging(World worldIn, BlockPos hangingPositionIn) {
      this(worldIn);
      this.hangingPosition = hangingPositionIn;
   }

   public EntityHanging(World worldIn) {
      super(worldIn);
      this.setSize(0.5F, 0.5F);
   }

   @Override
   protected void entityInit() {
   }

   private double getOffsetForSize(int size) {
      return size % 32 == 0 ? 0.5 : 0.0;
   }

   @Override
   public boolean hitByEntity(Entity entityIn) {
      return entityIn instanceof EntityPlayer ? this.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)entityIn), 0.0F) : false;
   }

   public abstract int getWidthPixels();

   @Override
   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.tickCounter1++ == 100 && !this.worldObj.isRemote) {
         this.tickCounter1 = 0;
         if (!this.isDead && !this.onValidSurface()) {
            this.setDead();
            this.onBroken((Entity)null);
         }
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      tagCompound.setByte("Facing", (byte)this.facingDirection.getHorizontalIndex());
      tagCompound.setInteger("TileX", this.getHangingPosition().getX());
      tagCompound.setInteger("TileY", this.getHangingPosition().getY());
      tagCompound.setInteger("TileZ", this.getHangingPosition().getZ());
   }

   public boolean onValidSurface() {
      if (!this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty()) {
         return false;
      }

      int i = Math.max(1, this.getWidthPixels() / 16);
      int i2 = Math.max(1, this.getHeightPixels() / 16);
      BlockPos blockPos2 = this.hangingPosition.offset(this.facingDirection.getOpposite());
      EnumFacing enumFacing = this.facingDirection.rotateYCCW();

      for (int i3 = 0; i3 < i; i3++) {
         for (int i4 = 0; i4 < i2; i4++) {
            BlockPos blockPos3 = blockPos2.offset(enumFacing, i3).up(i4);
            Block block2 = this.worldObj.getBlockState(blockPos3).getBlock();
            if (!block2.getMaterial().isSolid() && !BlockRedstoneDiode.isRedstoneRepeaterBlockID(block2)) {
               return false;
            }
         }
      }

      for (Entity entity2 : this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox())) {
         if (entity2 instanceof EntityHanging) {
            return false;
         }
      }

      return true;
   }

   public BlockPos getHangingPosition() {
      return this.hangingPosition;
   }

   @Override
   public void setPosition(double x, double y, double z) {
      this.posX = x;
      this.posY = y;
      this.posZ = z;
      BlockPos blockPos = this.hangingPosition;
      this.hangingPosition = new BlockPos(x, y, z);
      if (!this.hangingPosition.equals(blockPos)) {
         this.updateBoundingBox();
         this.isAirBorne = true;
      }
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      this.hangingPosition = new BlockPos(tagCompund.getInteger("TileX"), tagCompund.getInteger("TileY"), tagCompund.getInteger("TileZ"));
      EnumFacing enumFacing;
      if (tagCompund.hasKey("Direction", 99)) {
         enumFacing = EnumFacing.getHorizontal(tagCompund.getByte("Direction"));
         this.hangingPosition = this.hangingPosition.offset(enumFacing);
      } else if (tagCompund.hasKey("Facing", 99)) {
         enumFacing = EnumFacing.getHorizontal(tagCompund.getByte("Facing"));
      } else {
         enumFacing = EnumFacing.getHorizontal(tagCompund.getByte("Dir"));
      }

      this.updateFacingWithBoundingBox(enumFacing);
   }

   protected void updateFacingWithBoundingBox(EnumFacing facingDirectionIn) {
      Validate.notNull(facingDirectionIn);
      Validate.isTrue(facingDirectionIn.getAxis().isHorizontal());
      this.facingDirection = facingDirectionIn;
      this.prevRotationYaw = this.rotationYaw = this.facingDirection.getHorizontalIndex() * 90;
      this.updateBoundingBox();
   }

   @Override
   public void addVelocity(double d, double d2, double d3) {
      if (!this.worldObj.isRemote && !this.isDead && d * d + d2 * d2 + d3 * d3 > 0.0) {
         this.setDead();
         this.onBroken((Entity)null);
      }
   }

   @Override
   public boolean canBeCollidedWith() {
      return true;
   }

   @Override
   public void moveEntity(double d, double d2, double d3) {
      if (!this.worldObj.isRemote && !this.isDead && d * d + d2 * d2 + d3 * d3 > 0.0) {
         this.setDead();
         this.onBroken((Entity)null);
      }
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      if (!this.isDead && !this.worldObj.isRemote) {
         this.setDead();
         this.setBeenAttacked();
         this.onBroken(source.getEntity());
      }

      return true;
   }

   @Override
   protected boolean shouldSetPosAfterLoading() {
      return false;
   }

   private void updateBoundingBox() {
      if (this.facingDirection != null) {
         double d = this.hangingPosition.getX() + 0.5;
         double d2 = this.hangingPosition.getY() + 0.5;
         double d3 = this.hangingPosition.getZ() + 0.5;
         double offsetForSize = this.getOffsetForSize(this.getWidthPixels());
         double offsetForSize2 = this.getOffsetForSize(this.getHeightPixels());
         d -= this.facingDirection.getFrontOffsetX() * 0.46875;
         d3 -= this.facingDirection.getFrontOffsetZ() * 0.46875;
         d2 += offsetForSize2;
         EnumFacing enumFacing = this.facingDirection.rotateYCCW();
         d += offsetForSize * enumFacing.getFrontOffsetX();
         d3 += offsetForSize * enumFacing.getFrontOffsetZ();
         this.posX = d;
         this.posY = d2;
         this.posZ = d3;
         double widthPixels = this.getWidthPixels();
         double heightPixels = this.getHeightPixels();
         double widthPixels2 = this.getWidthPixels();
         if (this.facingDirection.getAxis() == net.minecraft.util.EnumFacing.Axis.Z) {
            widthPixels2 = 1.0;
         } else {
            widthPixels = 1.0;
         }

         widthPixels /= 32.0;
         heightPixels /= 32.0;
         widthPixels2 /= 32.0;
         this.setEntityBoundingBox(new AxisAlignedBB(d - widthPixels, d2 - heightPixels, d3 - widthPixels2, d + widthPixels, d2 + heightPixels, d3 + widthPixels2));
      }
   }

   @Override
   public EnumFacing getHorizontalFacing() {
      return this.facingDirection;
   }

   public abstract int getHeightPixels();

   public abstract void onBroken(Entity entity2);
}
