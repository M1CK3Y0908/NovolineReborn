package net.optifine.expr;

import net.optifine.shaders.uniform.Smoother;

public class FunctionFloat implements IExpressionFloat {
   private int smoothId = -1;
   private FunctionType type;
   private IExpression[] arguments;

   @Override
   public String toString() {
      return this.type + "()";
   }

   @Override
   public ExpressionType getExpressionType() {
      return ExpressionType.FLOAT;
   }

   private static float evalFloat(IExpression[] exprs, int index) {
      return ((IExpressionFloat)exprs[index]).eval();
   }

   public FunctionFloat(FunctionType type, IExpression[] arguments) {
      this.type = type;
      this.arguments = arguments;
   }

   @Override
   public float eval() {
      IExpression[] arguments2 = this.arguments;
      switch (this.type) {
         case SMOOTH:
            if (!(arguments2[0] instanceof ConstantFloat)) {
               float f = evalFloat(arguments2, 0);
               float f2 = arguments2.length > 1 ? evalFloat(arguments2, 1) : 1.0F;
               float f3 = arguments2.length > 2 ? evalFloat(arguments2, 2) : f2;
               if (this.smoothId < 0) {
                  this.smoothId = Smoother.getNextId();
               }

               return Smoother.getSmoothValue(this.smoothId, f, f2, f3);
            }
         default:
            return this.type.evalFloat(this.arguments);
      }
   }
}
