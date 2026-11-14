package wtf.novoline.module.combat;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.*;
import org.lwjgl.input.Mouse;
import wtf.novoline.enums.RelationType;
import wtf.novoline.event.*;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ComboSetting;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.combat.RotationUtils;
import wtf.novoline.utils.language.Language;
import wtf.novoline.utils.notification.NotificationType;
import wtf.novoline.utils.player.InventoryUtils;
import wtf.novoline.utils.player.PlayerUtils;
import wtf.novoline.utils.render.RenderUtils;
import wtf.novoline.utils.time.Stopwatch;

public class Killaura extends Module {

   public enum AutoBlockMode {
      Off(false, false, false),
      Manual(false, false, false),
      Vanilla(false, false, false),
      Damage(false, false, false),
      Fake(false, false, false),
      Interact_A(true, true, true),
      Interact_B(true, true, true),
      Interact_C(true, true, true),
      Interact_D(true, true, true),
      Swap(false, true, true),
      Dual(true, true, false);

      public final boolean interacts;
      public final boolean packet;
      public final boolean lags;

      AutoBlockMode(boolean interacts, boolean packet, boolean lags) {
         this.interacts = interacts;
         this.packet = packet;
         this.lags = lags;
      }
   }

   /* Settings */
   private final ComboSetting mode = settingManager.register(new ComboSetting(this, "Mode", 0, "Switch", "Single", "Multi"));
   private final ComboSetting sort = settingManager.register(new ComboSetting(this, "Sort by", 0, "Distance", "Health", "Hurt_time", "Yaw"));
   private final ComboSetting rotationMode = settingManager.register(new ComboSetting(this, "Rotation", 0, "Silent", "Lock_view", "None"));
   private final ComboSetting autoBlockMode = settingManager.register(
      new ComboSetting(this, "Auto block mode", 5, "Off", "Manual", "Vanilla", "Damage", "Fake", "Interact_A", "Interact_B", "Interact_C", "Interact_D", "Swap", "Dual")
   );
   private final NumberSetting aps = settingManager.register(new NumberSetting(this, "APS", 12.0F, 1.0F, 20.0F));
   private final NumberSetting autoBlockAps = settingManager.register(new NumberSetting(this, "Auto block APS", 10.0F, 1.0F, 20.0F));
   private final NumberSetting autoBlockLag = settingManager.register(new NumberSetting(this, "Auto block lag", 0, 0, 100));
   private final NumberSetting range = settingManager.register(new NumberSetting(this, "Attack range", 3.1F, 1.0F, 6.0F));
   private final NumberSetting swingRange = settingManager.register(new NumberSetting(this, "Swing range", 3.3F, 1.0F, 8.0F));
   private final NumberSetting blockRange = settingManager.register(new NumberSetting(this, "Block range", 3.5F, 1.0F, 8.0F));
   private final NumberSetting wallRange = settingManager.register(new NumberSetting(this, "Wall range", 3.0F, 1.0F, 6.0F));
   private final NumberSetting fov = settingManager.register(new NumberSetting(this, "FOV", 360, 30, 360));
   private final NumberSetting smoothness = settingManager.register(new NumberSetting(this, "Smoothing", 0.0F, 0.0F, 10.0F));
   private final NumberSetting switchDelay = settingManager.register(new NumberSetting(this, "Switch delay", 300, 0, 1000));
   private final NumberSetting targetsLimit = settingManager.register(new NumberSetting(this, "Targets limit", 1, 1, 10));
   private final NumberSetting attackOnDamage = settingManager.register(new NumberSetting(this, "Attack on damage", 0, 0, 5));
   private final MultiComboSetting targets = settingManager.register(
      new MultiComboSetting(this, "Targets").add("Players", true).add("Mobs", false).add("Animals", false).add("Invisibles", false).add("Fireballs", false)
   );
   private final MultiComboSetting filters = settingManager.register(
      new MultiComboSetting(this, "Filters").add("Teams", true).add("Armor", false)
   );
   private final CheckboxSetting disableInInventory = settingManager.register(new CheckboxSetting(this, "Disable in inventory", true));
   private final CheckboxSetting disableWhileBlocking = settingManager.register(new CheckboxSetting(this, "Disable while blocking", false));
   private final CheckboxSetting disableWhileMining = settingManager.register(new CheckboxSetting(this, "Disable while mining", false));
   private final CheckboxSetting disableSprintMidair = settingManager.register(new CheckboxSetting(this, "Disable sprint midair", false));
   private final CheckboxSetting hitThroughBlocks = settingManager.register(new CheckboxSetting(this, "Hit through blocks", false));
   private final CheckboxSetting ignoreTeammates = settingManager.register(new CheckboxSetting(this, "Ignore teammates", true));
   private final CheckboxSetting manualBlock = settingManager.register(new CheckboxSetting(this, "Manual block", false));
   private final CheckboxSetting prioritizeEnemies = settingManager.register(new CheckboxSetting(this, "Prioritize enemies", false));
   private final CheckboxSetting requireMouseDown = settingManager.register(new CheckboxSetting(this, "Require mouse down", false));
   private final CheckboxSetting silentSwing = settingManager.register(new CheckboxSetting(this, "Silent swing", false));
   private final CheckboxSetting weaponOnly = settingManager.register(new CheckboxSetting(this, "Weapon only", false));
   private final CheckboxSetting autoWeapon = settingManager.register(new CheckboxSetting(this, "Auto weapon", false));
   private final CheckboxSetting autoStrafe = settingManager.register(new CheckboxSetting(this, "Auto strafe", false));
   private final CheckboxSetting drawFov = settingManager.register(new CheckboxSetting(this, "Draw FOV", false));
   private final MultiComboSetting disableRules = settingManager.register(
      new MultiComboSetting(this, "Disable rules").add("World change", true).add("Death", true).add("Game end", true)
   );

