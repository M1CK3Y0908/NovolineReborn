package net.optifine;

import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.optifine.render.RenderEnv;

public class ConnectedTexturesCompact {
   private static final int COMPACT_V = 2;
   private static final int COMPACT_NONE = 0;
   private static final int COMPACT_ALL = 1;
   private static final int COMPACT_HV = 4;
   private static final int COMPACT_H = 3;

   private static BakedQuad makeSpriteQuadCompact(BakedQuad quad, TextureAtlasSprite sprite, int side, int x1, int y1, int x2, int y2) {
      int[] iArray = (int[])quad.getVertexData().clone();
      TextureAtlasSprite textureAtlasSprite = quad.getSprite();

      for (int index = 0; index < 4; index++) {
         fixVertexCompact(iArray, index, textureAtlasSprite, sprite, side, x1, y1, x2, y2);
      }

      return new BakedQuad(iArray, quad.getTintIndex(), quad.getFace(), sprite);
   }

   private static BakedQuad getQuadCompact(
      TextureAtlasSprite sprite, ConnectedTexturesCompact.Dir dir, int x1, int y1, int x2, int y2, int side, BakedQuad quadIn, RenderEnv renderEnv
   ) {
      Map[][] amap = ConnectedTextures.getSpriteQuadCompactMaps();
      if (amap == null) {
         return quadIn;
      }

      int indexInMap = sprite.getIndexInMap();
      if (indexInMap >= 0 && indexInMap < amap.length) {
         Map[] amap1 = amap[indexInMap];
         if (amap1 == null) {
            amap1 = new Map[ConnectedTexturesCompact.Dir.VALUES.length];
            amap[indexInMap] = amap1;
         }

         Map map = amap1[dir.ordinal()];
         if (map == null) {
            map = new IdentityHashMap(1);
            amap1[dir.ordinal()] = map;
         }

         BakedQuad bakedQuad = (BakedQuad)map.get(quadIn);
         if (bakedQuad == null) {
            bakedQuad = makeSpriteQuadCompact(quadIn, sprite, side, x1, y1, x2, y2);
            map.put(quadIn, bakedQuad);
         }

         return bakedQuad;
      } else {
         return quadIn;
      }
   }

   private static BakedQuad getQuadCompact(TextureAtlasSprite sprite, ConnectedTexturesCompact.Dir dir, int side, BakedQuad quad, RenderEnv renderEnv) {
      switch (dir) {
         case UP:
            return getQuadCompact(sprite, dir, 0, 0, 16, 8, side, quad, renderEnv);
         case UP_RIGHT:
            return getQuadCompact(sprite, dir, 8, 0, 16, 8, side, quad, renderEnv);
         case RIGHT:
            return getQuadCompact(sprite, dir, 8, 0, 16, 16, side, quad, renderEnv);
         case DOWN_RIGHT:
            return getQuadCompact(sprite, dir, 8, 8, 16, 16, side, quad, renderEnv);
         case DOWN:
            return getQuadCompact(sprite, dir, 0, 8, 16, 16, side, quad, renderEnv);
         case DOWN_LEFT:
            return getQuadCompact(sprite, dir, 0, 8, 8, 16, side, quad, renderEnv);
         case LEFT:
            return getQuadCompact(sprite, dir, 0, 0, 8, 16, side, quad, renderEnv);
         case UP_LEFT:
            return getQuadCompact(sprite, dir, 0, 0, 8, 8, side, quad, renderEnv);
         default:
            return quad;
      }
   }

   private static BakedQuad[] getQuadsCompactV(int indexUp, int indexDown, TextureAtlasSprite[] sprites, int side, BakedQuad quad, RenderEnv renderEnv) {
      return getQuadsCompact(ConnectedTexturesCompact.Dir.UP, indexUp, ConnectedTexturesCompact.Dir.DOWN, indexDown, sprites, side, quad, renderEnv);
   }

