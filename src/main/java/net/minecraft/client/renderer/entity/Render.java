package net.minecraft.client.renderer.entity;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.src.Config;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.shaders.Shaders;
import org.lwjgl.opengl.GL11;
import wtf.novoline.module.visual.Chams;
import wtf.novoline.module.visual.PlayerESP;
import wtf.novoline.utils.render.RenderUtils;

public abstract class Render<T extends Entity> implements IEntityRenderer {
   protected float shadowOpaque = 1.0F;
   private static final ResourceLocation shadowTextures = new ResourceLocation("textures/misc/shadow.png");
   private Class entityClass = null;
   private ResourceLocation locationTextureCustom = null;
   public float shadowSize;
   protected final RenderManager renderManager;

   public boolean shouldRender(T livingEntity, ICamera camera, double camX, double camY, double camZ) {
      AxisAlignedBB axisAlignedBB = livingEntity.getEntityBoundingBox();
      if (axisAlignedBB.hasNaN() || axisAlignedBB.getAverageEdgeLength() == 0.0) {
         axisAlignedBB = new AxisAlignedBB(
            livingEntity.posX - 2.0,
            livingEntity.posY - 2.0,
            livingEntity.posZ - 2.0,
            livingEntity.posX + 2.0,
            livingEntity.posY + 2.0,
            livingEntity.posZ + 2.0
         );
      }

      return livingEntity.isInRangeToRender3d(camX, camY, camZ) && (livingEntity.ignoreFrustumCheck || camera.isBoundingBoxInFrustum(axisAlignedBB));
   }

   protected abstract ResourceLocation getEntityTexture(T value);

   @Override
   public void setEntityClass(Class arg1) {
      this.entityClass = arg1;
   }

   protected Render(RenderManager renderManager) {
      this.renderManager = renderManager;
   }

   protected boolean canRenderName(T entity) {
      return entity.getAlwaysRenderNameTagForRender() && entity.hasCustomName();
   }

   public void renderMultipass(T arg1, double arg2, double arg4, double arg6, float arg8, float arg9) {
   }

