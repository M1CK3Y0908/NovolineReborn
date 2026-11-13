package net.optifine.shaders.config;

import java.util.Map;
import net.minecraft.src.Config;
import net.optifine.expr.ConstantFloat;
import net.optifine.expr.FunctionBool;
import net.optifine.expr.FunctionType;
import net.optifine.expr.IExpression;
import net.optifine.expr.IExpressionResolver;

public class MacroExpressionResolver implements IExpressionResolver {
   private Map<String, String> mapMacroValues = null;

   public MacroExpressionResolver(Map<String, String> mapMacroValues) {
      this.mapMacroValues = mapMacroValues;
   }

   @Override
   public IExpression getExpression(String name) {
      if (name.startsWith("defined_")) {
         String text = name.substring("defined_".length());
         return this.mapMacroValues.containsKey(text)
            ? new FunctionBool(FunctionType.TRUE, (IExpression[])null)
            : new FunctionBool(FunctionType.FALSE, (IExpression[])null);
      }

      while (this.mapMacroValues.containsKey(name)) {
         String text2 = this.mapMacroValues.get(name);
         if (text2 == null || text2.equals(name)) {
            break;
         }

         name = text2;
      }

      int i = Config.parseInt(name, Integer.MIN_VALUE);
      if (i == Integer.MIN_VALUE) {
         Config.warn("Unknown macro value: " + name);
         return new ConstantFloat(0.0F);
      } else {
         return new ConstantFloat(i);
      }
   }
}
