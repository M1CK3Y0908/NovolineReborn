package wtf.novoline.module.world;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C0BPacketEntityAction.Action;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import wtf.novoline.enums.ServerType;
import wtf.novoline.event.GameEndEvent;
import wtf.novoline.event.LoadWorldEvent;
import wtf.novoline.event.MoveEntityEvent;
import wtf.novoline.event.MoveFlyingEvent;
import wtf.novoline.event.OnUpdateWalkingPlayerEvent;
import wtf.novoline.event.PlayerMoveEvent;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.event.SyncCurrentSlotEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.setting.MultiComboSetting.Option;
import wtf.novoline.utils.combat.RotationUtils;
import wtf.novoline.utils.hypixel.HypixelState;
import wtf.novoline.utils.language.Language;
import wtf.novoline.utils.notification.NotificationType;
import wtf.novoline.utils.player.InventoryUtils;
import wtf.novoline.utils.player.PlayerUtils;
import wtf.novoline.utils.time.Stopwatch;

public class Scaffold extends Module {
   private final ComboSetting scaffold = settingManager.register(new ComboSetting(this, "Mode", 0, "Normal"));
   public final MultiComboSetting rotationsAddons = settingManager.register(
      new MultiComboSetting(this, "Rotations addons").add("Look delay", false).add("Custom look speed", true).add("Randomize rotations", false)
   );
   public final MultiComboSetting moveAddons = settingManager.register(
      new MultiComboSetting(this, "Move addons")
         .add("Tower", true)
         .add("Safe walk", true)
         .add("Downward", false)
         .add("Same Y", false)
         .add("Timer boost", false)
         .add("Stop sprinting", true)
         .add("Rotation strafe", false)
         .add("Speed boost", false)
         .add("Low block slowdown", false)
         .add("Randomize delay", false)
         .add("Edge sneaking", false)
   );
   private final MultiComboSetting itemAddons = settingManager.register(
      new MultiComboSetting(this, "Item addons").add("Swing", true).add("Silent swing", false).add("Pick block", true)
   );
   private final MultiComboSetting disableRules = settingManager.register(
      new MultiComboSetting(this, "Disable rules").add("World change", true).add("Death", true).add("Game end", true)
   );
   private final ComboSetting pickMode = settingManager.register(new ComboSetting(this, "Pick mode", 1, "Switch", "Silent") {
      @Override
      public boolean shouldShow() {
         return Scaffold.this.itemAddons.getElements().get(2).isSelected();
      }
   });
   private final ComboSetting towerMode = settingManager.register(new ComboSetting(this, "Tower mode", 0, "Normal", "Double block", "Teleport") {
      @Override
      public boolean shouldShow() {
         return Scaffold.this.moveAddons.isSelected(0);
      }
   });
   private float appliedYaw;
   private final NumberSetting delay = settingManager.register(new NumberSetting(this, "Delay", 40, 0, 400) {
      @Override
      public boolean shouldShow() {
         return !Scaffold.this.minDelay.shouldShow();
      }
   });
   private final NumberSetting minDelay = settingManager.register(new NumberSetting(this, "Min delay", 40, 0, 400) {
      @Override
      public boolean shouldShow() {
         return Scaffold.this.moveAddons.isSelected(9);
      }
   });
   private final NumberSetting maxDelay = settingManager.register(new NumberSetting(this, "Max delay", 50, 0, 400) {
      @Override
      public boolean shouldShow() {
         return Scaffold.this.minDelay.shouldShow();
      }
   });
   private boolean miss;
   private final NumberSetting lookSpeedValue = settingManager.register(new NumberSetting(this, "Look speed value", 75.0F, 0.0F, 360.0F) {
      @Override
      public boolean shouldShow() {
         return Scaffold.this.rotationsAddons.isSelected(1);
      }
   });
   private final NumberSetting timerBoostWalk = settingManager.register(new NumberSetting(this, "Timer boost walk", 1.4F, 1.0F, 2.0F) {
      @Override
      public boolean shouldShow() {
         return Scaffold.this.moveAddons.getElements().get(4).isSelected();
      }
   });
   private final NumberSetting timerBoostTower = settingManager.register(new NumberSetting(this, "Timer boost tower", 1.7F, 1.0F, 2.0F) {
      @Override
      public boolean shouldShow() {
         List elements = Scaffold.this.moveAddons.getElements();
         return ((Option)elements.get(4)).isSelected() && ((Option)elements.get(0)).isSelected();
      }
   });
   private final NumberSetting speedBoost = settingManager.register(new NumberSetting(this, "Speed boost", 1.5F, 1.0F, 5.0F) {
      @Override
      public boolean shouldShow() {
         return Scaffold.this.moveAddons.isSelected(7);
      }
   });
   public final CheckboxSetting blockCounter = settingManager.register(new CheckboxSetting(this, "Block counter", true));
   public final CheckboxSetting autoJump = settingManager.register(new CheckboxSetting(this, "Auto jump", false) {
      @Override
      public boolean shouldShow() {
         return Scaffold.this.moveAddons.getElements().get(3).isSelected();
      }
   });
   public final CheckboxSetting hideJumps = settingManager.register(new CheckboxSetting(this, "Hide jumps", false) {
      @Override
      public boolean shouldShow() {
         return Scaffold.this.autoJump.isEnabled() && Scaffold.this.moveAddons.getElements().get(3).isSelected();
      }
   });
   private final Stopwatch placeTimer = new Stopwatch();
   private double groundY;
   private static final Map<EnumFacing, Vec3i> neighborOffsets = Map.of(
      EnumFacing.UP,
      new Vec3i(0, -1, 0),
      EnumFacing.EAST,
      new Vec3i(-1, 0, 0),
      EnumFacing.WEST,
      new Vec3i(1, 0, 0),
      EnumFacing.SOUTH,
      new Vec3i(0, 0, -1),
      EnumFacing.NORTH,
      new Vec3i(0, 0, 1)
   );
   private int pickedSlot = -1;
   private int originalSlot = -1;
   private int towerAirTicks = 0;
   private BlockPos pos;
   private int placeCount = 0;
   private int randomDelay = 0;
   private float targetPitch;
   @EventLink
   public final Listener<GameEndEvent> onGameEnd = gameEndEvent -> {
      if (this.disableRules.isSelected(2)) {
         this.setEnabled(false);
         this.showNotification(NotificationType.INFO, "Scaffold", Language.translate("feature.scaffold.disable_rules.notification.game_end"), 2500L);
      }
   };
   @EventLink
   public final Listener<LoadWorldEvent> onLoadWorld = loadWorldEvent -> {
      if (this.disableRules.isSelected(0)) {
         this.setEnabled(false);
         this.showNotification(NotificationType.INFO, "Scaffold", Language.translate("feature.scaffold.disable_rules.notification.world_change"), 2500L);
      }
   };
   @EventLink
   public final Listener<SyncCurrentSlotEvent> onSyncCurrentSlot = syncCurrentSlotEvent -> {
      if (this.originalSlot != -1 && this.pickMode.getCurrentElement() == 1) {
         syncCurrentSlotEvent.cancel();
         this.originalSlot = syncCurrentSlotEvent.clientSlot;
      }
   };
   @EventLink
   public final Listener<MoveEntityEvent> onMoveEntity = moveEntityEvent -> {
      if (this.moveAddons.getElements().get(1).isSelected()) {
         moveEntityEvent.cancel();
      }
   };
   private final Stopwatch speedBoostTimer = new Stopwatch();
   @EventLink
   public final Listener<PlayerMoveEvent> onPlayerMove = playerMoveEvent -> {
      double d = 0.2;
      if (minecraft.thePlayer.onGround && minecraft.thePlayer.isPotionActive(Potion.moveSpeed)) {
         playerMoveEvent.setSpeed(d);
      }

      List elements = this.moveAddons.getElements();
      if (((Option)elements.get(0)).isSelected() && this.towerMode.getCurrentElement() == 0 && minecraft.gameSettings.keyBindJump.isKeyDown()) {
         if (PlayerUtils.hasCollisionBelow(0.76)
            && !PlayerUtils.hasCollisionBelow(0.75)
            && minecraft.thePlayer.motionY > 0.23
            && minecraft.thePlayer.motionY < 0.25) {
            playerMoveEvent.y = minecraft.thePlayer.motionY = Math.round(minecraft.thePlayer.posY) - minecraft.thePlayer.posY;
         }

         if (PlayerUtils.hasCollisionBelow(1.0E-4)) {
            playerMoveEvent.y = minecraft.thePlayer.motionY = 0.41999998688698;
         }
      }

      if (((Option)elements.get(7)).isSelected()) {
         int i = ThreadLocalRandom.current().nextInt(105, 125);
         if (minecraft.thePlayer.onGround && PlayerUtils.isMoving() && this.speedBoostTimer.hasElapsed(i)) {
            playerMoveEvent.setSpeed(d = PlayerUtils.getDefaultMoveSpeed() * this.speedBoost.getCurrentValue());
            this.speedBoostTimer.reset();
         }
      }

      if (this.moveAddons.isSelected(8)) {
         int i2 = this.itemAddons.isSelected(2) && this.pickedSlot != -1 ? this.pickedSlot : minecraft.thePlayer.inventory.currentItem;
         ItemStack itemStack = minecraft.thePlayer.inventory.mainInventory[i2];
         if (itemStack != null && itemStack.getItem() instanceof ItemBlock && itemStack.stackSize < 10) {
            float f = (10 - itemStack.stackSize) * 7;
            d -= d * f / 100.0;
         }

         if (!ModuleManager.speed.isEnabled()) {
            playerMoveEvent.setSpeed(d);
         }
      }
   };
   private float targetYaw;
   @EventLink
   public final Listener<OnUpdateWalkingPlayerEvent> onOnUpdateWalkingPlayer = onUpdateWalkingPlayerEvent -> this.appliedYaw = this.targetYaw;
   @EventLink
   public final Listener<MoveFlyingEvent> onMoveFlying = moveFlyingEvent -> {
      if (this.moveAddons.isSelected(6)) {
         moveFlyingEvent.cancel();
         PlayerUtils.correctMovement(moveFlyingEvent, this.appliedYaw);
      }

      ItemStack heldItem = minecraft.thePlayer.getHeldItem();
      if (this.pickedSlot != -1) {
         heldItem = minecraft.thePlayer.inventory.mainInventory[this.pickedSlot];
      }

      if (heldItem != null && heldItem.getItem() instanceof ItemBlock) {
         BlockPos blockPos = new BlockPos(minecraft.thePlayer.posX, this.groundY - 1.0, minecraft.thePlayer.posZ);
         MovingObjectPosition movingObjectPosition = this.rayTraceBlock(this.appliedYaw, this.targetPitch);
         if (movingObjectPosition == null || movingObjectPosition.typeOfHit != MovingObjectType.BLOCK || !movingObjectPosition.getBlockPos().equals(this.pos) || !movingObjectPosition.sideHit.equals(this.facing)) {
            this.placeTimer.reset();
         } else if (minecraft.theWorld.getBlockState(blockPos).getBlock() instanceof BlockAir) {
            int i = this.moveAddons.isSelected(9) ? this.randomDelay : (int)this.delay.getCurrentValue();
            if (this.placeTimer.hasElapsed(i)) {
               if (minecraft.playerController
                  .performRightClick(minecraft.thePlayer, minecraft.theWorld, heldItem, movingObjectPosition.getBlockPos(), movingObjectPosition.sideHit, movingObjectPosition.hitVec)) {
                  this.placeCount++;
                  minecraft.thePlayer.swingItemNoPacket();
                  if (this.moveAddons.isSelected(10)) {
                     minecraft.thePlayer.movementInput.sneak = false;
                  }
               }

               this.placeTimer.reset();
               int i2 = (int)this.minDelay.getCurrentValue();
               int i3 = (int)this.maxDelay.getCurrentValue();
               if (i2 == i3) {
                  i3++;
               }

               this.randomDelay = ThreadLocalRandom.current().nextInt(Math.min(i2, i3), Math.max(i2, i3));
               if (this.towerMode.getCurrentElement() == 1
                  && this.placeCount % 2 != 0
                  && this.moveAddons.isSelected(0)
                  && minecraft.gameSettings.keyBindJump.isKeyDown()) {
                  minecraft.thePlayer.motionX = minecraft.thePlayer.motionZ = 0.0;
                  minecraft.thePlayer.jump();
               }
            } else if (this.moveAddons.isSelected(10)) {
               minecraft.thePlayer.movementInput.sneak = true;
            }
         }
      } else {
         this.placeTimer.reset();
      }
   };
   private EnumFacing facing;
   @EventLink
   public final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      this.setSuffix(this.scaffold.getCurrentElementAsString());
      if (!minecraft.thePlayer.isEntityAlive() && this.disableRules.getElements().get(1).isSelected() && this.disableRules.isSelected(1)) {
         this.setEnabled(false);
         this.showNotification(NotificationType.INFO, "Scaffold", Language.translate("feature.scaffold.disable_rules.notification.death"), 2500L);
      }

