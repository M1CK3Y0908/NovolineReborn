package wtf.novoline.module.combat;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.lwjgl.util.vector.Vector3f;
import wtf.novoline.enums.RelationType;
import wtf.novoline.event.GameEndEvent;
import wtf.novoline.event.LoadWorldEvent;
import wtf.novoline.event.OnUpdateWalkingPlayerEvent;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.event.RenderScreenEvent;
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
   private final ComboSetting killaura = settingManager.register(new ComboSetting(this, "Rotations", 1, "Vanilla", "Instant", "Smart", "Matrix"));
   private final ComboSetting rotationType = settingManager.register(new ComboSetting(this, "Rotation type", 1, "Client-side", "Server-side"));
   private final ComboSetting attackAt = settingManager.register(new ComboSetting(this, "Attack at", 1, "Pre", "Post"));
   private final ComboSetting type = settingManager.register(new ComboSetting(this, "Type", 1, "Single", "Switch"));
   private final ComboSetting priority = settingManager.register(new ComboSetting(this, "Priority", 1, "Random", "Distance", "Health", "FOV"));
   private final MultiComboSetting targets = settingManager.register(
      new MultiComboSetting(this, "Targets").add("Players", true).add("Mob", false).add("Animals", false)
   );
   private final MultiComboSetting ignoreRules = settingManager.register(
      new MultiComboSetting(this, "Ignore rules").add("Team", true).add("Invisible", false).add("Dead", true).add("Bots", true)
   );
   private final NumberSetting attackDistance = settingManager.register(new NumberSetting(this, "Attack distance", 4.0F, 0.0F, 8.0F));
   private final NumberSetting blockDistance = settingManager.register(new NumberSetting(this, "Block distance", 4.0F, 0.0F, 8.0F));
   private final NumberSetting wallDistance = settingManager.register(new NumberSetting(this, "Wall distance", 4.0F, 0.0F, 8.0F));
   private final NumberSetting fov = settingManager.register(new NumberSetting(this, "FOV", 180, 0, 180));
   private final NumberSetting hitChance = settingManager.register(new NumberSetting(this, "Hit chance", 1, 1, 20));
   private final NumberSetting cps = settingManager.register(new NumberSetting(this, "CPS", 10, 1, 20));
   private final CheckboxSetting autoStrafe = settingManager.register(new CheckboxSetting(this, "Auto strafe", false));
   private final CheckboxSetting randomizeCps = settingManager.register(new CheckboxSetting(this, "Randomize CPS", false));
   private final CheckboxSetting rayCast = settingManager.register(new CheckboxSetting(this, "Ray cast", false));
   private final CheckboxSetting autoBlock = settingManager.register(new CheckboxSetting(this, "Auto block", false));
   private final CheckboxSetting autoWeapon = settingManager.register(new CheckboxSetting(this, "Auto weapon", false));
   private final CheckboxSetting throughWalls = settingManager.register(new CheckboxSetting(this, "Through walls", false));
   private final ComboSetting autoBlockMode = settingManager.register(
      new ComboSetting(this, "Auto block mode", 1, "Always", "Normal", "Hypixel", "Fake", "Legit") {
         @Override
         public void interactCallback() {
            if (this.getCurrentElement() == 4) {
               Killaura.this.autoBlockDelays.setEnabled(true);
               Killaura.this.unblockTicks.setCurrentValue(0.0F);
               Killaura.this.attackTicks.setCurrentValue(1.0F);
               Killaura.this.blockTicks.setCurrentValue(2.0F);
            }
         }

         @Override
         public boolean shouldShow() {
            return Killaura.this.autoBlock.isEnabled();
         }
      }
   );
   private final CheckboxSetting autoBlockDelays = settingManager.register(new CheckboxSetting(this, "Auto block delays", false) {
      @Override
      public boolean shouldShow() {
         return Killaura.this.autoBlock.isEnabled() && Killaura.this.autoBlockMode.getCurrentElement() != 2;
      }
   });
   private final NumberSetting unblockTicks = settingManager.register(new NumberSetting(this, "Unblock ticks", 1, 0, 10) {
      @Override
      public boolean shouldShow() {
         return Killaura.this.autoBlockDelays.isEnabled() && Killaura.this.autoBlockDelays.shouldShow();
      }
   });
   private final NumberSetting attackTicks = settingManager.register(new NumberSetting(this, "Attack ticks", 1, 0, 10) {
      @Override
      public boolean shouldShow() {
         return Killaura.this.autoBlockDelays.isEnabled() && Killaura.this.autoBlockDelays.shouldShow();
      }
   });
   private final NumberSetting blockTicks = settingManager.register(new NumberSetting(this, "Block ticks", 1, 0, 10) {
      @Override
      public boolean shouldShow() {
         return Killaura.this.autoBlockDelays.isEnabled() && Killaura.this.autoBlockDelays.shouldShow();
      }
   });
   private final CheckboxSetting drawFov = settingManager.register(new CheckboxSetting(this, "Draw FOV", false));
   private final CheckboxSetting interactAutoBlock = settingManager.register(new CheckboxSetting(this, "Interact auto block", false) {
      @Override
      public boolean shouldShow() {
         return Killaura.this.autoBlockMode.shouldShow();
      }
   });
   private final MultiComboSetting disableRules = settingManager.register(
      new MultiComboSetting(this, "Disable rules").add("World change", true).add("Death", true).add("Game end", true)
   );
   public EntityLivingBase entityLivingBase;
   private final Stopwatch attackTimer = new Stopwatch();
   private boolean autoBlocked;
   private boolean criticalsApplied;
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
   private int ticksSinceBlock = 0;
   @EventLink
   public final Listener<RenderScreenEvent> onRenderScreen = renderScreenEvent -> {
      if (this.drawFov.isEnabled()) {
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
   private float rotPitch;
   @EventLink
   public final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      if (this.ticksSinceBlock < 1337) {
         this.ticksSinceBlock++;
      }

      this.setSuffix(this.killaura.getCurrentElementAsString());
      this.criticalsApplied = false;
      if (!minecraft.thePlayer.isEntityAlive() && this.disableRules.getElements().get(1).isSelected() && this.disableRules.isSelected(1)) {
         this.setEnabled(false);
         this.showNotification(NotificationType.INFO, "Killaura", Language.translate("feature.killaura.disable_rules.notification.death"), 2500L);
      }

      if (!ModuleManager.scaffold.isEnabled()) {
         int currentElement = this.type.getCurrentElement();
         if (currentElement != 2 && (currentElement != 0 || !this.isValidTarget(this.entityLivingBase))) {
            this.entityLivingBase = this.pickNewTarget();
         }

         boolean validTarget = this.isValidTarget(this.entityLivingBase);
         if (validTarget) {
            if (this.autoStrafe.isEnabled()
               && (minecraft.thePlayer.movementInput.moveForward != 0.0F || minecraft.thePlayer.movementInput.moveStrafe != 0.0F)
               && !minecraft.thePlayer.onGround) {
               PlayerUtils.setMotionInMoveDirection(
                  Math.sqrt(minecraft.thePlayer.motionX * minecraft.thePlayer.motionX + minecraft.thePlayer.motionZ * minecraft.thePlayer.motionZ)
               );
            }

            int currentElement2 = this.killaura.getCurrentElement();
            int currentElement3 = this.rotationType.getCurrentElement();
            if (currentElement2 == 0) {
               this.rotYaw = minecraft.thePlayer.rotationYaw;
               this.rotPitch = minecraft.thePlayer.rotationPitch;
            } else if (currentElement2 == 1) {
               float[] rotationsToEntityExact = RotationUtils.getRotationsToEntityExact(this.entityLivingBase, this.rotYaw, this.rotPitch);
               this.rotYaw = rotationsToEntityExact[0];
               this.rotPitch = rotationsToEntityExact[1];
               this.rotPitch = MathHelper.clamp_float(this.rotPitch, -90.0F, 90.0F);
            } else if (currentElement2 == 2) {
               float[] rotationsToEntity = RotationUtils.getRotationsToEntity(this.entityLivingBase, this.rotYaw, this.rotPitch, true);
               this.rotYaw += 0.15F;
               this.rotPitch += 0.15F;
               if (minecraft.thePlayer.ticksExisted % 2 == 0) {
                  this.rotYaw = RotationUtils.limitAngleChange(this.rotYaw, rotationsToEntity[0], 180.0F);
                  this.rotYaw = ((int)(this.rotYaw * 1000.0F) + 4) / 5 * 5 / 1000.0F;
               }

               this.rotPitch = RotationUtils.limitAngleChange(this.rotPitch, rotationsToEntity[1], 30.0F);
               this.rotPitch = ((int)(this.rotPitch * 100.0F) + 4) / 5 * 5 / 100.0F;
               float f = minecraft.gameSettings.mouseSensitivity * 0.6F + 0.2F;
               float f2 = f * f * f * 1.2F;
               this.rotYaw = this.rotYaw - this.rotYaw % f2;
               this.rotPitch = this.rotPitch - this.rotPitch % f2;
               this.rotPitch = MathHelper.clamp_float(this.rotPitch, -90.0F, 90.0F);
            } else if (currentElement2 == 3) {
               float[] rotationsToEntity2 = RotationUtils.getRotationsToEntity(this.entityLivingBase, this.rotYaw, this.rotPitch, true);
               if (minecraft.thePlayer.ticksExisted % 2 == 0) {
                  this.rotYaw = RotationUtils.limitAngleChange(this.rotYaw, rotationsToEntity2[0], 70.0F);
                  this.rotYaw = ((int)(this.rotYaw * 1000.0F) + 4) / 5 * 5 / 1000.0F;
               }

               this.rotPitch = RotationUtils.limitAngleChange(this.rotPitch, rotationsToEntity2[1], 9.0F);
               this.rotPitch = ((int)(this.rotPitch * 100.0F) + 4) / 5 * 5 / 100.0F;
               this.rotPitch = MathHelper.clamp_float(this.rotPitch, -90.0F, 90.0F);
            }

            minecraft.thePlayer.rotationYawHead = preUpdateEvent.rotationYaw;
            minecraft.thePlayer.renderYawOffset = preUpdateEvent.rotationYaw;
            minecraft.thePlayer.rotationPitchHead = preUpdateEvent.rotationPitch;
            if (currentElement3 == 0) {
               minecraft.thePlayer.rotationYaw = this.rotYaw;
               minecraft.thePlayer.rotationPitch = this.rotPitch;
            } else if (currentElement3 == 1) {
               preUpdateEvent.rotationYaw = this.rotYaw;
               preUpdateEvent.rotationPitch = this.rotPitch;
            }

            if (!this.criticalsApplied) {
               Criticals criticals2 = ModuleManager.criticals;
               if (criticals2.isEnabled()
                  && PlayerUtils.isOnSolidGround()
                  && this.isAttackReady()
                  && this.entityLivingBase.hurtResistantTime <= 15
                  && criticals2.criticalTimer.hasElapsed(200L)
                  && (PlayerUtils.isMoving() || criticals2.criticalTimer.hasElapsed(800L))) {
                  this.criticalsApplied = true;
               }
            }
         } else {
            this.rotYaw = minecraft.thePlayer.rotationYaw;
            this.rotPitch = minecraft.thePlayer.rotationPitch;
            if (!this.hasBlockEntities() && (!this.usesBlockDelays() || this.ticksSinceBlock > this.blockTicks.getCurrentValue())) {
               this.stopAutoBlock(true);
            }
         }

         if (this.autoWeapon.isEnabled() && validTarget && this.isAttackReady()) {
            int slotOfClass = InventoryUtils.findSlotOfClass(ItemSword.class, true);
            slotOfClass = slotOfClass == -1 ? InventoryUtils.findSlotOfClass(ItemAxe.class, true) : slotOfClass;
            if (slotOfClass != -1 && minecraft.thePlayer.inventory.currentItem != slotOfClass) {
               this.sendPacket(new C09PacketHeldItemChange(slotOfClass));
               minecraft.thePlayer.inventory.currentItem = slotOfClass;
            }
         }

         int currentElement4 = this.autoBlockMode.getCurrentElement();
         if (currentElement4 != 2 && this.isAttackReady() && (!this.usesBlockDelays() || this.ticksSinceBlock > this.unblockTicks.getCurrentValue())) {
            this.stopAutoBlock(false);
         }

         if (this.attackAt.getCurrentElement() == 0
            && validTarget
            && this.isAttackReady()
            && (!this.usesBlockDelays() || this.ticksSinceBlock > this.attackTicks.getCurrentValue())) {
            if (this.shouldAutoBlock(currentElement4 != 2) && currentElement4 == 2) {
               this.startAutoBlock();
            }

            this.attack();
         }
      }
   };
   private float rotYaw;
   private boolean attackedSinceBlock = false;
   @EventLink
   public final Listener<OnUpdateWalkingPlayerEvent> onOnUpdateWalkingPlayer = onUpdateWalkingPlayerEvent -> {
      if (!ModuleManager.scaffold.isEnabled()) {
         int currentElement = this.autoBlockMode.getCurrentElement();
         boolean validTarget = this.isValidTarget(this.entityLivingBase);
         if (this.attackAt.getCurrentElement() == 1
            && validTarget
            && this.isAttackReady()
            && (!this.usesBlockDelays() || this.ticksSinceBlock > this.attackTicks.getCurrentValue())) {
            this.attackedSinceBlock = true;
            this.attack();
         }

         if (this.autoBlock.isEnabled()) {
            if (this.attackAt.getCurrentElement() == 0 && currentElement == 2) {
               return;
            }

            if (this.attackAt.getCurrentElement() == 1 && !this.attackedSinceBlock) {
               return;
            }

            if (validTarget && this.shouldAutoBlock(currentElement != 2) && (!this.usesBlockDelays() || this.ticksSinceBlock > this.blockTicks.getCurrentValue())) {
               this.attackedSinceBlock = false;
               this.startAutoBlock();
            }
         }
      }
   };

   private void attack() {
      Entity entityLivingBase4 = this.entityLivingBase;
      if (this.rayCast.isEnabled()) {
         MovingObjectPosition movingObjectPosition = RotationUtils.rayTraceEntities(minecraft.thePlayer, this.attackDistance.getCurrentValue(), this.rotYaw, this.rotPitch);
         if (movingObjectPosition == null || movingObjectPosition.typeOfHit != MovingObjectType.ENTITY) {
            return;
         }

         entityLivingBase4 = movingObjectPosition.entityHit;
      }

      this.attackTimer.reset();
      if (this.criticalsApplied) {
         this.criticalsApplied = false;
         ModuleManager.criticals.sendCriticalPackets();
      }

      minecraft.thePlayer.swingItemNoPacket();
      if (ThreadLocalRandom.current().nextInt(1, 100) % this.hitChance.getCurrentValue() == 0.0F) {
         minecraft.playerController.attackEntity(minecraft.thePlayer, entityLivingBase4);
      }
   }

   private void stopAutoBlock(boolean flag2) {
      if (this.autoBlocked) {
         int currentElement = this.autoBlockMode.getCurrentElement();
         if ((currentElement == 1 || currentElement == 4 || flag2) && currentElement != 3) {
            this.sendPacketUnregistered(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, new BlockPos(-1, -1, -1), EnumFacing.DOWN));
         }

         this.autoBlocked = false;
         ItemRenderer.flag = false;
      }
   }

   public Killaura() {
      super(Category.COMBAT, "Killaura", "");
   }

   private boolean isValidTarget(Entity entity3) {
      return this.isValidTarget(entity3, false);
   }

   private void startAutoBlock() {
      if (this.autoBlock.isEnabled()) {
         int currentElement = this.autoBlockMode.getCurrentElement();
         if (currentElement == 2 || !this.autoBlocked && (!this.usesBlockDelays() || this.ticksSinceBlock > this.blockTicks.getCurrentValue())) {
            if (currentElement != 3 && this.interactAutoBlock.isEnabled() && this.entityLivingBase != null) {
               Vec3 positionEyes = minecraft.getRenderViewEntity().getPositionEyes(1.0F);
               float collisionBorderSize = this.entityLivingBase.getCollisionBorderSize();
               AxisAlignedBB axisAlignedBB = this.entityLivingBase.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
               float f = (float)(-Math.toRadians(this.rotYaw) - Math.PI);
               float f2 = MathHelper.cos(f);
               float f3 = MathHelper.sin(f);
               float f4 = (float)(-Math.toRadians(this.rotPitch));
               float f5 = -MathHelper.cos(f4);
               float f6 = MathHelper.sin(f4);
               float f7 = Math.max(this.attackDistance.getCurrentValue(), minecraft.thePlayer.getDistanceToEntity(this.entityLivingBase) + 1.0F);
               Vec3 vec3 = positionEyes.addVector(f3 * f5 * f7, f6 * f7, f2 * f5 * f7);
               MovingObjectPosition movingObjectPosition = axisAlignedBB.calculateIntercept(positionEyes, vec3);
               if (movingObjectPosition != null) {
                  Vec3 hitVec2 = movingObjectPosition.hitVec;
                  this.sendPacket(
                     new C02PacketUseEntity(
                        this.entityLivingBase,
                        new Vec3(
                           hitVec2.xCoord - this.entityLivingBase.posX, hitVec2.yCoord - this.entityLivingBase.posY, hitVec2.zCoord - this.entityLivingBase.posZ
                        )
                     )
                  );
                  this.sendPacket(new C02PacketUseEntity(this.entityLivingBase, net.minecraft.network.play.client.C02PacketUseEntity.Action.INTERACT));
               }
            }

            if (currentElement == 0 || currentElement == 1 || currentElement == 4) {
               this.sendPacketUnregistered(new C08PacketPlayerBlockPlacement(BlockPos.ORIGIN, 255, minecraft.thePlayer.getHeldItem(), 0.0F, 0.0F, 0.0F));
            } else if (currentElement == 2) {
               BlockPos blockPos = new BlockPos(-1, -1, -1);
               ItemStack currentItem2 = minecraft.thePlayer.inventory.getCurrentItem();
               float f8 = 0.0F;
               float f9 = 0.0F;
               float f10 = 0.0F;
               ItemStack itemStack = currentItem2;
               this.sendPacketUnregistered(new C08PacketPlayerBlockPlacement(blockPos, 255, itemStack, f10, f9, f8));
            }

            this.autoBlocked = true;
            ItemRenderer.flag = true;
            this.ticksSinceBlock = 0;
         }
      }
   }

   public float getRotYaw() {
      return this.rotYaw;
   }

   public boolean isAttackReady() {
      if (this.randomizeCps.isEnabled()) {
         int i = (int)(20 / ThreadLocalRandom.current().nextInt(6, 14) * 40.0);
         return this.attackTimer.hasElapsed(i + minecraft.thePlayer.getRNG().nextInt(140));
      } else {
         return this.attackTimer.hasElapsed(1000L / (long)this.cps.getCurrentValue());
      }
   }

   public Stopwatch getStopwatch() {
      return this.attackTimer;
   }

   private EntityLivingBase pickNewTarget() {
      List list = minecraft.theWorld
         .loadedEntityList
         .stream()
         .filter(this::isValidTarget)
         .map(entity2 -> (EntityLivingBase)entity2)
         .collect(Collectors.toList());
      if (list.size() < 1) {
         return null;
      }

      EntityLivingBase entityLivingBase4 = null;
      int currentElement = this.priority.getCurrentElement();
      if (currentElement == 0) {
         Collections.shuffle(list);
         entityLivingBase4 = (EntityLivingBase) list.stream()
            .filter((entityLivingBase2) -> novoline.getRelationManager().get(((EntityLivingBase) entityLivingBase2).getName()) == RelationType.TARGET)
            .findFirst()
            .orElse((EntityLivingBase)list.get(0));
      } else if (currentElement == 1) {
         list.sort(Comparator.comparingDouble((EntityLivingBase entityLivingBase2) -> entityLivingBase2.getDistance(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ)));
         entityLivingBase4 = (EntityLivingBase) list.stream()
            .filter((entityLivingBase2) -> novoline.getRelationManager().get(((EntityLivingBase) entityLivingBase2).getName()) == RelationType.TARGET)
            .findFirst()
            .orElse((EntityLivingBase)list.get(0));
      } else if (currentElement == 2) {
         list.sort((entityLivingBase2, entityLivingBase3) -> Float.compare(((EntityLivingBase) entityLivingBase2).getHealth(), ((EntityLivingBase) entityLivingBase3).getHealth()));
         entityLivingBase4 = (EntityLivingBase) list.stream()
            .filter((entityLivingBase2) -> novoline.getRelationManager().get(((EntityLivingBase) entityLivingBase2).getName()) == RelationType.TARGET)
            .findFirst()
            .orElse((EntityLivingBase)list.get(0));
      } else if (currentElement == 3) {
         if (this.fov.getCurrentValue() >= 360.0F) {
            entityLivingBase4 = (EntityLivingBase) list.stream()
               .filter((entityLivingBase2) -> novoline.getRelationManager().get(((EntityLivingBase) entityLivingBase2).getName()) == RelationType.TARGET)
               .findFirst()
               .orElse((EntityLivingBase)list.get(0));
         } else {
            ScaledResolution scaledResolution2 = new ScaledResolution(minecraft);
            float f = scaledResolution2.getScaledWidth() / 2.0F;
            float f2 = scaledResolution2.getScaledHeight() / 2.0F;
            Entity entity3 = null;
            float f3 = this.fov.getCurrentValue() / 360.0F * (float)Math.hypot(scaledResolution2.getScaledWidth(), scaledResolution2.getScaledHeight());
            Vector3f vector3f = new Vector3f();

            for (Entity entity4 : (Iterable<Entity>) list) {
               vector3f.set(
                  (float)entity4.posX - (float)TileEntityRendererDispatcher.staticPlayerX,
                  (float)entity4.posY + entity4.getEyeHeight() - (float)TileEntityRendererDispatcher.staticPlayerY,
                  (float)entity4.posZ - (float)TileEntityRendererDispatcher.staticPlayerZ
               );
               float[] fArray = RenderUtils.worldToScreen(vector3f.x, vector3f.y, vector3f.z);
               if (fArray != null) {
                  float f4 = f - fArray[0];
                  float f5 = f2 - fArray[1];
                  float f6 = MathHelper.abs((float)Math.sqrt(f4 * f4 + f5 * f5));
                  if (f3 >= f6) {
                     f3 = f6;
                     entity3 = entity4;
                  }
               }
            }

            entityLivingBase4 = (EntityLivingBase)entity3;
         }
      }

      return entityLivingBase4;
   }

   private boolean shouldAutoBlock(boolean flag2) {
      if (this.autoBlocked && flag2) {
         return false;
      }

      ItemStack heldItem = minecraft.thePlayer.getHeldItem();
      return heldItem != null && heldItem.getItem() instanceof ItemSword ? this.hasBlockEntities() : false;
   }

   private boolean hasBlockEntities() {
      return minecraft.theWorld.loadedEntityList.stream().filter(entity2 -> this.isValidTarget(entity2, true)).collect(Collectors.toList()).size() > 0;
   }

   @Override
   protected void onDisable() {
      this.stopAutoBlock(true);
   }

   public boolean usesBlockDelays() {
      return this.autoBlockMode.getCurrentElement() != 2 && this.autoBlock.isEnabled() && this.autoBlockDelays.isEnabled();
   }

   public float getRotPitch() {
      return this.rotPitch;
   }

   public boolean isAutoBlocked() {
      return this.autoBlocked;
   }

   @Override
   protected void onEnable() {
      if (minecraft.thePlayer != null) {
         this.rotYaw = minecraft.thePlayer.rotationYaw;
         this.rotPitch = minecraft.thePlayer.rotationPitch;
      }
   }

   private boolean isValidTarget(Entity entity3, boolean flag2) {
      if (!(entity3 instanceof EntityLivingBase entityLivingBase4)) {
         return false;
      } else {
         if (entity3 == minecraft.thePlayer) {
            return false;
         }

         if (novoline.getRelationManager().get(entity3.getName()) == RelationType.FRIEND) {
            return false;
         }

         if (this.ignoreRules.isSelected(0) && entity3 instanceof EntityPlayer entityPlayer && PlayerUtils.isSameTeam(minecraft.thePlayer, entityPlayer)) {
            return false;
         }

         if (this.ignoreRules.isSelected(1) && entity3.isInvisible()) {
            return false;
         }

         if (this.ignoreRules.isSelected(2) && !entity3.isEntityAlive()) {
            return false;
         }

         float f = flag2 ? this.blockDistance.getCurrentValue() : this.attackDistance.getCurrentValue();
         float[] rotationsToEntityExact = RotationUtils.getRotationsToEntityExact(entityLivingBase4, minecraft.thePlayer.rotationYaw, minecraft.thePlayer.rotationPitch);
         MovingObjectPosition movingObjectPosition = RotationUtils.rayTraceEntities(minecraft.thePlayer, this.attackDistance.getCurrentValue(), rotationsToEntityExact[0], rotationsToEntityExact[1]);
         boolean flag3 = movingObjectPosition != null && movingObjectPosition.typeOfHit == MovingObjectType.ENTITY;
         if (!flag3 && !this.throughWalls.isEnabled()) {
            return false;
         }

         if (!flag3) {
            f = this.wallDistance.getCurrentValue();
         }

         if (entity3.getDistanceSq(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ) > f * f) {
            return false;
         } else {
            return this.ignoreRules.isSelected(3)
                  && ModuleManager.antibot.isEnabled()
                  && entity3 instanceof EntityPlayer entityPlayer2
                  && ModuleManager.antibot.isPlayerBot(entityPlayer2)
               ? false
               : this.targets.isSelected(0) && entity3 instanceof EntityPlayer
                  || this.targets.isSelected(1) && entity3 instanceof IMob
                  || this.targets.isSelected(2) && !(entity3 instanceof IMob) && entity3 instanceof IAnimals;
         }
      }
   }
}
