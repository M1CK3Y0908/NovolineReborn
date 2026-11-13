package net.optifine.shaders.config;

import java.util.HashMap;
import java.util.Map;
import net.optifine.expr.IExpression;
import net.optifine.expr.IExpressionResolver;

public class ShaderOptionResolver implements IExpressionResolver {
   private Map<String, ExpressionShaderOptionSwitch> mapOptions = new HashMap<>();

   @Override
   public IExpression getExpression(String name) {
      return this.mapOptions.get(name);
   }

   public ShaderOptionResolver(ShaderOption[] options) {
      for (int index = 0; index < options.length; index++) {
         ShaderOption shaderOption = options[index];
         if (shaderOption instanceof ShaderOptionSwitch shaderOptionSwitch) {
            ExpressionShaderOptionSwitch expressionShaderOptionSwitch = new ExpressionShaderOptionSwitch(shaderOptionSwitch);
            this.mapOptions.put(shaderOption.getName(), expressionShaderOptionSwitch);
         }
      }
   }
}
