package net.minecraft.client.renderer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.src.Config;
import net.optifine.SmartAnimations;
import net.optifine.render.GlAlphaState;
import net.optifine.render.GlBlendState;
import net.optifine.shaders.Shaders;
import net.optifine.util.LockCounter;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

public class GlStateManager {
   private static GlStateManager.AlphaState alphaState = new GlStateManager.AlphaState();
   private static GlStateManager.BooleanState lightingState = new GlStateManager.BooleanState(2896);
   private static GlStateManager.BooleanState[] lightState = new GlStateManager.BooleanState[8];
   private static GlStateManager.ColorMaterialState colorMaterialState = new GlStateManager.ColorMaterialState();
   private static GlStateManager.BlendState blendState = new GlStateManager.BlendState();
   private static GlStateManager.DepthState depthState = new GlStateManager.DepthState();
   private static GlStateManager.FogState fogState = new GlStateManager.FogState();
   private static GlStateManager.CullState cullState = new GlStateManager.CullState();
   private static GlStateManager.PolygonOffsetState polygonOffsetState = new GlStateManager.PolygonOffsetState();
   private static GlStateManager.ColorLogicState colorLogicState = new GlStateManager.ColorLogicState();
   private static GlStateManager.TexGenState texGenState = new GlStateManager.TexGenState();
   private static GlStateManager.ClearState clearState = new GlStateManager.ClearState();
   private static GlStateManager.StencilState stencilState = new GlStateManager.StencilState();
   private static GlStateManager.BooleanState normalizeState = new GlStateManager.BooleanState(2977);
   private static int activeTextureUnit = 0;
   private static GlStateManager.TextureState[] textureState = new GlStateManager.TextureState[32];
   private static int activeShadeModel = 7425;
   private static GlStateManager.BooleanState rescaleNormalState = new GlStateManager.BooleanState(32826);
   private static GlStateManager.ColorMask colorMaskState = new GlStateManager.ColorMask();
   private static GlStateManager.Color colorState = new GlStateManager.Color();
   public static boolean clearEnabled = true;
   private static LockCounter alphaLock = new LockCounter();
   private static GlAlphaState alphaLockState = new GlAlphaState();
   private static LockCounter blendLock = new LockCounter();
   private static GlBlendState blendLockState = new GlBlendState();
   private static boolean creatingDisplayList = false;

   public static void enableColorMaterial() {
      colorMaterialState.colorMaterial.setEnabled();
   }

   public static void enableDepth() {
      depthState.depthTest.setEnabled();
   }

   public static void matrixMode(int mode) {
      GL11.glMatrixMode(mode);
   }

   public static void unlockBlend() {
      if (blendLock.unlock()) {
         setBlendState(blendLockState);
      }
   }

   public static void pushMatrix() {
      GL11.glPushMatrix();
   }

   public static void alphaFunc(int func, float ref) {
      if (alphaLock.isLocked()) {
         alphaLockState.setFuncRef(func, ref);
      } else if (func != alphaState.func || ref != alphaState.ref) {
         alphaState.func = func;
         alphaState.ref = ref;
         GL11.glAlphaFunc(func, ref);
      }
   }

   public static void disableRescaleNormal() {
      rescaleNormalState.setDisabled();
   }

   public static void popAttrib() {
      GL11.glPopAttrib();
   }

   public static void setAlphaState(GlAlphaState alphaStateIn) {
      if (alphaLock.isLocked()) {
         alphaLockState.setState(alphaStateIn);
      } else {
         alphaState.alphaTest.setState(alphaStateIn.isEnabled());
         alphaFunc(alphaStateIn.getFunc(), alphaStateIn.getRef());
      }
   }

   public static int getBoundTexture() {
      return textureState[activeTextureUnit].textureName;
   }

   public static void translate(double x, double y, double z) {
      GL11.glTranslated(x, y, z);
   }

   public static void disableDepth() {
      depthState.depthTest.setDisabled();
   }

   public static void depthFunc(int depthFunc) {
      if (depthFunc != depthState.depthFunc) {
         depthState.depthFunc = depthFunc;
         GL11.glDepthFunc(depthFunc);
      }
   }

   public static void handleSType(GlStateManager.SType sType) {
      texGenCoord(sType).textureGen.setDisabled();
   }

