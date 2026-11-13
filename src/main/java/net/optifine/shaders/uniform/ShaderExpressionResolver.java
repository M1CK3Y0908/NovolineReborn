package net.optifine.shaders.uniform;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.expr.ConstantFloat;
import net.optifine.expr.IExpression;
import net.optifine.expr.IExpressionResolver;
import net.optifine.shaders.SMCLog;

public class ShaderExpressionResolver implements IExpressionResolver {
   private Map<String, IExpression> mapExpressions = new HashMap<>();

   public boolean hasExpression(String name) {
      return this.mapExpressions.containsKey(name);
   }

   private void addParameterFloat(Map<String, IExpression> map, ShaderParameterFloat spf) {
      String[] stringArray = spf.getIndexNames1();
      if (stringArray == null) {
         map.put(spf.getName(), new ShaderParameterIndexed(spf));
      } else {
         for (int index = 0; index < stringArray.length; index++) {
            String text = stringArray[index];
            String[] stringArray1 = spf.getIndexNames2();
            if (stringArray1 == null) {
               map.put(spf.getName() + "." + text, new ShaderParameterIndexed(spf, index));
            } else {
               for (int innerIndex = 0; innerIndex < stringArray1.length; innerIndex++) {
                  String text2 = stringArray1[innerIndex];
                  map.put(spf.getName() + "." + text + "." + text2, new ShaderParameterIndexed(spf, index, innerIndex));
               }
            }
         }
      }
   }

   private void registerExpressions() {
      ShaderParameterFloat[] shaderParameterFloats = ShaderParameterFloat.values();

      for (int i = 0; i < shaderParameterFloats.length; i++) {
         ShaderParameterFloat shaderParameterFloat = shaderParameterFloats[i];
         this.addParameterFloat(this.mapExpressions, shaderParameterFloat);
      }

      ShaderParameterBool[] shaderParameterBools = ShaderParameterBool.values();

      for (int i2 = 0; i2 < shaderParameterBools.length; i2++) {
         ShaderParameterBool shaderParameterBool = shaderParameterBools[i2];
         this.mapExpressions.put(shaderParameterBool.getName(), shaderParameterBool);
      }

      for (BiomeGenBase biomeGenBase : BiomeGenBase.BIOME_ID_MAP.values()) {
         String text = biomeGenBase.biomeName.trim();
         text = "BIOME_" + text.toUpperCase().replace(' ', '_');
         int biomeID2 = biomeGenBase.biomeID;
         ConstantFloat constantFloat = new ConstantFloat(biomeID2);
         this.registerExpression(text, constantFloat);
      }
   }

   public boolean registerExpression(String name, IExpression expr) {
      if (this.mapExpressions.containsKey(name)) {
         SMCLog.warning("Expression already defined: " + name);
         return false;
      } else {
         this.mapExpressions.put(name, expr);
         return true;
      }
   }

   public ShaderExpressionResolver(Map<String, IExpression> map) {
      this.registerExpressions();

      for (String text : map.keySet()) {
         IExpression iExpression = map.get(text);
         this.registerExpression(text, iExpression);
      }
   }

   @Override
   public IExpression getExpression(String name) {
      return this.mapExpressions.get(name);
   }
}
