package net.minecraft.client.renderer;

import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import net.optifine.DynamicLights;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.Shaders;
import org.lwjgl.opengl.GL11;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.module.visual.Animations;
import wtf.novoline.module.visual.Chams;
import wtf.novoline.module.visual.Animations.ItemTransformState;
import wtf.novoline.utils.render.RenderUtils;

public class ItemRenderer {
   private static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
   private float equippedProgress;
   private static final ResourceLocation RES_UNDERWATER_OVERLAY = new ResourceLocation("textures/misc/underwater.png");
   private int intValue = -1;
   public static boolean flag;
   private int intValue2 = 0;
   private float prevEquippedProgress;
   private ItemStack itemToRender;
   private final Minecraft mc;
   private final RenderManager renderManager;
   private final RenderItem itemRenderer;

   public void renderItem(EntityLivingBase entityIn, ItemStack heldStack, net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType transform) {
      if (heldStack != null) {
         Block blockFromItem = Block.getBlockFromItem(heldStack.getItem());
         GlStateManager.pushMatrix();
         if (this.itemRenderer.shouldRenderItemIn3D(heldStack)) {
            GlStateManager.scale(2.0F, 2.0F, 2.0F);
            if (this.isBlockTranslucent(blockFromItem) && (!Config.isShaders() || !Shaders.renderItemKeepDepthMask)) {
               GlStateManager.depthMask(false);
            }
         } else {
            Animations animations2 = ModuleManager.animations;
            if (animations2.isEnabled()) {
               double d = 1.0F - animations2.getDownscale().getCurrentValue();
               GlStateManager.scale(d, d, d);
            }
         }

         this.itemRenderer.renderItemModelForEntity(heldStack, entityIn, transform);
         if (this.isBlockTranslucent(blockFromItem)) {
            GlStateManager.depthMask(true);
         }

         GlStateManager.popMatrix();
      }
   }

   public void renderItemInFirstPerson(float partialTicks) {
      if (!Config.isShaders() || !Shaders.isSkipRenderHand()) {
         float f2 = (ModuleManager.animations.isEnabled() ? 0.8F + ModuleManager.animations.getAnimatedHeight() / 100.0F : 1.0F)
            - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
         EntityPlayerSP thePlayer2 = this.mc.thePlayer;
         float swingProgress2 = thePlayer2.getSwingProgress(partialTicks);
         float f3 = thePlayer2.prevRotationPitch + (thePlayer2.rotationPitch - thePlayer2.prevRotationPitch) * partialTicks;
         float f4 = thePlayer2.prevRotationYaw + (thePlayer2.rotationYaw - thePlayer2.prevRotationYaw) * partialTicks;
         float f5 = MathHelper.sin(MathHelper.sqrt_float(swingProgress2) * MathHelper.PI);
         this.rotateArroundXAndY(f3, f4);
         this.setLightMapFromPlayer(thePlayer2);
         this.rotateWithPlayerRotations(thePlayer2, partialTicks);
         GlStateManager.enableRescaleNormal();
         GlStateManager.pushMatrix();
         if (this.itemToRender == null) {
            if (!thePlayer2.isInvisible()) {
               this.renderPlayerArm(thePlayer2, f2, swingProgress2);
            }
         } else {
            if (this.itemToRender.getItem() instanceof ItemMap) {
               this.renderItemMap(thePlayer2, f3, f2, swingProgress2);
            } else if (thePlayer2.getItemInUseCount() <= 0 && !flag) {
               this.handleFloat(swingProgress2);
               this.transformFirstPersonItem(f2, swingProgress2);
            } else {
               EnumAction itemUseAction = this.itemToRender.getItemUseAction();
               if (flag) {
                  itemUseAction = EnumAction.BLOCK;
               }

               Animations animations2 = ModuleManager.animations;
               if (animations2.isEnabled() && itemUseAction != EnumAction.NONE) {
                  Consumer<Float> consumer = switch (itemUseAction) {
                     case EAT, DRINK -> f -> this.performDrinking(thePlayer2, partialTicks);
                     case BOW -> f -> this.doBowTransformations(partialTicks, thePlayer2);
                     case BLOCK -> this::handleFloat2;
                     default -> throw new IllegalStateException("Unexpected value: " + itemUseAction);
                  };
                  animations2.performTransform(itemUseAction, consumer, this::transformFirstPersonItem, new ItemTransformState(f2, swingProgress2, f3, f4, f5));
               } else {
                  switch (itemUseAction) {
                     case EAT:
                     case DRINK:
                        this.performDrinking(thePlayer2, partialTicks);
                        this.transformFirstPersonItem(f2, 0.0F);
                        break;
                     case BOW:
                        this.transformFirstPersonItem(f2, 0.0F);
                        this.doBowTransformations(partialTicks, thePlayer2);
                        break;
                     case BLOCK:
                        this.transformFirstPersonItem(f2, 0.0F);
                        this.handleFloat2(0.2F);
                        break;
                     case NONE:
                        this.transformFirstPersonItem(f2, 0.0F);
                  }
               }
            }

            this.renderItem(thePlayer2, this.itemToRender, net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.FIRST_PERSON);
         }

         GlStateManager.popMatrix();
         GlStateManager.disableRescaleNormal();
         RenderHelper.disableStandardItemLighting();
      }
   }

