package net.minecraft.client.util;

import com.google.gson.JsonObject;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.JsonUtils;
import org.lwjgl.opengl.GL14;

public class JsonBlendingMode {
   private final boolean separateBlendFactors;
   private final int srcRgbFactor;
   private static JsonBlendingMode lastAppliedMode = null;
   private final int dstRgbFactor;
   private final int srcAlphaFactor;
   private final int dstAlphaFactor;
   private final boolean blendDisabled;
   private final int blendEquation;

   public JsonBlendingMode(int srcRgbFactor, int dstRgbFactor, int srcAlphaFactor, int dstAlphaFactor, int blendEquation) {
      this(true, false, srcRgbFactor, dstRgbFactor, srcAlphaFactor, dstAlphaFactor, blendEquation);
   }

   @Override
   public int hashCode() {
      int srcRgbFactor2 = this.srcRgbFactor;
      srcRgbFactor2 = 31 * srcRgbFactor2 + this.srcAlphaFactor;
      srcRgbFactor2 = 31 * srcRgbFactor2 + this.dstRgbFactor;
      srcRgbFactor2 = 31 * srcRgbFactor2 + this.dstAlphaFactor;
      srcRgbFactor2 = 31 * srcRgbFactor2 + this.blendEquation;
      srcRgbFactor2 = 31 * srcRgbFactor2 + (this.separateBlendFactors ? 1 : 0);
      return 31 * srcRgbFactor2 + (this.blendDisabled ? 1 : 0);
   }

   private static int parseBlendFactor(String factorName) {
      String text = factorName.trim().toLowerCase();
      text = text.replaceAll("_", "");
      text = text.replaceAll("one", "1");
      text = text.replaceAll("zero", "0");
      text = text.replaceAll("minus", "-");
      return text.equals("0")
         ? 0
         : (
            text.equals("1")
               ? 1
               : (
                  text.equals("srccolor")
                     ? 768
                     : (
                        text.equals("1-srccolor")
                           ? 769
                           : (
                              text.equals("dstcolor")
                                 ? 774
                                 : (
                                    text.equals("1-dstcolor")
                                       ? 775
                                       : (
                                          text.equals("srcalpha")
                                             ? 770
                                             : (text.equals("1-srcalpha") ? 771 : (text.equals("dstalpha") ? 772 : (text.equals("1-dstalpha") ? 773 : -1)))
                                       )
                                 )
                           )
                     )
               )
         );
   }

   public static JsonBlendingMode fromJson(JsonObject jsonObject) {
      if (jsonObject == null) {
         return new JsonBlendingMode();
      }

      int blendEquation = 32774;
      int srcRgbFactor = 1;
      int dstRgbFactor = 0;
      int srcAlphaFactor = 1;
      int dstAlphaFactor = 0;
      boolean useDefaultMode = true;
      boolean separateAlphaFactors = false;
      if (JsonUtils.isString(jsonObject, "func")) {
         blendEquation = parseBlendEquation(jsonObject.get("func").getAsString());
         if (blendEquation != 32774) {
            useDefaultMode = false;
         }
      }

      if (JsonUtils.isString(jsonObject, "srcrgb")) {
         srcRgbFactor = parseBlendFactor(jsonObject.get("srcrgb").getAsString());
         if (srcRgbFactor != 1) {
            useDefaultMode = false;
         }
      }

      if (JsonUtils.isString(jsonObject, "dstrgb")) {
         dstRgbFactor = parseBlendFactor(jsonObject.get("dstrgb").getAsString());
         if (dstRgbFactor != 0) {
            useDefaultMode = false;
         }
      }

      if (JsonUtils.isString(jsonObject, "srcalpha")) {
         srcAlphaFactor = parseBlendFactor(jsonObject.get("srcalpha").getAsString());
         if (srcAlphaFactor != 1) {
            useDefaultMode = false;
         }

         separateAlphaFactors = true;
      }

      if (JsonUtils.isString(jsonObject, "dstalpha")) {
         dstAlphaFactor = parseBlendFactor(jsonObject.get("dstalpha").getAsString());
         if (dstAlphaFactor != 0) {
            useDefaultMode = false;
         }

         separateAlphaFactors = true;
      }

      return useDefaultMode
         ? new JsonBlendingMode()
         : (
            separateAlphaFactors
               ? new JsonBlendingMode(srcRgbFactor, dstRgbFactor, srcAlphaFactor, dstAlphaFactor, blendEquation)
               : new JsonBlendingMode(srcRgbFactor, dstRgbFactor, blendEquation)
         );
   }

   @Override
   public boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof JsonBlendingMode jsonBlendingMode)) {
         return false;
      } else {
         return this.blendEquation != jsonBlendingMode.blendEquation
            ? false
            : (
               this.dstAlphaFactor != jsonBlendingMode.dstAlphaFactor
                  ? false
                  : (
                     this.dstRgbFactor != jsonBlendingMode.dstRgbFactor
                        ? false
                        : (
                           this.blendDisabled != jsonBlendingMode.blendDisabled
                              ? false
                              : (
                                 this.separateBlendFactors != jsonBlendingMode.separateBlendFactors
                                    ? false
                                    : (this.srcAlphaFactor != jsonBlendingMode.srcAlphaFactor ? false : this.srcRgbFactor == jsonBlendingMode.srcRgbFactor)
                              )
                        )
                  )
            );
      }
   }

   private JsonBlendingMode(
      boolean separateBlendFactors, boolean blendDisabled, int srcRgbFactor, int dstRgbFactor, int srcAlphaFactor, int dstAlphaFactor, int blendEquation
   ) {
      this.separateBlendFactors = separateBlendFactors;
      this.srcRgbFactor = srcRgbFactor;
      this.dstRgbFactor = dstRgbFactor;
      this.srcAlphaFactor = srcAlphaFactor;
      this.dstAlphaFactor = dstAlphaFactor;
      this.blendDisabled = blendDisabled;
      this.blendEquation = blendEquation;
   }

   public void apply() {
      if (!this.equals(lastAppliedMode)) {
         if (lastAppliedMode == null || this.blendDisabled != lastAppliedMode.isBlendDisabled()) {
            lastAppliedMode = this;
            if (this.blendDisabled) {
               GlStateManager.disableBlend();
               return;
            }

            GlStateManager.enableBlend();
         }

         GL14.glBlendEquation(this.blendEquation);
         if (this.separateBlendFactors) {
            GlStateManager.tryBlendFuncSeparate(this.srcRgbFactor, this.dstRgbFactor, this.srcAlphaFactor, this.dstAlphaFactor);
         } else {
            GlStateManager.blendFunc(this.srcRgbFactor, this.dstRgbFactor);
         }
      }
   }

   private static int parseBlendEquation(String equationName) {
      String normalizedEquation = equationName.trim().toLowerCase();
      return normalizedEquation.equals("add")
         ? 32774
         : (
            normalizedEquation.equals("subtract")
               ? 32778
               : (
                  normalizedEquation.equals("reversesubtract")
                     ? 32779
                     : (
                        normalizedEquation.equals("reverse_subtract")
                           ? 32779
                           : (normalizedEquation.equals("min") ? 32775 : (normalizedEquation.equals("max") ? 32776 : 32774))
                     )
               )
         );
   }

   public JsonBlendingMode() {
      this(false, true, 1, 0, 1, 0, 32774);
   }

   public JsonBlendingMode(int srcFactor, int dstFactor, int blendEquation) {
      this(false, false, srcFactor, dstFactor, srcFactor, dstFactor, blendEquation);
   }

   public boolean isBlendDisabled() {
      return this.blendDisabled;
   }
}
