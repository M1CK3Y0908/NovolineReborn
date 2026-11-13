package net.minecraft.client.entity;

import com.google.common.collect.Iterables;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSoundMinecartRiding;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiCommandBlock;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.potion.Potion;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import wtf.novoline.Novoline;
import wtf.novoline.client.ApiClient;
import wtf.novoline.client.PutRoomsMessagesRequest;
import wtf.novoline.command.CommandManager;
import wtf.novoline.event.OnUpdateWalkingPlayerEvent;
import wtf.novoline.event.PlayerMoveEvent;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.event.SlowdownEvent;
import wtf.novoline.event.UpdateEvent;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.util.Room;
import wtf.novoline.utils.player.PlayerUtils;

public class EntityPlayerSP extends AbstractClientPlayer {
   private int horseJumpPowerCounter;
   public float prevRenderArmPitch;
   private double lastReportedPosX;
   private float horseJumpPower;
   public float prevTimeInPortal;
   private float lastReportedPitch;
   public float renderArmPitch;
   private boolean serverSneakState;
   public float timeInPortal;
   public float renderArmYaw;
   public MovementInput movementInput;
   private int positionUpdateTicks;
   public final NetHandlerPlayClient sendQueue;
   private double lastReportedPosZ;
   public int sprintingTicksLeft;
   private String clientBrand;
   private double lastReportedPosY;
   public float prevRenderArmYaw;
   private boolean serverSprintState;
   private final StatFileWriter statWriter;
   private float lastReportedYaw;
   protected int sprintToggleTimer;
   protected Minecraft mc;
   private boolean hasValidHealth;

   public void respawnPlayer() {
      this.sendQueue.addToSendQueue(new C16PacketClientStatus(net.minecraft.network.play.client.C16PacketClientStatus.EnumState.PERFORM_RESPAWN));
   }

