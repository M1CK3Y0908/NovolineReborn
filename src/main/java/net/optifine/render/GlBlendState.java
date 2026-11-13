package net.optifine.render;

public class GlBlendState {
   private boolean enabled;
   private int srcFactor;
   private int dstFactor;
   private int srcFactorAlpha;
   private int dstFactorAlpha;

   public int getDstFactorAlpha() {
      return this.dstFactorAlpha;
   }

   public int getDstFactor() {
      return this.dstFactor;
   }

   public void setState(GlBlendState state) {
      this.enabled = state.enabled;
      this.srcFactor = state.srcFactor;
      this.dstFactor = state.dstFactor;
      this.srcFactorAlpha = state.srcFactorAlpha;
      this.dstFactorAlpha = state.dstFactorAlpha;
   }

   public int getSrcFactorAlpha() {
      return this.srcFactorAlpha;
   }

   public int getSrcFactor() {
      return this.srcFactor;
   }

   public void setState(boolean enabled, int srcFactor, int dstFactor, int srcFactorAlpha, int dstFactorAlpha) {
      this.enabled = enabled;
      this.srcFactor = srcFactor;
      this.dstFactor = dstFactor;
      this.srcFactorAlpha = srcFactorAlpha;
      this.dstFactorAlpha = dstFactorAlpha;
   }

   public GlBlendState() {
      this(false, 1, 0);
   }

   @Override
   public String toString() {
      return "enabled: "
         + this.enabled
         + ", src: "
         + this.srcFactor
         + ", dst: "
         + this.dstFactor
         + ", srcAlpha: "
         + this.srcFactorAlpha
         + ", dstAlpha: "
         + this.dstFactorAlpha;
   }

   public void setEnabled() {
      this.enabled = true;
   }

   public void setDisabled() {
      this.enabled = false;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public boolean isSeparate() {
      return this.srcFactor != this.srcFactorAlpha || this.dstFactor != this.dstFactorAlpha;
   }

   public GlBlendState(boolean enabled, int srcFactor, int dstFactor) {
      this(enabled, srcFactor, dstFactor, srcFactor, dstFactor);
   }

   public GlBlendState(boolean enabled, int srcFactor, int dstFactor, int srcFactorAlpha, int dstFactorAlpha) {
      this.enabled = enabled;
      this.srcFactor = srcFactor;
      this.dstFactor = dstFactor;
      this.srcFactorAlpha = srcFactorAlpha;
      this.dstFactorAlpha = dstFactorAlpha;
   }

   public void setFactors(int srcFactor, int dstFactor, int srcFactorAlpha, int dstFactorAlpha) {
      this.srcFactor = srcFactor;
      this.dstFactor = dstFactor;
      this.srcFactorAlpha = srcFactorAlpha;
      this.dstFactorAlpha = dstFactorAlpha;
   }

   public GlBlendState(boolean enabled) {
      this(enabled, 1, 0);
   }

   public void setFactors(int srcFactor, int dstFactor) {
      this.srcFactor = srcFactor;
      this.dstFactor = dstFactor;
      this.srcFactorAlpha = srcFactor;
      this.dstFactorAlpha = dstFactor;
   }
}