      if (this.autoJump.isEnabled() && this.moveAddons.getElements().get(3).isSelected() && !ModuleManager.speed.isEnabled()) {
         if (this.hideJumps.isEnabled()) {
            minecraft.thePlayer.posY = minecraft.thePlayer.posY - (minecraft.thePlayer.posY - minecraft.thePlayer.lastTickPosY);
            minecraft.thePlayer.lastTickPosY = minecraft.thePlayer.lastTickPosY - (minecraft.thePlayer.posY - minecraft.thePlayer.lastTickPosY);
         }

         if (minecraft.thePlayer.onGround) {
            minecraft.thePlayer.jump();
         }
      }

      if (this.miss) {
         this.miss = false;
         minecraft.timer.timerSpeed = 1.0F;
      }

      List elements = this.moveAddons.getElements();
      if (((Option)elements.get(4)).isSelected()) {
         if (minecraft.thePlayer.onGround && PlayerUtils.isMoving()) {
            minecraft.timer.timerSpeed = this.timerBoostWalk.getCurrentValue();
         } else {
            minecraft.timer.timerSpeed = 1.0F;
         }
      }

      if (((Option)elements.get(5)).isSelected()) {
         if (minecraft.thePlayer.isSprinting()) {
            this.sendPacketUnregistered(new C0BPacketEntityAction(minecraft.thePlayer, Action.STOP_SPRINTING));
         }

         minecraft.thePlayer.setSprinting(false);
         KeyBinding.setKeyBindState(minecraft.gameSettings.keyBindSprint.getKeyCode(), false);
      }