   /* State */
   public EntityLivingBase entityLivingBase;
   private Entity target;
   private Entity previousTarget;
   private final List<Entity> targetList = new CopyOnWriteArrayList<>();
   private final Stopwatch attackTimer = new Stopwatch();

   private float iyaw, ipitch, prevIYaw, prevIPitch;
   private float lastClientYaw, lastClientPitch;
   private float[] rotations;
   private float[] interactRotations;
   private Vec3 aimPoint;
   private final Map<Integer, Vec3> aimPoints = new HashMap<>();
   private final Map<UUID, Long> switchTimes = new HashMap<>();
   private final List<Packet<?>> heldPackets = Collections.synchronizedList(new ArrayList<>());

   private long attackDelay;
   private double currentAps;
   private boolean attackQueued;
   private int forcedAttacks;
   private int blockState = -1;
   private boolean fakeBlocking;
   private boolean serverBlocking;
   private boolean autoBlocked;
   private boolean inSwingRange;
   private boolean attackedThisTick;
   private boolean attacking;
   private boolean releasing;
   private boolean returning;
   private boolean lagging;
   private int lagAccumulator;
   private double dualAccumulator;
   private int dualSwaps;
   private long damageBlockAt;

   public Killaura() {
      super(Category.COMBAT, "Killaura", "Automatically attacks entities with RavenXD mechanics");
   }

   public AutoBlockMode getAutoBlockMode() {
      try {
         return AutoBlockMode.valueOf(this.autoBlockMode.getCurrentElementAsString());
      } catch (Exception e) {
         return AutoBlockMode.Off;
      }
   }

   private boolean isTargetSelected(String name) {
      MultiComboSetting.Option opt = this.targets.byName(name);
      return opt != null && opt.isSelected();
   }

   private boolean isFilterSelected(String name) {
      MultiComboSetting.Option opt = this.filters.byName(name);
      return opt != null && opt.isSelected();
   }

   @Override
   protected void onEnable() {
      this.setSuffix(this.mode.getCurrentElementAsString());
      if (minecraft.thePlayer == null) return;
      this.iyaw = minecraft.thePlayer.rotationYaw;
      this.ipitch = minecraft.thePlayer.rotationPitch;
      this.prevIYaw = this.iyaw;
      this.prevIPitch = this.ipitch;
      this.lastClientYaw = this.iyaw;
      this.lastClientPitch = this.ipitch;
      this.target = null;
      this.entityLivingBase = null;
      this.previousTarget = null;
      this.targetList.clear();
      this.attackTimer.reset();
      setDelay(true);
      this.blockState = -1;
      this.fakeBlocking = false;
      this.serverBlocking = false;
      this.autoBlocked = false;
      this.returning = false;
      this.rotations = null;
      this.interactRotations = null;
      this.heldPackets.clear();
      ItemRenderer.flag = false;
   }

   @Override
   protected void onDisable() {
      if (getAutoBlockMode() != AutoBlockMode.Manual && !isRightDown() && minecraft.thePlayer != null && minecraft.thePlayer.isUsingItem()) {
         setBlocking(false, false);
      }
      if (this.blockState != -1) {
         releaseFlow(this.blockState);
         this.blockState = -1;
      }
      this.fakeBlocking = false;
      this.serverBlocking = false;
      this.autoBlocked = false;
      ItemRenderer.flag = false;
      this.returning = false;
      this.attackedThisTick = false;
      this.inSwingRange = false;
      this.attackQueued = false;
      this.target = null;
      this.entityLivingBase = null;
      this.previousTarget = null;
      this.targetList.clear();
      this.interactRotations = null;
      this.rotations = null;
      this.aimPoint = null;
      this.aimPoints.clear();
      this.switchTimes.clear();
      this.dualSwaps = 0;
      this.forcedAttacks = 0;
      releaseHeldPackets(true);
   }