   public static void disableLight(int light) {
      lightState[light].setDisabled();
   }

   public static void enableAlpha() {
      if (alphaLock.isLocked()) {
         alphaLockState.setEnabled();
      } else {
         alphaState.alphaTest.setEnabled();
      }
   }

   public static void getBlendState(GlBlendState blendStateIn) {
      if (blendLock.isLocked()) {
         blendStateIn.setState(blendLockState);
      } else {
         blendStateIn.setState(blendState.blend.currentState, blendState.srcFactor, blendState.dstFactor, blendState.srcFactorAlpha, blendState.dstFactorAlpha);
      }
   }

   public static void callList(int list) {
      GL11.glCallList(list);
      if (Config.isShaders() && !creatingDisplayList) {
         int countInstances = Shaders.activeProgram.getCountInstances();
         if (countInstances > 1) {
            for (int innerIndex = 1; innerIndex < countInstances; innerIndex++) {
               Shaders.uniform_instanceId.setValue(innerIndex);
               GL11.glCallList(list);
            }

            Shaders.uniform_instanceId.setValue(0);
         }
      }
   }

   static {
      for (int i = 0; i < 8; i++) {
         lightState[i] = new GlStateManager.BooleanState(16384 + i);
      }

      for (int j = 0; j < textureState.length; j++) {
         textureState[j] = new GlStateManager.TextureState();
      }
   }

   public static void enableBlend() {
      if (blendLock.isLocked()) {
         blendLockState.setEnabled();
      } else {
         blendState.blend.setEnabled();
      }
   }

   public static void clearDepth(double depth) {
      if (depth != clearState.depth) {
         clearState.depth = depth;
         GL11.glClearDepth(depth);
      }
   }

   public static void setActiveTexture(int texture) {
      if (activeTextureUnit != texture - OpenGlHelper.defaultTexUnit) {
         activeTextureUnit = texture - OpenGlHelper.defaultTexUnit;
         OpenGlHelper.setActiveTexture(texture);
      }
   }

   public static void translate(float x, float y, float z) {
      GL11.glTranslatef(x, y, z);
   }

   public static void disablePolygonOffset() {
      polygonOffsetState.polygonOffsetFill.setDisabled();
   }

   public static void run2() {
      GL11.glVertexPointer(3, 5126, 12, 0L);
   }

   public static void enableCull() {
      cullState.cullFace.setEnabled();
   }

   public static void disableCull() {
      cullState.cullFace.setDisabled();
   }

   public static void enablePolygonOffset() {
      polygonOffsetState.polygonOffsetFill.setEnabled();
   }

   public static void color(float colorRed, float colorGreen, float colorBlue) {
      color(colorRed, colorGreen, colorBlue, 1.0F);
   }

   public static void getAlphaState(GlAlphaState alphaStateIn) {
      if (alphaLock.isLocked()) {
         alphaStateIn.setState(alphaLockState);
      } else {
         alphaStateIn.setState(alphaState.alphaTest.currentState, alphaState.func, alphaState.ref);
      }
   }

   public static void lockAlpha(GlAlphaState alphaStateIn) {
      if (!alphaLock.isLocked()) {
         getAlphaState(alphaLockState);
         setAlphaState(alphaStateIn);
         alphaLock.lock();
      }
   }

   public static void enableNormalize() {
      normalizeState.setEnabled();
   }

   public static void colorMask(boolean red, boolean green, boolean blue, boolean alpha) {
      if (red != colorMaskState.red || green != colorMaskState.green || blue != colorMaskState.blue || alpha != colorMaskState.alpha) {
         colorMaskState.red = red;
         colorMaskState.green = green;
         colorMaskState.blue = blue;
         colorMaskState.alpha = alpha;
         GL11.glColorMask(red, green, blue, alpha);
      }
   }

   public static void setFogStart(float param) {
      if (param != fogState.start) {
         fogState.start = param;
         GL11.glFogf(2915, param);
      }
   }

   public static void handleSType2(GlStateManager.SType sType) {
      texGenCoord(sType).textureGen.setEnabled();
   }

   public static void pushAttrib() {
      GL11.glPushAttrib(8256);
   }

   public static boolean isFogEnabled() {
      return fogState.fog.currentState;
   }

