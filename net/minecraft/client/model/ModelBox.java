package net.minecraft.client.model;

import net.minecraft.client.renderer.WorldRenderer;

public class ModelBox {
    private PositionTextureVertex[] vertexPositions;
    private TexturedQuad[] quadList;
    public final float posX1;
    public final float posY1;
    public final float posZ1;
    public final float posX2;
    public final float posY2;
    public final float posZ2;
    public String boxName;
    private static final String __OBFID = "CL_00000872";

    public ModelBox(
        ModelRenderer p_i46359_1_,
        int p_i46359_2_,
        int p_i46359_3_,
        float p_i46359_4_,
        float p_i46359_5_,
        float p_i46359_6_,
        int p_i46359_7_,
        int p_i46359_8_,
        int p_i46359_9_,
        float p_i46359_10_
    ) {
        this(
            p_i46359_1_,
            p_i46359_2_,
            p_i46359_3_,
            p_i46359_4_,
            p_i46359_5_,
            p_i46359_6_,
            p_i46359_7_,
            p_i46359_8_,
            p_i46359_9_,
            p_i46359_10_,
            p_i46359_1_.mirror
        );
    }

    public ModelBox(ModelRenderer renderer, int[][] faceUvs, float x, float y, float z, float dx, float dy, float dz, float delta, boolean mirror) {
        this.posX1 = x;
        this.posY1 = y;
        this.posZ1 = z;
        this.posX2 = x + dx;
        this.posY2 = y + dy;
        this.posZ2 = z + dz;
        this.vertexPositions = new PositionTextureVertex[8];
        this.quadList = new TexturedQuad[6];
        float f = x + dx;
        float f1 = y + dy;
        float f2 = z + dz;
        x -= delta;
        y -= delta;
        z -= delta;
        f += delta;
        f1 += delta;
        f2 += delta;
        if (mirror) {
            float f3 = f;
            f = x;
            x = f3;
        }

        PositionTextureVertex positiontexturevertex7 = new PositionTextureVertex(x, y, z, 0.0F, 0.0F);
        PositionTextureVertex positiontexturevertex = new PositionTextureVertex(f, y, z, 0.0F, 8.0F);
        PositionTextureVertex positiontexturevertex1 = new PositionTextureVertex(f, f1, z, 8.0F, 8.0F);
        PositionTextureVertex positiontexturevertex2 = new PositionTextureVertex(x, f1, z, 8.0F, 0.0F);
        PositionTextureVertex positiontexturevertex3 = new PositionTextureVertex(x, y, f2, 0.0F, 0.0F);
        PositionTextureVertex positiontexturevertex4 = new PositionTextureVertex(f, y, f2, 0.0F, 8.0F);
        PositionTextureVertex positiontexturevertex5 = new PositionTextureVertex(f, f1, f2, 8.0F, 8.0F);
        PositionTextureVertex positiontexturevertex6 = new PositionTextureVertex(x, f1, f2, 8.0F, 0.0F);
        this.vertexPositions[0] = positiontexturevertex7;
        this.vertexPositions[1] = positiontexturevertex;
        this.vertexPositions[2] = positiontexturevertex1;
        this.vertexPositions[3] = positiontexturevertex2;
        this.vertexPositions[4] = positiontexturevertex3;
        this.vertexPositions[5] = positiontexturevertex4;
        this.vertexPositions[6] = positiontexturevertex5;
        this.vertexPositions[7] = positiontexturevertex6;
        this.quadList[0] = this.makeTexturedQuad(
            new PositionTextureVertex[]{positiontexturevertex4, positiontexturevertex, positiontexturevertex1, positiontexturevertex5},
            faceUvs[4],
            false,
            renderer.textureWidth,
            renderer.textureHeight
        );
        this.quadList[1] = this.makeTexturedQuad(
            new PositionTextureVertex[]{positiontexturevertex7, positiontexturevertex3, positiontexturevertex6, positiontexturevertex2},
            faceUvs[5],
            false,
            renderer.textureWidth,
            renderer.textureHeight
        );
        this.quadList[2] = this.makeTexturedQuad(
            new PositionTextureVertex[]{positiontexturevertex4, positiontexturevertex3, positiontexturevertex7, positiontexturevertex},
            faceUvs[1],
            true,
            renderer.textureWidth,
            renderer.textureHeight
        );
        this.quadList[3] = this.makeTexturedQuad(
            new PositionTextureVertex[]{positiontexturevertex1, positiontexturevertex2, positiontexturevertex6, positiontexturevertex5},
            faceUvs[0],
            true,
            renderer.textureWidth,
            renderer.textureHeight
        );
        this.quadList[4] = this.makeTexturedQuad(
            new PositionTextureVertex[]{positiontexturevertex, positiontexturevertex7, positiontexturevertex2, positiontexturevertex1},
            faceUvs[2],
            false,
            renderer.textureWidth,
            renderer.textureHeight
        );
        this.quadList[5] = this.makeTexturedQuad(
            new PositionTextureVertex[]{positiontexturevertex3, positiontexturevertex4, positiontexturevertex5, positiontexturevertex6},
            faceUvs[3],
            false,
            renderer.textureWidth,
            renderer.textureHeight
        );
        if (mirror) {
            for (TexturedQuad texturedquad : this.quadList) {
                texturedquad.flipFace();
            }
        }
    }

