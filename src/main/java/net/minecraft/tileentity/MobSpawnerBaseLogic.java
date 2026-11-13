package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.item.EntityMinecart.EnumMinecartType;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.StringUtils;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;

public abstract class MobSpawnerBaseLogic {
   private int spawnDelay = 20;
   private String mobID = "Pig";
   private final List<MobSpawnerBaseLogic.WeightedRandomMinecart> minecartToSpawn = Lists.newArrayList();
   private int minSpawnDelay = 200;
   private int maxSpawnDelay = 800;
   private Entity cachedEntity;
   private double prevMobRotation;
   private int spawnCount = 4;
   private int maxNearbyEntities = 6;
   private double mobRotation;
   private int activatingRangeFromPlayer = 16;
   private MobSpawnerBaseLogic.WeightedRandomMinecart randomEntity;
   private int spawnRange = 4;

   private void resetTimer() {
      if (this.maxSpawnDelay <= this.minSpawnDelay) {
         this.spawnDelay = this.minSpawnDelay;
      } else {
         int i = this.maxSpawnDelay - this.minSpawnDelay;
         this.spawnDelay = this.minSpawnDelay + this.getSpawnerWorld().rand.nextInt(i);
      }

      if (this.minecartToSpawn.size() > 0) {
         this.setRandomEntity(WeightedRandom.getRandomItem(this.getSpawnerWorld().rand, this.minecartToSpawn));
      }

      this.update();
   }

   public abstract BlockPos getSpawnerPosition();

   private MobSpawnerBaseLogic.WeightedRandomMinecart getRandomEntity() {
      return this.randomEntity;
   }

   public double getMobRotation() {
      return this.mobRotation;
   }

   private boolean isActivated() {
      BlockPos blockPos = this.getSpawnerPosition();
      return this.getSpawnerWorld()
         .isAnyPlayerWithinRangeAt(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, this.activatingRangeFromPlayer);
   }

   public void readFromNBT(NBTTagCompound nbt) {
      this.mobID = nbt.getString("EntityId");
      this.spawnDelay = nbt.getShort("Delay");
      this.minecartToSpawn.clear();
      if (nbt.hasKey("SpawnPotentials", 9)) {
         NBTTagList nBTTagList = nbt.getTagList("SpawnPotentials", 10);

         for (int index = 0; index < nBTTagList.tagCount(); index++) {
            this.minecartToSpawn.add(new MobSpawnerBaseLogic.WeightedRandomMinecart(nBTTagList.getCompoundTagAt(index)));
         }
      }

      if (nbt.hasKey("SpawnData", 10)) {
         this.setRandomEntity(new MobSpawnerBaseLogic.WeightedRandomMinecart(nbt.getCompoundTag("SpawnData"), this.mobID));
      } else {
         this.setRandomEntity((MobSpawnerBaseLogic.WeightedRandomMinecart)null);
      }

      if (nbt.hasKey("MinSpawnDelay", 99)) {
         this.minSpawnDelay = nbt.getShort("MinSpawnDelay");
         this.maxSpawnDelay = nbt.getShort("MaxSpawnDelay");
         this.spawnCount = nbt.getShort("SpawnCount");
      }

      if (nbt.hasKey("MaxNearbyEntities", 99)) {
         this.maxNearbyEntities = nbt.getShort("MaxNearbyEntities");
         this.activatingRangeFromPlayer = nbt.getShort("RequiredPlayerRange");
      }

      if (nbt.hasKey("SpawnRange", 99)) {
         this.spawnRange = nbt.getShort("SpawnRange");
      }

      if (this.getSpawnerWorld() != null) {
         this.cachedEntity = null;
      }
   }

   public boolean setDelayToMin(int delay) {
      if (delay == 1 && this.getSpawnerWorld().isRemote) {
         this.spawnDelay = this.minSpawnDelay;
         return true;
      } else {
         return false;
      }
   }

