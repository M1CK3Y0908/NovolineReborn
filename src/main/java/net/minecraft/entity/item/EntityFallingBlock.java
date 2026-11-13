package net.minecraft.entity.item;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityFallingBlock extends Entity {
   private IBlockState fallTile;
   private boolean hurtEntities;
   public NBTTagCompound tileEntityData;
   public boolean shouldDropItem = true;
   private int fallHurtMax = 40;
   private float fallHurtAmount = 2.0F;
   private boolean canSetAsBlock;
   public int fallTime;

   @Override
   public boolean canRenderOnFire() {
      return false;
   }

   public EntityFallingBlock(World worldIn) {
      super(worldIn);
   }

   @Override
   public void onUpdate() {
      Block block2 = this.fallTile.getBlock();
      if (block2.getMaterial() == Material.air) {
         this.setDead();
      } else {
         this.prevPosX = this.posX;
         this.prevPosY = this.posY;
         this.prevPosZ = this.posZ;
         if (this.fallTime++ == 0) {
            BlockPos blockPos = new BlockPos(this);
            if (this.worldObj.getBlockState(blockPos).getBlock() == block2) {
               this.worldObj.setBlockToAir(blockPos);
            } else if (!this.worldObj.isRemote) {
               this.setDead();
               return;
            }
         }

         this.motionY -= 0.04F;
         this.moveEntity(this.motionX, this.motionY, this.motionZ);
         this.motionX *= 0.98F;
         this.motionY *= 0.98F;
         this.motionZ *= 0.98F;
         if (!this.worldObj.isRemote) {
            BlockPos blockPos2 = new BlockPos(this);
            if (this.onGround) {
               this.motionX *= 0.7F;
               this.motionZ *= 0.7F;
               this.motionY *= -0.5;
               if (this.worldObj.getBlockState(blockPos2).getBlock() != Blocks.piston_extension) {
                  this.setDead();
                  if (!this.canSetAsBlock) {
                     if (this.worldObj.canBlockBePlaced(block2, blockPos2, true, EnumFacing.UP, (Entity)null, (ItemStack)null)
                        && !BlockFalling.canFallInto(this.worldObj, blockPos2.down())
                        && this.worldObj.setBlockState(blockPos2, this.fallTile, 3)) {
                        if (block2 instanceof BlockFalling) {
                           ((BlockFalling)block2).onEndFalling(this.worldObj, blockPos2);
                        }

                        if (this.tileEntityData != null && block2 instanceof ITileEntityProvider) {
                           TileEntity tileEntity = this.worldObj.getTileEntity(blockPos2);
                           if (tileEntity != null) {
                              NBTTagCompound nbtTagCompound = new NBTTagCompound();
                              tileEntity.writeToNBT(nbtTagCompound);

                              for (String text : this.tileEntityData.getKeySet()) {
                                 NBTBase tag = this.tileEntityData.getTag(text);
                                 if (!text.equals("x") && !text.equals("y") && !text.equals("z")) {
                                    nbtTagCompound.setTag(text, tag.copy());
                                 }
                              }

                              tileEntity.readFromNBT(nbtTagCompound);
                              tileEntity.markDirty();
                           }
                        }
                     } else if (this.shouldDropItem && this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
                        this.entityDropItem(new ItemStack(block2, 1, block2.damageDropped(this.fallTile)), 0.0F);
                     }
                  }
               }
            } else if (this.fallTime > 100 && !this.worldObj.isRemote && (blockPos2.getY() < 1 || blockPos2.getY() > 256) || this.fallTime > 600) {
               if (this.shouldDropItem && this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
                  this.entityDropItem(new ItemStack(block2, 1, block2.damageDropped(this.fallTile)), 0.0F);
               }

               this.setDead();
            }
         }
      }
   }

   @Override
   public void addEntityCrashInfo(CrashReportCategory category) {
      super.addEntityCrashInfo(category);
      if (this.fallTile != null) {
         Block block = this.fallTile.getBlock();
         category.addCrashSection("Immitating block ID", Block.getIdFromBlock(block));
         category.addCrashSection("Immitating block data", block.getMetaFromState(this.fallTile));
      }
   }

   @Override
   protected boolean canTriggerWalking() {
      return false;
   }

   public EntityFallingBlock(World worldIn, double x, double y, double z, IBlockState fallingBlockState) {
      super(worldIn);
      this.fallTile = fallingBlockState;
      this.preventEntitySpawning = true;
      this.setSize(0.98F, 0.98F);
      this.setPosition(x, y, z);
      this.motionX = 0.0;
      this.motionY = 0.0;
      this.motionZ = 0.0;
      this.prevPosX = x;
      this.prevPosY = y;
      this.prevPosZ = z;
   }

   @Override
   protected void readEntityFromNBT(NBTTagCompound tagCompund) {
      int i = tagCompund.getByte("Data") & 255;
      if (tagCompund.hasKey("Block", 8)) {
         this.fallTile = Block.getBlockFromName(tagCompund.getString("Block")).getStateFromMeta(i);
      } else if (tagCompund.hasKey("TileID", 99)) {
         this.fallTile = Block.getBlockById(tagCompund.getInteger("TileID")).getStateFromMeta(i);
      } else {
         this.fallTile = Block.getBlockById(tagCompund.getByte("Tile") & 255).getStateFromMeta(i);
      }

      this.fallTime = tagCompund.getByte("Time") & 255;
      Block block = this.fallTile.getBlock();
      if (tagCompund.hasKey("HurtEntities", 99)) {
         this.hurtEntities = tagCompund.getBoolean("HurtEntities");
         this.fallHurtAmount = tagCompund.getFloat("FallHurtAmount");
         this.fallHurtMax = tagCompund.getInteger("FallHurtMax");
      } else if (block == Blocks.anvil) {
         this.hurtEntities = true;
      }

      if (tagCompund.hasKey("DropItem", 99)) {
         this.shouldDropItem = tagCompund.getBoolean("DropItem");
      }

      if (tagCompund.hasKey("TileEntityData", 10)) {
         this.tileEntityData = tagCompund.getCompoundTag("TileEntityData");
      }

      if (block == null || block.getMaterial() == Material.air) {
         this.fallTile = Blocks.sand.getDefaultState();
      }
   }

   @Override
   public void fall(float distance, float damageMultiplier) {
      Block block = this.fallTile.getBlock();
      if (this.hurtEntities) {
         int i = MathHelper.ceiling_float_int(distance - 1.0F);
         if (i > 0) {
            List<Entity> list = Lists.newArrayList(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox()));
            boolean flag = block == Blocks.anvil;
            DamageSource damageSource = flag ? DamageSource.anvil : DamageSource.fallingBlock;
            Iterator iterator2 = list.iterator();

            while (iterator2.hasNext()) {
               ((Entity)iterator2.next())
                  .attackEntityFrom(damageSource, Math.min(MathHelper.floor_float(i * this.fallHurtAmount), this.fallHurtMax));
            }

            if (flag && this.rand.nextFloat() < 0.05F + i * 0.05) {
               int secondIntValuex = this.fallTile.getValue(BlockAnvil.DAMAGE);
               if (++secondIntValuex > 2) {
                  this.canSetAsBlock = true;
               } else {
                  this.fallTile = this.fallTile.withProperty(BlockAnvil.DAMAGE, secondIntValuex);
               }
            }
         }
      }
   }

   public IBlockState getBlock() {
      return this.fallTile;
   }

   public World getWorldObj() {
      return this.worldObj;
   }

   @Override
   protected void writeEntityToNBT(NBTTagCompound tagCompound) {
      Block block = this.fallTile != null ? this.fallTile.getBlock() : Blocks.air;
      ResourceLocation resourceLocation = Block.blockRegistry.getNameForObject(block);
      tagCompound.setString("Block", resourceLocation == null ? "" : resourceLocation.toString());
      tagCompound.setByte("Data", (byte)block.getMetaFromState(this.fallTile));
      tagCompound.setByte("Time", (byte)this.fallTime);
      tagCompound.setBoolean("DropItem", this.shouldDropItem);
      tagCompound.setBoolean("HurtEntities", this.hurtEntities);
      tagCompound.setFloat("FallHurtAmount", this.fallHurtAmount);
      tagCompound.setInteger("FallHurtMax", this.fallHurtMax);
      if (this.tileEntityData != null) {
         tagCompound.setTag("TileEntityData", this.tileEntityData);
      }
   }

   @Override
   protected void entityInit() {
   }

   public void update() {
      this.hurtEntities = true;
   }

   @Override
   public boolean canBeCollidedWith() {
      return !this.isDead;
   }
}
