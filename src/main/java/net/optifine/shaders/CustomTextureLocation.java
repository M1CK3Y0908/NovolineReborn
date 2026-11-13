package net.optifine.shaders;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

public class CustomTextureLocation implements ICustomTexture {
   public static final int VARIANT_NORMAL = 1;
   private ITextureObject texture;
   public static final int VARIANT_BASE = 0;
   public static final int VARIANT_SPECULAR = 2;
   private int textureUnit = -1;
   private int variant = 0;
   private ResourceLocation location;

   @Override
   public int getTextureId() {
      ITextureObject texture2 = this.getTexture();
      if (this.variant != 0 && texture2 instanceof AbstractTexture) {
         MultiTexID multiTex2 = ((AbstractTexture)texture2).multiTex;
         if (multiTex2 != null) {
            if (this.variant == 1) {
               return multiTex2.norm;
            }

            if (this.variant == 2) {
               return multiTex2.spec;
            }
         }
      }

      return texture2.getGlTextureId();
   }

   @Override
   public void deleteTexture() {
   }

   @Override
   public int getTarget() {
      return 3553;
   }

   public CustomTextureLocation(int textureUnit, ResourceLocation location, int variant) {
      this.textureUnit = textureUnit;
      this.location = location;
      this.variant = variant;
   }

   @Override
   public int getTextureUnit() {
      return this.textureUnit;
   }

   @Override
   public String toString() {
      return "textureUnit: "
         + this.textureUnit
         + ", location: "
         + this.location
         + ", glTextureId: "
         + (this.texture != null ? this.texture.getGlTextureId() : "");
   }

   public ITextureObject getTexture() {
      if (this.texture == null) {
         TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
         this.texture = textureManager.getTexture(this.location);
         if (this.texture == null) {
            this.texture = new SimpleTexture(this.location);
            textureManager.loadTexture(this.location, this.texture);
            this.texture = textureManager.getTexture(this.location);
         }
      }

      return this.texture;
   }
}
