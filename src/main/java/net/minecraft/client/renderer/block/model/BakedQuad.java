package net.minecraft.client.renderer.block.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.src.Config;
import net.minecraft.util.EnumFacing;
import net.optifine.model.QuadBounds;
import wtf.novoline.api.Contract3;

public class BakedQuad implements Contract3 {
   private int[] vertexDataSingle = null;
   protected int[] vertexData;
   protected final int tintIndex;
   private boolean quadEmissiveChecked;
   private QuadBounds quadBounds;
   protected EnumFacing face;
   protected TextureAtlasSprite sprite;
   private BakedQuad quadEmissive;

   public float getMidX() {
      QuadBounds quadBounds = this.getQuadBounds();
      return (quadBounds.getMaxX() + quadBounds.getMinX()) / 2.0F;
   }

   public double getMidZ() {
      QuadBounds quadBounds = this.getQuadBounds();
      return (quadBounds.getMaxZ() + quadBounds.getMinZ()) / 2.0F;
   }

   public int getTintIndex() {
      return this.tintIndex;
   }

   public BakedQuad getQuadEmissive() {
      if (this.quadEmissiveChecked) {
         return this.quadEmissive;
      }

      if (this.quadEmissive == null && this.sprite != null && this.sprite.spriteEmissive != null) {
         this.quadEmissive = new BreakingFour(this, this.sprite.spriteEmissive);
      }

      this.quadEmissiveChecked = true;
      return this.quadEmissive;
   }

   private static int[] makeVertexDataSingle(int[] arg0, TextureAtlasSprite arg1) {
      int[] iArray = (int[])arg0.clone();
      int count = iArray.length / 4;

      for (int innerIndex = 0; innerIndex < 4; innerIndex++) {
         int i = innerIndex * count;
         float f = Float.intBitsToFloat(iArray[i + 4]);
         float f2 = Float.intBitsToFloat(iArray[i + 4 + 1]);
         float f3 = arg1.toSingleU(f);
         float f4 = arg1.toSingleV(f2);
         iArray[i + 4] = Float.floatToRawIntBits(f3);
         iArray[i + 4 + 1] = Float.floatToRawIntBits(f4);
      }

      return iArray;
   }

   public TextureAtlasSprite getSprite() {
      if (this.sprite == null) {
         this.sprite = getSpriteByUv(this.getVertexData());
      }

      return this.sprite;
   }

   public QuadBounds getQuadBounds() {
      if (this.quadBounds == null) {
         this.quadBounds = new QuadBounds(this.getVertexData());
      }

      return this.quadBounds;
   }

   public int[] getVertexDataSingle() {
      if (this.vertexDataSingle == null) {
         this.vertexDataSingle = makeVertexDataSingle(this.getVertexData(), this.getSprite());
      }

      return this.vertexDataSingle;
   }

   public BakedQuad(int[] iArray, int i, EnumFacing enumFacing, TextureAtlasSprite textureAtlasSprite) {
      this.vertexData = iArray;
      this.tintIndex = i;
      this.face = enumFacing;
      this.sprite = textureAtlasSprite;
      this.fixVertexData();
   }

   public boolean isFullQuad() {
      return this.getQuadBounds().isFullQuad(this.face);
   }

   private static int[] compactVertexData(int[] arg0) {
      int count = arg0.length / 4;
      int i = count / 2;
      int[] intArray = new int[i * 4];

      for (int nestedIndex = 0; nestedIndex < 4; nestedIndex++) {
         System.arraycopy(arg0, nestedIndex * count, intArray, nestedIndex * i, i);
      }

      return intArray;
   }

   public EnumFacing getFace() {
      if (this.face == null) {
         this.face = FaceBakery.getFacingFromVertexData(this.getVertexData());
      }

      return this.face;
   }

   public BakedQuad(int[] vertexDataIn, int tintIndexIn, EnumFacing faceIn) {
      this.vertexData = vertexDataIn;
      this.tintIndex = tintIndexIn;
      this.face = faceIn;
      this.fixVertexData();
   }

   @Override
   public String toString() {
      return "vertex: " + this.vertexData.length / 7 + ", tint: " + this.tintIndex + ", facing: " + this.face + ", sprite: " + this.sprite;
   }

   private static int[] expandVertexData(int[] arg0) {
      int count = arg0.length / 4;
      int i = count * 2;
      int[] intArray = new int[i * 4];

      for (int nestedIndex = 0; nestedIndex < 4; nestedIndex++) {
         System.arraycopy(arg0, nestedIndex * count, intArray, nestedIndex * i, count);
      }

      return intArray;
   }

   public int[] getVertexData() {
      this.fixVertexData();
      return this.vertexData;
   }

   private static TextureAtlasSprite getSpriteByUv(int[] arg0) {
      float f = 1.0F;
      float f2 = 1.0F;
      float f3 = 0.0F;
      float f4 = 0.0F;
      int count = arg0.length / 4;

      for (int innerIndex = 0; innerIndex < 4; innerIndex++) {
         int i = innerIndex * count;
         float f5 = Float.intBitsToFloat(arg0[i + 4]);
         float f6 = Float.intBitsToFloat(arg0[i + 4 + 1]);
         f = Math.min(f, f5);
         f2 = Math.min(f2, f6);
         f3 = Math.max(f3, f5);
         f4 = Math.max(f4, f6);
      }

      float f7 = (f + f3) / 2.0F;
      float f8 = (f2 + f4) / 2.0F;
      return Minecraft.getMinecraft().getTextureMapBlocks().getIconByUV(f7, f8);
   }

   public boolean hasTintIndex() {
      return this.tintIndex != -1;
   }

   protected void fixVertexData() {
      if (Config.isShaders()) {
         if (this.vertexData.length == 28) {
            this.vertexData = expandVertexData(this.vertexData);
         }
      } else if (this.vertexData.length == 56) {
         this.vertexData = compactVertexData(this.vertexData);
      }
   }

   public boolean isFaceQuad() {
      return this.getQuadBounds().isFaceQuad(this.face);
   }
}
