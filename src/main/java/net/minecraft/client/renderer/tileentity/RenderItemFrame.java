package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureCompass;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.Shaders;
import org.lwjgl.opengl.GL11;

public class RenderItemFrame extends Render<EntityItemFrame> {
   private static final ResourceLocation mapBackgroundTextures = new ResourceLocation("textures/map/map_background.png");
   private final Minecraft mc = Minecraft.getMinecraft();
   private final ModelResourceLocation itemFrameModel = new ModelResourceLocation("item_frame", "normal");
   private final ModelResourceLocation mapModel = new ModelResourceLocation("item_frame", "map");
   private static double itemRenderDistanceSq = 4096.0;
   private RenderItem itemRenderer;

   protected void renderName(EntityItemFrame entity, double x, double y, double z) {
      if (Minecraft.isGuiEnabled()
         && entity.getDisplayedItem() != null
         && entity.getDisplayedItem().hasDisplayName()
         && this.renderManager.pointedEntity == entity) {
         float f = 1.6F;
         float f2 = 0.016666668F * f;
         double distanceSqToEntity = entity.getDistanceSqToEntity(this.renderManager.livingPlayer);
         float f3 = entity.isSneaking() ? 32.0F : 64.0F;
         if (distanceSqToEntity < f3 * f3) {
            String text = entity.getDisplayedItem().getDisplayName();
            if (entity.isSneaking()) {
               FontRenderer fontRenderer = this.getFontRendererFromRenderManager();
               GlStateManager.pushMatrix();
               GlStateManager.translate((float)x + 0.0F, (float)y + entity.height + 0.5F, (float)z);
               GL11.glNormal3f(0.0F, 1.0F, 0.0F);
               GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
               GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
               GlStateManager.scale(-f2, -f2, f2);
               GlStateManager.disableLighting();
               GlStateManager.translate(0.0F, 0.25F / f2, 0.0F);
               GlStateManager.depthMask(false);
               GlStateManager.enableBlend();
               GlStateManager.blendFunc(770, 771);
               Tessellator tessellator = Tessellator.getInstance();
               WorldRenderer worldRenderer = tessellator.getWorldRenderer();
               int i = fontRenderer.getInt3(text) / 2;
               GlStateManager.disableTexture2D();
               worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
               worldRenderer.pos(-i - 1, -1.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
               worldRenderer.pos(-i - 1, 8.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
               worldRenderer.pos(i + 1, 8.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
               worldRenderer.pos(i + 1, -1.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
               tessellator.draw();
               GlStateManager.enableTexture2D();
               GlStateManager.depthMask(true);
               fontRenderer.getInt(text, -fontRenderer.getInt3(text) / 2, 0, 553648127);
               GlStateManager.enableLighting();
               GlStateManager.disableBlend();
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               GlStateManager.popMatrix();
            } else {
               this.handleEntity(entity, text, x, y, z);
            }
         }
      }
   }

   private boolean isRenderItem(EntityItemFrame arg1) {
      if (Shaders.isShadowPass) {
         return false;
      }

      if (!Config.zoomMode) {
         Entity entity = this.mc.getRenderViewEntity();
         double xCoordinate = arg1.getDistanceSq(entity.posX, entity.posY, entity.posZ);
         if (xCoordinate > itemRenderDistanceSq) {
            return false;
         }
      }

      return true;
   }

   public RenderItemFrame(RenderManager renderManagerIn, RenderItem itemRendererIn) {
      super(renderManagerIn);
      this.itemRenderer = itemRendererIn;
   }

   public void doRender2(EntityItemFrame entity, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.pushMatrix();
      BlockPos hangingPosition = entity.getHangingPosition();
      double d = hangingPosition.getX() - entity.posX + x;
      double d2 = hangingPosition.getY() - entity.posY + y;
      double d3 = hangingPosition.getZ() - entity.posZ + z;
      GlStateManager.translate(d + 0.5, d2 + 0.5, d3 + 0.5);
      GlStateManager.rotate(180.0F - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
      this.renderManager.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
      BlockRendererDispatcher blockRendererDispatcher = this.mc.getBlockRendererDispatcher();
      ModelManager modelManager = blockRendererDispatcher.getBlockModelShapes().getModelManager();
      IBakedModel iBakedModel;
      if (entity.getDisplayedItem() != null && entity.getDisplayedItem().getItem() == Items.filled_map) {
         iBakedModel = modelManager.getModel(this.mapModel);
      } else {
         iBakedModel = modelManager.getModel(this.itemFrameModel);
      }

      GlStateManager.pushMatrix();
      GlStateManager.translate(-0.5F, -0.5F, -0.5F);
      blockRendererDispatcher.getBlockModelRenderer().renderModelBrightnessColor(iBakedModel, 1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
      GlStateManager.translate(0.0F, 0.0F, 0.4375F);
      this.renderItem(entity);
      GlStateManager.popMatrix();
      this.renderName(entity, x + entity.facingDirection.getFrontOffsetX() * 0.3F, y - 0.25, z + entity.facingDirection.getFrontOffsetZ() * 0.3F);
   }

   protected ResourceLocation getEntityTexture(EntityItemFrame entity) {
      return null;
   }

   public static void updateItemRenderDistance() {
      Minecraft minecraft = Config.getMinecraft();
      double d = Config.limit(minecraft.gameSettings.fovSetting, 1.0F, 120.0F);
      double d2 = Math.max(6.0 * minecraft.displayHeight / d, 16.0);
      itemRenderDistanceSq = d2 * d2;
   }

   private void renderItem(EntityItemFrame itemFrame) {
      ItemStack itemStack = itemFrame.getDisplayedItem();
      if (itemStack != null) {
         if (!this.isRenderItem(itemFrame)) {
            return;
         }

         if (!Config.zoomMode) {
            Entity entity = this.mc.thePlayer;
            double xCoordinate = itemFrame.getDistanceSq(entity.posX, entity.posY, entity.posZ);
            if (xCoordinate > 4096.0) {
               return;
            }
         }

         EntityItem entityItem = new EntityItem(itemFrame.worldObj, 0.0, 0.0, 0.0, itemStack);
         Item item = entityItem.getEntityItem().getItem();
         entityItem.getEntityItem().stackSize = 1;
         entityItem.hoverStart = 0.0F;
         GlStateManager.pushMatrix();
         GlStateManager.disableLighting();
         int rotation = itemFrame.getRotation();
         if (item instanceof ItemMap) {
            rotation = rotation % 4 * 2;
         }

         GlStateManager.rotate(rotation * 360.0F / 8.0F, 0.0F, 0.0F, 1.0F);
         if (!Reflector.checkReflectorConstructor(Reflector.reflectorConstructor9, itemFrame, this)) {
            if (item instanceof ItemMap) {
               this.renderManager.renderEngine.bindTexture(mapBackgroundTextures);
               GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
               float f = 0.0078125F;
               GlStateManager.scale(f, f, f);
               GlStateManager.translate(-64.0F, -64.0F, 0.0F);
               MapData mapData = Items.filled_map.getMapData(entityItem.getEntityItem(), itemFrame.worldObj);
               GlStateManager.translate(0.0F, 0.0F, -1.0F);
               if (mapData != null) {
                  this.mc.entityRenderer.getMapItemRenderer().renderMap(mapData, true);
               }
            } else {
               TextureAtlasSprite textureAtlasSprite = null;
               if (item == Items.compass) {
                  textureAtlasSprite = this.mc.getTextureMapBlocks().getAtlasSprite(TextureCompass.locationSprite);
                  this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
                  if (textureAtlasSprite instanceof TextureCompass textureCompass) {
                     double currentAngle2 = textureCompass.currentAngle;
                     double angleDelta2 = textureCompass.angleDelta;
                     textureCompass.currentAngle = 0.0;
                     textureCompass.angleDelta = 0.0;
                     textureCompass.updateCompass(
                        itemFrame.worldObj,
                        itemFrame.posX,
                        itemFrame.posZ,
                        MathHelper.wrapAngleTo180_float(180 + itemFrame.facingDirection.getHorizontalIndex() * 90),
                        false,
                        true
                     );
                     textureCompass.currentAngle = currentAngle2;
                     textureCompass.angleDelta = angleDelta2;
                  } else {
                     textureAtlasSprite = null;
                  }
               }

               GlStateManager.scale(0.5F, 0.5F, 0.5F);
               if (!this.itemRenderer.shouldRenderItemIn3D(entityItem.getEntityItem()) || item instanceof ItemSkull) {
                  GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
               }

               GlStateManager.pushAttrib();
               RenderHelper.enableStandardItemLighting();
               this.itemRenderer.renderItem(entityItem.getEntityItem(), net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType.FIXED);
               RenderHelper.disableStandardItemLighting();
               GlStateManager.popAttrib();
               if (textureAtlasSprite != null && textureAtlasSprite.getFrameCount() > 0) {
                  textureAtlasSprite.updateAnimation();
               }
            }
         }

         GlStateManager.enableLighting();
         GlStateManager.popMatrix();
      }
   }
}
