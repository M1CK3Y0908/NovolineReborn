package net.minecraft.client.renderer.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TextureCompass extends TextureAtlasSprite {
   public double currentAngle;
   public static String locationSprite;
   public double angleDelta;

   public void updateCompass(World worldIn, double d, double d2, double d3, boolean flag, boolean flag2) {
      if (!this.framesTextureData.isEmpty()) {
         double zCoordinate = 0.0;
         if (worldIn != null && !flag) {
            BlockPos spawnPoint = worldIn.getSpawnPoint();
            double d4 = spawnPoint.getX() - d;
            double d5 = spawnPoint.getZ() - d2;
            d3 %= 360.0;
            zCoordinate = -((d3 - 90.0) * Math.PI / 180.0 - Math.atan2(d5, d4));
            if (!worldIn.provider.isSurfaceWorld()) {
               zCoordinate = Math.random() * Math.PI * 2.0;
            }
         }

         if (flag2) {
            this.currentAngle = zCoordinate;
         } else {
            double d6 = zCoordinate - this.currentAngle;

            while (d6 < -Math.PI) {
               d6 += Math.PI * 2;
            }

            while (d6 >= Math.PI) {
               d6 -= Math.PI * 2;
            }

            d6 = MathHelper.clamp_double(d6, -1.0, 1.0);
            this.angleDelta += d6 * 0.1;
            this.angleDelta *= 0.8;
            this.currentAngle = this.currentAngle + this.angleDelta;
         }

         int i = (int)((this.currentAngle / (Math.PI * 2) + 1.0) * this.framesTextureData.size()) % this.framesTextureData.size();

         while (i < 0) {
            i = (i + this.framesTextureData.size()) % this.framesTextureData.size();
         }

         if (i != this.frameCounter) {
            this.frameCounter = i;
            TextureUtil.handleArray(this.framesTextureData.get(this.frameCounter), this.width, this.height, this.originX, this.originY, false);
         }
      }
   }

   @Override
   public void updateAnimation() {
      Minecraft minecraft = Minecraft.getMinecraft();
      if (minecraft.theWorld != null && minecraft.thePlayer != null) {
         this.updateCompass(minecraft.theWorld, minecraft.thePlayer.posX, minecraft.thePlayer.posZ, minecraft.thePlayer.rotationYaw, false, false);
      } else {
         this.updateCompass((World)null, 0.0, 0.0, 0.0, true, false);
      }
   }

   public TextureCompass(String iconName) {
      super(iconName, 87996829185329L);
      locationSprite = iconName;
   }
}