      if (!((Option)elements.get(3)).isSelected() || minecraft.thePlayer.onGround) {
         this.groundY = minecraft.thePlayer.posY;
      }

      if (((Option)elements.get(0)).isSelected() && minecraft.gameSettings.keyBindJump.isKeyDown()) {
         if (((Option)elements.get(4)).isSelected()) {
            minecraft.timer.timerSpeed = this.timerBoostTower.getCurrentValue();
            this.miss = true;
         }

         if (this.towerMode.getCurrentElement() == 0) {
            double d = MathHelper.floor_double(preUpdateEvent.posY);
            preUpdateEvent.posX = preUpdateEvent.posX
               + (
                  minecraft.thePlayer.ticksExisted % 2 == 0
                     ? ThreadLocalRandom.current().nextDouble(0.03, 0.03125)
                     : -ThreadLocalRandom.current().nextDouble(0.03, 0.03125)
               );
            preUpdateEvent.posZ = preUpdateEvent.posZ
               + (
                  minecraft.thePlayer.ticksExisted % 2 != 0
                     ? ThreadLocalRandom.current().nextDouble(0.03, 0.03125)
                     : -ThreadLocalRandom.current().nextDouble(0.03, 0.03125)
               );
            double d2 = this.roundToFourDecimals(preUpdateEvent.posY % 1.0);
            if (d2 == 0.42 && HypixelState.isServerType(ServerType.HYPIXEL)) {
               this.sendPacketUnregistered(
                  new C06PacketPlayerPosLook(preUpdateEvent.posX, d + 0.015625, preUpdateEvent.posZ, this.targetYaw, this.targetPitch, preUpdateEvent.onGround)
               );
               this.sendPacketUnregistered(
                  new C06PacketPlayerPosLook(preUpdateEvent.posX, d + 0.089899898876, preUpdateEvent.posZ, this.targetYaw, this.targetPitch, preUpdateEvent.onGround)
               );
               this.sendPacketUnregistered(new C06PacketPlayerPosLook(preUpdateEvent.posX, d, preUpdateEvent.posZ, this.targetYaw, this.targetPitch, preUpdateEvent.onGround));
            }

            if (!preUpdateEvent.onGround && preUpdateEvent.posY == d) {
               preUpdateEvent.posY += 0.00133597911216;
            }
         } else if (this.towerMode.getCurrentElement() == 2) {
            if (this.towerAirTicks == 2) {
               double d3 = minecraft.thePlayer.ticksExisted % 2 == 0 ? 0.1 : 0.3;
               minecraft.thePlayer.setPositionAndUpdate(minecraft.thePlayer.posX, minecraft.thePlayer.posY + d3, minecraft.thePlayer.posZ);
            }

            if (minecraft.thePlayer.onGround) {
               this.towerAirTicks = 0;
            } else {
               this.towerAirTicks++;
            }
         }
      }

