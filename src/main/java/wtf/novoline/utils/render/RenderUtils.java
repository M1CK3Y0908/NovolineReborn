package wtf.novoline.utils.render;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import wtf.novoline.utils.math.SmoothingUtils;

public class RenderUtils {
   private static final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(4);
   private static final Minecraft minecraft = Minecraft.getMinecraft();
   private static final ResourceLocation SHADOW_TEXTURE = new ResourceLocation("novoline", "images/shadow.png");

   public static float[] worldToScreen(Vec3 vec3) {
      return worldToScreen((float)vec3.xCoord, (float)vec3.yCoord, (float)vec3.zCoord);
   }

   public static int pulseBrightness(int i, float f, float f2, int i2, int i3, long l) {
      float f3 = i >> 24 & 0xFF;
      float f4 = i >> 16 & 0xFF;
      float f5 = i >> 8 & 0xFF;
      float f6 = i & 0xFF;
      double d = (double)i2 / i3 + (double)(System.currentTimeMillis() % l) / l;
      float f7 = (float)(f + (f2 - f) * ((Math.cos(d * 2.0 * Math.PI) + 1.0) / 2.0));
      return packColor(new float[]{f4 * f7, f5 * f7, f6 * f7, f3});
   }

   public static void drawLine(float f, float f2, float f3, float f4, int i) {
      drawLine(f, f2, f3, f4, 1.0F, i, true);
   }

   public static void drawArc(float f, float f2, float f3, float f4, int i, int i2) {
      drawArc(f, f2, f3, f4, i, i2, false);
   }

   public static void drawLine(float f, float f2, float f3, float f4, float f5, int i, boolean flag) {
      float f6 = (i >> 24 & 0xFF) / 255.0F;
      float f7 = (i >> 16 & 0xFF) / 255.0F;
      float f8 = (i >> 8 & 0xFF) / 255.0F;
      float f9 = (i & 0xFF) / 255.0F;
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      GlStateManager.enableBlend();
      GlStateManager.disableTexture2D();
      applyBlendFunc();
      GlStateManager.color(f7, f8, f9, f6);
      GL11.glLineWidth(f5);
      if (flag) {
         GL11.glEnable(2848);
      }

      worldRenderer.begin(1, DefaultVertexFormats.POSITION);
      worldRenderer.pos(f, f2, 0.0).endVertex();
      worldRenderer.pos(f3, f4, 0.0).endVertex();
      instance.draw();
      if (flag) {
         GL11.glDisable(2848);
      }

      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }

   public static void enableScissorDoubleScale(int i, int i2, int i3, int i4) {
      GL11.glEnable(3089);
      i2 += i4;
      GL11.glScissor(i * 2, minecraft.displayHeight - i2 * 2, i3 * 2, i4 * 2);
   }

   public static void drawScaledTexturedRect(
      float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float f10
   ) {
      float f11 = 1.0F / f9;
      float f12 = 1.0F / f10;
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(f, f2 + f8, 0.0).tex(f3 * f11, (f4 + f6) * f12).endVertex();
      worldRenderer.pos(f + f7, f2 + f8, 0.0).tex((f3 + f5) * f11, (f4 + f6) * f12).endVertex();
      worldRenderer.pos(f + f7, f2, 0.0).tex((f3 + f5) * f11, f4 * f12).endVertex();
      worldRenderer.pos(f, f2, 0.0).tex(f3 * f11, f4 * f12).endVertex();
      instance.draw();
   }

   public static void drawBoxOutlineAtRenderPos(AxisAlignedBB axisAlignedBB, int i, float f) {
      float[] fArray = unpackColorNormalized(i);
      GlStateManager.enableBlend();
      applyBlendFunc();
      GL11.glLineWidth(1.0F);
      GlStateManager.disableTexture2D();
      GlStateManager.depthMask(false);
      GlStateManager.disableDepth();
      GL11.glEnable(2848);
      double d = 0.002;
      drawBoxOutline(
         axisAlignedBB.expand(d, d, d)
            .offset(-TileEntityRendererDispatcher.staticPlayerX, -TileEntityRendererDispatcher.staticPlayerY, -TileEntityRendererDispatcher.staticPlayerZ),
         fArray[0],
         fArray[1],
         fArray[2],
         fArray[3]
      );
      GL11.glDisable(2848);
      GlStateManager.enableDepth();
      GlStateManager.depthMask(true);
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }

