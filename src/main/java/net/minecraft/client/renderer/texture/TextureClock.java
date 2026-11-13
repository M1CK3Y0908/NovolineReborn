package net.minecraft.client.renderer.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

public class TextureClock extends TextureAtlasSprite {
   private double currentAngle;
   private double angleDelta;

   public TextureClock(String iconName) {
      super(iconName, 87996829185329L);
   }

   @Override
   public void updateAnimation() {
      if (!this.framesTextureData.isEmpty()) {
         Minecraft minecraft2 = Minecraft.getMinecraft();
         double d = 0.0;
         if (minecraft2.theWorld != null && minecraft2.thePlayer != null) {
            d = minecraft2.theWorld.getCelestialAngle(1.0F);
            if (!minecraft2.theWorld.provider.isSurfaceWorld()) {
               d = Math.random();
            }
         }

         double d2 = d - this.currentAngle;

         while (d2 < -0.5) {
            d2++;
         }

         while (d2 >= 0.5) {
            d2--;
         }

         d2 = MathHelper.clamp_double(d2, -1.0, 1.0);
         this.angleDelta += d2 * 0.1;
         this.angleDelta *= 0.8;
         this.currentAngle = this.currentAngle + this.angleDelta;
         int i = (int)((this.currentAngle + 1.0) * this.framesTextureData.size()) % this.framesTextureData.size();

         while (i < 0) {
            i = (i + this.framesTextureData.size()) % this.framesTextureData.size();
         }

         if (i != this.frameCounter) {
            this.frameCounter = i;
            TextureUtil.handleArray(this.framesTextureData.get(this.frameCounter), this.width, this.height, this.originX, this.originY, false);
         }
      }
   }
}