   @EventLink
   public final Listener<PreUpdateEvent> onPreUpdate = event -> {
      if (minecraft.thePlayer == null || minecraft.theWorld == null) return;
      this.setSuffix(this.mode.getCurrentElementAsString());

      if (!minecraft.thePlayer.isEntityAlive() && this.disableRules.isSelected(1)) {
         this.setEnabled(false);
         this.showNotification(NotificationType.INFO, "Killaura", Language.translate("feature.killaura.disable_rules.notification.death"), 2500L);
         return;
      }

      if (ModuleManager.scaffold.isEnabled()) {
         setTarget(null);
         return;
      }

      if (this.interactRotations != null) {
         applyRotations(this.interactRotations, event);
         this.interactRotations = null;
         return;
      }

      findTarget();
      updateAps();

      boolean canAttack = exists(this.target);
      if (this.disableInInventory.isEnabled() && minecraft.currentScreen != null) {
         canAttack = false;
      }

      if (this.target != null) {
         if (this.inSwingRange) {
            updateRotations();
            applyRotations(this.rotations, event);
            if (this.smoothness.getCurrentValue() > 0.0F) {
               this.lastClientYaw = minecraft.thePlayer.rotationYaw;
               this.lastClientPitch = minecraft.thePlayer.rotationPitch;
               this.returning = true;
            }
         }

         AutoBlockMode ab = getAutoBlockMode();
         if (ab.packet) {
            if (!blockAllowed() || !PlayerUtils.holdingSword()) {
               if (this.blockState != -1) {
                  releaseFlow(this.blockState);
                  this.blockState = -1;
                  this.fakeBlocking = false;
                  this.serverBlocking = false;
                  this.autoBlocked = false;
                  ItemRenderer.flag = false;
                  canAttack = false;
               }
            } else {
               handlePacketAutoBlockPre(ab);
            }
         }

         if (this.autoStrafe.isEnabled()
            && (minecraft.thePlayer.movementInput.moveForward != 0.0F || minecraft.thePlayer.movementInput.moveStrafe != 0.0F)
            && !minecraft.thePlayer.onGround) {
            PlayerUtils.setMotionInMoveDirection(
               Math.sqrt(minecraft.thePlayer.motionX * minecraft.thePlayer.motionX + minecraft.thePlayer.motionZ * minecraft.thePlayer.motionZ)
            );
         }
      } else if (this.returning) {
         this.returning = smoothReturn(event);
      }

      if (this.forcedAttacks > 0) {
         --this.forcedAttacks;
         this.attackQueued = true;
      }

      if (this.attackTimer.hasElapsed(this.attackDelay)) {
         this.attackTimer.reset();
         setDelay(false);
         if (this.target != null) {
            if (!(this.disableSprintMidair.isEnabled() && !minecraft.thePlayer.onGround && minecraft.thePlayer.isSprinting())) {
               this.attackQueued = true;
            }
         }
      }

      if (this.attackQueued && canAttack) {
         this.attackQueued = false;
         executeAttack();
      }
   };

   @EventLink
   public final Listener<OnUpdateWalkingPlayerEvent> onPostUpdate = event -> {
      if (minecraft.thePlayer == null || minecraft.theWorld == null) return;
      if (this.target != null) {
         if (this.inSwingRange && this.rotationMode.getCurrentElement() == 1 && this.rotations != null) {
            minecraft.thePlayer.rotationYaw = this.rotations[0];
            minecraft.thePlayer.rotationPitch = this.rotations[1];
         }
         if (blockAllowed() && !getAutoBlockMode().packet) {
            AutoBlockMode ab = getAutoBlockMode();
            switch (ab) {
               case Vanilla:
                  setBlocking(true, false);
                  break;
               case Damage:
                  if (System.currentTimeMillis() - this.damageBlockAt < 800L) {
                     setBlocking(true, false);
                  } else if (!isRightDown()) {
                     setBlocking(false, false);
                  }
                  break;
               case Fake:
                  this.fakeBlocking = true;
                  this.autoBlocked = true;
                  ItemRenderer.flag = true;
                  break;
               default:
                  break;
            }
         }
      }
      this.attackedThisTick = false;
   };

