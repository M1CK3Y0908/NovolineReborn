package net.optifine;

import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.src.Config;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public class NaturalProperties {
   public int rotation = 1;
   public boolean flip = false;
   private Map[] quadMaps = new Map[8];

   public boolean isValid() {
      return this.rotation != 2 && this.rotation != 4 ? this.flip : true;
   }

   private int[] transformVertexData(int[] vertexData, int rotate, boolean flipU) {
      int[] iArray = (int[])vertexData.clone();
      int i = 4 - rotate;
      if (flipU) {
         i += 3;
      }

      i %= 4;
      int count = iArray.length / 4;

      for (int nestedIndex = 0; nestedIndex < 4; nestedIndex++) {
         int i2 = nestedIndex * count;
         int i3 = i * count;
         iArray[i3 + 4] = vertexData[i2 + 4];
         iArray[i3 + 4 + 1] = vertexData[i2 + 4 + 1];
         if (flipU) {
            if (--i < 0) {
               i = 3;
            }
         } else if (++i > 3) {
            i = 0;
         }
      }

      return iArray;
   }

   public synchronized BakedQuad getQuad(BakedQuad quadIn, int rotate, boolean flipU) {
      int rotate2 = rotate;
      if (flipU) {
         rotate2 = rotate | 4;
      }

      if (rotate2 > 0 && rotate2 < this.quadMaps.length) {
         Map map = this.quadMaps[rotate2];
         if (map == null) {
            map = new IdentityHashMap(1);
            this.quadMaps[rotate2] = map;
         }

         BakedQuad bakedQuad = (BakedQuad)map.get(quadIn);
         if (bakedQuad == null) {
            bakedQuad = this.makeQuad(quadIn, rotate, flipU);
            map.put(quadIn, bakedQuad);
         }

         return bakedQuad;
      } else {
         return quadIn;
      }
   }

   public NaturalProperties(String type) {
      if (type.equals("4")) {
         this.rotation = 4;
      } else if (type.equals("2")) {
         this.rotation = 2;
      } else if (type.equals("F")) {
         this.flip = true;
      } else if (type.equals("4F")) {
         this.rotation = 4;
         this.flip = true;
      } else if (type.equals("2F")) {
         this.rotation = 2;
         this.flip = true;
      } else {
         Config.warn("NaturalTextures: Unknown type: " + type);
      }
   }

   private boolean isFullSprite(BakedQuad quad) {
      TextureAtlasSprite textureAtlasSprite = quad.getSprite();
      float minU = textureAtlasSprite.getMinU();
      float maxU = textureAtlasSprite.getMaxU();
      float f = maxU - minU;
      float f2 = f / 256.0F;
      float minV = textureAtlasSprite.getMinV();
      float maxV = textureAtlasSprite.getMaxV();
      float f3 = maxV - minV;
      float f4 = f3 / 256.0F;
      int[] intArray = quad.getVertexData();
      int count = intArray.length / 4;

      for (int innerIndex = 0; innerIndex < 4; innerIndex++) {
         int i = innerIndex * count;
         float f5 = Float.intBitsToFloat(intArray[i + 4]);
         float f6 = Float.intBitsToFloat(intArray[i + 4 + 1]);
         if (!this.equalsDelta(f5, minU, f2) && !this.equalsDelta(f5, maxU, f2)) {
            return false;
         }

         if (!this.equalsDelta(f6, minV, f4) && !this.equalsDelta(f6, maxV, f4)) {
            return false;
         }
      }

      return true;
   }

   private boolean equalsDelta(float x1, float x2, float deltaMax) {
      float f = MathHelper.abs(x1 - x2);
      return f < deltaMax;
   }

   private BakedQuad makeQuad(BakedQuad quad, int rotate, boolean flipU) {
      int[] intArray = quad.getVertexData();
      int tintIndex = quad.getTintIndex();
      EnumFacing facing = quad.getFace();
      TextureAtlasSprite textureAtlasSprite = quad.getSprite();
      if (!this.isFullSprite(quad)) {
         rotate = 0;
      }

      intArray = this.transformVertexData(intArray, rotate, flipU);
      return new BakedQuad(intArray, tintIndex, facing, textureAtlasSprite);
   }
}
