package wtf.novoline.utils.animation;

public interface Easing {
   long UNUSED_LONG_CONSTANT = 25939870686924L;
   Easing linear = (d, d2, d3, d4) -> d3 * d / d4 + d2;
   Easing quadIn = (d, d2, d3, d4) -> {
      double d5;
      return d3 * (d5 = d / d4) * d5 + d2;
   };
   Easing quadOut = (d, d2, d3, d4) -> {
      double d5;
      return -d3 * (d5 = d / d4) * (d5 - 2.0) + d2;
   };
   Easing quadInOut = (d, d2, d3, d4) -> {
      double d5;
      return (d5 = d / (d4 / 2.0)) < 1.0 ? d3 / 2.0 * d5 * d5 + d2 : -d3 / 2.0 * (--d5 * (d5 - 2.0) - 1.0) + d2;
   };
   Easing cubicIn = (d, d2, d3, d4) -> {
      double d5;
      return d3 * (d5 = d / d4) * d5 * d5 + d2;
   };
   Easing cubicOut = (d, d2, d3, d4) -> {
      double d5;
      return d3 * ((d5 = d / d4 - 1.0) * d5 * d5 + 1.0) + d2;
   };
   Easing cubicInOut = (d, d2, d3, d4) -> {
      double d5;
      double d6;
      return (d5 = d / (d4 / 2.0)) < 1.0
         ? d3 / 2.0 * d5 * d5 * d5 + d2
         : d3 / 2.0 * ((d6 = d5 - 2.0) * d6 * d6 + 2.0) + d2;
   };
   Easing quarticIn = (d, d2, d3, d4) -> {
      double d5;
      return d3 * (d5 = d / d4) * d5 * d5 * d5 + d2;
   };
   Easing quarticOut = (d, d2, d3, d4) -> {
      double d5;
      return -d3 * ((d5 = d / d4 - 1.0) * d5 * d5 * d5 - 1.0) + d2;
   };
   Easing quarticInOut = (d, d2, d3, d4) -> {
      double d5;
      double d6;
      return (d5 = d / (d4 / 2.0)) < 1.0
         ? d3 / 2.0 * d5 * d5 * d5 * d5 + d2
         : -d3 / 2.0 * ((d6 = d5 - 2.0) * d6 * d6 * d6 - 2.0) + d2;
   };
   Easing quinticIn = (d, d2, d3, d4) -> {
      double d5;
      return d3 * (d5 = d / d4) * d5 * d5 * d5 * d5 + d2;
   };
   Easing quinticOut = (d, d2, d3, d4) -> {
      double d5;
      return d3 * ((d5 = d / d4 - 1.0) * d5 * d5 * d5 * d5 + 1.0) + d2;
   };
   Easing quinticInOut = (d, d2, d3, d4) -> {
      double d5;
      double d6;
      return (d5 = d / (d4 / 2.0)) < 1.0
         ? d3 / 2.0 * d5 * d5 * d5 * d5 * d5 + d2
         : d3 / 2.0 * ((d6 = d5 - 2.0) * d6 * d6 * d6 * d6 + 2.0) + d2;
   };
   Easing sineIn = (d, d2, d3, d4) -> -d3 * (float)Math.cos(d / d4 * (Math.PI / 2)) + d3 + d2;
   Easing sineOut = (d, d2, d3, d4) -> d3 * (float)Math.sin(d / d4 * (Math.PI / 2)) + d2;
   Easing sineInOut = (d, d2, d3, d4) -> -d3 / 2.0 * ((float)Math.cos(Math.PI * d / d4) - 1.0F) + d2;
   Easing expoIn = (d, d2, d3, d4) -> d == 0.0
      ? d2
      : d3 * (float)Math.pow(2.0, 10.0 * (d / d4 - 1.0)) + d2;
   Easing expoOut = (d, d2, d3, d4) -> d == d4
      ? d2 + d3
      : d3 * (-((float)Math.pow(2.0, -10.0 * d / d4)) + 1.0F) + d2;
   Easing expoInOut = (d, d2, d3, d4) -> {
      if (d == 0.0) {
         return d2;
      }

      if (d == d4) {
         return d2 + d3;
      }

      double d5;
      if ((d = d / (d4 / 2.0)) < 1.0) {
         d5 = d3 / 2.0 * (float)Math.pow(2.0, 10.0 * (d - 1.0)) + d2;
      } else {
         d5 = d3 / 2.0;
         double d6 = d - 1.0;
         d5 = d5 * (-((float)Math.pow(2.0, -10.0 * (d - 1.0))) + 2.0F) + d2;
      }

      return d5;
   };
   Easing circIn = (d, d2, d3, d4) -> {
      double d5;
      return -d3 * ((float)Math.sqrt(1.0 - (d5 = d / d4) * d5) - 1.0F) + d2;
   };
   Easing circOut = (d, d2, d3, d4) -> {
      double d5;
      return d3 * (float)Math.sqrt(1.0 - (d5 = d / d4 - 1.0) * d5) + d2;
   };
   Easing circInOut = (d, d2, d3, d4) -> {
      double d5;
      double d6;
      return (d5 = d / (d4 / 2.0)) < 1.0
         ? -d3 / 2.0 * ((float)Math.sqrt(1.0 - d5 * d5) - 1.0F) + d2
         : d3 / 2.0 * ((float)Math.sqrt(1.0 - (d6 = d5 - 2.0) * d6) + 1.0F) + d2;
   };
   Easing.EasingImpl elasticIn = new Easing.EasingImplChild();
   Easing.EasingImpl elasticOut = new Easing.EasingImplChild2();
   Easing.EasingImpl elasticInOut = new Easing.EasingImplChild3();
   Easing.EasingImpl2 backIn = new Easing.Holder();
   Easing.EasingImpl2 backOut = new Easing.Holder2();
   Easing.EasingImpl2 backInOut = new Easing.Holder3();
   Easing bounceOut = (d, d2, d3, d4) -> {
      if ((d = d / d4) < 0.36363637F) {
         return d3 * 7.5625 * d * d + d2;
      } else if (d < 0.72727275F) {
         double d5;
         return d3 * (7.5625 * (d5 = d - 0.54545456F) * d5 + 0.75) + d2;
      } else {
         double d6;
         double d7;
         return d < 0.90909094F
            ? d3 * (7.5625 * (d6 = d - 0.8181818F) * d6 + 0.9375) + d2
            : d3 * (7.5625 * (d7 = d - 0.95454544F) * d7 + 0.984375) + d2;
      }
   };
   Easing bounceIn = (d, d2, d3, d4) -> d3 - bounceOut.ease(d4 - d, 0.0, d3, d4) + d2;
   Easing bounceInOut = (d, d2, d3, d4) -> d < d4 / 2.0
      ? bounceIn.ease(d * 2.0, 0.0, d3, d4) * 0.5 + d2
      : bounceOut.ease(d * 2.0 - d4, 0.0, d3, d4) * 0.5 + d3 * 0.5 + d2;

