package net.optifine.expr;

public class ConstantFloat implements IExpressionFloat {
   private float value;

   @Override
   public ExpressionType getExpressionType() {
      return ExpressionType.FLOAT;
   }

   @Override
   public float eval() {
      return this.value;
   }

   @Override
   public String toString() {
      return this.value + "";
   }

   public ConstantFloat(float value) {
      this.value = value;
   }
}
