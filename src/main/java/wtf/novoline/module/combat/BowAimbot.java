package wtf.novoline.module.combat;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.apache.commons.lang3.tuple.Pair;
import wtf.novoline.event.PreUpdateEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.MultiComboSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.combat.RotationUtils;
import wtf.novoline.utils.player.PlayerUtils;

public class BowAimbot extends Module {
   private float controlPitch2;
   private float startYaw;
   private float startPitch;
   private final MultiComboSetting bowAimbot = settingManager.register(
      new MultiComboSetting(this, "Targets").add("Players", true).add("Teammates", false).add("Bots", false).add("Animals", false).add("Mobs", false)
   );
   private float controlYaw1;
   private Entity entity;
   private final CheckboxSetting silentRotation = settingManager.register(new CheckboxSetting(this, "Silent rotation", false));
   private float targetYaw;
   private float originalPitch;
   private float controlYaw2;
   private final NumberSetting maxDistance = settingManager.register(new NumberSetting(this, "Max distance", 150, 1, 250));
   private final NumberSetting hitboxAimHeight = settingManager.register(new NumberSetting(this, "Hitbox aim height", 0.75F, 0.001F, 1.001F));
   private float currentYaw;
   private final NumberSetting predictionMultiplier = settingManager.register(new NumberSetting(this, "Prediction multiplier", 1.001F, 0.001F, 2.001F));
   private final NumberSetting predictionAdd = settingManager.register(new NumberSetting(this, "Prediction add", 5.001F, 0.001F, 20.001F));
   private final NumberSetting rotationSpeed = settingManager.register(new NumberSetting(this, "Rotation speed", 0.25F, 0.001F, 1.001F));
   private static final Map<Integer, Double> arrowSpeedByTicks = new HashMap<>();
   private final NumberSetting rotationRandomization = settingManager.register(new NumberSetting(this, "Rotation randomization", 10, 0, 50));
   private int drawTicks = 0;
   private float currentPitch;
   private int phase = 1;
   private int cooldown = 0;
   private float originalYaw;
   private static double GRAVITY = 0.05;
   private static double arrowSpeed = 3.0;
   private float targetPitch;
   @EventLink
   private final Listener<PreUpdateEvent> onPreUpdate = preUpdateEvent -> {
      if (!(minecraft.currentScreen instanceof GuiContainer)
         && minecraft.thePlayer.getHeldItem() != null
         && minecraft.thePlayer.getHeldItem().getItem() instanceof ItemBow
         && minecraft.thePlayer.isUsingItem()) {
         this.drawTicks++;
         Entity bestTarget2 = this.bestTarget;
         this.bestTarget = this.getLowestAngleEntity();
         if (this.phase != 3
            || minecraft.currentScreen instanceof GuiContainer
            || minecraft.thePlayer.getHeldItem() != null && !(minecraft.thePlayer.getHeldItem().getItem() instanceof ItemBow)) {
            this.entity = null;
         }

         if (this.phase == 1 && this.cooldown <= 0 && this.bestTarget != null && this.drawTicks >= 1) {
            this.startYaw = minecraft.thePlayer.rotationYaw;
            this.startPitch = minecraft.thePlayer.rotationPitch;
            this.updateTargetRotation();
            this.updateControlPoints();
            this.originalYaw = minecraft.thePlayer.rotationYaw;
            this.originalPitch = minecraft.thePlayer.rotationPitch;
            this.curveProgress = 0.0F;
            this.phase = 2;
         }

         if (this.phase == 2 && this.cooldown <= 0 && this.bestTarget != null && this.drawTicks >= 1) {
            this.updateTargetRotation();
            this.curveProgress = this.curveProgress + this.rotationSpeed.getCurrentValue();
            this.curveProgress = Math.min(this.curveProgress, 1.0F);
            this.updateCurveRotation();
            this.applyRotation(preUpdateEvent);
            if (this.currentYaw == this.targetYaw && this.currentPitch == this.targetPitch) {
               this.cooldown = 1;
               this.phase = 3;
            }
         }

         if (this.phase == 3) {
            if (this.bestTarget != null && bestTarget2 != null && this.bestTarget != bestTarget2 && this.rotationSpeed.getCurrentValue() < 1.0F) {
               this.entity = null;
               this.applyRotation(preUpdateEvent);
               this.startYaw = this.currentYaw;
               this.startPitch = this.currentPitch;
               this.updateTargetRotation();
               this.updateControlPoints();
            } else if ((this.bestTarget == null || this.drawTicks == 0) && this.rotationSpeed.getCurrentValue() < 1.0F) {
               this.entity = null;
               this.startYaw = this.currentYaw;
               this.startPitch = this.currentPitch;
               if (this.silentRotation.isEnabled()) {
                  this.targetYaw = minecraft.thePlayer.rotationYaw;
                  this.targetPitch = minecraft.thePlayer.rotationPitch;
               } else {
                  this.targetYaw = this.originalYaw;
                  this.targetPitch = this.originalPitch;
               }

               this.updateControlPoints();
               this.curveProgress = 0.0F;
               this.phase = 4;
            } else if (this.cooldown <= 0 && this.bestTarget != null && this.drawTicks >= 1) {
               Vec3 predictedAimPoint = this.getPredictedAimPoint(this.bestTarget);
               arrowSpeed = 3.0;
               if (this.drawTicks < 4) {
                  return;
               }

               if (this.drawTicks <= 22) {
                  arrowSpeed = arrowSpeedByTicks.get(this.drawTicks);
               }

               float[] fArray = null;
               if (predictedAimPoint != null) {
                  fArray = this.getBowAngles(GRAVITY, arrowSpeed, predictedAimPoint.xCoord, predictedAimPoint.yCoord, predictedAimPoint.zCoord);
               }

               if (this.drawTicks >= 4 && predictedAimPoint != null && fArray != null) {
                  this.entity = this.bestTarget;
                  this.currentYaw = fArray[0];
                  this.currentPitch = fArray[1];
                  this.applyRotation(preUpdateEvent);
               } else if (this.cooldown <= 0 && this.drawTicks >= 1) {
                  float targetYaw2 = this.targetYaw;
                  float targetPitch2 = this.targetPitch;
                  this.updateTargetRotation();
                  this.currentYaw = this.targetYaw;
                  this.currentPitch = this.targetPitch;
                  this.targetYaw = targetYaw2;
                  this.targetPitch = targetPitch2;
                  this.applyRotation(preUpdateEvent);
               }
            }
         }

         if (this.phase == 4 && this.cooldown <= 0) {
            if (this.silentRotation.isEnabled()) {
               this.targetYaw = minecraft.thePlayer.rotationYaw;
               this.targetPitch = minecraft.thePlayer.rotationPitch;
            }

            this.curveProgress = this.curveProgress + this.rotationSpeed.getCurrentValue();
            this.curveProgress = Math.min(this.curveProgress, 1.0F);
            this.updateCurveRotation();
            this.applyRotation(preUpdateEvent);
            if (this.currentYaw == this.targetYaw && this.currentPitch == this.targetPitch) {
               this.cooldown = this.phase = 1;
            }
         }

         if (!(minecraft.currentScreen instanceof GuiContainer)
            && minecraft.thePlayer.getHeldItem() != null
            && !(minecraft.thePlayer.getHeldItem().getItem() instanceof ItemBow)
            && !minecraft.thePlayer.isUsingItem()) {
            this.drawTicks = 0;
         }

         this.cooldown--;
      } else {
         this.entity = null;
         this.phase = 1;
         this.drawTicks = 0;
      }
   };
   private float controlPitch1;
   private float curveProgress;
   private Entity bestTarget;

