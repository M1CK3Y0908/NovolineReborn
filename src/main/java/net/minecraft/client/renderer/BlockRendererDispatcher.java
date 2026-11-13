package net.minecraft.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import wtf.novoline.Novoline;
import wtf.novoline.event.RenderBlockEvent;

public class BlockRendererDispatcher implements IResourceManagerReloadListener {
   private final BlockModelRenderer blockModelRenderer = new BlockModelRenderer();
   private final ChestRenderer chestRenderer = new ChestRenderer();
   private final BlockFluidRenderer fluidRenderer = new BlockFluidRenderer();
   private BlockModelShapes blockModelShapes;
   private final GameSettings gameSettings;

   public void renderBlockBrightness(IBlockState state, float brightness) {
      int renderType = state.getBlock().getRenderType();
      if (renderType != -1) {
         switch (renderType) {
            case 1:
            default:
               break;
            case 2:
               this.chestRenderer.renderChestBrightness(state.getBlock(), brightness);
               break;
            case 3:
               IBakedModel bakedModel = this.getBakedModel(state, (BlockPos)null);
               this.blockModelRenderer.handleIBakedModel(bakedModel, state, brightness);
         }
      }
   }

   @Override
   public void onResourceManagerReload(IResourceManager resourceManager) {
      this.fluidRenderer.initAtlasSprites();
   }

   public void renderBlockDamage(IBlockState state, BlockPos pos, TextureAtlasSprite texture, IBlockAccess blockAccess) {
      Block block2 = state.getBlock();
      if (block2.getRenderType() == 3) {
         state = block2.getActualState(state, blockAccess, pos);
         IBakedModel modelForState = this.blockModelShapes.getModelForState(state);
         IBakedModel iBakedModel = new net.minecraft.client.resources.model.SimpleBakedModel.Builder(modelForState, texture).makeBakedModel();
         this.blockModelRenderer.renderModel(blockAccess, iBakedModel, state, pos, Tessellator.getInstance().getWorldRenderer());
      }
   }

   public IBakedModel getModelFromBlockState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      Block block = state.getBlock();
      if (worldIn.getWorldType() != WorldType.DEBUG_WORLD) {
         try {
            state = block.getActualState(state, worldIn, pos);
         } catch (Exception exception) {
         }
      }

      IBakedModel bakedModel = this.blockModelShapes.getModelForState(state);
      if (pos != null && this.gameSettings.allowBlockAlternatives && bakedModel instanceof WeightedBakedModel) {
         bakedModel = ((WeightedBakedModel)bakedModel).getAlternativeModel(MathHelper.getPositionRandom(pos));
      }

      return bakedModel;
   }

   public boolean isRenderTypeChest(Block block2, int i) {
      if (block2 == null) {
         return false;
      }

      int renderType = block2.getRenderType();
      return renderType == 3 ? false : renderType == 2;
   }

   public BlockModelRenderer getBlockModelRenderer() {
      return this.blockModelRenderer;
   }

   public BlockModelShapes getBlockModelShapes() {
      return this.blockModelShapes;
   }

   private IBakedModel getBakedModel(IBlockState state, BlockPos pos) {
      IBakedModel bakedModel = this.blockModelShapes.getModelForState(state);
      if (pos != null && this.gameSettings.allowBlockAlternatives && bakedModel instanceof WeightedBakedModel) {
         bakedModel = ((WeightedBakedModel)bakedModel).getAlternativeModel(MathHelper.getPositionRandom(pos));
      }

      return bakedModel;
   }

   public BlockRendererDispatcher(BlockModelShapes blockModelShapesIn, GameSettings gameSettingsIn) {
      this.blockModelShapes = blockModelShapesIn;
      this.gameSettings = gameSettingsIn;
   }

   public boolean renderBlock(IBlockState state, BlockPos pos, IBlockAccess blockAccess, WorldRenderer worldRendererIn) {
      try {
         int renderType = state.getBlock().getRenderType();
         if (renderType == -1) {
            return false;
         }

         RenderBlockEvent renderBlockEvent = new RenderBlockEvent(state, pos);
         Novoline.getNovoline().getBus().post(renderBlockEvent);
         if (renderBlockEvent.isCancelled()) {
            return false;
         }

         switch (renderType) {
            case 1:
               return this.fluidRenderer.renderFluid(blockAccess, state, pos, worldRendererIn);
            case 2:
               return false;
            case 3:
               IBakedModel modelFromBlockState = this.getModelFromBlockState(state, blockAccess, pos);
               return this.blockModelRenderer.renderModel(blockAccess, modelFromBlockState, state, pos, worldRendererIn, !renderBlockEvent.shouldRender);
            default:
               return false;
         }
      } catch (Throwable throwable) {
         CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Tesselating block in world");
         CrashReportCategory crashReportCategory = crashReport.makeCategory("Block being tesselated");
         CrashReportCategory.addBlockInfo(crashReportCategory, pos, state.getBlock(), state.getBlock().getMetaFromState(state));
         throw new ReportedException(crashReport);
      }
   }
}
