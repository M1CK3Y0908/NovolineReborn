package net.minecraft.client.renderer.entity;

import com.google.common.collect.Lists;
import java.nio.FloatBuffer;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelSpider;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.src.Config;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.optifine.EmissiveTextures;
import net.optifine.entity.model.CustomEntityModels;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.Shaders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.module.misc.NameProtect;
import wtf.novoline.module.visual.Chams;
import wtf.novoline.module.visual.PlayerESP;
import wtf.novoline.utils.render.RenderUtils;

public abstract class RendererLivingEntity<T extends EntityLivingBase> extends Render<T> {
   private static final Logger logger = LogManager.getLogger();
   public EntityLivingBase renderEntity;
   private static final DynamicTexture textureBrightness = new DynamicTexture(16, 16);
   protected FloatBuffer brightnessBuffer = GLAllocation.createDirectFloatBuffer(4);
   public float renderScaleFactor;
   public float renderHeadYaw;
   protected List<LayerRenderer<T>> layerRenderers = Lists.newArrayList();
   public float renderHeadPitch;
   public static float NAME_TAG_RANGE = 64.0F;
   protected boolean renderOutlines = false;
   public static float NAME_TAG_RANGE_SNEAK = 32.0F;
   public float renderLimbSwing;
   public static final boolean animateModelLiving = Boolean.getBoolean("animate.model.living");
   private boolean renderLayersPushMatrix;
   public ModelBase mainModel;
   private boolean renderModelPushMatrix;
   public float renderLimbSwingAmount;
   public float renderPartialTicks;
   public float renderAgeInTicks;

   public RendererLivingEntity(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
      super(renderManagerIn);
      this.mainModel = modelBaseIn;
      this.shadowSize = shadowSizeIn;
      this.renderModelPushMatrix = this.mainModel instanceof ModelSpider;
   }