   private Entity spawnNewEntity(Entity entityIn, boolean spawn) {
      if (this.getRandomEntity() != null) {
         NBTTagCompound nbtTagCompound = new NBTTagCompound();
         entityIn.writeToNBTOptional(nbtTagCompound);

         for (String text : this.getRandomEntity().nbtData.getKeySet()) {
            NBTBase tag = this.getRandomEntity().nbtData.getTag(text);
            nbtTagCompound.setTag(text, tag.copy());
         }

         entityIn.readFromNBT(nbtTagCompound);
         if (entityIn.worldObj != null && spawn) {
            entityIn.worldObj.spawnEntityInWorld(entityIn);
         }

         Entity entityIn2 = entityIn;

         while (nbtTagCompound.hasKey("Riding", 10)) {
            NBTTagCompound compoundTag = nbtTagCompound.getCompoundTag("Riding");
            Entity entityByName = EntityList.createEntityByName(compoundTag.getString("id"), entityIn.worldObj);
            if (entityByName != null) {
               NBTTagCompound nbtTagCompound2 = new NBTTagCompound();
               entityByName.writeToNBTOptional(nbtTagCompound2);

               for (String text2 : compoundTag.getKeySet()) {
                  NBTBase tag2 = compoundTag.getTag(text2);
                  nbtTagCompound2.setTag(text2, tag2.copy());
               }

               entityByName.readFromNBT(nbtTagCompound2);
               entityByName.setLocationAndAngles(entityIn2.posX, entityIn2.posY, entityIn2.posZ, entityIn2.rotationYaw, entityIn2.rotationPitch);
               if (entityIn.worldObj != null && spawn) {
                  entityIn.worldObj.spawnEntityInWorld(entityByName);
               }

               entityIn2.mountEntity(entityByName);
            }

            entityIn2 = entityByName;
            nbtTagCompound = compoundTag;
         }
      } else if (entityIn instanceof EntityLivingBase && entityIn.worldObj != null && spawn) {
         if (entityIn instanceof EntityLiving) {
            ((EntityLiving)entityIn).onInitialSpawn(entityIn.worldObj.getDifficultyForLocation(new BlockPos(entityIn)), (IEntityLivingData)null);
         }

         entityIn.worldObj.spawnEntityInWorld(entityIn);
      }

      return entityIn;
   }

   public void setRandomEntity(MobSpawnerBaseLogic.WeightedRandomMinecart weightedRandomMinecart) {
      this.randomEntity = weightedRandomMinecart;
   }

   public void writeToNBT(NBTTagCompound nbt) {
      String entityNameToSpawn = this.getEntityNameToSpawn();
      if (!StringUtils.isNullOrEmpty(entityNameToSpawn)) {
         nbt.setString("EntityId", entityNameToSpawn);
         nbt.setShort("Delay", (short)this.spawnDelay);
         nbt.setShort("MinSpawnDelay", (short)this.minSpawnDelay);
         nbt.setShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
         nbt.setShort("SpawnCount", (short)this.spawnCount);
         nbt.setShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
         nbt.setShort("RequiredPlayerRange", (short)this.activatingRangeFromPlayer);
         nbt.setShort("SpawnRange", (short)this.spawnRange);
         if (this.getRandomEntity() != null) {
            nbt.setTag("SpawnData", this.getRandomEntity().nbtData.copy());
         }

         if (this.getRandomEntity() != null || this.minecartToSpawn.size() > 0) {
            NBTTagList nbtTagList = new NBTTagList();
            if (this.minecartToSpawn.size() > 0) {
               for (MobSpawnerBaseLogic.WeightedRandomMinecart weightedRandomMinecart : this.minecartToSpawn) {
                  nbtTagList.appendTag(weightedRandomMinecart.toNBT());
               }
            } else {
               nbtTagList.appendTag(this.getRandomEntity().toNBT());
            }

            nbt.setTag("SpawnPotentials", nbtTagList);
         }
      }
   }

   public abstract void update();