      List elements2 = this.itemAddons.getElements();
      if (((Option)elements2.get(2)).isSelected()) {
         ItemStack heldItem = minecraft.thePlayer.getHeldItem();
         if (heldItem == null || !(heldItem.getItem() instanceof ItemBlock) || !((ItemBlock)heldItem.getItem()).getBlock().isFullCube()) {
            int blockSlot = InventoryUtils.findBlockSlot();
            if (blockSlot != -1 && blockSlot < 9) {
               if (minecraft.thePlayer.inventory.currentItem != blockSlot) {
                  int currentElement = this.pickMode.getCurrentElement();
                  if (currentElement == 0) {
                     this.sendPacketUnregistered(new C09PacketHeldItemChange(minecraft.thePlayer.inventory.currentItem = blockSlot));
                  } else if (currentElement == 1 && this.pickedSlot != blockSlot) {
                     this.originalSlot = minecraft.thePlayer.inventory.currentItem;
                     this.sendPacketUnregistered(new C09PacketHeldItemChange(this.pickedSlot = blockSlot));
                  }
               }
            } else if (this.originalSlot != -1) {
               this.sendPacketUnregistered(new C09PacketHeldItemChange(minecraft.thePlayer.inventory.currentItem = this.originalSlot));
               this.originalSlot = -1;
            }
         }
      }

