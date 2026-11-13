package net.minecraft.village;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Village {
   private int numIronGolems;
   private final List<VillageDoorInfo> villageDoorInfoList = Lists.newArrayList();
   private BlockPos centerHelper = BlockPos.ORIGIN;
   private World worldObj;
   private int noBreedTicks;
   private BlockPos center = BlockPos.ORIGIN;
   private TreeMap<String, Integer> playerReputation = new TreeMap<>();
   private int tickCounter;
   private int lastAddDoorTimestamp;
   private int numVillagers;
   private int villageRadius;
   private List<Village.VillageAggressor> villageAgressors = Lists.newArrayList();

   public void addOrRenewAgressor(EntityLivingBase entitylivingbaseIn) {
      for (Village.VillageAggressor villageAggressor : this.villageAgressors) {
         if (villageAggressor.agressor == entitylivingbaseIn) {
            villageAggressor.agressionTime = this.tickCounter;
            return;
         }
      }

      this.villageAgressors.add(new Village.VillageAggressor(entitylivingbaseIn, this.tickCounter));
   }

   private void updateNumVillagers() {
      List<EntityVillager> list = this.worldObj
         .getEntitiesWithinAABB(
            EntityVillager.class,
            new AxisAlignedBB(
               this.center.getX() - this.villageRadius,
               this.center.getY() - 4,
               this.center.getZ() - this.villageRadius,
               this.center.getX() + this.villageRadius,
               this.center.getY() + 4,
               this.center.getZ() + this.villageRadius
            )
         );
      this.numVillagers = list.size();
      if (this.numVillagers == 0) {
         this.playerReputation.clear();
      }
   }

   private void updateVillageRadiusAndCenter() {
      int count = this.villageDoorInfoList.size();
      if (count == 0) {
         this.center = new BlockPos(0, 0, 0);
         this.villageRadius = 0;
      } else {
         this.center = new BlockPos(this.centerHelper.getX() / count, this.centerHelper.getY() / count, this.centerHelper.getZ() / count);
         int i = 0;
         Iterator iterator2 = this.villageDoorInfoList.iterator();

         while (iterator2.hasNext()) {
            i = Math.max(((VillageDoorInfo)iterator2.next()).getDistanceToDoorBlockSq(this.center), i);
         }

         this.villageRadius = Math.max(32, (int)Math.sqrt(i) + 1);
      }
   }

   public BlockPos getCenter() {
      return this.center;
   }

   public int getNumVillagers() {
      return this.numVillagers;
   }

   public void readVillageDataFromNBT(NBTTagCompound compound) {
      this.numVillagers = compound.getInteger("PopSize");
      this.villageRadius = compound.getInteger("Radius");
      this.numIronGolems = compound.getInteger("Golems");
      this.lastAddDoorTimestamp = compound.getInteger("Stable");
      this.tickCounter = compound.getInteger("Tick");
      this.noBreedTicks = compound.getInteger("MTick");
      this.center = new BlockPos(compound.getInteger("CX"), compound.getInteger("CY"), compound.getInteger("CZ"));
      this.centerHelper = new BlockPos(compound.getInteger("ACX"), compound.getInteger("ACY"), compound.getInteger("ACZ"));
      NBTTagList tagList = compound.getTagList("Doors", 10);

      for (int i = 0; i < tagList.tagCount(); i++) {
         NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
         VillageDoorInfo villageDoorInfo2 = new VillageDoorInfo(
            new BlockPos(compoundTagAt.getInteger("X"), compoundTagAt.getInteger("Y"), compoundTagAt.getInteger("Z")),
            compoundTagAt.getInteger("IDX"),
            compoundTagAt.getInteger("IDZ"),
            compoundTagAt.getInteger("TS")
         );
         this.villageDoorInfoList.add(villageDoorInfo2);
      }

      NBTTagList tagList2 = compound.getTagList("Players", 10);

      for (int i2 = 0; i2 < tagList2.tagCount(); i2++) {
         NBTTagCompound compoundTagAt2 = tagList2.getCompoundTagAt(i2);
         if (compoundTagAt2.hasKey("UUID")) {
            GameProfile profileByUUID = MinecraftServer.getServer().getPlayerProfileCache().getProfileByUUID(UUID.fromString(compoundTagAt2.getString("UUID")));
            if (profileByUUID != null) {
               this.playerReputation.put(profileByUUID.getName(), compoundTagAt2.getInteger("S"));
            }
         } else {
            this.playerReputation.put(compoundTagAt2.getString("Name"), compoundTagAt2.getInteger("S"));
         }
      }
   }

   public void addVillageDoorInfo(VillageDoorInfo doorInfo) {
      this.villageDoorInfoList.add(doorInfo);
      this.centerHelper = this.centerHelper.add(doorInfo.getDoorBlockPos());
      this.updateVillageRadiusAndCenter();
      this.lastAddDoorTimestamp = doorInfo.getInsidePosY();
   }

   public void writeVillageDataToNBT(NBTTagCompound compound) {
      compound.setInteger("PopSize", this.numVillagers);
      compound.setInteger("Radius", this.villageRadius);
      compound.setInteger("Golems", this.numIronGolems);
      compound.setInteger("Stable", this.lastAddDoorTimestamp);
      compound.setInteger("Tick", this.tickCounter);
      compound.setInteger("MTick", this.noBreedTicks);
      compound.setInteger("CX", this.center.getX());
      compound.setInteger("CY", this.center.getY());
      compound.setInteger("CZ", this.center.getZ());
      compound.setInteger("ACX", this.centerHelper.getX());
      compound.setInteger("ACY", this.centerHelper.getY());
      compound.setInteger("ACZ", this.centerHelper.getZ());
      NBTTagList nbtTagList = new NBTTagList();

      for (VillageDoorInfo villageDoorInfo2 : this.villageDoorInfoList) {
         NBTTagCompound nbtTagCompound = new NBTTagCompound();
         nbtTagCompound.setInteger("X", villageDoorInfo2.getDoorBlockPos().getX());
         nbtTagCompound.setInteger("Y", villageDoorInfo2.getDoorBlockPos().getY());
         nbtTagCompound.setInteger("Z", villageDoorInfo2.getDoorBlockPos().getZ());
         nbtTagCompound.setInteger("IDX", villageDoorInfo2.getInsideOffsetX());
         nbtTagCompound.setInteger("IDZ", villageDoorInfo2.getInsideOffsetZ());
         nbtTagCompound.setInteger("TS", villageDoorInfo2.getInsidePosY());
         nbtTagList.appendTag(nbtTagCompound);
      }

      compound.setTag("Doors", nbtTagList);
      NBTTagList nbtTagList2 = new NBTTagList();

      for (String text : this.playerReputation.keySet()) {
         NBTTagCompound nbtTagCompound2 = new NBTTagCompound();
         GameProfile gameProfileForUsername = MinecraftServer.getServer().getPlayerProfileCache().getGameProfileForUsername(text);
         if (gameProfileForUsername != null) {
            nbtTagCompound2.setString("UUID", gameProfileForUsername.getId().toString());
            nbtTagCompound2.setInteger("S", this.playerReputation.get(text));
            nbtTagList2.appendTag(nbtTagCompound2);
         }
      }

      compound.setTag("Players", nbtTagList2);
   }

   public EntityLivingBase findNearestVillageAggressor(EntityLivingBase entitylivingbaseIn) {
      double d = Double.MAX_VALUE;
      Village.VillageAggressor villageAggressor = null;

      for (int index = 0; index < this.villageAgressors.size(); index++) {
         Village.VillageAggressor village$villageaggressor1 = this.villageAgressors.get(index);
         double distanceSqToEntity = village$villageaggressor1.agressor.getDistanceSqToEntity(entitylivingbaseIn);
         if (distanceSqToEntity <= d) {
            villageAggressor = village$villageaggressor1;
            d = distanceSqToEntity;
         }
      }

      return villageAggressor != null ? villageAggressor.agressor : null;
   }

   private Vec3 getVec3(BlockPos blockPos) {
      for (int i = 0; i < 10; i++) {
         BlockPos blockPos2 = blockPos.add(this.worldObj.rand.nextInt(16) - 8, this.worldObj.rand.nextInt(6) - 3, this.worldObj.rand.nextInt(16) - 8);
         if (this.isBlockPosWithinSqVillageRadius(blockPos2) && this.isAreaClearAround(new BlockPos(2, 4, 2), blockPos2)) {
            return new Vec3(blockPos2.getX(), blockPos2.getY(), blockPos2.getZ());
         }
      }

      return null;
   }

   public int setReputationForPlayer(String playerName, int reputationDelta) {
      int i = MathHelper.clamp_int(this.getReputationForPlayer(playerName) + reputationDelta, -30, 10);
      this.playerReputation.put(playerName, i);
      return i;
   }

   public EntityPlayer getNearestTargetPlayer(EntityLivingBase villageDefender) {
      double d = Double.MAX_VALUE;
      EntityPlayer entityPlayer = null;

      for (String text : this.playerReputation.keySet()) {
         if (this.isPlayerReputationTooLow(text)) {
            EntityPlayer playerEntityByName = this.worldObj.getPlayerEntityByName(text);
            if (playerEntityByName != null) {
               double distanceSqToEntity = playerEntityByName.getDistanceSqToEntity(villageDefender);
               if (distanceSqToEntity <= d) {
                  entityPlayer = playerEntityByName;
                  d = distanceSqToEntity;
               }
            }
         }
      }

      return entityPlayer;
   }

   public boolean isBlockPosWithinSqVillageRadius(BlockPos pos) {
      return this.center.distanceSq(pos) < this.villageRadius * this.villageRadius;
   }

   private void removeDeadAndOutOfRangeDoors() {
      boolean conditionMet = false;
      boolean flag = this.worldObj.rand.nextInt(50) == 0;
      Iterator<VillageDoorInfo> iterator = this.villageDoorInfoList.iterator();

      while (iterator.hasNext()) {
         VillageDoorInfo villageDoorInfo = iterator.next();
         if (flag) {
            villageDoorInfo.resetDoorOpeningRestrictionCounter();
         }

         if (!this.isWoodDoor(villageDoorInfo.getDoorBlockPos()) || Math.abs(this.tickCounter - villageDoorInfo.getInsidePosY()) > 1200) {
            this.centerHelper = this.centerHelper.subtract(villageDoorInfo.getDoorBlockPos());
            conditionMet = true;
            villageDoorInfo.update();
            iterator.remove();
         }
      }

      if (conditionMet) {
         this.updateVillageRadiusAndCenter();
      }
   }

   public boolean isAnnihilated() {
      return this.villageDoorInfoList.isEmpty();
   }

   public Village() {
   }

   private boolean isWoodDoor(BlockPos pos) {
      Block block = this.worldObj.getBlockState(pos).getBlock();
      return block instanceof BlockDoor ? block.getMaterial() == Material.wood : false;
   }

   public Village(World worldIn) {
      this.worldObj = worldIn;
   }

   public int getReputationForPlayer(String playerName) {
      Integer integer = this.playerReputation.get(playerName);
      return integer != null ? integer : 0;
   }

   public boolean isPlayerReputationTooLow(String playerName) {
      return this.getReputationForPlayer(playerName) <= -15;
   }

   private boolean isAreaClearAround(BlockPos areaSize, BlockPos centerPos) {
      if (!World.doesBlockHaveSolidTopSurface(this.worldObj, centerPos.down())) {
         return false;
      }

      int xCoordinate = centerPos.getX() - areaSize.getX() / 2;
      int zCoordinate = centerPos.getZ() - areaSize.getZ() / 2;

      for (int nestedIndex = xCoordinate; nestedIndex < xCoordinate + areaSize.getX(); nestedIndex++) {
         for (int outerIndex = centerPos.getY(); outerIndex < centerPos.getY() + areaSize.getY(); outerIndex++) {
            for (int index = zCoordinate; index < zCoordinate + areaSize.getZ(); index++) {
               if (this.worldObj.getBlockState(new BlockPos(nestedIndex, outerIndex, index)).getBlock().isNormalCube()) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   public VillageDoorInfo getNearestDoor(BlockPos pos) {
      VillageDoorInfo villageDoorInfo2 = null;
      int i = Integer.MAX_VALUE;

      for (VillageDoorInfo villageDoorInfo3 : this.villageDoorInfoList) {
         int distanceToDoorBlockSq = villageDoorInfo3.getDistanceToDoorBlockSq(pos);
         if (distanceToDoorBlockSq < i) {
            villageDoorInfo2 = villageDoorInfo3;
            i = distanceToDoorBlockSq;
         }
      }

      return villageDoorInfo2;
   }

   private void updateNumIronGolems() {
      List<EntityIronGolem> list = this.worldObj
         .getEntitiesWithinAABB(
            EntityIronGolem.class,
            new AxisAlignedBB(
               this.center.getX() - this.villageRadius,
               this.center.getY() - 4,
               this.center.getZ() - this.villageRadius,
               this.center.getX() + this.villageRadius,
               this.center.getY() + 4,
               this.center.getZ() + this.villageRadius
            )
         );
      this.numIronGolems = list.size();
   }

   public boolean isMatingSeason() {
      return this.noBreedTicks == 0 || this.tickCounter - this.noBreedTicks >= 3600;
   }

   public int getNumVillageDoors() {
      return this.villageDoorInfoList.size();
   }

   public VillageDoorInfo getExistedDoor(BlockPos doorBlock) {
      if (this.center.distanceSq(doorBlock) > this.villageRadius * this.villageRadius) {
         return null;
      }

      for (VillageDoorInfo villageDoorInfo2 : this.villageDoorInfoList) {
         if (villageDoorInfo2.getDoorBlockPos().getX() == doorBlock.getX()
            && villageDoorInfo2.getDoorBlockPos().getZ() == doorBlock.getZ()
            && Math.abs(villageDoorInfo2.getDoorBlockPos().getY() - doorBlock.getY()) <= 1) {
            return villageDoorInfo2;
         }
      }

      return null;
   }

   public void setWorld(World worldIn) {
      this.worldObj = worldIn;
   }

   public List<VillageDoorInfo> getVillageDoorInfoList() {
      return this.villageDoorInfoList;
   }

   public void tick(int tickCount) {
      this.tickCounter = tickCount;
      this.removeDeadAndOutOfRangeDoors();
      this.removeDeadAndOldAgressors();
      if (tickCount % 20 == 0) {
         this.updateNumVillagers();
      }

      if (tickCount % 30 == 0) {
         this.updateNumIronGolems();
      }

      int i = this.numVillagers / 10;
      if (this.numIronGolems < i && this.villageDoorInfoList.size() > 20 && this.worldObj.rand.nextInt(7000) == 0) {
         Vec3 vec3 = this.getVec3(this.center);
         if (vec3 != null) {
            EntityIronGolem entityIronGolem = new EntityIronGolem(this.worldObj);
            entityIronGolem.setPosition(vec3.xCoord, vec3.yCoord, vec3.zCoord);
            this.worldObj.spawnEntityInWorld(entityIronGolem);
            this.numIronGolems++;
         }
      }
   }

   public int getTicksSinceLastDoorAdding() {
      return this.tickCounter - this.lastAddDoorTimestamp;
   }

   public int getVillageRadius() {
      return this.villageRadius;
   }

   public void endMatingSeason() {
      this.noBreedTicks = this.tickCounter;
   }

   private void removeDeadAndOldAgressors() {
      Iterator<Village.VillageAggressor> iterator = this.villageAgressors.iterator();

      while (iterator.hasNext()) {
         Village.VillageAggressor village$villageaggressor = iterator.next();
         if (!village$villageaggressor.agressor.isEntityAlive() || Math.abs(this.tickCounter - village$villageaggressor.agressionTime) > 300) {
            iterator.remove();
         }
      }
   }

   public VillageDoorInfo getDoorInfo(BlockPos pos) {
      VillageDoorInfo villageDoorInfo2 = null;
      int i = Integer.MAX_VALUE;

      for (VillageDoorInfo villageDoorInfo3 : this.villageDoorInfoList) {
         int distanceToDoorBlockSq = villageDoorInfo3.getDistanceToDoorBlockSq(pos);
         if (distanceToDoorBlockSq > 256) {
            distanceToDoorBlockSq *= 1000;
         } else {
            distanceToDoorBlockSq = villageDoorInfo3.getDoorOpeningRestrictionCounter();
         }

         if (distanceToDoorBlockSq < i) {
            villageDoorInfo2 = villageDoorInfo3;
            i = distanceToDoorBlockSq;
         }
      }

      return villageDoorInfo2;
   }

   public void update() {
      for (String text : this.playerReputation.keySet()) {
         this.setReputationForPlayer(text, 5);
      }
   }

   class VillageAggressor {
      public EntityLivingBase agressor;
      public int agressionTime;

      VillageAggressor(EntityLivingBase agressor, int agressionTime) {
         this.agressor = agressor;
         this.agressionTime = agressionTime;
      }
   }
}