   public static BakedQuad[] getConnectedTextureCtmCompact(int ctmIndex, ConnectedProperties cp, int side, BakedQuad quad, RenderEnv renderEnv) {
      if (cp.ctmTileIndexes != null && ctmIndex >= 0 && ctmIndex < cp.ctmTileIndexes.length) {
         int i = cp.ctmTileIndexes[ctmIndex];
         if (i >= 0 && i <= cp.tileIcons.length) {
            return getQuadsCompact(i, cp.tileIcons, quad, renderEnv);
         }
      }

      switch (ctmIndex) {
         case 1:
            return getQuadsCompactH(0, 3, cp.tileIcons, side, quad, renderEnv);
         case 2:
            return getQuadsCompact(3, cp.tileIcons, quad, renderEnv);
         case 3:
            return getQuadsCompactH(3, 0, cp.tileIcons, side, quad, renderEnv);
         case 4:
            return getQuadsCompact4(0, 3, 2, 4, cp.tileIcons, side, quad, renderEnv);
         case 5:
            return getQuadsCompact4(3, 0, 4, 2, cp.tileIcons, side, quad, renderEnv);
         case 6:
            return getQuadsCompact4(2, 4, 2, 4, cp.tileIcons, side, quad, renderEnv);
         case 7:
            return getQuadsCompact4(3, 3, 4, 4, cp.tileIcons, side, quad, renderEnv);
         case 8:
            return getQuadsCompact4(4, 1, 4, 4, cp.tileIcons, side, quad, renderEnv);
         case 9:
            return getQuadsCompact4(4, 4, 4, 1, cp.tileIcons, side, quad, renderEnv);
         case 10:
            return getQuadsCompact4(1, 4, 1, 4, cp.tileIcons, side, quad, renderEnv);
         case 11:
            return getQuadsCompact4(1, 1, 4, 4, cp.tileIcons, side, quad, renderEnv);
         case 12:
            return getQuadsCompactV(0, 2, cp.tileIcons, side, quad, renderEnv);
         case 13:
            return getQuadsCompact4(0, 3, 2, 1, cp.tileIcons, side, quad, renderEnv);
         case 14:
            return getQuadsCompactV(3, 1, cp.tileIcons, side, quad, renderEnv);
         case 15:
            return getQuadsCompact4(3, 0, 1, 2, cp.tileIcons, side, quad, renderEnv);
         case 16:
            return getQuadsCompact4(2, 4, 0, 3, cp.tileIcons, side, quad, renderEnv);
         case 17:
            return getQuadsCompact4(4, 2, 3, 0, cp.tileIcons, side, quad, renderEnv);
         case 18:
            return getQuadsCompact4(4, 4, 3, 3, cp.tileIcons, side, quad, renderEnv);
         case 19:
            return getQuadsCompact4(4, 2, 4, 2, cp.tileIcons, side, quad, renderEnv);
         case 20:
            return getQuadsCompact4(1, 4, 4, 4, cp.tileIcons, side, quad, renderEnv);
         case 21:
            return getQuadsCompact4(4, 4, 1, 4, cp.tileIcons, side, quad, renderEnv);
         case 22:
            return getQuadsCompact4(4, 4, 1, 1, cp.tileIcons, side, quad, renderEnv);
         case 23:
            return getQuadsCompact4(4, 1, 4, 1, cp.tileIcons, side, quad, renderEnv);
         case 24:
            return getQuadsCompact(2, cp.tileIcons, quad, renderEnv);
         case 25:
            return getQuadsCompactH(2, 1, cp.tileIcons, side, quad, renderEnv);
         case 26:
            return getQuadsCompact(1, cp.tileIcons, quad, renderEnv);
         case 27:
            return getQuadsCompactH(1, 2, cp.tileIcons, side, quad, renderEnv);
         case 28:
            return getQuadsCompact4(2, 4, 2, 1, cp.tileIcons, side, quad, renderEnv);
         case 29:
            return getQuadsCompact4(3, 3, 1, 4, cp.tileIcons, side, quad, renderEnv);
         case 30:
            return getQuadsCompact4(2, 1, 2, 4, cp.tileIcons, side, quad, renderEnv);
         case 31:
            return getQuadsCompact4(3, 3, 4, 1, cp.tileIcons, side, quad, renderEnv);
         case 32:
            return getQuadsCompact4(1, 1, 1, 4, cp.tileIcons, side, quad, renderEnv);
         case 33:
            return getQuadsCompact4(1, 1, 4, 1, cp.tileIcons, side, quad, renderEnv);
         case 34:
            return getQuadsCompact4(4, 1, 1, 4, cp.tileIcons, side, quad, renderEnv);
         case 35:
            return getQuadsCompact4(1, 4, 4, 1, cp.tileIcons, side, quad, renderEnv);
         case 36:
            return getQuadsCompactV(2, 0, cp.tileIcons, side, quad, renderEnv);
         case 37:
            return getQuadsCompact4(2, 1, 0, 3, cp.tileIcons, side, quad, renderEnv);
         case 38:
            return getQuadsCompactV(1, 3, cp.tileIcons, side, quad, renderEnv);
         case 39:
            return getQuadsCompact4(1, 2, 3, 0, cp.tileIcons, side, quad, renderEnv);
         case 40:
            return getQuadsCompact4(4, 1, 3, 3, cp.tileIcons, side, quad, renderEnv);
         case 41:
            return getQuadsCompact4(1, 2, 4, 2, cp.tileIcons, side, quad, renderEnv);
         case 42:
            return getQuadsCompact4(1, 4, 3, 3, cp.tileIcons, side, quad, renderEnv);
         case 43:
            return getQuadsCompact4(4, 2, 1, 2, cp.tileIcons, side, quad, renderEnv);
         case 44:
            return getQuadsCompact4(1, 4, 1, 1, cp.tileIcons, side, quad, renderEnv);
         case 45:
            return getQuadsCompact4(4, 1, 1, 1, cp.tileIcons, side, quad, renderEnv);
         case 46:
            return getQuadsCompact(4, cp.tileIcons, quad, renderEnv);
         default:
            return getQuadsCompact(0, cp.tileIcons, quad, renderEnv);
      }
   }

