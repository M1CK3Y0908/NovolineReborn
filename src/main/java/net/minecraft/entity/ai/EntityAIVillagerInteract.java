package net.minecraft.entity.ai;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class EntityAIVillagerInteract extends EntityAIWatchClosest2 {
   private int interactionDelay;
   private EntityVillager villager;

   @Override
   public void startExecuting() {
      super.startExecuting();
      if (this.villager.canAbandonItems() && this.closestEntity instanceof EntityVillager && ((EntityVillager)this.closestEntity).needsFood()) {
         this.interactionDelay = 10;
      } else {
         this.interactionDelay = 0;
      }
   }

   public EntityAIVillagerInteract(EntityVillager villagerIn) {
      super(villagerIn, EntityVillager.class, 3.0F, 0.02F);
      this.villager = villagerIn;
   }

   @Override
   public void updateTask() {
      super.updateTask();
      if (this.interactionDelay > 0) {
         this.interactionDelay--;
         if (this.interactionDelay == 0) {
            InventoryBasic inventoryBasic = this.villager.getVillagerInventory();

            for (int index = 0; index < inventoryBasic.getSizeInventory(); index++) {
               ItemStack itemStack = inventoryBasic.getStackInSlot(index);
               ItemStack itemStack2 = null;
               if (itemStack != null) {
                  Item item = itemStack.getItem();
                  if ((item == Items.bread || item == Items.potato || item == Items.carrot) && itemStack.stackSize > 3) {
                     int i = itemStack.stackSize / 2;
                     itemStack.stackSize -= i;
                     itemStack2 = new ItemStack(item, i, itemStack.getMetadata());
                  } else if (item == Items.wheat && itemStack.stackSize > 5) {
                     int i2 = itemStack.stackSize / 2 / 3 * 3;
                     int i3 = i2 / 3;
                     itemStack.stackSize -= i2;
                     itemStack2 = new ItemStack(Items.bread, i3, 0);
                  }

                  if (itemStack.stackSize <= 0) {
                     inventoryBasic.setInventorySlotContents(index, (ItemStack)null);
                  }
               }

               if (itemStack2 != null) {
                  double yCoordinate = this.villager.posY - 0.3F + this.villager.getEyeHeight();
                  EntityItem entityItem = new EntityItem(this.villager.worldObj, this.villager.posX, yCoordinate, this.villager.posZ, itemStack2);
                  float f = 0.3F;
                  float rotationYawHead2 = this.villager.rotationYawHead;
                  float rotationPitch2 = this.villager.rotationPitch;
                  entityItem.motionX = -MathHelper.sin(rotationYawHead2 / 180.0F * (float) Math.PI) * MathHelper.cos(rotationPitch2 / 180.0F * (float) Math.PI) * f;
                  entityItem.motionZ = MathHelper.cos(rotationYawHead2 / 180.0F * (float) Math.PI) * MathHelper.cos(rotationPitch2 / 180.0F * (float) Math.PI) * f;
                  entityItem.motionY = -MathHelper.sin(rotationPitch2 / 180.0F * (float) Math.PI) * f + 0.1F;
                  entityItem.setDefaultPickupDelay();
                  this.villager.worldObj.spawnEntityInWorld(entityItem);
                  break;
               }
            }
         }
      }
   }
}
