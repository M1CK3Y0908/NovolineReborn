package net.minecraft.client.network;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.GuardianSound;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.GuiScreenDemo;
import net.minecraft.client.gui.GuiScreenRealmsProxy;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityPickupFX;
import net.minecraft.client.player.inventory.ContainerLocalMenu;
import net.minecraft.client.player.inventory.LocalBlockIntercommunication;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.stream.MetadataAchievement;
import net.minecraft.client.stream.MetadataCombat;
import net.minecraft.client.stream.MetadataPlayerDeath;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.NpcMerchant;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.effect.EntityLightningBolt;
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
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Items;
import net.minecraft.inventory.AnimalChest;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S10PacketSpawnPainting;
import net.minecraft.network.play.server.S11PacketSpawnExperienceOrb;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.network.play.server.S31PacketWindowProperty;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S33PacketUpdateSign;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.network.play.server.S36PacketSignEditorOpen;
import net.minecraft.network.play.server.S37PacketStatistics;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.network.play.server.S41PacketServerDifficulty;
import net.minecraft.network.play.server.S42PacketCombatEvent;
import net.minecraft.network.play.server.S43PacketCamera;
import net.minecraft.network.play.server.S44PacketWorldBorder;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.network.play.server.S46PacketSetCompressionLevel;
import net.minecraft.network.play.server.S47PacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import net.minecraft.network.play.server.S49PacketUpdateEntityNBT;
import net.minecraft.potion.PotionEffect;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StringUtils;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.Explosion;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wtf.novoline.gui.NovolineMainMenu;

public class NetHandlerPlayClient implements INetHandlerPlayClient {
   public final Map<UUID, NetworkPlayerInfo> playerInfoMap = Maps.newHashMap();
   public int currentServerMaxPlayers = 20;
   private boolean hasShownAchievementHint = false;
   private final Random avRandomizer = new Random();
   private Minecraft gameController;
   private final GuiScreen guiScreenServer;
   private final NetworkManager netManager;
   public boolean doneLoadingTerrain;
   private final GameProfile profile;
   private WorldClient clientWorldController;
   private static final Logger logger = LogManager.getLogger();

   @Override
   public void handleBlockBreakAnim(S25PacketBlockBreakAnim packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      this.gameController.theWorld.sendBlockBreakProgress(packetIn.getBreakerId(), packetIn.getPosition(), packetIn.getProgress());
   }

   @Override
   public void handleJoinGame(S01PacketJoinGame packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      this.gameController.playerController = new PlayerControllerMP(this.gameController, this);
      this.clientWorldController = new WorldClient(
         this,
         new WorldSettings(0L, packetIn.getGameType(), false, packetIn.isHardcoreMode(), packetIn.getWorldType()),
         packetIn.getDimension(),
         packetIn.getDifficulty(),
         this.gameController.mcProfiler
      );
      this.gameController.gameSettings.difficulty = packetIn.getDifficulty();
      this.gameController.loadWorld(this.clientWorldController);
      this.gameController.thePlayer.dimension = packetIn.getDimension();
      this.gameController.displayGuiScreen(new GuiDownloadTerrain(this));
      this.gameController.thePlayer.setEntityId(packetIn.getEntityId());
      this.currentServerMaxPlayers = packetIn.getMaxPlayers();
      this.gameController.thePlayer.setReducedDebug(packetIn.isReducedDebugInfo());
      this.gameController.playerController.setGameType(packetIn.getGameType());
      this.gameController.gameSettings.sendSettingsToServer();
      this.netManager
         .sendPacket(new C17PacketCustomPayload("MC|Brand", new PacketBuffer(Unpooled.buffer()).writeString(ClientBrandRetriever.getClientModName())));
   }

   @Override
   public void handleSetCompressionLevel(S46PacketSetCompressionLevel packetIn) {
      if (!this.netManager.isLocalChannel()) {
         this.netManager.setCompressionTreshold(packetIn.getThreshold());
      }
   }