   public static void renderOffsetAABB(AxisAlignedBB boundingBox, double x, double y, double z) {
      GlStateManager.disableTexture2D();
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      worldRenderer.setTranslation(x, y, z);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_NORMAL);
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).normal(0.0F, 0.0F, -1.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).normal(0.0F, 0.0F, -1.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).normal(0.0F, 0.0F, -1.0F).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).normal(0.0F, 0.0F, -1.0F).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).normal(0.0F, -1.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).normal(0.0F, -1.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).normal(0.0F, -1.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).normal(0.0F, -1.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).normal(0.0F, 1.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).normal(0.0F, 1.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).normal(0.0F, 1.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).normal(0.0F, 1.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).normal(-1.0F, 0.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).normal(-1.0F, 0.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).normal(-1.0F, 0.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).normal(-1.0F, 0.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).normal(1.0F, 0.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).normal(1.0F, 0.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).normal(1.0F, 0.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).normal(1.0F, 0.0F, 0.0F).endVertex();
      tessellator.draw();
      worldRenderer.setTranslation(0.0, 0.0, 0.0);
      GlStateManager.enableTexture2D();
   }

   public void doRender2(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
      if (!Chams.renderingChams && !PlayerESP.renderingEsp) {
         this.renderName(entity, x, y, z);
      }
   }

   public void doRenderShadowAndFire(Entity entityIn, double x, double y, double z, float yaw, float partialTicks) {
      if (this.renderManager.options != null) {
         if (this.renderManager.options.entityShadowsOption && this.shadowSize > 0.0F && !entityIn.isInvisible() && this.renderManager.isRenderShadow()) {
            double xCoordinate = this.renderManager.getDistanceToCamera(entityIn.posX, entityIn.posY, entityIn.posZ);
            float f = (float)((1.0 - xCoordinate / 256.0) * this.shadowOpaque);
            if (f > 0.0F) {
               this.renderShadow(entityIn, x, y, z, f, partialTicks);
            }
         }

         if (entityIn.canRenderOnFire() && (!(entityIn instanceof EntityPlayer) || !((EntityPlayer)entityIn).isSpectator())) {
            this.renderEntityOnFire(entityIn, x, y, z, partialTicks);
         }
      }
   }

   @Override
   public Class getEntityClass() {
      return this.entityClass;
   }

   private World getWorldFromRenderManager() {
      return this.renderManager.worldObj;
   }

   private void renderEntityOnFire(Entity entity, double x, double y, double z, float partialTicks) {
      GlStateManager.disableLighting();
      TextureMap textureMap = Minecraft.getMinecraft().getTextureMapBlocks();
      TextureAtlasSprite textureAtlasSprite = textureMap.getAtlasSprite("minecraft:blocks/fire_layer_0");
      TextureAtlasSprite textureAtlasSprite2 = textureMap.getAtlasSprite("minecraft:blocks/fire_layer_1");
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)x, (float)y, (float)z);
      float f = entity.width * 1.4F;
      GlStateManager.scale(f, f, f);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldRenderer = tessellator.getWorldRenderer();
      float f2 = 0.5F;
      float f3 = 0.0F;
      float f4 = entity.height / f;
      float f5 = (float)(entity.posY - entity.getEntityBoundingBox().minY);
      GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
      GlStateManager.translate(0.0F, 0.0F, -0.3F + (int)f4 * 0.02F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      float f6 = 0.0F;
      int i = 0;
      boolean isMultiTexture = Config.isMultiTexture();
      if (isMultiTexture) {
         worldRenderer.setBlockLayer(EnumWorldBlockLayer.SOLID);
      }

      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);

      while (f4 > 0.0F) {
         TextureAtlasSprite textureAtlasSprite3 = i % 2 == 0 ? textureAtlasSprite : textureAtlasSprite2;
         worldRenderer.setSprite(textureAtlasSprite3);
         this.bindTexture(TextureMap.locationBlocksTexture);
         float minU = textureAtlasSprite3.getMinU();
         float minV = textureAtlasSprite3.getMinV();
         float maxU = textureAtlasSprite3.getMaxU();
         float maxV = textureAtlasSprite3.getMaxV();
         if (i / 2 % 2 == 0) {
            float f7 = maxU;
            maxU = minU;
            minU = f7;
         }

         worldRenderer.pos(f2 - f3, 0.0F - f5, f6).tex(maxU, maxV).endVertex();
         worldRenderer.pos(-f2 - f3, 0.0F - f5, f6).tex(minU, maxV).endVertex();
         worldRenderer.pos(-f2 - f3, 1.4F - f5, f6).tex(minU, minV).endVertex();
         worldRenderer.pos(f2 - f3, 1.4F - f5, f6).tex(maxU, minV).endVertex();
         f4 -= 0.45F;
         f5 -= 0.45F;
         f2 *= 0.9F;
         f6 += 0.03F;
         i++;
      }

      tessellator.draw();
      if (isMultiTexture) {
         worldRenderer.setBlockLayer((EnumWorldBlockLayer)null);
         GlStateManager.bindCurrentTexture();
      }

      GlStateManager.popMatrix();
      GlStateManager.enableLighting();
   }

   @Override
   public ResourceLocation getLocationTextureCustom() {
      return this.locationTextureCustom;
   }

   protected void handleEntity(Entity entity2, String text, double d, double d2, double d3) {
      double distanceSqToEntity = entity2.getDistanceSqToEntity(this.renderManager.livingPlayer);
      if (distanceSqToEntity <= 4096) {
         FontRenderer fontRendererFromRenderManager = this.getFontRendererFromRenderManager();
         float f = 1.6F;
         float f2 = 0.016666668F * f;
         GlStateManager.pushMatrix();
         GlStateManager.translate((float)d + 0.0F, (float)d2 + entity2.height + 0.5F, (float)d3);
         GL11.glNormal3f(0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
         GlStateManager.scale(-f2, -f2, f2);
         GlStateManager.disableLighting();
         GlStateManager.depthMask(false);
         GlStateManager.disableDepth();
         GlStateManager.enableBlend();
         RenderUtils.applyBlendFunc();
         Tessellator instance = Tessellator.getInstance();
         WorldRenderer worldRenderer2 = instance.getWorldRenderer();
         byte b = 0;
         if (text.equals("deadmau5")) {
            b = -10;
         }

         int i = fontRendererFromRenderManager.getInt3(text) / 2;
         GlStateManager.disableTexture2D();
         worldRenderer2.begin(7, DefaultVertexFormats.POSITION_COLOR);
         worldRenderer2.pos(-i - 1, -1 + b, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
         worldRenderer2.pos(-i - 1, 8 + b, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
         worldRenderer2.pos(i + 1, 8 + b, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
         worldRenderer2.pos(i + 1, -1 + b, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
         instance.draw();
         GlStateManager.enableTexture2D();
         fontRendererFromRenderManager.getInt(text, -fontRendererFromRenderManager.getInt3(text) / 2, b, 553648127);
         GlStateManager.enableDepth();
         GlStateManager.depthMask(true);
         fontRendererFromRenderManager.getInt(text, -fontRendererFromRenderManager.getInt3(text) / 2, b, -1);
         GlStateManager.enableLighting();
         GlStateManager.disableBlend();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.popMatrix();
      }
   }

   public static void setModelBipedMain(RenderBiped arg0, ModelBiped arg1) {
      arg0.modelBipedMain = arg1;
   }

   protected boolean bindEntityTexture(T entity) {
      ResourceLocation resourceLocation = this.getEntityTexture(entity);
      if (this.locationTextureCustom != null) {
         resourceLocation = this.locationTextureCustom;
      }

      if (resourceLocation == null) {
         return false;
      }

      this.bindTexture(resourceLocation);
      return true;
   }

   public boolean isMultipass() {
      return false;
   }

   protected void renderOffsetLivingLabel(T entityIn, double x, double y, double z, String str, float f, double d) {
      this.handleEntity(entityIn, str, x, y, z);
   }

   protected void renderName(T entity, double x, double y, double z) {
      if (this.canRenderName(entity)) {
         this.handleEntity(entity, entity.getDisplayName().getFormattedText(), x, y, z);
      }
   }

   public RenderManager getRenderManager() {
      return this.renderManager;
   }

   private void renderShadow(Entity entityIn, double x, double y, double z, float shadowAlpha, float partialTicks) {
      if (!Config.isShaders() || !Shaders.shouldSkipDefaultShadow) {
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 771);
         this.renderManager.renderEngine.bindTexture(shadowTextures);
         World world = this.getWorldFromRenderManager();
         GlStateManager.depthMask(false);
         float shadowSize2 = this.shadowSize;
         if (entityIn instanceof EntityLiving entityLiving) {
            shadowSize2 *= entityLiving.getRenderSizeModifier();
            if (entityLiving.isChild()) {
               shadowSize2 *= 0.5F;
            }
         }

         double xCoordinate = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * partialTicks;
         double yCoordinate = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * partialTicks;
         double zCoordinate = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * partialTicks;
         int i = MathHelper.floor_double(xCoordinate - shadowSize2);
         int i2 = MathHelper.floor_double(xCoordinate + shadowSize2);
         int i3 = MathHelper.floor_double(yCoordinate - shadowSize2);
         int i4 = MathHelper.floor_double(yCoordinate);
         int i5 = MathHelper.floor_double(zCoordinate - shadowSize2);
         int i6 = MathHelper.floor_double(zCoordinate + shadowSize2);
         double d = x - xCoordinate;
         double d2 = y - yCoordinate;
         double d3 = z - zCoordinate;
         Tessellator tessellator = Tessellator.getInstance();
         tessellator.getWorldRenderer().begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

         for (BlockPos blockPos : BlockPos.getAllInBoxMutable(
            new BlockPos(i, i3, i5), new BlockPos(i2, i4, i6)
         )) {
            Block block = world.getBlockState(blockPos.down()).getBlock();
            if (block.getRenderType() != -1 && world.getLightFromNeighbors(blockPos) > 3) {
               this.renderShadowBlock(block, x, y, z, blockPos, shadowAlpha, shadowSize2, d, d2, d3);
            }
         }

         tessellator.draw();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableBlend();
         GlStateManager.depthMask(true);
      }
   }

   @Override
   public void setLocationTextureCustom(ResourceLocation arg1) {
      this.locationTextureCustom = arg1;
   }

   public FontRenderer getFontRendererFromRenderManager() {
      return this.renderManager.getFontRenderer();
   }

   public void bindTexture(ResourceLocation location) {
      this.renderManager.renderEngine.bindTexture(location);
   }

   private void renderShadowBlock(
      Block blockIn, double d, double d2, double d3, BlockPos pos, float f, float f2, double d4, double d5, double d6
   ) {
      if (blockIn.isFullCube()) {
         WorldRenderer worldRenderer2 = Tessellator.getInstance().getWorldRenderer();
         double d7 = (f - (d2 - (pos.getY() + d5)) / 2.0) * 0.5 * this.getWorldFromRenderManager().getLightBrightness(pos);
         if (d7 >= 0.0) {
            if (d7 > 1.0) {
               d7 = 1.0;
            }

            double d8 = pos.getX() + blockIn.getBlockBoundsMinX() + d4;
            double d9 = pos.getX() + blockIn.getBlockBoundsMaxX() + d4;
            double d10 = pos.getY() + blockIn.getBlockBoundsMinY() + d5 + 0.015625;
            double d11 = pos.getZ() + blockIn.getBlockBoundsMinZ() + d6;
            double d12 = pos.getZ() + blockIn.getBlockBoundsMaxZ() + d6;
            float f3 = (float)((d - d8) / 2.0 / f2 + 0.5);
            float f4 = (float)((d - d9) / 2.0 / f2 + 0.5);
            float f5 = (float)((d3 - d11) / 2.0 / f2 + 0.5);
            float f6 = (float)((d3 - d12) / 2.0 / f2 + 0.5);
            worldRenderer2.pos(d8, d10, d11).tex(f3, f5).color(1.0F, 1.0F, 1.0F, (float)d7).endVertex();
            worldRenderer2.pos(d8, d10, d12).tex(f3, f6).color(1.0F, 1.0F, 1.0F, (float)d7).endVertex();
            worldRenderer2.pos(d9, d10, d12).tex(f4, f6).color(1.0F, 1.0F, 1.0F, (float)d7).endVertex();
            worldRenderer2.pos(d9, d10, d11).tex(f4, f5).color(1.0F, 1.0F, 1.0F, (float)d7).endVertex();
         }
      }
   }
}
