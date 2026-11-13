package net.minecraft.client.shader;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import org.lwjgl.util.vector.Matrix4f;

public class Shader {
   private final List<Object> listAuxFramebuffers = Lists.newArrayList();
   private final List<String> listAuxNames = Lists.newArrayList();
   private final List<Integer> listAuxWidths = Lists.newArrayList();
   private final List<Integer> listAuxHeights = Lists.newArrayList();
   private final ShaderManager manager;
   public final Framebuffer framebufferIn;
   public final Framebuffer framebufferOut;
   private Matrix4f projectionMatrix;

   public void deleteShader() {
      this.manager.deleteShader();
   }

   public void addAuxFramebuffer(String samplerName, Object auxiliaryFramebuffer, int width, int height) {
      this.listAuxNames.add(this.listAuxNames.size(), samplerName);
      this.listAuxFramebuffers.add(this.listAuxFramebuffers.size(), auxiliaryFramebuffer);
      this.listAuxWidths.add(this.listAuxWidths.size(), width);
      this.listAuxHeights.add(this.listAuxHeights.size(), height);
   }

   public void setProjectionMatrix(Matrix4f projectionMatrix) {
      this.projectionMatrix = projectionMatrix;
   }

   public ShaderManager getShaderManager() {
      return this.manager;
   }

   private void preLoadShader() {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableBlend();
      GlStateManager.disableDepth();
      GlStateManager.disableAlpha();
      GlStateManager.disableFog();
      GlStateManager.disableLighting();
      GlStateManager.disableColorMaterial();
      GlStateManager.enableTexture2D();
      GlStateManager.bindTexture(0);
   }

   public Shader(IResourceManager resourceManager, String programName, Framebuffer framebufferIn, Framebuffer framebufferOut) throws IOException {
      this.manager = new ShaderManager(resourceManager, programName);
      this.framebufferIn = framebufferIn;
      this.framebufferOut = framebufferOut;
   }

   public void loadShader(float partialTicks) {
      this.preLoadShader();
      this.framebufferIn.unbindFramebuffer();
      float framebufferTextureWidth2 = this.framebufferOut.framebufferTextureWidth;
      float framebufferTextureHeight2 = this.framebufferOut.framebufferTextureHeight;
      GlStateManager.viewport(0, 0, (int)framebufferTextureWidth2, (int)framebufferTextureHeight2);
      this.manager.addSamplerTexture("DiffuseSampler", this.framebufferIn);

      for (int i = 0; i < this.listAuxFramebuffers.size(); i++) {
         this.manager.addSamplerTexture(this.listAuxNames.get(i), this.listAuxFramebuffers.get(i));
         this.manager.getShaderUniformOrDefault("AuxSize" + i).set(this.listAuxWidths.get(i).intValue(), this.listAuxHeights.get(i).intValue());
      }

      this.manager.getShaderUniformOrDefault("ProjMat").set(this.projectionMatrix);
      this.manager.getShaderUniformOrDefault("InSize").set(this.framebufferIn.framebufferTextureWidth, this.framebufferIn.framebufferTextureHeight);
      this.manager.getShaderUniformOrDefault("OutSize").set(framebufferTextureWidth2, framebufferTextureHeight2);
      this.manager.getShaderUniformOrDefault("Time").set(partialTicks);
      Minecraft minecraft2 = Minecraft.getMinecraft();
      this.manager.getShaderUniformOrDefault("ScreenSize").set(minecraft2.displayWidth, minecraft2.displayHeight);
      this.manager.useShader();
      this.framebufferOut.framebufferClear();
      this.framebufferOut.bindFramebuffer(false);
      GlStateManager.depthMask(false);
      GlStateManager.colorMask(true, true, true, true);
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      worldRenderer.pos(0.0, framebufferTextureHeight2, 500.0).color(255, 255, 255, 255).endVertex();
      worldRenderer.pos(framebufferTextureWidth2, framebufferTextureHeight2, 500.0).color(255, 255, 255, 255).endVertex();
      worldRenderer.pos(framebufferTextureWidth2, 0.0, 500.0).color(255, 255, 255, 255).endVertex();
      worldRenderer.pos(0.0, 0.0, 500.0).color(255, 255, 255, 255).endVertex();
      instance.draw();
      GlStateManager.depthMask(true);
      GlStateManager.colorMask(true, true, true, true);
      this.manager.endShader();
      this.framebufferOut.unbindFramebuffer();
      this.framebufferIn.unbindFramebufferTexture();

      for (Object object : this.listAuxFramebuffers) {
         if (object instanceof Framebuffer) {
            ((Framebuffer)object).unbindFramebufferTexture();
         }
      }
   }
}
