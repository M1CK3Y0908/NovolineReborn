package net.minecraft.client.shader;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.util.JsonBlendingMode;
import net.minecraft.client.util.JsonException;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShaderManager {
   private final Map<String, Object> shaderSamplers = Maps.newHashMap();
   private final List<String> samplerNames = Lists.newArrayList();
   private final List<Integer> shaderSamplerLocations = Lists.newArrayList();
   private final List<ShaderUniform> shaderUniforms = Lists.newArrayList();
   private final List<Integer> shaderUniformLocations = Lists.newArrayList();
   private static final Logger logger = LogManager.getLogger();
   private final Map<String, ShaderUniform> mappedShaderUniforms = Maps.newHashMap();
   private final String programFilename;
   private final List<Integer> attribLocations;
   private final List<String> attributes;
   private static final ShaderDefault defaultShaderUniform = new ShaderDefault();
   private final JsonBlendingMode blendingMode;
   private static ShaderManager staticShaderManager = null;
   private final boolean useFaceCulling;
   private final ShaderLoader vertexShaderLoader;
   private static int currentProgram = -1;
   private final ShaderLoader fragmentShaderLoader;
   private final int program;
   private static boolean shaderStateDirty = true;
   private boolean isDirty;

   public ShaderManager(IResourceManager resourceManager, String programName) throws IOException {
      JsonParser jsonParser = new JsonParser();
      ResourceLocation resourceLocation = new ResourceLocation("shaders/program/" + programName + ".json");
      this.programFilename = programName;
      InputStream inputStream = null;

      try {
         inputStream = resourceManager.getResource(resourceLocation).getInputStream();
         JsonObject asJsonObject = jsonParser.parse(IOUtils.toString(inputStream, Charsets.UTF_8)).getAsJsonObject();
         String string = JsonUtils.getString(asJsonObject, "vertex");
         String string2 = JsonUtils.getString(asJsonObject, "fragment");
         JsonArray jsonArray2 = JsonUtils.getJsonArray(asJsonObject, "samplers", (JsonArray)null);
         if (jsonArray2 != null) {
            int i = 0;

            for (JsonElement jsonElement : jsonArray2) {
               try {
                  this.parseSampler(jsonElement);
               } catch (Exception exception) {
                  JsonException jsonException2 = JsonException.forException(exception);
                  jsonException2.prependJsonKey("samplers[" + i + "]");
                  throw jsonException2;
               }

               i++;
            }
         }

         JsonArray jsonArray3 = JsonUtils.getJsonArray(asJsonObject, "attributes", (JsonArray)null);
         if (jsonArray3 != null) {
            int i2 = 0;
            this.attribLocations = Lists.newArrayListWithCapacity(jsonArray3.size());
            this.attributes = Lists.newArrayListWithCapacity(jsonArray3.size());

            for (JsonElement jsonElement2 : jsonArray3) {
               try {
                  this.attributes.add(JsonUtils.getString(jsonElement2, "attribute"));
               } catch (Exception exception2) {
                  JsonException jsonException3 = JsonException.forException(exception2);
                  jsonException3.prependJsonKey("attributes[" + i2 + "]");
                  throw jsonException3;
               }

               i2++;
            }
         } else {
            this.attribLocations = null;
            this.attributes = null;
         }

         JsonArray jsonArray4 = JsonUtils.getJsonArray(asJsonObject, "uniforms", (JsonArray)null);
         if (jsonArray4 != null) {
            int i3 = 0;

            for (JsonElement jsonElement3 : jsonArray4) {
               try {
                  this.parseUniform(jsonElement3);
               } catch (Exception exception3) {
                  JsonException jsonException4 = JsonException.forException(exception3);
                  jsonException4.prependJsonKey("uniforms[" + i3 + "]");
                  throw jsonException4;
               }

               i3++;
            }
         }

         this.blendingMode = JsonBlendingMode.fromJson(JsonUtils.getJsonObject(asJsonObject, "blend", (JsonObject)null));
         this.useFaceCulling = JsonUtils.getBoolean(asJsonObject, "cull", true);
         this.vertexShaderLoader = ShaderLoader.loadShader(resourceManager, ShaderLoader.ShaderType.VERTEX, string);
         this.fragmentShaderLoader = ShaderLoader.loadShader(resourceManager, ShaderLoader.ShaderType.FRAGMENT, string2);
         this.program = ShaderLinkHelper.getStaticShaderLinkHelper().createProgram();
         ShaderLinkHelper.getStaticShaderLinkHelper().linkProgram(this);
         this.setupUniforms();
         if (this.attributes != null) {
            for (String text2 : this.attributes) {
               int i4 = OpenGlHelper.glGetAttribLocation(this.program, text2);
               this.attribLocations.add(i4);
            }
         }
      } catch (Exception exception4) {
         JsonException jsonException5 = JsonException.forException(exception4);
         jsonException5.setFilename(resourceLocation.getResourcePath());
         throw jsonException5;
      } finally {
         IOUtils.closeQuietly(inputStream);
      }

      this.markDirty();
   }

   public ShaderUniform getShaderUniform(String uniformName) {
      return this.mappedShaderUniforms.containsKey(uniformName) ? this.mappedShaderUniforms.get(uniformName) : null;
   }

   private void parseUniform(JsonElement uniformElement) throws JsonException {
      JsonObject jsonObject = JsonUtils.getJsonObject(uniformElement, "uniform");
      String text = JsonUtils.getString(jsonObject, "name");
      int i = ShaderUniform.parseType(JsonUtils.getString(jsonObject, "type"));
      int intValue = JsonUtils.getInt(jsonObject, "count");
      float[] floatArray = new float[Math.max(intValue, 16)];
      JsonArray jsonArray = JsonUtils.getJsonArray(jsonObject, "values");
      if (jsonArray.size() != intValue && jsonArray.size() > 1) {
         throw new JsonException("Invalid amount of values specified (expected " + intValue + ", found " + jsonArray.size() + ")");
      }

      int i2 = 0;

      for (JsonElement jsonElement : jsonArray) {
         try {
            floatArray[i2] = JsonUtils.getFloat(jsonElement, "value");
         } catch (Exception exception) {
            JsonException jsonException = JsonException.forException(exception);
            jsonException.prependJsonKey("values[" + i2 + "]");
            throw jsonException;
         }

         i2++;
      }

      if (intValue > 1 && jsonArray.size() == 1) {
         while (i2 < intValue) {
            floatArray[i2] = floatArray[0];
            i2++;
         }
      }

      int i3 = intValue > 1 && intValue <= 4 && i < 8 ? intValue - 1 : 0;
      ShaderUniform shaderUniform = new ShaderUniform(text, i + i3, intValue, this);
      if (i <= 3) {
         shaderUniform.set((int)floatArray[0], (int)floatArray[1], (int)floatArray[2], (int)floatArray[3]);
      } else if (i <= 7) {
         shaderUniform.setFloatComponents(floatArray[0], floatArray[1], floatArray[2], floatArray[3]);
      } else {
         shaderUniform.set(floatArray);
      }

      this.shaderUniforms.add(shaderUniform);
   }

   public void endShader() {
      OpenGlHelper.glUseProgram(0);
      currentProgram = -1;
      staticShaderManager = null;
      shaderStateDirty = true;

      for (int index = 0; index < this.shaderSamplerLocations.size(); index++) {
         if (this.shaderSamplers.get(this.samplerNames.get(index)) != null) {
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit + index);
            GlStateManager.bindTexture(0);
         }
      }
   }

   public void deleteShader() {
      ShaderLinkHelper.getStaticShaderLinkHelper().deleteShader(this);
   }

   public void addSamplerTexture(String samplerName, Object samplerTexture) {
      if (this.shaderSamplers.containsKey(samplerName)) {
         this.shaderSamplers.remove(samplerName);
      }

      this.shaderSamplers.put(samplerName, samplerTexture);
      this.markDirty();
   }

   public ShaderUniform getShaderUniformOrDefault(String uniformName) {
      return this.mappedShaderUniforms.containsKey(uniformName) ? this.mappedShaderUniforms.get(uniformName) : defaultShaderUniform;
   }

   public void useShader() {
      this.isDirty = false;
      staticShaderManager = this;
      this.blendingMode.apply();
      if (this.program != currentProgram) {
         OpenGlHelper.glUseProgram(this.program);
         currentProgram = this.program;
      }

      if (this.useFaceCulling) {
         GlStateManager.enableCull();
      } else {
         GlStateManager.disableCull();
      }

      for (int i = 0; i < this.shaderSamplerLocations.size(); i++) {
         if (this.shaderSamplers.get(this.samplerNames.get(i)) != null) {
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit + i);
            GlStateManager.enableTexture2D();
            Object object = this.shaderSamplers.get(this.samplerNames.get(i));
            int i2 = -1;
            if (object instanceof Framebuffer) {
               i2 = ((Framebuffer)object).framebufferTexture;
            } else if (object instanceof ITextureObject) {
               i2 = ((ITextureObject)object).getGlTextureId();
            } else if (object instanceof Integer) {
               i2 = (Integer)object;
            }

            if (i2 != -1) {
               GlStateManager.bindTexture(i2);
               OpenGlHelper.glUniform1i(OpenGlHelper.glGetUniformLocation(this.program, this.samplerNames.get(i)), i);
            }
         }
      }

      for (ShaderUniform shaderUniform : this.shaderUniforms) {
         shaderUniform.upload();
      }
   }

   public int getProgram() {
      return this.program;
   }

   private void parseSampler(JsonElement samplerElement) throws JsonException {
      JsonObject jsonObject = JsonUtils.getJsonObject(samplerElement, "sampler");
      String text = JsonUtils.getString(jsonObject, "name");
      if (!JsonUtils.isString(jsonObject, "file")) {
         this.shaderSamplers.put(text, null);
         this.samplerNames.add(text);
      } else {
         this.samplerNames.add(text);
      }
   }

   public ShaderLoader getFragmentShaderLoader() {
      return this.fragmentShaderLoader;
   }

   private void setupUniforms() {
      int i = 0;

      for (int i2 = 0; i < this.samplerNames.size(); i2++) {
         String text2 = this.samplerNames.get(i);
         int i3 = OpenGlHelper.glGetUniformLocation(this.program, text2);
         if (i3 == -1) {
            logger.warn("Shader " + this.programFilename + "could not find sampler named " + text2 + " in the specified shader program.");
            this.shaderSamplers.remove(text2);
            this.samplerNames.remove(i2);
            i2--;
         } else {
            this.shaderSamplerLocations.add(i3);
         }

         i++;
      }

      for (ShaderUniform shaderUniform : this.shaderUniforms) {
         String shaderName = shaderUniform.getShaderName();
         int i4 = OpenGlHelper.glGetUniformLocation(this.program, shaderName);
         if (i4 == -1) {
            logger.warn("Could not find uniform named " + shaderName + " in the specified shader program.");
         } else {
            this.shaderUniformLocations.add(i4);
            shaderUniform.setUniformLocation(i4);
            this.mappedShaderUniforms.put(shaderName, shaderUniform);
         }
      }
   }

   public ShaderLoader getVertexShaderLoader() {
      return this.vertexShaderLoader;
   }

   public void markDirty() {
      this.isDirty = true;
   }
}
