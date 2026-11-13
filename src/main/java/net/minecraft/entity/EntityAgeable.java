package net.minecraft.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public abstract class EntityAgeable extends EntityCreature {
   protected int forcedAgeTimer;
   protected int growingAge;
   private float ageWidth = -1.0F;
   private float ageHeight;
   protected int forcedAge;

   @Override
   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.worldObj.isRemote) {
         if (this.forcedAgeTimer > 0) {
            if (this.forcedAgeTimer % 4 == 0) {
               this.worldObj
                  .spawnParticle(
                     EnumParticleTypes.VILLAGER_HAPPY,
                     this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
                     this.posY + 0.5 + this.rand.nextFloat() * this.height,
                     this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
                     0.0,
                     0.0,
                     0.0
                  );
            }

            this.forcedAgeTimer--;
         }

         this.setScaleForAge(this.isChild());
      } else {
         int growingAge2 = this.getGrowingAge();
         if (growingAge2 < 0) {
            this.setGrowingAge(++growingAge2);
            if (growingAge2 == 0) {
               this.onGrowingAdult();
            }
         } else if (growingAge2 > 0) {
            this.setGrowingAge(--growingAge2);
         }
      }
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      this.setGrowingAge(tagCompund.getInteger("Age"));
      this.forcedAge = tagCompund.getInteger("ForcedAge");
   }

   @Override
   public boolean interact(EntityPlayer player) {
      ItemStack itemStack = player.inventory.getCurrentItem();
      if (itemStack != null && itemStack.getItem() == Items.spawn_egg) {
         if (!this.worldObj.isRemote) {
            Class<? extends Entity> oclass = EntityList.getClassFromID(itemStack.getMetadata());
            if (oclass != null && this.getClass() == oclass) {
               EntityAgeable entityAgeable = this.createChild(this);
               if (entityAgeable != null) {
                  entityAgeable.setGrowingAge(-24000);
                  entityAgeable.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
                  this.worldObj.spawnEntityInWorld(entityAgeable);
                  if (itemStack.hasDisplayName()) {
                     entityAgeable.setCustomNameTag(itemStack.getDisplayName());
                  }

                  if (!player.capabilities.isCreativeMode) {
                     itemStack.stackSize--;
                     if (itemStack.stackSize <= 0) {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
                     }
                  }
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setInteger("Age", this.getGrowingAge());
      tagCompound.setInteger("ForcedAge", this.forcedAge);
   }

   public void addGrowth(int growth) {
      this.addGrowth(growth, false);
   }

   protected final void setScale(float scale) {
      super.setSize(this.ageWidth * scale, this.ageHeight * scale);
   }

   protected void onGrowingAdult() {
   }

   public abstract EntityAgeable createChild(EntityAgeable entityAgeable2);

   public void addGrowth(int growth, boolean force) {
      int growingAge2 = this.getGrowingAge();
      int i = growingAge2;
      growingAge2 += growth * 20;
      if (growingAge2 > 0) {
         growingAge2 = 0;
         if (i < 0) {
            this.onGrowingAdult();
         }
      }

      int i2 = growingAge2 - i;
      this.setGrowingAge(growingAge2);
      if (force) {
         this.forcedAge += i2;
         if (this.forcedAgeTimer == 0) {
            this.forcedAgeTimer = 40;
         }
      }

      if (this.getGrowingAge() == 0) {
         this.setGrowingAge(this.forcedAge);
      }
   }

   public EntityAgeable(World worldIn) {
      super(worldIn);
   }

   public int getGrowingAge() {
      return this.worldObj.isRemote ? this.dataWatcher.getWatchableObjectByte(12) : this.growingAge;
   }

   @Override
   public boolean isChild() {
      return this.getGrowingAge() < 0;
   }

   public void setGrowingAge(int age) {
      this.dataWatcher.updateObject(12, (byte)MathHelper.clamp_int(age, -1, 1));
      this.growingAge = age;
      this.setScaleForAge(this.isChild());
   }

   public void setScaleForAge(boolean flag) {
      this.setScale(flag ? 0.5F : 1.0F);
   }

   @Override
   protected void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(12, (byte)0);
   }

   @Override
   protected final void setSize(float width, float height) {
      boolean flag = this.ageWidth > 0.0F;
      this.ageWidth = width;
      this.ageHeight = height;
      if (!flag) {
         this.setScale(1.0F);
      }
   }
}
