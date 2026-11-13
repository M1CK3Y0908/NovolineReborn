package net.minecraft.client.gui;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.CustomColors;
import net.optifine.render.GlBlendState;
import net.optifine.util.FontUtils;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;

public class FontRenderer implements IResourceManagerReloadListener {
   private final int[] charWidth = new int[256];
   private float green;
   public int FONT_HEIGHT = 9;
   private float alpha;
   public Random fontRandom = new Random();
   private byte[] glyphWidth = new byte[65536];
   private int[] colorCode = new int[32];
   private boolean bidiFlag;
   public float offsetBold = 1.0F;
   private float[] charWidthFloat = new float[256];
   private boolean blend = false;
   private boolean boldStyle;
   private float posY;
   private boolean randomStyle;
   private boolean strikethroughStyle;
   private static final ResourceLocation[] unicodePageLocations = new ResourceLocation[256];
   private GlBlendState oldBlendState = new GlBlendState();
   private boolean underlineStyle;
   public GameSettings gameSettings;
   private float red;
   private float blue;
   private int textColor;
   public ResourceLocation locationFontTextureBase;
   private ResourceLocation locationFontTexture;
   private final TextureManager renderEngine;
   private float posX;
   private boolean italicStyle;
   private boolean unicodeFlag;

   private float renderChar(char ch, boolean italic) {
      if (ch != ' ' && ch != 160) {
         int i = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
            .indexOf(ch);
         return i != -1 && !this.unicodeFlag ? this.renderDefaultChar(i, italic) : this.renderUnicodeChar(ch, italic);
      } else {
         return !this.unicodeFlag ? this.charWidthFloat[ch] : 4.0F;
      }
   }

   public int getInt(String text2, int i, int i2, int i3) {
      return this.drawString(text2, i, i2, i3, false);
   }

   protected void bindTexture(ResourceLocation arg1) {
      this.renderEngine.bindTexture(arg1);
   }

   protected void enableAlpha() {
      GlStateManager.enableAlpha();
   }

   private void handleString(String text2, int i, int i2, int i3) {
      for (String text3 : this.listFormattedStringToWidth(text2, i3)) {
         this.getInt2(text3, i, i2, i3, this.textColor);
         i2 += this.FONT_HEIGHT;
      }
   }

   private void readGlyphSizes() {
      InputStream inputStream = null;

      try {
         inputStream = this.getResourceInputStream(new ResourceLocation("font/glyph_sizes.bin"));
         inputStream.read(this.glyphWidth);
      } catch (IOException ioException) {
         throw new RuntimeException(ioException);
      } finally {
         IOUtils.closeQuietly(inputStream);
      }
   }

   private int renderString(String text, float x, float y, int color, boolean dropShadow) {
      if (text == null) {
         return 0;
      }

      if (this.bidiFlag) {
         text = this.bidiReorder(text);
      }

      if ((color & -67108864) == 0) {
         color |= -16777216;
      }

      if (dropShadow) {
         color = (color & 16579836) >> 2 | color & 0xFF000000;
      }

      this.red = (color >> 16 & 0xFF) / 255.0F;
      this.blue = (color >> 8 & 0xFF) / 255.0F;
      this.green = (color & 0xFF) / 255.0F;
      this.alpha = (color >> 24 & 0xFF) / 255.0F;
      this.setColor(this.red, this.blue, this.green, this.alpha);
      this.posX = x;
      this.posY = y;
      this.renderStringAtPos(text, dropShadow);
      return (int)this.posX;
   }

   String wrapFormattedStringToWidth(String str, int wrapWidth) {
      if (str.length() <= 1) {
         return str;
      }

      int i = this.sizeStringToWidth(str, wrapWidth);
      if (str.length() <= i) {
         return str;
      }

      String text = str.substring(0, i);
      char c0 = str.charAt(i);
      boolean flag = c0 == ' ' || c0 == '\n';
      String text2 = getFormatFromString(text) + str.substring(i + (flag ? 1 : 0));
      return text + "\n" + this.wrapFormattedStringToWidth(text2, wrapWidth);
   }

