package net.minecraft.client;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MinecraftError;
import net.optifine.CustomLoadingScreen;
import net.optifine.CustomLoadingScreens;
import net.optifine.reflect.Reflector;
import wtf.novoline.utils.render.RenderUtils;

public class LoadingScreenRenderer implements IProgressUpdate {
   private String message = "";
   private String currentlyDisplayedText = "";
   private long systemTime = Minecraft.getSystemTime();
   private Minecraft mc;
   private ScaledResolution scaledResolution;
   private Framebuffer framebuffer;
   private boolean loadingSuccess;

   @Override
   public void displaySavingString(String message) {
      this.loadingSuccess = true;
      this.displayString(message);
   }

   @Override
   public void setDoneWorking() {
   }

   public LoadingScreenRenderer(Minecraft mcIn) {
      this.mc = mcIn;
      this.scaledResolution = new ScaledResolution(mcIn);
      this.framebuffer = new Framebuffer(mcIn.displayWidth, mcIn.displayHeight, false);
      this.framebuffer.setFramebufferFilter(9728);
   }

   @Override
   public void setLoadingProgress(int progress) {
      if (!this.mc.running) {
         if (!this.loadingSuccess) {
            throw new MinecraftError();
         }
      } else {
         long systemTime2 = Minecraft.getSystemTime();
         if (systemTime2 - this.systemTime >= 100L) {
            this.systemTime = systemTime2;
            ScaledResolution scaledResolution2 = new ScaledResolution(this.mc);
            int scaleFactor = scaledResolution2.getScaleFactor();
            int scaledWidth = scaledResolution2.getScaledWidth();
            int scaledHeight = scaledResolution2.getScaledHeight();
            if (OpenGlHelper.isFramebufferEnabled()) {
               this.framebuffer.framebufferClear();
            } else {
               GlStateManager.clear(256);
            }

            this.framebuffer.bindFramebuffer(false);
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0, scaledResolution2.getScaledWidth_double(), scaledResolution2.getScaledHeight_double(), 0.0, 100.0, 300.0);
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, 0.0F, -200.0F);
            if (!OpenGlHelper.isFramebufferEnabled()) {
               GlStateManager.clear(16640);
            }

            boolean flag = true;
            if (Reflector.reflectorMethod8.isActive()) {
               Object object = Reflector.createObject2(Reflector.reflectorMethod7);
               if (object != null) {
                  flag = !Reflector.checkObject(object, Reflector.reflectorMethod8, scaledResolution2);
               }
            }

            if (flag) {
               Tessellator instance = Tessellator.getInstance();
               WorldRenderer worldRenderer = instance.getWorldRenderer();
               CustomLoadingScreen customLoadingScreen = CustomLoadingScreens.getCustomLoadingScreen();
               if (customLoadingScreen != null) {
                  customLoadingScreen.drawBackground(scaledResolution2.getScaledWidth(), scaledResolution2.getScaledHeight());
               } else {
                  this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
                  float f = 32.0F;
                  worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                  worldRenderer.pos(0.0, scaledHeight, 0.0).tex(0.0, scaledHeight / f).color(64, 64, 64, 255).endVertex();
                  worldRenderer.pos(scaledWidth, scaledHeight, 0.0).tex(scaledWidth / f, scaledHeight / f).color(64, 64, 64, 255).endVertex();
                  worldRenderer.pos(scaledWidth, 0.0, 0.0).tex(scaledWidth / f, 0.0).color(64, 64, 64, 255).endVertex();
                  worldRenderer.pos(0.0, 0.0, 0.0).tex(0.0, 0.0).color(64, 64, 64, 255).endVertex();
                  instance.draw();
               }

               if (progress >= 0) {
                  int i = scaledWidth / 2 - 50;
                  int i2 = scaledHeight / 2 + 16;
                  GlStateManager.disableTexture2D();
                  worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
                  worldRenderer.pos(i, i2, 0.0).color(128, 128, 128, 255).endVertex();
                  worldRenderer.pos(i, i2 + 2, 0.0).color(128, 128, 128, 255).endVertex();
                  worldRenderer.pos(i + 100, i2 + 2, 0.0).color(128, 128, 128, 255).endVertex();
                  worldRenderer.pos(i + 100, i2, 0.0).color(128, 128, 128, 255).endVertex();
                  worldRenderer.pos(i, i2, 0.0).color(128, 255, 128, 255).endVertex();
                  worldRenderer.pos(i, i2 + 2, 0.0).color(128, 255, 128, 255).endVertex();
                  worldRenderer.pos(i + progress, i2 + 2, 0.0).color(128, 255, 128, 255).endVertex();
                  worldRenderer.pos(i + progress, i2, 0.0).color(128, 255, 128, 255).endVertex();
                  instance.draw();
                  GlStateManager.enableTexture2D();
               }

               GlStateManager.enableBlend();
               RenderUtils.applyBlendFunc();
               this.mc
                  .fontRendererObj
                  .drawStringWithShadow(
                     this.currentlyDisplayedText, (scaledWidth - this.mc.fontRendererObj.getInt3(this.currentlyDisplayedText)) / 2, scaledHeight / 2 - 4 - 16, 16777215
                  );
               this.mc
                  .fontRendererObj
                  .drawStringWithShadow(this.message, (scaledWidth - this.mc.fontRendererObj.getInt3(this.message)) / 2, scaledHeight / 2 - 4 + 8, 16777215);
            }

            this.framebuffer.unbindFramebuffer();
            if (OpenGlHelper.isFramebufferEnabled()) {
               this.framebuffer.framebufferRender(scaledWidth * scaleFactor, scaledHeight * scaleFactor);
            }

            this.mc.updateDisplay();

            try {
               Thread.yield();
            } catch (Exception exception) {
            }
         }
      }
   }

   @Override
   public void displayLoadingString(String message) {
      if (!this.mc.running) {
         if (!this.loadingSuccess) {
            throw new MinecraftError();
         }
      } else {
         this.systemTime = 0L;
         this.message = message;
         this.setLoadingProgress(-1);
         this.systemTime = 0L;
      }
   }

   @Override
   public void resetProgressAndMessage(String message) {
      this.loadingSuccess = false;
      this.displayString(message);
   }

   private void displayString(String message) {
      this.currentlyDisplayedText = message;
      if (!this.mc.running) {
         if (!this.loadingSuccess) {
            throw new MinecraftError();
         }
      } else {
         GlStateManager.clear(256);
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         if (OpenGlHelper.isFramebufferEnabled()) {
            int scaleFactor = this.scaledResolution.getScaleFactor();
            GlStateManager.ortho(0.0, this.scaledResolution.getScaledWidth() * scaleFactor, this.scaledResolution.getScaledHeight() * scaleFactor, 0.0, 100.0, 300.0);
         } else {
            ScaledResolution scaledResolution2 = new ScaledResolution(this.mc);
            GlStateManager.ortho(0.0, scaledResolution2.getScaledWidth_double(), scaledResolution2.getScaledHeight_double(), 0.0, 100.0, 300.0);
         }

         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
         GlStateManager.translate(0.0F, 0.0F, -200.0F);
      }
   }
}