   private boolean isBlockTranslucent(Block blockIn) {
      return blockIn != null && blockIn.getBlockLayer() == EnumWorldBlockLayer.TRANSLUCENT;
   }

   private void rotateWithPlayerRotations(EntityPlayerSP entityplayerspIn, float partialTicks) {
      float f2 = entityplayerspIn.prevRenderArmPitch + (entityplayerspIn.renderArmPitch - entityplayerspIn.prevRenderArmPitch) * partialTicks;
      float f3 = entityplayerspIn.prevRenderArmYaw + (entityplayerspIn.renderArmYaw - entityplayerspIn.prevRenderArmYaw) * partialTicks;
      GlStateManager.rotate((entityplayerspIn.rotationPitch - f2) * 0.1F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate((entityplayerspIn.rotationYaw - f3) * 0.1F, 0.0F, 1.0F, 0.0F);
   }

   private void renderItemMap(AbstractClientPlayer clientPlayer, float pitch, float equipmentProgress, float swingProgress) {
      float f2 = -0.4F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
      float f3 = 0.2F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI * 2.0F);
      float f4 = -0.2F * MathHelper.sin(swingProgress * (float) Math.PI);
      GlStateManager.translate(f2, f3, f4);
      float mapAngleFromPitch = this.getMapAngleFromPitch(pitch);
      GlStateManager.translate(0.0F, 0.04F, -0.72F);
      GlStateManager.translate(0.0F, equipmentProgress * -1.2F, 0.0F);
      GlStateManager.translate(0.0F, mapAngleFromPitch * -0.5F, 0.0F);
      GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(mapAngleFromPitch * -85.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
      this.renderPlayerArms(clientPlayer);
      float f5 = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
      float f6 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
      GlStateManager.rotate(f5 * -20.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(f6 * -20.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(f6 * -80.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.scale(0.38F, 0.38F, 0.38F);
      GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.translate(-1.0F, -1.0F, 0.0F);
      GlStateManager.scale(0.015625F, 0.015625F, 0.015625F);
      this.mc.getTextureManager().bindTexture(RES_MAP_BACKGROUND);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      GL11.glNormal3f(0.0F, 0.0F, -1.0F);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(-7.0, 135.0, 0.0).tex(0.0, 1.0).endVertex();
      worldRenderer.pos(135.0, 135.0, 0.0).tex(1.0, 1.0).endVertex();
      worldRenderer.pos(135.0, -7.0, 0.0).tex(1.0, 0.0).endVertex();
      worldRenderer.pos(-7.0, -7.0, 0.0).tex(0.0, 0.0).endVertex();
      tessellator.draw();
      MapData mapData = Items.filled_map.getMapData(this.itemToRender, this.mc.theWorld);
      if (mapData != null) {
         this.mc.entityRenderer.getMapItemRenderer().renderMap(mapData, false);
      }
   }

   public void updateEquippedItem() {
      this.prevEquippedProgress = this.equippedProgress;
      EntityPlayerSP thePlayer2 = this.mc.thePlayer;
      ItemStack currentItem2 = thePlayer2.inventory.getCurrentItem();
      boolean flag2 = false;
      if (this.itemToRender != null && currentItem2 != null) {
         if (!this.itemToRender.getIsItemStackEqual(currentItem2)) {
            if (Reflector.reflectorMethod78.isActive()) {
               boolean flag3 = Reflector.checkObject(
                  this.itemToRender.getItem(), Reflector.reflectorMethod78, this.itemToRender, currentItem2, this.intValue != thePlayer2.inventory.currentItem
               );
               if (!flag3) {
                  this.itemToRender = currentItem2;
                  this.intValue = thePlayer2.inventory.currentItem;
                  return;
               }
            }

            flag2 = true;
         }
      } else if (this.itemToRender == null && currentItem2 == null) {
         flag2 = false;
      } else {
         flag2 = true;
      }

      float f2 = 0.4F;
      float f3 = flag2 ? 0.0F : 1.0F;
      float f4 = MathHelper.clamp_float(f3 - this.equippedProgress, -f2, f2);
      this.equippedProgress += f4;
      if (this.equippedProgress < 0.1F) {
         this.itemToRender = currentItem2;
         this.intValue = thePlayer2.inventory.currentItem;
         if (Config.isShaders()) {
            Shaders.setItemToRenderMain(currentItem2);
         }
      }
   }

   private void renderWaterOverlayTexture(float partialTicks) {
      if (!Config.isShaders() || Shaders.isUnderwaterOverlay()) {
         this.mc.getTextureManager().bindTexture(RES_UNDERWATER_OVERLAY);
         Tessellator instance = Tessellator.getInstance();
         WorldRenderer worldRenderer2 = instance.getWorldRenderer();
         float brightness = this.mc.thePlayer.getBrightness(partialTicks);
         GlStateManager.color(brightness, brightness, brightness, 0.5F);
         GlStateManager.enableBlend();
         RenderUtils.applyBlendFunc();
         GlStateManager.pushMatrix();
         float f2 = -this.mc.thePlayer.rotationYaw / 64.0F;
         float f3 = this.mc.thePlayer.rotationPitch / 64.0F;
         worldRenderer2.begin(7, DefaultVertexFormats.POSITION_TEX);
         worldRenderer2.pos(-1.0, -1.0, -0.5).tex(4.0F + f2, 4.0F + f3).endVertex();
         worldRenderer2.pos(1.0, -1.0, -0.5).tex(0.0F + f2, 4.0F + f3).endVertex();
         worldRenderer2.pos(1.0, 1.0, -0.5).tex(0.0F + f2, 0.0F + f3).endVertex();
         worldRenderer2.pos(-1.0, 1.0, -0.5).tex(4.0F + f2, 0.0F + f3).endVertex();
         instance.draw();
         GlStateManager.popMatrix();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableBlend();
      }
   }

   private void renderPlayerArm(AbstractClientPlayer clientPlayer, float equipProgress, float swingProgress) {
      float f2 = -0.3F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
      float f3 = 0.4F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI * 2.0F);
      float f4 = -0.4F * MathHelper.sin(swingProgress * (float) Math.PI);
      GlStateManager.translate(f2, f3, f4);
      GlStateManager.translate(0.64000005F, -0.6F, -0.71999997F);
      GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
      GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
      float f5 = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
      float f6 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
      GlStateManager.rotate(f6 * 70.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(f5 * -20.0F, 0.0F, 0.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(clientPlayer.getLocationSkin());
      GlStateManager.translate(-1.0F, 3.6F, 3.5F);
      GlStateManager.rotate(120.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(200.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.scale(1.0F, 1.0F, 1.0F);
      GlStateManager.translate(5.6F, 0.0F, 0.0F);
      Render entityRenderObject = this.renderManager.getEntityRenderObject(this.mc.thePlayer);
      GlStateManager.disableCull();
      RenderPlayer renderPlayer = (RenderPlayer)entityRenderObject;
      Chams chams2 = ModuleManager.chams;
      if (chams2.isEnabled() && chams2.arm.isEnabled() && chams2.chams.getCurrentElement() == 1) {
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
      }

      renderPlayer.renderRightArm(this.mc.thePlayer);
      if (chams2.isEnabled() && chams2.arm.isEnabled() && chams2.chams.getCurrentElement() == 1) {
         float[] fArray2 = RenderUtils.unpackColorNormalized(chams2.color.getColor());
         if (fArray2[3] < 0.1F) {
            GlStateManager.alphaFunc(516, 0.1F);
         }

         RenderUtils.applyEntityLighting(clientPlayer);
         if (chams2.wireframe.isEnabled()) {
            GL11.glPolygonMode(1032, 6914);
         }

         GlStateManager.enableLighting();
         GlStateManager.enableTexture2D();
         GL11.glColor4d(1.0, 1.0, 1.0, 1.0);
         GL11.glPopMatrix();
      }

      GlStateManager.enableCull();
   }

   private void setLightMapFromPlayer(AbstractClientPlayer clientPlayer) {
      int combinedLight = this.mc.theWorld.getCombinedLight(new BlockPos(clientPlayer.posX, clientPlayer.posY + clientPlayer.getEyeHeight(), clientPlayer.posZ), 0);
      if (Config.isDynamicLights()) {
         combinedLight = DynamicLights.getCombinedLight(this.mc.getRenderViewEntity(), combinedLight);
      }

      float f2 = combinedLight & 65535;
      float f3 = combinedLight >> 16;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, f2, f3);
   }

   public ItemRenderer(Minecraft mcIn) {
      this.mc = mcIn;
      this.renderManager = mcIn.getRenderManager();
      this.itemRenderer = mcIn.getRenderItem();
   }

   private void performDrinking(AbstractClientPlayer clientPlayer, float partialTicks) {
      float f2 = clientPlayer.getItemInUseCount() - partialTicks + 1.0F;
      float f3 = f2 / this.itemToRender.getMaxItemUseDuration();
      float f4 = MathHelper.abs(MathHelper.cos(f2 / 4.0F * (float) Math.PI) * 0.1F);
      if (f3 >= 0.8F) {
         f4 = 0.0F;
      }

      GlStateManager.translate(0.0F, f4, 0.0F);
      float f5 = 1.0F - (float)Math.pow(f3, 27.0);
      GlStateManager.translate(f5 * 0.6F, f5 * -0.5F, f5 * 0.0F);
      GlStateManager.rotate(f5 * 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(f5 * 10.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(f5 * 30.0F, 0.0F, 0.0F, 1.0F);
   }

   private void renderPlayerArms(AbstractClientPlayer clientPlayer) {
      this.mc.getTextureManager().bindTexture(clientPlayer.getLocationSkin());
      RenderPlayer renderPlayer = (RenderPlayer)this.renderManager.<AbstractClientPlayer>getEntityRenderObject(this.mc.thePlayer);
      if (!clientPlayer.isInvisible()) {
         GlStateManager.disableCull();
         this.renderRightArm(renderPlayer);
         this.renderLeftArm(renderPlayer);
         GlStateManager.enableCull();
      }
   }

   private void rotateArroundXAndY(float angle, float angleY) {
      GlStateManager.pushMatrix();
      GlStateManager.rotate(angle, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(angleY, 0.0F, 1.0F, 0.0F);
      RenderHelper.enableStandardItemLighting();
      GlStateManager.popMatrix();
   }

   public void resetEquippedProgress2() {
      this.equippedProgress = 0.0F;
   }

   public void renderOverlays(float partialTicks) {
      GlStateManager.disableAlpha();
      if (this.mc.thePlayer.isEntityInsideOpaqueBlock()) {
         IBlockState blockState = this.mc.theWorld.getBlockState(new BlockPos(this.mc.thePlayer));
         BlockPos blockPos = new BlockPos(this.mc.thePlayer);
         EntityPlayerSP thePlayer2 = this.mc.thePlayer;

         for (int i = 0; i < 8; i++) {
            double d = thePlayer2.posX + ((i >> 0) % 2 - 0.5F) * thePlayer2.width * 0.8F;
            double d2 = thePlayer2.posY + ((i >> 1) % 2 - 0.5F) * 0.1F;
            double d3 = thePlayer2.posZ + ((i >> 2) % 2 - 0.5F) * thePlayer2.width * 0.8F;
            BlockPos blockPos2 = new BlockPos(d, d2 + thePlayer2.getEyeHeight(), d3);
            IBlockState blockState2 = this.mc.theWorld.getBlockState(blockPos2);
            if (blockState2.getBlock().isVisuallyOpaque()) {
               blockState = blockState2;
               blockPos = blockPos2;
            }
         }

         if (blockState.getBlock().getRenderType() != -1) {
            Object object = Reflector.createObject(Reflector.reflectorField21);
            if (!Reflector.checkReflectorMethod(Reflector.reflectorMethod41, this.mc.thePlayer, partialTicks, object, blockState, blockPos)) {
               this.renderBlockInHand(partialTicks, this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(blockState));
            }
         }
      }

      if (!this.mc.thePlayer.isSpectator()) {
         if (this.mc.thePlayer.isInsideOfMaterial(Material.water)
            && !Reflector.checkReflectorMethod(Reflector.reflectorMethod43, this.mc.thePlayer, partialTicks)) {
            this.renderWaterOverlayTexture(partialTicks);
         }

         if (this.mc.thePlayer.isBurning() && !Reflector.checkReflectorMethod(Reflector.reflectorMethod42, this.mc.thePlayer, partialTicks)) {
            this.renderFireInFirstPerson(partialTicks);
         }
      }

      GlStateManager.enableAlpha();
   }

   private void transformFirstPersonItem(float equipProgress, float swingProgress) {
      GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
      GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
      GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
      float f2 = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
      float f3 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
      GlStateManager.rotate(f2 * -20.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(f3 * -20.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(f3 * -80.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.scale(0.4F, 0.4F, 0.4F);
   }

   private float getMapAngleFromPitch(float pitch) {
      float f2 = 1.0F - pitch / 45.0F + 0.1F;
      f2 = MathHelper.clamp_float(f2, 0.0F, 1.0F);
      return -MathHelper.cos(f2 * (float) Math.PI) * 0.5F + 0.5F;
   }

   private void renderLeftArm(RenderPlayer renderPlayerIn) {
      GlStateManager.pushMatrix();
      GlStateManager.rotate(92.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(41.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.translate(-0.3F, -1.1F, 0.45F);
      renderPlayerIn.renderLeftArm(this.mc.thePlayer);
      GlStateManager.popMatrix();
   }

   private void handleFloat(float f2) {
      float f3 = -0.4F * MathHelper.sin(MathHelper.sqrt_float(f2) * (float) Math.PI);
      float f4 = 0.2F * MathHelper.sin(MathHelper.sqrt_float(f2) * (float) Math.PI * 2.0F);
      float f5 = -0.2F * MathHelper.sin(f2 * (float) Math.PI);
      GlStateManager.translate(f3, f4, f5);
   }

   private void renderRightArm(RenderPlayer renderPlayerIn) {
      GlStateManager.pushMatrix();
      GlStateManager.rotate(54.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(64.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(-62.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.translate(0.25F, -0.85F, 0.75F);
      renderPlayerIn.renderRightArm(this.mc.thePlayer);
      GlStateManager.popMatrix();
   }

   private void handleFloat2(float f2) {
      GlStateManager.translate(-0.5F, f2, 0.0F);
      GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
   }

   private void renderFireInFirstPerson(float partialTicks) {
      if (!ModuleManager.noRender.isEnabled() || !ModuleManager.noRender.fire.isEnabled()) {
         Tessellator instance = Tessellator.getInstance();
         WorldRenderer worldRenderer2 = instance.getWorldRenderer();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F);
         GlStateManager.depthFunc(519);
         GlStateManager.depthMask(false);
         GlStateManager.enableBlend();
         RenderUtils.applyBlendFunc();
         float f2 = 1.0F;

         for (int i = 0; i < 2; i++) {
            GlStateManager.pushMatrix();
            TextureAtlasSprite atlasSprite = this.mc.getTextureMapBlocks().getAtlasSprite("minecraft:blocks/fire_layer_1");
            this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            float minU = atlasSprite.getMinU();
            float maxU = atlasSprite.getMaxU();
            float minV = atlasSprite.getMinV();
            float maxV = atlasSprite.getMaxV();
            float f3 = (0.0F - f2) / 2.0F;
            float f4 = f3 + f2;
            float f5 = 0.0F - f2 / 2.0F;
            float f6 = f5 + f2;
            float f7 = -0.5F;
            GlStateManager.translate(-(i * 2 - 1) * 0.24F, -0.3F, 0.0F);
            GlStateManager.rotate((i * 2 - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
            worldRenderer2.begin(7, DefaultVertexFormats.POSITION_TEX);
            worldRenderer2.setSprite(atlasSprite);
            worldRenderer2.pos(f3, f5, f7).tex(maxU, maxV).endVertex();
            worldRenderer2.pos(f4, f5, f7).tex(minU, maxV).endVertex();
            worldRenderer2.pos(f4, f6, f7).tex(minU, minV).endVertex();
            worldRenderer2.pos(f3, f6, f7).tex(maxU, minV).endVertex();
            instance.draw();
            GlStateManager.popMatrix();
         }

         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableBlend();
         GlStateManager.depthMask(true);
         GlStateManager.depthFunc(515);
      }
   }

   private void renderBlockInHand(float partialTicks, TextureAtlasSprite atlas) {
      this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer2 = instance.getWorldRenderer();
      GlStateManager.color(0.1F, 0.1F, 0.1F, 0.5F);
      GlStateManager.pushMatrix();
      float minU = atlas.getMinU();
      float maxU = atlas.getMaxU();
      float minV = atlas.getMinV();
      float maxV = atlas.getMaxV();
      worldRenderer2.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer2.pos(-1.0, -1.0, -0.5).tex(maxU, maxV).endVertex();
      worldRenderer2.pos(1.0, -1.0, -0.5).tex(minU, maxV).endVertex();
      worldRenderer2.pos(1.0, 1.0, -0.5).tex(minU, minV).endVertex();
      worldRenderer2.pos(-1.0, 1.0, -0.5).tex(maxU, minV).endVertex();
      instance.draw();
      GlStateManager.popMatrix();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void doBowTransformations(float partialTicks, AbstractClientPlayer clientPlayer) {
      GlStateManager.rotate(-18.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(-12.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-8.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.translate(-0.9F, 0.2F, 0.0F);
      float f2 = this.itemToRender.getMaxItemUseDuration() - (clientPlayer.getItemInUseCount() - partialTicks + 1.0F);
      float f3 = f2 / 20.0F;
      f3 = (f3 * f3 + f3 * 2.0F) / 3.0F;
      if (f3 > 1.0F) {
         f3 = 1.0F;
      }

      if (f3 > 0.1F) {
         float f4 = MathHelper.sin((f2 - 0.1F) * 1.3F);
         float f5 = f3 - 0.1F;
         float f6 = f4 * f5;
         GlStateManager.translate(f6 * 0.0F, f6 * 0.01F, f6 * 0.0F);
      }

      GlStateManager.translate(f3 * 0.0F, f3 * 0.0F, f3 * 0.1F);
      GlStateManager.scale(1.0F, 1.0F, 1.0F + f3 * 0.2F);
   }

   public void resetEquippedProgress() {
      this.equippedProgress = 0.0F;
   }
}
