package net.minecraft.network;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.util.concurrent.Futures;
import io.netty.buffer.Unpooled;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.block.material.Material;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityMinecartCommandBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.network.play.client.C11PacketEnchantItem;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.client.C18PacketSpectate;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListBansEntry;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.WorldServer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerPlayServer implements INetHandlerPlayServer, ITickable {
   private IntHashMap<Short> pendingTransactions = new IntHashMap<>();
   private static final Logger logger = LogManager.getLogger();
   private long lastPingTime;
   private int floatingTickCount;
   private double lastPosY;
   private boolean hasMoved = true;
   private int networkTickCount;
   private int lastPositionUpdate;
   private double lastPosX;
   private boolean processedMovePacket;
   private int itemDropThreshold;
   private int keepAliveKey;
   private long lastSentPingPacket;
   private double lastPosZ;
   private final MinecraftServer serverController;
   private int chatSpamThresholdCount;
   public final NetworkManager netManager;
   public EntityPlayerMP playerEntity;

   public void setPlayerLocation(double x, double y, double z, float yaw, float pitch, Set<net.minecraft.network.play.server.S08PacketPlayerPosLook.EnumFlags> relativeSet) {
      this.hasMoved = false;
      this.lastPosX = x;
      this.lastPosY = y;
      this.lastPosZ = z;
      if (relativeSet.contains(net.minecraft.network.play.server.S08PacketPlayerPosLook.EnumFlags.X)) {
         this.lastPosX = this.lastPosX + this.playerEntity.posX;
      }

      if (relativeSet.contains(net.minecraft.network.play.server.S08PacketPlayerPosLook.EnumFlags.Y)) {
         this.lastPosY = this.lastPosY + this.playerEntity.posY;
      }

      if (relativeSet.contains(net.minecraft.network.play.server.S08PacketPlayerPosLook.EnumFlags.Z)) {
         this.lastPosZ = this.lastPosZ + this.playerEntity.posZ;
      }

      float yaw2 = yaw;
      float pitch2 = pitch;
      if (relativeSet.contains(net.minecraft.network.play.server.S08PacketPlayerPosLook.EnumFlags.Y_ROT)) {
         yaw2 = yaw + this.playerEntity.rotationYaw;
      }

      if (relativeSet.contains(net.minecraft.network.play.server.S08PacketPlayerPosLook.EnumFlags.X_ROT)) {
         pitch2 = pitch + this.playerEntity.rotationPitch;
      }

      this.playerEntity.setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, yaw2, pitch2);
      this.playerEntity.playerNetServerHandler.sendPacket(new S08PacketPlayerPosLook(x, y, z, yaw, pitch, relativeSet));
   }

   @Override
   public void processHeldItemChange(C09PacketHeldItemChange packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.playerEntity.getServerForPlayer());
      if (packetIn.slotId >= 0 && packetIn.slotId < InventoryPlayer.getHotbarSize()) {
         this.playerEntity.inventory.currentItem = packetIn.slotId;
         this.playerEntity.markPlayerActive();
      } else {
         logger.warn(this.playerEntity.getName() + " tried to set an invalid carried item");
      }
   }

   @Override
   public void processPlayerDigging(C07PacketPlayerDigging packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.playerEntity.getServerForPlayer());
      WorldServer worldServer = this.serverController.worldServerForDimension(this.playerEntity.dimension);
      BlockPos blockPos = packetIn.position;
      this.playerEntity.markPlayerActive();
      switch (packetIn.status) {
         case DROP_ITEM:
            if (!this.playerEntity.isSpectator()) {
               this.playerEntity.dropOneItem(false);
            }

            return;
         case DROP_ALL_ITEMS:
            if (!this.playerEntity.isSpectator()) {
               this.playerEntity.dropOneItem(true);
            }

            return;
         case RELEASE_USE_ITEM:
            this.playerEntity.stopUsingItem();
            return;
         case START_DESTROY_BLOCK:
         case ABORT_DESTROY_BLOCK:
         case STOP_DESTROY_BLOCK:
            double xCoordinate = this.playerEntity.posX - (blockPos.getX() + 0.5);
            double yCoordinate = this.playerEntity.posY - (blockPos.getY() + 0.5) + 1.5;
            double zCoordinate = this.playerEntity.posZ - (blockPos.getZ() + 0.5);
            double d = xCoordinate * xCoordinate + yCoordinate * yCoordinate + zCoordinate * zCoordinate;
            if (d > 36.0) {
               return;
            } else if (blockPos.getY() >= this.serverController.getBuildLimit()) {
               return;
            } else {
               if (packetIn.status == net.minecraft.network.play.client.C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                  if (!this.serverController.isBlockProtected(worldServer, blockPos, this.playerEntity) && worldServer.getWorldBorder().contains(blockPos)) {
                     this.playerEntity.theItemInWorldManager.onBlockClicked(blockPos, packetIn.facing);
                  } else {
                     this.playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(worldServer, blockPos));
                  }
               } else {
                  if (packetIn.status == net.minecraft.network.play.client.C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
                     this.playerEntity.theItemInWorldManager.blockRemoving(blockPos);
                  } else if (packetIn.status == net.minecraft.network.play.client.C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK) {
                     this.playerEntity.theItemInWorldManager.cancelDestroyingBlock();
                  }

                  if (worldServer.getBlockState(blockPos).getBlock().getMaterial() != Material.air) {
                     this.playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(worldServer, blockPos));
                  }
               }

               return;
            }
         default:
            throw new IllegalArgumentException("Invalid player action");
      }
   }

   @Override
   public void handleSpectate(C18PacketSpectate packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.playerEntity.getServerForPlayer());
      if (this.playerEntity.isSpectator()) {
         Entity entity2 = null;

         for (WorldServer worldServer2 : this.serverController.worldServers) {
            if (worldServer2 != null) {
               entity2 = worldServer2.getEntityFromUuid(packetIn.id);
               if (entity2 != null) {
                  break;
               }
            }
         }

         if (entity2 != null) {
            this.playerEntity.setSpectatingEntity(this.playerEntity);
            this.playerEntity.mountEntity((Entity)null);
            if (entity2.worldObj != this.playerEntity.worldObj) {
               WorldServer serverForPlayer = this.playerEntity.getServerForPlayer();
               WorldServer worldServer3 = (WorldServer)entity2.worldObj;
               this.playerEntity.dimension = entity2.dimension;
               this.sendPacket(
                  new S07PacketRespawn(
                     this.playerEntity.dimension,
                     serverForPlayer.getDifficulty(),
                     serverForPlayer.getWorldInfo().getTerrainType(),
                     this.playerEntity.theItemInWorldManager.getGameType()
                  )
               );
               serverForPlayer.removePlayerEntityDangerously(this.playerEntity);
               this.playerEntity.isDead = false;
               this.playerEntity.setLocationAndAngles(entity2.posX, entity2.posY, entity2.posZ, entity2.rotationYaw, entity2.rotationPitch);
               if (this.playerEntity.isEntityAlive()) {
                  serverForPlayer.updateEntityWithOptionalForce(this.playerEntity, false);
                  worldServer3.spawnEntityInWorld(this.playerEntity);
                  worldServer3.updateEntityWithOptionalForce(this.playerEntity, false);
               }

               this.playerEntity.setWorld(worldServer3);
               this.serverController.getConfigurationManager().preparePlayer(this.playerEntity, serverForPlayer);
               this.playerEntity.setPositionAndUpdate(entity2.posX, entity2.posY, entity2.posZ);
               this.playerEntity.theItemInWorldManager.setWorld(worldServer3);
               this.serverController.getConfigurationManager().updateTimeAndWeatherForPlayer(this.playerEntity, worldServer3);
               this.serverController.getConfigurationManager().syncPlayerInventory(this.playerEntity);
            } else {
               this.playerEntity.setPositionAndUpdate(entity2.posX, entity2.posY, entity2.posZ);
            }
         }
      }
   }

   @Override
   public void processCloseWindow(C0DPacketCloseWindow packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.closeContainer();
   }

   @Override
   public void processEnchantItem(C11PacketEnchantItem packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.markPlayerActive();
      if (this.playerEntity.openContainer.windowId == packetIn.windowId
         && this.playerEntity.openContainer.getCanCraft(this.playerEntity)
         && !this.playerEntity.isSpectator()) {
         this.playerEntity.openContainer.enchantItem(this.playerEntity, packetIn.button);
         this.playerEntity.openContainer.detectAndSendChanges();
      }
   }

   public void sendPacket(final Packet packetIn) {
      if (packetIn instanceof S02PacketChat s02PacketChat) {
         net.minecraft.entity.player.EntityPlayer.EnumChatVisibility chatVisibility = this.playerEntity.getChatVisibility();
         if (chatVisibility == net.minecraft.entity.player.EntityPlayer.EnumChatVisibility.HIDDEN) {
            return;
         }

         if (chatVisibility == net.minecraft.entity.player.EntityPlayer.EnumChatVisibility.SYSTEM && !s02PacketChat.isChat()) {
            return;
         }
      }

      try {
         this.netManager.sendPacket(packetIn);
      } catch (Throwable throwable) {
         CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Sending packet");
         crashReport.makeCategory("Packet being sent").addCrashSectionCallable("Packet class", new Callable<String>() {
            public String call() throws Exception {
               return packetIn.getClass().getCanonicalName();
            }
         });
         throw new ReportedException(crashReport);
      }
   }

   private boolean isMovePacketInvalid(C03PacketPlayer packet) {
      return !Doubles.isFinite(packet.x)
         || !Doubles.isFinite(packet.y)
         || !Doubles.isFinite(packet.z)
         || !Floats.isFinite(packet.pitch)
         || !Floats.isFinite(packet.yaw);
   }

   @Override
   public void processCreativeInventoryAction(C10PacketCreativeInventoryAction packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.playerEntity.getServerForPlayer());
      if (this.playerEntity.theItemInWorldManager.isCreative()) {
         boolean flag = packetIn.slotId < 0;
         ItemStack itemStack = packetIn.stack;
         if (itemStack != null && itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("BlockEntityTag", 10)) {
            NBTTagCompound nBTTagCompound = itemStack.getTagCompound().getCompoundTag("BlockEntityTag");
            if (nBTTagCompound.hasKey("x") && nBTTagCompound.hasKey("y") && nBTTagCompound.hasKey("z")) {
               BlockPos blockPos2 = new BlockPos(nBTTagCompound.getInteger("x"), nBTTagCompound.getInteger("y"), nBTTagCompound.getInteger("z"));
               TileEntity tileEntity = this.playerEntity.worldObj.getTileEntity(blockPos2);
               if (tileEntity != null) {
                  NBTTagCompound entityItem = new NBTTagCompound();
                  tileEntity.writeToNBT(entityItem);
                  entityItem.removeTag("x");
                  entityItem.removeTag("y");
                  entityItem.removeTag("z");
                  itemStack.setTagInfo("BlockEntityTag", entityItem);
               }
            }
         }

         boolean flag2 = packetIn.slotId >= 1 && packetIn.slotId < 36 + InventoryPlayer.getHotbarSize();
         boolean flag3 = itemStack == null || itemStack.getItem() != null;
         boolean flag4 = itemStack == null || itemStack.getMetadata() >= 0 && itemStack.stackSize <= 64 && itemStack.stackSize > 0;
         if (flag2 && flag3 && flag4) {
            if (itemStack == null) {
               this.playerEntity.inventoryContainer.putStackInSlot(packetIn.slotId, (ItemStack)null);
            } else {
               this.playerEntity.inventoryContainer.putStackInSlot(packetIn.slotId, itemStack);
            }

            this.playerEntity.inventoryContainer.setCanCraft(this.playerEntity, true);
         } else if (flag && flag3 && flag4 && this.itemDropThreshold < 200) {
            this.itemDropThreshold += 20;
            EntityItem entityItem = this.playerEntity.getEntityItem3(itemStack);
            if (entityItem != null) {
               entityItem.setAgeToCreativeDespawnTime();
            }
         }
      }
   }

   private long currentTimeMillis() {
      return System.nanoTime() / 1000000L;
   }

   @Override
   public void onDisconnect(IChatComponent reason) {
      logger.info(this.playerEntity.getName() + " lost connection: " + reason);
      this.serverController.refreshStatusNextTick();
      ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("multiplayer.player.left", this.playerEntity.getDisplayName());
      chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.YELLOW);
      this.serverController.getConfigurationManager().sendChatMsg(chatComponentTranslation);
      this.playerEntity.mountEntityAndWakeUp();
      this.serverController.getConfigurationManager().playerLoggedOut(this.playerEntity);
      if (this.serverController.isSinglePlayer() && this.playerEntity.getName().equals(this.serverController.getServerOwner())) {
         logger.info("Stopping singleplayer server as player logged out");
         this.serverController.initiateShutdown();
      }
   }

   @Override
   public void processVanilla250Packet(C17PacketCustomPayload packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.playerEntity.getServerForPlayer());
      if ("MC|BEdit".equals(packetIn.channel)) {
         PacketBuffer packetBuffer = new PacketBuffer(Unpooled.wrappedBuffer(packetIn.data));

         try {
            ItemStack itemStackFromBuffer = packetBuffer.readItemStackFromBuffer();
            if (itemStackFromBuffer == null) {
               return;
            }

            if (!ItemWritableBook.isNBTValid(itemStackFromBuffer.getTagCompound())) {
               throw new IOException("Invalid book tag!");
            }

            ItemStack currentItem2 = this.playerEntity.inventory.getCurrentItem();
            if (currentItem2 == null) {
               return;
            }

            if (itemStackFromBuffer.getItem() == Items.writable_book && itemStackFromBuffer.getItem() == currentItem2.getItem()) {
               currentItem2.setTagInfo("pages", itemStackFromBuffer.getTagCompound().getTagList("pages", 8));
            }
         } catch (Exception exception) {
            logger.error("Couldn't handle book info", exception);
            return;
         } finally {
            packetBuffer.release();
         }
      } else if ("MC|BSign".equals(packetIn.channel)) {
         PacketBuffer packetBuffer2 = new PacketBuffer(Unpooled.wrappedBuffer(packetIn.data));

         try {
            ItemStack itemStackFromBuffer2 = packetBuffer2.readItemStackFromBuffer();
            if (itemStackFromBuffer2 == null) {
               return;
            }

            if (!ItemEditableBook.validBookTagContents(itemStackFromBuffer2.getTagCompound())) {
               throw new IOException("Invalid book tag!");
            }

            ItemStack currentItem3 = this.playerEntity.inventory.getCurrentItem();
            if (currentItem3 == null) {
               return;
            }

            if (itemStackFromBuffer2.getItem() == Items.written_book && currentItem3.getItem() == Items.writable_book) {
               currentItem3.setTagInfo("author", new NBTTagString(this.playerEntity.getName()));
               currentItem3.setTagInfo("title", new NBTTagString(itemStackFromBuffer2.getTagCompound().getString("title")));
               currentItem3.setTagInfo("pages", itemStackFromBuffer2.getTagCompound().getTagList("pages", 8));
               currentItem3.setItem(Items.written_book);
            }
         } catch (Exception exception2) {
            logger.error("Couldn't sign book", exception2);
            return;
         } finally {
            packetBuffer2.release();
         }
      } else {
         if ("MC|TrSel".equals(packetIn.channel)) {
            try {
               int intValue = packetIn.data.readInt();
               Container openContainer2 = this.playerEntity.openContainer;
               if (openContainer2 instanceof ContainerMerchant) {
                  ((ContainerMerchant)openContainer2).setCurrentRecipeIndex(intValue);
               }
            } catch (Exception exception3) {
               logger.error("Couldn't select trade", exception3);
            }
         } else if ("MC|AdvCdm".equals(packetIn.channel)) {
            if (!this.serverController.isCommandBlockEnabled()) {
               this.playerEntity.addChatMessage(new ChatComponentTranslation("advMode.notEnabled"));
            } else if (this.playerEntity.canCommandSenderUseCommand(2, "") && this.playerEntity.capabilities.isCreativeMode) {
               PacketBuffer data2 = packetIn.data;

               try {
                  byte byteValue = data2.readByte();
                  CommandBlockLogic commandBlockLogic = null;
                  if (byteValue == 0) {
                     TileEntity tileEntity2 = this.playerEntity.worldObj.getTileEntity(new BlockPos(data2.readInt(), data2.readInt(), data2.readInt()));
                     if (tileEntity2 instanceof TileEntityCommandBlock) {
                        commandBlockLogic = ((TileEntityCommandBlock)tileEntity2).getCommandBlockLogic();
                     }
                  } else if (byteValue == 1) {
                     Entity entityByID = this.playerEntity.worldObj.getEntityByID(data2.readInt());
                     if (entityByID instanceof EntityMinecartCommandBlock) {
                        commandBlockLogic = ((EntityMinecartCommandBlock)entityByID).getCommandBlockLogic();
                     }
                  }

                  String stringFromBuffer = data2.readStringFromBuffer(data2.readableBytes());
                  boolean booleanValue = data2.readBoolean();
                  if (commandBlockLogic != null) {
                     commandBlockLogic.setCommand(stringFromBuffer);
                     commandBlockLogic.setTrackOutput(booleanValue);
                     if (!booleanValue) {
                        commandBlockLogic.setLastOutput((IChatComponent)null);
                     }

                     commandBlockLogic.updateCommand();
                     this.playerEntity.addChatMessage(new ChatComponentTranslation("advMode.setCommand.success", stringFromBuffer));
                  }
               } catch (Exception exception4) {
                  logger.error("Couldn't set command block", exception4);
               } finally {
                  data2.release();
               }
            } else {
               this.playerEntity.addChatMessage(new ChatComponentTranslation("advMode.notAllowed"));
            }
         } else if ("MC|Beacon".equals(packetIn.channel)) {
            if (this.playerEntity.openContainer instanceof ContainerBeacon) {
               try {
                  PacketBuffer data3 = packetIn.data;
                  int intValue2 = data3.readInt();
                  int intValue3 = data3.readInt();
                  ContainerBeacon containerBeacon = (ContainerBeacon)this.playerEntity.openContainer;
                  Slot slot = containerBeacon.getSlot(0);
                  if (slot.getHasStack()) {
                     slot.decrStackSize(1);
                     IInventory beaconInventory = containerBeacon.getBeaconInventory();
                     beaconInventory.setField(1, intValue2);
                     beaconInventory.setField(2, intValue3);
                     beaconInventory.markDirty();
                  }
               } catch (Exception exception5) {
                  logger.error("Couldn't set beacon", exception5);
               }
            }
         } else if ("MC|ItemName".equals(packetIn.channel) && this.playerEntity.openContainer instanceof ContainerRepair containerRepair) {
            if (packetIn.data != null && packetIn.data.readableBytes() >= 1) {
               String text2 = ChatAllowedCharacters.filterAllowedCharacters(packetIn.data.readStringFromBuffer(32767));
               if (text2.length() <= 30) {
                  containerRepair.updateItemName(text2);
               }
            } else {
               containerRepair.updateItemName("");
            }
         }
      }
   }

   @Override
   public void processKeepAlive(C00PacketKeepAlive packetIn) {
      if (packetIn.key == this.keepAliveKey) {
         int i = (int)(this.currentTimeMillis() - this.lastPingTime);
         this.playerEntity.ping = (this.playerEntity.ping * 3 + i) / 4;
      }
   }

   public NetHandlerPlayServer(MinecraftServer server, NetworkManager networkManagerIn, EntityPlayerMP playerIn) {
      this.serverController = server;
      this.netManager = networkManagerIn;
      networkManagerIn.setNetHandler(this);
      this.playerEntity = playerIn;
      playerIn.playerNetServerHandler = this;
   }

   public NetworkManager getNetworkManager() {
      return this.netManager;
   }

   @Override
   public void processEntityAction(C0BPacketEntityAction packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.markPlayerActive();
      switch (packetIn.action) {
         case START_SNEAKING:
            this.playerEntity.setSneaking(true);
            break;
         case STOP_SNEAKING:
            this.playerEntity.setSneaking(false);
            break;
         case START_SPRINTING:
            this.playerEntity.setSprinting(true);
            break;
         case STOP_SPRINTING:
            this.playerEntity.setSprinting(false);
            break;
         case STOP_SLEEPING:
            this.playerEntity.wakeUpPlayer(false, true, true);
            this.hasMoved = false;
            break;
         case RIDING_JUMP:
            if (this.playerEntity.ridingEntity instanceof EntityHorse) {
               ((EntityHorse)this.playerEntity.ridingEntity).setJumpPower(packetIn.auxData);
            }
            break;
         case OPEN_INVENTORY:
            if (this.playerEntity.ridingEntity instanceof EntityHorse) {
               ((EntityHorse)this.playerEntity.ridingEntity).openGUI(this.playerEntity);
            }
            break;
         default:
            throw new IllegalArgumentException("Invalid client command!");
      }
   }

   public void setPlayerLocation(double x, double y, double z, float yaw, float pitch) {
      this.setPlayerLocation(x, y, z, yaw, pitch, Collections.emptySet());
   }

   private void handleSlashCommand(String command) {
      this.serverController.getCommandManager().executeCommand(this.playerEntity, command);
   }

   @Override
   public void update() {
      this.processedMovePacket = false;
      this.networkTickCount++;
      this.serverController.theProfiler.startSection("keepAlive");
      if (this.networkTickCount - this.lastSentPingPacket > 40L) {
         this.lastSentPingPacket = this.networkTickCount;
         this.lastPingTime = this.currentTimeMillis();
         this.keepAliveKey = (int)this.lastPingTime;
         this.sendPacket(new S00PacketKeepAlive(this.keepAliveKey));
      }

      this.serverController.theProfiler.endSection();
      if (this.chatSpamThresholdCount > 0) {
         this.chatSpamThresholdCount--;
      }

      if (this.itemDropThreshold > 0) {
         this.itemDropThreshold--;
      }

      if (this.playerEntity.getLastActiveTime() > 0L
         && this.serverController.getMaxPlayerIdleMinutes() > 0
         && MinecraftServer.getCurrentTimeMillis() - this.playerEntity.getLastActiveTime() > this.serverController.getMaxPlayerIdleMinutes() * 1000 * 60) {
         this.kickPlayerFromServer("You have been idle for too long!");
      }
   }

   @Override
   public void processUseEntity(C02PacketUseEntity packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.playerEntity.getServerForPlayer());
      Entity entityByID = this.serverController.worldServerForDimension(this.playerEntity.dimension).getEntityByID(packetIn.entityId);
      this.playerEntity.markPlayerActive();
      if (entityByID != null) {
         boolean entityBeSeen = this.playerEntity.canEntityBeSeen(entityByID);
         double d = 36.0;
         if (!entityBeSeen) {
            d = 9.0;
         }

         if (this.playerEntity.getDistanceSqToEntity(entityByID) < d) {
            if (packetIn.action == net.minecraft.network.play.client.C02PacketUseEntity.Action.INTERACT) {
               this.playerEntity.interactWith(entityByID);
            } else if (packetIn.action == net.minecraft.network.play.client.C02PacketUseEntity.Action.INTERACT_AT) {
               entityByID.interactAt(this.playerEntity, packetIn.hitVec);
            } else if (packetIn.action == net.minecraft.network.play.client.C02PacketUseEntity.Action.ATTACK) {
               if (entityByID instanceof EntityItem || entityByID instanceof EntityXPOrb || entityByID instanceof EntityArrow || entityByID == this.playerEntity) {
                  this.kickPlayerFromServer("Attempting to attack an invalid entity");
                  this.serverController.logWarning("Player " + this.playerEntity.getName() + " tried to attack an invalid entity");
                  return;
               }

               this.playerEntity.attackTargetEntityWithCurrentItem(entityByID);
            }
         }
      }
   }

   @Override
   public void handleResourcePackStatus(C19PacketResourcePackStatus packetIn) {
   }

   @Override
   public void processPlayerBlockPlacement(C08PacketPlayerBlockPlacement packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.playerEntity.getServerForPlayer());
      WorldServer worldServer2 = this.serverController.worldServerForDimension(this.playerEntity.dimension);
      ItemStack currentItem2 = this.playerEntity.inventory.getCurrentItem();
      boolean flag = false;
      BlockPos position2 = packetIn.position;
      EnumFacing front = EnumFacing.getFront(packetIn.placedBlockDirection);
      this.playerEntity.markPlayerActive();
      if (packetIn.placedBlockDirection == 255) {
         if (currentItem2 == null) {
            return;
         }

         this.playerEntity.theItemInWorldManager.tryUseItem(this.playerEntity, worldServer2, currentItem2);
      } else if (position2.getY() < this.serverController.getBuildLimit() - 1 || front != EnumFacing.UP && position2.getY() < this.serverController.getBuildLimit()) {
         if (this.hasMoved
            && this.playerEntity.getDistanceSq(position2.getX() + 0.5, position2.getY() + 0.5, position2.getZ() + 0.5) < 64.0
            && !this.serverController.isBlockProtected(worldServer2, position2, this.playerEntity)
            && worldServer2.getWorldBorder().contains(position2)) {
            this.playerEntity
               .theItemInWorldManager
               .activateBlockOrUseItem(this.playerEntity, worldServer2, currentItem2, position2, front, packetIn.facingX, packetIn.facingY, packetIn.facingZ);
         }

         flag = true;
      } else {
         ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("build.tooHigh", this.serverController.getBuildLimit());
         chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.RED);
         this.playerEntity.playerNetServerHandler.sendPacket(new S02PacketChat(chatComponentTranslation));
         flag = true;
      }

      if (flag) {
         this.playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(worldServer2, position2));
         this.playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(worldServer2, position2.offset(front)));
      }

      currentItem2 = this.playerEntity.inventory.getCurrentItem();
      if (currentItem2 != null && currentItem2.stackSize == 0) {
         this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem] = null;
         currentItem2 = null;
      }

      if (currentItem2 == null || currentItem2.getMaxItemUseDuration() == 0) {
         this.playerEntity.isChangingQuantityOnly = true;
         this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem] = ItemStack.copyItemStack(
            this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem]
         );
         Slot slotFromInventory = this.playerEntity.openContainer.getSlotFromInventory(this.playerEntity.inventory, this.playerEntity.inventory.currentItem);
         this.playerEntity.openContainer.detectAndSendChanges();
         this.playerEntity.isChangingQuantityOnly = false;
         if (!ItemStack.areItemStacksEqual(this.playerEntity.inventory.getCurrentItem(), packetIn.stack)) {
            this.sendPacket(new S2FPacketSetSlot(this.playerEntity.openContainer.windowId, slotFromInventory.slotNumber, this.playerEntity.inventory.getCurrentItem()));
         }
      }
   }

   @Override
   public void processConfirmTransaction(C0FPacketConfirmTransaction packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.playerEntity.getServerForPlayer());
      Short oshort = this.pendingTransactions.lookup(this.playerEntity.openContainer.windowId);
      if (oshort != null
         && packetIn.uid == oshort
         && this.playerEntity.openContainer.windowId == packetIn.windowId
         && !this.playerEntity.openContainer.getCanCraft(this.playerEntity)
         && !this.playerEntity.isSpectator()) {
         this.playerEntity.openContainer.setCanCraft(this.playerEntity, true);
      }
   }

   @Override
   public void processInput(C0CPacketInput packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.setEntityActionState(packetIn.strafeSpeed, packetIn.forwardSpeed, packetIn.jumping, packetIn.sneaking);
   }

   @Override
   public void handleAnimation(C0APacketAnimation packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.markPlayerActive();
      this.playerEntity.swingItemNoPacket();
   }

   @Override
   public void processUpdateSign(C12PacketUpdateSign packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.markPlayerActive();
      WorldServer worldServer = this.serverController.worldServerForDimension(this.playerEntity.dimension);
      BlockPos blockPos = packetIn.pos;
      if (worldServer.isBlockLoaded(blockPos)) {
         if (!(worldServer.getTileEntity(blockPos) instanceof TileEntitySign tileEntitySign)) {
            return;
         }

         if (!tileEntitySign.getIsEditable() || tileEntitySign.getPlayer() != this.playerEntity) {
            this.serverController.logWarning("Player " + this.playerEntity.getName() + " just tried to change non-editable sign");
            return;
         }

         IChatComponent[] aichatcomponent = packetIn.lines;

         for (int index = 0; index < aichatcomponent.length; index++) {
            tileEntitySign.signText[index] = new ChatComponentText(
               EnumChatFormatting.getTextWithoutFormattingCodes(aichatcomponent[index].getUnformattedText())
            );
         }

         tileEntitySign.markDirty();
         worldServer.markBlockForUpdate(blockPos);
      }
   }

   @Override
   public void processPlayer(C03PacketPlayer packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.playerEntity.getServerForPlayer());
      if (this.isMovePacketInvalid(packetIn)) {
         this.kickPlayerFromServer("Invalid move packet received");
      } else {
         WorldServer worldServer2 = this.serverController.worldServerForDimension(this.playerEntity.dimension);
         this.processedMovePacket = true;
         if (!this.playerEntity.playerConqueredTheEnd) {
            double posX2 = this.playerEntity.posX;
            double posY2 = this.playerEntity.posY;
            double posZ2 = this.playerEntity.posZ;
            double d = 0.0;
            double d2 = packetIn.x - this.lastPosX;
            double d3 = packetIn.y - this.lastPosY;
            double d4 = packetIn.z - this.lastPosZ;
            if (packetIn.moving) {
               d = d2 * d2 + d3 * d3 + d4 * d4;
               if (!this.hasMoved && d < 0.25) {
                  this.hasMoved = true;
               }
            }

            if (this.hasMoved) {
               this.lastPositionUpdate = this.networkTickCount;
               if (this.playerEntity.ridingEntity != null) {
                  float rotationYaw2 = this.playerEntity.rotationYaw;
                  float rotationPitch2 = this.playerEntity.rotationPitch;
                  this.playerEntity.ridingEntity.updateRiderPosition();
                  double posX3 = this.playerEntity.posX;
                  double posY3 = this.playerEntity.posY;
                  double posZ3 = this.playerEntity.posZ;
                  if (packetIn.rotating) {
                     rotationYaw2 = packetIn.yaw;
                     rotationPitch2 = packetIn.pitch;
                  }

                  this.playerEntity.onGround = packetIn.onGround;
                  this.playerEntity.onUpdateEntity();
                  this.playerEntity.setPositionAndRotation(posX3, posY3, posZ3, rotationYaw2, rotationPitch2);
                  if (this.playerEntity.ridingEntity != null) {
                     this.playerEntity.ridingEntity.updateRiderPosition();
                  }

                  this.serverController.getConfigurationManager().serverUpdateMountedMovingPlayer(this.playerEntity);
                  if (this.playerEntity.ridingEntity != null) {
                     if (d > 4.0) {
                        Entity ridingEntity2 = this.playerEntity.ridingEntity;
                        this.playerEntity.playerNetServerHandler.sendPacket(new S18PacketEntityTeleport(ridingEntity2));
                        this.setPlayerLocation(
                           this.playerEntity.posX,
                           this.playerEntity.posY,
                           this.playerEntity.posZ,
                           this.playerEntity.rotationYaw,
                           this.playerEntity.rotationPitch
                        );
                     }

                     this.playerEntity.ridingEntity.isAirBorne = true;
                  }

                  if (this.hasMoved) {
                     this.lastPosX = this.playerEntity.posX;
                     this.lastPosY = this.playerEntity.posY;
                     this.lastPosZ = this.playerEntity.posZ;
                  }

                  worldServer2.updateEntity(this.playerEntity);
                  return;
               }

               if (this.playerEntity.isPlayerSleeping()) {
                  this.playerEntity.onUpdateEntity();
                  this.playerEntity
                     .setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
                  worldServer2.updateEntity(this.playerEntity);
                  return;
               }

               double posY4 = this.playerEntity.posY;
               this.lastPosX = this.playerEntity.posX;
               this.lastPosY = this.playerEntity.posY;
               this.lastPosZ = this.playerEntity.posZ;
               double posX4 = this.playerEntity.posX;
               double posY5 = this.playerEntity.posY;
               double posZ4 = this.playerEntity.posZ;
               float rotationYaw3 = this.playerEntity.rotationYaw;
               float rotationPitch3 = this.playerEntity.rotationPitch;
               if (packetIn.moving && packetIn.y == -999.0) {
                  packetIn.moving = false;
               }

               if (packetIn.moving) {
                  posX4 = packetIn.x;
                  posY5 = packetIn.y;
                  posZ4 = packetIn.z;
                  if (Math.abs(packetIn.x) > 3.0E7 || Math.abs(packetIn.z) > 3.0E7) {
                     this.kickPlayerFromServer("Illegal position");
                     return;
                  }
               }

               if (packetIn.rotating) {
                  rotationYaw3 = packetIn.yaw;
                  rotationPitch3 = packetIn.pitch;
               }

               this.playerEntity.onUpdateEntity();
               this.playerEntity.setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, rotationYaw3, rotationPitch3);
               if (!this.hasMoved) {
                  return;
               }

               double d5 = posX4 - this.playerEntity.posX;
               double d6 = posY5 - this.playerEntity.posY;
               double d7 = posZ4 - this.playerEntity.posZ;
               double d8 = this.playerEntity.motionX * this.playerEntity.motionX
                  + this.playerEntity.motionY * this.playerEntity.motionY
                  + this.playerEntity.motionZ * this.playerEntity.motionZ;
               double d9 = d5 * d5 + d6 * d6 + d7 * d7;
               if (d9 - d8 > 100.0
                  && (!this.serverController.isSinglePlayer() || !this.serverController.getServerOwner().equals(this.playerEntity.getName()))) {
                  Logger logger2 = logger;
                  String name = this.playerEntity.getName();
                  double d10 = d7;
                  double d11 = d6;
                  double d12 = d5;
                  double d13 = d7;
                  double d14 = d6;
                  double d15 = d5;
                  logger2.warn(name + " moved too quickly! " + d15 + "," + d14 + "," + d13 + " (" + d12 + ", " + d11 + ", " + d10 + ")");
                  this.setPlayerLocation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
                  return;
               }

               float f = 0.0625F;
               boolean empty = worldServer2.getCollidingBoundingBoxes(this.playerEntity, this.playerEntity.getEntityBoundingBox().contract(f, f, f))
                  .isEmpty();
               if (this.playerEntity.onGround && !packetIn.onGround && d6 > 0.0) {
                  this.playerEntity.jump();
               }

               this.playerEntity.moveEntity(d5, d6, d7);
               this.playerEntity.onGround = packetIn.onGround;
               d5 = posX4 - this.playerEntity.posX;
               d6 = posY5 - this.playerEntity.posY;
               if (d6 > -0.5 || d6 < 0.5) {
                  d6 = 0.0;
               }

               d7 = posZ4 - this.playerEntity.posZ;
               d9 = d5 * d5 + d6 * d6 + d7 * d7;
               boolean flag = false;
               if (d9 > 0.0625 && !this.playerEntity.isPlayerSleeping() && !this.playerEntity.theItemInWorldManager.isCreative()) {
                  flag = true;
                  logger.warn(this.playerEntity.getName() + " moved wrongly!");
               }

               this.playerEntity.setPositionAndRotation(posX4, posY5, posZ4, rotationYaw3, rotationPitch3);
               this.playerEntity.addMovementStat(this.playerEntity.posX - posX2, this.playerEntity.posY - posY2, this.playerEntity.posZ - posZ2);
               if (!this.playerEntity.noClip) {
                  boolean empty2 = worldServer2.getCollidingBoundingBoxes(this.playerEntity, this.playerEntity.getEntityBoundingBox().contract(f, f, f))
                     .isEmpty();
                  if (empty && (flag || !empty2) && !this.playerEntity.isPlayerSleeping()) {
                     this.setPlayerLocation(this.lastPosX, this.lastPosY, this.lastPosZ, rotationYaw3, rotationPitch3);
                     return;
                  }
               }

               AxisAlignedBB axisAlignedBB = this.playerEntity.getEntityBoundingBox().expand(f, f, f).addCoord(0.0, -0.55, 0.0);
               if (this.serverController.isFlightAllowed() || this.playerEntity.capabilities.allowFlying || worldServer2.checkBlockCollision(axisAlignedBB)) {
                  this.floatingTickCount = 0;
               } else if (d6 >= -0.03125) {
                  this.floatingTickCount++;
                  if (this.floatingTickCount > 80) {
                     logger.warn(this.playerEntity.getName() + " was kicked for floating too long!");
                     this.kickPlayerFromServer("Flying is not enabled on this server");
                     return;
                  }
               }

               this.playerEntity.onGround = packetIn.onGround;
               this.serverController.getConfigurationManager().serverUpdateMountedMovingPlayer(this.playerEntity);
               this.playerEntity.handleFalling(this.playerEntity.posY - posY4, packetIn.onGround);
            } else if (this.networkTickCount - this.lastPositionUpdate > 20) {
               this.setPlayerLocation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
            }
         }
      }
   }

   @Override
   public void processClientStatus(C16PacketClientStatus packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.markPlayerActive();
      net.minecraft.network.play.client.C16PacketClientStatus.EnumState c16packetclientstatus$enumstate = packetIn.status;
      switch (c16packetclientstatus$enumstate) {
         case PERFORM_RESPAWN:
            if (this.playerEntity.playerConqueredTheEnd) {
               this.playerEntity = this.serverController.getConfigurationManager().getEntityPlayerMP(this.playerEntity, true);
            } else if (this.playerEntity.getServerForPlayer().getWorldInfo().isHardcoreModeEnabled()) {
               if (this.serverController.isSinglePlayer() && this.playerEntity.getName().equals(this.serverController.getServerOwner())) {
                  this.playerEntity.playerNetServerHandler.kickPlayerFromServer("You have died. Game over, man, it's game over!");
                  this.serverController.deleteWorldAndStopServer();
               } else {
                  UserListBansEntry userListBansEntry = new UserListBansEntry(
                     this.playerEntity.getGameProfile(), (Date)null, "(You just lost the game)", (Date)null, "Death in Hardcore"
                  );
                  this.serverController.getConfigurationManager().getBannedPlayers().addEntry(userListBansEntry);
                  this.playerEntity.playerNetServerHandler.kickPlayerFromServer("You have died. Game over, man, it's game over!");
               }
            } else {
               if (this.playerEntity.getHealth() > 0.0F) {
                  return;
               }

               this.playerEntity = this.serverController.getConfigurationManager().getEntityPlayerMP(this.playerEntity, false);
            }
            break;
         case REQUEST_STATS:
            this.playerEntity.getStatFile().sendStats(this.playerEntity);
            break;
         case OPEN_INVENTORY_ACHIEVEMENT:
            this.playerEntity.triggerAchievement(AchievementList.openInventory);
      }
   }

   @Override
   public void processTabComplete(C14PacketTabComplete packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.playerEntity.getServerForPlayer());
      ArrayList arrayList = Lists.newArrayList();

      for (String text2 : this.serverController.getTabCompletions(this.playerEntity, packetIn.message, packetIn.targetBlock)) {
         arrayList.add(text2);
      }

      this.playerEntity.playerNetServerHandler.sendPacket(new S3APacketTabComplete((String[]) arrayList.toArray(new String[arrayList.size()])));
   }

   @Override
   public void processPlayerAbilities(C13PacketPlayerAbilities packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.capabilities.isFlying = packetIn.flying && this.playerEntity.capabilities.allowFlying;
   }

   public void kickPlayerFromServer(String reason) {
      final ChatComponentText chatComponentText = new ChatComponentText(reason);
      this.netManager.sendPacket(new S40PacketDisconnect(chatComponentText), new GenericFutureListener<Future<? super Void>>() {
         @Override
         public void operationComplete(Future<? super Void> future) throws Exception {
            NetHandlerPlayServer.this.netManager.closeChannel(chatComponentText);
         }
      });
      this.netManager.disableAutoRead();
      Futures.getUnchecked(this.serverController.addScheduledTask(new Runnable() {
         @Override
         public void run() {
            NetHandlerPlayServer.this.netManager.checkDisconnected();
         }
      }));
   }

   @Override
   public void processClickWindow(C0EPacketClickWindow packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.markPlayerActive();
      if (this.playerEntity.openContainer.windowId == packetIn.windowId && this.playerEntity.openContainer.getCanCraft(this.playerEntity)) {
         if (this.playerEntity.isSpectator()) {
            ArrayList arrayList = Lists.newArrayList();

            for (int i = 0; i < this.playerEntity.openContainer.inventorySlots.size(); i++) {
               arrayList.add(this.playerEntity.openContainer.inventorySlots.get(i).getStack());
            }

            this.playerEntity.updateCraftingInventory(this.playerEntity.openContainer, arrayList);
         } else {
            ItemStack itemStack2 = this.playerEntity.openContainer.slotClick(packetIn.slotId, packetIn.usedButton, packetIn.mode, this.playerEntity);
            if (ItemStack.areItemStacksEqual(packetIn.clickedItem, itemStack2)) {
               S32PacketConfirmTransaction s32PacketConfirmTransaction = new S32PacketConfirmTransaction(packetIn.windowId, packetIn.actionNumber, true);
               this.playerEntity.playerNetServerHandler.sendPacket(s32PacketConfirmTransaction);
               this.playerEntity.isChangingQuantityOnly = true;
               this.playerEntity.openContainer.detectAndSendChanges();
               this.playerEntity.updateHeldItem();
               this.playerEntity.isChangingQuantityOnly = false;
            } else {
               this.pendingTransactions.addKey(this.playerEntity.openContainer.windowId, packetIn.actionNumber);
               S32PacketConfirmTransaction s32PacketConfirmTransaction2 = new S32PacketConfirmTransaction(packetIn.windowId, packetIn.actionNumber, false);
               this.playerEntity.playerNetServerHandler.sendPacket(s32PacketConfirmTransaction2);
               this.playerEntity.openContainer.setCanCraft(this.playerEntity, false);
               ArrayList arrayList2 = Lists.newArrayList();

               for (int i2 = 0; i2 < this.playerEntity.openContainer.inventorySlots.size(); i2++) {
                  arrayList2.add(this.playerEntity.openContainer.inventorySlots.get(i2).getStack());
               }

               this.playerEntity.updateCraftingInventory(this.playerEntity.openContainer, arrayList2);
            }
         }
      }
   }

   @Override
   public void processChatMessage(C01PacketChatMessage packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.playerEntity.getServerForPlayer());
      if (this.playerEntity.getChatVisibility() == net.minecraft.entity.player.EntityPlayer.EnumChatVisibility.HIDDEN) {
         ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("chat.cannotSend");
         chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.RED);
         this.sendPacket(new S02PacketChat(chatComponentTranslation));
      } else {
         this.playerEntity.markPlayerActive();
         String message2 = packetIn.message;
         message2 = StringUtils.normalizeSpace(message2);

         for (int i = 0; i < message2.length(); i++) {
            if (!ChatAllowedCharacters.isAllowedCharacter(message2.charAt(i))) {
               this.kickPlayerFromServer("Illegal characters in chat");
               return;
            }
         }

         if (message2.startsWith("/")) {
            this.handleSlashCommand(message2);
         } else {
            ChatComponentTranslation chatComponentTranslation2 = new ChatComponentTranslation("chat.type.text", this.playerEntity.getDisplayName(), message2);
            this.serverController.getConfigurationManager().sendChatMsgImpl(chatComponentTranslation2, false);
         }

         this.chatSpamThresholdCount += 20;
         if (this.chatSpamThresholdCount > 200 && !this.serverController.getConfigurationManager().canSendCommands(this.playerEntity.getGameProfile())) {
            this.kickPlayerFromServer("disconnect.spam");
         }
      }
   }

   @Override
   public void processClientSettings(C15PacketClientSettings packetIn) {
      PacketThreadUtil.handlePacket(packetIn, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.handleClientSettings(packetIn);
   }
}
