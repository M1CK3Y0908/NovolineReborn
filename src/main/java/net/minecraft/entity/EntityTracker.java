package net.minecraft.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.network.Packet;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTracker {
   private static final Logger logger = LogManager.getLogger();
   private Set<EntityTrackerEntry> trackedEntities = Sets.newHashSet();
   private IntHashMap<EntityTrackerEntry> trackedEntityHashTable = new IntHashMap<>();
   private final WorldServer theWorld;
   private int maxTrackingDistanceThreshold;

   public void removePlayerFromTrackers(EntityPlayerMP player) {
      Iterator iterator2 = this.trackedEntities.iterator();

      while (iterator2.hasNext()) {
         ((EntityTrackerEntry)iterator2.next()).removeTrackedPlayerSymmetric(player);
      }
   }

   public EntityTracker(WorldServer theWorldIn) {
      this.theWorld = theWorldIn;
      this.maxTrackingDistanceThreshold = theWorldIn.getMinecraftServer().getConfigurationManager().getEntityViewDistance();
   }

   public void trackEntity(Entity entityIn) {
      if (entityIn instanceof EntityPlayerMP) {
         this.handleEntity(entityIn, 2);
         EntityPlayerMP entityPlayerMP2 = (EntityPlayerMP)entityIn;

         for (EntityTrackerEntry entityTrackerEntry3 : this.trackedEntities) {
            if (entityTrackerEntry3.trackedEntity != entityPlayerMP2) {
               entityTrackerEntry3.updatePlayerEntity(entityPlayerMP2);
            }
         }
      } else if (entityIn instanceof EntityFishHook) {
         this.addEntityToTracker(entityIn, 64, 5, true);
      } else if (entityIn instanceof EntityArrow) {
         this.addEntityToTracker(entityIn, 64, 20, false);
      } else if (entityIn instanceof EntitySmallFireball) {
         this.addEntityToTracker(entityIn, 64, 10, false);
      } else if (entityIn instanceof EntityFireball) {
         this.addEntityToTracker(entityIn, 64, 10, false);
      } else if (entityIn instanceof EntitySnowball) {
         this.addEntityToTracker(entityIn, 64, 10, true);
      } else if (entityIn instanceof EntityEnderPearl) {
         this.addEntityToTracker(entityIn, 64, 10, true);
      } else if (entityIn instanceof EntityEnderEye) {
         this.addEntityToTracker(entityIn, 64, 4, true);
      } else if (entityIn instanceof EntityEgg) {
         this.addEntityToTracker(entityIn, 64, 10, true);
      } else if (entityIn instanceof EntityPotion) {
         this.addEntityToTracker(entityIn, 64, 10, true);
      } else if (entityIn instanceof EntityExpBottle) {
         this.addEntityToTracker(entityIn, 64, 10, true);
      } else if (entityIn instanceof EntityFireworkRocket) {
         this.addEntityToTracker(entityIn, 64, 10, true);
      } else if (entityIn instanceof EntityItem) {
         this.addEntityToTracker(entityIn, 64, 20, true);
      } else if (entityIn instanceof EntityMinecart) {
         this.addEntityToTracker(entityIn, 80, 3, true);
      } else if (entityIn instanceof EntityBoat) {
         this.addEntityToTracker(entityIn, 80, 3, true);
      } else if (entityIn instanceof EntitySquid) {
         this.addEntityToTracker(entityIn, 64, 3, true);
      } else if (entityIn instanceof EntityWither) {
         this.addEntityToTracker(entityIn, 80, 3, false);
      } else if (entityIn instanceof EntityBat) {
         this.addEntityToTracker(entityIn, 80, 3, false);
      } else if (entityIn instanceof EntityDragon) {
         this.addEntityToTracker(entityIn, 160, 3, true);
      } else if (entityIn instanceof IAnimals) {
         this.addEntityToTracker(entityIn, 80, 3, true);
      } else if (entityIn instanceof EntityTNTPrimed) {
         this.addEntityToTracker(entityIn, 160, 10, true);
      } else if (entityIn instanceof EntityFallingBlock) {
         this.addEntityToTracker(entityIn, 160, 20, true);
      } else if (entityIn instanceof EntityHanging) {
         this.addEntityToTracker(entityIn, 160, Integer.MAX_VALUE, false);
      } else if (entityIn instanceof EntityArmorStand) {
         this.addEntityToTracker(entityIn, 160, 3, true);
      } else if (entityIn instanceof EntityXPOrb) {
         this.addEntityToTracker(entityIn, 160, 20, true);
      } else if (entityIn instanceof EntityEnderCrystal) {
         this.addEntityToTracker(entityIn, 256, Integer.MAX_VALUE, false);
      }
   }

   public void handleEntity(Entity entity2, int i) {
      this.addEntityToTracker(entity2, 512, i, false);
   }

   public void sendLeashedEntitiesInChunk(EntityPlayerMP player, Chunk chunkIn) {
      for (EntityTrackerEntry entityTrackerEntry3 : this.trackedEntities) {
         if (entityTrackerEntry3.trackedEntity != player && entityTrackerEntry3.trackedEntity.chunkCoordX == chunkIn.xPosition && entityTrackerEntry3.trackedEntity.chunkCoordZ == chunkIn.zPosition) {
            entityTrackerEntry3.updatePlayerEntity(player);
         }
      }
   }

   public void updateTrackedEntities() {
      ArrayList arrayList = Lists.newArrayList();

      for (EntityTrackerEntry entityTrackerEntry3 : this.trackedEntities) {
         entityTrackerEntry3.updatePlayerList(this.theWorld.playerEntities);
         if (entityTrackerEntry3.playerEntitiesUpdated && entityTrackerEntry3.trackedEntity instanceof EntityPlayerMP) {
            arrayList.add((EntityPlayerMP)entityTrackerEntry3.trackedEntity);
         }
      }

      for (int i = 0; i < arrayList.size(); i++) {
         EntityPlayerMP entityPlayerMP2 = (EntityPlayerMP)arrayList.get(i);

         for (EntityTrackerEntry entityTrackerEntry4 : this.trackedEntities) {
            if (entityTrackerEntry4.trackedEntity != entityPlayerMP2) {
               entityTrackerEntry4.updatePlayerEntity(entityPlayerMP2);
            }
         }
      }
   }

   public void sendToAllTrackingEntityAndSelf(Entity entityIn, Packet packetIn) {
      EntityTrackerEntry entityTrackerEntry = this.trackedEntityHashTable.lookup(entityIn.getEntityId());
      if (entityTrackerEntry != null) {
         entityTrackerEntry.sendPacketToTrackedPlayersAndTrackedEntity(packetIn);
      }
   }

   public void sendToAllTrackingEntity(Entity entityIn, Packet packet) {
      EntityTrackerEntry entityTrackerEntry = this.trackedEntityHashTable.lookup(entityIn.getEntityId());
      if (entityTrackerEntry != null) {
         entityTrackerEntry.sendPacketToTrackedPlayers(packet);
      }
   }

   public void updateVisibility(EntityPlayerMP player) {
      for (EntityTrackerEntry entityTrackerEntry3 : this.trackedEntities) {
         if (entityTrackerEntry3.trackedEntity == player) {
            entityTrackerEntry3.updatePlayerEntities(this.theWorld.playerEntities);
         } else {
            entityTrackerEntry3.updatePlayerEntity(player);
         }
      }
   }

   public void untrackEntity(Entity entityIn) {
      if (entityIn instanceof EntityPlayerMP entityPlayerMP) {
         Iterator iterator2 = this.trackedEntities.iterator();

         while (iterator2.hasNext()) {
            ((EntityTrackerEntry)iterator2.next()).removeFromTrackedPlayers(entityPlayerMP);
         }
      }

      EntityTrackerEntry entityTrackerEntry2 = this.trackedEntityHashTable.removeObject(entityIn.getEntityId());
      if (entityTrackerEntry2 != null) {
         this.trackedEntities.remove(entityTrackerEntry2);
         entityTrackerEntry2.sendDestroyEntityPacketToTrackedPlayers();
      }
   }

   public void addEntityToTracker(Entity entityIn, int trackingRange, final int updateFrequency, boolean sendVelocityUpdates) {
      if (trackingRange > this.maxTrackingDistanceThreshold) {
         trackingRange = this.maxTrackingDistanceThreshold;
      }

      try {
         if (this.trackedEntityHashTable.containsItem(entityIn.getEntityId())) {
            throw new IllegalStateException("Entity is already tracked!");
         }

         EntityTrackerEntry entityTrackerEntry3 = new EntityTrackerEntry(entityIn, trackingRange, updateFrequency, sendVelocityUpdates);
         this.trackedEntities.add(entityTrackerEntry3);
         this.trackedEntityHashTable.addKey(entityIn.getEntityId(), entityTrackerEntry3);
         entityTrackerEntry3.updatePlayerEntities(this.theWorld.playerEntities);
      } catch (Throwable throwable) {
         CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Adding entity to track");
         CrashReportCategory crashReportCategory = crashReport.makeCategory("Entity To Track");
         crashReportCategory.addCrashSection("Tracking range", trackingRange + " blocks");
         crashReportCategory.addCrashSectionCallable("Update interval", new Callable<String>() {
            public String call() throws Exception {
               String text = "Once per " + updateFrequency + " ticks";
               if (updateFrequency == Integer.MAX_VALUE) {
                  text = "Maximum (" + text + ")";
               }

               return text;
            }
         });
         entityIn.addEntityCrashInfo(crashReportCategory);
         CrashReportCategory crashReportCategory2 = crashReport.makeCategory("Entity That Is Already Tracked");
         this.trackedEntityHashTable.lookup(entityIn.getEntityId()).trackedEntity.addEntityCrashInfo(crashReportCategory2);

         try {
            throw new ReportedException(crashReport);
         } catch (ReportedException reportedException) {
            logger.error("\"Silently\" catching entity tracking error.", reportedException);
         }
      }
   }
}