   public static void drawVerticalGradientRect(float f, float f2, float f3, float f4, int i, int i2) {
      float f5 = (i >> 24 & 0xFF) / 255.0F;
      float f6 = (i >> 16 & 0xFF) / 255.0F;
      float f7 = (i >> 8 & 0xFF) / 255.0F;
      float f8 = (i & 0xFF) / 255.0F;
      float f9 = (i2 >> 24 & 0xFF) / 255.0F;
      float f10 = (i2 >> 16 & 0xFF) / 255.0F;
      float f11 = (i2 >> 8 & 0xFF) / 255.0F;
      float f12 = (i2 & 0xFF) / 255.0F;
      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      applyBlendFunc();
      GlStateManager.shadeModel(7425);
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      worldRenderer.pos(f3, f2, 0.0).color(f6, f7, f8, f5).endVertex();
      worldRenderer.pos(f, f2, 0.0).color(f6, f7, f8, f5).endVertex();
      worldRenderer.pos(f, f4, 0.0).color(f10, f11, f12, f9).endVertex();
      worldRenderer.pos(f3, f4, 0.0).color(f10, f11, f12, f9).endVertex();
      instance.draw();
      GlStateManager.shadeModel(7424);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableTexture2D();
   }

   public static float[] unpackColorNormalized(int i) {
      float f = i >> 24 & 0xFF;
      float f2 = i >> 16 & 0xFF;
      float f3 = i >> 8 & 0xFF;
      float f4 = i & 0xFF;
      return new float[]{f2 / 255.0F, f3 / 255.0F, f4 / 255.0F, f / 255.0F};
   }

