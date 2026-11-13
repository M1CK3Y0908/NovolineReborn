package net.minecraft.entity;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S10PacketSpawnPainting;
import net.minecraft.network.play.server.S11PacketSpawnExperienceOrb;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S49PacketUpdateEntityNBT;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.storage.MapData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTrackerEntry {
   public Set<EntityPlayerMP> trackingPlayers = Sets.newHashSet();
   public Entity trackedEntity;
   public boolean playerEntitiesUpdated;
   public int updateCounter;
   private int ticksSinceLastForcedTeleport;
   private double lastTrackedEntityPosZ;
   public double motionZ;
   private static final Logger logger = LogManager.getLogger();
   private double lastTrackedEntityPosY;
   private double lastTrackedEntityPosX;
   public int trackingDistanceThreshold;
   public int updateFrequency;
   private boolean sendVelocityUpdates;
   public int encodedPosX;
   public int encodedPosY;
   public int encodedPosZ;
   private boolean firstUpdateDone;
   private boolean ridingEntity;
   public double lastTrackedEntityMotionY;
   public int encodedRotationYaw;
   public int encodedRotationPitch;
   public int lastHeadMotion;
   private Entity lastRidingEntity;
   private boolean onGround;
   public double lastTrackedEntityMotionX;

   public void updatePlayerList(List<EntityPlayer> players) {
      this.playerEntitiesUpdated = false;
      if (!this.firstUpdateDone || this.trackedEntity.getDistanceSq(this.lastTrackedEntityPosX, this.lastTrackedEntityPosY, this.lastTrackedEntityPosZ) > 16.0) {
         this.lastTrackedEntityPosX = this.trackedEntity.posX;
         this.lastTrackedEntityPosY = this.trackedEntity.posY;
         this.lastTrackedEntityPosZ = this.trackedEntity.posZ;
         this.firstUpdateDone = true;
         this.playerEntitiesUpdated = true;
         this.updatePlayerEntities(players);
      }

      if (this.lastRidingEntity != this.trackedEntity.ridingEntity || this.trackedEntity.ridingEntity != null && this.updateCounter % 60 == 0) {
         this.lastRidingEntity = this.trackedEntity.ridingEntity;
         this.sendPacketToTrackedPlayers(new S1BPacketEntityAttach(0, this.trackedEntity, this.trackedEntity.ridingEntity));
      }

      if (this.trackedEntity instanceof EntityItemFrame && this.updateCounter % 10 == 0) {
         EntityItemFrame entityItemFrame = (EntityItemFrame)this.trackedEntity;
         ItemStack displayedItem = entityItemFrame.getDisplayedItem();
         if (displayedItem != null && displayedItem.getItem() instanceof ItemMap) {
            MapData mapData = Items.filled_map.getMapData(displayedItem, this.trackedEntity.worldObj);

            for (EntityPlayer entityPlayer : players) {
               EntityPlayerMP entityPlayerMP = (EntityPlayerMP)entityPlayer;
               mapData.updateVisiblePlayers(entityPlayerMP, displayedItem);
               Packet mapDataPacket = Items.filled_map.createMapDataPacket(displayedItem, this.trackedEntity.worldObj, entityPlayerMP);
               if (mapDataPacket != null) {
                  entityPlayerMP.playerNetServerHandler.sendPacket(mapDataPacket);
               }
            }
         }

         this.sendMetadataToAllAssociatedPlayers();
      }

      if (this.updateCounter % this.updateFrequency == 0 || this.trackedEntity.isAirBorne || this.trackedEntity.getDataWatcher().hasObjectChanged()) {
         if (this.trackedEntity.ridingEntity == null) {
            this.ticksSinceLastForcedTeleport++;
            int i = MathHelper.floor_double(this.trackedEntity.posX * 32.0);
            int i2 = MathHelper.floor_double(this.trackedEntity.posY * 32.0);
            int i3 = MathHelper.floor_double(this.trackedEntity.posZ * 32.0);
            int i4 = MathHelper.floor_float(this.trackedEntity.rotationYaw * 256.0F / 360.0F);
            int i5 = MathHelper.floor_float(this.trackedEntity.rotationPitch * 256.0F / 360.0F);
            int i6 = i - this.encodedPosX;
            int i7 = i2 - this.encodedPosY;
            int i8 = i3 - this.encodedPosZ;
            Packet packet = null;
            boolean flag = Math.abs(i6) >= 4 || Math.abs(i7) >= 4 || Math.abs(i8) >= 4 || this.updateCounter % 60 == 0;
            boolean flag2 = Math.abs(i4 - this.encodedRotationYaw) >= 4 || Math.abs(i5 - this.encodedRotationPitch) >= 4;
            if (this.updateCounter > 0 || this.trackedEntity instanceof EntityArrow) {
               if (i6 >= -128
                  && i6 < 128
                  && i7 >= -128
                  && i7 < 128
                  && i8 >= -128
                  && i8 < 128
                  && this.ticksSinceLastForcedTeleport <= 400
                  && !this.ridingEntity
                  && this.onGround == this.trackedEntity.onGround) {
                  if ((!flag || !flag2) && !(this.trackedEntity instanceof EntityArrow)) {
                     if (flag) {
                        packet = new net.minecraft.network.play.server.S14PacketEntity.S15PacketEntityRelMove(
                           this.trackedEntity.getEntityId(), (byte)i6, (byte)i7, (byte)i8, this.trackedEntity.onGround
                        );
                     } else if (flag2) {
                        packet = new net.minecraft.network.play.server.S14PacketEntity.S16PacketEntityLook(this.trackedEntity.getEntityId(), (byte)i4, (byte)i5, this.trackedEntity.onGround);
                     }
                  } else {
                     packet = new net.minecraft.network.play.server.S14PacketEntity.S17PacketEntityLookMove(
                        this.trackedEntity.getEntityId(), (byte)i6, (byte)i7, (byte)i8, (byte)i4, (byte)i5, this.trackedEntity.onGround
                     );
                  }
               } else {
                  this.onGround = this.trackedEntity.onGround;
                  this.ticksSinceLastForcedTeleport = 0;
                  packet = new S18PacketEntityTeleport(
                     this.trackedEntity.getEntityId(), i, i2, i3, (byte)i4, (byte)i5, this.trackedEntity.onGround
                  );
               }
            }

            if (this.sendVelocityUpdates) {
               double d = this.trackedEntity.motionX - this.lastTrackedEntityMotionX;
               double d2 = this.trackedEntity.motionY - this.lastTrackedEntityMotionY;
               double d3 = this.trackedEntity.motionZ - this.motionZ;
               double d4 = 0.02;
               double d5 = d * d + d2 * d2 + d3 * d3;
               if (d5 > d4 * d4
                  || d5 > 0.0 && this.trackedEntity.motionX == 0.0 && this.trackedEntity.motionY == 0.0 && this.trackedEntity.motionZ == 0.0) {
                  this.lastTrackedEntityMotionX = this.trackedEntity.motionX;
                  this.lastTrackedEntityMotionY = this.trackedEntity.motionY;
                  this.motionZ = this.trackedEntity.motionZ;
                  this.sendPacketToTrackedPlayers(
                     new S12PacketEntityVelocity(this.trackedEntity.getEntityId(), this.lastTrackedEntityMotionX, this.lastTrackedEntityMotionY, this.motionZ)
                  );
               }
            }

            if (packet != null) {
               this.sendPacketToTrackedPlayers(packet);
            }

            this.sendMetadataToAllAssociatedPlayers();
            if (flag) {
               this.encodedPosX = i;
               this.encodedPosY = i2;
               this.encodedPosZ = i3;
            }

            if (flag2) {
               this.encodedRotationYaw = i4;
               this.encodedRotationPitch = i5;
            }

            this.ridingEntity = false;
         } else {
            int i9 = MathHelper.floor_float(this.trackedEntity.rotationYaw * 256.0F / 360.0F);
            int i10 = MathHelper.floor_float(this.trackedEntity.rotationPitch * 256.0F / 360.0F);
            boolean flag3 = Math.abs(i9 - this.encodedRotationYaw) >= 4 || Math.abs(i10 - this.encodedRotationPitch) >= 4;
            if (flag3) {
               this.sendPacketToTrackedPlayers(
                  new net.minecraft.network.play.server.S14PacketEntity.S16PacketEntityLook(this.trackedEntity.getEntityId(), (byte)i9, (byte)i10, this.trackedEntity.onGround)
               );
               this.encodedRotationYaw = i9;
               this.encodedRotationPitch = i10;
            }

            this.encodedPosX = MathHelper.floor_double(this.trackedEntity.posX * 32.0);
            this.encodedPosY = MathHelper.floor_double(this.trackedEntity.posY * 32.0);
            this.encodedPosZ = MathHelper.floor_double(this.trackedEntity.posZ * 32.0);
            this.sendMetadataToAllAssociatedPlayers();
            this.ridingEntity = true;
         }

         int i11 = MathHelper.floor_float(this.trackedEntity.getRotationYawHead() * 256.0F / 360.0F);
         if (Math.abs(i11 - this.lastHeadMotion) >= 4) {
            this.sendPacketToTrackedPlayers(new S19PacketEntityHeadLook(this.trackedEntity, (byte)i11));
            this.lastHeadMotion = i11;
         }

         this.trackedEntity.isAirBorne = false;
      }

      this.updateCounter++;
      if (this.trackedEntity.velocityChanged) {
         this.sendPacketToTrackedPlayersAndTrackedEntity(new S12PacketEntityVelocity(this.trackedEntity));
         this.trackedEntity.velocityChanged = false;
      }
   }

   public void sendPacketToTrackedPlayersAndTrackedEntity(Packet packetIn) {
      this.sendPacketToTrackedPlayers(packetIn);
      if (this.trackedEntity instanceof EntityPlayerMP) {
         ((EntityPlayerMP)this.trackedEntity).playerNetServerHandler.sendPacket(packetIn);
      }
   }

   @Override
   public boolean equals(Object arg1) {
      return arg1 instanceof EntityTrackerEntry ? ((EntityTrackerEntry)arg1).trackedEntity.getEntityId() == this.trackedEntity.getEntityId() : false;
   }

   @Override
   public int hashCode() {
      return this.trackedEntity.getEntityId();
   }

   public boolean isPlayerWithinTrackingDistance(EntityPlayerMP playerMP) {
      double xCoordinate = playerMP.posX - this.encodedPosX / 32;
      double zCoordinate = playerMP.posZ - this.encodedPosZ / 32;
      return xCoordinate >= -this.trackingDistanceThreshold
         && xCoordinate <= this.trackingDistanceThreshold
         && zCoordinate >= -this.trackingDistanceThreshold
         && zCoordinate <= this.trackingDistanceThreshold
         && this.trackedEntity.isSpectatedByPlayer(playerMP);
   }

   private void sendMetadataToAllAssociatedPlayers() {
      DataWatcher dataWatcher = this.trackedEntity.getDataWatcher();
      if (dataWatcher.hasObjectChanged()) {
         this.sendPacketToTrackedPlayersAndTrackedEntity(new S1CPacketEntityMetadata(this.trackedEntity.getEntityId(), dataWatcher, false));
      }

      if (this.trackedEntity instanceof EntityLivingBase) {
         Set attributeInstanceSet = ((ServersideAttributeMap)((EntityLivingBase)this.trackedEntity).getAttributeMap()).getAttributeInstanceSet();
         if (!attributeInstanceSet.isEmpty()) {
            this.sendPacketToTrackedPlayersAndTrackedEntity(new S20PacketEntityProperties(this.trackedEntity.getEntityId(), attributeInstanceSet));
         }

         attributeInstanceSet.clear();
      }
   }

   public void sendPacketToTrackedPlayers(Packet packetIn) {
      Iterator iterator2 = this.trackingPlayers.iterator();

      while (iterator2.hasNext()) {
         ((EntityPlayerMP)iterator2.next()).playerNetServerHandler.sendPacket(packetIn);
      }
   }

   private boolean isPlayerWatchingThisChunk(EntityPlayerMP playerMP) {
      return playerMP.getServerForPlayer().getPlayerManager().isPlayerWatchingChunk(playerMP, this.trackedEntity.chunkCoordX, this.trackedEntity.chunkCoordZ);
   }

   public void removeTrackedPlayerSymmetric(EntityPlayerMP playerMP) {
      if (this.trackingPlayers.contains(playerMP)) {
         this.trackingPlayers.remove(playerMP);
         playerMP.removeEntity(this.trackedEntity);
      }
   }

   public void updatePlayerEntity(EntityPlayerMP playerMP) {
      if (playerMP != this.trackedEntity) {
         if (this.isPlayerWithinTrackingDistance(playerMP)) {
            if (!this.trackingPlayers.contains(playerMP) && (this.isPlayerWatchingThisChunk(playerMP) || this.trackedEntity.forceSpawn)) {
               this.trackingPlayers.add(playerMP);
               Packet spawnPacket = this.createSpawnPacket();
               playerMP.playerNetServerHandler.sendPacket(spawnPacket);
               if (!this.trackedEntity.getDataWatcher().getIsBlank()) {
                  playerMP.playerNetServerHandler
                     .sendPacket(new S1CPacketEntityMetadata(this.trackedEntity.getEntityId(), this.trackedEntity.getDataWatcher(), true));
               }

               NBTTagCompound nbtTagCompound = this.trackedEntity.getNBTTagCompound();
               if (nbtTagCompound != null) {
                  playerMP.playerNetServerHandler.sendPacket(new S49PacketUpdateEntityNBT(this.trackedEntity.getEntityId(), nbtTagCompound));
               }

               if (this.trackedEntity instanceof EntityLivingBase) {
                  ServersideAttributeMap serversideAttributeMap = (ServersideAttributeMap)((EntityLivingBase)this.trackedEntity).getAttributeMap();
                  Collection watchedAttributes = serversideAttributeMap.getWatchedAttributes();
                  if (!watchedAttributes.isEmpty()) {
                     playerMP.playerNetServerHandler.sendPacket(new S20PacketEntityProperties(this.trackedEntity.getEntityId(), watchedAttributes));
                  }
               }

               this.lastTrackedEntityMotionX = this.trackedEntity.motionX;
               this.lastTrackedEntityMotionY = this.trackedEntity.motionY;
               this.motionZ = this.trackedEntity.motionZ;
               if (this.sendVelocityUpdates && !(spawnPacket instanceof S0FPacketSpawnMob)) {
                  playerMP.playerNetServerHandler
                     .sendPacket(
                        new S12PacketEntityVelocity(
                           this.trackedEntity.getEntityId(), this.trackedEntity.motionX, this.trackedEntity.motionY, this.trackedEntity.motionZ
                        )
                     );
               }

               if (this.trackedEntity.ridingEntity != null) {
                  playerMP.playerNetServerHandler.sendPacket(new S1BPacketEntityAttach(0, this.trackedEntity, this.trackedEntity.ridingEntity));
               }

               if (this.trackedEntity instanceof EntityLiving && ((EntityLiving)this.trackedEntity).getLeashedToEntity() != null) {
                  playerMP.playerNetServerHandler
                     .sendPacket(new S1BPacketEntityAttach(1, this.trackedEntity, ((EntityLiving)this.trackedEntity).getLeashedToEntity()));
               }

               if (this.trackedEntity instanceof EntityLivingBase) {
                  for (int i = 0; i < 5; i++) {
                     ItemStack equipmentInSlot = ((EntityLivingBase)this.trackedEntity).getEquipmentInSlot(i);
                     if (equipmentInSlot != null) {
                        playerMP.playerNetServerHandler.sendPacket(new S04PacketEntityEquipment(this.trackedEntity.getEntityId(), i, equipmentInSlot));
                     }
                  }
               }

               if (this.trackedEntity instanceof EntityPlayer entityPlayer && entityPlayer.isPlayerSleeping()) {
                  playerMP.playerNetServerHandler.sendPacket(new S0APacketUseBed(entityPlayer, new BlockPos(this.trackedEntity)));
               }

               if (this.trackedEntity instanceof EntityLivingBase entityLivingBase) {
                  for (PotionEffect potionEffect : entityLivingBase.getActivePotionEffects()) {
                     playerMP.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(this.trackedEntity.getEntityId(), potionEffect));
                  }
               }
            }
         } else if (this.trackingPlayers.contains(playerMP)) {
            this.trackingPlayers.remove(playerMP);
            playerMP.removeEntity(this.trackedEntity);
         }
      }
   }

   public EntityTrackerEntry(Entity trackedEntityIn, int trackingDistanceThresholdIn, int updateFrequencyIn, boolean sendVelocityUpdatesIn) {
      this.trackedEntity = trackedEntityIn;
      this.trackingDistanceThreshold = trackingDistanceThresholdIn;
      this.updateFrequency = updateFrequencyIn;
      this.sendVelocityUpdates = sendVelocityUpdatesIn;
      this.encodedPosX = MathHelper.floor_double(trackedEntityIn.posX * 32.0);
      this.encodedPosY = MathHelper.floor_double(trackedEntityIn.posY * 32.0);
      this.encodedPosZ = MathHelper.floor_double(trackedEntityIn.posZ * 32.0);
      this.encodedRotationYaw = MathHelper.floor_float(trackedEntityIn.rotationYaw * 256.0F / 360.0F);
      this.encodedRotationPitch = MathHelper.floor_float(trackedEntityIn.rotationPitch * 256.0F / 360.0F);
      this.lastHeadMotion = MathHelper.floor_float(trackedEntityIn.getRotationYawHead() * 256.0F / 360.0F);
      this.onGround = trackedEntityIn.onGround;
   }

   public void removeFromTrackedPlayers(EntityPlayerMP playerMP) {
      if (this.trackingPlayers.contains(playerMP)) {
         playerMP.removeEntity(this.trackedEntity);
         this.trackingPlayers.remove(playerMP);
      }
   }

   public void sendDestroyEntityPacketToTrackedPlayers() {
      Iterator iterator2 = this.trackingPlayers.iterator();

      while (iterator2.hasNext()) {
         ((EntityPlayerMP)iterator2.next()).removeEntity(this.trackedEntity);
      }
   }

   public void updatePlayerEntities(List<EntityPlayer> players) {
      for (int index = 0; index < players.size(); index++) {
         this.updatePlayerEntity((EntityPlayerMP)players.get(index));
      }
   }

   private Packet createSpawnPacket() {
      if (this.trackedEntity.isDead) {
         logger.warn("Fetching addPacket for removed entity");
      }

      if (this.trackedEntity instanceof EntityItem) {
         return new S0EPacketSpawnObject(this.trackedEntity, 2, 1);
      } else if (this.trackedEntity instanceof EntityPlayerMP) {
         return new S0CPacketSpawnPlayer((EntityPlayer)this.trackedEntity);
      } else if (this.trackedEntity instanceof EntityMinecart entityMinecart) {
         return new S0EPacketSpawnObject(this.trackedEntity, 10, entityMinecart.getMinecartType().getNetworkID());
      } else {
         if (this.trackedEntity instanceof EntityBoat) {
            return new S0EPacketSpawnObject(this.trackedEntity, 1);
         }

         if (this.trackedEntity instanceof IAnimals) {
            this.lastHeadMotion = MathHelper.floor_float(this.trackedEntity.getRotationYawHead() * 256.0F / 360.0F);
            return new S0FPacketSpawnMob((EntityLivingBase)this.trackedEntity);
         }

         if (this.trackedEntity instanceof EntityFishHook) {
            EntityPlayer angler2 = ((EntityFishHook)this.trackedEntity).angler;
            return new S0EPacketSpawnObject(this.trackedEntity, 90, angler2 != null ? angler2.getEntityId() : this.trackedEntity.getEntityId());
         }

         if (this.trackedEntity instanceof EntityArrow) {
            Entity shootingEntity2 = ((EntityArrow)this.trackedEntity).shootingEntity;
            return new S0EPacketSpawnObject(this.trackedEntity, 60, shootingEntity2 != null ? shootingEntity2.getEntityId() : this.trackedEntity.getEntityId());
         }

         if (this.trackedEntity instanceof EntitySnowball) {
            return new S0EPacketSpawnObject(this.trackedEntity, 61);
         }

         if (this.trackedEntity instanceof EntityPotion) {
            return new S0EPacketSpawnObject(this.trackedEntity, 73, ((EntityPotion)this.trackedEntity).getPotionDamage());
         }

         if (this.trackedEntity instanceof EntityExpBottle) {
            return new S0EPacketSpawnObject(this.trackedEntity, 75);
         }

         if (this.trackedEntity instanceof EntityEnderPearl) {
            return new S0EPacketSpawnObject(this.trackedEntity, 65);
         }

         if (this.trackedEntity instanceof EntityEnderEye) {
            return new S0EPacketSpawnObject(this.trackedEntity, 72);
         }

         if (this.trackedEntity instanceof EntityFireworkRocket) {
            return new S0EPacketSpawnObject(this.trackedEntity, 76);
         }

         if (this.trackedEntity instanceof EntityFireball entityFireball) {
            byte b = 63;
            if (this.trackedEntity instanceof EntitySmallFireball) {
               b = 64;
            } else if (this.trackedEntity instanceof EntityWitherSkull) {
               b = 66;
            }

            S0EPacketSpawnObject s0EPacketSpawnObject;
            if (entityFireball.shootingEntity != null) {
               s0EPacketSpawnObject = new S0EPacketSpawnObject(this.trackedEntity, b, ((EntityFireball)this.trackedEntity).shootingEntity.getEntityId());
            } else {
               s0EPacketSpawnObject = new S0EPacketSpawnObject(this.trackedEntity, b, 0);
            }

            s0EPacketSpawnObject.setSpeedX((int)(entityFireball.accelerationX * 8000.0));
            s0EPacketSpawnObject.setSpeedY((int)(entityFireball.accelerationY * 8000.0));
            s0EPacketSpawnObject.setSpeedZ((int)(entityFireball.accelerationZ * 8000.0));
            return s0EPacketSpawnObject;
         } else if (this.trackedEntity instanceof EntityEgg) {
            return new S0EPacketSpawnObject(this.trackedEntity, 62);
         } else if (this.trackedEntity instanceof EntityTNTPrimed) {
            return new S0EPacketSpawnObject(this.trackedEntity, 50);
         } else if (this.trackedEntity instanceof EntityEnderCrystal) {
            return new S0EPacketSpawnObject(this.trackedEntity, 51);
         } else if (this.trackedEntity instanceof EntityFallingBlock entityFallingBlock) {
            return new S0EPacketSpawnObject(this.trackedEntity, 70, Block.getStateId(entityFallingBlock.getBlock()));
         } else if (this.trackedEntity instanceof EntityArmorStand) {
            return new S0EPacketSpawnObject(this.trackedEntity, 78);
         } else if (this.trackedEntity instanceof EntityPainting) {
            return new S10PacketSpawnPainting((EntityPainting)this.trackedEntity);
         } else if (this.trackedEntity instanceof EntityItemFrame entityItemFrame) {
            S0EPacketSpawnObject s0EPacketSpawnObject2 = new S0EPacketSpawnObject(this.trackedEntity, 71, entityItemFrame.facingDirection.getHorizontalIndex());
            BlockPos hangingPosition = entityItemFrame.getHangingPosition();
            s0EPacketSpawnObject2.setX(MathHelper.floor_float(hangingPosition.getX() * 32));
            s0EPacketSpawnObject2.setY(MathHelper.floor_float(hangingPosition.getY() * 32));
            s0EPacketSpawnObject2.setZ(MathHelper.floor_float(hangingPosition.getZ() * 32));
            return s0EPacketSpawnObject2;
         } else if (this.trackedEntity instanceof EntityLeashKnot entityLeashKnot) {
            S0EPacketSpawnObject s0EPacketSpawnObject3 = new S0EPacketSpawnObject(this.trackedEntity, 77);
            BlockPos hangingPosition2 = entityLeashKnot.getHangingPosition();
            s0EPacketSpawnObject3.setX(MathHelper.floor_float(hangingPosition2.getX() * 32));
            s0EPacketSpawnObject3.setY(MathHelper.floor_float(hangingPosition2.getY() * 32));
            s0EPacketSpawnObject3.setZ(MathHelper.floor_float(hangingPosition2.getZ() * 32));
            return s0EPacketSpawnObject3;
         } else if (this.trackedEntity instanceof EntityXPOrb) {
            return new S11PacketSpawnExperienceOrb((EntityXPOrb)this.trackedEntity);
         } else {
            throw new IllegalArgumentException("Don't know how to add " + this.trackedEntity.getClass() + "!");
         }
      }
   }
}