   public static void handleInt(int i2, int i3, IntBuffer intBuffer) {
      GL11.glTexImage2D(32868, 0, 6408, i2, i3, 0, 6408, 5121, intBuffer);
   }

   public static void color(float colorRed, float colorGreen, float colorBlue, float colorAlpha) {
      if (colorRed != colorState.red || colorGreen != colorState.green || colorBlue != colorState.blue || colorAlpha != colorState.alpha) {
         colorState.red = colorRed;
         colorState.green = colorGreen;
         colorState.blue = colorBlue;
         colorState.alpha = colorAlpha;
         GL11.glColor4f(colorRed, colorGreen, colorBlue, colorAlpha);
      }
   }

   public static void colorMaterial(int face, int mode) {
      if (face != colorMaterialState.face || mode != colorMaterialState.mode) {
         colorMaterialState.face = face;
         colorMaterialState.mode = mode;
         GL11.glColorMaterial(face, mode);
      }
   }

   public static void disableAlpha() {
      if (alphaLock.isLocked()) {
         alphaLockState.setDisabled();
      } else {
         alphaState.alphaTest.setDisabled();
      }
   }

   public static void lockBlend(GlBlendState blendStateIn) {
      if (!blendLock.isLocked()) {
         getBlendState(blendLockState);
         setBlendState(blendStateIn);
         blendLock.lock();
      }
   }

   public static void shadeModel(int mode) {
      if (mode != activeShadeModel) {
         activeShadeModel = mode;
         GL11.glShadeModel(mode);
      }
   }

   public static void enableFog() {
      fogState.fog.setEnabled();
   }

   public static void deleteTextures(IntBuffer textures) {
      textures.rewind();

      while (textures.position() < textures.limit()) {
         deleteTexture(textures.get());
      }

      textures.rewind();
   }

   public static void colorLogicOp(int opcode) {
      if (opcode != colorLogicState.opcode) {
         colorLogicState.opcode = opcode;
         GL11.glLogicOp(opcode);
      }
   }

   public static void viewport(int x, int y, int width, int height) {
      GL11.glViewport(x, y, width, height);
   }

   public static int getActiveTextureUnit() {
      return OpenGlHelper.defaultTexUnit + activeTextureUnit;
   }

   public static void enableTexture2D() {
      textureState[activeTextureUnit].texture2DState.setEnabled();
   }

