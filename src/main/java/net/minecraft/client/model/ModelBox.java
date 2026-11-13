package net.minecraft.client.model;

import net.minecraft.client.renderer.WorldRenderer;

public class ModelBox {
   public final float posX1;
   public final float posY1;
   public final float posZ1;
   public final float posX2;
   public String boxName;
   public final float posY2;
   public final float posZ2;
   private PositionTextureVertex[] vertexPositions;
   private TexturedQuad[] quadList;

   private TexturedQuad makeTexturedQuad(PositionTextureVertex[] vertices, int[] textureCoords, boolean mirror, float textureWidth, float textureHeight) {
      return textureCoords == null
         ? null
         : (
            mirror
               ? new TexturedQuad(vertices, textureCoords[2], textureCoords[3], textureCoords[0], textureCoords[1], textureWidth, textureHeight)
               : new TexturedQuad(vertices, textureCoords[0], textureCoords[1], textureCoords[2], textureCoords[3], textureWidth, textureHeight)
         );
   }

   public void render(WorldRenderer renderer, float scale) {
      for (int index = 0; index < this.quadList.length; index++) {
         TexturedQuad texturedQuad = this.quadList[index];
         if (texturedQuad != null) {
            texturedQuad.draw(renderer, scale);
         }
      }
   }

   public ModelBox(ModelRenderer renderer, int textureX, int textureY, float x, float y, float z, int sizeX, int sizeY, int sizeZ, float delta) {
      this(renderer, textureX, textureY, x, y, z, sizeX, sizeY, sizeZ, delta, renderer.mirror);
   }

   public ModelBox setBoxName(String name) {
      this.boxName = name;
      return this;
   }

   public ModelBox(ModelRenderer renderer, int[][] textureData, float x, float y, float z, float sizeX, float sizeY, float sizeZ, float delta, boolean mirror) {
      this.posX1 = x;
      this.posY1 = y;
      this.posZ1 = z;
      this.posX2 = x + sizeX;
      this.posY2 = y + sizeY;
      this.posZ2 = z + sizeZ;
      this.vertexPositions = new PositionTextureVertex[8];
      this.quadList = new TexturedQuad[6];
      float f = x + sizeX;
      float f2 = y + sizeY;
      float f3 = z + sizeZ;
      x -= delta;
      y -= delta;
      z -= delta;
      f += delta;
      f2 += delta;
      f3 += delta;
      if (mirror) {
         float f4 = f;
         f = x;
         x = f4;
      }

      PositionTextureVertex positionTextureVertex = new PositionTextureVertex(x, y, z, 0.0F, 0.0F);
      PositionTextureVertex positionTextureVertex2 = new PositionTextureVertex(f, y, z, 0.0F, 8.0F);
      PositionTextureVertex positionTextureVertex3 = new PositionTextureVertex(f, f2, z, 8.0F, 8.0F);
      PositionTextureVertex positionTextureVertex4 = new PositionTextureVertex(x, f2, z, 8.0F, 0.0F);
      PositionTextureVertex positionTextureVertex5 = new PositionTextureVertex(x, y, f3, 0.0F, 0.0F);
      PositionTextureVertex positionTextureVertex6 = new PositionTextureVertex(f, y, f3, 0.0F, 8.0F);
      PositionTextureVertex positionTextureVertex7 = new PositionTextureVertex(f, f2, f3, 8.0F, 8.0F);
      PositionTextureVertex positionTextureVertex8 = new PositionTextureVertex(x, f2, f3, 8.0F, 0.0F);
      this.vertexPositions[0] = positionTextureVertex;
      this.vertexPositions[1] = positionTextureVertex2;
      this.vertexPositions[2] = positionTextureVertex3;
      this.vertexPositions[3] = positionTextureVertex4;
      this.vertexPositions[4] = positionTextureVertex5;
      this.vertexPositions[5] = positionTextureVertex6;
      this.vertexPositions[6] = positionTextureVertex7;
      this.vertexPositions[7] = positionTextureVertex8;
      this.quadList[0] = this.makeTexturedQuad(
         new PositionTextureVertex[]{positionTextureVertex6, positionTextureVertex2, positionTextureVertex3, positionTextureVertex7}, textureData[4], false, renderer.textureWidth, renderer.textureHeight
      );
      this.quadList[1] = this.makeTexturedQuad(
         new PositionTextureVertex[]{positionTextureVertex, positionTextureVertex5, positionTextureVertex8, positionTextureVertex4}, textureData[5], false, renderer.textureWidth, renderer.textureHeight
      );
      this.quadList[2] = this.makeTexturedQuad(
         new PositionTextureVertex[]{positionTextureVertex6, positionTextureVertex5, positionTextureVertex, positionTextureVertex2}, textureData[1], true, renderer.textureWidth, renderer.textureHeight
      );
      this.quadList[3] = this.makeTexturedQuad(
         new PositionTextureVertex[]{positionTextureVertex3, positionTextureVertex4, positionTextureVertex8, positionTextureVertex7}, textureData[0], true, renderer.textureWidth, renderer.textureHeight
      );
      this.quadList[4] = this.makeTexturedQuad(
         new PositionTextureVertex[]{positionTextureVertex2, positionTextureVertex, positionTextureVertex4, positionTextureVertex3}, textureData[2], false, renderer.textureWidth, renderer.textureHeight
      );
      this.quadList[5] = this.makeTexturedQuad(
         new PositionTextureVertex[]{positionTextureVertex5, positionTextureVertex6, positionTextureVertex7, positionTextureVertex8}, textureData[3], false, renderer.textureWidth, renderer.textureHeight
      );
      if (mirror) {
         TexturedQuad[] quadList2 = this.quadList;
         int length2 = quadList2.length;

         for (int i = 0; i < length2; i++) {
            quadList2[i].flipFace();
         }
      }
   }