   double ease(double d5, double d6, double d7, double d8);

   abstract class EasingImpl implements Easing {
      private double amplitude;
      private double period;

      public EasingImpl() {
         this(-1.0, 0.0);
      }

      public void setAmplitude(double d5) {
         this.amplitude = d5;
      }

      public void setPeriod(double d5) {
         this.period = d5;
      }

      public EasingImpl(double d5, double d6) {
         this.amplitude = d5;
         this.period = d6;
      }

      public double getAmplitude() {
         return this.amplitude;
      }

      public double getPeriod() {
         return this.period;
      }
   }

   abstract class EasingImpl2 implements Easing {
      private double overshoot;
      public static final double DEFAULT_OVERSHOOT = 1.70158;

      public double getOvershoot() {
         return this.overshoot;
      }

      public void setOvershoot(double d5) {
         this.overshoot = d5;
      }

      public EasingImpl2(double d5) {
         this.overshoot = d5;
      }

      public EasingImpl2() {
         this(1.70158);
      }
   }

   class EasingImplChild extends Easing.EasingImpl {
      public EasingImplChild(double d5, double d6) {
         super(d5, d6);
      }

      public EasingImplChild() {
      }

      @Override
      public double ease(double d5, double d6, double d7, double d8) {
         double amplitude2 = this.getAmplitude();
         double period2 = this.getPeriod();
         if (d5 == 0.0) {
            return d6;
         }

         if ((d5 = d5 / d8) == 1.0) {
            return d6 + d7;
         }

         if (period2 == 0.0) {
            period2 = d8 * 0.3;
         }

         double d9;
         if (amplitude2 < Math.abs(d7)) {
            amplitude2 = d7;
            d9 = period2 / 4.0;
         } else {
            d9 = period2 / (Math.PI * 2) * Math.asin(d7 / amplitude2);
         }

         return -(amplitude2 * Math.pow(2.0, 10.0 * --d5) * (float)Math.sin((d5 * d8 - d9) * (Math.PI * 2) / period2)) + d6;
      }
   }