   public static void tryBlendFuncSeparate(int srcFactor, int dstFactor, int srcFactorAlpha, int dstFactorAlpha) {
      if (blendLock.isLocked()) {
         blendLockState.setFactors(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
      } else if (srcFactor != blendState.srcFactor
         || dstFactor != blendState.dstFactor
         || srcFactorAlpha != blendState.srcFactorAlpha
         || dstFactorAlpha != blendState.dstFactorAlpha) {
         blendState.srcFactor = srcFactor;
         blendState.dstFactor = dstFactor;
         blendState.srcFactorAlpha = srcFactorAlpha;
         blendState.dstFactorAlpha = dstFactorAlpha;
         if (Config.isShaders()) {
            Shaders.uniform_blendFunc.setValue(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
         }

         OpenGlHelper.glBlendFunc(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
      }
   }

   public static void doPolygonOffset(float factor, float units) {
      if (factor != polygonOffsetState.factor || units != polygonOffsetState.units) {
         polygonOffsetState.factor = factor;
         polygonOffsetState.units = units;
         GL11.glPolygonOffset(factor, units);
      }
   }

   public static void callLists(IntBuffer buffer) {
      GL11.glCallLists(buffer);
      if (Config.isShaders() && !creatingDisplayList) {
         int countInstances = Shaders.activeProgram.getCountInstances();
         if (countInstances > 1) {
            for (int innerIndex = 1; innerIndex < countInstances; innerIndex++) {
               Shaders.uniform_instanceId.setValue(innerIndex);
               GL11.glCallLists(buffer);
            }

            Shaders.uniform_instanceId.setValue(0);
         }
      }
   }

   public static void resetColor() {
      colorState.red = colorState.green = colorState.blue = colorState.alpha = -1.0F;
   }

   public static void glMultiDrawArrays(int mode, IntBuffer first, IntBuffer count) {
      GL14.glMultiDrawArrays(mode, first, count);
      if (Config.isShaders() && !creatingDisplayList) {
         int countInstances = Shaders.activeProgram.getCountInstances();
         if (countInstances > 1) {
            for (int innerIndex = 1; innerIndex < countInstances; innerIndex++) {
               Shaders.uniform_instanceId.setValue(innerIndex);
               GL14.glMultiDrawArrays(mode, first, count);
            }

            Shaders.uniform_instanceId.setValue(0);
         }
      }
   }

   public static void disableFog() {
      fogState.fog.setDisabled();
   }

   public static void bindCurrentTexture() {
      GL11.glBindTexture(3553, textureState[activeTextureUnit].textureName);
   }

   public static void blendFunc(int srcFactor, int dstFactor) {
      if (blendLock.isLocked()) {
         blendLockState.setFactors(srcFactor, dstFactor);
      } else if (srcFactor != blendState.srcFactor
         || dstFactor != blendState.dstFactor
         || srcFactor != blendState.srcFactorAlpha
         || dstFactor != blendState.dstFactorAlpha) {
         blendState.srcFactor = srcFactor;
         blendState.dstFactor = dstFactor;
         blendState.srcFactorAlpha = srcFactor;
         blendState.dstFactorAlpha = dstFactor;
         if (Config.isShaders()) {
            Shaders.uniform_blendFunc.setValue(srcFactor, dstFactor, srcFactor, dstFactor);
         }

         GL11.glBlendFunc(srcFactor, dstFactor);
      }
   }

   public static void scale(double x, double y, double z) {
      GL11.glScaled(x, y, z);
   }

   public static void disableLighting() {
      lightingState.setDisabled();
   }

   public static void depthMask(boolean flagIn) {
      if (flagIn != depthState.maskEnabled) {
         depthState.maskEnabled = flagIn;
         GL11.glDepthMask(flagIn);
      }
   }

   public static void disableTexture2D() {
      textureState[activeTextureUnit].texture2DState.setDisabled();
   }

   public static void popMatrix() {
      GL11.glPopMatrix();
   }

   public static void loadIdentity() {
      GL11.glLoadIdentity();
   }

   public static void multMatrix(FloatBuffer matrix) {
      GL11.glMultMatrix(matrix);
   }

   public static void setFogEnd(float param) {
      if (param != fogState.end) {
         fogState.end = param;
         GL11.glFogf(2916, param);
      }
   }

   public static void scale(float x, float y, float z) {
      GL11.glScalef(x, y, z);
   }

   public static void setFogDensity(float param) {
      if (param < 0.0F) {
         param = 0.0F;
      }

      if (param != fogState.density) {
         fogState.density = param;
         GL11.glFogf(2914, param);
         if (Config.isShaders()) {
            Shaders.setFogDensity(param);
         }
      }
   }

   public static void glNewList(int list, int mode) {
      GL11.glDrawArrays(list, 0, mode);
      if (Config.isShaders() && !creatingDisplayList) {
         int countInstances = Shaders.activeProgram.getCountInstances();
         if (countInstances > 1) {
            for (int i2 = 1; i2 < countInstances; i2++) {
               Shaders.uniform_instanceId.setValue(i2);
               GL11.glDrawArrays(list, 0, mode);
            }

            Shaders.uniform_instanceId.setValue(0);
         }
      }
   }

   public static void glEndList() {
      GL11.glEndList();
      creatingDisplayList = false;
   }

   public static void disableColorLogic() {
      colorLogicState.colorLogicOp.setDisabled();
   }

   public static void cullFace(int mode) {
      if (mode != cullState.mode) {
         cullState.mode = mode;
         GL11.glCullFace(mode);
      }
   }

   public static void disableBlend() {
      if (blendLock.isLocked()) {
         blendLockState.setDisabled();
      } else {
         blendState.blend.setDisabled();
      }
   }

   public static void deleteTexture(int texture) {
      if (texture != 0) {
         GL11.glDeleteTextures(texture);

         for (GlStateManager.TextureState textureState2 : textureState) {
            if (textureState2.textureName == texture) {
               textureState2.textureName = 0;
            }
         }
      }
   }

   public static void disableColorMaterial() {
      colorMaterialState.colorMaterial.setDisabled();
   }

   public static void run3() {
      GL11.glDisableClientState(32884);
   }

   public static void enableRescaleNormal() {
      rescaleNormalState.setEnabled();
   }

   public static int generateTexture() {
      return GL11.glGenTextures();
   }

   public static void disableNormalize() {
      normalizeState.setDisabled();
   }

   public static void clear(int mask) {
      if (clearEnabled) {
         GL11.glClear(mask);
      }
   }

   private static GlStateManager.TexGenCoord texGenCoord(GlStateManager.SType texGen) {
      switch (texGen) {
         case S:
            return texGenState.s;
         case T:
            return texGenState.t;
         case R:
            return texGenState.r;
         case Q:
            return texGenState.q;
         default:
            return texGenState.s;
      }
   }

   public static void clearColor(float red, float green, float blue, float alpha) {
      if (red != clearState.color.red || green != clearState.color.green || blue != clearState.color.blue || alpha != clearState.color.alpha) {
         clearState.color.red = red;
         clearState.color.green = green;
         clearState.color.blue = blue;
         clearState.color.alpha = alpha;
         GL11.glClearColor(red, green, blue, alpha);
      }
   }

   public static int glGetError() {
      return GL11.glGetError();
   }

   public static void rotate(float angle, float x, float y, float z) {
      GL11.glRotatef(angle, x, y, z);
   }

   public static void getFloat(int pname, FloatBuffer params) {
      GL11.glGetFloat(pname, params);
   }

   public static void run() {
      GL11.glEnableClientState(32884);
   }

   public static void unlockAlpha() {
      if (alphaLock.unlock()) {
         setAlphaState(alphaLockState);
      }
   }

   public static void enableLighting() {
      lightingState.setEnabled();
   }

   public static void texGen(GlStateManager.SType texGen, int pname, FloatBuffer params) {
      GL11.glTexGen(texGenCoord(texGen).coord, pname, params);
   }

   public static void bindTexture(int texture) {
      if (texture != textureState[activeTextureUnit].textureName) {
         textureState[activeTextureUnit].textureName = texture;
         GL11.glBindTexture(3553, texture);
         if (SmartAnimations.isActive()) {
            SmartAnimations.textureRendered(texture);
         }
      }
   }

   public static void enableLight(int light) {
      lightState[light].setEnabled();
   }

   public static void setFog(int param) {
      if (param != fogState.mode) {
         fogState.mode = param;
         GL11.glFogi(2917, param);
         if (Config.isShaders()) {
            Shaders.setFogMode(param);
         }
      }
   }

   public static void texGen(GlStateManager.SType texGen, int param) {
      GlStateManager.TexGenCoord glstatemanager$texgencoord = texGenCoord(texGen);
      if (param != glstatemanager$texgencoord.param) {
         glstatemanager$texgencoord.param = param;
         GL11.glTexGeni(glstatemanager$texgencoord.coord, 9472, param);
      }
   }

   public static void enableColorLogic() {
      colorLogicState.colorLogicOp.setEnabled();
   }

   public static int getInt() {
      return GL11.glGetTexLevelParameteri(32868, 0, 4096);
   }

   public static void glEnd() {
      GL11.glEnd();
   }

   public static void setBlendState(GlBlendState blendStateIn) {
      if (blendLock.isLocked()) {
         blendLockState.setState(blendStateIn);
      } else {
         blendState.blend.setState(blendStateIn.isEnabled());
         if (!blendStateIn.isSeparate()) {
            blendFunc(blendStateIn.getSrcFactor(), blendStateIn.getDstFactor());
         } else {
            tryBlendFuncSeparate(blendStateIn.getSrcFactor(), blendStateIn.getDstFactor(), blendStateIn.getSrcFactorAlpha(), blendStateIn.getDstFactorAlpha());
         }
      }
   }

   public static void ortho(double left, double right, double bottom, double top, double zNear, double zFar) {
      GL11.glOrtho(left, right, bottom, top, zNear, zFar);
   }

   static class AlphaState {
      public GlStateManager.BooleanState alphaTest = new GlStateManager.BooleanState(3008);
      public int func = 519;
      public float ref = -1.0F;

      private AlphaState() {
      }
   }

   static class BlendState {
      public GlStateManager.BooleanState blend = new GlStateManager.BooleanState(3042);
      public int srcFactor = 1;
      public int dstFactor = 0;
      public int srcFactorAlpha = 1;
      public int dstFactorAlpha = 0;

      private BlendState() {
      }
   }

   static class BooleanState {
      private boolean currentState = false;
      private final int capability;

      public void setDisabled() {
         this.setState(false);
      }

      public void setState(boolean state) {
         if (state != this.currentState) {
            this.currentState = state;
            if (state) {
               GL11.glEnable(this.capability);
            } else {
               GL11.glDisable(this.capability);
            }
         }
      }

      public BooleanState(int capabilityIn) {
         this.capability = capabilityIn;
      }

      public void setEnabled() {
         this.setState(true);
      }
   }

   static class ClearState {
      public double depth = 1.0;
      public GlStateManager.Color color = new GlStateManager.Color(0.0F, 0.0F, 0.0F, 0.0F);
      public int stencil = 0;

      private ClearState() {
      }
   }

   static class Color {
      public float red = 1.0F;
      public float green = 1.0F;
      public float blue = 1.0F;
      public float alpha = 1.0F;

      public Color(float redIn, float greenIn, float blueIn, float alphaIn) {
         this.red = redIn;
         this.green = greenIn;
         this.blue = blueIn;
         this.alpha = alphaIn;
      }

      public Color() {
      }
   }

   static class ColorLogicState {
      public GlStateManager.BooleanState colorLogicOp = new GlStateManager.BooleanState(3058);
      public int opcode = 5379;

      private ColorLogicState() {
      }
   }

   static class ColorMask {
      public boolean red = true;
      public boolean green = true;
      public boolean blue = true;
      public boolean alpha = true;

      private ColorMask() {
      }
   }

   static class ColorMaterialState {
      public GlStateManager.BooleanState colorMaterial = new GlStateManager.BooleanState(2903);
      public int face = 1032;
      public int mode = 5634;

      private ColorMaterialState() {
      }
   }

   static class CullState {
      public GlStateManager.BooleanState cullFace = new GlStateManager.BooleanState(2884);
      public int mode = 1029;

      private CullState() {
      }
   }

   static class DepthState {
      public GlStateManager.BooleanState depthTest = new GlStateManager.BooleanState(2929);
      public boolean maskEnabled = true;
      public int depthFunc = 513;

      private DepthState() {
      }
   }

   static class FogState {
      public GlStateManager.BooleanState fog = new GlStateManager.BooleanState(2912);
      public int mode = 2048;
      public float density = 1.0F;
      public float start = 0.0F;
      public float end = 1.0F;

      private FogState() {
      }
   }

   static class PolygonOffsetState {
      public GlStateManager.BooleanState polygonOffsetFill = new GlStateManager.BooleanState(32823);
      public GlStateManager.BooleanState polygonOffsetLine = new GlStateManager.BooleanState(10754);
      public float factor = 0.0F;
      public float units = 0.0F;

      private PolygonOffsetState() {
      }
   }

   public enum SType {
      S,
      T,
      R,
      Q;
   }

   static class StencilFunc {
      public int func = 519;
      public int ref = 0;
      public int mask = -1;

      private StencilFunc() {
      }
   }

   static class StencilState {
      public GlStateManager.StencilFunc func = new GlStateManager.StencilFunc();
      public int mask = -1;
      public int fail = 7680;
      public int zfail = 7680;
      public int zpass = 7680;

      private StencilState() {
      }
   }

   static class TexGenCoord {
      public int param = -1;
      public int coord;
      public GlStateManager.BooleanState textureGen;

      public TexGenCoord(int coord, int capability) {
         this.coord = coord;
         this.textureGen = new GlStateManager.BooleanState(capability);
      }
   }

   static class TexGenState {
      public GlStateManager.TexGenCoord s = new GlStateManager.TexGenCoord(8192, 3168);
      public GlStateManager.TexGenCoord t = new GlStateManager.TexGenCoord(8193, 3169);
      public GlStateManager.TexGenCoord r = new GlStateManager.TexGenCoord(8194, 3170);
      public GlStateManager.TexGenCoord q = new GlStateManager.TexGenCoord(8195, 3171);

      private TexGenState() {
      }
   }

   static class TextureState {
      public GlStateManager.BooleanState texture2DState = new GlStateManager.BooleanState(3553);
      public int textureName = 0;

      private TextureState() {
      }
   }
}
