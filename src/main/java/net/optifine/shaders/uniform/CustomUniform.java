package net.optifine.shaders.uniform;

import net.optifine.expr.IExpression;
import net.optifine.shaders.SMCLog;

public class CustomUniform {
   private String name;
   private UniformType type;
   private IExpression expression;
   private ShaderUniformBase shaderUniform;

   public CustomUniform(String name, UniformType type, IExpression expression) {
      this.name = name;
      this.type = type;
      this.expression = expression;
      this.shaderUniform = type.makeShaderUniform(name);
   }

   public void update() {
      if (this.shaderUniform.isDefined()) {
         try {
            this.type.updateUniform(this.expression, this.shaderUniform);
         } catch (RuntimeException runtimeException) {
            SMCLog.severe("Error updating custom uniform: " + this.shaderUniform.getName());
            SMCLog.severe(runtimeException.getClass().getName() + ": " + runtimeException.getMessage());
            this.shaderUniform.disable();
            SMCLog.severe("Custom uniform disabled: " + this.shaderUniform.getName());
         }
      }
   }

   public UniformType getType() {
      return this.type;
   }

   public void reset() {
      this.shaderUniform.reset();
   }

   @Override
   public String toString() {
      return this.type.name().toLowerCase() + " " + this.name;
   }

   public ShaderUniformBase getShaderUniform() {
      return this.shaderUniform;
   }

   public IExpression getExpression() {
      return this.expression;
   }

   public void setProgram(int program) {
      this.shaderUniform.setProgram(program);
   }

   public String getName() {
      return this.name;
   }
}