   private static void fixVertexCompact(
      int[] data, int vertex, TextureAtlasSprite spriteFrom, TextureAtlasSprite spriteTo, int side, int x1, int y1, int x2, int y2
   ) {
      int i = data.length / 4 * vertex;
      float f = Float.intBitsToFloat(data[i + 4]);
      float f2 = Float.intBitsToFloat(data[i + 4 + 1]);
      double spriteU = spriteFrom.getSpriteU16(f);
      double spriteV = spriteFrom.getSpriteV16(f2);
      float f3 = Float.intBitsToFloat(data[i + 0]);
      float f4 = Float.intBitsToFloat(data[i + 1]);
      float f5 = Float.intBitsToFloat(data[i + 2]);
      float f6;
      float f7;
      switch (side) {
         case 0:
            f6 = f3;
            f7 = 1.0F - f5;
            break;
         case 1:
            f6 = f3;
            f7 = f5;
            break;
         case 2:
            f6 = 1.0F - f3;
            f7 = 1.0F - f4;
            break;
         case 3:
            f6 = f3;
            f7 = 1.0F - f4;
            break;
         case 4:
            f6 = f5;
            f7 = 1.0F - f4;
            break;
         case 5:
            f6 = 1.0F - f5;
            f7 = 1.0F - f4;
            break;
         default:
            return;
      }

      float f8 = 15.968F;
      float f9 = 15.968F;
      if (spriteU < x1) {
         f6 = (float)(f6 + (x1 - spriteU) / f8);
         spriteU = x1;
      }

      if (spriteU > x2) {
         f6 = (float)(f6 - (spriteU - x2) / f8);
         spriteU = x2;
      }

      if (spriteV < y1) {
         f7 = (float)(f7 + (y1 - spriteV) / f9);
         spriteV = y1;
      }

      if (spriteV > y2) {
         f7 = (float)(f7 - (spriteV - y2) / f9);
         spriteV = y2;
      }

      switch (side) {
         case 0:
            f3 = f6;
            f5 = 1.0F - f7;
            break;
         case 1:
            f3 = f6;
            f5 = f7;
            break;
         case 2:
            f3 = 1.0F - f6;
            f4 = 1.0F - f7;
            break;
         case 3:
            f3 = f6;
            f4 = 1.0F - f7;
            break;
         case 4:
            f5 = f6;
            f4 = 1.0F - f7;
            break;
         case 5:
            f5 = 1.0F - f6;
            f4 = 1.0F - f7;
            break;
         default:
            return;
      }

      data[i + 4] = Float.floatToRawIntBits(spriteTo.getInterpolatedU(spriteU));
      data[i + 4 + 1] = Float.floatToRawIntBits(spriteTo.getInterpolatedV(spriteV));
      data[i + 0] = Float.floatToRawIntBits(f3);
      data[i + 1] = Float.floatToRawIntBits(f4);
      data[i + 2] = Float.floatToRawIntBits(f5);
   }