   public static void applyEntityLighting(EntityPlayer entityPlayer) {
      int brightnessForRender = entityPlayer.getBrightnessForRender(1.0F);
      int i = brightnessForRender % 65536;
      int i2 = brightnessForRender / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, i, i2);
   }

   public static int timedHsbColorMirrored(double d, float f, float f2, long l) {
      if (l != -1L) {
         d += (double)(System.currentTimeMillis() % l) / l;
      }

      while (d < 0.0) {
         d++;
      }

      while (d > 1.0) {
         d--;
      }

      if (d < 0.5) {
         d = 1.0 - d;
      }

      return Color.getHSBColor((float)d, f, f2).getRGB();
   }

   public static void drawGradientLine(float f, float f2, float f3, float f4, float f5, int i, int i2, boolean flag) {
      float f6 = (i >> 24 & 0xFF) / 255.0F;
      float f7 = (i >> 16 & 0xFF) / 255.0F;
      float f8 = (i >> 8 & 0xFF) / 255.0F;
      float f9 = (i & 0xFF) / 255.0F;
      float f10 = (i2 >> 24 & 0xFF) / 255.0F;
      float f11 = (i2 >> 16 & 0xFF) / 255.0F;
      float f12 = (i2 >> 8 & 0xFF) / 255.0F;
      float f13 = (i2 & 0xFF) / 255.0F;
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      GlStateManager.enableBlend();
      GlStateManager.disableTexture2D();
      applyBlendFunc();
      GlStateManager.color(f7, f8, f9, f6);
      GL11.glLineWidth(f5);
      if (flag) {
         GL11.glEnable(2848);
      }

      GlStateManager.shadeModel(7425);
      worldRenderer.begin(1, DefaultVertexFormats.POSITION_COLOR);
      worldRenderer.pos(f, f2, 0.0).color(f7, f8, f9, f6).endVertex();
      worldRenderer.pos(f3, f4, 0.0).color(f11, f12, f13, f10).endVertex();
      instance.draw();
      GlStateManager.shadeModel(7424);
      if (flag) {
         GL11.glDisable(2848);
      }

      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }

   public static void drawBoxFilled(AxisAlignedBB axisAlignedBB, float f, float f2, float f3, float f4) {
      drawFilledBox(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ, f, f2, f3, f4);
   }

   public static void drawFlippedTexturedQuad(ScaledResolution scaledResolution, double d, double d2, double d3, double d4) {
      double d5 = scaledResolution.getScaledWidth_double();
      double d6 = scaledResolution.getScaledHeight_double();
      double d7 = d6 - d2 - d4;
      GL11.glBegin(7);
      GL11.glTexCoord2d(d / d5, (d7 + d4) / d6);
      GL11.glVertex2d(d, d2);
      GL11.glTexCoord2d(d / d5, d7 / d6);
      GL11.glVertex2d(d, d2 + d4);
      GL11.glTexCoord2d((d + d3) / d5, d7 / d6);
      GL11.glVertex2d(d + d3, d2 + d4);
      GL11.glTexCoord2d((d + d3) / d5, (d7 + d4) / d6);
      GL11.glVertex2d(d + d3, d2);
      GL11.glEnd();
   }

   public static int timedHsbColor(double d, float f, float f2, long l) {
      if (l != -1L) {
         d += (double)(System.currentTimeMillis() % l) / l;
      }

      return Color.getHSBColor((float)d, f, f2).getRGB();
   }

   public static void drawColorWheel(float f, float f2, float f3) {
      GlStateManager.enableBlend();
      GlStateManager.disableTexture2D();
      applyBlendFunc();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.shadeModel(7425);
      double d = 90.0 / 10;
      GL11.glBegin(5);

      for (int i = 0; i <= 40; i++) {
         double d2 = i * d * Math.PI / 180.0;
         Color hsbColor = Color.getHSBColor((float)(i * d / 360.0), 1.0F, 1.0F);
         GlStateManager.color(hsbColor.getRed() / 255.0F, hsbColor.getGreen() / 255.0F, hsbColor.getBlue() / 255.0F, 1.0F);
         GL11.glVertex2d(f + Math.sin(d2) * f3, f2 + Math.cos(d2) * f3);
         GlStateManager.color(hsbColor.getRed() / 255.0F, hsbColor.getGreen() / 255.0F, hsbColor.getBlue() / 255.0F, 0.0F);
         GL11.glVertex2d(f + Math.sin(d2) * (f3 + 4.0F), f2 + Math.cos(d2) * (f3 + 4.0F));
      }

      GL11.glEnd();
      GL11.glBegin(6);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glVertex2d(f, f2);

      for (int i2 = 0; i2 <= 40; i2++) {
         double d3 = i2 * d * Math.PI / 180.0;
         Color hsbColor2 = Color.getHSBColor((float)(i2 * d / 360.0), 1.0F, 1.0F);
         GlStateManager.color(hsbColor2.getRed() / 255.0F, hsbColor2.getGreen() / 255.0F, hsbColor2.getBlue() / 255.0F, 1.0F);
         GL11.glVertex2d(f + Math.sin(d3) * f3, f2 + Math.cos(d3) * f3);
      }

      GL11.glEnd();
      GlStateManager.shadeModel(7424);
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }

   public static void drawTexturedRectSquareTexture(float f, float f2, float f3, float f4, float f5, float f6, int i) {
      double d = 1.0 / i;
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(f + 0.0F, f2 + f6, 0.0).tex((f3 + 0.0F) * d, (f4 + f6) * d).endVertex();
      worldRenderer.pos(f + f5, f2 + f6, 0.0).tex((f3 + f5) * d, (f4 + f6) * d).endVertex();
      worldRenderer.pos(f + f5, f2 + 0.0F, 0.0).tex((f3 + f5) * d, (f4 + 0.0F) * d).endVertex();
      worldRenderer.pos(f + 0.0F, f2 + 0.0F, 0.0).tex((f3 + 0.0F) * d, (f4 + 0.0F) * d).endVertex();
      instance.draw();
   }

   public static void drawFilledBox(
      double d, double d2, double d3, double d4, double d5, double d6, float f, float f2, float f3, float f4
   ) {
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      worldRenderer.begin(5, DefaultVertexFormats.POSITION_COLOR);
      putFilledBoxVertices(worldRenderer, d, d2, d3, d4, d5, d6, f, f2, f3, f4);
      instance.draw();
   }

   public static void putFilledBoxVertices(
      WorldRenderer worldRenderer, double d, double d2, double d3, double d4, double d5, double d6, float f, float f2, float f3, float f4
   ) {
      worldRenderer.pos(d, d2, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d2, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d2, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d2, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d5, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d5, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d5, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d2, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d5, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d2, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d2, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d2, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d5, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d5, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d5, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d2, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d5, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d2, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d2, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d2, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d2, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d2, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d2, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d5, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d5, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d5, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d5, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d5, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d5, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d5, d6).color(f, f2, f3, f4).endVertex();
   }

   public static void applyBlendFunc() {
      if (OpenGlHelper.shadersSupported) {
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 1);
      } else {
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      }
   }

   public static void drawTexturedRect256(float f, float f2, float f3, float f4, float f5, float f6) {
      drawTexturedRectSquareTexture(f, f2, f3, f4, f5, f6, 256);
   }

   public static void putOutlinedBoxVertices(
      WorldRenderer worldRenderer, double d, double d2, double d3, double d4, double d5, double d6, float f, float f2, float f3, float f4
   ) {
      worldRenderer.pos(d, d2, d3).color(f, f2, f3, 0.0F).endVertex();
      worldRenderer.pos(d, d2, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d2, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d2, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d2, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d2, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d5, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d5, d3).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d5, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d5, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d5, d3).color(f, f2, f3, 0.0F).endVertex();
      worldRenderer.pos(d, d5, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d, d2, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d2, d6).color(f, f2, f3, 0.0F).endVertex();
      worldRenderer.pos(d4, d5, d6).color(f, f2, f3, f4).endVertex();
      worldRenderer.pos(d4, d5, d3).color(f, f2, f3, 0.0F).endVertex();
      worldRenderer.pos(d4, d2, d3).color(f, f2, f3, f4).endVertex();
   }

   public static void drawTexturedRect(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8) {
      float f9 = 1.0F / f7;
      float f10 = 1.0F / f8;
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(f, f2 + f6, 0.0).tex(f3 * f9, (f4 + f6) * f10).endVertex();
      worldRenderer.pos(f + f5, f2 + f6, 0.0).tex((f3 + f5) * f9, (f4 + f6) * f10).endVertex();
      worldRenderer.pos(f + f5, f2, 0.0).tex((f3 + f5) * f9, f4 * f10).endVertex();
      worldRenderer.pos(f, f2, 0.0).tex(f3 * f9, f4 * f10).endVertex();
      instance.draw();
   }

   public static void drawColoredTexture(float f, float f2, float f3, float f4, int i, ResourceLocation resourceLocation) {
      GL11.glPushMatrix();
      applyBlendFunc();
      GlStateManager.enableBlend();
      GlStateManager.enableAlpha();
      setColor(i);
      minecraft.getTextureManager().bindTexture(resourceLocation);
      drawTexturedRect(f, f2, 0.0F, 0.0F, f3, f4, f3, f4);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GL11.glPopMatrix();
   }

   public static int smoothBlendColors(int i, int i2, float f) {
      float[] fArray = unpackColor255(i);
      float[] fArray2 = unpackColor255(i2);
      fArray[0] = (int)SmoothingUtils.approachWithMinimumStep(fArray[0], fArray2[0], f, 1.5F);
      fArray[1] = (int)SmoothingUtils.approachWithMinimumStep(fArray[1], fArray2[1], f, 1.5F);
      fArray[2] = (int)SmoothingUtils.approachWithMinimumStep(fArray[2], fArray2[2], f, 1.5F);
      fArray[3] = (int)SmoothingUtils.approachWithMinimumStep(fArray[3], fArray2[3], f, 1.5F);
      return packColor(fArray);
   }

   public static void drawBoxFilledAndOutlined(AxisAlignedBB axisAlignedBB, int i, float f) {
      float[] fArray = unpackColorNormalized(i);
      GlStateManager.enableBlend();
      applyBlendFunc();
      GL11.glLineWidth(1.0F);
      GlStateManager.disableTexture2D();
      GlStateManager.depthMask(false);
      GlStateManager.disableDepth();
      GL11.glEnable(2848);
      double d = 0.002;
      AxisAlignedBB axisAlignedBB2 = axisAlignedBB.expand(d, d, d)
         .offset(-TileEntityRendererDispatcher.staticPlayerX, -TileEntityRendererDispatcher.staticPlayerY, -TileEntityRendererDispatcher.staticPlayerZ);
      drawBoxFilled(axisAlignedBB2, fArray[0], fArray[1], fArray[2], 0.3F);
      drawBoxOutline(axisAlignedBB2, fArray[0], fArray[1], fArray[2], fArray[3]);
      GL11.glDisable(2848);
      GlStateManager.enableDepth();
      GlStateManager.depthMask(true);
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }

   public static float[] unpackColor255(int i) {
      float f = i >> 24 & 0xFF;
      float f2 = i >> 16 & 0xFF;
      float f3 = i >> 8 & 0xFF;
      float f4 = i & 0xFF;
      return new float[]{f2, f3, f4, f};
   }

   public static void drawOutlinedBox(
      double d, double d2, double d3, double d4, double d5, double d6, float f, float f2, float f3, float f4
   ) {
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      worldRenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
      putOutlinedBoxVertices(worldRenderer, d, d2, d3, d4, d5, d6, f, f2, f3, f4);
      instance.draw();
   }

   public static float[] worldToScreen(float f, float f2, float f3) {
      FloatBuffer floatBuffer2 = GLAllocation.createDirectFloatBuffer(3).clear();
      IntBuffer intBuffer = ActiveRenderInfo.VIEWPORT;
      FloatBuffer floatBuffer3 = ActiveRenderInfo.MODELVIEW;
      FloatBuffer floatBuffer4 = ActiveRenderInfo.PROJECTION;
      return GLU.gluProject(f, f2, f3, floatBuffer3, floatBuffer4, intBuffer, floatBuffer2) && floatBuffer2.get(2) >= 0.0F && floatBuffer2.get(2) <= 1.0F
         ? new float[]{floatBuffer2.get(0) / 2.0F, Display.getHeight() / 2.0F - floatBuffer2.get(1) / 2.0F, floatBuffer2.get(2)}
         : null;
   }

   public static void setTextureEnvColor(int i) {
      floatBuffer.put(0, (i >> 16 & 0xFF) / 255.0F);
      floatBuffer.put(1, (i >> 8 & 0xFF) / 255.0F);
      floatBuffer.put(2, (i & 0xFF) / 255.0F);
      floatBuffer.put(3, (i >> 24 & 0xFF) / 255.0F);
      GL11.glTexEnv(8960, 8705, floatBuffer);
      GL11.glTexEnvi(8960, 8704, 34160);
      GL11.glTexEnvi(8960, 34161, 7681);
      GL11.glTexEnvi(8960, 34176, 34166);
      GL11.glTexEnvi(8960, 34192, 768);
      GL11.glTexEnvi(8960, 34162, 7681);
      GL11.glTexEnvi(8960, 34184, 5890);
      GL11.glTexEnvi(8960, 34200, 770);
   }

   public static void drawArc(float f, float f2, float f3, float f4, int i, int i2, boolean flag) {
      float f5 = (i >> 16 & 0xFF) / 255.0F;
      float f6 = (i >> 8 & 0xFF) / 255.0F;
      float f7 = (i & 0xFF) / 255.0F;
      float f8 = (i >> 24 & 0xFF) / 255.0F;
      GlStateManager.enableBlend();
      GlStateManager.disableTexture2D();
      applyBlendFunc();
      GL11.glEnable(2848);
      GlStateManager.color(f5, f6, f7, f8);
      GL11.glColor4f(f5, f6, f7, f8);
      GL11.glLineWidth(f4);
      GlStateManager.shadeModel(7425);
      GL11.glBegin(3);

      for (int i3 = 0; i3 <= i2; i3++) {
         if (flag) {
            float[] fArray = unpackColorNormalized(indexedHsbColorMirrored(i3, 360, 0.6F, 1.0F, 2000L));
            GlStateManager.color(fArray[0], fArray[1], fArray[2], 1.0F);
         }

         GL11.glVertex2d(f - Math.sin(i3 * Math.PI / 180.0) * f3, f2 - Math.cos(i3 * Math.PI / 180.0) * f3);
      }

      GL11.glEnd();
      GlStateManager.shadeModel(7424);
      GL11.glDisable(2848);
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }

   public static void setColor(int i) {
      float f = i >> 24 & 0xFF;
      float f2 = i >> 16 & 0xFF;
      float f3 = i >> 8 & 0xFF;
      float f4 = i & 0xFF;
      GlStateManager.color(f2 / 255.0F, f3 / 255.0F, f4 / 255.0F, f / 255.0F);
   }

   public static int withAlpha(int i, int i2) {
      return i & 16777215 | (i2 & 0xFF) << 24;
   }

   public static void drawLine(float f, float f2, float f3, float f4, int i, boolean flag) {
      drawLine(f, f2, f3, f4, 1.0F, i, flag);
   }

   public static void drawTintedTexture(float f, float f2, float f3, float f4, int i, ResourceLocation resourceLocation) {
      GL11.glPushMatrix();
      applyBlendFunc();
      GlStateManager.enableBlend();
      GlStateManager.enableAlpha();
      setColor(i);
      minecraft.getTextureManager().bindTexture(resourceLocation);
      drawTexturedRect(f, f2, 0.0F, 0.0F, f3, f4, f3, f4);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GL11.glPopMatrix();
   }

   public static void drawHorizontalGradientRect(float f, float f2, float f3, float f4, int i, int i2) {
      float f5 = (i >> 24 & 0xFF) / 255.0F;
      float f6 = (i >> 16 & 0xFF) / 255.0F;
      float f7 = (i >> 8 & 0xFF) / 255.0F;
      float f8 = (i & 0xFF) / 255.0F;
      float f9 = (i2 >> 24 & 0xFF) / 255.0F;
      float f10 = (i2 >> 16 & 0xFF) / 255.0F;
      float f11 = (i2 >> 8 & 0xFF) / 255.0F;
      float f12 = (i2 & 0xFF) / 255.0F;
      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      applyBlendFunc();
      GlStateManager.shadeModel(7425);
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      worldRenderer.pos(f3, f2, 0.0).color(f10, f11, f12, f9).endVertex();
      worldRenderer.pos(f, f2, 0.0).color(f6, f7, f8, f5).endVertex();
      worldRenderer.pos(f, f4, 0.0).color(f6, f7, f8, f5).endVertex();
      worldRenderer.pos(f3, f4, 0.0).color(f10, f11, f12, f9).endVertex();
      instance.draw();
      GlStateManager.shadeModel(7424);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableTexture2D();
   }

   public static void enableScissor(int i, int i2, int i3, int i4) {
      ScaledResolution scaledResolution = new ScaledResolution(minecraft);
      GL11.glEnable(3089);
      i2 += i4;
      GL11.glScissor(
         i * scaledResolution.getScaleFactor(), minecraft.displayHeight - i2 * scaledResolution.getScaleFactor(), i3 * scaledResolution.getScaleFactor(), i4 * scaledResolution.getScaleFactor()
      );
   }

   public static void setTextureEnvModulate() {
      GL11.glTexEnvi(8960, 8704, 8448);
      GL11.glTexEnvi(8960, 34161, 8448);
      GL11.glTexEnvi(8960, 34162, 8448);
      GL11.glTexEnvi(8960, 34176, 5890);
      GL11.glTexEnvi(8960, 34184, 5890);
      GL11.glTexEnvi(8960, 34192, 768);
      GL11.glTexEnvi(8960, 34200, 770);
   }

   public static Framebuffer ensureFramebuffer(Framebuffer framebuffer) {
      if (framebuffer != null && framebuffer.framebufferWidth == minecraft.displayWidth && framebuffer.framebufferHeight == minecraft.displayHeight) {
         return framebuffer;
      }

      if (framebuffer != null) {
         framebuffer.deleteFramebuffer();
      }

      return new Framebuffer(minecraft.displayWidth, minecraft.displayHeight, true);
   }

   public static void drawShadow(float f, float f2, float f3, float f4, float f5, int i) {
      drawShadow(f, f2, f3, f4, f5, i, true);
   }

   public static int lerpColor(int i, int i2, float f) {
      if (f <= 0.0F) {
         return i;
      }

      if (f >= 1.0F) {
         return i2;
      }

      int i3 = i >> 16 & 0xFF;
      int i4 = i >> 8 & 0xFF;
      int i5 = i & 0xFF;
      int i6 = i >> 24;
      int i7 = i2 >> 16 & 0xFF;
      int i8 = i2 >> 8 & 0xFF;
      int i9 = i2 & 0xFF;
      int i10 = i2 >> 24;
      float f2 = 1.0F - f;
      int i11 = (int)(f2 * i3 + f * i7);
      int i12 = (int)(f2 * i4 + f * i8);
      int i13 = (int)(f2 * i5 + f * i9);
      return ((int)(f2 * i6 + f * i10) << 24) + (i11 << 16) + (i12 << 8) + i13;
   }

   public static int packColor(float[] fArray) {
      return ((int)fArray[3] & 0xFF) << 24 | ((int)fArray[0] & 0xFF) << 16 | ((int)fArray[1] & 0xFF) << 8 | (int)fArray[2] & 0xFF;
   }

   public static int colorToInt(Color color2) {
      return (color2.getAlpha() & 0xFF) << 24 | (color2.getRed() & 0xFF) << 16 | (color2.getGreen() & 0xFF) << 8 | color2.getBlue() & 0xFF;
   }

   public static void drawFramebuffer(Framebuffer framebuffer, ScaledResolution scaledResolution) {
      framebuffer.bindFramebufferTexture();
      drawFullscreenQuad(scaledResolution);
   }

   public static void drawRect(float f, float f2, float f3, float f4, int i) {
      float f5 = (i >> 24 & 0xFF) / 255.0F;
      float f6 = (i >> 16 & 0xFF) / 255.0F;
      float f7 = (i >> 8 & 0xFF) / 255.0F;
      float f8 = (i & 0xFF) / 255.0F;
      applyBlendFunc();
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      GlStateManager.enableBlend();
      GlStateManager.disableTexture2D();
      GlStateManager.color(f6, f7, f8, f5);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION);
      worldRenderer.pos(f, f4, 0.0).endVertex();
      worldRenderer.pos(f3, f4, 0.0).endVertex();
      worldRenderer.pos(f3, f2, 0.0).endVertex();
      worldRenderer.pos(f, f2, 0.0).endVertex();
      instance.draw();
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }

   public static void drawBorderedRect(float f, float f2, float f3, float f4, float f5, int i, int i2) {
      float f6 = 0.0F;
      if (f5 < 1.0F) {
         f6 = 1.0F;
      }

      drawRect(f + f5, f2 + f5, f3 - f5, f4 - f5, i);
      drawRect(f, f2 + 1.0F - f6, f + f5, f4, i2);
      drawRect(f, f2, f3 - 1.0F + f6, f2 + f5, i2);
      drawRect(f3 - f5, f2, f3, f4 - 1.0F + f6, i2);
      drawRect(f + 1.0F - f6, f4 - f5, f3, f4, i2);
   }

   public static void resetColor() {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static void drawShadow(float f, float f2, float f3, float f4, float f5, int i, boolean flag) {
      setColor(i);
      Minecraft.getMinecraft().getTextureManager().bindTexture(SHADOW_TEXTURE);
      GlStateManager.enableBlend();
      applyBlendFunc();
      GlStateManager.alphaFunc(516, 0.001F);
      GL11.glBegin(7);
      if (flag) {
         GL11.glTexCoord2d(0.5, 1.0);
         GL11.glVertex2d(f, f2);
         GL11.glTexCoord2d(1.0, 1.0);
         GL11.glVertex2d(f3, f2);
         GL11.glTexCoord2d(1.0, 0.0);
         GL11.glVertex2d(f3, f2 - f5);
         GL11.glTexCoord2d(0.5, 0.0);
         GL11.glVertex2d(f, f2 - f5);
      }

      GL11.glTexCoord2d(1.0, 1.0);
      GL11.glVertex2d(f3, f4);
      GL11.glTexCoord2d(0.5, 1.0);
      GL11.glVertex2d(f, f4);
      GL11.glTexCoord2d(0.5, 0.0);
      GL11.glVertex2d(f, f4 + f5);
      GL11.glTexCoord2d(1.0, 0.0);
      GL11.glVertex2d(f3, f4 + f5);
      GL11.glTexCoord2d(0.5, 1.0);
      GL11.glVertex2d(f3, f2);
      GL11.glTexCoord2d(1.0, 1.0);
      GL11.glVertex2d(f3, f4);
      GL11.glTexCoord2d(1.0, 0.0);
      GL11.glVertex2d(f3 + f5, f4);
      GL11.glTexCoord2d(0.5, 0.0);
      GL11.glVertex2d(f3 + f5, f2);
      GL11.glTexCoord2d(1.0, 1.0);
      GL11.glVertex2d(f, f4);
      GL11.glTexCoord2d(0.5, 1.0);
      GL11.glVertex2d(f, f2);
      GL11.glTexCoord2d(0.5, 0.0);
      GL11.glVertex2d(f - f5, f2);
      GL11.glTexCoord2d(1.0, 0.0);
      GL11.glVertex2d(f - f5, f4);
      if (flag) {
         GL11.glTexCoord2d(0.5, 1.0);
         GL11.glVertex2d(f, f2);
         GL11.glTexCoord2d(0.5, 0.0);
         GL11.glVertex2d(f, f2 - f5);
         GL11.glTexCoord2d(0.0, 0.0);
         GL11.glVertex2d(f - f5, f2 - f5);
         GL11.glTexCoord2d(0.0, 1.0);
         GL11.glVertex2d(f - f5, f2);
      }

      GL11.glTexCoord2d(0.5, 1.0);
      GL11.glVertex2d(f3, f4);
      GL11.glTexCoord2d(0.5, 0.0);
      GL11.glVertex2d(f3, f4 + f5);
      GL11.glTexCoord2d(0.0, 0.0);
      GL11.glVertex2d(f3 + f5, f4 + f5);
      GL11.glTexCoord2d(0.0, 1.0);
      GL11.glVertex2d(f3 + f5, f4);
      if (flag) {
         GL11.glTexCoord2d(0.5, 0.0);
         GL11.glVertex2d(f3, f2 - f5);
         GL11.glTexCoord2d(0.5, 1.0);
         GL11.glVertex2d(f3, f2);
         GL11.glTexCoord2d(0.0, 1.0);
         GL11.glVertex2d(f3 + f5, f2);
         GL11.glTexCoord2d(0.0, 0.0);
         GL11.glVertex2d(f3 + f5, f2 - f5);
      }

      GL11.glTexCoord2d(0.5, 0.0);
      GL11.glVertex2d(f, f4 + f5);
      GL11.glTexCoord2d(0.5, 1.0);
      GL11.glVertex2d(f, f4);
      GL11.glTexCoord2d(0.0, 1.0);
      GL11.glVertex2d(f - f5, f4);
      GL11.glTexCoord2d(0.0, 0.0);
      GL11.glVertex2d(f - f5, f4 + f5);
      GL11.glEnd();
      applyBlendFunc();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.disableBlend();
      resetColor();
   }

   public static int pulseBetweenColors(int i, int i2, int i3, int i4, long l) {
      double d = (double)i3 / i4 + (double)(System.currentTimeMillis() % l) / l;
      float f = (float)((Math.cos(d * 2.0 * Math.PI) + 1.0) / 2.0);
      return lerpColor(i, i2, f);
   }

   public static void drawSelectionHighlight(float f, float f2, float f3, float f4) {
      if (f < f3) {
         float f5 = f;
         f = f3;
         f3 = f5;
      }

      if (f2 < f4) {
         float f6 = f2;
         f2 = f4;
         f4 = f6;
      }

      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
      GlStateManager.disableTexture2D();
      GlStateManager.enableColorLogic();
      GlStateManager.colorLogicOp(5387);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION);
      worldRenderer.pos(f, f4, 0.0).endVertex();
      worldRenderer.pos(f3, f4, 0.0).endVertex();
      worldRenderer.pos(f3, f2, 0.0).endVertex();
      worldRenderer.pos(f, f2, 0.0).endVertex();
      instance.draw();
      GlStateManager.disableColorLogic();
      GlStateManager.enableTexture2D();
   }

   public static void drawFullscreenQuad(ScaledResolution scaledResolution) {
      drawFlippedTexturedQuad(scaledResolution, 0.0, 0.0, scaledResolution.getScaledWidth_double(), scaledResolution.getScaledHeight_double());
   }

   public static void disableScissor() {
      GL11.glDisable(3089);
   }

   public static void drawTextureWithAlpha(float f, float f2, float f3, float f4, float f5, ResourceLocation resourceLocation) {
      drawColoredTexture(f, f2, f3, f4, withAlphaFraction(-1, f5), resourceLocation);
   }

   public static int indexedHsbColorMirrored(int i, int i2, float f, float f2, long l) {
      double d = (double)i / i2 + (double)(System.currentTimeMillis() % l) / l;

      while (d < 0.0) {
         d++;
      }

      while (d > 1.0) {
         d--;
      }

      while (d < 0.5) {
         d = 1.0 - d;
      }

      return Color.getHSBColor((float)d, f, f2).getRGB();
   }

   public static void drawBoxOutline(AxisAlignedBB axisAlignedBB, float f, float f2, float f3, float f4) {
      drawOutlinedBox(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ, axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ, f, f2, f3, f4);
   }

   public static int indexedHsbColor(int i, int i2, float f, float f2, long l) {
      double d = (double)i / i2 + (double)(System.currentTimeMillis() % l) / l;
      return Color.getHSBColor((float)d, f, f2).getRGB();
   }

   public static int withAlphaFraction(int i, float f) {
      return withAlpha(i, (int)Math.min(255.0F, 255.0F * f));
   }

   public static void drawCircle(float f, float f2, float f3, float f4, int i) {
      float f5 = (i >> 24 & 0xFF) / 255.0F;
      float f6 = (i >> 16 & 0xFF) / 255.0F;
      float f7 = (i >> 8 & 0xFF) / 255.0F;
      float f8 = (i & 0xFF) / 255.0F;
      float f9 = (float)(6.2831852 / 16);
      float f10 = MathHelper.cos(f9);
      float f11 = MathHelper.sin(f9);
      float f12 = f3;
      float f13 = 0.0F;
      GlStateManager.enableBlend();
      GlStateManager.disableTexture2D();
      applyBlendFunc();
      GlStateManager.color(f6, f7, f8, f5);
      GlStateManager.disableCull();
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      worldRenderer.begin(6, DefaultVertexFormats.POSITION);

      for (int i2 = 0; i2 < 16; i2++) {
         worldRenderer.pos(f12 + f, f13 + f2, 0.0).endVertex();
         float f14 = f12;
         f12 = f10 * f12 - f11 * f13;
         f13 = f11 * f14 + f10 * f13;
      }

      instance.draw();
      GlStateManager.enableCull();
      GL11.glLineWidth(f4);
      GL11.glEnable(2848);
      worldRenderer.begin(2, DefaultVertexFormats.POSITION);

      for (int i3 = 0; i3 < 16; i3++) {
         worldRenderer.pos(f12 + f, f13 + f2, 0.0).endVertex();
         float f15 = f12;
         f12 = f10 * f12 - f11 * f13;
         f13 = f11 * f15 + f10 * f13;
      }

      instance.draw();
      GL11.glDisable(2848);
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }
}
