package wtf.novoline.utils.render;

import net.minecraft.util.ResourceLocation;

public class MaskShader extends ShaderProgram {
   public static MaskShader maskShader = new MaskShader();

   private MaskShader() {
      super(new ResourceLocation("novoline", "shaders/mask.fsh"));
   }
}
