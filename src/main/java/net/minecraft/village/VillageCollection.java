package net.minecraft.village;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSavedData;

public class VillageCollection extends WorldSavedData {
   private final List<BlockPos> villagerPositionsList = Lists.newArrayList();
   private final List<VillageDoorInfo> newDoors = Lists.newArrayList();
   private final List<Village> villageList = Lists.newArrayList();
   private int tickCounter;
   private World worldObj;

   public static String fileNameForProvider(WorldProvider provider) {
      return "villages" + provider.getInternalNameSuffix();
   }

   private void addNewDoorsToVillageOrCreateVillage() {
      for (int index = 0; index < this.newDoors.size(); index++) {
         VillageDoorInfo villageDoorInfo = this.newDoors.get(index);
         Village village = this.getNearestVillage(villageDoorInfo.getDoorBlockPos(), 32);
         if (village == null) {
            village = new Village(this.worldObj);
            this.villageList.add(village);
            this.markDirty();
         }

         village.addVillageDoorInfo(villageDoorInfo);
      }

      this.newDoors.clear();
   }

   public VillageCollection(World worldIn) {
      super(fileNameForProvider(worldIn.provider));
      this.worldObj = worldIn;
      this.markDirty();
   }

   private boolean isWoodDoor(BlockPos doorPos) {
      Block block = this.worldObj.getBlockState(doorPos).getBlock();
      return block instanceof BlockDoor ? block.getMaterial() == Material.wood : false;
   }

   public void setWorldsForAll(World worldIn) {
      this.worldObj = worldIn;
      Iterator iterator2 = this.villageList.iterator();

      while (iterator2.hasNext()) {
         ((Village)iterator2.next()).setWorld(worldIn);
      }
   }

   private VillageDoorInfo checkDoorExistence(BlockPos doorBlock) {
      for (VillageDoorInfo villageDoorInfo2 : this.newDoors) {
         if (villageDoorInfo2.getDoorBlockPos().getX() == doorBlock.getX()
            && villageDoorInfo2.getDoorBlockPos().getZ() == doorBlock.getZ()
            && Math.abs(villageDoorInfo2.getDoorBlockPos().getY() - doorBlock.getY()) <= 1) {
            return villageDoorInfo2;
         }
      }

      for (Village village2 : this.villageList) {
         VillageDoorInfo existedDoor = village2.getExistedDoor(doorBlock);
         if (existedDoor != null) {
            return existedDoor;
         }
      }

      return null;
   }

   public List<Village> getVillageList() {
      return this.villageList;
   }

   public Village getNearestVillage(BlockPos doorBlock, int radius) {
      Village village2 = null;
      double d = Float.MAX_VALUE;

      for (Village village3 : this.villageList) {
         double d2 = village3.getCenter().distanceSq(doorBlock);
         if (d2 < d) {
            float f = radius + village3.getVillageRadius();
            if (d2 <= f * f) {
               village2 = village3;
               d = d2;
            }
         }
      }

      return village2;
   }

   public void tick() {
      this.tickCounter++;
      Iterator iterator2 = this.villageList.iterator();

      while (iterator2.hasNext()) {
         ((Village)iterator2.next()).tick(this.tickCounter);
      }

      this.removeAnnihilatedVillages();
      this.dropOldestVillagerPosition();
      this.addNewDoorsToVillageOrCreateVillage();
      if (this.tickCounter % 400 == 0) {
         this.markDirty();
      }
   }

   public VillageCollection(String name) {
      super(name);
   }

   public void addToVillagerPositionList(BlockPos pos) {
      if (this.villagerPositionsList.size() <= 64 && !this.positionInList(pos)) {
         this.villagerPositionsList.add(pos);
      }
   }

   private void addToNewDoorsList(BlockPos doorBlock) {
      EnumFacing facing = BlockDoor.getFacing(this.worldObj, doorBlock);
      EnumFacing facing2 = facing.getOpposite();
      int i = this.countBlocksCanSeeSky(doorBlock, facing, 5);
      int i2 = this.countBlocksCanSeeSky(doorBlock, facing2, i + 1);
      if (i != i2) {
         this.newDoors.add(new VillageDoorInfo(doorBlock, i < i2 ? facing : facing2, this.tickCounter));
      }
   }

   private void removeAnnihilatedVillages() {
      Iterator iterator2 = this.villageList.iterator();

      while (iterator2.hasNext()) {
         if (((Village)iterator2.next()).isAnnihilated()) {
            iterator2.remove();
            this.markDirty();
         }
      }
   }

   private boolean positionInList(BlockPos pos) {
      Iterator iterator2 = this.villagerPositionsList.iterator();

      while (iterator2.hasNext()) {
         if (((BlockPos)iterator2.next()).equals(pos)) {
            return true;
         }
      }

      return false;
   }

   private int countBlocksCanSeeSky(BlockPos centerPos, EnumFacing direction, int limitation) {
      int i = 0;

      for (int innerIndex = 1; innerIndex <= 5; innerIndex++) {
         if (this.worldObj.canSeeSky(centerPos.offset(direction, innerIndex))) {
            if (++i >= limitation) {
               return i;
            }
         }
      }

      return i;
   }

   private void addDoorsAround(BlockPos central) {
      for (int i = -16; i < 16; i++) {
         for (int i2 = -4; i2 < 4; i2++) {
            for (int i3 = -16; i3 < 16; i3++) {
               BlockPos blockPos = central.add(i, i2, i3);
               if (this.isWoodDoor(blockPos)) {
                  VillageDoorInfo villageDoorInfo2 = this.checkDoorExistence(blockPos);
                  if (villageDoorInfo2 == null) {
                     this.addToNewDoorsList(blockPos);
                  } else {
                     villageDoorInfo2.setLastActivityTimestamp(this.tickCounter);
                  }
               }
            }
         }
      }
   }

   private void dropOldestVillagerPosition() {
      if (!this.villagerPositionsList.isEmpty()) {
         this.addDoorsAround(this.villagerPositionsList.remove(0));
      }
   }

   @Override
   public void writeToNBT(NBTTagCompound nbt) {
      nbt.setInteger("Tick", this.tickCounter);
      NBTTagList nbtTagList = new NBTTagList();

      for (Village village2 : this.villageList) {
         NBTTagCompound nbtTagCompound = new NBTTagCompound();
         village2.writeVillageDataToNBT(nbtTagCompound);
         nbtTagList.appendTag(nbtTagCompound);
      }

      nbt.setTag("Villages", nbtTagList);
   }

   @Override
   public void readFromNBT(NBTTagCompound nbt) {
      this.tickCounter = nbt.getInteger("Tick");
      NBTTagList nBTTagList = nbt.getTagList("Villages", 10);

      for (int index = 0; index < nBTTagList.tagCount(); index++) {
         NBTTagCompound nBTTagCompound = nBTTagList.getCompoundTagAt(index);
         Village village2 = new Village();
         village2.readVillageDataFromNBT(nBTTagCompound);
         this.villageList.add(village2);
      }
   }
}
