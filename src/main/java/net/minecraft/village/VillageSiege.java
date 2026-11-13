package net.minecraft.village;

import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;

public class VillageSiege {
   private int siegeState = -1;
   private World worldObj;
   private int zombiesToSpawn;
   private Village theVillage;
   private int spawnY;
   private int spawnZ;
   private int nextSpawnDelay;
   private int spawnX;
   private boolean siegeSetupComplete;

   private boolean trySetupSiege() {
      for (EntityPlayer entityPlayer : this.worldObj.playerEntities) {
         if (!entityPlayer.isSpectator()) {
            this.theVillage = this.worldObj.getVillageCollection().getNearestVillage(new BlockPos(entityPlayer), 1);
            if (this.theVillage != null
               && this.theVillage.getNumVillageDoors() >= 10
               && this.theVillage.getTicksSinceLastDoorAdding() >= 20
               && this.theVillage.getNumVillagers() >= 20) {
               BlockPos center = this.theVillage.getCenter();
               float villageRadius = this.theVillage.getVillageRadius();
               boolean flag = false;

               for (int i = 0; i < 10; i++) {
                  float f = this.worldObj.rand.nextFloat() * (float) Math.PI * 2.0F;
                  this.spawnX = center.getX() + (int)(MathHelper.cos(f) * villageRadius * 0.9);
                  this.spawnY = center.getY();
                  this.spawnZ = center.getZ() + (int)(MathHelper.sin(f) * villageRadius * 0.9);
                  flag = false;

                  for (Village village2 : this.worldObj.getVillageCollection().getVillageList()) {
                     if (village2 != this.theVillage && village2.isBlockPosWithinSqVillageRadius(new BlockPos(this.spawnX, this.spawnY, this.spawnZ))) {
                        flag = true;
                        break;
                     }
                  }

                  if (!flag) {
                     break;
                  }
               }

               if (flag) {
                  return false;
               }

               Vec3 randomSpawnPos = this.findRandomSpawnPos(new BlockPos(this.spawnX, this.spawnY, this.spawnZ));
               if (randomSpawnPos != null) {
                  this.nextSpawnDelay = 0;
                  this.zombiesToSpawn = 20;
                  return true;
               }
            }
         }
      }

      return false;
   }

   public VillageSiege(World worldIn) {
      this.worldObj = worldIn;
   }

   public void tick() {
      if (this.worldObj.isDaytime()) {
         this.siegeState = 0;
      } else if (this.siegeState != 2) {
         if (this.siegeState == 0) {
            float celestialAngle = this.worldObj.getCelestialAngle(0.0F);
            if (celestialAngle < 0.5 || celestialAngle > 0.501) {
               return;
            }

            this.siegeState = this.worldObj.rand.nextInt(10) == 0 ? 1 : 2;
            this.siegeSetupComplete = false;
            if (this.siegeState == 2) {
               return;
            }
         }

         if (this.siegeState != -1) {
            if (!this.siegeSetupComplete) {
               if (!this.trySetupSiege()) {
                  return;
               }

               this.siegeSetupComplete = true;
            }

            if (this.nextSpawnDelay > 0) {
               this.nextSpawnDelay--;
            } else {
               this.nextSpawnDelay = 2;
               if (this.zombiesToSpawn > 0) {
                  this.spawnZombie();
                  this.zombiesToSpawn--;
               } else {
                  this.siegeState = 2;
               }
            }
         }
      }
   }

   private Vec3 findRandomSpawnPos(BlockPos centerPos) {
      for (int index = 0; index < 10; index++) {
         BlockPos blockPos = centerPos.add(this.worldObj.rand.nextInt(16) - 8, this.worldObj.rand.nextInt(6) - 3, this.worldObj.rand.nextInt(16) - 8);
         if (this.theVillage.isBlockPosWithinSqVillageRadius(blockPos)
            && SpawnerAnimals.canCreatureTypeSpawnAtLocation(net.minecraft.entity.EntityLiving.SpawnPlacementType.ON_GROUND, this.worldObj, blockPos)) {
            return new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ());
         }
      }

      return null;
   }

   private boolean spawnZombie() {
      Vec3 randomSpawnPos = this.findRandomSpawnPos(new BlockPos(this.spawnX, this.spawnY, this.spawnZ));
      if (randomSpawnPos == null) {
         return false;
      }

      EntityZombie entityZombie;
      try {
         entityZombie = new EntityZombie(this.worldObj);
         entityZombie.onInitialSpawn(this.worldObj.getDifficultyForLocation(new BlockPos(entityZombie)), (IEntityLivingData)null);
         entityZombie.setVillager(false);
      } catch (Exception exception) {
         exception.printStackTrace();
         return false;
      }

      entityZombie.setLocationAndAngles(randomSpawnPos.xCoord, randomSpawnPos.yCoord, randomSpawnPos.zCoord, this.worldObj.rand.nextFloat() * 360.0F, 0.0F);
      this.worldObj.spawnEntityInWorld(entityZombie);
      BlockPos center = this.theVillage.getCenter();
      entityZombie.setHomePosAndDistance(center, this.theVillage.getVillageRadius());
      return true;
   }
}
