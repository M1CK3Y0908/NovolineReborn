package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.optifine.entity.model.IEntityRenderer;

public abstract class TileEntitySpecialRenderer<T extends TileEntity> implements IEntityRenderer {
   private Class tileEntityClass = null;
   private ResourceLocation locationTextureCustom = null;
   protected static final ResourceLocation[] DESTROY_STAGES = new ResourceLocation[]{
      new ResourceLocation("textures/blocks/destroy_stage_0.png"),
      new ResourceLocation("textures/blocks/destroy_stage_1.png"),
      new ResourceLocation("textures/blocks/destroy_stage_2.png"),
      new ResourceLocation("textures/blocks/destroy_stage_3.png"),
      new ResourceLocation("textures/blocks/destroy_stage_4.png"),
      new ResourceLocation("textures/blocks/destroy_stage_5.png"),
      new ResourceLocation("textures/blocks/destroy_stage_6.png"),
      new ResourceLocation("textures/blocks/destroy_stage_7.png"),
      new ResourceLocation("textures/blocks/destroy_stage_8.png"),
      new ResourceLocation("textures/blocks/destroy_stage_9.png")
   };
   protected TileEntityRendererDispatcher rendererDispatcher;

   protected World getWorld() {
      return this.rendererDispatcher.worldObj;
   }

   public boolean forceTileEntityRender() {
      return false;
   }

   public void setRendererDispatcher(TileEntityRendererDispatcher rendererDispatcherIn) {
      this.rendererDispatcher = rendererDispatcherIn;
   }

   public abstract void renderTileEntityAt(T value, double d, double d2, double d3, float f, int i);

   public FontRenderer getFontRenderer() {
      return this.rendererDispatcher.getFontRenderer();
   }

   @Override
   public ResourceLocation getLocationTextureCustom() {
      return this.locationTextureCustom;
   }

   protected void bindTexture(ResourceLocation location) {
      TextureManager textureManager = this.rendererDispatcher.renderEngine;
      if (textureManager != null) {
         textureManager.bindTexture(location);
      }
   }

   public void noop(TileEntity tileEntity, double d, double d2, double d3, float f, WorldRenderer worldRenderer) {
   }

   @Override
   public Class getEntityClass() {
      return this.tileEntityClass;
   }

   @Override
   public void setLocationTextureCustom(ResourceLocation arg1) {
      this.locationTextureCustom = arg1;
   }

   @Override
   public void setEntityClass(Class arg1) {
      this.tileEntityClass = arg1;
   }
}
