package net.optifine.shaders;

import java.nio.IntBuffer;
import java.util.Arrays;
import net.optifine.render.GlAlphaState;
import net.optifine.render.GlBlendState;
import net.optifine.shaders.config.RenderScale;

public class Program {
   private final Boolean[] buffersFlip = new Boolean[8];
   private int compositeMipmapSetting;
   private GlBlendState blendState;
   private String drawBufSettings;
   private final boolean[] toggleColorTextures = new boolean[8];
   private RenderScale renderScale;
   private final int index;
   private int id;
   private int countInstances;
   private final String name;
   private IntBuffer drawBuffersBuffer;
   private final ProgramStage programStage;
   private int ref;
   private final Program programBackup;
   private GlAlphaState alphaState;
   private IntBuffer drawBuffers;

   public void resetId() {
      this.id = 0;
      this.ref = 0;
   }

   public void setCompositeMipmapSetting(int compositeMipmapSetting) {
      this.compositeMipmapSetting = compositeMipmapSetting;
   }

   public String getDrawBufSettings() {
      return this.drawBufSettings;
   }

   public Boolean[] getBuffersFlip() {
      return this.buffersFlip;
   }

   public void setAlphaState(GlAlphaState alphaState) {
      this.alphaState = alphaState;
   }

   public int getIndex() {
      return this.index;
   }

   public void setRenderScale(RenderScale renderScale) {
      this.renderScale = renderScale;
   }

   public GlBlendState getBlendState() {
      return this.blendState;
   }

   public void resetProperties() {
      this.alphaState = null;
      this.blendState = null;
      this.renderScale = null;
      Arrays.fill(this.buffersFlip, null);
   }

   public void setDrawBufSettings(String drawBufSettings) {
      this.drawBufSettings = drawBufSettings;
   }

   public int getId() {
      return this.id;
   }

   public IntBuffer getDrawBuffersBuffer() {
      return this.drawBuffersBuffer;
   }

   public String getName() {
      return this.name;
   }

   public boolean[] getToggleColorTextures() {
      return this.toggleColorTextures;
   }

   public int getRef() {
      return this.ref;
   }

   public void setBlendState(GlBlendState blendState) {
      this.blendState = blendState;
   }

   public Program(int index, String name, ProgramStage programStage, Program programBackup) {
      this.index = index;
      this.name = name;
      this.programStage = programStage;
      this.programBackup = programBackup;
   }

   public void copyFrom(Program program) {
      this.id = program.getId();
      this.alphaState = program.getAlphaState();
      this.blendState = program.getBlendState();
      this.renderScale = program.getRenderScale();
      System.arraycopy(program.getBuffersFlip(), 0, this.buffersFlip, 0, this.buffersFlip.length);
      this.drawBufSettings = program.getDrawBufSettings();
      this.drawBuffers = program.getDrawBuffers();
      this.compositeMipmapSetting = program.getCompositeMipmapSetting();
      this.countInstances = program.getCountInstances();
      System.arraycopy(program.getToggleColorTextures(), 0, this.toggleColorTextures, 0, this.toggleColorTextures.length);
   }

   public void setDrawBuffers(IntBuffer drawBuffers) {
      this.drawBuffers = drawBuffers;
   }

   public Program(int i, String text, ProgramStage programStage2) {
      this.index = i;
      this.name = text;
      this.programStage = programStage2;
      this.programBackup = this;
   }

   public GlAlphaState getAlphaState() {
      return this.alphaState;
   }

   public IntBuffer getDrawBuffers() {
      return this.drawBuffers;
   }

   public void setRef(int ref) {
      this.ref = ref;
   }

   public Program getProgramBackup() {
      return this.programBackup;
   }

   public ProgramStage getProgramStage() {
      return this.programStage;
   }

   public int getCompositeMipmapSetting() {
      return this.compositeMipmapSetting;
   }

   public void setId(int id) {
      this.id = id;
   }

   public RenderScale getRenderScale() {
      return this.renderScale;
   }

   public String getRealProgramName() {
      if (this.id == 0) {
         return "none";
      }

      Program program;
      for (program = this; program.getRef() != this.id; program = program.getProgramBackup()) {
         if (program.getProgramBackup() == null || program.getProgramBackup() == program) {
            return "unknown";
         }
      }

      return program.getName();
   }

   public void resetConfiguration() {
      this.drawBufSettings = null;
      this.compositeMipmapSetting = 0;
      this.countInstances = 0;
      if (this.drawBuffersBuffer == null) {
         this.drawBuffersBuffer = Shaders.nextIntBuffer(8);
      }
   }

   public void setCountInstances(int countInstances) {
      this.countInstances = countInstances;
   }

   public int getCountInstances() {
      return this.countInstances;
   }

   @Override
   public String toString() {
      return "name: " + this.name + ", id: " + this.id + ", ref: " + this.ref + ", real: " + this.getRealProgramName();
   }
}
