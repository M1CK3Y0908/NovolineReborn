package net.optifine.expr;

import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.src.Config;
import net.minecraft.util.MathHelper;
import net.optifine.shaders.uniform.Smoother;
import net.optifine.util.MathUtils;

public enum FunctionType {
   PLUS(10, ExpressionType.FLOAT, "+", ExpressionType.FLOAT, ExpressionType.FLOAT),
   MINUS(10, ExpressionType.FLOAT, "-", ExpressionType.FLOAT, ExpressionType.FLOAT),
   MUL(11, ExpressionType.FLOAT, "*", ExpressionType.FLOAT, ExpressionType.FLOAT),
   DIV(11, ExpressionType.FLOAT, "/", ExpressionType.FLOAT, ExpressionType.FLOAT),
   MOD(11, ExpressionType.FLOAT, "%", ExpressionType.FLOAT, ExpressionType.FLOAT),
   NEG(12, ExpressionType.FLOAT, "neg", ExpressionType.FLOAT),
   PI(ExpressionType.FLOAT, "pi"),
   SIN(ExpressionType.FLOAT, "sin", ExpressionType.FLOAT),
   COS(ExpressionType.FLOAT, "cos", ExpressionType.FLOAT),
   ASIN(ExpressionType.FLOAT, "asin", ExpressionType.FLOAT),
   ACOS(ExpressionType.FLOAT, "acos", ExpressionType.FLOAT),
   TAN(ExpressionType.FLOAT, "tan", ExpressionType.FLOAT),
   ATAN(ExpressionType.FLOAT, "atan", ExpressionType.FLOAT),
   ATAN2(ExpressionType.FLOAT, "atan2", ExpressionType.FLOAT, ExpressionType.FLOAT),
   TORAD(ExpressionType.FLOAT, "torad", ExpressionType.FLOAT),
   TODEG(ExpressionType.FLOAT, "todeg", ExpressionType.FLOAT),
   MIN(ExpressionType.FLOAT, "min", new ParametersVariable().first(ExpressionType.FLOAT).repeat(ExpressionType.FLOAT)),
   MAX(ExpressionType.FLOAT, "max", new ParametersVariable().first(ExpressionType.FLOAT).repeat(ExpressionType.FLOAT)),
   CLAMP(ExpressionType.FLOAT, "clamp", ExpressionType.FLOAT, ExpressionType.FLOAT, ExpressionType.FLOAT),
   ABS(ExpressionType.FLOAT, "abs", ExpressionType.FLOAT),
   FLOOR(ExpressionType.FLOAT, "floor", ExpressionType.FLOAT),
   CEIL(ExpressionType.FLOAT, "ceil", ExpressionType.FLOAT),
   EXP(ExpressionType.FLOAT, "exp", ExpressionType.FLOAT),
   FRAC(ExpressionType.FLOAT, "frac", ExpressionType.FLOAT),
   LOG(ExpressionType.FLOAT, "log", ExpressionType.FLOAT),
   POW(ExpressionType.FLOAT, "pow", ExpressionType.FLOAT, ExpressionType.FLOAT),
   RANDOM(ExpressionType.FLOAT, "random"),
   ROUND(ExpressionType.FLOAT, "round", ExpressionType.FLOAT),
   SIGNUM(ExpressionType.FLOAT, "signum", ExpressionType.FLOAT),
   SQRT(ExpressionType.FLOAT, "sqrt", ExpressionType.FLOAT),
   FMOD(ExpressionType.FLOAT, "fmod", ExpressionType.FLOAT, ExpressionType.FLOAT),
   TIME(ExpressionType.FLOAT, "time"),
   IF(
      ExpressionType.FLOAT,
      "if",
      new ParametersVariable().first(ExpressionType.BOOL, ExpressionType.FLOAT).repeat(ExpressionType.BOOL, ExpressionType.FLOAT).last(ExpressionType.FLOAT)
   ),
   NOT(12, ExpressionType.BOOL, "!", ExpressionType.BOOL),
   AND(3, ExpressionType.BOOL, "&&", ExpressionType.BOOL, ExpressionType.BOOL),
   OR(2, ExpressionType.BOOL, "||", ExpressionType.BOOL, ExpressionType.BOOL),
   GREATER(8, ExpressionType.BOOL, ">", ExpressionType.FLOAT, ExpressionType.FLOAT),
   GREATER_OR_EQUAL(8, ExpressionType.BOOL, ">=", ExpressionType.FLOAT, ExpressionType.FLOAT),
   SMALLER(8, ExpressionType.BOOL, "<", ExpressionType.FLOAT, ExpressionType.FLOAT),
   SMALLER_OR_EQUAL(8, ExpressionType.BOOL, "<=", ExpressionType.FLOAT, ExpressionType.FLOAT),
   EQUAL(7, ExpressionType.BOOL, "==", ExpressionType.FLOAT, ExpressionType.FLOAT),
   NOT_EQUAL(7, ExpressionType.BOOL, "!=", ExpressionType.FLOAT, ExpressionType.FLOAT),
   BETWEEN(7, ExpressionType.BOOL, "between", ExpressionType.FLOAT, ExpressionType.FLOAT, ExpressionType.FLOAT),
   EQUALS(7, ExpressionType.BOOL, "equals", ExpressionType.FLOAT, ExpressionType.FLOAT, ExpressionType.FLOAT),
   IN(ExpressionType.BOOL, "in", new ParametersVariable().first(ExpressionType.FLOAT).repeat(ExpressionType.FLOAT).last(ExpressionType.FLOAT)),
   SMOOTH(ExpressionType.FLOAT, "smooth", new ParametersVariable().first(ExpressionType.FLOAT).repeat(ExpressionType.FLOAT).getParametersVariable()),
   TRUE(ExpressionType.BOOL, "true"),
   FALSE(ExpressionType.BOOL, "false"),
   VEC2(ExpressionType.FLOAT_ARRAY, "vec2", ExpressionType.FLOAT, ExpressionType.FLOAT),
   VEC3(ExpressionType.FLOAT_ARRAY, "vec3", ExpressionType.FLOAT, ExpressionType.FLOAT, ExpressionType.FLOAT),
   VEC4(ExpressionType.FLOAT_ARRAY, "vec4", ExpressionType.FLOAT, ExpressionType.FLOAT, ExpressionType.FLOAT, ExpressionType.FLOAT);

