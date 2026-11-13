package net.optifine.entity.model.anim;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.optifine.expr.ExpressionType;
import net.optifine.expr.IExpressionBool;

public enum RenderEntityParameterBool implements IExpressionBool {
   IS_ALIVE("is_alive"),
   IS_BURNING("is_burning"),
   IS_CHILD("is_child"),
   IS_GLOWING("is_glowing"),
   IS_HURT("is_hurt"),
   IS_IN_LAVA("is_in_lava"),
   IS_IN_WATER("is_in_water"),
   IS_INVISIBLE("is_invisible"),
   IS_ON_GROUND("is_on_ground"),
   IS_RIDDEN("is_ridden"),
   IS_RIDING("is_riding"),
   IS_SNEAKING("is_sneaking"),
   IS_SPRINTING("is_sprinting"),
   IS_WET("is_wet");

   private String name;
   private static final RenderEntityParameterBool[] VALUES = values();
   private RenderManager renderManager;

   public String getName() {
      return this.name;
   }

   RenderEntityParameterBool(String text) {
      this.name = text;
      this.renderManager = Minecraft.getMinecraft().getRenderManager();
   }

   public static RenderEntityParameterBool parse(String str) {
      if (str == null) {
         return null;
      }

      for (int index = 0; index < VALUES.length; index++) {
         RenderEntityParameterBool renderEntityParameterBool = VALUES[index];
         if (renderEntityParameterBool.getName().equals(str)) {
            return renderEntityParameterBool;
         }
      }

      return null;
   }

   @Override
   public ExpressionType getExpressionType() {
      return ExpressionType.BOOL;
   }

   @Override
   public boolean eval() {
      Render renderRender2 = this.renderManager.renderRender;
      if (renderRender2 == null) {
         return false;
      }

      if (renderRender2 instanceof RendererLivingEntity) {
         EntityLivingBase renderEntity2 = ((RendererLivingEntity)renderRender2).renderEntity;
         if (renderEntity2 == null) {
            return false;
         }

         switch (this) {
            case IS_ALIVE:
               return renderEntity2.isEntityAlive();
            case IS_BURNING:
               return renderEntity2.isBurning();
            case IS_CHILD:
               return renderEntity2.isChild();
            case IS_HURT:
               return renderEntity2.hurtTime > 0;
            case IS_IN_LAVA:
               return renderEntity2.isInLava();
            case IS_IN_WATER:
               return renderEntity2.isInWater();
            case IS_INVISIBLE:
               return renderEntity2.isInvisible();
            case IS_ON_GROUND:
               return renderEntity2.onGround;
            case IS_RIDDEN:
               return renderEntity2.riddenByEntity != null;
            case IS_RIDING:
               return renderEntity2.isRiding();
            case IS_SNEAKING:
               return renderEntity2.isSneaking();
            case IS_SPRINTING:
               return renderEntity2.isSprinting();
            case IS_WET:
               return renderEntity2.isWet();
         }
      }

      return false;
   }
}
