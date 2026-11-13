package net.minecraft.client.renderer.entity;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerDeadmau5Head;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ResourceLocation;

public class RenderPlayer extends RendererLivingEntity<AbstractClientPlayer> {
   private boolean smallArms;

   protected void renderOffsetLivingLabel(AbstractClientPlayer entityIn, double x, double y, double z, String str, float f, double d) {
      if (d < 100.0) {
         Scoreboard scoreboard = entityIn.getWorldScoreboard();
         ScoreObjective scoreObjective = scoreboard.getObjectiveInDisplaySlot(2);
         if (scoreObjective != null) {
            Score score = scoreboard.getValueFromObjective(entityIn.getName(), scoreObjective);
            this.handleEntity(entityIn, score.getScorePoints() + " " + scoreObjective.getDisplayName(), x, y, z);
            y += this.getFontRendererFromRenderManager().FONT_HEIGHT * 1.15F * f;
         }
      }

      super.renderOffsetLivingLabel(entityIn, x, y, z, str, f, d);
   }

   public RenderPlayer(RenderManager renderManager) {
      this(renderManager, false);
   }

   protected void preRenderCallback(AbstractClientPlayer entitylivingbaseIn, float partialTickTime) {
      float f = 0.9375F;
      GlStateManager.scale(f, f, f);
   }

   public void renderLeftArm(AbstractClientPlayer clientPlayer) {
      float f = 1.0F;
      GlStateManager.color(f, f, f);
      ModelPlayer modelPlayer = this.getMainModel();
      this.setModelVisibilities(clientPlayer);
      modelPlayer.isSneak = false;
      modelPlayer.swingProgress = 0.0F;
      modelPlayer.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, clientPlayer);
      modelPlayer.renderLeftArm();
   }

   private void setModelVisibilities(AbstractClientPlayer clientPlayer) {
      ModelPlayer mainModel = this.getMainModel();
      if (clientPlayer.isSpectator()) {
         mainModel.setInvisible(false);
         mainModel.bipedHead.showModel = true;
         mainModel.bipedHeadwear.showModel = true;
      } else {
         ItemStack currentItem = clientPlayer.inventory.getCurrentItem();
         mainModel.setInvisible(true);
         mainModel.bipedHeadwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.HAT);
         mainModel.bipedBodyWear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.JACKET);
         mainModel.bipedLeftLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_PANTS_LEG);
         mainModel.bipedRightLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_PANTS_LEG);
         mainModel.bipedLeftArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_SLEEVE);
         mainModel.bipedRightArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_SLEEVE);
         mainModel.heldItemLeft = 0;
         mainModel.aimedBow = false;
         mainModel.isSneak = clientPlayer.isSneaking();
         if (currentItem == null) {
            mainModel.heldItemRight = 0;
         } else {
            mainModel.heldItemRight = 1;
            if (clientPlayer.getItemInUseCount() > 0) {
               EnumAction itemUseAction = currentItem.getItemUseAction();
               if (itemUseAction == EnumAction.BLOCK) {
                  mainModel.heldItemRight = 3;
               } else if (itemUseAction == EnumAction.BOW) {
                  mainModel.aimedBow = true;
               }
            }
         }
      }
   }

   public void renderRightArm(AbstractClientPlayer clientPlayer) {
      float f = 1.0F;
      GlStateManager.color(f, f, f);
      ModelPlayer modelPlayer = this.getMainModel();
      this.setModelVisibilities(clientPlayer);
      GlStateManager.enableBlend();
      modelPlayer.swingProgress = 0.0F;
      modelPlayer.isSneak = false;
      modelPlayer.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, clientPlayer);
      modelPlayer.renderRightArm();
      GlStateManager.disableBlend();
   }

   @Override
   public void transformHeldFull3DItemLayer() {
      GlStateManager.translate(0.0F, 0.1875F, 0.0F);
   }

   protected void rotateCorpse(AbstractClientPlayer bat, float f, float f2, float partialTicks) {
      if (bat.isEntityAlive() && bat.isPlayerSleeping()) {
         GlStateManager.rotate(bat.getBedOrientationInDegrees(), 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(this.getDeathMaxRotation(bat), 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
      } else {
         super.rotateCorpse(bat, f, f2, partialTicks);
      }
   }

   protected void renderLivingAt(AbstractClientPlayer entityLivingBaseIn, double x, double y, double z) {
      if (entityLivingBaseIn.isEntityAlive() && entityLivingBaseIn.isPlayerSleeping()) {
         super.renderLivingAt(
            entityLivingBaseIn, x + entityLivingBaseIn.renderOffsetX, y + entityLivingBaseIn.renderOffsetY, z + entityLivingBaseIn.renderOffsetZ
         );
      } else {
         super.renderLivingAt(entityLivingBaseIn, x, y, z);
      }
   }

   public void doRender2(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks) {
      if (!entity.isUser() || this.renderManager.livingPlayer == entity) {
         double y2 = y;
         if (entity.isSneaking() && !(entity instanceof EntityPlayerSP)) {
            y2 = y - 0.125;
         }

         this.setModelVisibilities(entity);
         super.doRender2(entity, x, y2, z, entityYaw, partialTicks);
      }
   }

   public RenderPlayer(RenderManager renderManager, boolean useSmallArms) {
      super(renderManager, new ModelPlayer(0.0F, useSmallArms), 0.5F);
      this.smallArms = useSmallArms;
      this.addLayer(new LayerBipedArmor(this));
      this.addLayer(new LayerHeldItem(this));
      this.addLayer(new LayerArrow(this));
      this.addLayer(new LayerDeadmau5Head(this));
      this.addLayer(new LayerCape(this));
      this.addLayer(new LayerCustomHead(this.getMainModel().bipedHead));
   }

   public ModelPlayer getMainModel() {
      return (ModelPlayer)super.getMainModel();
   }

   protected ResourceLocation getEntityTexture(AbstractClientPlayer entity) {
      return entity.getLocationSkin();
   }
}
