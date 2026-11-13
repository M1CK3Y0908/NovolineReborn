package net.minecraft.client.shader;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.util.JsonException;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

public class ShaderGroup {
   private final List<Shader> listShaders = Lists.newArrayList();
   private final Map<String, Framebuffer> mapFramebuffers = Maps.newHashMap();
   private final List<Framebuffer> listFramebuffers = Lists.newArrayList();
   private IResourceManager resourceManager;
   private Framebuffer mainFramebuffer;
   private float accumulatedTime;
   private float lastPartialTicks;
   private int mainFramebufferWidth;
   private int mainFramebufferHeight;
   private String shaderGroupName;
   private Matrix4f projectionMatrix;

   public void loadShaderGroup(float partialTicks) {
      if (partialTicks < this.lastPartialTicks) {
         this.accumulatedTime = this.accumulatedTime + (1.0F - this.lastPartialTicks);
         this.accumulatedTime += partialTicks;
      } else {
         this.accumulatedTime = this.accumulatedTime + (partialTicks - this.lastPartialTicks);
      }

      this.lastPartialTicks = partialTicks;

      while (this.accumulatedTime > 20.0F) {
         this.accumulatedTime -= 20.0F;
      }

      Iterator iterator2 = this.listShaders.iterator();

      while (iterator2.hasNext()) {
         ((Shader)iterator2.next()).loadShader(this.accumulatedTime / 20.0F);
      }
   }

   public Shader addShader(String programName, Framebuffer framebufferIn, Framebuffer framebufferOut) throws IOException {
      Shader shader2 = new Shader(this.resourceManager, programName, framebufferIn, framebufferOut);
      this.listShaders.add(this.listShaders.size(), shader2);
      return shader2;
   }

   private void initTarget(JsonElement targetElement) throws JsonException {
      if (JsonUtils.isString(targetElement)) {
         this.addFramebuffer(targetElement.getAsString(), this.mainFramebufferWidth, this.mainFramebufferHeight);
      } else {
         JsonObject jsonObject = JsonUtils.getJsonObject(targetElement, "target");
         String text = JsonUtils.getString(jsonObject, "name");
         int intValue = JsonUtils.getInt(jsonObject, "width", this.mainFramebufferWidth);
         int intValue2 = JsonUtils.getInt(jsonObject, "height", this.mainFramebufferHeight);
         if (this.mapFramebuffers.containsKey(text)) {
            throw new JsonException(text + " is already defined");
         }

         this.addFramebuffer(text, intValue, intValue2);
      }
   }

   public final String getShaderGroupName() {
      return this.shaderGroupName;
   }

   public Framebuffer getFramebufferRaw(String framebufferName) {
      return this.mapFramebuffers.get(framebufferName);
   }

   private void initUniform(JsonElement uniformElement) throws JsonException {
      JsonObject jsonObject = JsonUtils.getJsonObject(uniformElement, "uniform");
      String text = JsonUtils.getString(jsonObject, "name");
      ShaderUniform shaderUniform = this.listShaders.get(this.listShaders.size() - 1).getShaderManager().getShaderUniform(text);
      if (shaderUniform == null) {
         throw new JsonException("Uniform '" + text + "' does not exist");
      }

      float[] floatArray = new float[4];
      int i = 0;

      for (JsonElement jsonElement : JsonUtils.getJsonArray(jsonObject, "values")) {
         try {
            floatArray[i] = JsonUtils.getFloat(jsonElement, "value");
         } catch (Exception exception) {
            JsonException jsonException = JsonException.forException(exception);
            jsonException.prependJsonKey("values[" + i + "]");
            throw jsonException;
         }

         i++;
      }

      switch (i) {
         case 0:
         default:
            break;
         case 1:
            shaderUniform.set(floatArray[0]);
            break;
         case 2:
            shaderUniform.set(floatArray[0], floatArray[1]);
            break;
         case 3:
            shaderUniform.set(floatArray[0], floatArray[1], floatArray[2]);
            break;
         case 4:
            shaderUniform.set(floatArray[0], floatArray[1], floatArray[2], floatArray[3]);
      }
   }

