package wtf.novoline.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import org.lwjgl.opengl.GL11;
import wtf.novoline.event.JumpEvent;
import wtf.novoline.event.RenderWorldEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.ColorSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.math.SmoothingUtils;
import wtf.novoline.utils.render.RenderUtils;

public class JumpCircle extends Module {
   private final List<JumpCircle.Holder> circles = new CopyOnWriteArrayList<>();
   private final NumberSetting speed = settingManager.register(new NumberSetting(this, "Speed", 0.5F, 0.0F, 5.0F));
   private final NumberSetting radius = settingManager.register(new NumberSetting(this, "Radius", 3.0F, 0.0F, 20.0F));
   private final ColorSetting color = settingManager.register(new ColorSetting(this, "Color", -10262799));
   @EventLink
   public final Listener<JumpEvent> onJump = jumpEvent -> this.circles
      .add(new JumpCircle.Holder(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 0.05, minecraft.thePlayer.posZ));
   @EventLink
   public final Listener<RenderWorldEvent> onRenderWorld = renderWorldEvent -> {
      if (!this.circles.isEmpty()) {
         long l = (long)(this.speed.getCurrentValue() * 10000.0F) + 500L;
         this.circles.removeIf(holder -> holder.spawnTime + l < System.currentTimeMillis());
         if (!this.circles.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.disableCull();
            GlStateManager.disableTexture2D();
            GL11.glLineWidth(3.0F);
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
            RenderUtils.applyBlendFunc();
            GlStateManager.shadeModel(7425);
            GlStateManager.alphaFunc(516, 0.003921569F);
            GL11.glBegin(7);
            Iterator iterator2 = this.circles.iterator();

            while (iterator2.hasNext()) {
               ((JumpCircle.Holder)iterator2.next()).render();
            }

            GL11.glEnd();
            RenderUtils.resetColor();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.shadeModel(7424);
            GlStateManager.disableBlend();
            GlStateManager.enableTexture2D();
            GlStateManager.enableCull();
            GlStateManager.popMatrix();
         }
      }
   };

   public JumpCircle() {
      super(Category.VISUAL, "JumpCircle", "");
   }

   static Minecraft access$000() {
      return minecraft;
   }

   final class Holder {
      public double x;
      public double y;
      public double z;
      public long spawnTime;
      public double radius;
      public float alpha;

      public void render() {
         float[] fArray = RenderUtils.unpackColorNormalized(JumpCircle.this.color.getColor());
         GlStateManager.color(fArray[0], fArray[1], fArray[2], this.alpha / 255.0F);
         this.radius = SmoothingUtils.approach((float)this.radius, JumpCircle.this.radius.getCurrentValue(), JumpCircle.this.speed.getCurrentValue());
         this.alpha = SmoothingUtils.approach(this.alpha, 0.0F, 1.0F);
         RenderManager renderManager = JumpCircle.access$000().getRenderManager();
         double d = this.x - renderManager.viewerPosX;
         double d2 = this.y - renderManager.viewerPosY + 0.05 * (1.0F - this.alpha / 255.0F);
         double d3 = this.z - renderManager.viewerPosZ;
         double d4 = Math.max(0.0, this.radius - 0.15F);

         for (int i = 0; i <= 45; i++) {
            double d5 = i * 8;
            double d6 = d + Math.sin(Math.toRadians(d5)) * d4;
            double d7 = d3 + Math.cos(Math.toRadians(d5)) * d4;
            double d8 = d + Math.sin(Math.toRadians(d5)) * this.radius;
            double d9 = d3 + Math.cos(Math.toRadians(d5)) * this.radius;
            if (i > 0) {
               GlStateManager.color(fArray[0], fArray[1], fArray[2], this.alpha / 255.0F);
               GL11.glVertex3d(d8, d2, d9);
               GlStateManager.color(fArray[0], fArray[1], fArray[2], 0.0F);
               GL11.glVertex3d(d6, d2, d7);
            }

            if (i < 45) {
               GlStateManager.color(fArray[0], fArray[1], fArray[2], 0.0F);
               GL11.glVertex3d(d6, d2, d7);
               GlStateManager.color(fArray[0], fArray[1], fArray[2], this.alpha / 255.0F);
               GL11.glVertex3d(d8, d2, d9);
            }
         }
      }

      public Holder(double d, double d2, double d3) {
         this.x = d;
         this.y = d2;
         this.z = d3;
         this.spawnTime = System.currentTimeMillis();
         this.radius = 0.0;
         this.alpha = 255.0F;
      }
   }
}
