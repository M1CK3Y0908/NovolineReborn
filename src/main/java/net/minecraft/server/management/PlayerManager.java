package net.minecraft.server.management;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.optifine.ChunkPosComparator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerManager {
   private final List<EntityPlayerMP> players = Lists.newArrayList();
   private final LongHashMap<PlayerManager.PlayerInstance> playerInstances = new LongHashMap<>();
   private final List<PlayerManager.PlayerInstance> playerInstancesToUpdate = Lists.newArrayList();
   private final List<PlayerManager.PlayerInstance> playerInstanceList = Lists.newArrayList();
   private static final Logger pmLogger = LogManager.getLogger();
   private long previousTotalWorldTime;
   private final int[][] xzDirectionsConst = new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
   private int playerViewRadius;
   private final Map<EntityPlayerMP, Set<ChunkCoordIntPair>> mapPlayerPendingEntries = new HashMap<>();
   private final WorldServer theWorldServer;

   private PlayerManager.PlayerInstance getPlayerInstance(int chunkX, int chunkZ, boolean createIfAbsent) {
      long l = chunkX + 2147483647L | chunkZ + 2147483647L << 32;
      PlayerManager.PlayerInstance playermanager$playerinstance = this.playerInstances.getValueByKey(l);
      if (playermanager$playerinstance == null && createIfAbsent) {
         playermanager$playerinstance = new PlayerManager.PlayerInstance(chunkX, chunkZ);
         this.playerInstances.add(l, playermanager$playerinstance);
         this.playerInstanceList.add(playermanager$playerinstance);
      }

      return playermanager$playerinstance;
   }

   public static int getFurthestViewableBlock(int distance) {
      return distance * 16 - 16;
   }

   private boolean overlaps(int x1, int z1, int x2, int z2, int radius) {
      int i = x1 - x2;
      int i2 = z1 - z2;
      return i >= -radius && i <= radius ? i2 >= -radius && i2 <= radius : false;
   }

   private Set<ChunkCoordIntPair> getPendingEntriesSafe(EntityPlayerMP arg1) {
      Set<ChunkCoordIntPair> set = this.mapPlayerPendingEntries.get(arg1);
      if (set != null) {
         return set;
      }

      int i = Math.min(this.playerViewRadius, 8);
      int i2 = this.playerViewRadius * 2 + 1;
      int i3 = i * 2 + 1;
      int i4 = i2 * i2 - i3 * i3;
      i4 = Math.max(i4, 16);
      HashSet hashSet = new HashSet(i4);
      this.mapPlayerPendingEntries.put(arg1, hashSet);
      return hashSet;
   }

   public PlayerManager(WorldServer serverWorld) {
      this.theWorldServer = serverWorld;
      this.setPlayerViewRadius(serverWorld.getMinecraftServer().getConfigurationManager().getViewDistance());
   }

   public void removePlayer(EntityPlayerMP player) {
      this.mapPlayerPendingEntries.remove(player);
      int i = (int)player.managedPosX >> 4;
      int i2 = (int)player.managedPosZ >> 4;

      for (int nestedIndex = i - this.playerViewRadius; nestedIndex <= i + this.playerViewRadius; nestedIndex++) {
         for (int outerIndex = i2 - this.playerViewRadius; outerIndex <= i2 + this.playerViewRadius; outerIndex++) {
            PlayerManager.PlayerInstance playermanager$playerinstance = this.getPlayerInstance(nestedIndex, outerIndex, false);
            if (playermanager$playerinstance != null) {
               playermanager$playerinstance.removePlayer(player);
            }
         }
      }

      this.players.remove(player);
   }

   public void filterChunkLoadQueue(EntityPlayerMP player) {
      ArrayList arrayList = Lists.newArrayList(player.loadedChunks);
      int i = 0;
      int playerViewRadius2 = this.playerViewRadius;
      int i2 = (int)player.posX >> 4;
      int i3 = (int)player.posZ >> 4;
      int i4 = 0;
      int outerIndex = 0;
      ChunkCoordIntPair chunkCoords2 = this.getPlayerInstance(i2, i3, true).chunkCoords;
      player.loadedChunks.clear();
      if (arrayList.contains(chunkCoords2)) {
         player.loadedChunks.add(chunkCoords2);
      }

      for (int i5 = 1; i5 <= playerViewRadius2 * 2; i5++) {
         for (int i6 = 0; i6 < 2; i6++) {
            int[] iArray = this.xzDirectionsConst[i++ % 4];

            for (int i7 = 0; i7 < i5; i7++) {
               i4 += iArray[0];
               outerIndex += iArray[1];
               chunkCoords2 = this.getPlayerInstance(i2 + i4, i3 + outerIndex, true).chunkCoords;
               if (arrayList.contains(chunkCoords2)) {
                  player.loadedChunks.add(chunkCoords2);
               }
            }
         }
      }

      i %= 4;

      for (int i8 = 0; i8 < playerViewRadius2 * 2; i8++) {
         i4 += this.xzDirectionsConst[i][0];
         outerIndex += this.xzDirectionsConst[i][1];
         chunkCoords2 = this.getPlayerInstance(i2 + i4, i3 + outerIndex, true).chunkCoords;
         if (arrayList.contains(chunkCoords2)) {
            player.loadedChunks.add(chunkCoords2);
         }
      }
   }

   public void updateMountedMovingPlayer(EntityPlayerMP player) {
      int i = (int)player.posX >> 4;
      int i2 = (int)player.posZ >> 4;
      double xCoordinate = player.managedPosX - player.posX;
      double zCoordinate = player.managedPosZ - player.posZ;
      double d = xCoordinate * xCoordinate + zCoordinate * zCoordinate;
      if (d >= 64.0) {
         int i3 = (int)player.managedPosX >> 4;
         int i4 = (int)player.managedPosZ >> 4;
         int playerViewRadius2 = this.playerViewRadius;
         int i5 = i - i3;
         int i6 = i2 - i4;
         if (i5 != 0 || i6 != 0) {
            Set<ChunkCoordIntPair> set = this.getPendingEntriesSafe(player);

            for (int outerIndex = i - playerViewRadius2; outerIndex <= i + playerViewRadius2; outerIndex++) {
               for (int index = i2 - playerViewRadius2; index <= i2 + playerViewRadius2; index++) {
                  if (!this.overlaps(outerIndex, index, i3, i4, playerViewRadius2)) {
                     if (Config.isLazyChunkLoading()) {
                        set.add(new ChunkCoordIntPair(outerIndex, index));
                     } else {
                        this.getPlayerInstance(outerIndex, index, true).addPlayer(player);
                     }
                  }

                  if (!this.overlaps(outerIndex - i5, index - i6, i, i2, playerViewRadius2)) {
                     set.remove(new ChunkCoordIntPair(outerIndex - i5, index - i6));
                     PlayerManager.PlayerInstance playermanager$playerinstance = this.getPlayerInstance(
                        outerIndex - i5, index - i6, false
                     );
                     if (playermanager$playerinstance != null) {
                        playermanager$playerinstance.removePlayer(player);
                     }
                  }
               }
            }

            this.filterChunkLoadQueue(player);
            player.managedPosX = player.posX;
            player.managedPosZ = player.posZ;
         }
      }
   }

   public WorldServer getWorldServer() {
      return this.theWorldServer;
   }

   public void addPlayer(EntityPlayerMP player) {
      int i = (int)player.posX >> 4;
      int i2 = (int)player.posZ >> 4;
      player.managedPosX = player.posX;
      player.managedPosZ = player.posZ;
      int i3 = Math.min(this.playerViewRadius, 8);
      int i4 = i - i3;
      int i5 = i + i3;
      int i6 = i2 - i3;
      int i7 = i2 + i3;
      Set<ChunkCoordIntPair> set = this.getPendingEntriesSafe(player);

      for (int outerIndex = i - this.playerViewRadius; outerIndex <= i + this.playerViewRadius; outerIndex++) {
         for (int index = i2 - this.playerViewRadius; index <= i2 + this.playerViewRadius; index++) {
            if (outerIndex >= i4 && outerIndex <= i5 && index >= i6 && index <= i7) {
               this.getPlayerInstance(outerIndex, index, true).addPlayer(player);
            } else {
               set.add(new ChunkCoordIntPair(outerIndex, index));
            }
         }
      }

      this.players.add(player);
      this.filterChunkLoadQueue(player);
   }

   public void markBlockForUpdate(BlockPos pos) {
      int xCoordinate = pos.getX() >> 4;
      int zCoordinate = pos.getZ() >> 4;
      PlayerManager.PlayerInstance playermanager$playerinstance = this.getPlayerInstance(xCoordinate, zCoordinate, false);
      if (playermanager$playerinstance != null) {
         playermanager$playerinstance.flagChunkForUpdate(pos.getX() & 15, pos.getY(), pos.getZ() & 15);
      }
   }

   public void setPlayerViewRadius(int radius) {
      radius = MathHelper.clamp_int(radius, 3, 64);
      if (radius != this.playerViewRadius) {
         int i = radius - this.playerViewRadius;

         for (EntityPlayerMP entityPlayerMP2 : Lists.newArrayList(this.players)) {
            int i2 = (int)entityPlayerMP2.posX >> 4;
            int i3 = (int)entityPlayerMP2.posZ >> 4;
            Set pendingEntriesSafe = this.getPendingEntriesSafe(entityPlayerMP2);
            if (i > 0) {
               for (int i4 = i2 - radius; i4 <= i2 + radius; i4++) {
                  for (int i5 = i3 - radius; i5 <= i3 + radius; i5++) {
                     if (Config.isLazyChunkLoading()) {
                        pendingEntriesSafe.add(new ChunkCoordIntPair(i4, i5));
                     } else {
                        PlayerManager.PlayerInstance playerInstance = this.getPlayerInstance(i4, i5, true);
                        if (!playerInstance.playersWatchingChunk.contains(entityPlayerMP2)) {
                           playerInstance.addPlayer(entityPlayerMP2);
                        }
                     }
                  }
               }
            } else {
               for (int i6 = i2 - this.playerViewRadius; i6 <= i2 + this.playerViewRadius; i6++) {
                  for (int i7 = i3 - this.playerViewRadius; i7 <= i3 + this.playerViewRadius; i7++) {
                     if (!this.overlaps(i6, i7, i2, i3, radius)) {
                        pendingEntriesSafe.remove(new ChunkCoordIntPair(i6, i7));
                        PlayerManager.PlayerInstance playerInstance2 = this.getPlayerInstance(i6, i7, true);
                        if (playerInstance2 != null) {
                           playerInstance2.removePlayer(entityPlayerMP2);
                        }
                     }
                  }
               }
            }
         }

         this.playerViewRadius = radius;
      }
   }

   public void updatePlayerInstances() {
      Iterator iterator2 = this.mapPlayerPendingEntries.entrySet().iterator();

      while (iterator2.hasNext()) {
         Entry entry = (Entry)iterator2.next();
         Set set2 = (Set)entry.getValue();
         if (!set2.isEmpty()) {
            EntityPlayerMP entityPlayerMP2 = (EntityPlayerMP)entry.getKey();
            if (entityPlayerMP2.worldObj != this.theWorldServer) {
               iterator2.remove();
            } else {
               int i = this.playerViewRadius / 3 + 1;
               if (!Config.isLazyChunkLoading()) {
                  i = this.playerViewRadius * 2 + 1;
               }

               for (ChunkCoordIntPair chunkCoordIntPair : (Iterable<ChunkCoordIntPair>) this.getNearest(set2, entityPlayerMP2, i)) {
                  this.getPlayerInstance(chunkCoordIntPair.chunkXPos, chunkCoordIntPair.chunkZPos, true).addPlayer(entityPlayerMP2);
                  set2.remove(chunkCoordIntPair);
               }
            }
         }
      }

      long totalWorldTime = this.theWorldServer.getTotalWorldTime();
      if (totalWorldTime - this.previousTotalWorldTime > 8000L) {
         this.previousTotalWorldTime = totalWorldTime;

         for (int i2 = 0; i2 < this.playerInstanceList.size(); i2++) {
            PlayerManager.PlayerInstance playerInstance = this.playerInstanceList.get(i2);
            playerInstance.onUpdate();
            playerInstance.processChunk();
         }
      } else {
         for (int i3 = 0; i3 < this.playerInstancesToUpdate.size(); i3++) {
            PlayerManager.PlayerInstance playerInstance2 = this.playerInstancesToUpdate.get(i3);
            playerInstance2.onUpdate();
         }
      }

      this.playerInstancesToUpdate.clear();
      if (this.players.isEmpty()) {
         WorldProvider provider2 = this.theWorldServer.provider;
         if (!provider2.canRespawnHere()) {
            this.theWorldServer.theChunkProviderServer.unloadAllChunks();
         }
      }
   }

   private PriorityQueue<ChunkCoordIntPair> getNearest(Set<ChunkCoordIntPair> arg1, EntityPlayerMP arg2, int arg3) {
      float f = arg2.rotationYaw + 90.0F;

      while (f <= -180.0F) {
         f += 360.0F;
      }

      while (f > 180.0F) {
         f -= 360.0F;
      }

      double d = f * (Math.PI / 180.0);
      double rotationPitch2 = arg2.rotationPitch;
      double d2 = rotationPitch2 * (Math.PI / 180.0);
      ChunkPosComparator chunkPosComparator = new ChunkPosComparator(arg2.chunkCoordX, arg2.chunkCoordZ, d, d2);
      Comparator comparator = Collections.reverseOrder(chunkPosComparator);
      PriorityQueue priorityQueue = new PriorityQueue(arg3, comparator);

      for (ChunkCoordIntPair chunkCoordIntPair2 : arg1) {
         if (priorityQueue.size() < arg3) {
            priorityQueue.add(chunkCoordIntPair2);
         } else {
            ChunkCoordIntPair chunkCoordIntPair = (ChunkCoordIntPair)priorityQueue.peek();
            if (chunkPosComparator.compare(chunkCoordIntPair2, chunkCoordIntPair) < 0) {
               priorityQueue.remove();
               priorityQueue.add(chunkCoordIntPair2);
            }
         }
      }

      return priorityQueue;
   }

   public boolean hasPlayerInstance(int chunkX, int chunkZ) {
      long l = chunkX + 2147483647L | chunkZ + 2147483647L << 32;
      return this.playerInstances.getValueByKey(l) != null;
   }

   public boolean isPlayerWatchingChunk(EntityPlayerMP player, int chunkX, int chunkZ) {
      PlayerManager.PlayerInstance playermanager$playerinstance = this.getPlayerInstance(chunkX, chunkZ, false);
      return playermanager$playerinstance != null
         && playermanager$playerinstance.playersWatchingChunk.contains(player)
         && !player.loadedChunks.contains(playermanager$playerinstance.chunkCoords);
   }

   class PlayerInstance {
      private int numBlocksToUpdate;
      private final List<EntityPlayerMP> playersWatchingChunk = Lists.newArrayList();
      private long previousWorldTime;
      private short[] locationOfBlockChange = new short[64];
      private int flagsYAreasToUpdate;
      private final ChunkCoordIntPair chunkCoords;

      public PlayerInstance(int chunkX, int chunkZ) {
         this.chunkCoords = new ChunkCoordIntPair(chunkX, chunkZ);
         PlayerManager.this.getWorldServer().theChunkProviderServer.loadChunk(chunkX, chunkZ);
      }

      private void sendTileToAllPlayersWatchingChunk(TileEntity theTileEntity) {
         if (theTileEntity != null) {
            Packet packet = theTileEntity.getDescriptionPacket();
            if (packet != null) {
               this.sendToAllPlayersWatchingChunk(packet);
            }
         }
      }

      public void sendToAllPlayersWatchingChunk(Packet thePacket) {
         for (int index = 0; index < this.playersWatchingChunk.size(); index++) {
            EntityPlayerMP entityPlayerMP = this.playersWatchingChunk.get(index);
            if (!entityPlayerMP.loadedChunks.contains(this.chunkCoords)) {
               entityPlayerMP.playerNetServerHandler.sendPacket(thePacket);
            }
         }
      }

      public void onUpdate() {
         if (this.numBlocksToUpdate != 0) {
            if (this.numBlocksToUpdate == 1) {
               int i = (this.locationOfBlockChange[0] >> 12 & 15) + this.chunkCoords.chunkXPos * 16;
               int i2 = this.locationOfBlockChange[0] & 255;
               int i3 = (this.locationOfBlockChange[0] >> 8 & 15) + this.chunkCoords.chunkZPos * 16;
               BlockPos blockPos = new BlockPos(i, i2, i3);
               this.sendToAllPlayersWatchingChunk(new S23PacketBlockChange(PlayerManager.this.theWorldServer, blockPos));
               if (PlayerManager.this.theWorldServer.getBlockState(blockPos).getBlock().hasTileEntity()) {
                  this.sendTileToAllPlayersWatchingChunk(PlayerManager.this.theWorldServer.getTileEntity(blockPos));
               }
            } else if (this.numBlocksToUpdate != 64) {
               this.sendToAllPlayersWatchingChunk(
                  new S22PacketMultiBlockChange(
                     this.numBlocksToUpdate,
                     this.locationOfBlockChange,
                     PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos)
                  )
               );

               for (int innerIndex = 0; innerIndex < this.numBlocksToUpdate; innerIndex++) {
                  int i4 = (this.locationOfBlockChange[innerIndex] >> 12 & 15) + this.chunkCoords.chunkXPos * 16;
                  int i5 = this.locationOfBlockChange[innerIndex] & 255;
                  int i6 = (this.locationOfBlockChange[innerIndex] >> 8 & 15) + this.chunkCoords.chunkZPos * 16;
                  BlockPos blockPos2 = new BlockPos(i4, i5, i6);
                  if (PlayerManager.this.theWorldServer.getBlockState(blockPos2).getBlock().hasTileEntity()) {
                     this.sendTileToAllPlayersWatchingChunk(PlayerManager.this.theWorldServer.getTileEntity(blockPos2));
                  }
               }
            } else {
               int i7 = this.chunkCoords.chunkXPos * 16;
               int i8 = this.chunkCoords.chunkZPos * 16;
               this.sendToAllPlayersWatchingChunk(
                  new S21PacketChunkData(
                     PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos),
                     false,
                     this.flagsYAreasToUpdate
                  )
               );

               for (int nestedIndex = 0; nestedIndex < 16; nestedIndex++) {
                  if ((this.flagsYAreasToUpdate & 1 << nestedIndex) != 0) {
                     int i9 = nestedIndex << 4;
                     List<TileEntity> list = PlayerManager.this.theWorldServer
                        .getTileEntitiesIn(i7, i9, i8, i7 + 16, i9 + 16, i8 + 16);

                     for (int index = 0; index < list.size(); index++) {
                        this.sendTileToAllPlayersWatchingChunk(list.get(index));
                     }
                  }
               }
            }

            this.numBlocksToUpdate = 0;
            this.flagsYAreasToUpdate = 0;
         }
      }

      public void addPlayer(EntityPlayerMP player) {
         if (this.playersWatchingChunk.contains(player)) {
            PlayerManager.pmLogger.debug("Failed to add player. {} already is in chunk {}, {}", player, this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos);
         } else {
            if (this.playersWatchingChunk.isEmpty()) {
               this.previousWorldTime = PlayerManager.this.theWorldServer.getTotalWorldTime();
            }

            this.playersWatchingChunk.add(player);
            player.loadedChunks.add(this.chunkCoords);
         }
      }

      private void increaseInhabitedTime(Chunk theChunk) {
         theChunk.setInhabitedTime(theChunk.getInhabitedTime() + PlayerManager.this.theWorldServer.getTotalWorldTime() - this.previousWorldTime);
         this.previousWorldTime = PlayerManager.this.theWorldServer.getTotalWorldTime();
      }

      public void processChunk() {
         this.increaseInhabitedTime(PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos));
      }

      public void flagChunkForUpdate(int x, int y, int z) {
         if (this.numBlocksToUpdate == 0) {
            PlayerManager.this.playerInstancesToUpdate.add(this);
         }

         this.flagsYAreasToUpdate |= 1 << (y >> 4);
         if (this.numBlocksToUpdate < 64) {
            short s = (short)(x << 12 | z << 8 | y);

            for (int index = 0; index < this.numBlocksToUpdate; index++) {
               if (this.locationOfBlockChange[index] == s) {
                  return;
               }
            }

            this.locationOfBlockChange[this.numBlocksToUpdate++] = s;
         }
      }

      public void removePlayer(EntityPlayerMP player) {
         if (this.playersWatchingChunk.contains(player)) {
            Chunk chunk = PlayerManager.this.theWorldServer.getChunkFromChunkCoords(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos);
            if (chunk.isPopulated()) {
               player.playerNetServerHandler.sendPacket(new S21PacketChunkData(chunk, true, 0));
            }

            this.playersWatchingChunk.remove(player);
            player.loadedChunks.remove(this.chunkCoords);
            if (this.playersWatchingChunk.isEmpty()) {
               long l = this.chunkCoords.chunkXPos + 2147483647L | this.chunkCoords.chunkZPos + 2147483647L << 32;
               this.increaseInhabitedTime(chunk);
               PlayerManager.this.playerInstances.remove(l);
               PlayerManager.this.playerInstanceList.remove(this);
               if (this.numBlocksToUpdate > 0) {
                  PlayerManager.this.playerInstancesToUpdate.remove(this);
               }

               PlayerManager.this.getWorldServer().theChunkProviderServer.dropChunk(this.chunkCoords.chunkXPos, this.chunkCoords.chunkZPos);
            }
         }
      }
   }
}