    private TexturedQuad makeTexturedQuad(
        PositionTextureVertex[] positionTextureVertexs, int[] faceUvs, boolean reverseUV, float textureWidth, float textureHeight
    ) {
        if (faceUvs == null) {
            return null;
        } else {
            return reverseUV
                ? new TexturedQuad(positionTextureVertexs, faceUvs[2], faceUvs[3], faceUvs[0], faceUvs[1], textureWidth, textureHeight)
                : new TexturedQuad(positionTextureVertexs, faceUvs[0], faceUvs[1], faceUvs[2], faceUvs[3], textureWidth, textureHeight);
        }
    }

    public ModelBox(
        ModelRenderer p_i46301_1_,
        int p_i46301_2_,
        int p_i46301_3_,
        float p_i46301_4_,
        float p_i46301_5_,
        float p_i46301_6_,
        int p_i46301_7_,
        int p_i46301_8_,
        int p_i46301_9_,
        float p_i46301_10_,
        boolean p_i46301_11_
    ) {
        this.posX1 = p_i46301_4_;
        this.posY1 = p_i46301_5_;
        this.posZ1 = p_i46301_6_;
        this.posX2 = p_i46301_4_ + p_i46301_7_;
        this.posY2 = p_i46301_5_ + p_i46301_8_;
        this.posZ2 = p_i46301_6_ + p_i46301_9_;
        this.vertexPositions = new PositionTextureVertex[8];
        this.quadList = new TexturedQuad[6];
        float f = p_i46301_4_ + p_i46301_7_;
        float f1 = p_i46301_5_ + p_i46301_8_;
        float f2 = p_i46301_6_ + p_i46301_9_;
        p_i46301_4_ -= p_i46301_10_;
        p_i46301_5_ -= p_i46301_10_;
        p_i46301_6_ -= p_i46301_10_;
        f += p_i46301_10_;
        f1 += p_i46301_10_;
        f2 += p_i46301_10_;
        if (p_i46301_11_) {
            float f3 = f;
            f = p_i46301_4_;
            p_i46301_4_ = f3;
        }

        PositionTextureVertex positiontexturevertex7 = new PositionTextureVertex(p_i46301_4_, p_i46301_5_, p_i46301_6_, 0.0F, 0.0F);
        PositionTextureVertex positiontexturevertex = new PositionTextureVertex(f, p_i46301_5_, p_i46301_6_, 0.0F, 8.0F);
        PositionTextureVertex positiontexturevertex1 = new PositionTextureVertex(f, f1, p_i46301_6_, 8.0F, 8.0F);
        PositionTextureVertex positiontexturevertex2 = new PositionTextureVertex(p_i46301_4_, f1, p_i46301_6_, 8.0F, 0.0F);
        PositionTextureVertex positiontexturevertex3 = new PositionTextureVertex(p_i46301_4_, p_i46301_5_, f2, 0.0F, 0.0F);
        PositionTextureVertex positiontexturevertex4 = new PositionTextureVertex(f, p_i46301_5_, f2, 0.0F, 8.0F);
        PositionTextureVertex positiontexturevertex5 = new PositionTextureVertex(f, f1, f2, 8.0F, 8.0F);
        PositionTextureVertex positiontexturevertex6 = new PositionTextureVertex(p_i46301_4_, f1, f2, 8.0F, 0.0F);
        this.vertexPositions[0] = positiontexturevertex7;
        this.vertexPositions[1] = positiontexturevertex;
        this.vertexPositions[2] = positiontexturevertex1;
        this.vertexPositions[3] = positiontexturevertex2;
        this.vertexPositions[4] = positiontexturevertex3;
        this.vertexPositions[5] = positiontexturevertex4;
        this.vertexPositions[6] = positiontexturevertex5;
        this.vertexPositions[7] = positiontexturevertex6;
        this.quadList[0] = new TexturedQuad(
            new PositionTextureVertex[]{positiontexturevertex4, positiontexturevertex, positiontexturevertex1, positiontexturevertex5},
            p_i46301_2_ + p_i46301_9_ + p_i46301_7_,
            p_i46301_3_ + p_i46301_9_,
            p_i46301_2_ + p_i46301_9_ + p_i46301_7_ + p_i46301_9_,
            p_i46301_3_ + p_i46301_9_ + p_i46301_8_,
            p_i46301_1_.textureWidth,
            p_i46301_1_.textureHeight
        );
        this.quadList[1] = new TexturedQuad(
            new PositionTextureVertex[]{positiontexturevertex7, positiontexturevertex3, positiontexturevertex6, positiontexturevertex2},
            p_i46301_2_,
            p_i46301_3_ + p_i46301_9_,
            p_i46301_2_ + p_i46301_9_,
            p_i46301_3_ + p_i46301_9_ + p_i46301_8_,
            p_i46301_1_.textureWidth,
            p_i46301_1_.textureHeight
        );
        this.quadList[2] = new TexturedQuad(
            new PositionTextureVertex[]{positiontexturevertex4, positiontexturevertex3, positiontexturevertex7, positiontexturevertex},
            p_i46301_2_ + p_i46301_9_,
            p_i46301_3_,
            p_i46301_2_ + p_i46301_9_ + p_i46301_7_,
            p_i46301_3_ + p_i46301_9_,
            p_i46301_1_.textureWidth,
            p_i46301_1_.textureHeight
        );
        this.quadList[3] = new TexturedQuad(
            new PositionTextureVertex[]{positiontexturevertex1, positiontexturevertex2, positiontexturevertex6, positiontexturevertex5},
            p_i46301_2_ + p_i46301_9_ + p_i46301_7_,
            p_i46301_3_ + p_i46301_9_,
            p_i46301_2_ + p_i46301_9_ + p_i46301_7_ + p_i46301_7_,
            p_i46301_3_,
            p_i46301_1_.textureWidth,
            p_i46301_1_.textureHeight
        );
        this.quadList[4] = new TexturedQuad(
            new PositionTextureVertex[]{positiontexturevertex, positiontexturevertex7, positiontexturevertex2, positiontexturevertex1},
            p_i46301_2_ + p_i46301_9_,
            p_i46301_3_ + p_i46301_9_,
            p_i46301_2_ + p_i46301_9_ + p_i46301_7_,
            p_i46301_3_ + p_i46301_9_ + p_i46301_8_,
            p_i46301_1_.textureWidth,
            p_i46301_1_.textureHeight
        );
        this.quadList[5] = new TexturedQuad(
            new PositionTextureVertex[]{positiontexturevertex3, positiontexturevertex4, positiontexturevertex5, positiontexturevertex6},
            p_i46301_2_ + p_i46301_9_ + p_i46301_7_ + p_i46301_9_,
            p_i46301_3_ + p_i46301_9_,
            p_i46301_2_ + p_i46301_9_ + p_i46301_7_ + p_i46301_9_ + p_i46301_7_,
            p_i46301_3_ + p_i46301_9_ + p_i46301_8_,
            p_i46301_1_.textureWidth,
            p_i46301_1_.textureHeight
        );
        if (p_i46301_11_) {
            for (int i = 0; i < this.quadList.length; i++) {
                this.quadList[i].flipFace();
            }
        }
    }

    public void render(WorldRenderer p_178780_1_, float p_178780_2_) {
        for (int i = 0; i < this.quadList.length; i++) {
            TexturedQuad texturedquad = this.quadList[i];
            if (texturedquad != null) {
                texturedquad.draw(p_178780_1_, p_178780_2_);
            }
        }
    }

    public ModelBox setBoxName(String p_78244_1_) {
        this.boxName = p_78244_1_;
        return this;
    }
}