   private double distance2D(double d, double d2, double d3, double d4) {
      return Math.sqrt((d3 - d) * (d3 - d) + (d4 - d2) * (d4 - d2));
   }

   private float[] getRotationsTo(EntityLivingBase entityLivingBase2) {
      return RotationUtils.getRotationsToEntityExact(entityLivingBase2, minecraft.thePlayer.rotationYaw, minecraft.thePlayer.rotationPitch);
   }

   private boolean canHitTarget(EntityLivingBase entityLivingBase2) {
      AxisAlignedBB entityBoundingBox = entityLivingBase2.getEntityBoundingBox();
      double posY2 = entityLivingBase2.posY;
      if (!Double.isNaN(entityBoundingBox.minY) && !Double.isNaN(entityBoundingBox.maxY)) {
         entityLivingBase2.posY = entityLivingBase2.posY + (entityBoundingBox.maxY - entityBoundingBox.minY) * 2.0 / 3.0;
      }

      float[] rotationsToEntityExact = RotationUtils.getRotationsToEntityExact(entityLivingBase2, minecraft.thePlayer.rotationYaw, minecraft.thePlayer.rotationPitch);
      MovingObjectPosition movingObjectPosition = RotationUtils.rayTraceEntities(minecraft.thePlayer, 160.0, rotationsToEntityExact[0], rotationsToEntityExact[1]);
      entityLivingBase2.posY = posY2;
      return movingObjectPosition != null && movingObjectPosition.typeOfHit == MovingObjectType.ENTITY;
   }

