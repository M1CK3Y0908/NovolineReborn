package net.minecraft.client.model;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.entity.model.anim.ModelUpdater;
import net.optifine.model.ModelSprite;
import net.optifine.shaders.Shaders;
import org.lwjgl.opengl.GL11;

public class ModelRenderer {
   public float rotationPointZ;
   private int countResetDisplayList;
   public float offsetY;
   public List spriteList = new ArrayList();
   public float offsetX;
   public boolean mirrorV = false;
   private int displayList;
   public boolean mirror;
   public float rotateAngleX;
   public float scaleX = 1.0F;
   public float scaleY = 1.0F;
   public float rotationPointY;
   private boolean compiled;
   public float scaleZ = 1.0F;
   private ResourceLocation textureLocation = null;
   public float rotateAngleY;
   private String id = null;
   private RenderGlobal renderGlobal = Config.getRenderGlobal();
   public float rotateAngleZ;
   public float rotationPointX;
   private ModelUpdater modelUpdater;
   public float textureWidth = 64.0F;
   private int textureOffsetX;
   public float textureHeight = 32.0F;
   public List<ModelRenderer> childModels;
   private int textureOffsetY;
   public boolean showModel = true;
   public float offsetZ;
   public boolean isHidden;
   public List<ModelBox> cubeList = Lists.newArrayList();
   private ModelBase baseModel;
   public final String boxName;

   public void setId(String arg1) {
      this.id = arg1;
   }

   public boolean getCompiled() {
      return this.compiled;
   }

   public ModelRenderer(ModelBase model, String boxNameIn) {
      this.baseModel = model;
      model.boxList.add(this);
      this.boxName = boxNameIn;
      this.setTextureSize(model.textureWidth, model.textureHeight);
   }

   private void compileDisplayList(float scale) {
      if (this.displayList == 0) {
         this.displayList = GLAllocation.generateDisplayLists(1);
      }

      GL11.glNewList(this.displayList, 4864);
      WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();

      for (int i = 0; i < this.cubeList.size(); i++) {
         this.cubeList.get(i).render(worldRenderer, scale);
      }

      for (int i2 = 0; i2 < this.spriteList.size(); i2++) {
         ((ModelSprite)this.spriteList.get(i2)).render(Tessellator.getInstance(), scale);
      }

      GL11.glEndList();
      this.compiled = true;
   }

