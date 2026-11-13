package net.optifine.shaders.uniform;

import java.util.ArrayList;
import java.util.Map;
import net.optifine.expr.IExpression;
import net.optifine.expr.IExpressionCached;

public class CustomUniforms {
   private CustomUniform[] uniforms;
   private IExpressionCached[] expressionsCached;

   private void resetCache() {
      for (int i = 0; i < this.expressionsCached.length; i++) {
         this.expressionsCached[i].reset();
      }
   }

   public void reset() {
      for (int i = 0; i < this.uniforms.length; i++) {
         this.uniforms[i].reset();
      }
   }

   public void setProgram(int program) {
      for (int i = 0; i < this.uniforms.length; i++) {
         this.uniforms[i].setProgram(program);
      }
   }

   public CustomUniforms(CustomUniform[] uniforms, Map<String, IExpression> mapExpressions) {
      this.uniforms = uniforms;
      ArrayList arrayList = new ArrayList();

      for (String text : mapExpressions.keySet()) {
         IExpression iExpression = mapExpressions.get(text);
         if (iExpression instanceof IExpressionCached iExpressionCached) {
            arrayList.add(iExpressionCached);
         }
      }

      this.expressionsCached = (IExpressionCached[]) arrayList.toArray(new IExpressionCached[arrayList.size()]);
   }

   public void update() {
      this.resetCache();

      for (int i = 0; i < this.uniforms.length; i++) {
         this.uniforms[i].update();
      }
   }
}
