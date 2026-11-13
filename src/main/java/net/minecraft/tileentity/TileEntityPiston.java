package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileEntityPiston extends TileEntity implements ITickable {
   private float lastProgress;
   private List<Entity> entitiesToMove = Lists.newArrayList();
   private IBlockState pistonState;
   private float progress;
   private EnumFacing pistonFacing;
   private boolean extending;
   private boolean shouldHeadBeRendered;

   @Override
   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      this.pistonState = Block.getBlockById(compound.getInteger("blockId")).getStateFromMeta(compound.getInteger("blockData"));
      this.pistonFacing = EnumFacing.getFront(compound.getInteger("facing"));
      this.lastProgress = this.progress = compound.getFloat("progress");
      this.extending = compound.getBoolean("extending");
   }

   public TileEntityPiston(IBlockState pistonStateIn, EnumFacing pistonFacingIn, boolean extendingIn, boolean shouldHeadBeRenderedIn) {
      this.pistonState = pistonStateIn;
      this.pistonFacing = pistonFacingIn;
      this.extending = extendingIn;
      this.shouldHeadBeRendered = shouldHeadBeRenderedIn;
   }

   public float getFloat2(float f) {
      return this.extending
         ? (this.getProgress(f) - 1.0F) * this.pistonFacing.getFrontOffsetZ()
         : (1.0F - this.getProgress(f)) * this.pistonFacing.getFrontOffsetZ();
   }

   public float getFloat(float f) {
      return this.extending
         ? (this.getProgress(f) - 1.0F) * this.pistonFacing.getFrontOffsetY()
         : (1.0F - this.getProgress(f)) * this.pistonFacing.getFrontOffsetY();
   }

   public EnumFacing getFacing() {
      return this.pistonFacing;
   }

   @Override
   public void writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      compound.setInteger("blockId", Block.getIdFromBlock(this.pistonState.getBlock()));
      compound.setInteger("blockData", this.pistonState.getBlock().getMetaFromState(this.pistonState));
      compound.setInteger("facing", this.pistonFacing.getIndex());
      compound.setFloat("progress", this.lastProgress);
      compound.setBoolean("extending", this.extending);
   }

   public float getFloat3(float f) {
      return this.extending
         ? (this.getProgress(f) - 1.0F) * this.pistonFacing.getFrontOffsetX()
         : (1.0F - this.getProgress(f)) * this.pistonFacing.getFrontOffsetX();
   }

   @Override
   public void update() {
      this.lastProgress = this.progress;
      if (this.lastProgress >= 1.0F) {
         this.launchWithSlimeBlock(1.0F, 0.25F);
         this.worldObj.removeTileEntity(this.pos);
         this.invalidate();
         if (this.worldObj.getBlockState(this.pos).getBlock() == Blocks.piston_extension) {
            this.worldObj.setBlockState(this.pos, this.pistonState, 3);
            this.worldObj.notifyBlockOfStateChange(this.pos, this.pistonState.getBlock());
         }
      } else {
         this.progress += 0.5F;
         if (this.progress >= 1.0F) {
            this.progress = 1.0F;
         }

         if (this.extending) {
            this.launchWithSlimeBlock(this.progress, this.progress - this.lastProgress + 0.0625F);
         }
      }
   }

   public boolean isExtending() {
      return this.extending;
   }

   public TileEntityPiston() {
   }

   public IBlockState getPistonState() {
      return this.pistonState;
   }

   public float getProgress(float ticks) {
      if (ticks > 1.0F) {
         ticks = 1.0F;
      }

      return this.lastProgress + (this.progress - this.lastProgress) * ticks;
   }

   public boolean shouldPistonHeadBeRendered() {
      return this.shouldHeadBeRendered;
   }

   @Override
   public int getBlockMetadata() {
      return 0;
   }

   private void launchWithSlimeBlock(float f, float f2) {
      if (this.extending) {
         f = 1.0F - f;
      } else {
         f--;
      }

      AxisAlignedBB boundingBox = Blocks.piston_extension.getBoundingBox(this.worldObj, this.pos, this.pistonState, f, this.pistonFacing);
      if (boundingBox != null) {
         List entitiesWithinAABBExcludingEntity = this.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)null, boundingBox);
         if (!entitiesWithinAABBExcludingEntity.isEmpty()) {
            this.entitiesToMove.addAll(entitiesWithinAABBExcludingEntity);

            for (Entity entity2 : this.entitiesToMove) {
               if (this.pistonState.getBlock() == Blocks.slime_block && this.extending) {
                  switch (this.pistonFacing.getAxis()) {
                     case X:
                        entity2.motionX = this.pistonFacing.getFrontOffsetX();
                        break;
                     case Y:
                        entity2.motionY = this.pistonFacing.getFrontOffsetY();
                        break;
                     case Z:
                        entity2.motionZ = this.pistonFacing.getFrontOffsetZ();
                  }
               } else {
                  entity2.moveEntity(
                     f2 * this.pistonFacing.getFrontOffsetX(), f2 * this.pistonFacing.getFrontOffsetY(), f2 * this.pistonFacing.getFrontOffsetZ()
                  );
               }
            }

            this.entitiesToMove.clear();
         }
      }
   }

   public void clearPistonTileEntity() {
      if (this.lastProgress < 1.0F && this.worldObj != null) {
         this.lastProgress = this.progress = 1.0F;
         this.worldObj.removeTileEntity(this.pos);
         this.invalidate();
         if (this.worldObj.getBlockState(this.pos).getBlock() == Blocks.piston_extension) {
            this.worldObj.setBlockState(this.pos, this.pistonState, 3);
            this.worldObj.notifyBlockOfStateChange(this.pos, this.pistonState.getBlock());
         }
      }
   }
}
