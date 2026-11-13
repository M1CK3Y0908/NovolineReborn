package net.optifine.entity.model.anim;

import net.minecraft.src.Config;
import net.optifine.expr.ExpressionParser;
import net.optifine.expr.IExpressionFloat;
import net.optifine.expr.ParseException;

public class ModelVariableUpdater {
   private ModelVariableFloat modelVariable;
   private IExpressionFloat expression;
   private String modelVariableName;
   private String expressionText;

   private static ParseException createParseException(ParseException parseException) {
      return parseException;
   }

   public ModelVariableUpdater(String modelVariableName, String expressionText) {
      this.modelVariableName = modelVariableName;
      this.expressionText = expressionText;
   }

   public boolean initialize(IModelResolver mr) {
      this.modelVariable = mr.getModelVariable(this.modelVariableName);
      if (this.modelVariable == null) {
         Config.warn("Model variable not found: " + this.modelVariableName);
         return false;
      }

      try {
         ExpressionParser expressionParser = new ExpressionParser(mr);
         this.expression = expressionParser.parseFloat(this.expressionText);
         return true;
      } catch (ParseException parseException) {
         Config.warn("Error parsing expression: " + this.expressionText);
         Config.warn(parseException.getClass().getName() + ": " + parseException.getMessage());
         return false;
      }
   }

   public void update() {
      float f = this.expression.eval();
      this.modelVariable.setValue(f);
   }
}