   public void onUpdateWalkingPlayer() {
      boolean sprinting2 = this.isSprinting();
      if (sprinting2 != this.serverSprintState) {
         if (sprinting2) {
            this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, net.minecraft.network.play.client.C0BPacketEntityAction.Action.START_SPRINTING));
         } else {
            this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, net.minecraft.network.play.client.C0BPacketEntityAction.Action.STOP_SPRINTING));
         }

         this.serverSprintState = sprinting2;
      }

      boolean sneaking = this.isSneaking();
      if (sneaking != this.serverSneakState) {
         if (sneaking) {
            this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, net.minecraft.network.play.client.C0BPacketEntityAction.Action.START_SNEAKING));
         } else {
            this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, net.minecraft.network.play.client.C0BPacketEntityAction.Action.STOP_SNEAKING));
         }

         this.serverSneakState = sneaking;
      }

      if (this.isCurrentViewEntity()) {
         AxisAlignedBB entityBoundingBox = this.getEntityBoundingBox();
         PreUpdateEvent preUpdateEvent = new PreUpdateEvent(
            this.posX, entityBoundingBox.minY, this.posZ, this.motionX, this.motionY, this.motionZ, this.rotationYaw, this.rotationPitch, this.onGround
         );
         Novoline.getNovoline().getBus().post(preUpdateEvent);
         if (!preUpdateEvent.isCancelled()) {
            double v = preUpdateEvent.posX - this.lastReportedPosX;
            double d = preUpdateEvent.posY - this.lastReportedPosY;
            double d2 = preUpdateEvent.posZ - this.lastReportedPosZ;
            double d3 = preUpdateEvent.rotationYaw - this.lastReportedYaw;
            double d4 = preUpdateEvent.rotationPitch - this.lastReportedPitch;
            boolean flag = v * v + d * d + d2 * d2 > 9.0E-4 || this.positionUpdateTicks >= 20;
            boolean flag2 = d3 != 0.0 || d4 != 0.0;
            if (this.ridingEntity == null) {
               if (flag && flag2) {
                  this.sendQueue
                     .addToSendQueue(
                        new net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook(preUpdateEvent.posX, preUpdateEvent.posY, preUpdateEvent.posZ, preUpdateEvent.rotationYaw, preUpdateEvent.rotationPitch, preUpdateEvent.onGround)
                     );
               } else if (flag) {
                  this.sendQueue.addToSendQueue(new net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition(preUpdateEvent.posX, preUpdateEvent.posY, preUpdateEvent.posZ, preUpdateEvent.onGround));
               } else if (flag2) {
                  this.sendQueue.addToSendQueue(new net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook(preUpdateEvent.rotationYaw, preUpdateEvent.rotationPitch, preUpdateEvent.onGround));
               } else {
                  this.sendQueue.addToSendQueue(new C03PacketPlayer(preUpdateEvent.onGround));
               }
            } else {
               this.sendQueue
                  .addToSendQueue(
                     new net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook(preUpdateEvent.motionX, -999.0, preUpdateEvent.motionZ, preUpdateEvent.rotationYaw, preUpdateEvent.rotationPitch, preUpdateEvent.onGround)
                  );
               flag = false;
            }

            this.positionUpdateTicks++;
            if (flag) {
               this.lastReportedPosX = preUpdateEvent.posX;
               this.lastReportedPosY = preUpdateEvent.posY;
               this.lastReportedPosZ = preUpdateEvent.posZ;
               this.positionUpdateTicks = 0;
            }

            if (flag2) {
               this.lastReportedYaw = preUpdateEvent.rotationYaw;
               this.lastReportedPitch = preUpdateEvent.rotationPitch;
            }
         }

         Novoline.getNovoline().getBus().post(new OnUpdateWalkingPlayerEvent());
      }
   }

   @Override
   public void closeScreen() {
      this.sendQueue.addToSendQueue(new C0DPacketCloseWindow(this.openContainer.windowId));
      this.closeScreenAndDropStack();
   }

   @Override
   public void displayGui(IInteractionObject guiOwner) {
      String text = guiOwner.getGuiID();
      if ("minecraft:crafting_table".equals(text)) {
         this.mc.displayGuiScreen(new GuiCrafting(this.inventory, this.worldObj));
      } else if ("minecraft:enchanting_table".equals(text)) {
         this.mc.displayGuiScreen(new GuiEnchantment(this.inventory, this.worldObj, guiOwner));
      } else if ("minecraft:anvil".equals(text)) {
         this.mc.displayGuiScreen(new GuiRepair(this.inventory, this.worldObj));
      }
   }

   @Override
   protected void damageEntity(DamageSource damageSrc, float damageAmount) {
      if (!this.isEntityInvulnerable(damageSrc)) {
         this.setHealth(this.getHealth() - damageAmount);
      }
   }

   public void setClientBrand(String brand) {
      this.clientBrand = brand;
   }

   @Override
   protected boolean pushOutOfBlocks(double x, double y, double z) {
      if (this.noClip) {
         return false;
      }

      BlockPos blockPos = new BlockPos(x, y, z);
      double xCoordinate = x - blockPos.getX();
      double zCoordinate = z - blockPos.getZ();
      if (!this.isOpenBlockSpace(blockPos)) {
         int i = -1;
         double d = 9999.0;
         if (this.isOpenBlockSpace(blockPos.west()) && xCoordinate < d) {
            d = xCoordinate;
            i = 0;
         }

         if (this.isOpenBlockSpace(blockPos.east()) && 1.0 - xCoordinate < d) {
            d = 1.0 - xCoordinate;
            i = 1;
         }

         if (this.isOpenBlockSpace(blockPos.north()) && zCoordinate < d) {
            d = zCoordinate;
            i = 4;
         }

         if (this.isOpenBlockSpace(blockPos.south()) && 1.0 - zCoordinate < d) {
            i = 5;
         }

         float f = 0.1F;
         if (i == 0) {
            this.motionX = -f;
         }

         if (i == 1) {
            this.motionX = f;
         }

         if (i == 4) {
            this.motionZ = -f;
         }

         if (i == 5) {
            this.motionZ = f;
         }
      }

      return false;
   }

   @Override
   protected void joinEntityItemWithWorld(EntityItem itemIn) {
   }

   public EntityPlayerSP(Minecraft mcIn, World worldIn, NetHandlerPlayClient netHandler, StatFileWriter statFile) {
      super(worldIn, netHandler.getGameProfile());
      this.sendQueue = netHandler;
      this.statWriter = statFile;
      this.mc = mcIn;
      this.dimension = 0;
   }

   public void setPlayerSPHealth(float health) {
      if (this.hasValidHealth) {
         float f = this.getHealth() - health;
         if (f <= 0.0F) {
            this.setHealth(health);
            if (f < 0.0F) {
               this.hurtResistantTime = this.maxHurtResistantTime / 2;
            }
         } else {
            this.lastDamage = f;
            this.setHealth(this.getHealth());
            this.hurtResistantTime = this.maxHurtResistantTime;
            this.damageEntity(DamageSource.generic, f);
            this.hurtTime = this.maxHurtTime = 10;
         }
      } else {
         this.setHealth(health);
         this.hasValidHealth = true;
      }
   }

   @Override
   public void openEditSign(TileEntitySign signTile) {
      this.mc.displayGuiScreen(new GuiEditSign(signTile));
   }

   @Override
   public void playSound(String name, float volume, float pitch) {
      this.worldObj.playSound(this.posX, this.posY, this.posZ, name, volume, pitch, false);
   }

   public void sendHorseInventory() {
      this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, net.minecraft.network.play.client.C0BPacketEntityAction.Action.OPEN_INVENTORY));
   }

   public void sendChatMessage(String message) {
      CommandManager commandManager = Novoline.getNovoline().getCommandManager();
      if (message.startsWith(".")) {
         commandManager.getCommandDispatcher().handle(message);
      } else if (GuiNewChat.selectedRoom > 0) {
         ApiClient apiClient = Novoline.getNovoline().getIrcConnection().getApiClient();
         Room room = (Room)Iterables.get(Novoline.getNovoline().getIrcConnection().getGatewayClient().getRooms().values(), GuiNewChat.selectedRoom - 1);
         apiClient.sendRoomMessage(PutRoomsMessagesRequest.createPutRoomsMessagesRequest(room.getId(), message)).whenComplete((gatewayChatMessage, throwable) -> {
            if (throwable != null) {
               String text2 = throwable.getCause() == null ? throwable.getMessage() : throwable.getCause().getMessage();
               Novoline.getNovoline().sendChatMessage(EnumChatFormatting.RED + "Error sending message: " + text2);
            }
         });
      } else {
         this.sendQueue.addToSendQueue(new C01PacketChatMessage(message));
      }
   }

   @Override
   public void onEnchantmentCritical(Entity entity2) {
      this.mc.effectRenderer.emitParticleAtEntity(entity2, EnumParticleTypes.CRIT_MAGIC);
   }

   @Override
   public void displayGUIBook(ItemStack bookStack) {
      if (bookStack.getItem() == Items.writable_book) {
         this.mc.displayGuiScreen(new GuiScreenBook(this, bookStack, true));
      }
   }

   @Override
   public void onLivingUpdate() {
      if (this.sprintingTicksLeft > 0) {
         this.sprintingTicksLeft--;
         if (this.sprintingTicksLeft == 0) {
            this.setSprinting(false);
         }
      }

      if (this.sprintToggleTimer > 0) {
         this.sprintToggleTimer--;
      }

      this.prevTimeInPortal = this.timeInPortal;
      if (this.inPortal) {
         if (this.mc.currentScreen != null && !this.mc.currentScreen.doesGuiPauseGame()) {
            this.mc.displayGuiScreen((GuiScreen)null);
         }

         if (this.timeInPortal == 0.0F) {
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("portal.trigger"), this.rand.nextFloat() * 0.4F + 0.8F));
         }

         this.timeInPortal += 0.0125F;
         if (this.timeInPortal >= 1.0F) {
            this.timeInPortal = 1.0F;
         }

         this.inPortal = false;
      } else if (this.isPotionActive(Potion.confusion) && this.getActivePotionEffect(Potion.confusion).getDuration() > 60) {
         this.timeInPortal += 0.006666667F;
         if (this.timeInPortal > 1.0F) {
            this.timeInPortal = 1.0F;
         }
      } else {
         if (this.timeInPortal > 0.0F) {
            this.timeInPortal -= 0.05F;
         }

         if (this.timeInPortal < 0.0F) {
            this.timeInPortal = 0.0F;
         }
      }

      if (this.timeUntilPortal > 0) {
         this.timeUntilPortal--;
      }

      boolean jump2 = this.movementInput.jump;
      boolean sneak2 = this.movementInput.sneak;
      float f = 0.8F;
      boolean flag = this.movementInput.moveForward >= f;
      this.movementInput.updatePlayerMoveState();
      if (this.isUsingItem() && !this.isRiding()) {
         SlowdownEvent slowdownEvent = new SlowdownEvent();
         Novoline.getNovoline().getBus().post(slowdownEvent);
         if (!slowdownEvent.isCancelled()) {
            this.movementInput.moveStrafe = this.movementInput.moveStrafe * slowdownEvent.strafeMultiplier;
            this.movementInput.moveForward = this.movementInput.moveForward * slowdownEvent.forwardMultiplier;
            this.sprintToggleTimer = 0;
         }
      }

      this.pushOutOfBlocks(this.posX - this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ + this.width * 0.35);
      this.pushOutOfBlocks(this.posX - this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ - this.width * 0.35);
      this.pushOutOfBlocks(this.posX + this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ - this.width * 0.35);
      this.pushOutOfBlocks(this.posX + this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ + this.width * 0.35);
      boolean flag2 = this.getFoodStats().getFoodLevel() > 6.0F || this.capabilities.allowFlying;
      if (this.onGround
         && !sneak2
         && !flag
         && this.movementInput.moveForward >= f
         && !this.isSprinting()
         && flag2
         && !this.isUsingItem()
         && !this.isPotionActive(Potion.blindness)) {
         if (this.sprintToggleTimer <= 0 && !this.mc.gameSettings.keyBindSprint.isKeyDown()) {
            this.sprintToggleTimer = 7;
         } else {
            this.setSprinting(true);
         }
      }

      if (!this.isSprinting()
         && this.movementInput.moveForward >= f
         && flag2
         && !this.isUsingItem()
         && !this.isPotionActive(Potion.blindness)
         && this.mc.gameSettings.keyBindSprint.isKeyDown()) {
         this.setSprinting(true);
      }

      if (this.isSprinting()
         && (this.movementInput.moveForward < f || this.isCollidedHorizontally || !flag2)
         && (!ModuleManager.sprint.isEnabled() || ModuleManager.sprint.getSprint().getCurrentElement() == 0 || !PlayerUtils.isMoving())) {
         this.setSprinting(false);
      }

      if (this.capabilities.allowFlying) {
         if (this.mc.playerController.isSpectatorMode()) {
            if (!this.capabilities.isFlying) {
               this.capabilities.isFlying = true;
               this.sendPlayerAbilities();
            }
         } else if (!jump2 && this.movementInput.jump) {
            if (this.flyToggleTimer == 0) {
               this.flyToggleTimer = 7;
            } else {
               this.capabilities.isFlying = !this.capabilities.isFlying;
               this.sendPlayerAbilities();
               this.flyToggleTimer = 0;
            }
         }
      }

      if (this.capabilities.isFlying && this.isCurrentViewEntity()) {
         if (this.movementInput.sneak) {
            this.motionY = this.motionY - this.capabilities.getFlySpeed() * 3.0F;
         }

         if (this.movementInput.jump) {
            this.motionY = this.motionY + this.capabilities.getFlySpeed() * 3.0F;
         }
      }

      if (this.isRidingHorse()) {
         if (this.horseJumpPowerCounter < 0) {
            this.horseJumpPowerCounter++;
            if (this.horseJumpPowerCounter == 0) {
               this.horseJumpPower = 0.0F;
            }
         }

         if (jump2 && !this.movementInput.jump) {
            this.horseJumpPowerCounter = -10;
            this.sendHorseJump();
         } else if (!jump2 && this.movementInput.jump) {
            this.horseJumpPowerCounter = 0;
            this.horseJumpPower = 0.0F;
         } else if (jump2) {
            this.horseJumpPowerCounter++;
            if (this.horseJumpPowerCounter < 10) {
               this.horseJumpPower = this.horseJumpPowerCounter * 0.1F;
            } else {
               this.horseJumpPower = 0.8F + 2.0F / (this.horseJumpPowerCounter - 9) * 0.1F;
            }
         }
      } else {
         this.horseJumpPower = 0.0F;
      }

      super.onLivingUpdate();
      if (this.onGround && this.capabilities.isFlying && !this.mc.playerController.isSpectatorMode()) {
         this.capabilities.isFlying = false;
         this.sendPlayerAbilities();
      }
   }

   @Override
   public void setSprinting(boolean sprinting) {
      super.setSprinting(sprinting);
      this.sprintingTicksLeft = sprinting ? 600 : 0;
   }

   @Override
   public BlockPos getPosition() {
      return new BlockPos(this.posX + 0.5, this.posY + 0.5, this.posZ + 0.5);
   }

   public void sendPlayerAbilities() {
      this.sendQueue.addToSendQueue(new C13PacketPlayerAbilities(this.capabilities));
   }

   @Override
   public void addChatMessage(IChatComponent component) {
      this.mc.ingameGUI.getChatGUI().printChatMessage(component);
   }

   public StatFileWriter getStatFileWriter() {
      return this.statWriter;
   }

   @Override
   public void displayVillagerTradeGui(IMerchant villager) {
      this.mc.displayGuiScreen(new GuiMerchant(this.inventory, villager, this.worldObj));
   }

   @Override
   public void displayGUIChest(IInventory chestInventory) {
      String text = chestInventory instanceof IInteractionObject ? ((IInteractionObject)chestInventory).getGuiID() : "minecraft:container";
      if ("minecraft:chest".equals(text)) {
         this.mc.displayGuiScreen(new GuiChest(this.inventory, chestInventory));
      } else if ("minecraft:hopper".equals(text)) {
         this.mc.displayGuiScreen(new GuiHopper(this.inventory, chestInventory));
      } else if ("minecraft:furnace".equals(text)) {
         this.mc.displayGuiScreen(new GuiFurnace(this.inventory, chestInventory));
      } else if ("minecraft:brewing_stand".equals(text)) {
         this.mc.displayGuiScreen(new GuiBrewingStand(this.inventory, chestInventory));
      } else if ("minecraft:beacon".equals(text)) {
         this.mc.displayGuiScreen(new GuiBeacon(this.inventory, chestInventory));
      } else if (!"minecraft:dispenser".equals(text) && !"minecraft:dropper".equals(text)) {
         this.mc.displayGuiScreen(new GuiChest(this.inventory, chestInventory));
      } else {
         this.mc.displayGuiScreen(new GuiDispenser(this.inventory, chestInventory));
      }
   }

   @Override
   public boolean isUser() {
      return true;
   }

   public String getClientBrand() {
      return this.clientBrand;
   }

   public void setXPStats(float currentXP, int maxXP, int level) {
      this.experience = currentXP;
      this.experienceTotal = maxXP;
      this.experienceLevel = level;
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      return false;
   }

   @Override
   public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
      return permLevel <= 0;
   }

   @Override
   public void swingItemNoPacket() {
      super.swingItemNoPacket();
      this.sendQueue.addToSendQueue(new C0APacketAnimation());
   }

   @Override
   public boolean isSneaking() {
      return (this.movementInput != null ? this.movementInput.sneak : false) && !this.sleeping;
   }

   public boolean isRidingHorse() {
      return this.ridingEntity != null && this.ridingEntity instanceof EntityHorse && ((EntityHorse)this.ridingEntity).isHorseSaddled();
   }

   public float getHorseJumpPower() {
      return this.horseJumpPower;
   }

   @Override
   public void openEditCommandBlock(CommandBlockLogic cmdBlockLogic) {
      this.mc.displayGuiScreen(new GuiCommandBlock(cmdBlockLogic));
   }

   @Override
   public void onUpdate() {
      if (this.worldObj.isBlockLoaded(new BlockPos(this.posX, 0.0, this.posZ))) {
         Novoline.getNovoline().getBus().post(new UpdateEvent());
         super.onUpdate();
         if (this.isRiding()) {
            this.sendQueue.addToSendQueue(new net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook(this.rotationYaw, this.rotationPitch, this.onGround));
            this.sendQueue.addToSendQueue(new C0CPacketInput(this.moveStrafing, this.moveForward, this.movementInput.jump, this.movementInput.sneak));
         } else {
            this.onUpdateWalkingPlayer();
         }
      }
   }

   protected void sendHorseJump() {
      this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, net.minecraft.network.play.client.C0BPacketEntityAction.Action.RIDING_JUMP, (int)(this.getHorseJumpPower() * 100.0F)));
   }

   @Override
   public void addStat(StatBase stat, int amount) {
      if (stat != null && stat.isIndependent) {
         super.addStat(stat, amount);
      }
   }

   private boolean isOpenBlockSpace(BlockPos pos) {
      return !this.worldObj.getBlockState(pos).getBlock().isNormalCube() && !this.worldObj.getBlockState(pos.up()).getBlock().isNormalCube();
   }

   @Override
   public void onCriticalHit(Entity entity2) {
      this.mc.effectRenderer.emitParticleAtEntity(entity2, EnumParticleTypes.CRIT);
   }

   @Override
   public void addChatComponentMessage(IChatComponent chatComponent) {
      this.mc.ingameGUI.getChatGUI().printChatMessage(chatComponent);
   }

   @Override
   public void moveEntity(double d, double d2, double d3) {
      PlayerMoveEvent playerMoveEvent = new PlayerMoveEvent(d, d2, d3);
      Novoline.getNovoline().getBus().post(playerMoveEvent);
      if (!playerMoveEvent.isCancelled()) {
         super.moveEntity(playerMoveEvent.x, playerMoveEvent.y, playerMoveEvent.z);
      }
   }

   @Override
   public void displayGUIHorse(EntityHorse horse, IInventory horseInventory) {
      this.mc.displayGuiScreen(new GuiScreenHorseInventory(this.inventory, horseInventory, horse));
   }

   @Override
   public boolean isServerWorld() {
      return true;
   }

   @Override
   public void updateEntityActionState() {
      super.updateEntityActionState();
      if (this.isCurrentViewEntity()) {
         this.moveStrafing = this.movementInput.moveStrafe;
         this.moveForward = this.movementInput.moveForward;
         this.isJumping = this.movementInput.jump;
         this.prevRenderArmYaw = this.renderArmYaw;
         this.prevRenderArmPitch = this.renderArmPitch;
         this.renderArmPitch = (float)(this.renderArmPitch + (this.rotationPitch - this.renderArmPitch) * 0.5);
         this.renderArmYaw = (float)(this.renderArmYaw + (this.rotationYaw - this.renderArmYaw) * 0.5);
      }
   }

   @Override
   public void heal(float healAmount) {
   }

   public void closeScreenAndDropStack() {
      this.inventory.setItemStack((ItemStack)null);
      super.closeScreen();
      this.mc.displayGuiScreen((GuiScreen)null);
   }

   @Override
   public EntityItem dropOneItem(boolean dropAll) {
      net.minecraft.network.play.client.C07PacketPlayerDigging.Action action = dropAll ? net.minecraft.network.play.client.C07PacketPlayerDigging.Action.DROP_ALL_ITEMS : net.minecraft.network.play.client.C07PacketPlayerDigging.Action.DROP_ITEM;
      this.sendQueue.addToSendQueue(new C07PacketPlayerDigging(action, BlockPos.ORIGIN, EnumFacing.DOWN));
      return null;
   }

   @Override
   public void mountEntity(Entity entityIn) {
      super.mountEntity(entityIn);
      if (entityIn instanceof EntityMinecart) {
         this.mc.getSoundHandler().playSound(new MovingSoundMinecartRiding(this, (EntityMinecart)entityIn));
      }
   }

   protected boolean isCurrentViewEntity() {
      return this.mc.getRenderViewEntity() == this;
   }
}