   private int precedence;
   private ExpressionType expressionType;
   private String name;
   private IParameters parameters;
   public static FunctionType[] VALUES = values();

   public String getName() {
      return this.name;
   }

   public boolean evalBool(IExpression[] args) {
      switch (this) {
         case TRUE:
            return true;
         case FALSE:
            return false;
         case NOT:
            return !evalBool(args, 0);
         case AND:
            return evalBool(args, 0) && evalBool(args, 1);
         case OR:
            return evalBool(args, 0) || evalBool(args, 1);
         case GREATER:
            return evalFloat(args, 0) > evalFloat(args, 1);
         case GREATER_OR_EQUAL:
            return evalFloat(args, 0) >= evalFloat(args, 1);
         case SMALLER:
            return evalFloat(args, 0) < evalFloat(args, 1);
         case SMALLER_OR_EQUAL:
            return evalFloat(args, 0) <= evalFloat(args, 1);
         case EQUAL:
            return evalFloat(args, 0) == evalFloat(args, 1);
         case NOT_EQUAL:
            return evalFloat(args, 0) != evalFloat(args, 1);
         case BETWEEN:
            float f = evalFloat(args, 0);
            return f >= evalFloat(args, 1) && f <= evalFloat(args, 2);
         case EQUALS:
            float f2 = evalFloat(args, 0) - evalFloat(args, 1);
            float f3 = evalFloat(args, 2);
            return Math.abs(f2) <= f3;
         case IN:
            float f4 = evalFloat(args, 0);

            for (int index = 1; index < args.length; index++) {
               float f5 = evalFloat(args, index);
               if (f4 == f5) {
                  return true;
               }
            }

            return false;
         default:
            Config.warn("Unknown function type: " + this);
            return false;
      }
   }

   public float[] evalFloatArray(IExpression[] args) {
      switch (this) {
         case VEC2:
            return new float[]{evalFloat(args, 0), evalFloat(args, 1)};
         case VEC3:
            return new float[]{evalFloat(args, 0), evalFloat(args, 1), evalFloat(args, 2)};
         case VEC4:
            return new float[]{evalFloat(args, 0), evalFloat(args, 1), evalFloat(args, 2), evalFloat(args, 3)};
         default:
            Config.warn("Unknown function type: " + this);
            return null;
      }
   }

   public int getParameterCount(IExpression[] arguments) {
      return this.parameters.getParameterTypes(arguments).length;
   }

   FunctionType(ExpressionType expressionType2, String text, IParameters iParameters) {
      this(0, expressionType2, text, iParameters);
   }

   private static boolean evalBool(IExpression[] exprs, int index) {
      return ((IExpressionBool)exprs[index]).eval();
   }

