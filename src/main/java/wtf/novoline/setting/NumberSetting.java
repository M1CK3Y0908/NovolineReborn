package wtf.novoline.setting;

import net.minecraft.util.MathHelper;
import wtf.novoline.module.Module;
import wtf.novoline.setting.ModuleSetting.CheckboxType;

public class NumberSetting extends ModuleSetting {
   private float currentValue;
   private final float defaultValue;
   private final NumberSetting.IntType intType;
   private final float minValue;
   private final float maxValue;

   public void setCurrentValueIgnoreNormalize(float f) {
      if (this.intType.equals(NumberSetting.IntType.DECIMAL)) {
         this.currentValue = MathHelper.clamp_float(f, this.minValue, this.maxValue);
      } else {
         this.currentValue = (int)MathHelper.clamp_float(Math.round(f), this.minValue, this.maxValue);
      }

      this.interactCallback();
   }

   public NumberSetting.IntType getType() {
      return this.intType;
   }

   public float getCurrentValue() {
      return this.currentValue;
   }

   public float getMaxValue() {
      return this.maxValue;
   }

   public void setCurrentValue(float f) {
      if (this.intType.equals(NumberSetting.IntType.DECIMAL)) {
         this.currentValue = MathHelper.clamp_float(((int)(f * 100.0F) + 4) / 5 * 5 / 100.0F, this.minValue, this.maxValue);
      } else {
         this.currentValue = (int)MathHelper.clamp_float(Math.round(f), this.minValue, this.maxValue);
      }

      this.interactCallback();
   }

   public NumberSetting(Module module2, String text, int i, int i2, int i3) {
      super(module2, text, "slider", CheckboxType.SLIDER);
      this.currentValue = i;
      this.defaultValue = i;
      this.intType = NumberSetting.IntType.INT;
      this.minValue = i2;
      this.maxValue = i3;
   }

   public float getDefaultState() {
      return this.defaultValue;
   }

   public float getMinValue() {
      return this.minValue;
   }

   public NumberSetting(Module module2, String text, float f, float f2, float f3) {
      super(module2, text, "slider", CheckboxType.SLIDER);
      this.currentValue = f;
      this.defaultValue = f;
      this.intType = NumberSetting.IntType.DECIMAL;
      this.minValue = f2;
      this.maxValue = f3;
   }

   public enum IntType {
      INT,
      DECIMAL;
   }
}