      BlockPos blockPos = new BlockPos(minecraft.thePlayer.posX, this.groundY - 1.0, minecraft.thePlayer.posZ);
      Block block2 = minecraft.theWorld.getBlockState(blockPos).getBlock();
      this.setPos(blockPos);
      this.targetPitch = 80.824646F;
      if (this.pos != null && block2 == Blocks.air) {
         MovingObjectPosition movingObjectPosition = this.rayTraceBlock(this.targetYaw, this.targetPitch);
         boolean flag = false;
         if (movingObjectPosition != null && movingObjectPosition.typeOfHit == MovingObjectType.BLOCK && movingObjectPosition.getBlockPos().equals(this.pos) && movingObjectPosition.sideHit.equals(this.facing)) {
            flag = true;
         }

         if (!flag) {
            this.placeTimer.reset();
            boolean flag2 = false;
            float f = Float.MAX_VALUE;

            for (int i = -360; i <= 360; i += 2) {
               MovingObjectPosition movingObjectPosition2 = this.rayTraceBlock(i, this.targetPitch);
               if (movingObjectPosition2 != null
                  && movingObjectPosition2.typeOfHit == MovingObjectType.BLOCK
                  && movingObjectPosition2.getBlockPos().equals(this.pos)
                  && movingObjectPosition2.sideHit.equals(this.facing)
                  && f > Math.abs(this.targetYaw - i)) {
                  f = i;
                  flag2 = true;
               }
            }

            if (!flag2) {
               float[] rotationsToFace = RotationUtils.getRotationsToFace(new Vec3(this.pos.getX(), this.pos.getY(), this.pos.getZ()), this.facing, true);
               this.targetPitch = rotationsToFace[1];
               f = rotationsToFace[0];
            }

            if (minecraft.thePlayer.ticksExisted % 2 == 0 || !this.rotationsAddons.isSelected(0)) {
               if (this.rotationsAddons.isSelected(1)) {
                  this.targetYaw = RotationUtils.limitAngleChange(this.targetYaw, f, this.lookSpeedValue.getCurrentValue());
               } else {
                  this.targetYaw = f;
               }

               float f2 = minecraft.gameSettings.mouseSensitivity * 0.6F + 0.2F;
               float f3 = f2 * f2 * f2 * 1.2F;
               this.targetYaw = this.targetYaw + this.targetYaw % f3;
            }
         }
      } else {
         this.placeTimer.reset();
      }

      if (this.rotationsAddons.isSelected(2)) {
         this.targetYaw = (float)(this.targetYaw + ThreadLocalRandom.current().nextDouble(-5.0, 5.0));
         this.targetPitch = (float)(this.targetPitch + ThreadLocalRandom.current().nextDouble(-0.5, 0.5));
      }