   public ShaderGroup(TextureManager textureManager, IResourceManager resourceManager, Framebuffer mainFramebuffer, ResourceLocation shaderGroupLocation) throws IOException, JsonSyntaxException {
      this.resourceManager = resourceManager;
      this.mainFramebuffer = mainFramebuffer;
      this.accumulatedTime = 0.0F;
      this.lastPartialTicks = 0.0F;
      this.mainFramebufferWidth = mainFramebuffer.framebufferWidth;
      this.mainFramebufferHeight = mainFramebuffer.framebufferHeight;
      this.shaderGroupName = shaderGroupLocation.toString();
      this.resetProjectionMatrix();
      this.parseGroup(textureManager, shaderGroupLocation);
   }

   public void parseGroup(TextureManager textureManager, ResourceLocation shaderGroupLocation) throws IOException, JsonSyntaxException {
      JsonParser jsonParser = new JsonParser();
      InputStream inputStream = null;

      try {
         inputStream = this.resourceManager.getResource(shaderGroupLocation).getInputStream();
         JsonObject asJsonObject = jsonParser.parse(IOUtils.toString(inputStream, Charsets.UTF_8)).getAsJsonObject();
         if (JsonUtils.isJsonArray(asJsonObject, "targets")) {
            JsonArray asJsonArray = asJsonObject.getAsJsonArray("targets");
            int i = 0;

            for (JsonElement jsonElement : asJsonArray) {
               try {
                  this.initTarget(jsonElement);
               } catch (Exception exception) {
                  JsonException jsonException2 = JsonException.forException(exception);
                  jsonException2.prependJsonKey("targets[" + i + "]");
                  throw jsonException2;
               }

               i++;
            }
         }

         if (JsonUtils.isJsonArray(asJsonObject, "passes")) {
            JsonArray asJsonArray2 = asJsonObject.getAsJsonArray("passes");
            int i2 = 0;

            for (JsonElement jsonElement2 : asJsonArray2) {
               try {
                  this.parsePass(textureManager, jsonElement2);
               } catch (Exception exception2) {
                  JsonException jsonException3 = JsonException.forException(exception2);
                  jsonException3.prependJsonKey("passes[" + i2 + "]");
                  throw jsonException3;
               }

               i2++;
            }
         }
      } catch (Exception exception3) {
         JsonException jsonException4 = JsonException.forException(exception3);
         jsonException4.setFilename(shaderGroupLocation.getResourcePath());
         throw jsonException4;
      } finally {
         IOUtils.closeQuietly(inputStream);
      }
   }

   private Framebuffer getFramebuffer(String framebufferName) {
      return framebufferName == null ? null : (framebufferName.equals("minecraft:main") ? this.mainFramebuffer : this.mapFramebuffers.get(framebufferName));
   }

