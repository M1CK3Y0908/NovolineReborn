package net.minecraft.client.renderer.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class TileEntityPistonRenderer extends TileEntitySpecialRenderer<TileEntityPiston> {
   private final BlockRendererDispatcher blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();

   public void renderTileEntityAt(TileEntityPiston tileEntityPiston, double d, double d2, double d3, float f, int unused5) {
      BlockPos pos = tileEntityPiston.getPos();
      IBlockState pistonState = tileEntityPiston.getPistonState();
      Block block2 = pistonState.getBlock();
      if (block2.getMaterial() != Material.air && tileEntityPiston.getProgress(f) < 1.0F) {
         Tessellator instance = Tessellator.getInstance();
         WorldRenderer worldRenderer = instance.getWorldRenderer();
         this.bindTexture(TextureMap.locationBlocksTexture);
         RenderHelper.disableStandardItemLighting();
         GlStateManager.blendFunc(770, 771);
         GlStateManager.enableBlend();
         GlStateManager.disableCull();
         if (Minecraft.isAmbientOcclusionEnabled()) {
            GlStateManager.shadeModel(7425);
         } else {
            GlStateManager.shadeModel(7424);
         }

         worldRenderer.begin(7, DefaultVertexFormats.BLOCK);
         worldRenderer.setTranslation(
            (float)d - pos.getX() + tileEntityPiston.getFloat3(f),
            (float)d2 - pos.getY() + tileEntityPiston.getFloat(f),
            (float)d3 - pos.getZ() + tileEntityPiston.getFloat2(f)
         );
         World world2 = this.getWorld();
         if (block2 == Blocks.piston_head && tileEntityPiston.getProgress(f) < 0.5F) {
            pistonState = pistonState.withProperty(BlockPistonExtension.SHORT, true);
            this.blockRenderer
               .getBlockModelRenderer()
               .renderModel(world2, this.blockRenderer.getModelFromBlockState(pistonState, world2, pos), pistonState, pos, worldRenderer, true);
         } else if (tileEntityPiston.shouldPistonHeadBeRendered() && !tileEntityPiston.isExtending()) {
            net.minecraft.block.BlockPistonExtension.EnumPistonType enumPistonType = block2 == Blocks.sticky_piston
               ? net.minecraft.block.BlockPistonExtension.EnumPistonType.STICKY
               : net.minecraft.block.BlockPistonExtension.EnumPistonType.DEFAULT;
            IBlockState iBlockState = Blocks.piston_head
               .getDefaultState()
               .withProperty(BlockPistonExtension.TYPE, enumPistonType)
               .withProperty(BlockPistonExtension.FACING, pistonState.getValue(BlockPistonBase.FACING));
            iBlockState = iBlockState.withProperty(BlockPistonExtension.SHORT, tileEntityPiston.getProgress(f) >= 0.5F);
            this.blockRenderer
               .getBlockModelRenderer()
               .renderModel(world2, this.blockRenderer.getModelFromBlockState(iBlockState, world2, pos), iBlockState, pos, worldRenderer, true);
            worldRenderer.setTranslation((float)d - pos.getX(), (float)d2 - pos.getY(), (float)d3 - pos.getZ());
            pistonState.withProperty(BlockPistonBase.EXTENDED, true);
            this.blockRenderer
               .getBlockModelRenderer()
               .renderModel(world2, this.blockRenderer.getModelFromBlockState(pistonState, world2, pos), pistonState, pos, worldRenderer, true);
         } else {
            this.blockRenderer
               .getBlockModelRenderer()
               .renderModel(world2, this.blockRenderer.getModelFromBlockState(pistonState, world2, pos), pistonState, pos, worldRenderer, false);
         }

         worldRenderer.setTranslation(0.0, 0.0, 0.0);
         instance.draw();
         RenderHelper.enableStandardItemLighting();
      }
   }
}