   class EasingImplChild2 extends Easing.EasingImpl {
      public EasingImplChild2(double d5, double d6) {
         super(d5, d6);
      }

      @Override
      public double ease(double d5, double d6, double d7, double d8) {
         double amplitude2 = this.getAmplitude();
         double period2 = this.getPeriod();
         if (d5 == 0.0) {
            return d6;
         }

         if ((d5 = d5 / d8) == 1.0) {
            return d6 + d7;
         }

         if (period2 == 0.0) {
            period2 = d8 * 0.3F;
         }

         double d9;
         if (amplitude2 < Math.abs(d7)) {
            amplitude2 = d7;
            d9 = period2 / 4.0;
         } else {
            d9 = period2 / (Math.PI * 2) * Math.asin(d7 / amplitude2);
         }

         return amplitude2 * Math.pow(2.0, -10.0 * d5) * Math.sin((d5 * d8 - d9) * (Math.PI * 2) / period2) + d7 + d6;
      }

      public EasingImplChild2() {
      }
   }

   class EasingImplChild3 extends Easing.EasingImpl {
      @Override
      public double ease(double d5, double d6, double d7, double d8) {
         double amplitude2 = this.getAmplitude();
         double period2 = this.getPeriod();
         if (d5 == 0.0) {
            return d6;
         }

         if ((d5 = d5 / (d8 / 2.0)) == 2.0) {
            return d6 + d7;
         }

         if (period2 == 0.0) {
            period2 = d8 * 0.45000002F;
         }

         double d9;
         if (amplitude2 < Math.abs(d7)) {
            amplitude2 = d7;
            d9 = period2 / 4.0;
         } else {
            d9 = period2 / (Math.PI * 2) * Math.asin(d7 / amplitude2);
         }

         return d5 < 1.0
            ? -0.5 * amplitude2 * Math.pow(2.0, 10.0 * --d5) * Math.sin((d5 * d8 - d9) * (Math.PI * 2) / period2) + d6
            : amplitude2 * Math.pow(2.0, -10.0 * --d5) * Math.sin((d5 * d8 - d9) * (Math.PI * 2) / period2) * 0.5 + d7 + d6;
      }

      public EasingImplChild3() {
      }

      public EasingImplChild3(double d5, double d6) {
         super(d5, d6);
      }
   }

   class Holder extends Easing.EasingImpl2 {
      @Override
      public double ease(double d5, double d6, double d7, double d8) {
         double overshoot2 = this.getOvershoot();
         double d9;
         return d7 * (d9 = d5 / d8) * d9 * ((overshoot2 + 1.0) * d9 - overshoot2) + d6;
      }

      public Holder() {
      }

      public Holder(double d5) {
         super(d5);
      }
   }

   class Holder2 extends Easing.EasingImpl2 {
      public Holder2(double d5) {
         super(d5);
      }

      @Override
      public double ease(double d5, double d6, double d7, double d8) {
         double overshoot2 = this.getOvershoot();
         double d9;
         return d7 * ((d9 = d5 / d8 - 1.0) * d9 * ((overshoot2 + 1.0) * d9 + overshoot2) + 1.0) + d6;
      }

      public Holder2() {
      }
   }

   class Holder3 extends Easing.EasingImpl2 {
      public Holder3() {
      }

      public Holder3(double d5) {
         super(d5);
      }

      @Override
      public double ease(double d5, double d6, double d7, double d8) {
         double overshoot2 = this.getOvershoot();
         double d9;
         double d10;
         double d11;
         double d12;
         return (d9 = d5 / (d8 / 2.0)) < 1.0
            ? d7 / 2.0 * d9 * d9 * (((d11 = overshoot2 * 1.525) + 1.0) * d9 - d11) + d6
            : d7 / 2.0 * ((d10 = d9 - 2.0) * d10 * (((d12 = overshoot2 * 1.525) + 1.0) * d10 + d12) + 2.0) + d6;
      }
   }
}
