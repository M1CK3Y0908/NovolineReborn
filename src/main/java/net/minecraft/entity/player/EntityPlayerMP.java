package net.minecraft.entity.player;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerHorseInventory;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemMapBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.network.play.server.S31PacketWindowProperty;
import net.minecraft.network.play.server.S36PacketSignEditorOpen;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S42PacketCombatEvent;
import net.minecraft.network.play.server.S43PacketCamera;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsFile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.JsonSerializableSet;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityPlayerMP extends EntityPlayer implements ICrafting {
   private EntityPlayer.EnumChatVisibility chatVisibility;
   private String translator = "en_US";
   public final List<ChunkCoordIntPair> loadedChunks = Lists.newLinkedList();
   public boolean isChangingQuantityOnly;
   private final List<Integer> destroyedItemsNetCache = Lists.newLinkedList();
   private float combinedHealth = Float.MIN_VALUE;
   private int currentWindowId;
   private float lastHealth = -1.0E8F;
   private int lastFoodLevel = -99999999;
   private boolean wasHungry = true;
   public boolean playerConqueredTheEnd;
   public int ping;
   private int lastExperience = -99999999;
   public NetHandlerPlayServer playerNetServerHandler;
   private int respawnInvulnerabilityTicks = 60;
   private boolean chatColours = true;
   private long playerLastActiveTime = System.currentTimeMillis();
   private Entity spectatingEntity = null;
   public final ItemInWorldManager theItemInWorldManager;
   public double managedPosZ;
   public double managedPosX;
   public final MinecraftServer mcServer;
   private static final Logger logger = LogManager.getLogger();
   private final StatisticsFile statsFile;

   public long getLastActiveTime() {
      return this.playerLastActiveTime;
   }

   @Override
   public void attackTargetEntityWithCurrentItem(Entity targetEntity) {
      if (this.theItemInWorldManager.getGameType() == net.minecraft.world.WorldSettings.GameType.SPECTATOR) {
         this.setSpectatingEntity(targetEntity);
      } else {
         super.attackTargetEntityWithCurrentItem(targetEntity);
      }
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      if (tagCompund.hasKey("playerGameType", 99)) {
         if (MinecraftServer.getServer().getForceGamemode()) {
            this.theItemInWorldManager.setGameType(MinecraftServer.getServer().getGameType());
         } else {
            this.theItemInWorldManager.setGameType(net.minecraft.world.WorldSettings.GameType.getByID(tagCompund.getInteger("playerGameType")));
         }
      }
   }

   @Override
   public void setItemInUse(ItemStack stack, int duration) {
      super.setItemInUse(stack, duration);
      if (stack != null && stack.getItem() != null && stack.getItem().getItemUseAction(stack) == EnumAction.EAT) {
         this.getServerForPlayer().getEntityTracker().sendToAllTrackingEntityAndSelf(this, new S0BPacketAnimation(this, 3));
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setInteger("playerGameType", this.theItemInWorldManager.getGameType().getID());
   }

   @Override
   public void setGameType(net.minecraft.world.WorldSettings.GameType gameType) {
      this.theItemInWorldManager.setGameType(gameType);
      this.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(3, gameType.getID()));
      if (gameType == net.minecraft.world.WorldSettings.GameType.SPECTATOR) {
         this.mountEntity((Entity)null);
      } else {
         this.setSpectatingEntity(this);
      }

      this.sendPlayerAbilities();
      this.markPotionsDirty();
   }

   @Override
   public void onDeath(DamageSource cause) {
      if (this.worldObj.getGameRules().getBoolean("showDeathMessages")) {
         Team team = this.getTeam();
         if (team == null || team.getDeathMessageVisibility() == net.minecraft.scoreboard.Team.EnumVisible.ALWAYS) {
            this.mcServer.getConfigurationManager().sendChatMsg(this.getCombatTracker().getDeathMessage());
         } else if (team.getDeathMessageVisibility() == net.minecraft.scoreboard.Team.EnumVisible.HIDE_FOR_OTHER_TEAMS) {
            this.mcServer.getConfigurationManager().sendMessageToAllTeamMembers(this, this.getCombatTracker().getDeathMessage());
         } else if (team.getDeathMessageVisibility() == net.minecraft.scoreboard.Team.EnumVisible.HIDE_FOR_OWN_TEAM) {
            this.mcServer.getConfigurationManager().sendMessageToTeamOrEvryPlayer(this, this.getCombatTracker().getDeathMessage());
         }
      }

      if (!this.worldObj.getGameRules().getBoolean("keepInventory")) {
         this.inventory.dropAllItems();
      }

      for (ScoreObjective entitylist$entityegginfo : this.worldObj.getScoreboard().getObjectivesFromCriteria(IScoreObjectiveCriteria.deathCount)) {
         this.getWorldScoreboard().getValueFromObjective(this.getName(), entitylist$entityegginfo).incrementScore();
      }

      EntityLivingBase entityLivingBase = this.getAttackingEntity();
      if (entityLivingBase != null) {
         net.minecraft.entity.EntityList.EntityEggInfo entitylist$entityegginfo = EntityList.entityEggs.get(EntityList.getEntityID(entityLivingBase));
         if (entitylist$entityegginfo != null) {
            this.triggerAchievement(entitylist$entityegginfo.entityKilledByStat);
         }

         entityLivingBase.addToPlayerScore(this, this.scoreValue);
      }

      this.triggerAchievement(StatList.deathsStat);
      this.trackStat(StatList.timeSinceDeathStat);
      this.getCombatTracker().reset();
   }

   @Override
   protected void updatePotionMetadata() {
      if (this.isSpectator()) {
         this.resetPotionEffectMetadata();
         this.setInvisible(true);
      } else {
         super.updatePotionMetadata();
      }

      this.getServerForPlayer().getEntityTracker().updateVisibility(this);
   }

   public void setEntityActionState(float f, float f2, boolean flag, boolean sneaking) {
      if (this.ridingEntity != null) {
         if (f >= -1.0F && f <= 1.0F) {
            this.moveStrafing = f;
         }

         if (f2 >= -1.0F && f2 <= 1.0F) {
            this.moveForward = f2;
         }

         this.isJumping = flag;
         this.setSneaking(sneaking);
      }
   }

   @Override
   protected void onItemUseFinish() {
      this.playerNetServerHandler.sendPacket(new S19PacketEntityStatus(this, (byte)9));
      super.onItemUseFinish();
   }

   @Override
   public void onUpdate() {
      this.theItemInWorldManager.updateBlockRemoving();
      this.respawnInvulnerabilityTicks--;
      if (this.hurtResistantTime > 0) {
         this.hurtResistantTime--;
      }

      this.openContainer.detectAndSendChanges();
      if (!this.worldObj.isRemote && !this.openContainer.canInteractWith(this)) {
         this.closeScreen();
         this.openContainer = this.inventoryContainer;
      }

      while (!this.destroyedItemsNetCache.isEmpty()) {
         int i = Math.min(this.destroyedItemsNetCache.size(), Integer.MAX_VALUE);
         int[] iArray = new int[i];
         Iterator iterator2 = this.destroyedItemsNetCache.iterator();
         int i2 = 0;

         while (iterator2.hasNext() && i2 < i) {
            iArray[i2++] = (Integer)iterator2.next();
            iterator2.remove();
         }

         this.playerNetServerHandler.sendPacket(new S13PacketDestroyEntities(iArray));
      }

      if (!this.loadedChunks.isEmpty()) {
         ArrayList arrayList = Lists.newArrayList();
         Iterator iterator3 = this.loadedChunks.iterator();
         ArrayList arrayList2 = Lists.newArrayList();

         while (iterator3.hasNext() && arrayList.size() < 10) {
            ChunkCoordIntPair chunkCoordIntPair = (ChunkCoordIntPair)iterator3.next();
            if (chunkCoordIntPair != null) {
               if (this.worldObj.isBlockLoaded(new BlockPos(chunkCoordIntPair.chunkXPos << 4, 0, chunkCoordIntPair.chunkZPos << 4))) {
                  Chunk chunkFromChunkCoords = this.worldObj.getChunkFromChunkCoords(chunkCoordIntPair.chunkXPos, chunkCoordIntPair.chunkZPos);
                  if (chunkFromChunkCoords.isPopulated()) {
                     arrayList.add(chunkFromChunkCoords);
                     arrayList2.addAll(
                        ((WorldServer)this.worldObj)
                           .getTileEntitiesIn(chunkCoordIntPair.chunkXPos * 16, 0, chunkCoordIntPair.chunkZPos * 16, chunkCoordIntPair.chunkXPos * 16 + 16, 256, chunkCoordIntPair.chunkZPos * 16 + 16)
                     );
                     iterator3.remove();
                  }
               }
            } else {
               iterator3.remove();
            }
         }

         if (!arrayList.isEmpty()) {
            if (arrayList.size() == 1) {
               this.playerNetServerHandler.sendPacket(new S21PacketChunkData((Chunk)arrayList.get(0), true, 65535));
            } else {
               this.playerNetServerHandler.sendPacket(new S26PacketMapChunkBulk(arrayList));
            }

            for (TileEntity tileEntity : (Iterable<TileEntity>) arrayList2) {
               this.sendTileEntityUpdate(tileEntity);
            }

            for (Chunk chunk2 : (Iterable<Chunk>) arrayList) {
               this.getServerForPlayer().getEntityTracker().sendLeashedEntitiesInChunk(this, chunk2);
            }
         }
      }

      Entity spectatingEntity2 = this.getSpectatingEntity();
      if (spectatingEntity2 != this) {
         if (!spectatingEntity2.isEntityAlive()) {
            this.setSpectatingEntity(this);
         } else {
            this.setPositionAndRotation(spectatingEntity2.posX, spectatingEntity2.posY, spectatingEntity2.posZ, spectatingEntity2.rotationYaw, spectatingEntity2.rotationPitch);
            this.mcServer.getConfigurationManager().serverUpdateMountedMovingPlayer(this);
            if (this.isSneaking()) {
               this.setSpectatingEntity(this);
            }
         }
      }
   }

   public void closeContainer() {
      this.openContainer.onContainerClosed(this);
      this.openContainer = this.inventoryContainer;
   }

   public void handleClientSettings(C15PacketClientSettings packetIn) {
      this.translator = packetIn.lang;
      this.chatVisibility = packetIn.chatVisibility;
      this.chatColours = packetIn.enableColors;
      this.getDataWatcher().updateObject(10, (byte)packetIn.modelPartFlags);
   }

   public void onUpdateEntity() {
      try {
         super.onUpdate();

         for (int i = 0; i < this.inventory.getSizeInventory(); i++) {
            ItemStack stackInSlot = this.inventory.getStackInSlot(i);
            if (stackInSlot != null && stackInSlot.getItem().isMap()) {
               Packet mapDataPacket = ((ItemMapBase)stackInSlot.getItem()).createMapDataPacket(stackInSlot, this.worldObj, this);
               if (mapDataPacket != null) {
                  this.playerNetServerHandler.sendPacket(mapDataPacket);
               }
            }
         }

         if (this.getHealth() != this.lastHealth
            || this.lastFoodLevel != this.foodStats.getFoodLevel()
            || this.foodStats.getSaturationLevel() == 0.0F != this.wasHungry) {
            this.playerNetServerHandler
               .sendPacket(new S06PacketUpdateHealth(this.getHealth(), this.foodStats.getFoodLevel(), this.foodStats.getSaturationLevel()));
            this.lastHealth = this.getHealth();
            this.lastFoodLevel = this.foodStats.getFoodLevel();
            this.wasHungry = this.foodStats.getSaturationLevel() == 0.0F;
         }

         if (this.getHealth() + this.getAbsorptionAmount() != this.combinedHealth) {
            this.combinedHealth = this.getHealth() + this.getAbsorptionAmount();

            for (ScoreObjective scoreObjective : this.getWorldScoreboard().getObjectivesFromCriteria(IScoreObjectiveCriteria.health)) {
               this.getWorldScoreboard().getValueFromObjective(this.getName(), scoreObjective).setScoreFromPlayers(Arrays.asList(this));
            }
         }

         if (this.experienceTotal != this.lastExperience) {
            this.lastExperience = this.experienceTotal;
            this.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(this.experience, this.experienceTotal, this.experienceLevel));
         }

         if (this.ticksExisted % 20 * 5 == 0 && !this.getStatFile().hasAchievementUnlocked(AchievementList.exploreAllBiomes)) {
            this.updateBiomesExplored();
         }
      } catch (Throwable throwable) {
         CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Ticking player");
         CrashReportCategory crashReportCategory = crashReport.makeCategory("Player being ticked");
         this.addEntityCrashInfo(crashReportCategory);
         throw new ReportedException(crashReport);
      }
   }

   @Override
   protected void onFinishedPotionEffect(PotionEffect effect) {
      super.onFinishedPotionEffect(effect);
      this.playerNetServerHandler.sendPacket(new S1EPacketRemoveEntityEffect(this.getEntityId(), effect));
   }

   private boolean canPlayersAttack() {
      return this.mcServer.isPVPEnabled();
   }

   public void updateHeldItem() {
      if (!this.isChangingQuantityOnly) {
         this.playerNetServerHandler.sendPacket(new S2FPacketSetSlot(-1, -1, this.inventory.getItemStack()));
      }
   }

   @Override
   public void trackStat(StatBase statIn) {
      if (statIn != null) {
         this.statsFile.unlockAchievement(this, statIn, 0);

         for (ScoreObjective scoreObjective : this.getWorldScoreboard().getObjectivesFromCriteria(statIn.getCriteria())) {
            this.getWorldScoreboard().getValueFromObjective(this.getName(), scoreObjective).setScorePoints(0);
         }

         if (this.statsFile.hasAchievementDataChanged()) {
            this.statsFile.sendStats(this);
         }
      }
   }

   public void setPlayerHealthUpdated() {
      this.lastHealth = -1.0E8F;
   }

   public void handleFalling(double d, boolean flag) {
      int i = MathHelper.floor_double(this.posX);
      int i2 = MathHelper.floor_double(this.posY - 0.2F);
      int i3 = MathHelper.floor_double(this.posZ);
      BlockPos blockPos = new BlockPos(i, i2, i3);
      Block block = this.worldObj.getBlockState(blockPos).getBlock();
      if (block.getMaterial() == Material.air) {
         Block block2 = this.worldObj.getBlockState(blockPos.down()).getBlock();
         if (block2 instanceof BlockFence || block2 instanceof BlockWall || block2 instanceof BlockFenceGate) {
            blockPos = blockPos.down();
            block = this.worldObj.getBlockState(blockPos).getBlock();
         }
      }

      super.updateFallState(d, flag, block, blockPos);
   }

   public EntityPlayer.EnumChatVisibility getChatVisibility() {
      return this.chatVisibility;
   }

   @Override
   public void addStat(StatBase stat, int amount) {
      if (stat != null) {
         this.statsFile.increaseStat(this, stat, amount);

         for (ScoreObjective scoreObjective : this.getWorldScoreboard().getObjectivesFromCriteria(stat.getCriteria())) {
            this.getWorldScoreboard().getValueFromObjective(this.getName(), scoreObjective).increseScore(amount);
         }

         if (this.statsFile.hasAchievementDataChanged()) {
            this.statsFile.sendStats(this);
         }
      }
   }

   public String getPlayerIP() {
      String text = this.playerNetServerHandler.netManager.getRemoteAddress().toString();
      text = text.substring(text.indexOf("/") + 1);
      return text.substring(0, text.indexOf(":"));
   }

   private void sendTileEntityUpdate(TileEntity tileEntity) {
      if (tileEntity != null) {
         Packet packet = tileEntity.getDescriptionPacket();
         if (packet != null) {
            this.playerNetServerHandler.sendPacket(packet);
         }
      }
   }

   @Override
   public void wakeUpPlayer(boolean immediately, boolean updateWorldFlag, boolean setSpawn) {
      if (this.isPlayerSleeping()) {
         this.getServerForPlayer().getEntityTracker().sendToAllTrackingEntityAndSelf(this, new S0BPacketAnimation(this, 2));
      }

      super.wakeUpPlayer(immediately, updateWorldFlag, setSpawn);
      if (this.playerNetServerHandler != null) {
         this.playerNetServerHandler.setPlayerLocation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
      }
   }

   @Override
   public void addChatComponentMessage(IChatComponent chatComponent) {
      this.playerNetServerHandler.sendPacket(new S02PacketChat(chatComponent));
   }

   @Override
   public void sendAllWindowProperties(Container container2, IInventory iInventory) {
      for (int index = 0; index < iInventory.getFieldCount(); index++) {
         this.playerNetServerHandler.sendPacket(new S31PacketWindowProperty(container2.windowId, index, iInventory.getField(index)));
      }
   }

   @Override
   public void onItemPickup(Entity entity2, int i) {
      super.onItemPickup(entity2, i);
      this.openContainer.detectAndSendChanges();
   }

   @Override
   public void mountEntity(Entity entityIn) {
      Entity entity = this.ridingEntity;
      super.mountEntity(entityIn);
      if (entityIn != entity) {
         this.playerNetServerHandler.sendPacket(new S1BPacketEntityAttach(0, this, this.ridingEntity));
         this.playerNetServerHandler.setPlayerLocation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
      }
   }

   @Override
   public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) {
      if (!(containerToSend.getSlot(slotInd) instanceof SlotCrafting) && !this.isChangingQuantityOnly) {
         this.playerNetServerHandler.sendPacket(new S2FPacketSetSlot(containerToSend.windowId, slotInd, stack));
      }
   }

   public IChatComponent getTabListDisplayName() {
      return null;
   }

   public void sendContainerToPlayer(Container container2) {
      this.updateCraftingInventory(container2, container2.getInventory());
   }

   @Override
   public void addExperienceLevel(int i) {
      super.addExperienceLevel(i);
      this.lastExperience = -1;
   }

   @Override
   public void onCriticalHit(Entity entity2) {
      this.getServerForPlayer().getEntityTracker().sendToAllTrackingEntityAndSelf(this, new S0BPacketAnimation(entity2, 4));
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      if ((!this.mcServer.isDedicatedServer() || !this.canPlayersAttack() || !"fall".equals(source.damageType))
         && this.respawnInvulnerabilityTicks > 0
         && source != DamageSource.outOfWorld) {
         return false;
      }

      if (source instanceof EntityDamageSource) {
         Entity entity2 = source.getEntity();
         if (entity2 instanceof EntityPlayer && !this.canAttackPlayer((EntityPlayer)entity2)) {
            return false;
         }

         if (entity2 instanceof EntityArrow entityArrow && entityArrow.shootingEntity instanceof EntityPlayer && !this.canAttackPlayer((EntityPlayer)entityArrow.shootingEntity)) {
            return false;
         }
      }

      return super.attackEntityFrom(source, amount);
   }

   @Override
   public void displayGui(IInteractionObject guiOwner) {
      this.getNextWindowId();
      this.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(this.currentWindowId, guiOwner.getGuiID(), guiOwner.getDisplayName()));
      this.openContainer = guiOwner.createContainer(this.inventory, this);
      this.openContainer.windowId = this.currentWindowId;
      this.openContainer.onCraftGuiOpened(this);
   }

   public void sendPlayerAbilities() {
      if (this.playerNetServerHandler != null) {
         this.playerNetServerHandler.sendPacket(new S39PacketPlayerAbilities(this.capabilities));
         this.updatePotionMetadata();
      }
   }

   @Override
   public void sendProgressBarUpdate(Container containerIn, int varToUpdate, int newValue) {
      this.playerNetServerHandler.sendPacket(new S31PacketWindowProperty(containerIn.windowId, varToUpdate, newValue));
   }

   @Override
   public EntityPlayer.EnumStatus trySleep(BlockPos bedLocation) {
      EntityPlayer.EnumStatus entityplayer$enumstatus = super.trySleep(bedLocation);
      if (entityplayer$enumstatus == EntityPlayer.EnumStatus.OK) {
         S0APacketUseBed s0APacketUseBed = new S0APacketUseBed(this, bedLocation);
         this.getServerForPlayer().getEntityTracker().sendToAllTrackingEntity(this, s0APacketUseBed);
         this.playerNetServerHandler.setPlayerLocation(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
         this.playerNetServerHandler.sendPacket(s0APacketUseBed);
      }

      return entityplayer$enumstatus;
   }

   @Override
   public void displayGUIChest(IInventory chestInventory) {
      if (this.openContainer != this.inventoryContainer) {
         this.closeScreen();
      }

      if (chestInventory instanceof ILockableContainer lockableContainer
         && lockableContainer.isLocked()
         && !this.canOpen(lockableContainer.getLockCode())
         && !this.isSpectator()) {
         this.playerNetServerHandler
            .sendPacket(new S02PacketChat(new ChatComponentTranslation("container.isLocked", chestInventory.getDisplayName()), (byte)2));
         this.playerNetServerHandler.sendPacket(new S29PacketSoundEffect("random.door_close", this.posX, this.posY, this.posZ, 1.0F, 1.0F));
      } else {
         this.getNextWindowId();
         if (chestInventory instanceof IInteractionObject) {
            this.playerNetServerHandler
               .sendPacket(
                  new S2DPacketOpenWindow(
                     this.currentWindowId, ((IInteractionObject)chestInventory).getGuiID(), chestInventory.getDisplayName(), chestInventory.getSizeInventory()
                  )
               );
            this.openContainer = ((IInteractionObject)chestInventory).createContainer(this.inventory, this);
         } else {
            this.playerNetServerHandler
               .sendPacket(
                  new S2DPacketOpenWindow(this.currentWindowId, "minecraft:container", chestInventory.getDisplayName(), chestInventory.getSizeInventory())
               );
            this.openContainer = new ContainerChest(this.inventory, chestInventory, this);
         }

         this.openContainer.windowId = this.currentWindowId;
         this.openContainer.onCraftGuiOpened(this);
      }
   }

   public void setSpectatingEntity(Entity entityToSpectate) {
      Entity entity = this.getSpectatingEntity();
      this.spectatingEntity = entityToSpectate == null ? this : entityToSpectate;
      if (entity != this.spectatingEntity) {
         this.playerNetServerHandler.sendPacket(new S43PacketCamera(this.spectatingEntity));
         this.setPositionAndUpdate(this.spectatingEntity.posX, this.spectatingEntity.posY, this.spectatingEntity.posZ);
      }
   }

   @Override
   public void sendEndCombat() {
      super.sendEndCombat();
      this.playerNetServerHandler.sendPacket(new S42PacketCombatEvent(this.getCombatTracker(), net.minecraft.network.play.server.S42PacketCombatEvent.Event.END_COMBAT));
   }

   @Override
   protected void onChangedPotionEffect(PotionEffect id, boolean flag) {
      super.onChangedPotionEffect(id, flag);
      this.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(this.getEntityId(), id));
   }

   @Override
   protected void updateFallState(double y, boolean onGroundIn, Block blockIn, BlockPos pos) {
   }

   @Override
   public void sendEnterCombat() {
      super.sendEnterCombat();
      this.playerNetServerHandler.sendPacket(new S42PacketCombatEvent(this.getCombatTracker(), net.minecraft.network.play.server.S42PacketCombatEvent.Event.ENTER_COMBAT));
   }

   @Override
   public void displayVillagerTradeGui(IMerchant villager) {
      this.getNextWindowId();
      this.openContainer = new ContainerMerchant(this.inventory, villager, this.worldObj);
      this.openContainer.windowId = this.currentWindowId;
      this.openContainer.onCraftGuiOpened(this);
      IInventory inventory = ((ContainerMerchant)this.openContainer).getMerchantInventory();
      IChatComponent chatComponent = villager.getDisplayName();
      this.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(this.currentWindowId, "minecraft:villager", chatComponent, inventory.getSizeInventory()));
      MerchantRecipeList merchantRecipeList = villager.getRecipes(this);
      if (merchantRecipeList != null) {
         PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
         packetBuffer.writeInt(this.currentWindowId);
         merchantRecipeList.writeToBuf(packetBuffer);
         this.playerNetServerHandler.sendPacket(new S3FPacketCustomPayload("MC|TrList", packetBuffer));
      }
   }

   @Override
   public boolean isSpectatedByPlayer(EntityPlayerMP player) {
      return player.isSpectator() ? this.getSpectatingEntity() == this : (this.isSpectator() ? false : super.isSpectatedByPlayer(player));
   }

   private void getNextWindowId() {
      this.currentWindowId = this.currentWindowId % 100 + 1;
   }

   @Override
   public void removeExperienceLevel(int i) {
      super.removeExperienceLevel(i);
      this.lastExperience = -1;
   }

   @Override
   public boolean canAttackPlayer(EntityPlayer other) {
      return !this.canPlayersAttack() ? false : super.canAttackPlayer(other);
   }

   public void mountEntityAndWakeUp() {
      if (this.riddenByEntity != null) {
         this.riddenByEntity.mountEntity(this);
      }

      if (this.sleeping) {
         this.wakeUpPlayer(true, false, false);
      }
   }

   @Override
   public void onEnchantmentCritical(Entity entity2) {
      this.getServerForPlayer().getEntityTracker().sendToAllTrackingEntityAndSelf(this, new S0BPacketAnimation(entity2, 5));
   }

   @Override
   public void displayGUIBook(ItemStack bookStack) {
      if (bookStack.getItem() == Items.written_book) {
         this.playerNetServerHandler.sendPacket(new S3FPacketCustomPayload("MC|BOpen", new PacketBuffer(Unpooled.buffer())));
      }
   }

   @Override
   public void clonePlayer(EntityPlayer oldPlayer, boolean respawnFromEnd) {
      super.clonePlayer(oldPlayer, respawnFromEnd);
      this.lastExperience = -1;
      this.lastHealth = -1.0F;
      this.lastFoodLevel = -1;
      this.destroyedItemsNetCache.addAll(((EntityPlayerMP)oldPlayer).destroyedItemsNetCache);
   }

   public void removeEntity(Entity entity2) {
      if (entity2 instanceof EntityPlayer) {
         this.playerNetServerHandler.sendPacket(new S13PacketDestroyEntities(entity2.getEntityId()));
      } else {
         this.destroyedItemsNetCache.add(entity2.getEntityId());
      }
   }

   @Override
   public void travelToDimension(int dimensionId) {
      if (this.dimension == 1 && dimensionId == 1) {
         this.triggerAchievement(AchievementList.theEnd2);
         this.worldObj.removeEntity(this);
         this.playerConqueredTheEnd = true;
         this.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(4, 0.0F));
      } else {
         if (this.dimension == 0 && dimensionId == 1) {
            this.triggerAchievement(AchievementList.theEnd);
            BlockPos blockPos = this.mcServer.worldServerForDimension(dimensionId).getSpawnCoordinate();
            if (blockPos != null) {
               this.playerNetServerHandler.setPlayerLocation(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 0.0F, 0.0F);
            }

            dimensionId = 1;
         } else {
            this.triggerAchievement(AchievementList.portal);
         }

         this.mcServer.getConfigurationManager().transferPlayerToDimension(this, dimensionId);
         this.lastExperience = -1;
         this.lastHealth = -1.0F;
         this.lastFoodLevel = -1;
      }
   }

   @Override
   public BlockPos getPosition() {
      return new BlockPos(this.posX, this.posY + 0.5, this.posZ);
   }

   @Override
   public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
      if ("seed".equals(commandName) && !this.mcServer.isDedicatedServer()) {
         return true;
      }

      if (!"tell".equals(commandName) && !"help".equals(commandName) && !"me".equals(commandName) && !"trigger".equals(commandName)) {
         if (this.mcServer.getConfigurationManager().canSendCommands(this.getGameProfile())) {
            UserListOpsEntry userListOpsEntry = this.mcServer.getConfigurationManager().getOppedPlayers().getEntry(this.getGameProfile());
            return userListOpsEntry != null ? userListOpsEntry.getPermissionLevel() >= permLevel : this.mcServer.getOpPermissionLevel() >= permLevel;
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   @Override
   public boolean isSpectator() {
      return this.theItemInWorldManager.getGameType() == net.minecraft.world.WorldSettings.GameType.SPECTATOR;
   }

   @Override
   protected void onNewPotionEffect(PotionEffect id) {
      super.onNewPotionEffect(id);
      this.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(this.getEntityId(), id));
   }

   protected void updateBiomesExplored() {
      String biomeName2 = this.worldObj.getBiomeGenForCoords(new BlockPos(MathHelper.floor_double(this.posX), 0, MathHelper.floor_double(this.posZ))).biomeName;
      JsonSerializableSet progress = this.getStatFile().getProgress(AchievementList.exploreAllBiomes);
      if (progress == null) {
         progress = this.getStatFile().setProgress(AchievementList.exploreAllBiomes, new JsonSerializableSet());
      }

      progress.add(biomeName2);
      if (this.getStatFile().canUnlockAchievement(AchievementList.exploreAllBiomes) && progress.size() >= BiomeGenBase.explorationBiomesList.size()) {
         HashSet hashSet = Sets.newHashSet(BiomeGenBase.explorationBiomesList);

         for (String text : progress) {
            Iterator iterator2 = hashSet.iterator();

            while (iterator2.hasNext()) {
               if (((BiomeGenBase)iterator2.next()).biomeName.equals(text)) {
                  iterator2.remove();
               }
            }

            if (hashSet.isEmpty()) {
               break;
            }
         }

         if (hashSet.isEmpty()) {
            this.triggerAchievement(AchievementList.exploreAllBiomes);
         }
      }
   }

   @Override
   public void closeScreen() {
      this.playerNetServerHandler.sendPacket(new S2EPacketCloseWindow(this.openContainer.windowId));
      this.closeContainer();
   }

   public WorldServer getServerForPlayer() {
      return (WorldServer)this.worldObj;
   }

   @Override
   public void setPositionAndUpdate(double x, double y, double z) {
      this.playerNetServerHandler.setPlayerLocation(x, y, z, this.rotationYaw, this.rotationPitch);
   }

   public EntityPlayerMP(MinecraftServer server, WorldServer worldIn, GameProfile profile, ItemInWorldManager interactionManager) {
      super(worldIn, profile);
      interactionManager.thisPlayerMP = this;
      this.theItemInWorldManager = interactionManager;
      BlockPos blockPos = worldIn.getSpawnPoint();
      if (!worldIn.provider.getHasNoSky() && worldIn.getWorldInfo().getGameType() != net.minecraft.world.WorldSettings.GameType.ADVENTURE) {
         int i = Math.max(5, server.getSpawnProtectionSize() - 6);
         int xCoordinate = MathHelper.floor_double(worldIn.getWorldBorder().getClosestDistance(blockPos.getX(), blockPos.getZ()));
         if (xCoordinate < i) {
            i = xCoordinate;
         }

         if (xCoordinate <= 1) {
            i = 1;
         }

         blockPos = worldIn.getTopSolidOrLiquidBlock(blockPos.add(this.rand.nextInt(i * 2) - i, 0, this.rand.nextInt(i * 2) - i));
      }

      this.mcServer = server;
      this.statsFile = server.getConfigurationManager().getPlayerStatsFile(this);
      this.stepHeight = 0.0F;
      this.moveToBlockPosAndAngles(blockPos, 0.0F, 0.0F);

      while (!worldIn.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty() && this.posY < 255.0) {
         this.setPosition(this.posX, this.posY + 1.0, this.posZ);
      }
   }

   public void loadResourcePack(String url, String hash) {
      this.playerNetServerHandler.sendPacket(new S48PacketResourcePackSend(url, hash));
   }

   public void markPlayerActive() {
      this.playerLastActiveTime = MinecraftServer.getCurrentTimeMillis();
   }

   @Override
   public void openEditSign(TileEntitySign signTile) {
      signTile.setPlayer(this);
      this.playerNetServerHandler.sendPacket(new S36PacketSignEditorOpen(signTile.getPos()));
   }

   @Override
   public void displayGUIHorse(EntityHorse horse, IInventory horseInventory) {
      if (this.openContainer != this.inventoryContainer) {
         this.closeScreen();
      }

      this.getNextWindowId();
      this.playerNetServerHandler
         .sendPacket(
            new S2DPacketOpenWindow(
               this.currentWindowId, "EntityHorse", horseInventory.getDisplayName(), horseInventory.getSizeInventory(), horse.getEntityId()
            )
         );
      this.openContainer = new ContainerHorseInventory(this.inventory, horseInventory, horse, this);
      this.openContainer.windowId = this.currentWindowId;
      this.openContainer.onCraftGuiOpened(this);
   }

   @Override
   public void addChatMessage(IChatComponent component) {
      this.playerNetServerHandler.sendPacket(new S02PacketChat(component));
   }

   public Entity getSpectatingEntity() {
      return this.spectatingEntity == null ? this : this.spectatingEntity;
   }

   @Override
   public void updateCraftingInventory(Container containerToSend, List<ItemStack> itemsList) {
      this.playerNetServerHandler.sendPacket(new S30PacketWindowItems(containerToSend.windowId, itemsList));
      this.playerNetServerHandler.sendPacket(new S2FPacketSetSlot(-1, -1, this.inventory.getItemStack()));
   }

   public void addSelfToInternalCraftingInventory() {
      this.openContainer.onCraftGuiOpened(this);
   }

   public StatisticsFile getStatFile() {
      return this.statsFile;
   }
}