   private void executeAttack() {
      Entity entity = this.target;
      if (entity == null) return;
      boolean inReach = false;
      float[] currentRot = this.rotations != null ? this.rotations : new float[]{minecraft.thePlayer.rotationYaw, minecraft.thePlayer.rotationPitch};

      if (this.inSwingRange) {
         boolean sameRange = (this.range.getCurrentValue() == this.swingRange.getCurrentValue());
         inReach = (sameRange && this.smoothness.getCurrentValue() == 0.0F) || RotationUtils.rayHits(entity, currentRot[0], currentRot[1], (double) this.range.getCurrentValue());
      }

      if (this.inSwingRange) {
         boolean attack = inReach;
         boolean swing = true;
         boolean silent = this.silentSwing.isEnabled();

         if (this.disableWhileBlocking.isEnabled() && PlayerUtils.holdingSword() && minecraft.thePlayer.isBlocking()) {
            swing = false;
            attack = false;
         }

         if (attack) {
            this.attackedThisTick = true;
            this.attacking = true;

            if (this.mode.getCurrentElement() == 2) {
               List<Entity> list = new ArrayList<>(this.targetList);
               int limit = Math.min(list.size(), (int) this.targetsLimit.getCurrentValue());
               for (int i = 0; i < limit; i++) {
                  Entity e = list.get(i);
                  if (RotationUtils.rayHits(e, currentRot[0], currentRot[1], (double) this.range.getCurrentValue())) {
                     attackSingleEntity(e, silent);
                  }
               }
            } else {
               attackSingleEntity(entity, silent);
            }
            this.attacking = false;
         } else if (swing) {
            if (!silent) {
               minecraft.thePlayer.swingItemNoPacket();
            } else {
               sendPacketUnregistered(new C0APacketAnimation());
            }
         }

         AutoBlockMode ab = getAutoBlockMode();
         if (blockAllowed() && this.blockState == 1 && ab.interacts && inReach) {
            MovingObjectPosition hit = RotationUtils.rayTraceEntity(entity, (double) this.range.getCurrentValue() + 0.3, currentRot[0], currentRot[1]);
            if (hit != null && hit.hitVec != null) {
               Vec3 vec3 = new Vec3(hit.hitVec.xCoord - entity.posX, hit.hitVec.yCoord - entity.posY, hit.hitVec.zCoord - entity.posZ);
               sendPacketUnregistered(new C02PacketUseEntity(entity, vec3));
               sendPacketUnregistered(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.INTERACT));
            }
            if (PlayerUtils.holdingSword()) {
               setBlocking(true, true);
            }
         }
      }
   }

   private void attackSingleEntity(Entity e, boolean silent) {
      if (this.autoWeapon.isEnabled()) {
         int slot = InventoryUtils.findSlotOfClass(ItemSword.class, true);
         if (slot == -1) slot = InventoryUtils.findSlotOfClass(ItemAxe.class, true);
         if (slot != -1 && minecraft.thePlayer.inventory.currentItem != slot) {
            sendPacketUnregistered(new C09PacketHeldItemChange(slot));
            minecraft.thePlayer.inventory.currentItem = slot;
         }
      }

      if (!silent) {
         minecraft.thePlayer.swingItemNoPacket();
      } else {
         sendPacketUnregistered(new C0APacketAnimation());
      }

      if (ModuleManager.criticals.isEnabled()) {
         ModuleManager.criticals.sendCriticalPackets();
      }

      minecraft.playerController.attackEntity(minecraft.thePlayer, e);
   }

   private void handlePacketAutoBlockPre(AutoBlockMode ab) {
      if (this.blockState == 0) {
         setBlocking(false, false);
      }
      switch (ab) {
         case Swap:
            switch (this.blockState) {
               case 0:
                  setBlocking(true, true);
                  ++this.blockState;
                  break;
               case -1:
               case 1:
                  if (this.blockState == -1 || this.currentAps >= 10.0) {
                     this.attackQueued = true;
                  }
                  rollLag();
                  this.blockState = 0;
                  swapAway();
                  this.serverBlocking = false;
                  this.autoBlocked = false;
                  if (!this.lagging) {
                     this.fakeBlocking = false;
                     ItemRenderer.flag = false;
                  }
                  break;
            }
            break;
         case Dual:
            handleDualAutoBlock();
            break;
         case Interact_A:
         case Interact_B:
         case Interact_C:
         case Interact_D:
            switch (this.blockState) {
               case 0:
                  if (this.currentAps >= 10.0) this.attackQueued = true;
                  if (this.attackQueued) {
                     ++this.blockState;
                     if (ab == AutoBlockMode.Interact_B || ab == AutoBlockMode.Interact_C) {
                        sendPacketUnregistered(new C09PacketHeldItemChange(minecraft.thePlayer.inventory.currentItem));
                     }
                  }
                  break;
               case -1:
               case 1:
                  rollLag();
                  if (this.blockState == 1) {
                     if (ab == AutoBlockMode.Interact_B || ab == AutoBlockMode.Interact_C) {
                        sendPacketUnregistered(new C09PacketHeldItemChange(freeSlot()));
                        if (ab == AutoBlockMode.Interact_C) {
                           setBlocking(false, true);
                        } else {
                           this.serverBlocking = false;
                           this.autoBlocked = false;
                           ItemRenderer.flag = false;
                        }
                     } else {
                        if (ab == AutoBlockMode.Interact_D) swapAway();
                        setBlocking(false, true);
                     }
                  }
                  this.blockState = 0;
                  break;
            }
            break;
         default:
            break;
      }
   }

   private void handleDualAutoBlock() {
      int best = -1;
      int second = -1;
      double bestDamage = -1.0;
      double secondDamage = -1.0;
      for (int i = 0; i < 9; ++i) {
         ItemStack stack = minecraft.thePlayer.inventory.getStackInSlot(i);
         if (stack != null && stack.getItem() instanceof ItemSword) {
            double dmg = 4.0;
            if (dmg > bestDamage) {
               secondDamage = bestDamage;
               second = best;
               bestDamage = dmg;
               best = i;
            } else if (dmg > secondDamage) {
               secondDamage = dmg;
               second = i;
            }
         }
      }
      if (second == -1) {
         this.blockState = -1;
         return;
      }
      if (this.attackQueued) {
         this.blockState = 1;
         int slot = (this.dualSwaps++ & 1) == 0 ? best : second;
         sendPacketUnregistered(new C09PacketHeldItemChange(slot));
      } else {
         this.blockState = 0;
      }
   }

   private void swapAway() {
      sendPacketUnregistered(new C09PacketHeldItemChange(freeSlot()));
      sendPacketUnregistered(new C09PacketHeldItemChange(minecraft.thePlayer.inventory.currentItem));
   }

   private int freeSlot() {
      int current = minecraft.thePlayer.inventory.currentItem;
      for (int i = 0; i < 9; ++i) {
         if (i != current && minecraft.thePlayer.inventory.getStackInSlot(i) == null) {
            return i;
         }
      }
      for (int i = 0; i < 9; ++i) {
         if (i != current) {
            ItemStack stack = minecraft.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && !(stack.getItem() instanceof ItemSword)) {
               return i;
            }
         }
      }
      return (current + 1) % 9;
   }

   private void rollLag() {
      if ((this.lagAccumulator += (int) this.autoBlockLag.getCurrentValue()) >= 100) {
         this.lagAccumulator -= 100;
         this.lagging = true;
      } else {
         this.lagging = false;
      }
   }

   public void setBlocking(boolean block, boolean packet) {
      if (packet) {
         if (block) {
            sendPacketUnregistered(new C08PacketPlayerBlockPlacement(minecraft.thePlayer.getHeldItem()));
         } else {
            this.releasing = true;
            sendPacketUnregistered(new C07PacketPlayerDigging(
               C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            this.releasing = false;
         }
         this.serverBlocking = this.fakeBlocking = block;
      } else {
         KeyBinding.setKeyBindState(minecraft.gameSettings.keyBindUseItem.getKeyCode(), block);
         this.fakeBlocking = block;
      }
      this.autoBlocked = block;
      ItemRenderer.flag = isAutoBlocked();
   }

   private void releaseFlow(int state) {
      AutoBlockMode ab = getAutoBlockMode();
      if (ab.packet) {
         setBlocking(false, true);
      }
      this.fakeBlocking = false;
      this.serverBlocking = false;
      this.autoBlocked = false;
      ItemRenderer.flag = false;
   }

   private void releaseHeldPackets(boolean all) {
      if (this.heldPackets.isEmpty()) return;
      synchronized (this.heldPackets) {
         for (Packet<?> p : this.heldPackets) {
            sendPacketUnregistered(p);
         }
         this.heldPackets.clear();
      }
   }

   @EventLink
   public final Listener<PacketReceiveEvent> onPacketReceive = event -> {
      if (minecraft.thePlayer == null || event.packet == null) return;
      if (event.packet instanceof S12PacketEntityVelocity) {
         S12PacketEntityVelocity p = (S12PacketEntityVelocity) event.packet;
         if (p.getEntityID() == minecraft.thePlayer.getEntityId()) {
            this.damageBlockAt = System.currentTimeMillis();
            if (this.attackOnDamage.getCurrentValue() > 0.0F) {
               this.forcedAttacks = (int) this.attackOnDamage.getCurrentValue();
            }
         }
      }
   };

   @EventLink
   public final Listener<PacketSendEvent> onPacketSend = event -> {
      if (minecraft.thePlayer == null || event.packet == null) return;
      Packet<?> p = event.packet;
      AutoBlockMode ab = getAutoBlockMode();
      if (ab.lags && this.lagging && !this.heldPackets.isEmpty()) {
         if (!(p instanceof C0APacketAnimation)) {
            this.heldPackets.add(p);
            event.cancel();
         }
      }
   };

   @EventLink
   public final Listener<RunTickEvent> onTick = event -> {
      if (this.lagging && !this.heldPackets.isEmpty()) {
         releaseHeldPackets(true);
         this.lagging = false;
      }
   };

   @EventLink
   public final Listener<RenderScreenEvent> onRenderScreen = renderScreenEvent -> {
      if (this.drawFov.isEnabled() && renderScreenEvent.scaledResolution != null) {
         float f = (float)(renderScreenEvent.scaledResolution.getScaledWidth_double() / 2.0);
         float f2 = (float)(renderScreenEvent.scaledResolution.getScaledHeight_double() / 2.0);
         float f3 = (float)(
            this.fov.getCurrentValue() / 360.0F
               * Math.sqrt(
                  renderScreenEvent.scaledResolution.getScaledWidth_double() * renderScreenEvent.scaledResolution.getScaledWidth_double()
                     + renderScreenEvent.scaledResolution.getScaledHeight_double() * renderScreenEvent.scaledResolution.getScaledHeight_double()
               )
         );
         RenderUtils.drawArc(f, f2, f3, 2.0F, -1, 360);
      }
   };

   @EventLink
   public final Listener<GameEndEvent> onGameEnd = gameEndEvent -> {
      if (this.disableRules.isSelected(2)) {
         this.setEnabled(false);
         this.showNotification(NotificationType.INFO, "Killaura", Language.translate("feature.killaura.disable_rules.notification.game_end"), 2500L);
      }
   };

   @EventLink
   public final Listener<LoadWorldEvent> onLoadWorld = loadWorldEvent -> {
      if (this.disableRules.isSelected(0)) {
         this.setEnabled(false);
         this.showNotification(NotificationType.INFO, "Killaura", Language.translate("feature.killaura.disable_rules.notification.world_change"), 2500L);
      }
   };

   private void findTarget() {
      if (this.disableInInventory.isEnabled() && minecraft.currentScreen != null) {
         setTarget(null);
         return;
      }
      if (this.weaponOnly.isEnabled() && !PlayerUtils.holdingWeapon()) {
         setTarget(null);
         return;
      }
      if (this.requireMouseDown.isEnabled() && (!Mouse.isCreated() || !Mouse.isButtonDown(0) || minecraft.currentScreen instanceof GuiChat)) {
         setTarget(null);
         return;
      }
      if (this.disableWhileMining.isEnabled() && Mouse.isCreated() && Mouse.isButtonDown(0) && minecraft.objectMouseOver != null && minecraft.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
         setTarget(null);
         return;
      }

      double maxReach = Math.max(Math.max((double) this.range.getCurrentValue(), (double) this.swingRange.getCurrentValue()), (double) this.blockRange.getCurrentValue());
      double scanRangeSq = (maxReach + 1.2) * (maxReach + 1.2);

      List<EntityLivingBase> candidates = new ArrayList<>();
      Entity fireball = null;
      double nearestFireball = -1.0;

      for (Entity e : minecraft.theWorld.loadedEntityList) {
         if (e == minecraft.thePlayer) continue;

         if (isTargetSelected("Fireballs") && e instanceof EntityLargeFireball) {
            double d = minecraft.thePlayer.getDistanceSqToEntity(e);
            if (d <= scanRangeSq && PlayerUtils.inFov(this.fov.getCurrentValue(), e) && RotationUtils.rayHits(e, (double) this.swingRange.getCurrentValue())) {
               if (nearestFireball == -1.0 || d < nearestFireball) {
                  nearestFireball = d;
                  fireball = e;
               }
            }
            continue;
         }

         if (!(e instanceof EntityLivingBase)) continue;
         EntityLivingBase living = (EntityLivingBase) e;

         if (!isValidEntity(living)) continue;

         double d = minecraft.thePlayer.getDistanceSqToEntity(living);
         if (d > scanRangeSq) continue;

         if (this.fov.getCurrentValue() < 360.0F && !PlayerUtils.inFov(this.fov.getCurrentValue(), living)) continue;

         if (!RotationUtils.canSee(living)) {
            if (!this.hitThroughBlocks.isEnabled()) {
               continue;
            }
            double wallDist = (double) this.wallRange.getCurrentValue();
            if (d > wallDist * wallDist) {
               continue;
            }
         }

         if (!this.hitThroughBlocks.isEnabled()) {
            Vec3 point = RotationUtils.visiblePoint(living);
            if (point == null) continue;
            this.aimPoints.put(living.getEntityId(), point);
         }

         candidates.add(living);
      }

      if (candidates.isEmpty()) {
         if (fireball != null) {
            this.inSwingRange = true;
            setTarget(fireball);
         } else {
            setTarget(null);
         }
         this.targetList.clear();
         return;
      }

      Comparator<EntityLivingBase> comparator;
      int sortIdx = this.sort.getCurrentElement();
      if (sortIdx == 1) {
         comparator = Comparator.comparingDouble(EntityLivingBase::getHealth);
      } else if (sortIdx == 2) {
         comparator = Comparator.comparingDouble(ent -> ent.hurtTime);
      } else if (sortIdx == 3) {
         comparator = Comparator.comparingDouble(ent -> RotationUtils.absYawDifference(ent, minecraft.thePlayer.rotationYaw));
      } else {
         comparator = Comparator.comparingDouble(ent -> minecraft.thePlayer.getDistanceSqToEntity(ent));
      }

      if (this.prioritizeEnemies.isEnabled()) {
         Comparator<EntityLivingBase> compFinal = comparator;
         comparator = (e1, e2) -> {
            boolean p1 = novoline.getRelationManager().get(e1.getName()) == RelationType.TARGET;
            boolean p2 = novoline.getRelationManager().get(e2.getName()) == RelationType.TARGET;
            if (p1 && !p2) return -1;
            if (!p1 && p2) return 1;
            return compFinal.compare(e1, e2);
         };
      }
      candidates.sort(comparator);

      this.targetList.clear();
      this.targetList.addAll(candidates);

      int modeIdx = this.mode.getCurrentElement();
      if (modeIdx == 1) {
         if (this.target != null && candidates.contains(this.target)) {
            this.inSwingRange = true;
            setTarget(this.target);
            return;
         }
         this.inSwingRange = true;
         setTarget(candidates.get(0));
         return;
      }

      long delay = (long) Math.max(100.0F, this.switchDelay.getCurrentValue());
      long now = System.currentTimeMillis();
      long cycle = (long) Math.min((double) candidates.size(), (double) this.targetsLimit.getCurrentValue()) * delay;
      Entity chosen = null;

      for (EntityLivingBase e : candidates) {
         UUID uuid = e.getUniqueID();
         Long l = this.switchTimes.get(uuid);
         if (l == null) continue;
         if (l > 0L) {
            if (Math.abs(now - l) < delay) {
               chosen = e;
               break;
            }
            if (cycle != delay) {
               this.switchTimes.put(uuid, -l);
               continue;
            }
            this.switchTimes.remove(uuid);
            continue;
         }
         if (Math.abs(now - -l) < cycle) continue;
         this.switchTimes.remove(uuid);
      }

      if (chosen == null) {
         for (EntityLivingBase e : candidates) {
            if (this.switchTimes.get(e.getUniqueID()) != null) continue;
            this.switchTimes.put(e.getUniqueID(), now);
            chosen = e;
            break;
         }
         if (chosen == null && exists(this.previousTarget)) {
            chosen = this.previousTarget;
         }
      }

      if (chosen == null && !candidates.isEmpty()) {
         chosen = candidates.get(0);
      }

      this.inSwingRange = true;
      setTarget(chosen);
   }

   private void updateRotations() {
      float lastYaw = this.iyaw;
      float lastPitch = this.ipitch;
      this.rotations = aim();
      if (this.smoothness.getCurrentValue() > 0.0F) {
         smooth(this.rotations, lastYaw, lastPitch);
      }
      this.rotations = RotationUtils.fixSensitivity(this.rotations[0], this.rotations[1], lastYaw, lastPitch);
   }

   private void applyRotations(float[] rot, PreUpdateEvent event) {
      if (rot == null) return;
      this.prevIYaw = this.iyaw;
      this.prevIPitch = this.ipitch;
      this.iyaw = rot[0];
      this.ipitch = rot[1];

      int rotMode = this.rotationMode.getCurrentElement();
      if (rotMode == 0) {
         event.rotationYaw = rot[0];
         event.rotationPitch = rot[1];
         minecraft.thePlayer.rotationYawHead = rot[0];
         minecraft.thePlayer.renderYawOffset = rot[0];
         minecraft.thePlayer.rotationPitchHead = rot[1];
      } else if (rotMode == 1) {
         minecraft.thePlayer.rotationYaw = rot[0];
         minecraft.thePlayer.rotationPitch = rot[1];
         event.rotationYaw = rot[0];
         event.rotationPitch = rot[1];
         minecraft.thePlayer.rotationYawHead = rot[0];
         minecraft.thePlayer.renderYawOffset = rot[0];
         minecraft.thePlayer.rotationPitchHead = rot[1];
      }
   }

   private boolean smoothReturn(PreUpdateEvent event) {
      if (this.smoothness.getCurrentValue() == 0.0F) return false;
      float yaw = minecraft.thePlayer.rotationYaw;
      float pitch = minecraft.thePlayer.rotationPitch;
      float dYaw = Math.abs(MathHelper.wrapAngleTo180_float(yaw - this.lastClientYaw));
      float dPitch = Math.abs(pitch - this.lastClientPitch);
      this.lastClientYaw = yaw;
      this.lastClientPitch = pitch;
      if (dYaw > 20.0F || dPitch > 8.0F) return false;

      float[] rot = new float[]{yaw, pitch};
      smooth(rot, this.iyaw, this.ipitch);
      rot = RotationUtils.fixSensitivity(rot[0], rot[1], this.iyaw, this.ipitch);
      applyRotations(rot, event);

      return Math.abs(MathHelper.wrapAngleTo180_float(rot[0] - minecraft.thePlayer.rotationYaw)) > 3.0F
         || Math.abs(rot[1] - minecraft.thePlayer.rotationPitch) > 3.0F;
   }

   private void smooth(float[] rot, float lastYaw, float lastPitch) {
      float maxYaw = 7.0F;
      float maxPitch = 3.0F;
      float keep = (float) (1.0 - Math.min(9.9, (double) this.smoothness.getCurrentValue()) / 10.0);
      float dYaw = MathHelper.wrapAngleTo180_float(rot[0] - lastYaw);
      float aYaw = Math.abs(dYaw);
      float dPitch = rot[1] - lastPitch;
      float aPitch = Math.abs(dPitch);
      if (aYaw > maxYaw) {
         dYaw = Math.signum(dYaw) * ((aYaw - maxYaw) * keep + maxYaw);
      }
      if (aPitch > maxPitch) {
         dPitch = Math.signum(dPitch) * ((aPitch - maxPitch) * keep + maxPitch);
      }
      rot[0] = lastYaw + dYaw;
      rot[1] = lastPitch + dPitch;
   }

   private float[] aim() {
      if (this.aimPoint != null) {
         return RotationUtils.toVec(this.aimPoint);
      }
      return RotationUtils.toEntityBox(this.target);
   }

   private void updateAps() {
      double d = blockAllowed() ? (double) this.autoBlockAps.getCurrentValue() : (double) this.aps.getCurrentValue();
      if (this.target != null && getAutoBlockMode() == AutoBlockMode.Dual) {
         if (this.blockState == -1) {
            this.dualAccumulator = 20.0 - d;
         }
         if ((this.dualAccumulator += d) >= 20.0) {
            this.dualAccumulator -= 20.0;
            this.attackQueued = true;
         }
      }
      if (this.currentAps != d) {
         setDelay(true);
         this.currentAps = d;
      }
   }

   private void setDelay(boolean now) {
      if (now) {
         this.attackDelay = 0L;
      } else {
         double a = this.currentAps > 0.0 ? this.currentAps : (double) this.aps.getCurrentValue();
         this.attackDelay = (long) (1000.0 / a + (double) (int) ((Math.random() - 0.5) * 8.0));
      }
   }

   public boolean blockAllowed() {
      return !this.manualBlock.isEnabled() || isRightDown();
   }

   private boolean isRightDown() {
      return Mouse.isCreated() && Mouse.isButtonDown(1);
   }

   private boolean exists(Entity t) {
      return t != null && t.isEntityAlive() && minecraft.theWorld != null && minecraft.theWorld.getEntityByID(t.getEntityId()) != null;
   }

   public boolean isValidEntity(Entity entity) {
      if (entity == minecraft.thePlayer || !entity.isEntityAlive() || (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).deathTime != 0)) {
         return false;
      }
      if (entity instanceof EntityPlayer) {
         if (!isTargetSelected("Players")) {
            return false;
         }
         EntityPlayer player = (EntityPlayer) entity;
         if (novoline.getRelationManager().get(player.getName()) == RelationType.FRIEND) {
            return false;
         }
         if (ModuleManager.antibot.isEnabled() && ModuleManager.antibot.isPlayerBot(player)) {
            return false;
         }
         if (this.ignoreTeammates.isEnabled() && isFilterSelected("Teams") && PlayerUtils.inTeamWithMinecraftPlayer(player)) {
            return false;
         }
         if (isFilterSelected("Armor") && !hasArmor(player)) {
            return false;
         }
         if (entity.isInvisible() && !isTargetSelected("Invisibles") && PlayerUtils.isInvisibleUnarmoured((EntityLivingBase) entity)) {
            return false;
         }
         return true;
      }
      if (entity instanceof IMob) {
         return isTargetSelected("Mobs");
      }
      if (entity instanceof IAnimals || entity instanceof EntityVillager) {
         return isTargetSelected("Animals");
      }
      return false;
   }

   private boolean hasArmor(EntityPlayer e) {
      if (e.inventory == null || e.inventory.armorInventory == null) return false;
      for (ItemStack is : e.inventory.armorInventory) {
         if (is != null) return true;
      }
      return false;
   }

   public void setTarget(Entity entity) {
      if (entity == null) {
         this.switchTimes.clear();
         if (this.target != null && getAutoBlockMode().packet) {
            releaseFlow(this.blockState);
         }
         this.fakeBlocking = false;
         this.serverBlocking = false;
         this.autoBlocked = false;
         ItemRenderer.flag = false;
         this.inSwingRange = false;
         this.blockState = -1;
         this.aimPoint = null;
         this.aimPoints.clear();
         this.entityLivingBase = null;
      } else {
         if (!this.hitThroughBlocks.isEnabled()) {
            this.aimPoint = this.aimPoints.get(entity.getEntityId());
         }
         if (entity instanceof EntityLivingBase) {
            this.entityLivingBase = (EntityLivingBase) entity;
         } else {
            this.entityLivingBase = null;
         }
      }
      this.previousTarget = this.target;
      this.target = entity;
   }

   public Entity getTarget() {
      return this.target;
   }

   public List<Entity> getTargetList() {
      return this.targetList;
   }

   public float getRotYaw() {
      return this.iyaw;
   }

   public float getRotPitch() {
      return this.ipitch;
   }

   public boolean isAutoBlocked() {
      return this.autoBlocked || this.fakeBlocking || this.serverBlocking;
   }

   public boolean isAttackReady() {
      return this.attackTimer.hasElapsed(this.attackDelay);
   }

   public Stopwatch getStopwatch() {
      return this.attackTimer;
   }

   public boolean shouldBlock() {
      return (getAutoBlockMode() != AutoBlockMode.Off) && this.target != null && PlayerUtils.holdingSword() && isEnabled() && !ModuleManager.scaffold.isEnabled();
   }
}
