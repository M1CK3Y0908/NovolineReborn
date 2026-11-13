package net.minecraft.client.renderer.block.model;

import java.util.Arrays;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class BreakingFour extends BakedQuad {
   private final TextureAtlasSprite texture;

   public BreakingFour(BakedQuad quad, TextureAtlasSprite textureIn) {
      super(Arrays.copyOf(quad.getVertexData(), quad.getVertexData().length), quad.tintIndex, FaceBakery.getFacingFromVertexData(quad.getVertexData()));
      this.texture = textureIn;
      this.remapQuad();
      this.fixVertexData();
   }

   private void remapVert(int vertex) {
      int i = this.vertexData.length / 4 * vertex;
      float f = Float.intBitsToFloat(this.vertexData[i]);
      float f2 = Float.intBitsToFloat(this.vertexData[i + 1]);
      float f3 = Float.intBitsToFloat(this.vertexData[i + 2]);
      float f4 = 0.0F;
      float f5 = 0.0F;
      switch (this.face) {
         case DOWN:
            f4 = f * 16.0F;
            f5 = (1.0F - f3) * 16.0F;
            break;
         case UP:
            f4 = f * 16.0F;
            f5 = f3 * 16.0F;
            break;
         case NORTH:
            f4 = (1.0F - f) * 16.0F;
            f5 = (1.0F - f2) * 16.0F;
            break;
         case SOUTH:
            f4 = f * 16.0F;
            f5 = (1.0F - f2) * 16.0F;
            break;
         case WEST:
            f4 = f3 * 16.0F;
            f5 = (1.0F - f2) * 16.0F;
            break;
         case EAST:
            f4 = (1.0F - f3) * 16.0F;
            f5 = (1.0F - f2) * 16.0F;
      }

      this.vertexData[i + 4] = Float.floatToRawIntBits(this.texture.getInterpolatedU(f4));
      this.vertexData[i + 4 + 1] = Float.floatToRawIntBits(this.texture.getInterpolatedV(f5));
   }

   private void remapQuad() {
      for (int index = 0; index < 4; index++) {
         this.remapVert(index);
      }
   }
}
