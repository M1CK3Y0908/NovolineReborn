package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import java.util.BitSet;
import java.util.List;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.optifine.SmartAnimations;

public abstract class ChunkRenderContainer {
   private double viewEntityY;
   protected boolean initialized;
   private double viewEntityX;
   protected List<RenderChunk> renderChunks = Lists.newArrayListWithCapacity(17424);
   private final BitSet animatedSpritesCached = new BitSet();
   private BitSet animatedSpritesRendered;
   private double viewEntityZ;

   public abstract void renderChunkLayer(EnumWorldBlockLayer enumWorldBlockLayer);

   public void preRenderChunk(RenderChunk renderChunkIn) {
      BlockPos blockPos = renderChunkIn.getPosition();
      GlStateManager.translate(
         (float)(blockPos.getX() - this.viewEntityX), (float)(blockPos.getY() - this.viewEntityY), (float)(blockPos.getZ() - this.viewEntityZ)
      );
   }

   public void addRenderChunk(RenderChunk renderChunkIn, EnumWorldBlockLayer layer) {
      this.renderChunks.add(renderChunkIn);
      if (this.animatedSpritesRendered != null) {
         BitSet bitSet = renderChunkIn.compiledChunk.getAnimatedSprites(layer);
         if (bitSet != null) {
            this.animatedSpritesRendered.or(bitSet);
         }
      }
   }

   public void initialize(double viewEntityXIn, double viewEntityYIn, double viewEntityZIn) {
      this.initialized = true;
      this.renderChunks.clear();
      this.viewEntityX = viewEntityXIn;
      this.viewEntityY = viewEntityYIn;
      this.viewEntityZ = viewEntityZIn;
      if (SmartAnimations.isActive()) {
         if (this.animatedSpritesRendered != null) {
            SmartAnimations.spritesRendered(this.animatedSpritesRendered);
         } else {
            this.animatedSpritesRendered = this.animatedSpritesCached;
         }

         this.animatedSpritesRendered.clear();
      } else if (this.animatedSpritesRendered != null) {
         SmartAnimations.spritesRendered(this.animatedSpritesRendered);
         this.animatedSpritesRendered = null;
      }
   }
}
