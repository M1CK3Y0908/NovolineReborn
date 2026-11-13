package net.optifine.expr;

public class ExpressionFloatArrayCached implements IExpressionFloatArray, IExpressionCached {
   private IExpressionFloatArray expression;
   private float[] value;
   private boolean cached;

   public ExpressionFloatArrayCached(IExpressionFloatArray expression) {
      this.expression = expression;
   }

   @Override
   public void reset() {
      this.cached = false;
   }

   @Override
   public ExpressionType getExpressionType() {
      return ExpressionType.FLOAT;
   }

   @Override
   public float[] eval() {
      if (!this.cached) {
         this.value = this.expression.eval();
         this.cached = true;
      }

      return this.value;
   }

   @Override
   public String toString() {
      return "cached(" + this.expression + ")";
   }
}
