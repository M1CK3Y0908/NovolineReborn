package wtf.novoline.utils.render;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL20;
import wtf.novoline.Novoline;

public class MenuBackgroundShader {
   private final ShaderProgram shaderProgram;
   private static final long START_NANOS = System.nanoTime();

   public void draw(ScaledResolution scaledResolution) {
      GlStateManager.disableCull();
      this.shaderProgram.use();
      GL20.glUniform1f(this.shaderProgram.getUniformLocation("time"), (float)(System.nanoTime() - START_NANOS) / 1000000.0F / 1000.0F);
      GL20.glUniform2f(this.shaderProgram.getUniformLocation("resolution"), scaledResolution.getFloat() * scaledResolution.getScaleFactor(), scaledResolution.getFloat2() * scaledResolution.getScaleFactor());
      this.shaderProgram.drawClipSpaceQuad();
      GlStateManager.enableCull();
   }

   public MenuBackgroundShader() {
      this(Novoline.getNovoline().getClientPreferences().getShaderId());
   }

   public MenuBackgroundShader(int i) {
      this.shaderProgram = new ShaderProgram(
         new ResourceLocation("novoline", "shaders/bg_" + i + ".fsh"), new ResourceLocation("novoline", "shaders/bg_" + i + ".vsh")
      );
   }
}
