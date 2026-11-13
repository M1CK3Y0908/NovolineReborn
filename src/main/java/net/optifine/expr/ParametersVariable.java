package net.optifine.expr;

import java.util.ArrayList;
import java.util.Arrays;

public class ParametersVariable implements IParameters {
   private int maxCount = Integer.MAX_VALUE;
   private static final ExpressionType[] EMPTY = new ExpressionType[0];
   private ExpressionType[] first;
   private ExpressionType[] repeat;
   private ExpressionType[] last;

   public ParametersVariable() {
      this((ExpressionType[])null, (ExpressionType[])null, (ExpressionType[])null);
   }

   private static ExpressionType[] normalize(ExpressionType[] exprs) {
      return exprs == null ? EMPTY : exprs;
   }

   public ParametersVariable first(ExpressionType... first) {
      return new ParametersVariable(first, this.repeat, this.last);
   }

   public ParametersVariable(ExpressionType[] first, ExpressionType[] repeat, ExpressionType[] last, int maxCount) {
      this.first = normalize(first);
      this.repeat = normalize(repeat);
      this.last = normalize(last);
      this.maxCount = maxCount;
   }

   public ParametersVariable getParametersVariable() {
      return new ParametersVariable(this.first, this.repeat, this.last, 4);
   }

   @Override
   public ExpressionType[] getParameterTypes(IExpression[] arguments) {
      int count = this.first.length + this.last.length;
      int count2 = arguments.length - count;
      int i = 0;

      for (int outerIndex = 0;
         outerIndex + this.repeat.length <= count2 && count + outerIndex + this.repeat.length <= this.maxCount;
         outerIndex += this.repeat.length
      ) {
         i++;
      }

      ArrayList arrayList = new ArrayList();
      arrayList.addAll(Arrays.asList(this.first));

      for (int index = 0; index < i; index++) {
         arrayList.addAll(Arrays.asList(this.repeat));
      }

      arrayList.addAll(Arrays.asList(this.last));
      return (ExpressionType[]) arrayList.toArray(new ExpressionType[arrayList.size()]);
   }

   public ParametersVariable last(ExpressionType... last) {
      return new ParametersVariable(this.first, this.repeat, last);
   }

   public ExpressionType[] getRepeat() {
      return this.repeat;
   }

   public ExpressionType[] getFirst() {
      return this.first;
   }

   public ParametersVariable repeat(ExpressionType... repeat) {
      return new ParametersVariable(this.first, repeat, this.last);
   }

   public ExpressionType[] getLast() {
      return this.last;
   }

   public ParametersVariable(ExpressionType[] first, ExpressionType[] repeat, ExpressionType[] last) {
      this(first, repeat, last, Integer.MAX_VALUE);
   }
}
