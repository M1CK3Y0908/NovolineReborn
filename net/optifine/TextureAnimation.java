package net.optifine;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Properties;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TextureAnimation {
    private String srcTex = null;
    private String dstTex = null;
    ResourceLocation dstTexLoc = null;
    private int dstTextId = -1;
    private int dstX = 0;
    private int dstY = 0;
    private int frameWidth = 0;
    private int frameHeight = 0;
    private TextureAnimationFrame[] frames = null;
    private int activeFrame = 0;
    byte[] srcData = null;
    private ByteBuffer imageData = null;

    public TextureAnimation(
        String texFrom,
        byte[] srcData,
        String texTo,
        ResourceLocation locTexTo,
        int dstX,
        int dstY,
        int frameWidth,
        int frameHeight,
        Properties props,
        int durDef
    ) {
        this.srcTex = texFrom;
        this.dstTex = texTo;
        this.dstTexLoc = locTexTo;
        this.dstX = dstX;
        this.dstY = dstY;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        int i = frameWidth * frameHeight * 4;
        if (srcData.length % i != 0) {
            Config.warn("Invalid animated texture length: " + srcData.length + ", frameWidth: " + frameWidth + ", frameHeight: " + frameHeight);
        }

        this.srcData = srcData;
        int j = srcData.length / i;
        if (props.get("tile.0") != null) {
            for (int k = 0; props.get("tile." + k) != null; k++) {
                j = k + 1;
            }
        }

        String s2 = (String)props.get("duration");
        int l = Config.parseInt(s2, durDef);
        this.frames = new TextureAnimationFrame[j];

        for (int i1 = 0; i1 < this.frames.length; i1++) {
            String s = (String)props.get("tile." + i1);
            int j1 = Config.parseInt(s, i1);
            String s1 = (String)props.get("duration." + i1);
            int k1 = Config.parseInt(s1, l);
            TextureAnimationFrame textureanimationframe = new TextureAnimationFrame(j1, k1);
            this.frames[i1] = textureanimationframe;
        }
    }

    public boolean nextFrame() {
        if (this.frames.length <= 0) {
            return false;
        }

        if (this.activeFrame >= this.frames.length) {
            this.activeFrame = 0;
        }

        TextureAnimationFrame textureanimationframe = this.frames[this.activeFrame];
        textureanimationframe.counter++;
        if (textureanimationframe.counter < textureanimationframe.duration) {
            return false;
        }

        textureanimationframe.counter = 0;
        this.activeFrame++;
        if (this.activeFrame >= this.frames.length) {
            this.activeFrame = 0;
        }

        return true;
    }

    public int getActiveFrameIndex() {
        if (this.frames.length <= 0) {
            return 0;
        }

        if (this.activeFrame >= this.frames.length) {
            this.activeFrame = 0;
        }

        TextureAnimationFrame textureanimationframe = this.frames[this.activeFrame];
        return textureanimationframe.index;
    }

    public int getFrameCount() {
        return this.frames.length;
    }

    public boolean updateTexture() {
        if (this.dstTextId < 0) {
            ITextureObject itextureobject = TextureUtils.getTexture(this.dstTexLoc);
            if (itextureobject == null) {
                return false;
            }

            this.dstTextId = itextureobject.getGlTextureId();
        }

        if (this.imageData == null) {
            this.imageData = GLAllocation.createDirectByteBuffer(this.srcData.length);
            this.imageData.put(this.srcData);
            this.srcData = null;
        }

        if (!this.nextFrame()) {
            return false;
        }

        int k = this.frameWidth * this.frameHeight * 4;
        int i = this.getActiveFrameIndex();
        int j = k * i;
        if (j + k > this.imageData.capacity()) {
            return false;
        }

        ((Buffer)this.imageData).position(j);
        GlStateManager.bindTexture(this.dstTextId);
        GL11.glTexSubImage2D(
            GL11.GL_TEXTURE_2D, 0, this.dstX, this.dstY, this.frameWidth, this.frameHeight, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, this.imageData
        );
        return true;
    }

    public String getSrcTex() {
        return this.srcTex;
    }

    public String getDstTex() {
        return this.dstTex;
    }

    public ResourceLocation getDstTexLoc() {
        return this.dstTexLoc;
    }
}
