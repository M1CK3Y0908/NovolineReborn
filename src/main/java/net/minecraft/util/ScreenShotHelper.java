package net.minecraft.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.IntBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.event.ClickEvent;
import net.minecraft.src.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class ScreenShotHelper {
   private static int[] pixelValues;
   private static final Logger logger = LogManager.getLogger();
   private static IntBuffer pixelBuffer;
   private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

   private static File getTimestampedPNGFileForDirectory(File gameDirectory) {
      String text = dateFormat.format(new Date()).toString();
      int i = 1;

      while (true) {
         File file = new File(gameDirectory, text + (i == 1 ? "" : "_" + i) + ".png");
         if (!file.exists()) {
            return file;
         }

         i++;
      }
   }

   public static IChatComponent saveScreenshot(File gameDirectory, String screenshotName, int width, int height, Framebuffer buffer) {
      try {
         File file = new File(gameDirectory, "screenshots");
         file.mkdir();
         Minecraft minecraft2 = Minecraft.getMinecraft();
         int guiScale2 = Config.getGameSettings().guiScale;
         int scaleFactor = new ScaledResolution(minecraft2).getScaleFactor();
         int screenshotSize = Config.getScreenshotSize();
         boolean flag = OpenGlHelper.isFramebufferEnabled() && screenshotSize > 1;
         if (flag) {
            Config.getGameSettings().guiScale = scaleFactor * screenshotSize;
            resize(width * screenshotSize, height * screenshotSize);
            GlStateManager.pushMatrix();
            GlStateManager.clear(16640);
            minecraft2.getFramebuffer().bindFramebuffer(true);
            minecraft2.entityRenderer.updateCameraAndRender(Config.renderPartialTicks, System.nanoTime());
         }

         if (OpenGlHelper.isFramebufferEnabled()) {
            width = buffer.framebufferTextureWidth;
            height = buffer.framebufferTextureHeight;
         }

         int i = width * height;
         if (pixelBuffer == null || pixelBuffer.capacity() < i) {
            pixelBuffer = BufferUtils.createIntBuffer(i);
            pixelValues = new int[i];
         }

         GL11.glPixelStorei(3333, 1);
         GL11.glPixelStorei(3317, 1);
         pixelBuffer.clear();
         if (OpenGlHelper.isFramebufferEnabled()) {
            GlStateManager.bindTexture(buffer.framebufferTexture);
            GL11.glGetTexImage(3553, 0, 32993, 33639, pixelBuffer);
         } else {
            GL11.glReadPixels(0, 0, width, height, 32993, 33639, pixelBuffer);
         }

         pixelBuffer.get(pixelValues);
         TextureUtil.processPixelValues(pixelValues, width, height);
         BufferedImage bufferedImage;
         if (OpenGlHelper.isFramebufferEnabled()) {
            bufferedImage = new BufferedImage(buffer.framebufferWidth, buffer.framebufferHeight, 1);
            int i2 = buffer.framebufferTextureHeight - buffer.framebufferHeight;

            for (int i3 = i2; i3 < buffer.framebufferTextureHeight; i3++) {
               for (int i4 = 0; i4 < buffer.framebufferWidth; i4++) {
                  bufferedImage.setRGB(i4, i3 - i2, pixelValues[i3 * buffer.framebufferTextureWidth + i4]);
               }
            }
         } else {
            bufferedImage = new BufferedImage(width, height, 1);
            bufferedImage.setRGB(0, 0, width, height, pixelValues, 0, width);
         }

         if (flag) {
            minecraft2.getFramebuffer().unbindFramebuffer();
            GlStateManager.popMatrix();
            Config.getGameSettings().guiScale = guiScale2;
            resize(width, height);
         }

         File file2;
         if (screenshotName == null) {
            file2 = getTimestampedPNGFileForDirectory(file);
         } else {
            file2 = new File(file, screenshotName);
         }

         file2 = file2.getCanonicalFile();
         ImageIO.write(bufferedImage, "png", file2);
         ChatComponentText chatComponentText = new ChatComponentText(file2.getName());
         chatComponentText.getChatStyle().setChatClickEvent(new ClickEvent(net.minecraft.event.ClickEvent.Action.OPEN_FILE, file2.getAbsolutePath()));
         chatComponentText.getChatStyle().setUnderlined(true);
         return new ChatComponentTranslation("screenshot.success", chatComponentText);
      } catch (Exception exception) {
         logger.warn("Couldn't save screenshot", exception);
         return new ChatComponentTranslation("screenshot.failure", exception.getMessage());
      }
   }

   public static IChatComponent saveScreenshot(File gameDirectory, int width, int height, Framebuffer buffer) {
      return saveScreenshot(gameDirectory, (String)null, width, height, buffer);
   }

   private static void updateFramebufferSize() {
      Minecraft minecraft = Minecraft.getMinecraft();
      minecraft.getFramebuffer().createBindFramebuffer(minecraft.displayWidth, minecraft.displayHeight);
      if (minecraft.entityRenderer != null) {
         minecraft.entityRenderer.updateShaderGroupSize(minecraft.displayWidth, minecraft.displayHeight);
      }
   }

   private static void resize(int width, int height) {
      Minecraft minecraft = Minecraft.getMinecraft();
      minecraft.displayWidth = Math.max(1, width);
      minecraft.displayHeight = Math.max(1, height);
      if (minecraft.currentScreen != null) {
         ScaledResolution scaledResolution = new ScaledResolution(minecraft);
         minecraft.currentScreen.onResize(minecraft, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
      }

      updateFramebufferSize();
   }
}