   public void updateSpawner() {
      if (this.isActivated()) {
         BlockPos spawnerPosition = this.getSpawnerPosition();
         if (this.getSpawnerWorld().isRemote) {
            double d = spawnerPosition.getX() + this.getSpawnerWorld().rand.nextFloat();
            double d2 = spawnerPosition.getY() + this.getSpawnerWorld().rand.nextFloat();
            double d3 = spawnerPosition.getZ() + this.getSpawnerWorld().rand.nextFloat();
            this.getSpawnerWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d, d2, d3, 0.0, 0.0, 0.0);
            this.getSpawnerWorld().spawnParticle(EnumParticleTypes.FLAME, d, d2, d3, 0.0, 0.0, 0.0);
            if (this.spawnDelay > 0) {
               this.spawnDelay--;
            }

            this.prevMobRotation = this.mobRotation;
            this.mobRotation = (this.mobRotation + 1000.0F / (this.spawnDelay + 200.0F)) % 360.0;
         } else {
            if (this.spawnDelay == -1) {
               this.resetTimer();
            }

            if (this.spawnDelay > 0) {
               this.spawnDelay--;
               return;
            }

            boolean flag = false;

            for (int i = 0; i < this.spawnCount; i++) {
               Entity entityByName = EntityList.createEntityByName(this.getEntityNameToSpawn(), this.getSpawnerWorld());
               if (entityByName == null) {
                  return;
               }

               if (this.getSpawnerWorld()
                     .getEntitiesWithinAABB(
                        entityByName.getClass(),
                        new AxisAlignedBB(spawnerPosition.getX(), spawnerPosition.getY(), spawnerPosition.getZ(), spawnerPosition.getX() + 1, spawnerPosition.getY() + 1, spawnerPosition.getZ() + 1)
                           .expand(this.spawnRange, this.spawnRange, this.spawnRange)
                     )
                     .size()
                  >= this.maxNearbyEntities) {
                  this.resetTimer();
                  return;
               }

               double d4 = spawnerPosition.getX() + (this.getSpawnerWorld().rand.nextDouble() - this.getSpawnerWorld().rand.nextDouble()) * this.spawnRange + 0.5;
               double d5 = spawnerPosition.getY() + this.getSpawnerWorld().rand.nextInt(3) - 1;
               double d6 = spawnerPosition.getZ() + (this.getSpawnerWorld().rand.nextDouble() - this.getSpawnerWorld().rand.nextDouble()) * this.spawnRange + 0.5;
               EntityLiving entityLiving = entityByName instanceof EntityLiving ? (EntityLiving)entityByName : null;
               entityByName.setLocationAndAngles(d4, d5, d6, this.getSpawnerWorld().rand.nextFloat() * 360.0F, 0.0F);
               if (entityLiving == null || entityLiving.getCanSpawnHere() && entityLiving.isNotColliding()) {
                  this.spawnNewEntity(entityByName, true);
                  this.getSpawnerWorld().playAuxSFX(2004, spawnerPosition, 0);
                  if (entityLiving != null) {
                     entityLiving.spawnExplosionParticle();
                  }

                  flag = true;
               }
            }

            if (flag) {
               this.resetTimer();
            }
         }
      }
   }

   public abstract World getSpawnerWorld();

   public void setEntityName(String name) {
      this.mobID = name;
   }

   public double getPrevMobRotation() {
      return this.prevMobRotation;
   }

   public Entity getCachedEntity(World worldIn) {
      if (this.cachedEntity == null) {
         Entity entityByName = EntityList.createEntityByName(this.getEntityNameToSpawn(), worldIn);
         if (entityByName != null) {
            entityByName = this.spawnNewEntity(entityByName, false);
            this.cachedEntity = entityByName;
         }
      }

      return this.cachedEntity;
   }

   private String getEntityNameToSpawn() {
      if (this.getRandomEntity() == null) {
         if (this.mobID != null && this.mobID.equals("Minecart")) {
            this.mobID = "MinecartRideable";
         }

         return this.mobID;
      } else {
         return this.getRandomEntity().entityType;
      }
   }

   public class WeightedRandomMinecart extends net.minecraft.util.WeightedRandom.Item {
      private final NBTTagCompound nbtData;
      private final String entityType;

      public WeightedRandomMinecart(NBTTagCompound tagCompound) {
         this(tagCompound.getCompoundTag("Properties"), tagCompound.getString("Type"), tagCompound.getInteger("Weight"));
      }

      public NBTTagCompound toNBT() {
         NBTTagCompound nbtTagCompound = new NBTTagCompound();
         nbtTagCompound.setTag("Properties", this.nbtData);
         nbtTagCompound.setString("Type", this.entityType);
         nbtTagCompound.setInteger("Weight", this.itemWeight);
         return nbtTagCompound;
      }

      public WeightedRandomMinecart(NBTTagCompound tagCompound, String type) {
         this(tagCompound, type, 1);
      }

      private WeightedRandomMinecart(NBTTagCompound tagCompound, String type, int weight) {
         super(weight);
         if (type.equals("Minecart")) {
            if (tagCompound != null) {
               type = EnumMinecartType.byNetworkID(tagCompound.getInteger("Type")).getName();
            } else {
               type = "MinecartRideable";
            }
         }

         this.nbtData = tagCompound;
         this.entityType = type;
      }
   }
}
