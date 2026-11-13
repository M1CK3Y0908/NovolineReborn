package net.optifine.entity.model.anim;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.optifine.expr.ExpressionType;
import net.optifine.expr.IExpressionFloat;

public enum RenderEntityParameterFloat implements IExpressionFloat {
   LIMB_SWING("limb_swing"),
   LIMB_SWING_SPEED("limb_speed"),
   AGE("age"),
   HEAD_YAW("head_yaw"),
   HEAD_PITCH("head_pitch"),
   SCALE("scale"),
   HEALTH("health"),
   HURT_TIME("hurt_time"),
   IDLE_TIME("idle_time"),
   MAX_HEALTH("max_health"),
   MOVE_FORWARD("move_forward"),
   MOVE_STRAFING("move_strafing"),
   PARTIAL_TICKS("partial_ticks"),
   POS_X("pos_x"),
   POS_Y("pos_y"),
   POS_Z("pos_z"),
   REVENGE_TIME("revenge_time"),
   SWING_PROGRESS("swing_progress");

   private String name;
   private RenderManager renderManager;
   private static final RenderEntityParameterFloat[] VALUES = values();

   public String getName() {
      return this.name;
   }

   public static RenderEntityParameterFloat parse(String str) {
      if (str == null) {
         return null;
      }

      for (int index = 0; index < VALUES.length; index++) {
         RenderEntityParameterFloat renderEntityParameterFloat = VALUES[index];
         if (renderEntityParameterFloat.getName().equals(str)) {
            return renderEntityParameterFloat;
         }
      }

      return null;
   }

   @Override
   public float eval() {
      Render render = this.renderManager.renderRender;
      if (render == null) {
         return 0.0F;
      }

      if (render instanceof RendererLivingEntity rendererLivingEntity) {
         switch (this) {
            case LIMB_SWING:
               return rendererLivingEntity.renderLimbSwing;
            case LIMB_SWING_SPEED:
               return rendererLivingEntity.renderLimbSwingAmount;
            case AGE:
               return rendererLivingEntity.renderAgeInTicks;
            case HEAD_YAW:
               return rendererLivingEntity.renderHeadYaw;
            case HEAD_PITCH:
               return rendererLivingEntity.renderHeadPitch;
            case SCALE:
               return rendererLivingEntity.renderScaleFactor;
            default:
               EntityLivingBase entityLivingBase = rendererLivingEntity.renderEntity;
               if (entityLivingBase == null) {
                  return 0.0F;
               }

               switch (this) {
                  case HEALTH:
                     return entityLivingBase.getHealth();
                  case HURT_TIME:
                     return entityLivingBase.hurtTime;
                  case IDLE_TIME:
                     return entityLivingBase.getAge();
                  case MAX_HEALTH:
                     return entityLivingBase.getMaxHealth();
                  case MOVE_FORWARD:
                     return entityLivingBase.moveForward;
                  case MOVE_STRAFING:
                     return entityLivingBase.moveStrafing;
                  case POS_X:
                     return (float)entityLivingBase.posX;
                  case POS_Y:
                     return (float)entityLivingBase.posY;
                  case POS_Z:
                     return (float)entityLivingBase.posZ;
                  case REVENGE_TIME:
                     return entityLivingBase.getRevengeTimer();
                  case SWING_PROGRESS:
                     return entityLivingBase.getSwingProgress(rendererLivingEntity.renderPartialTicks);
               }
         }
      }

      return 0.0F;
   }

   RenderEntityParameterFloat(String text) {
      this.name = text;
      this.renderManager = Minecraft.getMinecraft().getRenderManager();
   }

   @Override
   public ExpressionType getExpressionType() {
      return ExpressionType.FLOAT;
   }
}
