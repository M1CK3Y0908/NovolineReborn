package net.minecraft.server.management;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S41PacketServerDifficulty;
import net.minecraft.network.play.server.S44PacketWorldBorder;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsFile;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.demo.DemoWorldManager;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ServerConfigurationManager {
   private boolean whiteListEnforced;
   private final List<EntityPlayerMP> playerEntityList = Lists.newArrayList();
   public static final File FILE_PLAYERBANS = new File("banned-players.json");
   public static final File FILE_IPBANS = new File("banned-ips.json");
   private final Map<UUID, EntityPlayerMP> uuidToPlayerMap = Maps.newHashMap();
   private boolean commandsAllowedForAll;
   private final UserListBans bannedPlayers = new UserListBans(FILE_PLAYERBANS);
   private int playerPingIndex;
   private net.minecraft.world.WorldSettings.GameType gameType;
   public static final File FILE_OPS = new File("ops.json");
   public static final File FILE_WHITELIST = new File("whitelist.json");
   private int viewDistance;
   private static final Logger logger = LogManager.getLogger();
   private final BanList bannedIPs = new BanList(FILE_IPBANS);
   private IPlayerFileData playerNBTManagerObj;
   private final UserListOps ops = new UserListOps(FILE_OPS);
   private final UserListWhitelist whiteListedPlayers = new UserListWhitelist(FILE_WHITELIST);
   private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
   private final Map<UUID, StatisticsFile> playerStatFiles = Maps.newHashMap();
   private final MinecraftServer mcServer;
   protected int maxPlayers;

   public UserListWhitelist getWhitelistedPlayers() {
      return this.whiteListedPlayers;
   }

   public int getViewDistance() {
      return this.viewDistance;
   }

   public String[] getOppedPlayerNames() {
      return this.ops.getKeys();
   }

   public String[] getWhitelistedPlayerNames() {
      return this.whiteListedPlayers.getKeys();
   }

   public List<EntityPlayerMP> getPlayersMatchingAddress(String address) {
      ArrayList arrayList = Lists.newArrayList();

      for (EntityPlayerMP entityPlayerMP2 : this.playerEntityList) {
         if (entityPlayerMP2.getPlayerIP().equals(address)) {
            arrayList.add(entityPlayerMP2);
         }
      }

      return arrayList;
   }

   public void setGameType(net.minecraft.world.WorldSettings.GameType gameType2) {
      this.gameType = gameType2;
   }

   public void sendPacketToAllPlayersInDimension(Packet packetIn, int dimension) {
      for (int index = 0; index < this.playerEntityList.size(); index++) {
         EntityPlayerMP entityPlayerMP = this.playerEntityList.get(index);
         if (entityPlayerMP.dimension == dimension) {
            entityPlayerMP.playerNetServerHandler.sendPacket(packetIn);
         }
      }
   }

   public void transferEntityToWorld(Entity entityIn, int i, WorldServer oldWorldIn, WorldServer toWorldIn) {
      double posX2 = entityIn.posX;
      double posZ2 = entityIn.posZ;
      double d = 8.0;
      float rotationYaw2 = entityIn.rotationYaw;
      oldWorldIn.theProfiler.startSection("moving");
      if (entityIn.dimension == -1) {
         posX2 = MathHelper.clamp_double(posX2 / d, toWorldIn.getWorldBorder().minX() + 16.0, toWorldIn.getWorldBorder().maxX() - 16.0);
         posZ2 = MathHelper.clamp_double(posZ2 / d, toWorldIn.getWorldBorder().minZ() + 16.0, toWorldIn.getWorldBorder().maxZ() - 16.0);
         entityIn.setLocationAndAngles(posX2, entityIn.posY, posZ2, entityIn.rotationYaw, entityIn.rotationPitch);
         if (entityIn.isEntityAlive()) {
            oldWorldIn.updateEntityWithOptionalForce(entityIn, false);
         }
      } else if (entityIn.dimension == 0) {
         posX2 = MathHelper.clamp_double(posX2 * d, toWorldIn.getWorldBorder().minX() + 16.0, toWorldIn.getWorldBorder().maxX() - 16.0);
         posZ2 = MathHelper.clamp_double(posZ2 * d, toWorldIn.getWorldBorder().minZ() + 16.0, toWorldIn.getWorldBorder().maxZ() - 16.0);
         entityIn.setLocationAndAngles(posX2, entityIn.posY, posZ2, entityIn.rotationYaw, entityIn.rotationPitch);
         if (entityIn.isEntityAlive()) {
            oldWorldIn.updateEntityWithOptionalForce(entityIn, false);
         }
      } else {
         BlockPos blockPos;
         if (i == 1) {
            blockPos = toWorldIn.getSpawnPoint();
         } else {
            blockPos = toWorldIn.getSpawnCoordinate();
         }

         posX2 = blockPos.getX();
         entityIn.posY = blockPos.getY();
         posZ2 = blockPos.getZ();
         entityIn.setLocationAndAngles(posX2, entityIn.posY, posZ2, 90.0F, 0.0F);
         if (entityIn.isEntityAlive()) {
            oldWorldIn.updateEntityWithOptionalForce(entityIn, false);
         }
      }

      oldWorldIn.theProfiler.endSection();
      if (i != 1) {
         oldWorldIn.theProfiler.startSection("placing");
         posX2 = MathHelper.clamp_int((int)posX2, -29999872, 29999872);
         posZ2 = MathHelper.clamp_int((int)posZ2, -29999872, 29999872);
         if (entityIn.isEntityAlive()) {
            entityIn.setLocationAndAngles(posX2, entityIn.posY, posZ2, entityIn.rotationYaw, entityIn.rotationPitch);
            toWorldIn.getDefaultTeleporter().placeInPortal(entityIn, rotationYaw2);
            toWorldIn.spawnEntityInWorld(entityIn);
            toWorldIn.updateEntityWithOptionalForce(entityIn, false);
         }

         oldWorldIn.theProfiler.endSection();
      }

      entityIn.setWorld(toWorldIn);
   }

   public boolean canSendCommands(GameProfile profile) {
      return this.ops.hasEntry(profile)
         || this.mcServer.isSinglePlayer()
            && this.mcServer.worldServers[0].getWorldInfo().areCommandsAllowed()
            && this.mcServer.getServerOwner().equalsIgnoreCase(profile.getName())
         || this.commandsAllowedForAll;
   }

   public String[] getAllUsernames() {
      String[] stringArray = new String[this.playerEntityList.size()];

      for (int index = 0; index < this.playerEntityList.size(); index++) {
         stringArray[index] = this.playerEntityList.get(index).getName();
      }

      return stringArray;
   }

   public void transferPlayerToDimension(EntityPlayerMP playerIn, int dimension) {
      int dimension2 = playerIn.dimension;
      WorldServer worldServer2 = this.mcServer.worldServerForDimension(playerIn.dimension);
      playerIn.dimension = dimension;
      WorldServer worldServer3 = this.mcServer.worldServerForDimension(playerIn.dimension);
      playerIn.playerNetServerHandler
         .sendPacket(
            new S07PacketRespawn(
               playerIn.dimension,
               playerIn.worldObj.getDifficulty(),
               playerIn.worldObj.getWorldInfo().getTerrainType(),
               playerIn.theItemInWorldManager.getGameType()
            )
         );
      worldServer2.removePlayerEntityDangerously(playerIn);
      playerIn.isDead = false;
      this.transferEntityToWorld(playerIn, dimension2, worldServer2, worldServer3);
      this.preparePlayer(playerIn, worldServer2);
      playerIn.playerNetServerHandler.setPlayerLocation(playerIn.posX, playerIn.posY, playerIn.posZ, playerIn.rotationYaw, playerIn.rotationPitch);
      playerIn.theItemInWorldManager.setWorld(worldServer3);
      this.updateTimeAndWeatherForPlayer(playerIn, worldServer3);
      this.syncPlayerInventory(playerIn);

      for (PotionEffect potionEffect : playerIn.getActivePotionEffects()) {
         playerIn.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(playerIn.getEntityId(), potionEffect));
      }
   }

   public void removePlayerFromWhitelist(GameProfile profile) {
      this.whiteListedPlayers.removeEntry(profile);
   }

   public BanList getBannedIPs() {
      return this.bannedIPs;
   }

   public void initializeConnectionToPlayer(NetworkManager netManager, EntityPlayerMP playerIn) {
      GameProfile gameProfile = playerIn.getGameProfile();
      PlayerProfileCache playerProfileCache = this.mcServer.getPlayerProfileCache();
      GameProfile profileByUUID = playerProfileCache.getProfileByUUID(gameProfile.getId());
      String text2 = profileByUUID == null ? gameProfile.getName() : profileByUUID.getName();
      playerProfileCache.addEntry(gameProfile);
      NBTTagCompound playerDataFromFile = this.readPlayerDataFromFile(playerIn);
      playerIn.setWorld(this.mcServer.worldServerForDimension(playerIn.dimension));
      playerIn.theItemInWorldManager.setWorld((WorldServer)playerIn.worldObj);
      String text3 = "local";
      if (netManager.getRemoteAddress() != null) {
         text3 = netManager.getRemoteAddress().toString();
      }

      Logger logger2 = logger;
      String name = playerIn.getName();
      int entityId = playerIn.getEntityId();
      double posZ2 = playerIn.posZ;
      double posY2 = playerIn.posY;
      double posX2 = playerIn.posX;
      logger2.info(name + "[" + text3 + "] logged in with entity id " + entityId + " at (" + posX2 + ", " + posY2 + ", " + posZ2 + ")");
      WorldServer worldServer2 = this.mcServer.worldServerForDimension(playerIn.dimension);
      WorldInfo worldInfo = worldServer2.getWorldInfo();
      BlockPos spawnPoint = worldServer2.getSpawnPoint();
      this.setPlayerGameTypeBasedOnOther(playerIn, (EntityPlayerMP)null, worldServer2);
      NetHandlerPlayServer netHandlerPlayServer = new NetHandlerPlayServer(this.mcServer, netManager, playerIn);
      netHandlerPlayServer.sendPacket(
         new S01PacketJoinGame(
            playerIn.getEntityId(),
            playerIn.theItemInWorldManager.getGameType(),
            worldInfo.isHardcoreModeEnabled(),
            worldServer2.provider.getDimensionId(),
            worldServer2.getDifficulty(),
            this.getMaxPlayers(),
            worldInfo.getTerrainType(),
            worldServer2.getGameRules().getBoolean("reducedDebugInfo")
         )
      );
      netHandlerPlayServer.sendPacket(new S3FPacketCustomPayload("MC|Brand", new PacketBuffer(Unpooled.buffer()).writeString(this.getServerInstance().getServerModName())));
      netHandlerPlayServer.sendPacket(new S41PacketServerDifficulty(worldInfo.getDifficulty(), worldInfo.isDifficultyLocked()));
      netHandlerPlayServer.sendPacket(new S05PacketSpawnPosition(spawnPoint));
      netHandlerPlayServer.sendPacket(new S39PacketPlayerAbilities(playerIn.capabilities));
      netHandlerPlayServer.sendPacket(new S09PacketHeldItemChange(playerIn.inventory.currentItem));
      playerIn.getStatFile().markAllDirty();
      playerIn.getStatFile().sendAchievements(playerIn);
      this.sendScoreboard((ServerScoreboard)worldServer2.getScoreboard(), playerIn);
      this.mcServer.refreshStatusNextTick();
      ChatComponentTranslation chatComponentTranslation;
      if (!playerIn.getName().equalsIgnoreCase(text2)) {
         chatComponentTranslation = new ChatComponentTranslation("multiplayer.player.joined.renamed", playerIn.getDisplayName(), text2);
      } else {
         chatComponentTranslation = new ChatComponentTranslation("multiplayer.player.joined", playerIn.getDisplayName());
      }

      chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.YELLOW);
      this.sendChatMsg(chatComponentTranslation);
      this.playerLoggedIn(playerIn);
      netHandlerPlayServer.setPlayerLocation(playerIn.posX, playerIn.posY, playerIn.posZ, playerIn.rotationYaw, playerIn.rotationPitch);
      this.updateTimeAndWeatherForPlayer(playerIn, worldServer2);
      if (this.mcServer.getResourcePackUrl().length() > 0) {
         playerIn.loadResourcePack(this.mcServer.getResourcePackUrl(), this.mcServer.getResourcePackHash());
      }

      for (PotionEffect potionEffect : playerIn.getActivePotionEffects()) {
         netHandlerPlayServer.sendPacket(new S1DPacketEntityEffect(playerIn.getEntityId(), potionEffect));
      }

      playerIn.addSelfToInternalCraftingInventory();
      if (playerDataFromFile != null && playerDataFromFile.hasKey("Riding", 10)) {
         Entity entityFromNBT = EntityList.createEntityFromNBT(playerDataFromFile.getCompoundTag("Riding"), worldServer2);
         if (entityFromNBT != null) {
            entityFromNBT.forceSpawn = true;
            worldServer2.spawnEntityInWorld(entityFromNBT);
            playerIn.mountEntity(entityFromNBT);
            entityFromNBT.forceSpawn = false;
         }
      }
   }

   public int getCurrentPlayerCount() {
      return this.playerEntityList.size();
   }

   public void playerLoggedOut(EntityPlayerMP playerIn) {
      playerIn.triggerAchievement(StatList.leaveGameStat);
      this.writePlayerData(playerIn);
      WorldServer serverForPlayer = playerIn.getServerForPlayer();
      if (playerIn.ridingEntity != null) {
         serverForPlayer.removePlayerEntityDangerously(playerIn.ridingEntity);
         logger.debug("removing player mount");
      }

      serverForPlayer.removeEntity(playerIn);
      serverForPlayer.getPlayerManager().removePlayer(playerIn);
      this.playerEntityList.remove(playerIn);
      UUID uniqueID = playerIn.getUniqueID();
      if (this.uuidToPlayerMap.get(uniqueID) == playerIn) {
         this.uuidToPlayerMap.remove(uniqueID);
         this.playerStatFiles.remove(uniqueID);
      }

      this.sendPacketToAllPlayers(new S38PacketPlayerListItem(net.minecraft.network.play.server.S38PacketPlayerListItem.Action.REMOVE_PLAYER, playerIn));
   }

   public void sendToAllNear(double x, double y, double z, double radius, int dimension, Packet packetIn) {
      this.sendToAllNearExcept((EntityPlayer)null, x, y, z, radius, dimension, packetIn);
   }

   public void serverUpdateMountedMovingPlayer(EntityPlayerMP playerIn) {
      playerIn.getServerForPlayer().getPlayerManager().updateMountedMovingPlayer(playerIn);
   }

   public EntityPlayerMP getEntityPlayerMP(EntityPlayerMP entityPlayerMP2, boolean flag) {
      entityPlayerMP2.getServerForPlayer().getEntityTracker().removePlayerFromTrackers(entityPlayerMP2);
      entityPlayerMP2.getServerForPlayer().getEntityTracker().untrackEntity(entityPlayerMP2);
      entityPlayerMP2.getServerForPlayer().getPlayerManager().removePlayer(entityPlayerMP2);
      this.playerEntityList.remove(entityPlayerMP2);
      this.mcServer.worldServerForDimension(entityPlayerMP2.dimension).removePlayerEntityDangerously(entityPlayerMP2);
      BlockPos bedLocation = entityPlayerMP2.getBedLocation();
      boolean spawnForced = entityPlayerMP2.isSpawnForced();
      entityPlayerMP2.dimension = 0;
      ItemInWorldManager itemInWorldManager;
      if (this.mcServer.isDemo()) {
         itemInWorldManager = new DemoWorldManager(this.mcServer.worldServerForDimension(entityPlayerMP2.dimension));
      } else {
         itemInWorldManager = new ItemInWorldManager(this.mcServer.worldServerForDimension(entityPlayerMP2.dimension));
      }

      EntityPlayerMP entityPlayerMP3 = new EntityPlayerMP(this.mcServer, this.mcServer.worldServerForDimension(entityPlayerMP2.dimension), entityPlayerMP2.getGameProfile(), itemInWorldManager);
      entityPlayerMP3.playerNetServerHandler = entityPlayerMP2.playerNetServerHandler;
      entityPlayerMP3.clonePlayer(entityPlayerMP2, flag);
      entityPlayerMP3.setEntityId(entityPlayerMP2.getEntityId());
      entityPlayerMP3.setCommandStats(entityPlayerMP2);
      WorldServer worldServer2 = this.mcServer.worldServerForDimension(entityPlayerMP2.dimension);
      this.setPlayerGameTypeBasedOnOther(entityPlayerMP3, entityPlayerMP2, worldServer2);
      if (bedLocation != null) {
         BlockPos bedSpawnLocation = EntityPlayer.getBedSpawnLocation(this.mcServer.worldServerForDimension(entityPlayerMP2.dimension), bedLocation, spawnForced);
         if (bedSpawnLocation != null) {
            entityPlayerMP3.setLocationAndAngles(bedSpawnLocation.getX() + 0.5F, bedSpawnLocation.getY() + 0.1F, bedSpawnLocation.getZ() + 0.5F, 0.0F, 0.0F);
            entityPlayerMP3.setSpawnPoint(bedLocation, spawnForced);
         } else {
            entityPlayerMP3.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(0, 0.0F));
         }
      }

      worldServer2.theChunkProviderServer.loadChunk((int)entityPlayerMP3.posX >> 4, (int)entityPlayerMP3.posZ >> 4);

      while (!worldServer2.getCollidingBoundingBoxes(entityPlayerMP3, entityPlayerMP3.getEntityBoundingBox()).isEmpty() && entityPlayerMP3.posY < 256.0) {
         entityPlayerMP3.setPosition(entityPlayerMP3.posX, entityPlayerMP3.posY + 1.0, entityPlayerMP3.posZ);
      }

      entityPlayerMP3.playerNetServerHandler
         .sendPacket(
            new S07PacketRespawn(
               entityPlayerMP3.dimension, entityPlayerMP3.worldObj.getDifficulty(), entityPlayerMP3.worldObj.getWorldInfo().getTerrainType(), entityPlayerMP3.theItemInWorldManager.getGameType()
            )
         );
      BlockPos spawnPoint = worldServer2.getSpawnPoint();
      entityPlayerMP3.playerNetServerHandler.setPlayerLocation(entityPlayerMP3.posX, entityPlayerMP3.posY, entityPlayerMP3.posZ, entityPlayerMP3.rotationYaw, entityPlayerMP3.rotationPitch);
      entityPlayerMP3.playerNetServerHandler.sendPacket(new S05PacketSpawnPosition(spawnPoint));
      entityPlayerMP3.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(entityPlayerMP3.experience, entityPlayerMP3.experienceTotal, entityPlayerMP3.experienceLevel));
      this.updateTimeAndWeatherForPlayer(entityPlayerMP3, worldServer2);
      worldServer2.getPlayerManager().addPlayer(entityPlayerMP3);
      worldServer2.spawnEntityInWorld(entityPlayerMP3);
      this.playerEntityList.add(entityPlayerMP3);
      this.uuidToPlayerMap.put(entityPlayerMP3.getUniqueID(), entityPlayerMP3);
      entityPlayerMP3.addSelfToInternalCraftingInventory();
      entityPlayerMP3.setHealth(entityPlayerMP3.getHealth());
      return entityPlayerMP3;
   }

   public void sendChatMsg(IChatComponent component) {
      this.sendChatMsgImpl(component, true);
   }

   public int getEntityViewDistance() {
      return PlayerManager.getFurthestViewableBlock(this.getViewDistance());
   }

   public StatisticsFile getPlayerStatsFile(EntityPlayer playerIn) {
      UUID uUID = playerIn.getUniqueID();
      StatisticsFile statisticsFile = uUID == null ? null : this.playerStatFiles.get(uUID);
      if (statisticsFile == null) {
         File file = new File(this.mcServer.worldServerForDimension(0).getSaveHandler().getWorldDirectory(), "stats");
         File file2 = new File(file, uUID.toString() + ".json");
         if (!file2.exists()) {
            File file3 = new File(file, playerIn.getName() + ".json");
            if (file3.exists() && file3.isFile()) {
               file3.renameTo(file2);
            }
         }

         statisticsFile = new StatisticsFile(this.mcServer, file2);
         statisticsFile.readStatFile();
         this.playerStatFiles.put(uUID, statisticsFile);
      }

      return statisticsFile;
   }

   public void setPlayerManager(WorldServer[] worldServers) {
      this.playerNBTManagerObj = worldServers[0].getSaveHandler().getPlayerNBTManager();
      worldServers[0].getWorldBorder().addListener(new IBorderListener() {
         @Override
         public void onTransitionStarted(WorldBorder border, double oldSize, double newSize, long time) {
            ServerConfigurationManager.this.sendPacketToAllPlayers(new S44PacketWorldBorder(border, net.minecraft.network.play.server.S44PacketWorldBorder.Action.LERP_SIZE));
         }

         @Override
         public void onWarningDistanceChanged(WorldBorder worldBorder2, int i) {
            ServerConfigurationManager.this.sendPacketToAllPlayers(new S44PacketWorldBorder(worldBorder2, net.minecraft.network.play.server.S44PacketWorldBorder.Action.SET_WARNING_BLOCKS));
         }

         @Override
         public void onSizeChanged(WorldBorder border, double newSize) {
            ServerConfigurationManager.this.sendPacketToAllPlayers(new S44PacketWorldBorder(border, net.minecraft.network.play.server.S44PacketWorldBorder.Action.SET_SIZE));
         }

         @Override
         public void onCenterChanged(WorldBorder border, double x, double z) {
            ServerConfigurationManager.this.sendPacketToAllPlayers(new S44PacketWorldBorder(border, net.minecraft.network.play.server.S44PacketWorldBorder.Action.SET_CENTER));
         }

         @Override
         public void onWarningTimeChanged(WorldBorder worldBorder3, int i2) {
            ServerConfigurationManager.this.sendPacketToAllPlayers(new S44PacketWorldBorder(worldBorder3, net.minecraft.network.play.server.S44PacketWorldBorder.Action.SET_WARNING_TIME));
         }

         @Override
         public void onDamageAmountChanged(WorldBorder worldBorder4, double d) {
         }

         @Override
         public void onDamageBufferChanged(WorldBorder worldBorder5, double d2) {
         }
      });
   }

   public void setViewDistance(int distance) {
      this.viewDistance = distance;
      if (this.mcServer.worldServers != null) {
         for (WorldServer worldServer2 : this.mcServer.worldServers) {
            if (worldServer2 != null) {
               worldServer2.getPlayerManager().setPlayerViewRadius(distance);
            }
         }
      }
   }

   public EntityPlayerMP getPlayerByUUID(UUID playerUUID) {
      return this.uuidToPlayerMap.get(playerUUID);
   }

   public void setWhiteListEnabled(boolean whitelistEnabled) {
      this.whiteListEnforced = whitelistEnabled;
   }

   public void addOp(GameProfile profile) {
      this.ops.addEntry(new UserListOpsEntry(profile, this.mcServer.getOpPermissionLevel(), this.ops.bypassesPlayerLimit(profile)));
   }

   public NBTTagCompound getHostPlayerData() {
      return null;
   }

   public void loadWhiteList() {
   }

   public List<EntityPlayerMP> getPlayerList() {
      return this.playerEntityList;
   }

   protected void writePlayerData(EntityPlayerMP playerIn) {
      this.playerNBTManagerObj.writePlayerData(playerIn);
      StatisticsFile statisticsFile = this.playerStatFiles.get(playerIn.getUniqueID());
      if (statisticsFile != null) {
         statisticsFile.saveStatFile();
      }
   }

   public UserListBans getBannedPlayers() {
      return this.bannedPlayers;
   }

   public EntityPlayerMP createPlayerForUser(GameProfile profile) {
      UUID uuid = EntityPlayer.getUUID(profile);
      ArrayList arrayList = Lists.newArrayList();

      for (int i = 0; i < this.playerEntityList.size(); i++) {
         EntityPlayerMP entityPlayerMP2 = this.playerEntityList.get(i);
         if (entityPlayerMP2.getUniqueID().equals(uuid)) {
            arrayList.add(entityPlayerMP2);
         }
      }

      EntityPlayerMP entityPlayerMP3 = this.uuidToPlayerMap.get(profile.getId());
      if (entityPlayerMP3 != null && !arrayList.contains(entityPlayerMP3)) {
         arrayList.add(entityPlayerMP3);
      }

      Iterator iterator2 = arrayList.iterator();

      while (iterator2.hasNext()) {
         ((EntityPlayerMP)iterator2.next()).playerNetServerHandler.kickPlayerFromServer("You logged in from another location");
      }

      ItemInWorldManager itemInWorldManager;
      if (this.mcServer.isDemo()) {
         itemInWorldManager = new DemoWorldManager(this.mcServer.worldServerForDimension(0));
      } else {
         itemInWorldManager = new ItemInWorldManager(this.mcServer.worldServerForDimension(0));
      }

      return new EntityPlayerMP(this.mcServer, this.mcServer.worldServerForDimension(0), profile, itemInWorldManager);
   }

   public MinecraftServer getServerInstance() {
      return this.mcServer;
   }

   public EntityPlayerMP getPlayerByUsername(String username) {
      for (EntityPlayerMP entityPlayerMP2 : this.playerEntityList) {
         if (entityPlayerMP2.getName().equalsIgnoreCase(username)) {
            return entityPlayerMP2;
         }
      }

      return null;
   }

   public String allowUserToConnect(SocketAddress address, GameProfile profile) {
      if (this.bannedPlayers.isBanned(profile)) {
         UserListBansEntry userListBansEntry = this.bannedPlayers.getEntry(profile);
         String text2 = "You are banned from this server!\nReason: " + userListBansEntry.getBanReason();
         if (userListBansEntry.getBanEndDate() != null) {
            text2 = text2 + "\nYour ban will be removed on " + dateFormat.format(userListBansEntry.getBanEndDate());
         }

         return text2;
      } else {
         if (!this.canJoin(profile)) {
            return "You are not white-listed on this server!";
         }

         if (this.bannedIPs.isBanned(address)) {
            IPBanEntry pBanEntry = this.bannedIPs.getBanEntry(address);
            String text3 = "Your IP address is banned from this server!\nReason: " + pBanEntry.getBanReason();
            if (pBanEntry.getBanEndDate() != null) {
               text3 = text3 + "\nYour ban will be removed on " + dateFormat.format(pBanEntry.getBanEndDate());
            }

            return text3;
         } else {
            return this.playerEntityList.size() >= this.maxPlayers && !this.bypassesPlayerLimit(profile) ? "The server is full!" : null;
         }
      }
   }

   public void addWhitelistedPlayer(GameProfile profile) {
      this.whiteListedPlayers.addEntry(new UserListWhitelistEntry(profile));
   }

   public void removeOp(GameProfile profile) {
      this.ops.removeEntry(profile);
   }

   protected void sendScoreboard(ServerScoreboard scoreboardIn, EntityPlayerMP playerIn) {
      HashSet hashSet = Sets.newHashSet();

      for (ScorePlayerTeam scorePlayerTeam : scoreboardIn.getTeams()) {
         playerIn.playerNetServerHandler.sendPacket(new S3EPacketTeams(scorePlayerTeam, 0));
      }

      for (int i = 0; i < 19; i++) {
         ScoreObjective objectiveInDisplaySlot = scoreboardIn.getObjectiveInDisplaySlot(i);
         if (objectiveInDisplaySlot != null && !hashSet.contains(objectiveInDisplaySlot)) {
            for (Packet packet : scoreboardIn.getCreatePackets(objectiveInDisplaySlot)) {
               playerIn.playerNetServerHandler.sendPacket(packet);
            }

            hashSet.add(objectiveInDisplaySlot);
         }
      }
   }

   public NBTTagCompound readPlayerDataFromFile(EntityPlayerMP playerIn) {
      NBTTagCompound playerNBTTagCompound = this.mcServer.worldServers[0].getWorldInfo().getPlayerNBTTagCompound();
      NBTTagCompound nbtTagCompound;
      if (playerIn.getName().equals(this.mcServer.getServerOwner()) && playerNBTTagCompound != null) {
         playerIn.readFromNBT(playerNBTTagCompound);
         nbtTagCompound = playerNBTTagCompound;
         logger.debug("loading single player");
      } else {
         nbtTagCompound = this.playerNBTManagerObj.readPlayerData(playerIn);
      }

      return nbtTagCompound;
   }

   public String[] getAvailablePlayerDat() {
      return this.mcServer.worldServers[0].getSaveHandler().getPlayerNBTManager().getAvailablePlayerDat();
   }

   public void removeAllPlayers() {
      for (int index = 0; index < this.playerEntityList.size(); index++) {
         this.playerEntityList.get(index).playerNetServerHandler.kickPlayerFromServer("Server closed");
      }
   }

   public boolean canJoin(GameProfile profile) {
      return !this.whiteListEnforced || this.ops.hasEntry(profile) || this.whiteListedPlayers.hasEntry(profile);
   }

   public void setCommandsAllowedForAll(boolean flag) {
      this.commandsAllowedForAll = flag;
   }

   public void playerLoggedIn(EntityPlayerMP playerIn) {
      this.playerEntityList.add(playerIn);
      this.uuidToPlayerMap.put(playerIn.getUniqueID(), playerIn);
      this.sendPacketToAllPlayers(new S38PacketPlayerListItem(net.minecraft.network.play.server.S38PacketPlayerListItem.Action.ADD_PLAYER, playerIn));
      this.mcServer.worldServerForDimension(playerIn.dimension).spawnEntityInWorld(playerIn);
      this.preparePlayer(playerIn, (WorldServer)null);

      for (int i = 0; i < this.playerEntityList.size(); i++) {
         EntityPlayerMP entityPlayerMP2 = this.playerEntityList.get(i);
         playerIn.playerNetServerHandler.sendPacket(new S38PacketPlayerListItem(net.minecraft.network.play.server.S38PacketPlayerListItem.Action.ADD_PLAYER, entityPlayerMP2));
      }
   }

   public void syncPlayerInventory(EntityPlayerMP playerIn) {
      playerIn.sendContainerToPlayer(playerIn.inventoryContainer);
      playerIn.setPlayerHealthUpdated();
      playerIn.playerNetServerHandler.sendPacket(new S09PacketHeldItemChange(playerIn.inventory.currentItem));
   }

   public void sendChatMsgImpl(IChatComponent component, boolean isChat) {
      this.mcServer.addChatMessage(component);
      byte b0 = (byte)(isChat ? 1 : 0);
      this.sendPacketToAllPlayers(new S02PacketChat(component, b0));
   }

   public String getPlayerListString(boolean includeUuids) {
      String message = "";
      List<EntityPlayerMP> list = Lists.newArrayList(this.playerEntityList);

      for (int index = 0; index < list.size(); index++) {
         if (index > 0) {
            message = message + ", ";
         }

         message = message + list.get(index).getName();
         if (includeUuids) {
            message = message + " (" + list.get(index).getUniqueID().toString() + ")";
         }
      }

      return message;
   }

   public void sendToAllNearExcept(EntityPlayer entityPlayer, double x, double y, double z, double radius, int dimension, Packet packet) {
      for (int index = 0; index < this.playerEntityList.size(); index++) {
         EntityPlayerMP entityPlayerMP = this.playerEntityList.get(index);
         if (entityPlayerMP != entityPlayer && entityPlayerMP.dimension == dimension) {
            double xCoordinate = x - entityPlayerMP.posX;
            double yCoordinate = y - entityPlayerMP.posY;
            double zCoordinate = z - entityPlayerMP.posZ;
            if (xCoordinate * xCoordinate + yCoordinate * yCoordinate + zCoordinate * zCoordinate < radius * radius) {
               entityPlayerMP.playerNetServerHandler.sendPacket(packet);
            }
         }
      }
   }

   public int getMaxPlayers() {
      return this.maxPlayers;
   }

   public GameProfile[] getAllProfiles() {
      GameProfile[] agameprofile = new GameProfile[this.playerEntityList.size()];

      for (int index = 0; index < this.playerEntityList.size(); index++) {
         agameprofile[index] = this.playerEntityList.get(index).getGameProfile();
      }

      return agameprofile;
   }

   public void updateTimeAndWeatherForPlayer(EntityPlayerMP playerIn, WorldServer worldIn) {
      WorldBorder worldBorder = this.mcServer.worldServers[0].getWorldBorder();
      playerIn.playerNetServerHandler.sendPacket(new S44PacketWorldBorder(worldBorder, net.minecraft.network.play.server.S44PacketWorldBorder.Action.INITIALIZE));
      playerIn.playerNetServerHandler
         .sendPacket(new S03PacketTimeUpdate(worldIn.getTotalWorldTime(), worldIn.getWorldTime(), worldIn.getGameRules().getBoolean("doDaylightCycle")));
      if (worldIn.isRaining()) {
         playerIn.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(1, 0.0F));
         playerIn.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(7, worldIn.getRainStrength(1.0F)));
         playerIn.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(8, worldIn.getThunderStrength(1.0F)));
      }
   }

   public void sendPacketToAllPlayers(Packet packetIn) {
      for (int index = 0; index < this.playerEntityList.size(); index++) {
         this.playerEntityList.get(index).playerNetServerHandler.sendPacket(packetIn);
      }
   }

   public boolean bypassesPlayerLimit(GameProfile gameProfile) {
      return false;
   }

   public void sendMessageToTeamOrEvryPlayer(EntityPlayer player, IChatComponent message) {
      Team team = player.getTeam();
      if (team == null) {
         this.sendChatMsg(message);
      } else {
         for (int index = 0; index < this.playerEntityList.size(); index++) {
            EntityPlayerMP entityPlayerMP = this.playerEntityList.get(index);
            if (entityPlayerMP.getTeam() != team) {
               entityPlayerMP.addChatMessage(message);
            }
         }
      }
   }

   private void setPlayerGameTypeBasedOnOther(EntityPlayerMP entityPlayerMP2, EntityPlayerMP entityPlayerMP3, World worldIn) {
      if (entityPlayerMP3 != null) {
         entityPlayerMP2.theItemInWorldManager.setGameType(entityPlayerMP3.theItemInWorldManager.getGameType());
      } else if (this.gameType != null) {
         entityPlayerMP2.theItemInWorldManager.setGameType(this.gameType);
      }

      entityPlayerMP2.theItemInWorldManager.initializeGameType(worldIn.getWorldInfo().getGameType());
   }

   public void preparePlayer(EntityPlayerMP playerIn, WorldServer worldIn) {
      WorldServer worldServer = playerIn.getServerForPlayer();
      if (worldIn != null) {
         worldIn.getPlayerManager().removePlayer(playerIn);
      }

      worldServer.getPlayerManager().addPlayer(playerIn);
      worldServer.theChunkProviderServer.loadChunk((int)playerIn.posX >> 4, (int)playerIn.posZ >> 4);
   }

   public void saveAllPlayerData() {
      for (int index = 0; index < this.playerEntityList.size(); index++) {
         this.writePlayerData(this.playerEntityList.get(index));
      }
   }

   public ServerConfigurationManager(MinecraftServer server) {
      this.mcServer = server;
      this.bannedPlayers.update();
      this.bannedIPs.update();
      this.maxPlayers = 8;
   }

   public void onTick() {
      if (++this.playerPingIndex > 600) {
         this.sendPacketToAllPlayers(new S38PacketPlayerListItem(net.minecraft.network.play.server.S38PacketPlayerListItem.Action.UPDATE_LATENCY, this.playerEntityList));
         this.playerPingIndex = 0;
      }
   }

   public void sendMessageToAllTeamMembers(EntityPlayer player, IChatComponent message) {
      Team team2 = player.getTeam();
      if (team2 != null) {
         for (String text2 : team2.getMembershipCollection()) {
            EntityPlayerMP playerByUsername = this.getPlayerByUsername(text2);
            if (playerByUsername != null && playerByUsername != player) {
               playerByUsername.addChatMessage(message);
            }
         }
      }
   }

   public UserListOps getOppedPlayers() {
      return this.ops;
   }
}