   private static BakedQuad[] getQuadsCompact(
      ConnectedTexturesCompact.Dir dir1,
      int index1,
      ConnectedTexturesCompact.Dir dir2,
      int index2,
      ConnectedTexturesCompact.Dir dir3,
      int index3,
      TextureAtlasSprite[] sprites,
      int side,
      BakedQuad quad,
      RenderEnv renderEnv
   ) {
      BakedQuad bakedQuad = getQuadCompact(sprites[index1], dir1, side, quad, renderEnv);
      BakedQuad bakedQuad2 = getQuadCompact(sprites[index2], dir2, side, quad, renderEnv);
      BakedQuad bakedQuad3 = getQuadCompact(sprites[index3], dir3, side, quad, renderEnv);
      return renderEnv.getArrayQuadsCtm(bakedQuad, bakedQuad2, bakedQuad3);
   }

   private static BakedQuad[] getQuadsCompact4(
      int upLeft, int upRight, int downLeft, int downRight, TextureAtlasSprite[] sprites, int side, BakedQuad quad, RenderEnv renderEnv
   ) {
      return upLeft == upRight
         ? (
            downLeft == downRight
               ? getQuadsCompact(ConnectedTexturesCompact.Dir.UP, upLeft, ConnectedTexturesCompact.Dir.DOWN, downLeft, sprites, side, quad, renderEnv)
               : getQuadsCompact(
                  ConnectedTexturesCompact.Dir.UP,
                  upLeft,
                  ConnectedTexturesCompact.Dir.DOWN_LEFT,
                  downLeft,
                  ConnectedTexturesCompact.Dir.DOWN_RIGHT,
                  downRight,
                  sprites,
                  side,
                  quad,
                  renderEnv
               )
         )
         : (
            downLeft == downRight
               ? getQuadsCompact(
                  ConnectedTexturesCompact.Dir.UP_LEFT,
                  upLeft,
                  ConnectedTexturesCompact.Dir.UP_RIGHT,
                  upRight,
                  ConnectedTexturesCompact.Dir.DOWN,
                  downLeft,
                  sprites,
                  side,
                  quad,
                  renderEnv
               )
               : (
                  upLeft == downLeft
                     ? (
                        upRight == downRight
                           ? getQuadsCompact(
                              ConnectedTexturesCompact.Dir.LEFT, upLeft, ConnectedTexturesCompact.Dir.RIGHT, upRight, sprites, side, quad, renderEnv
                           )
                           : getQuadsCompact(
                              ConnectedTexturesCompact.Dir.LEFT,
                              upLeft,
                              ConnectedTexturesCompact.Dir.UP_RIGHT,
                              upRight,
                              ConnectedTexturesCompact.Dir.DOWN_RIGHT,
                              downRight,
                              sprites,
                              side,
                              quad,
                              renderEnv
                           )
                     )
                     : (
                        upRight == downRight
                           ? getQuadsCompact(
                              ConnectedTexturesCompact.Dir.UP_LEFT,
                              upLeft,
                              ConnectedTexturesCompact.Dir.DOWN_LEFT,
                              downLeft,
                              ConnectedTexturesCompact.Dir.RIGHT,
                              upRight,
                              sprites,
                              side,
                              quad,
                              renderEnv
                           )
                           : getQuadsCompact(
                              ConnectedTexturesCompact.Dir.UP_LEFT,
                              upLeft,
                              ConnectedTexturesCompact.Dir.UP_RIGHT,
                              upRight,
                              ConnectedTexturesCompact.Dir.DOWN_LEFT,
                              downLeft,
                              ConnectedTexturesCompact.Dir.DOWN_RIGHT,
                              downRight,
                              sprites,
                              side,
                              quad,
                              renderEnv
                           )
                     )
               )
         );
   }

