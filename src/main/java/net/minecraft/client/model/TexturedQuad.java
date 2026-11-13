package net.minecraft.client.model;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.src.Config;
import net.minecraft.util.Vec3;
import net.optifine.shaders.SVertexFormat;

public class TexturedQuad {
   public PositionTextureVertex[] vertexPositions;
   private boolean invertNormal;
   public int nVertices;

   public void draw(WorldRenderer renderer, float scale) {
      Vec3 vec3 = this.vertexPositions[1].vector3D.subtractReverse(this.vertexPositions[0].vector3D);
      Vec3 vec32 = this.vertexPositions[1].vector3D.subtractReverse(this.vertexPositions[2].vector3D).crossProduct(vec3).normalize();
      float f = (float)vec32.xCoord;
      float f2 = (float)vec32.yCoord;
      float f3 = (float)vec32.zCoord;
      if (this.invertNormal) {
         f = -f;
         f2 = -f2;
         f3 = -f3;
      }

      if (Config.isShaders()) {
         renderer.begin(7, SVertexFormat.defVertexFormatTextured);
      } else {
         renderer.begin(7, DefaultVertexFormats.vertexFormat);
      }

      for (int i = 0; i < 4; i++) {
         PositionTextureVertex positionTextureVertex = this.vertexPositions[i];
         renderer.pos(positionTextureVertex.vector3D.xCoord * scale, positionTextureVertex.vector3D.yCoord * scale, positionTextureVertex.vector3D.zCoord * scale)
            .tex(positionTextureVertex.texturePositionX, positionTextureVertex.texturePositionY)
            .normal(f, f2, f3)
            .endVertex();
      }

      Tessellator.getInstance().draw();
   }

   public TexturedQuad(PositionTextureVertex[] vertices) {
      this.vertexPositions = vertices;
      this.nVertices = vertices.length;
   }

   public void flipFace() {
      PositionTextureVertex[] apositiontexturevertex = new PositionTextureVertex[this.vertexPositions.length];

      for (int index = 0; index < this.vertexPositions.length; index++) {
         apositiontexturevertex[index] = this.vertexPositions[this.vertexPositions.length - index - 1];
      }

      this.vertexPositions = apositiontexturevertex;
   }

   public TexturedQuad(
      PositionTextureVertex[] vertices, int texcoordU1, int texcoordV1, int texcoordU2, int texcoordV2, float textureWidth, float textureHeight
   ) {
      this(vertices);
      float f = 0.0F / textureWidth;
      float f2 = 0.0F / textureHeight;
      vertices[0] = vertices[0].setTexturePosition(texcoordU2 / textureWidth - f, texcoordV1 / textureHeight + f2);
      vertices[1] = vertices[1].setTexturePosition(texcoordU1 / textureWidth + f, texcoordV1 / textureHeight + f2);
      vertices[2] = vertices[2].setTexturePosition(texcoordU1 / textureWidth + f, texcoordV2 / textureHeight - f2);
      vertices[3] = vertices[3].setTexturePosition(texcoordU2 / textureWidth - f, texcoordV2 / textureHeight - f2);
   }
}
