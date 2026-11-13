package wtf.novoline.utils.render;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import wtf.novoline.utils.render.TrueTypeFont.Helper;
import wtf.novoline.utils.render.shape.IRectRenderer;

public class CFontRenderer extends TrueTypeFont {
   private final int[] intArray = new int[32];
   private static final Minecraft minecraft = Minecraft.getMinecraft();
   private final String formatCodeChars = "0123456789abcdefklmnor";

   public float drawStringWithNiceShadow(String text, float f, float f2, int i) {
      return this.drawStringInternal(text, f, f2, i, false, true);
   }

   public float drawStringWithShadow(String text, double d, double d2, int i) {
      float f = this.drawStringInternal(text, d + 0.5, d2 + 0.5, i, true, false);
      return Math.max(f, this.drawStringInternal(text, d, d2, i, false, false));
   }

   private void buildColorTable() {
      for (int i = 0; i < 32; i++) {
         int i2 = (i >> 3 & 1) * 85;
         int i3 = (i >> 2 & 1) * 170 + i2;
         int i4 = (i >> 1 & 1) * 170 + i2;
         int i5 = (i >> 0 & 1) * 170 + i2;
         if (i == 6) {
            i3 += 85;
         }

         if (i >= 16) {
            i3 /= 4;
            i4 /= 4;
            i5 /= 4;
         }

         this.intArray[i] = (i3 & 0xFF) << 16 | (i4 & 0xFF) << 8 | i5 & 0xFF;
      }
   }

   public float drawStringInternal(String text, double d, double d2, int i, boolean flag, boolean flag2) {
      d--;
      if (text == null) {
         return 0.0F;
      }

      if (i == 553648127) {
         i = 16777215;
      }

      if ((i & -67108864) == 0) {
         i |= -16777216;
      }

      if (flag) {
         i = (i & 16579836) >> 2 | i & 0xFF000000;
      }

      Helper[] helperArray2 = this.helperArray;
      int i2 = i >> 24 & 0xFF;
      boolean flag3 = false;
      boolean flag4 = false;
      d *= 2.0;
      d2 = (d2 - 3.0) * 2.0;
      if (!text.isEmpty()) {
         GL11.glPushMatrix();
         GlStateManager.scale(0.5, 0.5, 0.5);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 1);
         int length2 = text.length();
         GlStateManager.enableTexture2D();
         GlStateManager.bindTexture(this.dynamicTexture.getGlTextureId());
         Tessellator instance = Tessellator.getInstance();
         WorldRenderer worldRenderer = instance.getWorldRenderer();
         worldRenderer.begin(4, DefaultVertexFormats.POSITION_TEX_COLOR);
         int i3 = i;

         for (int i4 = 0; i4 < length2; i4++) {
            char c = text.charAt(i4);
            if (c == 167 && i4 < length2) {
               int i5 = 21;

               try {
                  i5 = "0123456789abcdefklmnor".indexOf(text.charAt(i4 + 1));
               } catch (Exception exception) {
                  exception.printStackTrace();
               }

               if (i5 >= 16 && i5 <= 21) {
                  if (i5 == 18) {
                     flag3 = true;
                  } else if (i5 == 19) {
                     flag4 = true;
                  } else if (i5 == 21) {
                     flag4 = false;
                     flag3 = false;
                     i3 = i;
                  }
               } else {
                  if (i5 > 21) {
                     i5 -= 12;
                  }

                  flag4 = false;
                  flag3 = false;
                  if (i5 < 0 || i5 > 15) {
                     i5 = 15;
                  }

                  if (flag) {
                     i5 += 16;
                  }

                  i3 = this.intArray[i5] | (i2 & 0xFF) << 24;
               }

               i4++;
            } else if (c < helperArray2.length && c >= 0) {
               if (flag2) {
                  this.addGlyph(worldRenderer, helperArray2, c, (float)d - 1.0F, (float)d2, 0.0F, 0.0F, 0.0F, 0.50196F);
                  this.addGlyph(worldRenderer, helperArray2, c, (float)d, (float)d2 - 1.0F, 0.0F, 0.0F, 0.0F, 0.50196F);
                  this.addGlyph(worldRenderer, helperArray2, c, (float)d + 1.0F, (float)d2, 0.0F, 0.0F, 0.0F, 0.50196F);
                  this.addGlyph(worldRenderer, helperArray2, c, (float)d, (float)d2 + 1.0F, 0.0F, 0.0F, 0.0F, 0.50196F);
               }

               this.addGlyph(
                  worldRenderer,
                  helperArray2,
                  c,
                  (float)d,
                  (float)d2,
                  (i3 >> 16 & 0xFF) / 255.0F,
                  (i3 >> 8 & 0xFF) / 255.0F,
                  (i3 & 0xFF) / 255.0F,
                  (i3 >> 24 & 0xFF) / 255.0F
               );
               if (flag3) {
                  this.drawLine(d, d2 + helperArray2[c].height / 2, d + helperArray2[c].width - 8.0, d2 + helperArray2[c].height / 2, 1.0F);
               }

               if (flag4) {
                  this.drawLine(d, d2 + helperArray2[c].height - 2.0, d + helperArray2[c].width - 8.0, d2 + helperArray2[c].height - 2.0, 1.0F);
               }

               d += helperArray2[c].width - 8 + this.glyphSpacing;
            }
         }

         instance.draw();
         GL11.glHint(3155, 4352);
         GL11.glPopMatrix();
      }

