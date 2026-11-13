package net.optifine.util;

public class SmoothFloat {
   private float valueLast;
   private float timeFadeUpSec;
   private float timeFadeDownSec;
   private long timeLastMs;

   public float getSmoothValue(float value) {
      long l = System.currentTimeMillis();
      float valueLast2 = this.valueLast;
      long timeLastMs2 = this.timeLastMs;
      float f = (float)(l - timeLastMs2) / 1000.0F;
      float f2 = value >= valueLast2 ? this.timeFadeUpSec : this.timeFadeDownSec;
      float smoothValue = getSmoothValue(valueLast2, value, f, f2);
      this.valueLast = smoothValue;
      this.timeLastMs = l;
      return smoothValue;
   }

   public float getSmoothValue(float value, float timeFadeUpSec, float timeFadeDownSec) {
      this.timeFadeUpSec = timeFadeUpSec;
      this.timeFadeDownSec = timeFadeDownSec;
      return this.getSmoothValue(value);
   }

   public float getTimeFadeDownSec() {
      return this.timeFadeDownSec;
   }

   public float getValueLast() {
      return this.valueLast;
   }

   public long getTimeLastMs() {
      return this.timeLastMs;
   }

   public static float getSmoothValue(float valPrev, float value, float timeDeltaSec, float timeFadeSec) {
      if (timeDeltaSec <= 0.0F) {
         return valPrev;
      }

      float f = value - valPrev;
      float f2;
      if (timeFadeSec > 0.0F && timeDeltaSec < timeFadeSec && Math.abs(f) > 1.0E-6F) {
         float f3 = timeFadeSec / timeDeltaSec;
         float f4 = 4.61F;
         float f5 = 0.13F;
         float f6 = 10.0F;
         float f7 = f4 - 1.0F / (f5 + f3 / f6);
         float f8 = timeDeltaSec / timeFadeSec * f7;
         f8 = NumUtils.limit(f8, 0.0F, 1.0F);
         f2 = valPrev + f * f8;
      } else {
         f2 = value;
      }

      return f2;
   }

   public SmoothFloat(float valueLast, float timeFadeSec) {
      this(valueLast, timeFadeSec, timeFadeSec);
   }

   public SmoothFloat(float valueLast, float timeFadeUpSec, float timeFadeDownSec) {
      this.valueLast = valueLast;
      this.timeFadeUpSec = timeFadeUpSec;
      this.timeFadeDownSec = timeFadeDownSec;
      this.timeLastMs = System.currentTimeMillis();
   }

   public float getTimeFadeUpSec() {
      return this.timeFadeUpSec;
   }
}