      preUpdateEvent.rotationYaw = this.targetYaw;
      preUpdateEvent.rotationPitch = this.targetPitch;
      minecraft.thePlayer.renderYawOffset = preUpdateEvent.rotationYaw;
      minecraft.thePlayer.rotationYawHead = preUpdateEvent.rotationYaw;
      minecraft.thePlayer.rotationPitchHead = preUpdateEvent.rotationPitch;
   };

   private static Scaffold.BlockData findNeighbor(BlockPos blockPos) {
      for (java.util.Map.Entry entry : neighborOffsets.entrySet()) {
         BlockPos blockPos2 = blockPos.add((Vec3i)entry.getValue());
         if (minecraft.theWorld.getBlockState(blockPos2).getBlock() != Blocks.air) {
            return Scaffold.BlockData.create(blockPos2, (EnumFacing)entry.getKey());
         }
      }

      return null;
   }

   private double roundToFourDecimals(double d) {
      return new BigDecimal(d).setScale(4, RoundingMode.HALF_UP).doubleValue();
   }

   public MovingObjectPosition rayTraceBlock(float f, float f2) {
      Vec3 positionEyes = minecraft.thePlayer.getPositionEyes(1.0F);
      Vec3 vectorForRotation = RotationUtils.getVectorForRotation(f, f2);
      Vec3 vec3 = positionEyes.addVector(vectorForRotation.xCoord * 5.0, vectorForRotation.yCoord * 5.0, vectorForRotation.zCoord * 5.0);
      MovingObjectPosition movingObjectPosition = minecraft.theWorld.rayTraceBlocks(positionEyes, vec3, false);
      return movingObjectPosition != null && movingObjectPosition.typeOfHit == MovingObjectType.BLOCK ? movingObjectPosition : null;
   }

   private boolean selectPlacement(BlockPos blockPos) {
      Scaffold.BlockData neighbor = findNeighbor(blockPos);
      if (neighbor != null) {
         this.pos = neighbor.getPos();
         this.facing = neighbor.getFacing();
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected void onDisable() {
      minecraft.timer.timerSpeed = 1.0F;
      this.placeCount = 0;
      if (this.originalSlot != -1) {
         this.sendPacketUnregistered(new C09PacketHeldItemChange(minecraft.thePlayer.inventory.currentItem = this.originalSlot));
         this.originalSlot = -1;
      }
   }

   private void selectFirstPlacement(BlockPos... blockPosArray) {
      for (BlockPos blockPos : blockPosArray) {
         if (this.selectPlacement(blockPos)) {
            break;
         }
      }
   }

   public void setPos(BlockPos pos) {
      this.pos = null;
      this.facing = null;
      this.selectFirstPlacement(pos, pos.add(1, 0, 0), pos.add(-1, 0, 0), pos.add(0, 0, 1), pos.add(0, 0, -1));
   }

   @Override
   protected void onEnable() {
      this.pos = null;
      this.pickedSlot = this.originalSlot = -1;
      if (minecraft.thePlayer != null) {
         this.targetYaw = minecraft.thePlayer.rotationYaw;
         this.targetPitch = minecraft.thePlayer.rotationPitch;
      }
   }

   public Scaffold() {
      super(Category.WORLD, "Scaffold", "");
   }

   @Override
   protected void onInitialized() {
      this.addAutoDisableTargets(ModuleManager.speed, ModuleManager.longJump, ModuleManager.fly);
      super.onInitialized();
   }

   static final class BlockData {
      private final BlockPos pos;
      private final EnumFacing facing;

      private BlockData(BlockPos blockPos, EnumFacing enumFacing) {
         this.pos = blockPos;
         this.facing = enumFacing;
      }

      public EnumFacing getFacing() {
         return this.facing;
      }

      @Override
      public int hashCode() {
         Object $action = this.getPos();
         int i = 59 + ($action == null ? 43 : $action.hashCode());
         EnumFacing facing2 = this.getFacing();
         return i * 59 + (facing2 == null ? 43 : facing2.hashCode());
      }

      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof Scaffold.BlockData)) {
            return false;
         } else {
            Object other$pos = (Scaffold.BlockData)o;
            Object this$facing = this.getPos();
            Object other$facing = ((Scaffold.BlockData)other$pos).getPos();
            if (this$facing == null ? other$facing == null : this$facing.equals(other$facing)) {
               Object this$action = this.getFacing();
               Object other$action = ((Scaffold.BlockData)other$pos).getFacing();
               return this$action == null ? other$action == null : this$action.equals(other$action);
            } else {
               return false;
            }
         }
      }

      public static Scaffold.BlockData create(BlockPos blockPos, EnumFacing enumFacing) {
         return new Scaffold.BlockData(blockPos, enumFacing);
      }

      @Override
      public String toString() {
         return "ScaffoldFeature.BlockData(pos=" + this.getPos() + ", facing=" + this.getFacing() + ")";
      }

      public BlockPos getPos() {
         return this.pos;
      }
   }
}