   public ModelBox(ModelRenderer renderer, int textureX, int textureY, float x, float y, float z, int sizeX, int sizeY, int sizeZ, float delta, boolean mirror) {
      this.posX1 = x;
      this.posY1 = y;
      this.posZ1 = z;
      this.posX2 = x + sizeX;
      this.posY2 = y + sizeY;
      this.posZ2 = z + sizeZ;
      this.vertexPositions = new PositionTextureVertex[8];
      this.quadList = new TexturedQuad[6];
      float f = x + sizeX;
      float f2 = y + sizeY;
      float f3 = z + sizeZ;
      x -= delta;
      y -= delta;
      z -= delta;
      f += delta;
      f2 += delta;
      f3 += delta;
      if (mirror) {
         float f4 = f;
         f = x;
         x = f4;
      }

      PositionTextureVertex positionTextureVertex = new PositionTextureVertex(x, y, z, 0.0F, 0.0F);
      PositionTextureVertex positionTextureVertex2 = new PositionTextureVertex(f, y, z, 0.0F, 8.0F);
      PositionTextureVertex positionTextureVertex3 = new PositionTextureVertex(f, f2, z, 8.0F, 8.0F);
      PositionTextureVertex positionTextureVertex4 = new PositionTextureVertex(x, f2, z, 8.0F, 0.0F);
      PositionTextureVertex positionTextureVertex5 = new PositionTextureVertex(x, y, f3, 0.0F, 0.0F);
      PositionTextureVertex positionTextureVertex6 = new PositionTextureVertex(f, y, f3, 0.0F, 8.0F);
      PositionTextureVertex positionTextureVertex7 = new PositionTextureVertex(f, f2, f3, 8.0F, 8.0F);
      PositionTextureVertex positionTextureVertex8 = new PositionTextureVertex(x, f2, f3, 8.0F, 0.0F);
      this.vertexPositions[0] = positionTextureVertex;
      this.vertexPositions[1] = positionTextureVertex2;
      this.vertexPositions[2] = positionTextureVertex3;
      this.vertexPositions[3] = positionTextureVertex4;
      this.vertexPositions[4] = positionTextureVertex5;
      this.vertexPositions[5] = positionTextureVertex6;
      this.vertexPositions[6] = positionTextureVertex7;
      this.vertexPositions[7] = positionTextureVertex8;
      this.quadList[0] = new TexturedQuad(
         new PositionTextureVertex[]{positionTextureVertex6, positionTextureVertex2, positionTextureVertex3, positionTextureVertex7},
         textureX + sizeZ + sizeX,
         textureY + sizeZ,
         textureX + sizeZ + sizeX + sizeZ,
         textureY + sizeZ + sizeY,
         renderer.textureWidth,
         renderer.textureHeight
      );
      this.quadList[1] = new TexturedQuad(
         new PositionTextureVertex[]{positionTextureVertex, positionTextureVertex5, positionTextureVertex8, positionTextureVertex4},
         textureX,
         textureY + sizeZ,
         textureX + sizeZ,
         textureY + sizeZ + sizeY,
         renderer.textureWidth,
         renderer.textureHeight
      );
      this.quadList[2] = new TexturedQuad(
         new PositionTextureVertex[]{positionTextureVertex6, positionTextureVertex5, positionTextureVertex, positionTextureVertex2},
         textureX + sizeZ,
         textureY,
         textureX + sizeZ + sizeX,
         textureY + sizeZ,
         renderer.textureWidth,
         renderer.textureHeight
      );
      this.quadList[3] = new TexturedQuad(
         new PositionTextureVertex[]{positionTextureVertex3, positionTextureVertex4, positionTextureVertex8, positionTextureVertex7},
         textureX + sizeZ + sizeX,
         textureY + sizeZ,
         textureX + sizeZ + sizeX + sizeX,
         textureY,
         renderer.textureWidth,
         renderer.textureHeight
      );
      this.quadList[4] = new TexturedQuad(
         new PositionTextureVertex[]{positionTextureVertex2, positionTextureVertex, positionTextureVertex4, positionTextureVertex3},
         textureX + sizeZ,
         textureY + sizeZ,
         textureX + sizeZ + sizeX,
         textureY + sizeZ + sizeY,
         renderer.textureWidth,
         renderer.textureHeight
      );
      this.quadList[5] = new TexturedQuad(
         new PositionTextureVertex[]{positionTextureVertex5, positionTextureVertex6, positionTextureVertex7, positionTextureVertex8},
         textureX + sizeZ + sizeX + sizeZ,
         textureY + sizeZ,
         textureX + sizeZ + sizeX + sizeZ + sizeX,
         textureY + sizeZ + sizeY,
         renderer.textureWidth,
         renderer.textureHeight
      );
      if (mirror) {
         for (int i = 0; i < this.quadList.length; i++) {
            this.quadList[i].flipFace();
         }
      }
   }
}
