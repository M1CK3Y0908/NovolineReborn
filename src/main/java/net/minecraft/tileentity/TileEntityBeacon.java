package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ITickable;

public class TileEntityBeacon extends TileEntityLockable implements ITickable, IInventory {
   private String customName;
   private long beamRenderCounter;
   private final List<TileEntityBeacon.BeamSegment> beamSegments = Lists.newArrayList();
   private int secondaryEffect;
   private int primaryEffect;
   private float beamRenderScale;
   private ItemStack payment;
   private int levels = -1;
   public static final Potion[][] effectsList = new Potion[][]{
      {Potion.moveSpeed, Potion.digSpeed}, {Potion.resistance, Potion.jump}, {Potion.damageBoost}, {Potion.regeneration}
   };
   private boolean isComplete;

   @Override
   public void update() {
      if (this.worldObj.getTotalWorldTime() % 80L == 0L) {
         this.updateBeacon();
      }
   }

   @Override
   public void openInventory(EntityPlayer entityPlayer) {
   }

   @Override
   public int getField(int id) {
      switch (id) {
         case 0:
            return this.levels;
         case 1:
            return this.primaryEffect;
         case 2:
            return this.secondaryEffect;
         default:
            return 0;
      }
   }

   @Override
   public double getMaxRenderDistanceSquared() {
      return 65536.0;
   }

   @Override
   public String getGuiID() {
      return "minecraft:beacon";
   }

   public List<TileEntityBeacon.BeamSegment> getBeamSegments() {
      return this.beamSegments;
   }

   private int validateEffect(int effectId) {
      if (effectId >= 0 && effectId < Potion.potionTypes.length && Potion.potionTypes[effectId] != null) {
         Potion potion2 = Potion.potionTypes[effectId];
         return potion2 != Potion.moveSpeed
               && potion2 != Potion.digSpeed
               && potion2 != Potion.resistance
               && potion2 != Potion.jump
               && potion2 != Potion.damageBoost
               && potion2 != Potion.regeneration
            ? 0
            : effectId;
      } else {
         return 0;
      }
   }

   @Override
   public Packet getDescriptionPacket() {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      this.writeToNBT(nbtTagCompound);
      return new S35PacketUpdateTileEntity(this.pos, 3, nbtTagCompound);
   }