   protected boolean setBrightness(T entitylivingbaseIn, float partialTicks, boolean combineTextures) {
      float brightness = entitylivingbaseIn.getBrightness(partialTicks);
      int colorMultiplier = this.getColorMultiplier(entitylivingbaseIn, brightness, partialTicks);
      boolean flag = (colorMultiplier >> 24 & 0xFF) > 0;
      boolean flag2 = entitylivingbaseIn.hurtTime > 0 || entitylivingbaseIn.deathTime > 0;
      if (!flag && !flag2) {
         return false;
      }

      if (!flag && !combineTextures) {
         return false;
      }

      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      GlStateManager.enableTexture2D();
      GL11.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.defaultTexUnit);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PRIMARY_COLOR);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.defaultTexUnit);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GlStateManager.enableTexture2D();
      GL11.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, OpenGlHelper.GL_INTERPOLATE);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.GL_CONSTANT);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE2_RGB, OpenGlHelper.GL_CONSTANT);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND2_RGB, 770);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.GL_PREVIOUS);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
      this.brightnessBuffer.position(0);
      if (flag2) {
         this.brightnessBuffer.put(1.0F);
         this.brightnessBuffer.put(0.0F);
         this.brightnessBuffer.put(0.0F);
         this.brightnessBuffer.put(0.3F);
         if (Config.isShaders()) {
            Shaders.setEntityColor(1.0F, 0.0F, 0.0F, 0.3F);
         }
      } else {
         float f = (colorMultiplier >> 24 & 0xFF) / 255.0F;
         float f2 = (colorMultiplier >> 16 & 0xFF) / 255.0F;
         float f3 = (colorMultiplier >> 8 & 0xFF) / 255.0F;
         float f4 = (colorMultiplier & 0xFF) / 255.0F;
         this.brightnessBuffer.put(f2);
         this.brightnessBuffer.put(f3);
         this.brightnessBuffer.put(f4);
         this.brightnessBuffer.put(1.0F - f);
         if (Config.isShaders()) {
            Shaders.setEntityColor(f2, f3, f4, 1.0F - f);
         }
      }

      this.brightnessBuffer.flip();
      GL11.glTexEnv(8960, 8705, this.brightnessBuffer);
      GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2);
      GlStateManager.enableTexture2D();
      GlStateManager.bindTexture(textureBrightness.getGlTextureId());
      GL11.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.GL_PREVIOUS);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.lightmapTexUnit);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.GL_PREVIOUS);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      return true;
   }

   protected int getColorMultiplier(T entitylivingbaseIn, float lightBrightness, float partialTickTime) {
      return 0;
   }

   static {
      int[] intArray = textureBrightness.getTextureData();

      for (int i = 0; i < 256; i++) {
         intArray[i] = -1;
      }

      textureBrightness.updateDynamicTexture();
   }

   protected float getDeathMaxRotation(T entityLivingBaseIn) {
      return 90.0F;
   }

   public void setRenderOutlines(boolean renderOutlinesIn) {
      this.renderOutlines = renderOutlinesIn;
   }

   protected float getSwingProgress(T livingBase, float partialTickTime) {
      return livingBase.getSwingProgress(partialTickTime);
   }

   public ModelBase getMainModel() {
      return this.mainModel;
   }

   protected boolean setDoRenderBrightness(T entityLivingBaseIn, float partialTicks) {
      return this.setBrightness(entityLivingBaseIn, partialTicks, true);
   }

   protected void renderLayers(T entitylivingbaseIn, float f, float f2, float partialTicks, float f3, float f4, float f5, float f6) {
      for (LayerRenderer layerRenderer : this.layerRenderers) {
         if (!ModuleManager.customModel.isEnabled() || !ModuleManager.customModel.appliesTo(entitylivingbaseIn) || layerRenderer instanceof LayerHeldItem) {
            boolean flag = this.setBrightness(entitylivingbaseIn, partialTicks, layerRenderer.shouldCombineTextures());
            if (EmissiveTextures.isActive()) {
               EmissiveTextures.beginRender();
            }

            if (this.renderLayersPushMatrix) {
               GlStateManager.pushMatrix();
            }

            layerRenderer.doRenderLayer(entitylivingbaseIn, f, f2, partialTicks, f3, f4, f5, f6);
            if (this.renderLayersPushMatrix) {
               GlStateManager.popMatrix();
            }

            if (EmissiveTextures.isActive()) {
               if (EmissiveTextures.hasEmissive()) {
                  this.renderLayersPushMatrix = true;
                  EmissiveTextures.beginRenderEmissive();
                  GlStateManager.pushMatrix();
                  layerRenderer.doRenderLayer(entitylivingbaseIn, f, f2, partialTicks, f3, f4, f5, f6);
                  GlStateManager.popMatrix();
                  EmissiveTextures.endRenderEmissive();
               }

               EmissiveTextures.endRender();
            }

            if (flag) {
               this.unsetBrightness();
            }
         }
      }
   }

   public List<LayerRenderer<T>> getLayerRenderers() {
      return this.layerRenderers;
   }

   protected boolean setScoreTeamColor(T entityLivingBaseIn) {
      int i2 = 16777215;
      if (entityLivingBaseIn instanceof EntityPlayer) {
         ScorePlayerTeam scorePlayerTeam = (ScorePlayerTeam)entityLivingBaseIn.getTeam();
         if (scorePlayerTeam != null) {
            String text = FontRenderer.getFormatFromString(scorePlayerTeam.getColorPrefix());
            if (text.length() >= 2) {
               i2 = this.getFontRendererFromRenderManager().getColorCode(text.charAt(1));
            }
         }
      }

      float f = (i2 >> 16 & 0xFF) / 255.0F;
      float f2 = (i2 >> 8 & 0xFF) / 255.0F;
      float f3 = (i2 & 0xFF) / 255.0F;
      GlStateManager.disableLighting();
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      GlStateManager.color(f, f2, f3, 1.0F);
      GlStateManager.disableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GlStateManager.disableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      return true;
   }

   protected <V extends EntityLivingBase, U extends LayerRenderer<V>> boolean removeLayer(U layer) {
      return this.layerRenderers.remove(layer);
   }

   public void doRender2(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
      if (!Reflector.reflectorConstructor10.isActive() || !Reflector.checkReflectorConstructor(Reflector.reflectorConstructor10, entity, this, x, y, z)) {
         if (animateModelLiving) {
            entity.limbSwingAmount = 1.0F;
         }

         GlStateManager.pushMatrix();
         GlStateManager.disableCull();
         this.mainModel.swingProgress = this.getSwingProgress(entity, partialTicks);
         this.mainModel.isRiding = entity.isRiding();
         if (Reflector.reflectorMethod36.isActive()) {
            this.mainModel.isRiding = entity.isRiding()
               && entity.ridingEntity != null
               && Reflector.checkObject(entity.ridingEntity, Reflector.reflectorMethod36);
         }

         this.mainModel.isChild = entity.isChild();

         try {
            float f = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
            float f2 = this.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
            float f3 = f2 - f;
            if (this.mainModel.isRiding && entity.ridingEntity instanceof EntityLivingBase entityLivingBase) {
               f = this.interpolateRotation(entityLivingBase.prevRenderYawOffset, entityLivingBase.renderYawOffset, partialTicks);
               f3 = f2 - f;
               float f4 = MathHelper.wrapAngleTo180_float(f3);
               if (f4 < -85.0F) {
                  f4 = -85.0F;
               }

               if (f4 >= 85.0F) {
                  f4 = 85.0F;
               }

               f = f2 - f4;
               if (f4 * f4 > 2500.0F) {
                  f += f4 * 0.2F;
               }

               f3 = f2 - f;
            }

            float f5 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
            if (entity == Minecraft.getMinecraft().thePlayer) {
               f5 = entity.prevRotationPitchHead + (entity.rotationPitchHead - entity.prevRotationPitchHead) * partialTicks;
            }

            this.renderLivingAt(entity, x, y, z);
            float f6 = this.handleRotationFloat(entity, partialTicks);
            this.rotateCorpse(entity, f6, f, partialTicks);
            GlStateManager.enableRescaleNormal();
            GlStateManager.scale(-1.0F, -1.0F, 1.0F);
            this.preRenderCallback(entity, partialTicks);
            float f7 = 0.0625F;
            GlStateManager.translate(0.0F, -1.5078125F, 0.0F);
            float f8 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
            float f9 = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);
            if (entity.isChild()) {
               f9 *= 3.0F;
            }

            if (f8 > 1.0F) {
               f8 = 1.0F;
            }

            GlStateManager.enableAlpha();
            this.mainModel.setLivingAnimations(entity, f9, f8, partialTicks);
            this.mainModel.setRotationAngles(f9, f8, f6, f3, f5, 0.0625F, entity);
            if (CustomEntityModels.isActive()) {
               this.renderEntity = entity;
               this.renderLimbSwing = f9;
               this.renderLimbSwingAmount = f8;
               this.renderAgeInTicks = f6;
               this.renderHeadYaw = f3;
               this.renderHeadPitch = f5;
               this.renderScaleFactor = f7;
               this.renderPartialTicks = partialTicks;
            }

            if (ModuleManager.chams.isEnabled() && entity instanceof EntityPlayer) {
               int currentElement = ModuleManager.chams.chams.getCurrentElement();
               if (currentElement == 0) {
                  GL11.glEnable(32823);
                  GL11.glPolygonOffset(1.0F, -2000000.0F);
               }
            }

            if (this.renderOutlines) {
               boolean flag = this.setScoreTeamColor(entity);
               this.renderModel(entity, f9, f8, f6, f3, f5, 0.0625F);
               if (flag) {
                  this.unsetScoreTeamColor();
               }
            } else {
               boolean flag2 = this.setDoRenderBrightness(entity, partialTicks);
               if (EmissiveTextures.isActive()) {
                  EmissiveTextures.beginRender();
               }

               if (this.renderModelPushMatrix) {
                  GlStateManager.pushMatrix();
               }

               this.renderModel(entity, f9, f8, f6, f3, f5, 0.0625F);
               if (this.renderModelPushMatrix) {
                  GlStateManager.popMatrix();
               }

               if (EmissiveTextures.isActive()) {
                  if (EmissiveTextures.hasEmissive()) {
                     this.renderModelPushMatrix = true;
                     EmissiveTextures.beginRenderEmissive();
                     GlStateManager.pushMatrix();
                     this.renderModel(entity, f9, f8, f6, f3, f5, f7);
                     GlStateManager.popMatrix();
                     EmissiveTextures.endRenderEmissive();
                  }

                  EmissiveTextures.endRender();
               }

               if (flag2) {
                  this.unsetBrightness();
               }

               GlStateManager.depthMask(true);
               if ((!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).isSpectator())
                  && (!ModuleManager.noRender.isEnabled() || !ModuleManager.noRender.armor.isEnabled())) {
                  this.renderLayers(entity, f9, f8, partialTicks, f6, f3, f5, 0.0625F);
               }
            }

            if (ModuleManager.chams.isEnabled() && entity instanceof EntityPlayer) {
               int currentElement2 = ModuleManager.chams.chams.getCurrentElement();
               if (currentElement2 == 0) {
                  GL11.glDisable(32823);
                  GL11.glPolygonOffset(1.0F, 2000000.0F);
               }
            }

            if (CustomEntityModels.isActive()) {
               this.renderEntity = null;
            }

            GlStateManager.disableRescaleNormal();
         } catch (Exception exception) {
            logger.error("Couldn't render entity", exception);
         }

         GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
         GlStateManager.enableTexture2D();
         GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
         GlStateManager.enableCull();
         GlStateManager.popMatrix();
         if (!this.renderOutlines) {
            super.doRender2(entity, x, y, z, entityYaw, partialTicks);
         }

         if (Reflector.reflectorConstructor11.isActive()) {
            Reflector.checkReflectorConstructor(Reflector.reflectorConstructor11, entity, this, x, y, z);
         }
      }
   }

   protected void unsetScoreTeamColor() {
      GlStateManager.enableLighting();
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      GlStateManager.enableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GlStateManager.enableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
   }

   public void transformHeldFull3DItemLayer() {
   }

   protected void renderLivingAt(T entityLivingBaseIn, double x, double y, double z) {
      GlStateManager.translate((float)x, (float)y, (float)z);
   }

   protected void rotateCorpse(T bat, float f, float f2, float partialTicks) {
      GlStateManager.rotate(180.0F - f2, 0.0F, 1.0F, 0.0F);
      if (bat.deathTime > 0) {
         float f3 = (bat.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
         f3 = MathHelper.sqrt_float(f3);
         if (f3 > 1.0F) {
            f3 = 1.0F;
         }

         GlStateManager.rotate(f3 * this.getDeathMaxRotation(bat), 0.0F, 0.0F, 1.0F);
      } else {
         String textWithoutFormattingCodes = EnumChatFormatting.getTextWithoutFormattingCodes(bat.getName());
         if (textWithoutFormattingCodes != null
            && (textWithoutFormattingCodes.equals("Dinnerbone") || textWithoutFormattingCodes.equals("Grumm"))
            && (!(bat instanceof EntityPlayer) || ((EntityPlayer)bat).isWearing(EnumPlayerModelParts.CAPE))) {
            GlStateManager.translate(0.0F, bat.height + 0.1F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
         }
      }
   }

   protected void unsetBrightness() {
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      GlStateManager.enableTexture2D();
      GL11.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.defaultTexUnit);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PRIMARY_COLOR);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.defaultTexUnit);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_ALPHA, OpenGlHelper.GL_PRIMARY_COLOR);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_ALPHA, 770);
      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GL11.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, 5890);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, 5890);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2);
      GlStateManager.disableTexture2D();
      GlStateManager.bindTexture(0);
      GL11.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, 5890);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, 5890);
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      if (Config.isShaders()) {
         Shaders.setEntityColor(0.0F, 0.0F, 0.0F, 0.0F);
      }
   }

   protected boolean canRenderName(T entity) {
      EntityPlayerSP entityPlayerSP = Minecraft.getMinecraft().thePlayer;
      if (ModuleManager.nameTags.isEnabled() && ModuleManager.nameTags.addons.isSelected(2)) {
         return false;
      }

      if (entity instanceof EntityPlayer && entity != entityPlayerSP) {
         Team team = entity.getTeam();
         Team team2 = entityPlayerSP.getTeam();
         if (team != null) {
            net.minecraft.scoreboard.Team.EnumVisible team$enumvisible = team.getNameTagVisibility();

            return switch (team$enumvisible) {
               case NEVER -> false;
               case HIDE_FOR_OTHER_TEAMS -> team2 == null || team.isSameTeam(team2);
               case HIDE_FOR_OWN_TEAM -> team2 == null || !team.isSameTeam(team2);
               default -> true;
            };
         }
      }

      return Minecraft.isGuiEnabled()
         && entity != this.renderManager.livingPlayer
         && !entity.isInvisibleToPlayer(entityPlayerSP)
         && entity.riddenByEntity == null;
   }

   public <V extends EntityLivingBase, U extends LayerRenderer<V>> boolean addLayer(U layer) {
      return this.layerRenderers.add((LayerRenderer<T>)(LayerRenderer)layer);
   }

   protected void preRenderCallback(T entitylivingbaseIn, float partialTickTime) {
   }

   protected void renderModel(T entitylivingbaseIn, float f, float f2, float f3, float f4, float f5, float scaleFactor) {
      boolean flag = !entitylivingbaseIn.isInvisible();
      boolean flag2 = !flag && !entitylivingbaseIn.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer);
      if (flag || flag2) {
         if (!this.bindEntityTexture(entitylivingbaseIn)) {
            return;
         }

         if (flag2) {
            GlStateManager.pushMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.15F);
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.alphaFunc(516, 0.003921569F);
         }

         Chams chams2 = ModuleManager.chams;
         if (!PlayerESP.renderingEspModel && entitylivingbaseIn instanceof EntityPlayer && chams2.isEnabled()) {
            int currentElement = chams2.chams.getCurrentElement();
            if (currentElement != 0) {
               GlStateManager.color(1.0F, 1.0F, 1.0F, 0.15F);
               GlStateManager.depthMask(false);
               GlStateManager.enableBlend();
               GlStateManager.blendFunc(770, 771);
               GlStateManager.alphaFunc(516, 0.003921569F);
            }

            if (currentElement == 0) {
               this.mainModel.render(entitylivingbaseIn, f, f2, f3, f4, f5, scaleFactor);
            } else if (currentElement == 1) {
               GL11.glPushMatrix();
               GlStateManager.disableTexture2D();
               float[] fArray = RenderUtils.unpackColorNormalized(chams2.color.getColor());
               if (chams2.wireframe.isEnabled()) {
                  GL11.glPolygonMode(1032, 6913);
               }

               GlStateManager.disableLighting();
               OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 61680.0F, 0.0F);
               GL11.glColor4d(fArray[0], fArray[1], fArray[2], fArray[3]);
               if (fArray[3] < 0.1F) {
                  GlStateManager.alphaFunc(516, 0.003921569F);
               }

               GlStateManager.disableDepth();
               this.mainModel.render(entitylivingbaseIn, f, f2, f3, f4, f5, scaleFactor);
               GlStateManager.enableDepth();
               this.mainModel.render(entitylivingbaseIn, f, f2, f3, f4, f5, scaleFactor);
               if (fArray[3] < 0.1F) {
                  GlStateManager.alphaFunc(516, 0.1F);
               }

               RenderUtils.applyEntityLighting((EntityPlayer)entitylivingbaseIn);
               if (chams2.wireframe.isEnabled()) {
                  GL11.glPolygonMode(1032, 6914);
               }

               GlStateManager.enableLighting();
               GlStateManager.enableTexture2D();
               GL11.glColor4d(1.0, 1.0, 1.0, 1.0);
               GL11.glPopMatrix();
            } else if (currentElement == 2) {
               this.mainModel.render(entitylivingbaseIn, f, f2, f3, f4, f5, scaleFactor);
            }

            if (currentElement != 0) {
               GlStateManager.disableBlend();
               GlStateManager.alphaFunc(516, 0.1F);
               GlStateManager.depthMask(true);
            }
         } else {
            this.mainModel.render(entitylivingbaseIn, f, f2, f3, f4, f5, scaleFactor);
         }

         if (flag2) {
            GlStateManager.disableBlend();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.popMatrix();
            GlStateManager.depthMask(true);
         }
      }
   }

   protected float handleRotationFloat(T livingBase, float partialTicks) {
      return livingBase.ticksExisted + partialTicks;
   }

   protected float interpolateRotation(float par1, float par2, float par3) {
      float f = par2 - par1;

      while (f < -180.0F) {
         f += 360.0F;
      }

      while (f >= 180.0F) {
         f -= 360.0F;
      }

      return par1 + par3 * f;
   }

   public void renderName(T entity, double x, double y, double z) {
      if (!Reflector.reflectorConstructor12.isActive() || !Reflector.checkReflectorConstructor(Reflector.reflectorConstructor12, entity, this, x, y, z)) {
         if (this.canRenderName(entity)) {
            double distanceSqToEntity = entity.getDistanceSqToEntity(this.renderManager.livingPlayer);
            float f = entity.isSneaking() ? NAME_TAG_RANGE_SNEAK : NAME_TAG_RANGE;
            if (distanceSqToEntity < f * f) {
               String formattedText = entity.getDisplayName().getFormattedText();
               NameProtect nameProtect2 = ModuleManager.nameProtect;
               if (nameProtect2.isEnabled() && nameProtect2.getHideIn().isSelected(2)) {
                  formattedText = nameProtect2.protectText(formattedText);
               }

               GlStateManager.alphaFunc(516, 0.1F);
               if (entity.isSneaking()) {
                  FontRenderer fontRendererFromRenderManager = this.getFontRendererFromRenderManager();
                  GlStateManager.pushMatrix();
                  GlStateManager.translate((float)x, (float)y + entity.height + 0.5F - (entity.isChild() ? entity.height / 2.0F : 0.0F), (float)z);
                  GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                  GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                  GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
                  GlStateManager.scale(-0.02666667F, -0.02666667F, 0.02666667F);
                  GlStateManager.translate(0.0F, 9.374999F, 0.0F);
                  GlStateManager.disableLighting();
                  GlStateManager.depthMask(false);
                  GlStateManager.enableBlend();
                  GlStateManager.disableTexture2D();
                  RenderUtils.applyBlendFunc();
                  int i2 = fontRendererFromRenderManager.getInt3(formattedText) / 2;
                  Tessellator instance = Tessellator.getInstance();
                  WorldRenderer worldRenderer = instance.getWorldRenderer();
                  worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
                  worldRenderer.pos(-i2 - 1, -1.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                  worldRenderer.pos(-i2 - 1, 8.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                  worldRenderer.pos(i2 + 1, 8.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                  worldRenderer.pos(i2 + 1, -1.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                  instance.draw();
                  GlStateManager.enableTexture2D();
                  GlStateManager.depthMask(true);
                  fontRendererFromRenderManager.getInt(formattedText, -fontRendererFromRenderManager.getInt3(formattedText) / 2, 0, 553648127);
                  GlStateManager.enableLighting();
                  GlStateManager.disableBlend();
                  GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                  GlStateManager.popMatrix();
               } else {
                  this.renderOffsetLivingLabel(entity, x, y - (entity.isChild() ? entity.height / 2.0F : 0.0), z, formattedText, 0.02666667F, distanceSqToEntity);
               }
            }
         }

         if (Reflector.reflectorConstructor13.isActive()) {
            Reflector.checkReflectorConstructor(Reflector.reflectorConstructor13, entity, this, x, y, z);
         }
      }
   }
}