   private int getInt2(String text2, int i, int i2, int i3, int i4) {
      if (this.bidiFlag) {
         int intValue = this.getInt3(this.bidiReorder(text2));
         i = i + i3 - intValue;
      }

      return this.renderString(text2, i, i2, i4, false);
   }

   public boolean getBidiFlag() {
      return this.bidiFlag;
   }

   protected void setColor(float arg1, float arg2, float arg3, float arg4) {
      GlStateManager.color(arg1, arg2, arg3, arg4);
   }

   private void resetStyles() {
      this.randomStyle = false;
      this.boldStyle = false;
      this.italicStyle = false;
      this.underlineStyle = false;
      this.strikethroughStyle = false;
   }

   public int splitStringWidth(String str, int maxLength) {
      return this.FONT_HEIGHT * this.listFormattedStringToWidth(str, maxLength).size();
   }

   public void drawSplitString(String str, int x, int y, int wrapWidth, int textColor) {
      if (this.blend) {
         GlStateManager.getBlendState(this.oldBlendState);
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 771);
      }

      this.resetStyles();
      this.textColor = textColor;
      str = this.trimStringNewline(str);
      this.handleString(str, x, y, wrapWidth);
      if (this.blend) {
         GlStateManager.setBlendState(this.oldBlendState);
      }
   }

   private void renderStringAtPos(String text, boolean shadow) {
      for (int i = 0; i < text.length(); i++) {
         char c = text.charAt(i);
         if (c == 167 && i + 1 < text.length()) {
            int i2 = "0123456789abcdefklmnor".indexOf(text.toLowerCase(Locale.ENGLISH).charAt(i + 1));
            if (i2 < 16) {
               this.randomStyle = false;
               this.boldStyle = false;
               this.strikethroughStyle = false;
               this.underlineStyle = false;
               this.italicStyle = false;
               if (i2 < 0 || i2 > 15) {
                  i2 = 15;
               }

               if (shadow) {
                  i2 += 16;
               }

               int i3 = this.colorCode[i2];
               if (Config.isCustomColors()) {
                  i3 = CustomColors.getTextColor(i2, i3);
               }

               this.textColor = i3;
               this.setColor((i3 >> 16) / 255.0F, (i3 >> 8 & 0xFF) / 255.0F, (i3 & 0xFF) / 255.0F, this.alpha);
            } else if (i2 == 16) {
               this.randomStyle = true;
            } else if (i2 == 17) {
               this.boldStyle = true;
            } else if (i2 == 18) {
               this.strikethroughStyle = true;
            } else if (i2 == 19) {
               this.underlineStyle = true;
            } else if (i2 == 20) {
               this.italicStyle = true;
            } else if (i2 == 21) {
               this.randomStyle = false;
               this.boldStyle = false;
               this.strikethroughStyle = false;
               this.underlineStyle = false;
               this.italicStyle = false;
               this.setColor(this.red, this.blue, this.green, this.alpha);
            }

            i++;
         } else {
            int i4 = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
               .indexOf(c);
            if (this.randomStyle && i4 != -1) {
               int charWidth2 = this.getCharWidth(c);

               char c2;
               do {
                  i4 = this.fontRandom
                     .nextInt(
                        "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
                           .length()
                     );
                  c2 = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
                     .charAt(i4);
               } while (charWidth2 != this.getCharWidth(c2));

               c = c2;
            }

            float f2 = i4 != -1 && !this.unicodeFlag ? this.offsetBold : 0.5F;
            boolean flag = (c == 0 || i4 == -1 || this.unicodeFlag) && shadow;
            if (flag) {
               this.posX -= f2;
               this.posY -= f2;
            }

            float f3 = this.renderChar(c, this.italicStyle);
            if (flag) {
               this.posX += f2;
               this.posY += f2;
            }

            if (this.boldStyle) {
               this.posX += f2;
               if (flag) {
                  this.posX -= f2;
                  this.posY -= f2;
               }

               this.renderChar(c, this.italicStyle);
               this.posX -= f2;
               if (flag) {
                  this.posX += f2;
                  this.posY += f2;
               }

               f3 += f2;
            }

            this.doDraw(f3);
         }
      }
   }

   private ResourceLocation getUnicodePageLocation(int page) {
      if (unicodePageLocations[page] == null) {
         unicodePageLocations[page] = new ResourceLocation(String.format("textures/font/unicode_page_%02x.png", page));
         unicodePageLocations[page] = FontUtils.getHdFontLocation(unicodePageLocations[page]);
      }

      return unicodePageLocations[page];
   }

   public String trimStringToWidth(String text, int width) {
      return this.trimStringToWidth(text, width, false);
   }

   public int drawString(String text, float x, float y, int color, boolean dropShadow) {
      this.enableAlpha();
      if (this.blend) {
         GlStateManager.getBlendState(this.oldBlendState);
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 771);
      }

      this.resetStyles();
      int i;
      if (dropShadow) {
         i = this.renderString(text, x + 1.0F, y + 1.0F, color, true);
         i = Math.max(i, this.renderString(text, x, y, color, false));
      } else {
         i = this.renderString(text, x, y, color, false);
      }

      if (this.blend) {
         GlStateManager.setBlendState(this.oldBlendState);
      }

      return i;
   }

   protected InputStream getResourceInputStream(ResourceLocation arg1) throws IOException {
      return Minecraft.getMinecraft().getResourceManager().getResource(arg1).getInputStream();
   }

   private float getCharWidthFloat(char charIn) {
      if (charIn == 167) {
         return -1.0F;
      }

      if (charIn != ' ' && charIn != 160) {
         int i = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
            .indexOf(charIn);
         if (charIn > 0 && i != -1 && !this.unicodeFlag) {
            return this.charWidthFloat[i];
         }

         if (this.glyphWidth[charIn] != 0) {
            int i2 = this.glyphWidth[charIn] >>> 4;
            int i3 = this.glyphWidth[charIn] & 15;
            if (i3 > 7) {
               i3 = 15;
               i2 = 0;
            }

            i3++;
            return (i3 - i2) / 2 + 1;
         } else {
            return 0.0F;
         }
      } else {
         return this.charWidthFloat[32];
      }
   }

   public List<String> listFormattedStringToWidth(String str, int wrapWidth) {
      return Arrays.asList(this.wrapFormattedStringToWidth(str, wrapWidth).split("\n"));
   }

   protected void doDraw(float arg1) {
      if (this.strikethroughStyle) {
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer worldRenderer = tessellator.getWorldRenderer();
         GlStateManager.disableTexture2D();
         worldRenderer.begin(7, DefaultVertexFormats.POSITION);
         worldRenderer.pos(this.posX, this.posY + this.FONT_HEIGHT / 2, 0.0).endVertex();
         worldRenderer.pos(this.posX + arg1, this.posY + this.FONT_HEIGHT / 2, 0.0).endVertex();
         worldRenderer.pos(this.posX + arg1, this.posY + this.FONT_HEIGHT / 2 - 1.0F, 0.0).endVertex();
         worldRenderer.pos(this.posX, this.posY + this.FONT_HEIGHT / 2 - 1.0F, 0.0).endVertex();
         tessellator.draw();
         GlStateManager.enableTexture2D();
      }

      if (this.underlineStyle) {
         Tessellator tessellator2 = Tessellator.getInstance();
         WorldRenderer worldRenderer2 = tessellator2.getWorldRenderer();
         GlStateManager.disableTexture2D();
         worldRenderer2.begin(7, DefaultVertexFormats.POSITION);
         int i = this.underlineStyle ? -1 : 0;
         worldRenderer2.pos(this.posX + i, this.posY + this.FONT_HEIGHT, 0.0).endVertex();
         worldRenderer2.pos(this.posX + arg1, this.posY + this.FONT_HEIGHT, 0.0).endVertex();
         worldRenderer2.pos(this.posX + arg1, this.posY + this.FONT_HEIGHT - 1.0F, 0.0).endVertex();
         worldRenderer2.pos(this.posX + i, this.posY + this.FONT_HEIGHT - 1.0F, 0.0).endVertex();
         tessellator2.draw();
         GlStateManager.enableTexture2D();
      }

      this.posX += arg1;
   }

   public String trimStringToWidth(String text, int width, boolean reverse) {
      StringBuilder stringBuilder = new StringBuilder();
      float f2 = 0.0F;
      int count = reverse ? text.length() - 1 : 0;
      int i = reverse ? -1 : 1;
      boolean conditionMet = false;
      boolean conditionMet2 = false;

      for (int nestedIndex = count; nestedIndex >= 0 && nestedIndex < text.length() && f2 < width; nestedIndex += i) {
         char c0 = text.charAt(nestedIndex);
         float charWidthFloat2 = this.getCharWidthFloat(c0);
         if (conditionMet) {
            conditionMet = false;
            if (c0 == 'l' || c0 == 'L') {
               conditionMet2 = true;
            } else if (c0 == 'r' || c0 == 'R') {
               conditionMet2 = false;
            }
         } else if (charWidthFloat2 < 0.0F) {
            conditionMet = true;
         } else {
            f2 += charWidthFloat2;
            if (conditionMet2) {
               f2++;
            }
         }

         if (f2 > width) {
            break;
         }

         if (reverse) {
            stringBuilder.insert(0, c0);
         } else {
            stringBuilder.append(c0);
         }
      }

      return stringBuilder.toString();
   }

   public FontRenderer(GameSettings gameSettings2, ResourceLocation resourceLocation, TextureManager textureManager) {
      this.gameSettings = gameSettings2;
      this.locationFontTextureBase = resourceLocation;
      this.locationFontTexture = resourceLocation;
      this.renderEngine = textureManager;
      this.unicodeFlag = false;
      this.locationFontTexture = FontUtils.getHdFontLocation(this.locationFontTextureBase);
      this.bindTexture(this.locationFontTexture);

      for (int i = 0; i < 32; i++) {
         int i2 = (i >> 3 & 1) * 85;
         int i3 = (i >> 2 & 1) * 170 + i2;
         int i4 = (i >> 1 & 1) * 170 + i2;
         int i5 = (i >> 0 & 1) * 170 + i2;
         if (i == 6) {
            i3 += 85;
         }

         if (gameSettings2.anaglyph) {
            int i6 = (i3 * 30 + i4 * 59 + i5 * 11) / 100;
            int i7 = (i3 * 30 + i4 * 70) / 100;
            int i8 = (i3 * 30 + i5 * 70) / 100;
            i3 = i6;
            i4 = i7;
            i5 = i8;
         }

         if (i >= 16) {
            i3 /= 4;
            i4 /= 4;
            i5 /= 4;
         }

         this.colorCode[i] = (i3 & 0xFF) << 16 | (i4 & 0xFF) << 8 | i5 & 0xFF;
      }

      this.readGlyphSizes();
   }

   public static String getFormatFromString(String text) {
      String message = "";
      int i = -1;
      int count = text.length();

      while ((i = text.indexOf(167, i + 1)) != -1) {
         if (i < count - 1) {
            char c0 = text.charAt(i + 1);
            if (isFormatColor(c0)) {
               message = "§" + c0;
            } else if (isFormatSpecial(c0)) {
               message = message + "§" + c0;
            }
         }
      }

      return message;
   }

   public int drawStringWithShadow(String text, float x, float y, int color) {
      return this.drawString(text, x, y, color, true);
   }

   private float renderDefaultChar(int ch, boolean italic) {
      int i = ch % 16 * 8;
      int i2 = ch / 16 * 8;
      int i3 = italic ? 1 : 0;
      this.bindTexture(this.locationFontTexture);
      float f2 = this.charWidthFloat[ch];
      float f3 = 7.99F;
      GL11.glBegin(5);
      GL11.glTexCoord2f(i / 128.0F, i2 / 128.0F);
      GL11.glVertex3f(this.posX + i3, this.posY, 0.0F);
      GL11.glTexCoord2f(i / 128.0F, (i2 + 7.99F) / 128.0F);
      GL11.glVertex3f(this.posX - i3, this.posY + 7.99F, 0.0F);
      GL11.glTexCoord2f((i + f3 - 1.0F) / 128.0F, i2 / 128.0F);
      GL11.glVertex3f(this.posX + f3 - 1.0F + i3, this.posY, 0.0F);
      GL11.glTexCoord2f((i + f3 - 1.0F) / 128.0F, (i2 + 7.99F) / 128.0F);
      GL11.glVertex3f(this.posX + f3 - 1.0F - i3, this.posY + 7.99F, 0.0F);
      GL11.glEnd();
      return f2;
   }

   private String trimStringNewline(String text) {
      while (text != null && text.endsWith("\n")) {
         text = text.substring(0, text.length() - 1);
      }

      return text;
   }

   private float renderUnicodeChar(char ch, boolean italic) {
      if (this.glyphWidth[ch] == 0) {
         return 0.0F;
      }

      int i = ch / 256;
      this.loadGlyphTexture(i);
      int i2 = this.glyphWidth[ch] >>> 4;
      int i3 = this.glyphWidth[ch] & 15;
      float f2 = i2;
      float f3 = i3 + 1;
      float f4 = ch % 16 * 16 + f2;
      float f5 = (ch & 255) / 16 * 16;
      float f6 = f3 - f2 - 0.02F;
      float f7 = italic ? 1.0F : 0.0F;
      GL11.glBegin(5);
      GL11.glTexCoord2f(f4 / 256.0F, f5 / 256.0F);
      GL11.glVertex3f(this.posX + f7, this.posY, 0.0F);
      GL11.glTexCoord2f(f4 / 256.0F, (f5 + 15.98F) / 256.0F);
      GL11.glVertex3f(this.posX - f7, this.posY + 7.99F, 0.0F);
      GL11.glTexCoord2f((f4 + f6) / 256.0F, f5 / 256.0F);
      GL11.glVertex3f(this.posX + f6 / 2.0F + f7, this.posY, 0.0F);
      GL11.glTexCoord2f((f4 + f6) / 256.0F, (f5 + 15.98F) / 256.0F);
      GL11.glVertex3f(this.posX + f6 / 2.0F - f7, this.posY + 7.99F, 0.0F);
      GL11.glEnd();
      return (f3 - f2) / 2.0F + 1.0F;
   }

   private static boolean isFormatSpecial(char formatChar) {
      return formatChar >= 'k' && formatChar <= 'o' || formatChar >= 'K' && formatChar <= 'O' || formatChar == 'r' || formatChar == 'R';
   }

   public int getCharWidth(char character) {
      return Math.round(this.getCharWidthFloat(character));
   }

   public boolean getUnicodeFlag() {
      return this.unicodeFlag;
   }

   private String bidiReorder(String text) {
      try {
         Bidi bidi = new Bidi(new ArabicShaping(8).shape(text), 127);
         bidi.setReorderingMode(0);
         return bidi.writeReordered(2);
      } catch (ArabicShapingException arabicShapingException) {
         return text;
      }
   }

   private void loadGlyphTexture(int page) {
      this.bindTexture(this.getUnicodePageLocation(page));
   }

   public void setUnicodeFlag(boolean unicodeFlag) {
      this.unicodeFlag = unicodeFlag;
   }

   public int getInt3(String text2) {
      if (text2 == null) {
         return 0;
      }

      float f2 = 0.0F;
      boolean flag = false;

      for (int i = 0; i < text2.length(); i++) {
         char c = text2.charAt(i);
         float charWidthFloat2 = this.getCharWidthFloat(c);
         if (charWidthFloat2 < 0.0F && i < text2.length() - 1) {
            c = text2.charAt(++i);
            if (c == 'l' || c == 'L') {
               flag = true;
            } else if (c == 'r' || c == 'R') {
               flag = false;
            }

            charWidthFloat2 = 0.0F;
         }

         f2 += charWidthFloat2;
         if (flag && charWidthFloat2 > 0.0F) {
            f2 += this.unicodeFlag ? 1.0F : this.offsetBold;
         }
      }

      return Math.round(f2);
   }

   private void readFontTexture() {
      BufferedImage bufferedImage;
      try {
         bufferedImage = TextureUtil.readBufferedImage(this.getResourceInputStream(this.locationFontTexture));
      } catch (IOException ioException) {
         throw new RuntimeException(ioException);
      }

      Properties fontProperties = FontUtils.readFontProperties(this.locationFontTexture);
      this.blend = FontUtils.checkProperties(fontProperties, "blend");
      int width2 = bufferedImage.getWidth();
      int height = bufferedImage.getHeight();
      int i = width2 / 16;
      int i2 = height / 16;
      float f2 = width2 / 128.0F;
      float f3 = Config.limit(f2, 1.0F, 2.0F);
      this.offsetBold = 1.0F / f3;
      float floatValue = FontUtils.readFloat(fontProperties, "offsetBold", -1.0F);
      if (floatValue >= 0.0F) {
         this.offsetBold = floatValue;
      }

      int[] iArray = new int[width2 * height];
      bufferedImage.getRGB(0, 0, width2, height, iArray, 0, width2);

      for (int i3 = 0; i3 < 256; i3++) {
         int i4 = i3 % 16;
         int i5 = i3 / 16;

         int i6;
         for (i6 = i - 1; i6 >= 0; i6--) {
            int i7 = i4 * i + i6;
            boolean flag = true;

            for (int i8 = 0; i8 < i2 && flag; i8++) {
               int i9 = (i5 * i2 + i8) * width2;
               if ((iArray[i7 + i9] >> 24 & 0xFF) > 16) {
                  flag = false;
               }
            }

            if (!flag) {
               break;
            }
         }

         if (i3 == 65) {
            i3 = i3;
         }

         if (i3 == 32) {
            if (i <= 8) {
               i6 = (int)(2.0F * f2);
            } else {
               i6 = (int)(1.5F * f2);
            }
         }

         this.charWidthFloat[i3] = (i6 + 1) / f2 + 1.0F;
      }

      FontUtils.readCustomCharWidths(fontProperties, this.charWidthFloat);

      for (int i10 = 0; i10 < this.charWidth.length; i10++) {
         this.charWidth[i10] = Math.round(this.charWidthFloat[i10]);
      }
   }

   private int sizeStringToWidth(String str, int wrapWidth) {
      int count = str.length();
      float f2 = 0.0F;
      int i = 0;
      int i2 = -1;
      boolean conditionMet = false;

      while (i < count) {
         char c0 = str.charAt(i);
         switch (c0) {
            case '\n':
               i--;
               break;
            case ' ':
               i2 = i;
            default:
               f2 += this.getCharWidth(c0);
               if (conditionMet) {
                  f2++;
               }
               break;
            case '§':
               if (i < count - 1) {
                  char c1 = str.charAt(++i);
                  if (c1 == 'l' || c1 == 'L') {
                     conditionMet = true;
                  } else if (c1 == 'r' || c1 == 'R' || isFormatColor(c1)) {
                     conditionMet = false;
                  }
               }
         }

         if (c0 == '\n') {
            i2 = ++i;
            break;
         }

         if (Math.round(f2) > wrapWidth) {
            break;
         }

         i++;
      }

      return i != count && i2 != -1 && i2 < i ? i2 : i;
   }

   private static boolean isFormatColor(char colorChar) {
      return colorChar >= '0' && colorChar <= '9' || colorChar >= 'a' && colorChar <= 'f' || colorChar >= 'A' && colorChar <= 'F';
   }

   public void setBidiFlag(boolean bidiFlag) {
      this.bidiFlag = bidiFlag;
   }

   public int getColorCode(char character) {
      int i = "0123456789abcdef".indexOf(character);
      if (i >= 0 && i < this.colorCode.length) {
         int i2 = this.colorCode[i];
         if (Config.isCustomColors()) {
            i2 = CustomColors.getTextColor(i, i2);
         }

         return i2;
      } else {
         return 16777215;
      }
   }

   @Override
   public void onResourceManagerReload(IResourceManager resourceManager) {
      this.locationFontTexture = FontUtils.getHdFontLocation(this.locationFontTextureBase);

      for (int i = 0; i < unicodePageLocations.length; i++) {
         unicodePageLocations[i] = null;
      }

      this.readFontTexture();
      this.readGlyphSizes();
   }
}