   private boolean isValidTarget(EntityLivingBase entityLivingBase2) {
      if (entityLivingBase2.equals(minecraft.thePlayer) || entityLivingBase2.getName() == null) {
         return false;
      } else if (entityLivingBase2 instanceof EntityPlayer && !this.bowAimbot.isSelected(0)) {
         return false;
      } else if (entityLivingBase2 instanceof EntityPlayer entityPlayer && PlayerUtils.isSameTeam(minecraft.thePlayer, entityPlayer) && !this.bowAimbot.isSelected(1)) {
         return false;
      } else if (entityLivingBase2 instanceof EntityPlayer entityPlayer2
         && ModuleManager.antibot.isEnabled()
         && ModuleManager.antibot.isPlayerBot(entityPlayer2)
         && !this.bowAimbot.isSelected(2)) {
         return false;
      } else if ((entityLivingBase2 instanceof EntityAnimal || entityLivingBase2 instanceof EntityVillager) && !this.bowAimbot.isSelected(3)) {
         return false;
      } else if (entityLivingBase2 instanceof EntityMob && !this.bowAimbot.isSelected(4)) {
         return false;
      } else {
         return !(entityLivingBase2 instanceof EntityMob) && !(entityLivingBase2 instanceof EntityPlayer) && !(entityLivingBase2 instanceof EntityAnimal) && !(entityLivingBase2 instanceof EntityVillager)
            ? false
            : minecraft.thePlayer.getDistanceToEntity(entityLivingBase2) < this.maxDistance.getCurrentValue() && this.canHitTarget(entityLivingBase2);
      }
   }

   private void applyRotation(PreUpdateEvent preUpdateEvent2) {
      preUpdateEvent2.rotationYaw = this.currentYaw;
      preUpdateEvent2.rotationPitch = this.currentPitch;
      if (!this.silentRotation.isEnabled()) {
         minecraft.thePlayer.rotationYaw = this.currentYaw;
         minecraft.thePlayer.rotationPitch = this.currentPitch;
      }
   }

   private float[][] randomizeControlPoints(float f, float f2, float f3, float f4, float f5) {
      float f6 = this.randomInRange(f, f3, f5);
      float f7 = this.randomInRange(f, f3, f5);
      float f8 = this.randomInRange(f2, f4, f5);
      float f9 = this.randomInRange(f2, f4, f5);
      return new float[][]{{f6, f8}, {f7, f9}};
   }

   private double getFlightTime(double d, double d2, double d3, double d4, double d5, double d6, double d7) {
      return this.distance2D(minecraft.thePlayer.posX, minecraft.thePlayer.posZ, d5, d7) / (Math.cos(d3) * d2);
   }

