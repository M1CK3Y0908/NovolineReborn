package net.optifine.render;

public class GlAlphaState {
   private boolean enabled;
   private int func;
   private float ref;

   public void setState(boolean enabled, int func, float ref) {
      this.enabled = enabled;
      this.func = func;
      this.ref = ref;
   }

   public float getRef() {
      return this.ref;
   }

   public GlAlphaState(boolean enabled) {
      this(enabled, 519, 0.0F);
   }

   public void setEnabled() {
      this.enabled = true;
   }

   public GlAlphaState(boolean enabled, int func, float ref) {
      this.enabled = enabled;
      this.func = func;
      this.ref = ref;
   }

   public void setDisabled() {
      this.enabled = false;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public int getFunc() {
      return this.func;
   }

   public GlAlphaState() {
      this(false, 519, 0.0F);
   }

   public void setFuncRef(int func, float ref) {
      this.func = func;
      this.ref = ref;
   }

   @Override
   public String toString() {
      float ref2 = this.ref;
      return "enabled: " + this.enabled + ", func: " + this.func + ", ref: " + ref2;
   }

   public void setState(GlAlphaState state) {
      this.enabled = state.enabled;
      this.func = state.func;
      this.ref = state.ref;
   }
}