   private void parsePass(TextureManager textureManager, JsonElement passElement) throws IOException {
      JsonObject jsonObject2 = JsonUtils.getJsonObject(passElement, "pass");
      String string = JsonUtils.getString(jsonObject2, "name");
      String string2 = JsonUtils.getString(jsonObject2, "intarget");
      String string3 = JsonUtils.getString(jsonObject2, "outtarget");
      Framebuffer framebuffer = this.getFramebuffer(string2);
      Framebuffer framebuffer2 = this.getFramebuffer(string3);
      if (framebuffer == null) {
         throw new JsonException("Input target '" + string2 + "' does not exist");
      }

      if (framebuffer2 == null) {
         throw new JsonException("Output target '" + string3 + "' does not exist");
      }

      Shader shader2 = this.addShader(string, framebuffer, framebuffer2);
      JsonArray jsonArray = JsonUtils.getJsonArray(jsonObject2, "auxtargets", (JsonArray)null);
      if (jsonArray != null) {
         int i = 0;

         for (JsonElement jsonElement : jsonArray) {
            try {
               JsonObject jsonObject3 = JsonUtils.getJsonObject(jsonElement, "auxtarget");
               String string4 = JsonUtils.getString(jsonObject3, "name");
               String string5 = JsonUtils.getString(jsonObject3, "id");
               Framebuffer framebuffer3 = this.getFramebuffer(string5);
               if (framebuffer3 == null) {
                  ResourceLocation resourceLocation = new ResourceLocation("textures/effect/" + string5 + ".png");

                  try {
                     this.resourceManager.getResource(resourceLocation);
                  } catch (FileNotFoundException fileNotFoundException) {
                     throw new JsonException("Render target or texture '" + string5 + "' does not exist");
                  }

                  textureManager.bindTexture(resourceLocation);
                  ITextureObject texture2 = textureManager.getTexture(resourceLocation);
                  int intValue = JsonUtils.getInt(jsonObject3, "width");
                  int intValue2 = JsonUtils.getInt(jsonObject3, "height");
                  if (JsonUtils.getBoolean(jsonObject3, "bilinear")) {
                     GL11.glTexParameteri(3553, 10241, 9729);
                     GL11.glTexParameteri(3553, 10240, 9729);
                  } else {
                     GL11.glTexParameteri(3553, 10241, 9728);
                     GL11.glTexParameteri(3553, 10240, 9728);
                  }

                  shader2.addAuxFramebuffer(string4, texture2.getGlTextureId(), intValue, intValue2);
               } else {
                  shader2.addAuxFramebuffer(string4, framebuffer3, framebuffer3.framebufferTextureWidth, framebuffer3.framebufferTextureHeight);
               }
            } catch (Exception exception) {
               JsonException jsonException2 = JsonException.forException(exception);
               jsonException2.prependJsonKey("auxtargets[" + i + "]");
               throw jsonException2;
            }

            i++;
         }
      }

      JsonArray jsonArray2 = JsonUtils.getJsonArray(jsonObject2, "uniforms", (JsonArray)null);
      if (jsonArray2 != null) {
         int i2 = 0;

         for (JsonElement jsonElement2 : jsonArray2) {
            try {
               this.initUniform(jsonElement2);
            } catch (Exception exception2) {
               JsonException jsonException3 = JsonException.forException(exception2);
               jsonException3.prependJsonKey("uniforms[" + i2 + "]");
               throw jsonException3;
            }

            i2++;
         }
      }
   }

   private void resetProjectionMatrix() {
      this.projectionMatrix = new Matrix4f();
      this.projectionMatrix.setIdentity();
      this.projectionMatrix.m00 = 2.0F / this.mainFramebuffer.framebufferTextureWidth;
      this.projectionMatrix.m11 = 2.0F / -this.mainFramebuffer.framebufferTextureHeight;
      this.projectionMatrix.m22 = -0.0020001999F;
      this.projectionMatrix.m33 = 1.0F;
      this.projectionMatrix.m03 = -1.0F;
      this.projectionMatrix.m13 = 1.0F;
      this.projectionMatrix.m23 = -1.0001999F;
   }

   public void createBindFramebuffers(int width, int height) {
      this.mainFramebufferWidth = this.mainFramebuffer.framebufferTextureWidth;
      this.mainFramebufferHeight = this.mainFramebuffer.framebufferTextureHeight;
      this.resetProjectionMatrix();
      Iterator iterator2 = this.listShaders.iterator();

      while (iterator2.hasNext()) {
         ((Shader)iterator2.next()).setProjectionMatrix(this.projectionMatrix);
      }

      iterator2 = this.listFramebuffers.iterator();

      while (iterator2.hasNext()) {
         ((Framebuffer)iterator2.next()).createBindFramebuffer(width, height);
      }
   }

   public void addFramebuffer(String framebufferName, int width, int height) {
      Framebuffer framebuffer = new Framebuffer(width, height, true);
      framebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
      this.mapFramebuffers.put(framebufferName, framebuffer);
      if (width == this.mainFramebufferWidth && height == this.mainFramebufferHeight) {
         this.listFramebuffers.add(framebuffer);
      }
   }

   public void deleteShaderGroup() {
      Iterator iterator2 = this.mapFramebuffers.values().iterator();

      while (iterator2.hasNext()) {
         ((Framebuffer)iterator2.next()).deleteFramebuffer();
      }

      iterator2 = this.listShaders.iterator();

      while (iterator2.hasNext()) {
         ((Shader)iterator2.next()).deleteShader();
      }

      this.listShaders.clear();
   }
}