   public float evalFloat(IExpression[] args) {
      switch (this) {
         case PLUS:
            return evalFloat(args, 0) + evalFloat(args, 1);
         case MINUS:
            return evalFloat(args, 0) - evalFloat(args, 1);
         case MUL:
            return evalFloat(args, 0) * evalFloat(args, 1);
         case DIV:
            return evalFloat(args, 0) / evalFloat(args, 1);
         case MOD:
            float f = evalFloat(args, 0);
            float f2 = evalFloat(args, 1);
            return f - f2 * (int)(f / f2);
         case NEG:
            return -evalFloat(args, 0);
         case PI:
            return MathHelper.PI;
         case SIN:
            return MathHelper.sin(evalFloat(args, 0));
         case COS:
            return MathHelper.cos(evalFloat(args, 0));
         case ASIN:
            return MathUtils.asin(evalFloat(args, 0));
         case ACOS:
            return MathUtils.acos(evalFloat(args, 0));
         case TAN:
            return (float)Math.tan(evalFloat(args, 0));
         case ATAN:
            return (float)Math.atan(evalFloat(args, 0));
         case ATAN2:
            return (float)MathHelper.atan2(evalFloat(args, 0), evalFloat(args, 1));
         case TORAD:
            return MathUtils.toRad(evalFloat(args, 0));
         case TODEG:
            return MathUtils.toDeg(evalFloat(args, 0));
         case MIN:
            return this.getMin(args);
         case MAX:
            return this.getMax(args);
         case CLAMP:
            return MathHelper.clamp_float(evalFloat(args, 0), evalFloat(args, 1), evalFloat(args, 2));
         case ABS:
            return MathHelper.abs(evalFloat(args, 0));
         case EXP:
            return (float)Math.exp(evalFloat(args, 0));
         case FLOOR:
            return MathHelper.floor_float(evalFloat(args, 0));
         case CEIL:
            return MathHelper.ceiling_float_int(evalFloat(args, 0));
         case FRAC:
            return (float)MathHelper.frac(evalFloat(args, 0));
         case LOG:
            return (float)Math.log(evalFloat(args, 0));
         case POW:
            return (float)Math.pow(evalFloat(args, 0), evalFloat(args, 1));
         case RANDOM:
            return (float)Math.random();
         case ROUND:
            return Math.round(evalFloat(args, 0));
         case SIGNUM:
            return Math.signum(evalFloat(args, 0));
         case SQRT:
            return MathHelper.sqrt_float(evalFloat(args, 0));
         case FMOD:
            float f3 = evalFloat(args, 0);
            float f4 = evalFloat(args, 1);
            return f3 - f4 * MathHelper.floor_float(f3 / f4);
         case TIME:
            WorldClient theWorld2 = Minecraft.getMinecraft().theWorld;
            if (theWorld2 == null) {
               return 0.0F;
            }

            return (float)(theWorld2.getTotalWorldTime() % 24000L) + Config.renderPartialTicks;
         case IF: {
            int i = (args.length - 1) / 2;

            for (int i2 = 0; i2 < i; i2++) {
               int i3 = i2 * 2;
               if (evalBool(args, i3)) {
                  return evalFloat(args, i3 + 1);
               }
            }

            return evalFloat(args, i * 2);
         }
         case SMOOTH: {
            int i4 = (int)evalFloat(args, 0);
            float f5 = evalFloat(args, 1);
            float f6 = args.length > 2 ? evalFloat(args, 2) : 1.0F;
            float f7 = args.length > 3 ? evalFloat(args, 3) : f6;
            return Smoother.getSmoothValue(i4, f5, f6, f7);
         }
         default:
            Config.warn("Unknown function type: " + this);
            return 0.0F;
      }
   }

   static {
      new HashMap();
   }

   public ExpressionType getExpressionType() {
      return this.expressionType;
   }

   public static FunctionType parse(String str) {
      for (int i = 0; i < VALUES.length; i++) {
         FunctionType functionType = VALUES[i];
         if (functionType.getName().equals(str)) {
            return functionType;
         }
      }

      return null;
   }

   public ExpressionType[] getParameterTypes(IExpression[] arguments) {
      return this.parameters.getParameterTypes(arguments);
   }

   private float getMin(IExpression[] exprs) {
      if (exprs.length == 2) {
         return Math.min(evalFloat(exprs, 0), evalFloat(exprs, 1));
      }

      float f = evalFloat(exprs, 0);

      for (int index = 1; index < exprs.length; index++) {
         float f2 = evalFloat(exprs, index);
         if (f2 < f) {
            f = f2;
         }
      }

      return f;
   }

   public IParameters getParameters() {
      return this.parameters;
   }

   FunctionType(int i, ExpressionType expressionType2, String text, ExpressionType... expressionTypes) {
      this(i, expressionType2, text, new Parameters(expressionTypes));
   }

   private float getMax(IExpression[] exprs) {
      if (exprs.length == 2) {
         return Math.max(evalFloat(exprs, 0), evalFloat(exprs, 1));
      }

      float f = evalFloat(exprs, 0);

      for (int index = 1; index < exprs.length; index++) {
         float f2 = evalFloat(exprs, index);
         if (f2 > f) {
            f = f2;
         }
      }

      return f;
   }

   FunctionType(int i, ExpressionType expressionType2, String text, IParameters iParameters) {
      this.precedence = i;
      this.expressionType = expressionType2;
      this.name = text;
      this.parameters = iParameters;
   }

   FunctionType(ExpressionType expressionType2, String text, ExpressionType... expressionTypes) {
      this(0, expressionType2, text, expressionTypes);
   }

   public int getPrecedence() {
      return this.precedence;
   }

   private static float evalFloat(IExpression[] exprs, int index) {
      return ((IExpressionFloat)exprs[index]).eval();
   }
}