   private boolean isFullCubeAt(double d, double d2, double d3) {
      return minecraft.theWorld.getBlockState(new BlockPos(d, d2, d3)).getBlock().isFullCube();
   }

   private double getHeightAboveGround(Entity entity3) {
      AxisAlignedBB entityBoundingBox = entity3.getEntityBoundingBox();

      for (double minY2 = entityBoundingBox.minY; minY2 > 0.0; minY2 -= 0.1) {
         if (this.isFullCubeAt(entityBoundingBox.minX, minY2, entityBoundingBox.minZ)
            || this.isFullCubeAt(entityBoundingBox.minX, minY2, entityBoundingBox.maxZ)
            || this.isFullCubeAt(entityBoundingBox.maxX, minY2, entityBoundingBox.minZ)
            || this.isFullCubeAt(entityBoundingBox.maxX, minY2, entityBoundingBox.maxZ)) {
            return entityBoundingBox.minY - minY2;
         }
      }

      return -1.0;
   }

   static {
      arrowSpeedByTicks.put(4, 0.32);
      arrowSpeedByTicks.put(5, 0.44);
      arrowSpeedByTicks.put(6, 0.56);
      arrowSpeedByTicks.put(7, 0.69);
      arrowSpeedByTicks.put(8, 0.82);
      arrowSpeedByTicks.put(9, 0.96);
      arrowSpeedByTicks.put(10, 1.15);
      arrowSpeedByTicks.put(11, 1.25);
      arrowSpeedByTicks.put(12, 1.4);
      arrowSpeedByTicks.put(13, 1.56);
      arrowSpeedByTicks.put(14, 1.71);
      arrowSpeedByTicks.put(15, 1.88);
      arrowSpeedByTicks.put(16, 2.06);
      arrowSpeedByTicks.put(17, 2.24);
      arrowSpeedByTicks.put(18, 2.41);
      arrowSpeedByTicks.put(19, 2.6);
      arrowSpeedByTicks.put(20, 2.81);
      arrowSpeedByTicks.put(21, 2.97);
      arrowSpeedByTicks.put(22, 3.0);
   }

   private void updateControlPoints() {
      float[][] fArrays = this.randomizeControlPoints(
         this.startYaw, this.startPitch, this.targetYaw, this.targetPitch, this.rotationRandomization.getCurrentValue()
      );
      this.controlYaw1 = fArrays[0][0];
      this.controlPitch1 = fArrays[0][1];
      this.controlYaw2 = fArrays[1][0];
      this.controlPitch2 = fArrays[1][1];
   }

   private float randomOffset(float f, float f2) {
      return ThreadLocalRandom.current().nextFloat() * (f2 + f) - f;
   }

   public Entity getEntity() {
      return this.entity;
   }

   private double[] bezier(double d, double d2, double d3, double d4, double d5, double d6, double d7, double d8, double d9) {
      double d10 = Math.pow(1.0 - d, 3.0) * d2
         + 3.0 * Math.pow(1.0 - d, 2.0) * d * d4
         + 3.0 * (1.0 - d) * (d * d) * d6
         + Math.pow(d, 3.0) * d8;
      double d11 = Math.pow(1.0 - d, 3.0) * d3
         + 3.0 * Math.pow(1.0 - d, 2.0) * d * d5
         + 3.0 * (1.0 - d) * (d * d) * d7
         + Math.pow(d, 3.0) * d9;
      return new double[]{d10, MathHelper.clamp_double(d11, -90.0, 90.0)};
   }

   private float randomInRange(float f, float f2, float f3) {
      return this.randomOffset(Math.min(f, f2) - f3, Math.max(f, f2) + f3);
   }

   public BowAimbot() {
      super(Category.COMBAT, "BowAimbot", "");
   }

   private void updateTargetRotation() {
      float[] rotationsToPoint = RotationUtils.getRotationsToPoint(
         this.bestTarget.posX, this.bestTarget.posY + minecraft.thePlayer.getDistanceToEntity(this.bestTarget) / 2.0F, this.bestTarget.posZ
      );
      this.targetYaw = rotationsToPoint[0];
      this.targetPitch = rotationsToPoint[1];
   }