      return (float)d / 2.0F;
   }

   private void drawLine(double d, double d2, double d3, double d4, float f) {
      GL11.glDisable(3553);
      GL11.glLineWidth(f);
      GL11.glBegin(1);
      GL11.glVertex2d(d, d2);
      GL11.glVertex2d(d3, d4);
      GL11.glEnd();
      GL11.glEnable(3553);
   }

   public float drawCenteredString(String text, float f, float f2, int i) {
      return this.drawString(text, f - this.getStringWidth(text) / 2, f2, i);
   }

   public List<String> wrapWords(String text, double d) {
      ArrayList arrayList = new ArrayList();
      if (this.getStringWidth(text) > d) {
         String[] texts = text.split(" ");
         String text2 = "";
         char c = '\uffff';

         for (String text3 : texts) {
            for (int i = 0; i < text3.toCharArray().length; i++) {
               if (text3.toCharArray()[i] == 167 && i < text3.toCharArray().length - 1) {
                  c = text3.toCharArray()[i + 1];
               }
            }

            if (this.getStringWidth(text2 + text3 + " ") < d) {
               text2 = text2 + text3 + " ";
            } else {
               arrayList.add(text2);
               text2 = "§" + c + text3 + " ";
            }
         }

         if (text2.length() > 0) {
            if (this.getStringWidth(text2) < d) {
               arrayList.add("§" + c + text2 + " ");
            } else {
               arrayList.addAll(this.formatString(text2, d));
            }
         }
      } else {
         arrayList.add(text);
      }

      return arrayList;
   }

   @Override
   public int getStringWidth(String text) {
      if (text != null && !text.isEmpty()) {
         int i = 0;
         Helper[] helperArray2 = this.helperArray;
         int length2 = text.length();

         for (int i2 = 0; i2 < length2; i2++) {
            char c = text.charAt(i2);
            if (c == 167 && i2 < length2) {
               i2++;
            } else if (c < helperArray2.length && c >= 0) {
               i += helperArray2[c].width - 8 + this.glyphSpacing;
            }
         }

         return i / 2;
      } else {
         return 0;
      }
   }

   public float drawStringWithNiceShadowNoColor(String text, float f, float f2, int i) {
      return this.drawStringInternal(text, f, f2, i, false, true);
   }

   public CFontRenderer(Font font) {
      super(font);
      this.buildColorTable();
   }

   public float drawCenteredStringWithShadow(String text, float f, float f2, int i) {
      this.drawStringInternal(text, f - this.getStringWidth(text) / 2 + 1.0, f2 + 1.0, i, true, false);
      return this.drawString(text, f - this.getStringWidth(text) / 2, f2, i);
   }

   public List<String> formatString(String text, double d) {
      ArrayList arrayList = new ArrayList();
      String text2 = "";
      char c = '\uffff';
      char[] cArray = text.toCharArray();

      for (int i = 0; i < cArray.length; i++) {
         char c2 = cArray[i];
         if (c2 == 167 && i < cArray.length - 1) {
            c = cArray[i + 1];
         }

         if (this.getStringWidth(text2 + c2) < d) {
            text2 = text2 + c2;
         } else {
            arrayList.add(text2);
            text2 = "§" + c + c2;
         }
      }

      if (text2.length() > 0) {
         arrayList.add(text2);
      }

      return arrayList;
   }

   public float drawString(String text, float f, float f2, int i) {
      return this.drawStringInternal(text, f, f2, i, false, false);
   }

   public float drawStringToRenderer(IRectRenderer iRectRenderer, String text, double d, double d2, int i) {
      d--;
      if (text == null) {
         return 0.0F;
      }

      if (i == 553648127) {
         i = 16777215;
      }

      if ((i & -67108864) == 0) {
         i |= -16777216;
      }

      Helper[] helperArray2 = this.helperArray;
      int i2 = i >> 24 & 0xFF;
      int i3 = i;
      d *= 2.0;
      d2 = (d2 - 3.0) * 2.0;
      int length2 = text.length();

      for (int i4 = 0; i4 < length2; i4++) {
         char c = text.charAt(i4);
         if (c == 167 && i4 < length2) {
            int i5 = 21;

            try {
               i5 = "0123456789abcdefklmnorABCDEF".indexOf(text.charAt(i4 + 1));
            } catch (Exception exception) {
               exception.printStackTrace();
            }

            if (i5 >= 16 && i5 <= 21) {
               if (i5 == 21) {
                  i3 = RenderUtils.withAlpha(i, i2);
                  helperArray2 = this.helperArray;
               }
            } else {
               if (i5 > 21) {
                  i5 -= 12;
               }

               helperArray2 = this.helperArray;
               if (i5 < 0 || i5 > 15) {
                  i5 = 15;
               }

               i3 = RenderUtils.withAlpha(this.intArray[i5], i2);
            }

            i4++;
         } else if (c < helperArray2.length && c >= 0) {
            this.addGlyphRect(iRectRenderer, helperArray2, c, (float)d, (float)d2, i3);
            d += helperArray2[c].width - 8 + this.glyphSpacing;
         }
      }

      return (float)d / 2.0F;
   }
}