   @Override
   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      return new ContainerBeacon(playerInventory, this);
   }

   @Override
   public String getName() {
      return this.hasCustomName() ? this.customName : "container.beacon";
   }

   @Override
   public int getInventoryStackLimit() {
      return 1;
   }

   private void addEffectsToPlayers() {
      if (this.isComplete && this.levels > 0 && !this.worldObj.isRemote && this.primaryEffect > 0) {
         double d = this.levels * 10 + 10;
         int i = 0;
         if (this.levels >= 4 && this.primaryEffect == this.secondaryEffect) {
            i = 1;
         }

         int xCoordinate = this.pos.getX();
         int yCoordinate = this.pos.getY();
         int zCoordinate = this.pos.getZ();
         AxisAlignedBB axisAlignedBB = new AxisAlignedBB(xCoordinate, yCoordinate, zCoordinate, xCoordinate + 1, yCoordinate + 1, zCoordinate + 1)
            .expand(d, d, d)
            .addCoord(0.0, this.worldObj.getHeight(), 0.0);
         List<EntityPlayer> list = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, axisAlignedBB);
         Iterator iterator2 = list.iterator();

         while (iterator2.hasNext()) {
            ((EntityPlayer)iterator2.next()).addPotionEffect(new PotionEffect(this.primaryEffect, 180, i, true, true));
         }

         if (this.levels >= 4 && this.primaryEffect != this.secondaryEffect && this.secondaryEffect > 0) {
            iterator2 = list.iterator();

            while (iterator2.hasNext()) {
               ((EntityPlayer)iterator2.next()).addPotionEffect(new PotionEffect(this.secondaryEffect, 180, 0, true, true));
            }
         }
      }
   }

   @Override
   public void setInventorySlotContents(int index, ItemStack stack) {
      if (index == 0) {
         this.payment = stack;
      }
   }

   @Override
   public ItemStack removeStackFromSlot(int index) {
      if (index == 0 && this.payment != null) {
         ItemStack itemStack = this.payment;
         this.payment = null;
         return itemStack;
      } else {
         return null;
      }
   }

   @Override
   public boolean isUseableByPlayer(EntityPlayer player) {
      return this.worldObj.getTileEntity(this.pos) != this
         ? false
         : player.getDistanceSq(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64.0;
   }

   public void updateBeacon() {
      this.updateSegmentColors();
      this.addEffectsToPlayers();
   }

   @Override
   public ItemStack decrStackSize(int index, int count) {
      if (index != 0 || this.payment == null) {
         return null;
      } else if (count >= this.payment.stackSize) {
         ItemStack itemStack = this.payment;
         this.payment = null;
         return itemStack;
      } else {
         this.payment.stackSize -= count;
         return new ItemStack(this.payment.getItem(), count, this.payment.getMetadata());
      }
   }

   @Override
   public ItemStack getStackInSlot(int index) {
      return index == 0 ? this.payment : null;
   }

   @Override
   public void closeInventory(EntityPlayer entityPlayer) {
   }

   public void setName(String name) {
      this.customName = name;
   }

   @Override
   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      this.primaryEffect = this.validateEffect(compound.getInteger("Primary"));
      this.secondaryEffect = this.validateEffect(compound.getInteger("Secondary"));
      this.levels = compound.getInteger("Levels");
   }

   public float shouldBeamRender() {
      if (!this.isComplete) {
         return 0.0F;
      }

      int i = (int)(this.worldObj.getTotalWorldTime() - this.beamRenderCounter);
      this.beamRenderCounter = this.worldObj.getTotalWorldTime();
      if (i > 1) {
         this.beamRenderScale -= i / 40.0F;
         if (this.beamRenderScale < 0.0F) {
            this.beamRenderScale = 0.0F;
         }
      }

      this.beamRenderScale += 0.025F;
      if (this.beamRenderScale > 1.0F) {
         this.beamRenderScale = 1.0F;
      }

      return this.beamRenderScale;
   }

   @Override
   public int getSizeInventory() {
      return 1;
   }

   private void updateSegmentColors() {
      int levels2 = this.levels;
      int innerIndex = this.pos.getX();
      int nestedIndex = this.pos.getY();
      int z = this.pos.getZ();
      this.levels = 0;
      this.beamSegments.clear();
      this.isComplete = true;
      TileEntityBeacon.BeamSegment beamSegment = new TileEntityBeacon.BeamSegment(EntitySheep.getDyeRgb(EnumDyeColor.WHITE));
      this.beamSegments.add(beamSegment);
      boolean flag = true;
      net.minecraft.util.BlockPos.MutableBlockPos mutableBlockPos = new net.minecraft.util.BlockPos.MutableBlockPos();

      for (int i = nestedIndex + 1; i < 256; i++) {
         IBlockState blockState = this.worldObj.getBlockState(mutableBlockPos.set(innerIndex, i, z));
         float[] fArray;
         if (blockState.getBlock() == Blocks.stained_glass) {
            fArray = EntitySheep.getDyeRgb(blockState.getValue(BlockStainedGlass.COLOR));
         } else {
            if (blockState.getBlock() != Blocks.stained_glass_pane) {
               if (blockState.getBlock().getLightOpacity() >= 15 && blockState.getBlock() != Blocks.bedrock) {
                  this.isComplete = false;
                  this.beamSegments.clear();
                  break;
               }

               beamSegment.incrementHeight();
               continue;
            }

            fArray = EntitySheep.getDyeRgb(blockState.getValue(BlockStainedGlassPane.COLOR));
         }

         if (!flag) {
            fArray = new float[]{(beamSegment.getColors()[0] + fArray[0]) / 2.0F, (beamSegment.getColors()[1] + fArray[1]) / 2.0F, (beamSegment.getColors()[2] + fArray[2]) / 2.0F};
         }

         if (Arrays.equals(fArray, beamSegment.getColors())) {
            beamSegment.incrementHeight();
         } else {
            beamSegment = new TileEntityBeacon.BeamSegment(fArray);
            this.beamSegments.add(beamSegment);
         }

         flag = false;
      }

      if (this.isComplete) {
         for (int i2 = 1; i2 <= 4; this.levels = i2++) {
            int i3 = nestedIndex - i2;
            if (i3 < 0) {
               break;
            }

            boolean flag2 = true;

            for (int i4 = innerIndex - i2; i4 <= innerIndex + i2 && flag2; i4++) {
               for (int i5 = z - i2; i5 <= z + i2; i5++) {
                  Block block2 = this.worldObj.getBlockState(new BlockPos(i4, i3, i5)).getBlock();
                  if (block2 != Blocks.emerald_block && block2 != Blocks.gold_block && block2 != Blocks.diamond_block && block2 != Blocks.iron_block) {
                     flag2 = false;
                     break;
                  }
               }
            }

            if (!flag2) {
               break;
            }
         }

         if (this.levels == 0) {
            this.isComplete = false;
         }
      }

      if (!this.worldObj.isRemote && this.levels == 4 && levels2 < this.levels) {
         for (EntityPlayer entityPlayer : this.worldObj
            .getEntitiesWithinAABB(
               EntityPlayer.class, new AxisAlignedBB(innerIndex, nestedIndex, z, innerIndex, nestedIndex - 4, z).expand(10.0, 5.0, 10.0)
            )) {
            entityPlayer.triggerAchievement(AchievementList.fullBeacon);
         }
      }
   }

   @Override
   public void setField(int id, int value) {
      switch (id) {
         case 0:
            this.levels = value;
            break;
         case 1:
            this.primaryEffect = this.validateEffect(value);
            break;
         case 2:
            this.secondaryEffect = this.validateEffect(value);
      }
   }

   @Override
   public boolean isItemValidForSlot(int index, ItemStack stack) {
      return stack.getItem() == Items.emerald || stack.getItem() == Items.diamond || stack.getItem() == Items.gold_ingot || stack.getItem() == Items.iron_ingot;
   }

   @Override
   public boolean receiveClientEvent(int id, int type) {
      if (id == 1) {
         this.updateBeacon();
         return true;
      } else {
         return super.receiveClientEvent(id, type);
      }
   }

   @Override
   public int getFieldCount() {
      return 3;
   }

   @Override
   public void clear() {
      this.payment = null;
   }

   @Override
   public boolean hasCustomName() {
      return this.customName != null && this.customName.length() > 0;
   }

   @Override
   public void writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      compound.setInteger("Primary", this.primaryEffect);
      compound.setInteger("Secondary", this.secondaryEffect);
      compound.setInteger("Levels", this.levels);
   }

   public static class BeamSegment {
      private final float[] colors;
      private int height;

      public int getHeight() {
         return this.height;
      }

      public float[] getColors() {
         return this.colors;
      }

      protected void incrementHeight() {
         this.height++;
      }

      public BeamSegment(float[] fArray) {
         this.colors = fArray;
         this.height = 1;
      }
   }
}