   private EntityLivingBase getLowestAngleEntity() {
      return minecraft.theWorld
         .loadedEntityList
         .stream()
         .filter(entity2 -> entity2 instanceof EntityLivingBase entityLivingBase2 && this.isValidTarget(entityLivingBase2))
         .map(entity2 -> (EntityLivingBase)entity2)
         .map(entityLivingBase -> {
            float[] rotationsTo = this.getRotationsTo(entityLivingBase);
            float f = Math.abs(rotationsTo[0] - minecraft.thePlayer.rotationYaw) + Math.abs(rotationsTo[1] - minecraft.thePlayer.rotationPitch);
            return Pair.of(entityLivingBase, f);
         })
         .sorted(Comparator.comparingDouble(Pair::getRight))
         .map(Pair::getKey)
         .findFirst()
         .orElse(null);
   }

   private void updateCurveRotation() {
      double[] dArray = this.bezier(
         this.curveProgress,
         this.startYaw,
         this.startPitch,
         this.controlYaw1,
         this.controlPitch1,
         this.controlYaw2,
         this.controlPitch2,
         this.targetYaw,
         this.targetPitch
      );
      this.currentYaw = (float)dArray[0];
      this.currentPitch = (float)dArray[1];
   }

   private float[] getBowAngles(double d, double d2, double d3, double d4, double d5) {
      double d6 = minecraft.thePlayer.posY + minecraft.thePlayer.getEyeHeight();
      double d7 = this.distance2D(d3, d5, minecraft.thePlayer.posX, minecraft.thePlayer.posZ);
      double d8 = d4 - d6;
      double d9 = Math.pow(d2, 4.0) - d * (d * (d7 * d7) + 2.0 * d8 * (d2 * d2));
      if (d9 >= 0.0) {
         double d10 = (d2 * d2 + Math.sqrt(d9)) / (d * d7);
         double d11 = (d2 * d2 - Math.sqrt(d9)) / (d * d7);
         Math.atan(d10);
         double d12 = Math.atan(d11);
         double d13 = d12 * 180.0 / Math.PI;
         float f = RotationUtils.getRotationsToPoint(d3, d4, d5)[0];
         float f2 = 0.0F - (float)d13;
         return new float[]{f, f2};
      } else {
         return null;
      }
   }

   private Vec3 getPredictedAimPoint(Entity entity3) {
      double posY2 = entity3.posY;
      AxisAlignedBB entityBoundingBox = entity3.getEntityBoundingBox();
      if (!Double.isNaN(entityBoundingBox.maxY) && !Double.isNaN(entityBoundingBox.minY)) {
         posY2 += (entityBoundingBox.maxY - entityBoundingBox.minY) * this.hitboxAimHeight.getCurrentValue();
      }

      float[] bowAngles = this.getBowAngles(GRAVITY, arrowSpeed, entity3.posX, posY2, entity3.posZ);
      if (bowAngles != null) {
         double d = -1.0F * bowAngles[1] * (Math.PI / 180.0);
         this.getFlightTime(GRAVITY, arrowSpeed, d, minecraft.thePlayer.posY - posY2, entity3.posX, posY2, entity3.posZ);
         double currentValue = this.predictionMultiplier.getCurrentValue();
         double currentValue2 = this.predictionAdd.getCurrentValue();
         double d2 = (entity3.posX - entity3.prevPosX) * currentValue + currentValue2;
         double d3 = (entity3.posZ - entity3.prevPosZ) * currentValue + currentValue2;
         double d4 = 0.0;
         double heightAboveGround = this.getHeightAboveGround(entity3);
         if (heightAboveGround != -1.0) {
            d4 -= heightAboveGround;
         }

         return new Vec3(entity3.posX + d2, posY2 + d4, entity3.posZ + d3);
      } else {
         return null;
      }
   }
}
