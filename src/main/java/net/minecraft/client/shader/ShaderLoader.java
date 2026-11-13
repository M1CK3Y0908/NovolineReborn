package net.minecraft.client.shader;

import com.google.common.collect.Maps;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.util.JsonException;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.BufferUtils;

public class ShaderLoader {
   private int shaderAttachCount = 0;
   private final ShaderLoader.ShaderType shaderType;
   private int shader;
   private final String shaderFilename;

   protected static byte[] toByteArray(BufferedInputStream inputStream) throws IOException {
      byte[] byteArray;
      try {
         byteArray = IOUtils.toByteArray(inputStream);
      } finally {
         inputStream.close();
      }

      return byteArray;
   }

   private ShaderLoader(ShaderLoader.ShaderType type, int shaderId, String filename) {
      this.shaderType = type;
      this.shader = shaderId;
      this.shaderFilename = filename;
   }

   public static ShaderLoader loadShader(IResourceManager resourceManager, ShaderLoader.ShaderType type, String filename) throws IOException {
      ShaderLoader shaderLoader = type.getLoadedShaders().get(filename);
      if (shaderLoader == null) {
         ResourceLocation resourceLocation = new ResourceLocation("shaders/program/" + filename + type.getShaderExtension());
         byte[] bArray = toByteArray(new BufferedInputStream(resourceManager.getResource(resourceLocation).getInputStream()));
         ByteBuffer byteBuffer = BufferUtils.createByteBuffer(bArray.length);
         byteBuffer.put(bArray);
         byteBuffer.position(0);
         int i = OpenGlHelper.glCreateShader(type.getShaderMode());
         OpenGlHelper.glShaderSource(i, byteBuffer);
         OpenGlHelper.glCompileShader(i);
         if (OpenGlHelper.glGetShaderi(i, OpenGlHelper.GL_COMPILE_STATUS) == 0) {
            String text = StringUtils.trim(OpenGlHelper.createString2(i));
            JsonException jsonException = new JsonException("Couldn't compile " + type.getShaderName() + " program: " + text);
            jsonException.setFilename(resourceLocation.getResourcePath());
            throw jsonException;
         }

         shaderLoader = new ShaderLoader(type, i, filename);
         type.getLoadedShaders().put(filename, shaderLoader);
      }

      return shaderLoader;
   }

   public void attachShader(ShaderManager manager) {
      this.shaderAttachCount++;
      OpenGlHelper.glAttachShader(manager.getProgram(), this.shader);
   }

   public void deleteShader(ShaderManager manager) {
      this.shaderAttachCount--;
      if (this.shaderAttachCount <= 0) {
         OpenGlHelper.glDeleteShader(this.shader);
         this.shaderType.getLoadedShaders().remove(this.shaderFilename);
      }
   }

   public String getShaderFilename() {
      return this.shaderFilename;
   }

   public enum ShaderType {
      VERTEX("vertex", ".vsh", OpenGlHelper.GL_VERTEX_SHADER),
      FRAGMENT("fragment", ".fsh", OpenGlHelper.GL_FRAGMENT_SHADER);

      private final Map<String, ShaderLoader> loadedShaders = Maps.newHashMap();
      private final String shaderName;
      private final String shaderExtension;
      private final int shaderMode;

      protected int getShaderMode() {
         return this.shaderMode;
      }

      public String getShaderName() {
         return this.shaderName;
      }

      protected Map<String, ShaderLoader> getLoadedShaders() {
         return this.loadedShaders;
      }

      protected String getShaderExtension() {
         return this.shaderExtension;
      }

      ShaderType(String text, String text2, int i) {
         this.shaderName = text;
         this.shaderExtension = text2;
         this.shaderMode = i;
      }
   }
}