   private static BakedQuad[] getQuadsCompact(int index, TextureAtlasSprite[] sprites, BakedQuad quad, RenderEnv renderEnv) {
      return ConnectedTextures.getQuads(sprites[index], quad, renderEnv);
   }

   private static BakedQuad[] getQuadsCompactH(int indexLeft, int indexRight, TextureAtlasSprite[] sprites, int side, BakedQuad quad, RenderEnv renderEnv) {
      return getQuadsCompact(ConnectedTexturesCompact.Dir.LEFT, indexLeft, ConnectedTexturesCompact.Dir.RIGHT, indexRight, sprites, side, quad, renderEnv);
   }

   private static BakedQuad[] getQuadsCompact(
      ConnectedTexturesCompact.Dir dir1,
      int index1,
      ConnectedTexturesCompact.Dir dir2,
      int index2,
      TextureAtlasSprite[] sprites,
      int side,
      BakedQuad quad,
      RenderEnv renderEnv
   ) {
      BakedQuad bakedQuad = getQuadCompact(sprites[index1], dir1, side, quad, renderEnv);
      BakedQuad bakedQuad2 = getQuadCompact(sprites[index2], dir2, side, quad, renderEnv);
      return renderEnv.getArrayQuadsCtm(bakedQuad, bakedQuad2);
   }

   private static BakedQuad[] getQuadsCompact(
      ConnectedTexturesCompact.Dir dir1,
      int index1,
      ConnectedTexturesCompact.Dir dir2,
      int index2,
      ConnectedTexturesCompact.Dir dir3,
      int index3,
      ConnectedTexturesCompact.Dir dir4,
      int index4,
      TextureAtlasSprite[] sprites,
      int side,
      BakedQuad quad,
      RenderEnv renderEnv
   ) {
      BakedQuad bakedQuad = getQuadCompact(sprites[index1], dir1, side, quad, renderEnv);
      BakedQuad bakedQuad2 = getQuadCompact(sprites[index2], dir2, side, quad, renderEnv);
      BakedQuad bakedQuad3 = getQuadCompact(sprites[index3], dir3, side, quad, renderEnv);
      BakedQuad bakedQuad4 = getQuadCompact(sprites[index4], dir4, side, quad, renderEnv);
      return renderEnv.getArrayQuadsCtm(bakedQuad, bakedQuad2, bakedQuad3, bakedQuad4);
   }

   private enum Dir {
      UP,
      UP_RIGHT,
      RIGHT,
      DOWN_RIGHT,
      DOWN,
      DOWN_LEFT,
      LEFT,
      UP_LEFT;

      public static final ConnectedTexturesCompact.Dir[] VALUES = values();
   }
}