   public ModelRenderer getModelRenderer(float f, float f2, float f3) {
      this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, f, f2, f3, 2, 12, 12, 0.0F, true));
      return this;
   }

   public void setTextureLocation(ResourceLocation arg1) {
      this.textureLocation = arg1;
   }

   public void setModelUpdater(ModelUpdater arg1) {
      this.modelUpdater = arg1;
   }

   public ModelRenderer addBox(float offX, float offY, float offZ, int width, int height, int depth) {
      this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, offX, offY, offZ, width, height, depth, 0.0F));
      return this;
   }

   public void setRotationPoint(float rotationPointXIn, float rotationPointYIn, float rotationPointZIn) {
      this.rotationPointX = rotationPointXIn;
      this.rotationPointY = rotationPointYIn;
      this.rotationPointZ = rotationPointZIn;
   }

   public ResourceLocation getTextureLocation() {
      return this.textureLocation;
   }

   public void addBox(int[][] arg1, float arg2, float arg3, float arg4, float arg5, float arg6, float arg7, float arg8) {
      this.cubeList.add(new ModelBox(this, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, this.mirror));
   }

   public ModelRenderer getChild(String arg1) {
      if (arg1 == null) {
         return null;
      }

      if (this.childModels != null) {
         for (int index = 0; index < this.childModels.size(); index++) {
            ModelRenderer modelRenderer = this.childModels.get(index);
            if (arg1.equals(modelRenderer.getId())) {
               return modelRenderer;
            }
         }
      }

      return null;
   }

   public ModelRenderer setTextureSize(int textureWidthIn, int textureHeightIn) {
      this.textureWidth = textureWidthIn;
      this.textureHeight = textureHeightIn;
      return this;
   }

   private void checkResetDisplayList() {
      if (this.countResetDisplayList != Shaders.countResetDisplayLists) {
         this.compiled = false;
         this.countResetDisplayList = Shaders.countResetDisplayLists;
      }
   }

   @Override
   public String toString() {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append(
         "id: "
            + this.id
            + ", boxes: "
            + (this.cubeList != null ? this.cubeList.size() : null)
            + ", submodels: "
            + (this.childModels != null ? this.childModels.size() : null)
      );
      return stringBuffer.toString();
   }

   public void addSprite(float arg1, float arg2, float arg3, int arg4, int arg5, int arg6, float arg7) {
      this.spriteList.add(new ModelSprite(this, this.textureOffsetX, this.textureOffsetY, arg1, arg2, arg3, arg4, arg5, arg6, arg7));
   }

   public void renderWithRotation(float f) {
      if (!this.isHidden && this.showModel) {
         this.checkResetDisplayList();
         if (!this.compiled) {
            this.compileDisplayList(f);
         }

         int i = 0;
         if (this.textureLocation != null && !this.renderGlobal.renderOverlayDamaged) {
            if (this.renderGlobal.renderOverlayEyes) {
               return;
            }

            i = GlStateManager.getBoundTexture();
            Config.getTextureManager().bindTexture(this.textureLocation);
         }

         if (this.modelUpdater != null) {
            this.modelUpdater.update();
         }

         boolean flag = this.scaleX != 1.0F || this.scaleY != 1.0F || this.scaleZ != 1.0F;
         GlStateManager.pushMatrix();
         GlStateManager.translate(this.rotationPointX * f, this.rotationPointY * f, this.rotationPointZ * f);
         if (this.rotateAngleY != 0.0F) {
            GlStateManager.rotate(this.rotateAngleY * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
         }

         if (this.rotateAngleX != 0.0F) {
            GlStateManager.rotate(this.rotateAngleX * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
         }

         if (this.rotateAngleZ != 0.0F) {
            GlStateManager.rotate(this.rotateAngleZ * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
         }

         if (flag) {
            GlStateManager.scale(this.scaleX, this.scaleY, this.scaleZ);
         }

         GlStateManager.callList(this.displayList);
         if (this.childModels != null) {
            for (int innerIndex = 0; innerIndex < this.childModels.size(); innerIndex++) {
               this.childModels.get(innerIndex).render(f);
            }
         }

         GlStateManager.popMatrix();
         if (i != 0) {
            GlStateManager.bindTexture(i);
         }
      }
   }

   public int getDisplayList() {
      return this.displayList;
   }

   public ModelRenderer(ModelBase model) {
      this(model, (String)null);
   }

   public void addChild(ModelRenderer renderer) {
      if (this.childModels == null) {
         this.childModels = Lists.newArrayList();
      }

      this.childModels.add(renderer);
   }

   public void render(float f) {
      if (!this.isHidden && this.showModel) {
         this.checkResetDisplayList();
         if (!this.compiled) {
            this.compileDisplayList(f);
         }

         int i = 0;
         if (this.textureLocation != null && !this.renderGlobal.renderOverlayDamaged) {
            if (this.renderGlobal.renderOverlayEyes) {
               return;
            }

            i = GlStateManager.getBoundTexture();
            Config.getTextureManager().bindTexture(this.textureLocation);
         }

         if (this.modelUpdater != null) {
            this.modelUpdater.update();
         }

         boolean flag = this.scaleX != 1.0F || this.scaleY != 1.0F || this.scaleZ != 1.0F;
         GlStateManager.translate(this.offsetX, this.offsetY, this.offsetZ);
         if (this.rotateAngleX != 0.0F || this.rotateAngleY != 0.0F || this.rotateAngleZ != 0.0F) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(this.rotationPointX * f, this.rotationPointY * f, this.rotationPointZ * f);
            if (this.rotateAngleZ != 0.0F) {
               GlStateManager.rotate(this.rotateAngleZ * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
            }

            if (this.rotateAngleY != 0.0F) {
               GlStateManager.rotate(this.rotateAngleY * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
            }

            if (this.rotateAngleX != 0.0F) {
               GlStateManager.rotate(this.rotateAngleX * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
            }

            if (flag) {
               GlStateManager.scale(this.scaleX, this.scaleY, this.scaleZ);
            }

            GlStateManager.callList(this.displayList);
            if (this.childModels != null) {
               for (int innerIndex = 0; innerIndex < this.childModels.size(); innerIndex++) {
                  this.childModels.get(innerIndex).render(f);
               }
            }

            GlStateManager.popMatrix();
         } else if (this.rotationPointX == 0.0F && this.rotationPointY == 0.0F && this.rotationPointZ == 0.0F) {
            if (flag) {
               GlStateManager.scale(this.scaleX, this.scaleY, this.scaleZ);
            }

            GlStateManager.callList(this.displayList);
            if (this.childModels != null) {
               for (int outerIndex = 0; outerIndex < this.childModels.size(); outerIndex++) {
                  this.childModels.get(outerIndex).render(f);
               }
            }

            if (flag) {
               GlStateManager.scale(1.0F / this.scaleX, 1.0F / this.scaleY, 1.0F / this.scaleZ);
            }
         } else {
            GlStateManager.translate(this.rotationPointX * f, this.rotationPointY * f, this.rotationPointZ * f);
            if (flag) {
               GlStateManager.scale(this.scaleX, this.scaleY, this.scaleZ);
            }

            GlStateManager.callList(this.displayList);
            if (this.childModels != null) {
               for (int nestedIndex = 0; nestedIndex < this.childModels.size(); nestedIndex++) {
                  this.childModels.get(nestedIndex).render(f);
               }
            }

            if (flag) {
               GlStateManager.scale(1.0F / this.scaleX, 1.0F / this.scaleY, 1.0F / this.scaleZ);
            }

            GlStateManager.translate(-this.rotationPointX * f, -this.rotationPointY * f, -this.rotationPointZ * f);
         }

         GlStateManager.translate(-this.offsetX, -this.offsetY, -this.offsetZ);
         if (i != 0) {
            GlStateManager.bindTexture(i);
         }
      }
   }

   public void postRender(float scale) {
      if (!this.isHidden && this.showModel) {
         this.checkResetDisplayList();
         if (!this.compiled) {
            this.compileDisplayList(scale);
         }

         if (this.rotateAngleX != 0.0F || this.rotateAngleY != 0.0F || this.rotateAngleZ != 0.0F) {
            GlStateManager.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
            if (this.rotateAngleZ != 0.0F) {
               GlStateManager.rotate(this.rotateAngleZ * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
            }

            if (this.rotateAngleY != 0.0F) {
               GlStateManager.rotate(this.rotateAngleY * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
            }

            if (this.rotateAngleX != 0.0F) {
               GlStateManager.rotate(this.rotateAngleX * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
            }
         } else if (this.rotationPointX != 0.0F || this.rotationPointY != 0.0F || this.rotationPointZ != 0.0F) {
            GlStateManager.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
         }
      }
   }

   public ModelRenderer setTextureOffset(int x, int y) {
      this.textureOffsetX = x;
      this.textureOffsetY = y;
      return this;
   }

   public String getId() {
      return this.id;
   }

   public ModelRenderer(ModelBase model, int texOffX, int texOffY) {
      this(model);
      this.setTextureOffset(texOffX, texOffY);
   }

   public ModelRenderer addBox(String partName, float offX, float offY, float offZ, int width, int height, int depth) {
      partName = this.boxName + "." + partName;
      TextureOffset textureOffset = this.baseModel.getTextureOffset(partName);
      this.setTextureOffset(textureOffset.textureOffsetX, textureOffset.textureOffsetY);
      this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, offX, offY, offZ, width, height, depth, 0.0F).setBoxName(partName));
      return this;
   }

   public ModelRenderer getChildDeep(String arg1) {
      if (arg1 == null) {
         return null;
      }

      ModelRenderer child = this.getChild(arg1);
      if (child != null) {
         return child;
      }

      if (this.childModels != null) {
         for (int i = 0; i < this.childModels.size(); i++) {
            ModelRenderer childDeep = this.childModels.get(i).getChildDeep(arg1);
            if (childDeep != null) {
               return childDeep;
            }
         }
      }

      return null;
   }

   public void addBox(float f, float f2, float f3, int width, int height, int depth, float scaleFactor) {
      this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, f, f2, f3, width, height, depth, scaleFactor));
   }
}