   @Override
   public void handleEntityAttach(S1BPacketEntityAttach packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityId());
      Entity entity2 = this.clientWorldController.getEntityByID(packetIn.getVehicleEntityId());
      if (packetIn.getLeash() == 0) {
         boolean conditionMet = false;
         if (packetIn.getEntityId() == this.gameController.thePlayer.getEntityId()) {
            entity = this.gameController.thePlayer;
            if (entity2 instanceof EntityBoat) {
               ((EntityBoat)entity2).setIsBoatEmpty(false);
            }

            conditionMet = entity.ridingEntity == null && entity2 != null;
         } else if (entity2 instanceof EntityBoat) {
            ((EntityBoat)entity2).setIsBoatEmpty(true);
         }

         if (entity == null) {
            return;
         }

         entity.mountEntity(entity2);
         if (conditionMet) {
            GameSettings gameSettings = this.gameController.gameSettings;
            this.gameController
               .ingameGUI
               .setRecordPlaying(I18n.format("mount.onboard", GameSettings.getKeyDisplayString(gameSettings.keyBindSneak.getKeyCode())), false);
         }
      } else if (packetIn.getLeash() == 1 && entity instanceof EntityLiving) {
         if (entity2 != null) {
            ((EntityLiving)entity).setLeashedToEntity(entity2, false);
         } else {
            ((EntityLiving)entity).clearLeashed(false, false);
         }
      }
   }

   @Override
   public void handleWorldBorder(S44PacketWorldBorder packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      packetIn.applyTo(this.clientWorldController.getWorldBorder());
   }

   @Override
   public void handleBlockAction(S24PacketBlockAction packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      this.gameController.theWorld.addBlockEvent(packetIn.getBlockPosition(), packetIn.getBlockType(), packetIn.getData1(), packetIn.getData2());
   }

   @Override
   public void handleSpawnPainting(S10PacketSpawnPainting packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      EntityPainting entityPainting = new EntityPainting(this.clientWorldController, packetIn.getPosition(), packetIn.getFacing(), packetIn.getTitle());
      this.clientWorldController.addEntityToWorld(packetIn.getEntityID(), entityPainting);
   }

   @Override
   public void handleChangeGameState(S2BPacketChangeGameState packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      EntityPlayer entityPlayer = this.gameController.thePlayer;
      int gameState = packetIn.getGameState();
      float value = packetIn.getValue();
      int i = MathHelper.floor_float(value + 0.5F);
      if (gameState >= 0 && gameState < S2BPacketChangeGameState.MESSAGE_NAMES.length && S2BPacketChangeGameState.MESSAGE_NAMES[gameState] != null) {
         entityPlayer.addChatComponentMessage(new ChatComponentTranslation(S2BPacketChangeGameState.MESSAGE_NAMES[gameState]));
      }

      if (gameState == 1) {
         this.clientWorldController.getWorldInfo().setRaining(true);
         this.clientWorldController.setRainStrength(0.0F);
      } else if (gameState == 2) {
         this.clientWorldController.getWorldInfo().setRaining(false);
         this.clientWorldController.setRainStrength(1.0F);
      } else if (gameState == 3) {
         this.gameController.playerController.setGameType(net.minecraft.world.WorldSettings.GameType.getByID(i));
      } else if (gameState == 4) {
         this.gameController.displayGuiScreen(new GuiWinGame());
      } else if (gameState == 5) {
         GameSettings settings = this.gameController.gameSettings;
         if (value == 0.0F) {
            this.gameController.displayGuiScreen(new GuiScreenDemo());
         } else if (value == 101.0F) {
            this.gameController
               .ingameGUI
               .getChatGUI()
               .printChatMessage(
                  new ChatComponentTranslation(
                     "demo.help.movement",
                     GameSettings.getKeyDisplayString(settings.keyBindForward.getKeyCode()),
                     GameSettings.getKeyDisplayString(settings.keyBindLeft.getKeyCode()),
                     GameSettings.getKeyDisplayString(settings.keyBindBack.getKeyCode()),
                     GameSettings.getKeyDisplayString(settings.keyBindRight.getKeyCode())
                  )
               );
         } else if (value == 102.0F) {
            this.gameController
               .ingameGUI
               .getChatGUI()
               .printChatMessage(new ChatComponentTranslation("demo.help.jump", GameSettings.getKeyDisplayString(settings.keyBindJump.getKeyCode())));
         } else if (value == 103.0F) {
            this.gameController
               .ingameGUI
               .getChatGUI()
               .printChatMessage(new ChatComponentTranslation("demo.help.inventory", GameSettings.getKeyDisplayString(settings.keyBindInventory.getKeyCode())));
         }
      } else if (gameState == 6) {
         this.clientWorldController
            .playSound(entityPlayer.posX, entityPlayer.posY + entityPlayer.getEyeHeight(), entityPlayer.posZ, "random.successful_hit", 0.18F, 0.45F, false);
      } else if (gameState == 7) {
         this.clientWorldController.setRainStrength(value);
      } else if (gameState == 8) {
         this.clientWorldController.setThunderStrength(value);
      } else if (gameState == 10) {
         this.clientWorldController.spawnParticle(EnumParticleTypes.MOB_APPEARANCE, entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, 0.0, 0.0, 0.0);
         this.clientWorldController.playSound(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, "mob.guardian.curse", 1.0F, 1.0F, false);
      }
   }

   @Override
   public void handleSignEditorOpen(S36PacketSignEditorOpen packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      TileEntity tileEntity = this.clientWorldController.getTileEntity(packetIn.getSignPosition());
      if (!(tileEntity instanceof TileEntitySign)) {
         tileEntity = new TileEntitySign();
         tileEntity.setWorldObj(this.clientWorldController);
         tileEntity.setPos(packetIn.getSignPosition());
      }

      this.gameController.thePlayer.openEditSign((TileEntitySign)tileEntity);
   }

   public NetworkPlayerInfo getPlayerInfo(UUID uuid) {
      return this.playerInfoMap.get(uuid);
   }

   public Collection<NetworkPlayerInfo> getPlayerInfoMap() {
      return this.playerInfoMap.values();
   }

   public void cleanup() {
      this.clientWorldController = null;
   }

   @Override
   public void handlePlayerAbilities(S39PacketPlayerAbilities packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      EntityPlayer entityPlayer = this.gameController.thePlayer;
      entityPlayer.capabilities.isFlying = packetIn.isFlying();
      entityPlayer.capabilities.isCreativeMode = packetIn.isCreativeMode();
      entityPlayer.capabilities.disableDamage = packetIn.isInvulnerable();
      entityPlayer.capabilities.allowFlying = packetIn.isAllowFlying();
      entityPlayer.capabilities.setFlySpeed(packetIn.getFlySpeed());
      entityPlayer.capabilities.setPlayerWalkSpeed(packetIn.getWalkSpeed());
   }

   @Override
   public void handleParticles(S2APacketParticles packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      if (packetIn.getParticleCount() == 0) {
         double d = packetIn.getParticleSpeed() * packetIn.getXOffset();
         double d2 = packetIn.getParticleSpeed() * packetIn.getYOffset();
         double d3 = packetIn.getParticleSpeed() * packetIn.getZOffset();

         try {
            this.clientWorldController
               .spawnParticle(
                  packetIn.getParticleType(),
                  packetIn.isLongDistance(),
                  packetIn.getXCoordinate(),
                  packetIn.getYCoordinate(),
                  packetIn.getZCoordinate(),
                  d,
                  d2,
                  d3,
                  packetIn.getParticleArgs()
               );
         } catch (Throwable throwable) {
            logger.warn("Could not spawn particle effect " + packetIn.getParticleType());
         }
      } else {
         for (int i = 0; i < packetIn.getParticleCount(); i++) {
            double d4 = this.avRandomizer.nextGaussian() * packetIn.getXOffset();
            double d5 = this.avRandomizer.nextGaussian() * packetIn.getYOffset();
            double d6 = this.avRandomizer.nextGaussian() * packetIn.getZOffset();
            double d7 = this.avRandomizer.nextGaussian() * packetIn.getParticleSpeed();
            double d8 = this.avRandomizer.nextGaussian() * packetIn.getParticleSpeed();
            double d9 = this.avRandomizer.nextGaussian() * packetIn.getParticleSpeed();

            try {
               this.clientWorldController
                  .spawnParticle(
                     packetIn.getParticleType(),
                     packetIn.isLongDistance(),
                     packetIn.getXCoordinate() + d4,
                     packetIn.getYCoordinate() + d5,
                     packetIn.getZCoordinate() + d6,
                     d7,
                     d8,
                     d9,
                     packetIn.getParticleArgs()
                  );
            } catch (Throwable throwable2) {
               logger.warn("Could not spawn particle effect " + packetIn.getParticleType());
               return;
            }
         }
      }
   }

   @Override
   public void handleEntityStatus(S19PacketEntityStatus packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      Entity entity = packetIn.getEntity(this.clientWorldController);
      if (entity != null) {
         if (packetIn.getOpCode() == 21) {
            this.gameController.getSoundHandler().playSound(new GuardianSound((EntityGuardian)entity));
         } else {
            entity.handleStatusUpdate(packetIn.getOpCode());
         }
      }
   }

   @Override
   public void handleCamera(S43PacketCamera packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      Entity entity = packetIn.getEntity(this.clientWorldController);
      if (entity != null) {
         this.gameController.setRenderViewEntity(entity);
      }
   }

   @Override
   public void handleServerDifficulty(S41PacketServerDifficulty packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      this.gameController.theWorld.getWorldInfo().setDifficulty(packetIn.getDifficulty());
      this.gameController.theWorld.getWorldInfo().setDifficultyLocked(packetIn.isDifficultyLocked());
   }

   @Override
   public void handleCustomPayload(S3FPacketCustomPayload packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      if ("MC|TrList".equals(packetIn.getChannelName())) {
         PacketBuffer bufferData = packetIn.getBufferData();

         try {
            int intValue = bufferData.readInt();
            GuiScreen currentScreen2 = this.gameController.currentScreen;
            if (currentScreen2 != null && currentScreen2 instanceof GuiMerchant && intValue == this.gameController.thePlayer.openContainer.windowId) {
               IMerchant merchant = ((GuiMerchant)currentScreen2).getMerchant();
               MerchantRecipeList fromBuf = MerchantRecipeList.readFromBuf(bufferData);
               merchant.setRecipes(fromBuf);
            }
         } catch (IOException ioException) {
            logger.error("Couldn't load trade info", ioException);
         } finally {
            bufferData.release();
         }
      } else if ("MC|Brand".equals(packetIn.getChannelName())) {
         this.gameController.thePlayer.setClientBrand(packetIn.getBufferData().readStringFromBuffer(32767));
      } else if ("MC|BOpen".equals(packetIn.getChannelName())) {
         ItemStack currentEquippedItem = this.gameController.thePlayer.getCurrentEquippedItem();
         if (currentEquippedItem != null && currentEquippedItem.getItem() == Items.written_book) {
            this.gameController.displayGuiScreen(new GuiScreenBook(this.gameController.thePlayer, currentEquippedItem, false));
         }
      }
   }

   @Override
   public void handleCloseWindow(S2EPacketCloseWindow packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      this.gameController.thePlayer.closeScreenAndDropStack();
   }

   @Override
   public void handleUpdateScore(S3CPacketUpdateScore packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      Scoreboard scoreboard2 = this.clientWorldController.getScoreboard();
      ScoreObjective objective = scoreboard2.getObjective(packetIn.getObjectiveName());
      if (packetIn.getScoreAction() == net.minecraft.network.play.server.S3CPacketUpdateScore.Action.CHANGE) {
         scoreboard2.getValueFromObjective(packetIn.getPlayerName(), objective).setScorePoints(packetIn.getScoreValue());
      } else if (packetIn.getScoreAction() == net.minecraft.network.play.server.S3CPacketUpdateScore.Action.REMOVE) {
         if (StringUtils.isNullOrEmpty(packetIn.getObjectiveName())) {
            scoreboard2.removeObjectiveFromEntity(packetIn.getPlayerName(), (ScoreObjective)null);
         } else if (objective != null) {
            scoreboard2.removeObjectiveFromEntity(packetIn.getPlayerName(), objective);
         }
      }
   }

   @Override
   public void handlePlayerListHeaderFooter(S47PacketPlayerListHeaderFooter packetIn) {
      this.gameController.ingameGUI.getTabList().setHeader(packetIn.getHeader().getFormattedText().length() == 0 ? null : packetIn.getHeader());
      this.gameController.ingameGUI.getTabList().setFooter(packetIn.getFooter().getFormattedText().length() == 0 ? null : packetIn.getFooter());
   }

   public NetworkManager getNetworkManager() {
      return this.netManager;
   }

   @Override
   public void handleEntityMovement(S14PacketEntity packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      Entity entity = packetIn.getEntity(this.clientWorldController);
      if (entity != null) {
         entity.serverPosX = entity.serverPosX + packetIn.getPosX();
         entity.serverPosY = entity.serverPosY + packetIn.getPosY();
         entity.serverPosZ = entity.serverPosZ + packetIn.getPosZ();
         double d = entity.serverPosX / 32.0;
         double d2 = entity.serverPosY / 32.0;
         double d3 = entity.serverPosZ / 32.0;
         float f = packetIn.hasRotation() ? packetIn.getYaw() * 360 / 256.0F : entity.rotationYaw;
         float f2 = packetIn.hasRotation() ? packetIn.getPitch() * 360 / 256.0F : entity.rotationPitch;
         entity.handleDouble(d, d2, d3, f, f2, false);
         entity.onGround = packetIn.getOnGround();
      }
   }

   @Override
   public void handleChunkData(S21PacketChunkData packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      if (packetIn.isGroundUp()) {
         if (packetIn.getExtractedSize() == 0) {
            this.clientWorldController.doPreChunk(packetIn.getChunkX(), packetIn.getChunkZ(), false);
            return;
         }

         this.clientWorldController.doPreChunk(packetIn.getChunkX(), packetIn.getChunkZ(), true);
      }

      this.clientWorldController.noop(packetIn.getChunkX() << 4, packetIn.getChunkZ() << 4, (packetIn.getChunkX() << 4) + 15, (packetIn.getChunkZ() << 4) + 15);
      Chunk chunkFromChunkCoords = this.clientWorldController.getChunkFromChunkCoords(packetIn.getChunkX(), packetIn.getChunkZ());
      chunkFromChunkCoords.fillChunk(packetIn.getExtractedDataBytes(), packetIn.getExtractedSize(), packetIn.isGroundUp());
      this.clientWorldController
         .markBlockRangeForRenderUpdate(
            packetIn.getChunkX() << 4, 0, packetIn.getChunkZ() << 4, (packetIn.getChunkX() << 4) + 15, 256, (packetIn.getChunkZ() << 4) + 15
         );
      if (!packetIn.isGroundUp() || !(this.clientWorldController.provider instanceof WorldProviderSurface)) {
         chunkFromChunkCoords.resetRelightChecks();
      }
   }

   @Override
   public void handleStatistics(S37PacketStatistics packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      boolean flag = false;

      for (Entry entry : packetIn.getStats().entrySet()) {
         StatBase statBase = (StatBase)entry.getKey();
         int i = (Integer)entry.getValue();
         if (statBase.isAchievement() && i > 0) {
            if (this.hasShownAchievementHint && this.gameController.thePlayer.getStatFileWriter().readStat(statBase) == 0) {
               Achievement achievement = (Achievement)statBase;
               this.gameController.guiAchievement.displayAchievement(achievement);
               this.gameController.getIStream().handleMetadata(new MetadataAchievement(achievement));
               if (statBase == AchievementList.openInventory) {
                  this.gameController.gameSettings.showInventoryAchievementHint = false;
                  this.gameController.gameSettings.saveOptions();
               }
            }

            flag = true;
         }

         this.gameController.thePlayer.getStatFileWriter().unlockAchievement(this.gameController.thePlayer, statBase, i);
      }

      if (!this.hasShownAchievementHint && !flag && this.gameController.gameSettings.showInventoryAchievementHint) {
         this.gameController.guiAchievement.displayUnformattedAchievement(AchievementList.openInventory);
      }

      this.hasShownAchievementHint = true;
      if (this.gameController.currentScreen instanceof IProgressMeter) {
         ((IProgressMeter)this.gameController.currentScreen).doneLoading();
      }
   }

   @Override
   public void handleEntityVelocity(S12PacketEntityVelocity packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getEntityID());
      if (entityByID != null) {
         entityByID.setVelocity(packetIn.getMotionX() / 8000.0, packetIn.getMotionY() / 8000.0, packetIn.getMotionZ() / 8000.0);
      }
   }

   @Override
   public void handleOpenWindow(S2DPacketOpenWindow packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      EntityPlayerSP entityPlayerSP = this.gameController.thePlayer;
      if ("minecraft:container".equals(packetIn.getGuiId())) {
         entityPlayerSP.displayGUIChest(new InventoryBasic(packetIn.getWindowTitle(), packetIn.getSlotCount()));
         entityPlayerSP.openContainer.windowId = packetIn.getWindowId();
      } else if ("minecraft:villager".equals(packetIn.getGuiId())) {
         entityPlayerSP.displayVillagerTradeGui(new NpcMerchant(entityPlayerSP, packetIn.getWindowTitle()));
         entityPlayerSP.openContainer.windowId = packetIn.getWindowId();
      } else if ("EntityHorse".equals(packetIn.getGuiId())) {
         Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityId());
         if (entity instanceof EntityHorse) {
            entityPlayerSP.displayGUIHorse((EntityHorse)entity, new AnimalChest(packetIn.getWindowTitle(), packetIn.getSlotCount()));
            entityPlayerSP.openContainer.windowId = packetIn.getWindowId();
         }
      } else if (!packetIn.hasSlots()) {
         entityPlayerSP.displayGui(new LocalBlockIntercommunication(packetIn.getGuiId(), packetIn.getWindowTitle()));
         entityPlayerSP.openContainer.windowId = packetIn.getWindowId();
      } else {
         ContainerLocalMenu containerLocalMenu = new ContainerLocalMenu(packetIn.getGuiId(), packetIn.getWindowTitle(), packetIn.getSlotCount());
         entityPlayerSP.displayGUIChest(containerLocalMenu);
         entityPlayerSP.openContainer.windowId = packetIn.getWindowId();
      }
   }

   @Override
   public void handleEntityEffect(S1DPacketEntityEffect packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityId());
      if (entity instanceof EntityLivingBase) {
         PotionEffect potionEffect = new PotionEffect(packetIn.getEffectId(), packetIn.getDuration(), packetIn.getAmplifier(), false, packetIn.isHideParticles());
         potionEffect.setPotionDurationMax(packetIn.isBadEffect());
         ((EntityLivingBase)entity).addPotionEffect(potionEffect);
      }
   }

   @Override
   public void handleDisconnect(S40PacketDisconnect packetIn) {
      this.netManager.closeChannel(packetIn.getReason());
   }

   public NetHandlerPlayClient(Minecraft mcIn, GuiScreen guiScreen, NetworkManager networkManager, GameProfile gameProfile) {
      this.gameController = mcIn;
      this.guiScreenServer = guiScreen;
      this.netManager = networkManager;
      this.profile = gameProfile;
   }

   @Override
   public void handleSpawnPosition(S05PacketSpawnPosition packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      this.gameController.thePlayer.setSpawnPoint(packetIn.getSpawnPos(), true);
      this.gameController.theWorld.getWorldInfo().setSpawn(packetIn.getSpawnPos());
   }

   @Override
   public void handleMaps(S34PacketMaps packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      MapData mapData = ItemMap.loadMapData(packetIn.getMapId(), this.gameController.theWorld);
      packetIn.setMapdataTo(mapData);
      this.gameController.entityRenderer.getMapItemRenderer().updateMapTexture(mapData);
   }

   @Override
   public void handleSpawnMob(S0FPacketSpawnMob packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      double xCoordinate = packetIn.getX() / 32.0;
      double yCoordinate = packetIn.getY() / 32.0;
      double zCoordinate = packetIn.getZ() / 32.0;
      float f = packetIn.getYaw() * 360 / 256.0F;
      float f2 = packetIn.getPitch() * 360 / 256.0F;
      EntityLivingBase entityLivingBase = (EntityLivingBase)EntityList.createEntityByID(packetIn.getEntityType(), this.gameController.theWorld);
      entityLivingBase.serverPosX = packetIn.getX();
      entityLivingBase.serverPosY = packetIn.getY();
      entityLivingBase.serverPosZ = packetIn.getZ();
      entityLivingBase.renderYawOffset = entityLivingBase.rotationYawHead = packetIn.getHeadPitch() * 360 / 256.0F;
      Entity[] aentity = entityLivingBase.getParts();
      if (aentity != null) {
         int i = packetIn.getEntityID() - entityLivingBase.getEntityId();

         for (int innerIndex = 0; innerIndex < aentity.length; innerIndex++) {
            aentity[innerIndex].setEntityId(aentity[innerIndex].getEntityId() + i);
         }
      }

      entityLivingBase.setEntityId(packetIn.getEntityID());
      entityLivingBase.setPositionAndRotation(xCoordinate, yCoordinate, zCoordinate, f, f2);
      entityLivingBase.motionX = packetIn.getVelocityX() / 8000.0F;
      entityLivingBase.motionY = packetIn.getVelocityY() / 8000.0F;
      entityLivingBase.motionZ = packetIn.getVelocityZ() / 8000.0F;
      this.clientWorldController.addEntityToWorld(packetIn.getEntityID(), entityLivingBase);
      List<net.minecraft.entity.DataWatcher.WatchableObject> list = packetIn.getWatcher();
      if (list != null) {
         entityLivingBase.getDataWatcher().updateWatchedObjectsFromList(list);
      }
   }

   @Override
   public void handlePlayerListItem(S38PacketPlayerListItem packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);

      for (net.minecraft.network.play.server.S38PacketPlayerListItem.AddPlayerData addPlayerData : packetIn.getEntries()) {
         if (packetIn.getAction() == net.minecraft.network.play.server.S38PacketPlayerListItem.Action.REMOVE_PLAYER) {
            this.playerInfoMap.remove(addPlayerData.getProfile().getId());
         } else {
            NetworkPlayerInfo networkPlayerInfo = this.playerInfoMap.get(addPlayerData.getProfile().getId());
            if (packetIn.getAction() == net.minecraft.network.play.server.S38PacketPlayerListItem.Action.ADD_PLAYER) {
               networkPlayerInfo = new NetworkPlayerInfo(addPlayerData);
               this.playerInfoMap.put(networkPlayerInfo.getGameProfile().getId(), networkPlayerInfo);
            }

            if (networkPlayerInfo != null) {
               switch (packetIn.getAction()) {
                  case ADD_PLAYER:
                     networkPlayerInfo.setGameType(addPlayerData.getGameMode());
                     networkPlayerInfo.setResponseTime(addPlayerData.getPing());
                     break;
                  case UPDATE_GAME_MODE:
                     networkPlayerInfo.setGameType(addPlayerData.getGameMode());
                     break;
                  case UPDATE_LATENCY:
                     networkPlayerInfo.setResponseTime(addPlayerData.getPing());
                     break;
                  case UPDATE_DISPLAY_NAME:
                     networkPlayerInfo.setDisplayName(addPlayerData.getDisplayName());
               }
            }
         }
      }
   }

   @Override
   public void handleScoreboardObjective(S3BPacketScoreboardObjective packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      Scoreboard scoreboard = this.clientWorldController.getScoreboard();
      if (packetIn.getMode() == 0) {
         ScoreObjective scoreObjective = scoreboard.addScoreObjective(packetIn.getObjectiveName(), IScoreObjectiveCriteria.DUMMY);
         scoreObjective.setDisplayName(packetIn.getObjectiveValue());
         scoreObjective.setRenderType(packetIn.getType());
      } else {
         ScoreObjective scoreObjective2 = scoreboard.getObjective(packetIn.getObjectiveName());
         if (packetIn.getMode() == 1) {
            scoreboard.removeObjective(scoreObjective2);
         } else if (packetIn.getMode() == 2) {
            scoreObjective2.setDisplayName(packetIn.getObjectiveValue());
            scoreObjective2.setRenderType(packetIn.getType());
         }
      }
   }

   @Override
   public void handleEffect(S28PacketEffect packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      if (packetIn.isSoundServerwide()) {
         this.gameController.theWorld.playBroadcastSound(packetIn.getSoundType(), packetIn.getSoundPos(), packetIn.getSoundData());
      } else {
         this.gameController.theWorld.playAuxSFX(packetIn.getSoundType(), packetIn.getSoundPos(), packetIn.getSoundData());
      }
   }

   @Override
   public void handleHeldItemChange(S09PacketHeldItemChange packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      if (packetIn.getHeldItemHotbarIndex() >= 0 && packetIn.getHeldItemHotbarIndex() < InventoryPlayer.getHotbarSize()) {
         this.gameController.thePlayer.inventory.currentItem = packetIn.getHeldItemHotbarIndex();
      }
   }

   @Override
   public void handleChat(S02PacketChat packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      if (packetIn.getType() == 2) {
         this.gameController.ingameGUI.handleIChatComponent(packetIn.getChatComponent());
      } else {
         this.gameController.ingameGUI.getChatGUI().printChatMessage(packetIn.getChatComponent());
      }
   }

   public GameProfile getGameProfile() {
      return this.profile;
   }

   @Override
   public void handleSpawnGlobalEntity(S2CPacketSpawnGlobalEntity packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      double xCoordinate = packetIn.getX() / 32.0;
      double yCoordinate = packetIn.getY() / 32.0;
      double zCoordinate = packetIn.getZ() / 32.0;
      EntityLightningBolt entityLightningBolt = null;
      if (packetIn.getType() == 1) {
         entityLightningBolt = new EntityLightningBolt(this.clientWorldController, xCoordinate, yCoordinate, zCoordinate);
      }

      if (entityLightningBolt != null) {
         entityLightningBolt.serverPosX = packetIn.getX();
         entityLightningBolt.serverPosY = packetIn.getY();
         entityLightningBolt.serverPosZ = packetIn.getZ();
         entityLightningBolt.rotationYaw = 0.0F;
         entityLightningBolt.rotationPitch = 0.0F;
         entityLightningBolt.setEntityId(packetIn.getEntityId());
         this.clientWorldController.addWeatherEffect(entityLightningBolt);
      }
   }

   @Override
   public void handleUpdateHealth(S06PacketUpdateHealth packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      this.gameController.thePlayer.setPlayerSPHealth(packetIn.getHealth());
      this.gameController.thePlayer.getFoodStats().setFoodLevel(packetIn.getFoodLevel());
      this.gameController.thePlayer.getFoodStats().setFoodSaturationLevel(packetIn.getSaturationLevel());
   }

   @Override
   public void handleSpawnObject(S0EPacketSpawnObject packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      double xCoordinate = packetIn.getX() / 32.0;
      double yCoordinate = packetIn.getY() / 32.0;
      double zCoordinate = packetIn.getZ() / 32.0;
      Entity entity = null;
      if (packetIn.getType() == 10) {
         entity = EntityMinecart.getMinecart(
            this.clientWorldController, xCoordinate, yCoordinate, zCoordinate, net.minecraft.entity.item.EntityMinecart.EnumMinecartType.byNetworkID(packetIn.getData())
         );
      } else if (packetIn.getType() == 90) {
         Entity entity3 = this.clientWorldController.getEntityByID(packetIn.getData());
         if (entity3 instanceof EntityPlayer) {
            entity = new EntityFishHook(this.clientWorldController, xCoordinate, yCoordinate, zCoordinate, (EntityPlayer)entity3);
         }

         packetIn.update();
      } else if (packetIn.getType() == 60) {
         entity = new EntityArrow(this.clientWorldController, xCoordinate, yCoordinate, zCoordinate);
      } else if (packetIn.getType() == 61) {
         entity = new EntitySnowball(this.clientWorldController, xCoordinate, yCoordinate, zCoordinate);
      } else if (packetIn.getType() == 71) {
         entity = new EntityItemFrame(
            this.clientWorldController,
            new BlockPos(MathHelper.floor_double(xCoordinate), MathHelper.floor_double(yCoordinate), MathHelper.floor_double(zCoordinate)),
            EnumFacing.getHorizontal(packetIn.getData())
         );
         packetIn.update();
      } else if (packetIn.getType() == 77) {
         entity = new EntityLeashKnot(
            this.clientWorldController,
            new BlockPos(MathHelper.floor_double(xCoordinate), MathHelper.floor_double(yCoordinate), MathHelper.floor_double(zCoordinate))
         );
         packetIn.update();
      } else if (packetIn.getType() == 65) {
         entity = new EntityEnderPearl(this.clientWorldController, xCoordinate, yCoordinate, zCoordinate);
      } else if (packetIn.getType() == 72) {
         entity = new EntityEnderEye(this.clientWorldController, xCoordinate, yCoordinate, zCoordinate);
      } else if (packetIn.getType() == 76) {
         entity = new EntityFireworkRocket(this.clientWorldController, xCoordinate, yCoordinate, zCoordinate, (ItemStack)null);
      } else if (packetIn.getType() == 63) {
         entity = new EntityLargeFireball(
            this.clientWorldController,
            xCoordinate,
            yCoordinate,
            zCoordinate,
            packetIn.getSpeedX() / 8000.0,
            packetIn.getSpeedY() / 8000.0,
            packetIn.getSpeedZ() / 8000.0
         );
         packetIn.update();
      } else if (packetIn.getType() == 64) {
         entity = new EntitySmallFireball(
            this.clientWorldController,
            xCoordinate,
            yCoordinate,
            zCoordinate,
            packetIn.getSpeedX() / 8000.0,
            packetIn.getSpeedY() / 8000.0,
            packetIn.getSpeedZ() / 8000.0
         );
         packetIn.update();
      } else if (packetIn.getType() == 66) {
         entity = new EntityWitherSkull(
            this.clientWorldController,
            xCoordinate,
            yCoordinate,
            zCoordinate,
            packetIn.getSpeedX() / 8000.0,
            packetIn.getSpeedY() / 8000.0,
            packetIn.getSpeedZ() / 8000.0
         );
         packetIn.update();
      } else if (packetIn.getType() == 62) {
         entity = new EntityEgg(this.clientWorldController, xCoordinate, yCoordinate, zCoordinate);
      } else if (packetIn.getType() == 73) {
         entity = new EntityPotion(this.clientWorldController, xCoordinate, yCoordinate, zCoordinate, packetIn.getData());
         packetIn.update();
      } else if (packetIn.getType() == 75) {
         entity = new EntityExpBottle(this.clientWorldController, xCoordinate, yCoordinate, zCoordinate);
         packetIn.update();
      } else if (packetIn.getType() == 1) {
         entity = new EntityBoat(this.clientWorldController, xCoordinate, yCoordinate, zCoordinate);
      } else if (packetIn.getType() == 50) {
         entity = new EntityTNTPrimed(this.clientWorldController, xCoordinate, yCoordinate, zCoordinate, (EntityLivingBase)null);
      } else if (packetIn.getType() == 78) {
         entity = new EntityArmorStand(this.clientWorldController, xCoordinate, yCoordinate, zCoordinate);
      } else if (packetIn.getType() == 51) {
         entity = new EntityEnderCrystal(this.clientWorldController, xCoordinate, yCoordinate, zCoordinate);
      } else if (packetIn.getType() == 2) {
         entity = new EntityItem(this.clientWorldController, xCoordinate, yCoordinate, zCoordinate);
      } else if (packetIn.getType() == 70) {
         entity = new EntityFallingBlock(this.clientWorldController, xCoordinate, yCoordinate, zCoordinate, Block.getStateById(packetIn.getData() & 65535));
         packetIn.update();
      }

      if (entity != null) {
         entity.serverPosX = packetIn.getX();
         entity.serverPosY = packetIn.getY();
         entity.serverPosZ = packetIn.getZ();
         entity.rotationPitch = packetIn.getPitch() * 360 / 256.0F;
         entity.rotationYaw = packetIn.getYaw() * 360 / 256.0F;
         Entity[] aentity = entity.getParts();
         if (aentity != null) {
            int i = packetIn.getEntityID() - entity.getEntityId();

            for (int innerIndex = 0; innerIndex < aentity.length; innerIndex++) {
               aentity[innerIndex].setEntityId(aentity[innerIndex].getEntityId() + i);
            }
         }

         entity.setEntityId(packetIn.getEntityID());
         this.clientWorldController.addEntityToWorld(packetIn.getEntityID(), entity);
         if (packetIn.getData() > 0) {
            if (packetIn.getType() == 60) {
               Entity entity4 = this.clientWorldController.getEntityByID(packetIn.getData());
               if (entity4 instanceof EntityLivingBase && entity instanceof EntityArrow) {
                  ((EntityArrow)entity).shootingEntity = entity4;
               }
            }

            entity.setVelocity(packetIn.getSpeedX() / 8000.0, packetIn.getSpeedY() / 8000.0, packetIn.getSpeedZ() / 8000.0);
         }
      }
   }

   @Override
   public void handleEntityTeleport(S18PacketEntityTeleport packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityId());
      if (entity != null) {
         entity.serverPosX = packetIn.getX();
         entity.serverPosY = packetIn.getY();
         entity.serverPosZ = packetIn.getZ();
         double d = entity.serverPosX / 32.0;
         double d2 = entity.serverPosY / 32.0;
         double d3 = entity.serverPosZ / 32.0;
         float f = packetIn.getYaw() * 360 / 256.0F;
         float f2 = packetIn.getPitch() * 360 / 256.0F;
         if (Math.abs(entity.posX - d) < 0.03125 && Math.abs(entity.posY - d2) < 0.015625 && Math.abs(entity.posZ - d3) < 0.03125
            )
          {
            entity.handleDouble(entity.posX, entity.posY, entity.posZ, f, f2, true);
         } else {
            entity.handleDouble(d, d2, d3, f, f2, true);
         }

         entity.onGround = packetIn.getOnGround();
      }
   }

   @Override
   public void handleWindowProperty(S31PacketWindowProperty packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      EntityPlayer entityPlayer = this.gameController.thePlayer;
      if (entityPlayer.openContainer != null && entityPlayer.openContainer.windowId == packetIn.getWindowId()) {
         entityPlayer.openContainer.updateProgressBar(packetIn.getVarIndex(), packetIn.getVarValue());
      }
   }

   @Override
   public void handleTitle(S45PacketTitle packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      net.minecraft.network.play.server.S45PacketTitle.Type s45packettitle$type = packetIn.getType();
      String text4 = null;
      String text5 = null;
      String text3 = packetIn.getMessage() != null ? packetIn.getMessage().getFormattedText() : "";
      switch (s45packettitle$type) {
         case TITLE:
            text4 = text3;
            break;
         case SUBTITLE:
            text5 = text3;
            break;
         case RESET:
            this.gameController.ingameGUI.displayTitle("", "", -1, -1, -1);
            this.gameController.ingameGUI.setDefaultTitlesTimes();
            return;
      }

      this.gameController.ingameGUI.displayTitle(text4, text5, packetIn.getFadeInTime(), packetIn.getDisplayTime(), packetIn.getFadeOutTime());
   }

   @Override
   public void handleTimeUpdate(S03PacketTimeUpdate packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      this.gameController.theWorld.setTotalWorldTime(packetIn.getTotalWorldTime());
      this.gameController.theWorld.setWorldTime(packetIn.getWorldTime());
   }

   @Override
   public void handleKeepAlive(S00PacketKeepAlive packetIn) {
      this.addToSendQueue(new C00PacketKeepAlive(packetIn.getId()));
   }

   @Override
   public void handleSpawnExperienceOrb(S11PacketSpawnExperienceOrb packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      EntityXPOrb entityXPOrb = new EntityXPOrb(
         this.clientWorldController, packetIn.getX() / 32.0, packetIn.getY() / 32.0, packetIn.getZ() / 32.0, packetIn.getXPValue()
      );
      entityXPOrb.serverPosX = packetIn.getX();
      entityXPOrb.serverPosY = packetIn.getY();
      entityXPOrb.serverPosZ = packetIn.getZ();
      entityXPOrb.rotationYaw = 0.0F;
      entityXPOrb.rotationPitch = 0.0F;
      entityXPOrb.setEntityId(packetIn.getEntityID());
      this.clientWorldController.addEntityToWorld(packetIn.getEntityID(), entityXPOrb);
   }

   @Override
   public void handleWindowItems(S30PacketWindowItems packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      EntityPlayerSP thePlayer2 = this.gameController.thePlayer;
      if (packetIn.getWindowId() == 0) {
         thePlayer2.inventoryContainer.putStacksInSlots(packetIn.getItemStacks());
      } else if (packetIn.getWindowId() == thePlayer2.openContainer.windowId) {
         thePlayer2.openContainer.putStacksInSlots(packetIn.getItemStacks());
      }
   }

   @Override
   public void handleEntityNBT(S49PacketUpdateEntityNBT packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      Entity entity = packetIn.getEntity(this.clientWorldController);
      if (entity != null) {
         entity.clientUpdateEntityNBT(packetIn.getTagCompound());
      }
   }

   @Override
   public void handleUpdateTileEntity(S35PacketUpdateTileEntity packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      if (this.gameController.theWorld.isBlockLoaded(packetIn.getPos())) {
         TileEntity tileEntity = this.gameController.theWorld.getTileEntity(packetIn.getPos());
         int tileEntityType = packetIn.getTileEntityType();
         if (tileEntityType == 1 && tileEntity instanceof TileEntityMobSpawner
            || tileEntityType == 2 && tileEntity instanceof TileEntityCommandBlock
            || tileEntityType == 3 && tileEntity instanceof TileEntityBeacon
            || tileEntityType == 4 && tileEntity instanceof TileEntitySkull
            || tileEntityType == 5 && tileEntity instanceof TileEntityFlowerPot
            || tileEntityType == 6 && tileEntity instanceof TileEntityBanner) {
            tileEntity.readFromNBT(packetIn.getNbtCompound());
         }
      }
   }

   @Override
   public void handleEntityMetadata(S1CPacketEntityMetadata packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityId());
      if (entity != null && packetIn.getWatchableObjects() != null) {
         entity.getDataWatcher().updateWatchedObjectsFromList(packetIn.getWatchableObjects());
      }
   }

   public void addToSendQueue(Packet packet) {
      this.netManager.sendPacket(packet);
   }

   @Override
   public void handleMapChunkBulk(S26PacketMapChunkBulk packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);

      for (int index = 0; index < packetIn.getChunkCount(); index++) {
         int chunkX = packetIn.getChunkX(index);
         int chunkZ = packetIn.getChunkZ(index);
         this.clientWorldController.doPreChunk(chunkX, chunkZ, true);
         this.clientWorldController.noop(chunkX << 4, chunkZ << 4, (chunkX << 4) + 15, (chunkZ << 4) + 15);
         Chunk chunk = this.clientWorldController.getChunkFromChunkCoords(chunkX, chunkZ);
         chunk.fillChunk(packetIn.getChunkBytes(index), packetIn.getChunkSize(index), true);
         this.clientWorldController
            .markBlockRangeForRenderUpdate(chunkX << 4, 0, chunkZ << 4, (chunkX << 4) + 15, 256, (chunkZ << 4) + 15);
         if (!(this.clientWorldController.provider instanceof WorldProviderSurface)) {
            chunk.resetRelightChecks();
         }
      }
   }

   @Override
   public void handleAnimation(S0BPacketAnimation packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getEntityID());
      if (entityByID != null) {
         if (packetIn.getAnimationType() == 0) {
            ((EntityLivingBase)entityByID).swingItemNoPacket();
         } else if (packetIn.getAnimationType() == 1) {
            entityByID.performHurtAnimation();
         } else if (packetIn.getAnimationType() == 2) {
            ((EntityPlayer)entityByID).wakeUpPlayer(false, false, false);
         } else if (packetIn.getAnimationType() == 4) {
            this.gameController.effectRenderer.emitParticleAtEntity(entityByID, EnumParticleTypes.CRIT);
         } else if (packetIn.getAnimationType() == 5) {
            this.gameController.effectRenderer.emitParticleAtEntity(entityByID, EnumParticleTypes.CRIT_MAGIC);
         }
      }
   }

   @Override
   public void handleExplosion(S27PacketExplosion packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      new Explosion(
            this.gameController.theWorld,
            (Entity)null,
            packetIn.getX(),
            packetIn.getY(),
            packetIn.getZ(),
            packetIn.getStrength(),
            packetIn.getAffectedBlockPositions()
         )
         .doExplosionB(true);
      this.gameController.thePlayer.motionX = this.gameController.thePlayer.motionX + packetIn.getMotionX();
      this.gameController.thePlayer.motionY = this.gameController.thePlayer.motionY + packetIn.getMotionY();
      this.gameController.thePlayer.motionZ = this.gameController.thePlayer.motionZ + packetIn.getMotionZ();
   }

   @Override
   public void handleDisplayScoreboard(S3DPacketDisplayScoreboard packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      Scoreboard scoreboard = this.clientWorldController.getScoreboard();
      if (packetIn.getScoreName().length() == 0) {
         scoreboard.setObjectiveInDisplaySlot(packetIn.getPosition(), (ScoreObjective)null);
      } else {
         ScoreObjective scoreObjective = scoreboard.getObjective(packetIn.getScoreName());
         scoreboard.setObjectiveInDisplaySlot(packetIn.getPosition(), scoreObjective);
      }
   }

   @Override
   public void handleTabComplete(S3APacketTabComplete packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      String[] matches = packetIn.getMatches();
      if (this.gameController.currentScreen instanceof GuiChat) {
         ((GuiChat)this.gameController.currentScreen).onAutocompleteResponse(matches);
      }
   }

   @Override
   public void handleSetExperience(S1FPacketSetExperience packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      this.gameController.thePlayer.setXPStats(packetIn.getExperienceProgress(), packetIn.getTotalExperience(), packetIn.getLevel());
   }

   @Override
   public void handleResourcePack(S48PacketResourcePackSend packetIn) {
      final String text = packetIn.getURL();
      final String text2 = packetIn.getHash();
      if (text.startsWith("level://")) {
         String text3 = text.substring("level://".length());
         File file = new File(this.gameController.mcDataDir, "saves");
         File file2 = new File(file, text3);
         if (file2.isFile()) {
            this.netManager.sendPacket(new C19PacketResourcePackStatus(text2, net.minecraft.network.play.client.C19PacketResourcePackStatus.Action.ACCEPTED));
            Futures.addCallback(
               this.gameController.getResourcePackRepository().setResourcePackInstance(file2),
               new FutureCallback<Object>() {
                  public void onSuccess(Object arg1) {
                     NetHandlerPlayClient.this.netManager
                        .sendPacket(new C19PacketResourcePackStatus(text2, net.minecraft.network.play.client.C19PacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
                  }

                  public void onFailure(Throwable arg1) {
                     NetHandlerPlayClient.this.netManager
                        .sendPacket(new C19PacketResourcePackStatus(text2, net.minecraft.network.play.client.C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
                  }
               }
            );
         } else {
            this.netManager.sendPacket(new C19PacketResourcePackStatus(text2, net.minecraft.network.play.client.C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
         }
      } else if (this.gameController.getCurrentServerData() != null
         && this.gameController.getCurrentServerData().getResourceMode() == net.minecraft.client.multiplayer.ServerData.ServerResourceMode.ENABLED) {
         this.netManager.sendPacket(new C19PacketResourcePackStatus(text2, net.minecraft.network.play.client.C19PacketResourcePackStatus.Action.ACCEPTED));
         Futures.addCallback(this.gameController.getResourcePackRepository().downloadResourcePack(text, text2), new FutureCallback<Object>() {
            public void onFailure(Throwable arg1) {
               NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(text2, net.minecraft.network.play.client.C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
            }

            public void onSuccess(Object arg1) {
               NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(text2, net.minecraft.network.play.client.C19PacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
            }
         });
      } else if (this.gameController.getCurrentServerData() != null
         && this.gameController.getCurrentServerData().getResourceMode() != net.minecraft.client.multiplayer.ServerData.ServerResourceMode.PROMPT) {
         this.netManager.sendPacket(new C19PacketResourcePackStatus(text2, net.minecraft.network.play.client.C19PacketResourcePackStatus.Action.DECLINED));
      } else {
         this.gameController
            .addScheduledTask(
               new Runnable() {
                  @Override
                  public void run() {
                     NetHandlerPlayClient.this.gameController
                        .displayGuiScreen(
                           new GuiYesNo(
                              new GuiYesNoCallback() {
                                 @Override
                                 public void confirmClicked(boolean result, int id) {
                                    NetHandlerPlayClient.this.gameController = Minecraft.getMinecraft();
                                    if (result) {
                                       if (NetHandlerPlayClient.this.gameController.getCurrentServerData() != null) {
                                          NetHandlerPlayClient.this.gameController
                                             .getCurrentServerData()
                                             .setResourceMode(net.minecraft.client.multiplayer.ServerData.ServerResourceMode.ENABLED);
                                       }

                                       NetHandlerPlayClient.this.netManager
                                          .sendPacket(new C19PacketResourcePackStatus(text2, net.minecraft.network.play.client.C19PacketResourcePackStatus.Action.ACCEPTED));
                                       Futures.addCallback(
                                          NetHandlerPlayClient.this.gameController.getResourcePackRepository().downloadResourcePack(text, text2),
                                          new FutureCallback<Object>() {
                                             public void onFailure(Throwable arg1) {
                                                NetHandlerPlayClient.this.netManager
                                                   .sendPacket(new C19PacketResourcePackStatus(text2, net.minecraft.network.play.client.C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
                                             }

                                             public void onSuccess(Object arg1) {
                                                NetHandlerPlayClient.this.netManager
                                                   .sendPacket(new C19PacketResourcePackStatus(text2, net.minecraft.network.play.client.C19PacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
                                             }
                                          }
                                       );
                                    } else {
                                       if (NetHandlerPlayClient.this.gameController.getCurrentServerData() != null) {
                                          NetHandlerPlayClient.this.gameController
                                             .getCurrentServerData()
                                             .setResourceMode(net.minecraft.client.multiplayer.ServerData.ServerResourceMode.DISABLED);
                                       }

                                       NetHandlerPlayClient.this.netManager
                                          .sendPacket(new C19PacketResourcePackStatus(text2, net.minecraft.network.play.client.C19PacketResourcePackStatus.Action.DECLINED));
                                    }

                                    ServerList.saveSingleServer(NetHandlerPlayClient.this.gameController.getCurrentServerData());
                                    NetHandlerPlayClient.this.gameController.displayGuiScreen((GuiScreen)null);
                                 }
                              },
                              I18n.format("multiplayer.texturePrompt.line1"),
                              I18n.format("multiplayer.texturePrompt.line2"),
                              0
                           )
                        );
                  }
               }
            );
      }
   }

   @Override
   public void handleEntityProperties(S20PacketEntityProperties packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getEntityId());
      if (entityByID != null) {
         if (!(entityByID instanceof EntityLivingBase)) {
            throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + entityByID + ")");
         }

         BaseAttributeMap attributeMap = ((EntityLivingBase)entityByID).getAttributeMap();

         for (net.minecraft.network.play.server.S20PacketEntityProperties.Snapshot snapshot : packetIn.getSnapshots()) {
            IAttributeInstance attributeInstanceByName = attributeMap.getAttributeInstanceByName(snapshot.getName());
            if (attributeInstanceByName == null) {
               attributeInstanceByName = attributeMap.registerAttribute(new RangedAttribute((IAttribute)null, snapshot.getName(), 0.0, Double.MIN_NORMAL, Double.MAX_VALUE));
            }

            attributeInstanceByName.setBaseValue(snapshot.getBaseValue());
            attributeInstanceByName.removeAllModifiers();

            for (AttributeModifier attributeModifier : snapshot.getModifiers()) {
               attributeInstanceByName.applyModifier(attributeModifier);
            }
         }
      }
   }

   @Override
   public void handleEntityHeadLook(S19PacketEntityHeadLook packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      Entity entity = packetIn.getEntity(this.clientWorldController);
      if (entity != null) {
         float f = packetIn.getYaw() * 360 / 256.0F;
         entity.setRotationYawHead(f);
      }
   }

   @Override
   public void handleTeams(S3EPacketTeams packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      Scoreboard scoreboard = this.clientWorldController.getScoreboard();
      ScorePlayerTeam scorePlayerTeam;
      if (packetIn.getAction() == 0) {
         scorePlayerTeam = scoreboard.createTeam(packetIn.getName());
      } else {
         scorePlayerTeam = scoreboard.getTeam(packetIn.getName());
      }

      if (packetIn.getAction() == 0 || packetIn.getAction() == 2) {
         scorePlayerTeam.setTeamName(packetIn.getDisplayName());
         scorePlayerTeam.setNamePrefix(packetIn.getPrefix());
         scorePlayerTeam.setNameSuffix(packetIn.getSuffix());
         scorePlayerTeam.setChatFormat(EnumChatFormatting.getByColorIndex(packetIn.getColor()));
         scorePlayerTeam.setFriendlyFlags(packetIn.getFriendlyFlags());
         net.minecraft.scoreboard.Team.EnumVisible enumVisible = net.minecraft.scoreboard.Team.EnumVisible.byInternalName(packetIn.getNameTagVisibility());
         if (enumVisible != null) {
            scorePlayerTeam.setNameTagVisibility(enumVisible);
         }
      }

      if (packetIn.getAction() == 0 || packetIn.getAction() == 3) {
         for (String text2 : packetIn.getPlayers()) {
            scoreboard.addPlayerToTeam(text2, packetIn.getName());
         }
      }

      if (packetIn.getAction() == 4) {
         for (String text2 : packetIn.getPlayers()) {
            scoreboard.removePlayerFromTeam(text2, scorePlayerTeam);
         }
      }

      if (packetIn.getAction() == 1) {
         scoreboard.removeTeam(scorePlayerTeam);
      }
   }

   @Override
   public void handleSpawnPlayer(S0CPacketSpawnPlayer packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      double d = packetIn.getX() / 32.0;
      double d2 = packetIn.getY() / 32.0;
      double d3 = packetIn.getZ() / 32.0;
      float f = packetIn.getYaw() * 360 / 256.0F;
      float f2 = packetIn.getPitch() * 360 / 256.0F;
      EntityOtherPlayerMP entityOtherPlayerMP = new EntityOtherPlayerMP(this.gameController.theWorld, this.getPlayerInfo(packetIn.getPlayer()).getGameProfile());
      entityOtherPlayerMP.prevPosX = entityOtherPlayerMP.lastTickPosX = entityOtherPlayerMP.serverPosX = packetIn.getX();
      entityOtherPlayerMP.prevPosY = entityOtherPlayerMP.lastTickPosY = entityOtherPlayerMP.serverPosY = packetIn.getY();
      entityOtherPlayerMP.prevPosZ = entityOtherPlayerMP.lastTickPosZ = entityOtherPlayerMP.serverPosZ = packetIn.getZ();
      int currentItemID = packetIn.getCurrentItemID();
      if (currentItemID == 0) {
         entityOtherPlayerMP.inventory.mainInventory[entityOtherPlayerMP.inventory.currentItem] = null;
      } else {
         entityOtherPlayerMP.inventory.mainInventory[entityOtherPlayerMP.inventory.currentItem] = new ItemStack(Item.getItemById(currentItemID), 1, 0);
      }

      entityOtherPlayerMP.setPositionAndRotation(d, d2, d3, f, f2);
      this.clientWorldController.addEntityToWorld(packetIn.getEntityID(), entityOtherPlayerMP);
      List watchedList = packetIn.getWatchedList();
      if (watchedList != null) {
         entityOtherPlayerMP.getDataWatcher().updateWatchedObjectsFromList(watchedList);
      }
   }

   @Override
   public void handleConfirmTransaction(S32PacketConfirmTransaction packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      Container container = null;
      EntityPlayer entityPlayer = this.gameController.thePlayer;
      if (packetIn.getWindowId() == 0) {
         container = entityPlayer.inventoryContainer;
      } else if (packetIn.getWindowId() == entityPlayer.openContainer.windowId) {
         container = entityPlayer.openContainer;
      }

      if (container != null && !packetIn.isAccepted()) {
         this.addToSendQueue(new C0FPacketConfirmTransaction(packetIn.getWindowId(), packetIn.getActionNumber()));
      }
   }

   @Override
   public void handleMultiBlockChange(S22PacketMultiBlockChange packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);

      for (net.minecraft.network.play.server.S22PacketMultiBlockChange.BlockUpdateData blockUpdateData : packetIn.getChangedBlocks()) {
         this.clientWorldController.invalidateRegionAndSetBlock(blockUpdateData.getPos(), blockUpdateData.getBlockState());
      }
   }

   @Override
   public void handleCombatEvent(S42PacketCombatEvent packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      Entity entityByID = this.clientWorldController.getEntityByID(packetIn.killerId);
      EntityLivingBase entityLivingBase2 = entityByID instanceof EntityLivingBase ? (EntityLivingBase)entityByID : null;
      if (packetIn.eventType == net.minecraft.network.play.server.S42PacketCombatEvent.Event.END_COMBAT) {
         long l = 1000 * packetIn.duration / 20;
         MetadataCombat metadataCombat = new MetadataCombat(this.gameController.thePlayer, entityLivingBase2);
         this.gameController.getIStream().handleMetadata2(metadataCombat, 0L - l);
      } else if (packetIn.eventType == net.minecraft.network.play.server.S42PacketCombatEvent.Event.ENTITY_DIED) {
         Entity entityByID2 = this.clientWorldController.getEntityByID(packetIn.entityId);
         if (entityByID2 instanceof EntityPlayer) {
            MetadataPlayerDeath metadataPlayerDeath = new MetadataPlayerDeath((EntityPlayer)entityByID2, entityLivingBase2);
            metadataPlayerDeath.setText2(packetIn.deathMessage);
            this.gameController.getIStream().handleMetadata(metadataPlayerDeath);
         }
      }
   }

   @Override
   public void handleUpdateSign(S33PacketUpdateSign packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      boolean conditionMet = false;
      if (this.gameController.theWorld.isBlockLoaded(packetIn.getPos())
         && this.gameController.theWorld.getTileEntity(packetIn.getPos()) instanceof TileEntitySign tileEntitySign) {
         if (tileEntitySign.getIsEditable()) {
            System.arraycopy(packetIn.getLines(), 0, tileEntitySign.signText, 0, 4);
            tileEntitySign.markDirty();
         }

         conditionMet = true;
      }

      if (!conditionMet && this.gameController.thePlayer != null) {
         this.gameController
            .thePlayer
            .addChatMessage(
               new ChatComponentText("Unable to locate sign at " + packetIn.getPos().getX() + ", " + packetIn.getPos().getY() + ", " + packetIn.getPos().getZ())
            );
      }
   }

   public NetworkPlayerInfo getPlayerInfo(String text4) {
      for (NetworkPlayerInfo networkPlayerInfo : this.playerInfoMap.values()) {
         if (networkPlayerInfo.getGameProfile().getName().equals(text4)) {
            return networkPlayerInfo;
         }
      }

      return null;
   }

   @Override
   public void handleCollectItem(S0DPacketCollectItem packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      Entity entity = this.clientWorldController.getEntityByID(packetIn.getCollectedItemEntityID());
      EntityLivingBase entityLivingBase = (EntityLivingBase)this.clientWorldController.getEntityByID(packetIn.getEntityID());
      if (entityLivingBase == null) {
         entityLivingBase = this.gameController.thePlayer;
      }

      if (entity != null) {
         if (entity instanceof EntityXPOrb) {
            this.clientWorldController
               .playSoundAtEntity(entity, "random.orb", 0.2F, ((this.avRandomizer.nextFloat() - this.avRandomizer.nextFloat()) * 0.7F + 1.0F) * 2.0F);
         } else {
            this.clientWorldController
               .playSoundAtEntity(entity, "random.pop", 0.2F, ((this.avRandomizer.nextFloat() - this.avRandomizer.nextFloat()) * 0.7F + 1.0F) * 2.0F);
         }

         this.gameController.effectRenderer.addEffect(new EntityPickupFX(this.clientWorldController, entity, entityLivingBase, 0.5F));
         this.clientWorldController.removeEntityFromWorld(packetIn.getCollectedItemEntityID());
      }
   }

   @Override
   public void handleSetSlot(S2FPacketSetSlot packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      EntityPlayerSP thePlayer2 = this.gameController.thePlayer;
      if (packetIn.getWindowId() == -1) {
         thePlayer2.inventory.setItemStack(packetIn.getItemStack());
      } else {
         boolean flag = false;
         if (this.gameController.currentScreen instanceof GuiContainerCreative guiContainerCreative) {
            flag = guiContainerCreative.getSelectedTabIndex() != CreativeTabs.tabInventory.getTabIndex();
         }

         if (packetIn.getWindowId() == 0 && packetIn.getSlot() >= 36 && packetIn.getSlot() < 45) {
            ItemStack stack = thePlayer2.inventoryContainer.getSlot(packetIn.getSlot()).getStack();
            if (packetIn.getItemStack() != null && (stack == null || stack.stackSize < packetIn.getItemStack().stackSize)) {
               packetIn.getItemStack().animationsToGo = 5;
            }

            thePlayer2.inventoryContainer.putStackInSlot(packetIn.getSlot(), packetIn.getItemStack());
         } else if (packetIn.getWindowId() == thePlayer2.openContainer.windowId && (packetIn.getWindowId() != 0 || !flag)) {
            thePlayer2.openContainer.putStackInSlot(packetIn.getSlot(), packetIn.getItemStack());
         }
      }
   }

   @Override
   public void handleBlockChange(S23PacketBlockChange packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      this.clientWorldController.invalidateRegionAndSetBlock(packetIn.getBlockPosition(), packetIn.getBlockState());
   }

   @Override
   public void handleUseBed(S0APacketUseBed packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      packetIn.getPlayer(this.clientWorldController).trySleep(packetIn.getBedPosition());
   }

   @Override
   public void handleSoundEffect(S29PacketSoundEffect packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      this.gameController
         .theWorld
         .playSound(packetIn.getX(), packetIn.getY(), packetIn.getZ(), packetIn.getSoundName(), packetIn.getVolume(), packetIn.getPitch(), false);
   }

   @Override
   public void handlePlayerPosLook(S08PacketPlayerPosLook packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      EntityPlayerSP thePlayer2 = this.gameController.thePlayer;
      double x = packetIn.getX();
      double y = packetIn.getY();
      double z = packetIn.getZ();
      float yaw = packetIn.getYaw();
      float pitch = packetIn.getPitch();
      if (packetIn.getFlags().contains(net.minecraft.network.play.server.S08PacketPlayerPosLook.EnumFlags.X)) {
         x += thePlayer2.posX;
      } else {
         thePlayer2.motionX = 0.0;
      }

      if (packetIn.getFlags().contains(net.minecraft.network.play.server.S08PacketPlayerPosLook.EnumFlags.Y)) {
         y += thePlayer2.posY;
      } else {
         thePlayer2.motionY = 0.0;
      }

      if (packetIn.getFlags().contains(net.minecraft.network.play.server.S08PacketPlayerPosLook.EnumFlags.Z)) {
         z += thePlayer2.posZ;
      } else {
         thePlayer2.motionZ = 0.0;
      }

      if (packetIn.getFlags().contains(net.minecraft.network.play.server.S08PacketPlayerPosLook.EnumFlags.X_ROT)) {
         pitch += thePlayer2.rotationPitch;
      }

      if (packetIn.getFlags().contains(net.minecraft.network.play.server.S08PacketPlayerPosLook.EnumFlags.Y_ROT)) {
         yaw += thePlayer2.rotationYaw;
      }

      thePlayer2.setPositionAndRotation(x, y, z, yaw, pitch);
      this.netManager
         .sendPacket(
            new net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook(thePlayer2.posX, thePlayer2.getEntityBoundingBox().minY, thePlayer2.posZ, thePlayer2.rotationYaw, thePlayer2.rotationPitch, false)
         );
      if (!this.doneLoadingTerrain) {
         this.gameController.thePlayer.prevPosX = this.gameController.thePlayer.posX;
         this.gameController.thePlayer.prevPosY = this.gameController.thePlayer.posY;
         this.gameController.thePlayer.prevPosZ = this.gameController.thePlayer.posZ;
         this.doneLoadingTerrain = true;
         this.gameController.displayGuiScreen((GuiScreen)null);
      }
   }

   @Override
   public void handleDestroyEntities(S13PacketDestroyEntities packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);

      for (int index = 0; index < packetIn.getEntityIDs().length; index++) {
         this.clientWorldController.removeEntityFromWorld(packetIn.getEntityIDs()[index]);
      }
   }

   @Override
   public void onDisconnect(IChatComponent reason) {
      this.gameController.loadWorld((WorldClient)null);
      if (this.guiScreenServer != null) {
         if (this.guiScreenServer instanceof GuiScreenRealmsProxy) {
            this.gameController
               .displayGuiScreen(
                  new DisconnectedRealmsScreen(((GuiScreenRealmsProxy)this.guiScreenServer).getCommandSuggestions(), "disconnect.lost", reason)
                     .getGuiScreenRealmsProxy()
               );
         } else {
            this.gameController.displayGuiScreen(new GuiDisconnected(this.guiScreenServer, "disconnect.lost", reason));
         }
      } else {
         this.gameController.displayGuiScreen(new GuiDisconnected(new GuiMultiplayer(new NovolineMainMenu()), "disconnect.lost", reason));
      }
   }

   @Override
   public void handleRespawn(S07PacketRespawn packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      if (packetIn.getDimensionID() != this.gameController.thePlayer.dimension) {
         this.doneLoadingTerrain = false;
         Scoreboard scoreboard = this.clientWorldController.getScoreboard();
         this.clientWorldController = new WorldClient(
            this,
            new WorldSettings(0L, packetIn.getGameType(), false, this.gameController.theWorld.getWorldInfo().isHardcoreModeEnabled(), packetIn.getWorldType()),
            packetIn.getDimensionID(),
            packetIn.getDifficulty(),
            this.gameController.mcProfiler
         );
         this.clientWorldController.setWorldScoreboard(scoreboard);
         this.gameController.loadWorld(this.clientWorldController);
         this.gameController.thePlayer.dimension = packetIn.getDimensionID();
         this.gameController.displayGuiScreen(new GuiDownloadTerrain(this));
      }

      this.gameController.setDimensionAndSpawnPlayer(packetIn.getDimensionID());
      this.gameController.playerController.setGameType(packetIn.getGameType());
   }

   public void addToSendQueueUnregistered(Packet packet) {
      this.netManager.sendUnregisteredPacket(packet);
   }

   @Override
   public void handleEntityEquipment(S04PacketEntityEquipment packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getEntityID());
      if (entityByID != null) {
         entityByID.setCurrentItemOrArmor(packetIn.getEquipmentSlot(), packetIn.getItemStack());
      }
   }

   @Override
   public void handleRemoveEntityEffect(S1EPacketRemoveEntityEffect packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.gameController);
      Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityId());
      if (entity instanceof EntityLivingBase) {
         ((EntityLivingBase)entity).removePotionEffectClient(packetIn.getEffectId());
      }
   }
}
