package wtf.novoline.utils.notification;

import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import wtf.novoline.Novoline;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.module.hud.Notifications;
import wtf.novoline.utils.math.SmoothingUtils;
import wtf.novoline.utils.render.BloomRenderer;
import wtf.novoline.utils.render.BlurRenderer;
import wtf.novoline.utils.render.CFontRenderer;
import wtf.novoline.utils.render.FilledPolygonRenderer;
import wtf.novoline.utils.render.RenderUtils;
import wtf.novoline.utils.render.RoundedPolygonRenderer;

public class NotificationManager extends CopyOnWriteArrayList<Notification> {
   private static final CFontRenderer cFontRenderer = Fonts.rubikRegularSize18;
   private static final CFontRenderer cFontRenderer2 = Fonts.rubikRegularSize16;
   private final BlurRenderer blurRenderer = new BlurRenderer(Novoline.framebufferManager);
   private final BloomRenderer bloomRenderer = new BloomRenderer(Novoline.framebufferManager);

   public void show(Notification notification2) {
      this.add(notification2);
   }

   public void dismiss(Notification notification2) {
      this.remove(notification2);
   }

   private void drawNotificationContent(Notification notification2, float f, float f2, float f3, ResourceLocation resourceLocation) {
      RenderUtils.drawTextureWithAlpha(f + 4.0F, f2 + f3 / 2.0F - 8.0F, 16.0F, 16.0F, 1.0F, resourceLocation);
      cFontRenderer.drawString(notification2.getTitle(), f + 28.0F, f2 + f3 / 2.0F - cFontRenderer.getHeight() / 2.0F - 3.0F, -1);
      cFontRenderer2.drawString(notification2.getSubText(), f + 28.0F, f2 + f3 / 2.0F - cFontRenderer2.getHeight() / 2.0F + 6.0F, -2039584);
   }

   public void renderAll(ScaledResolution scaledResolution) {
      Notifications notifications2 = ModuleManager.notifications;
      if (notifications2.isEnabled() && !this.isEmpty()) {
         boolean enabled = notifications2.blur.isEnabled();
         boolean enabled2 = notifications2.notifications.isEnabled();
         boolean enabled3 = notifications2.shadow.isEnabled();
         Framebuffer framebuffer = Minecraft.getMinecraft().getFramebuffer();
         if (enabled) {
            this.blurRenderer.blur("notifications-blur", framebuffer, scaledResolution, 10, 0.6F, 1);
         }

         framebuffer.bindFramebuffer(true);
         float scaledWidth = scaledResolution.getScaledWidth();
         float scaledHeight = scaledResolution.getScaledHeight();
         float f = 5.0F;
         float f2 = 44.0F;
         float f3 = 30.0F;
         this.removeIf(Notification::hasExpired);

         for (int i = 0; i < Math.min(this.size(), 15); i++) {
            Notification notification2 = this.get(i);
            float f4 = Math.max(170.0F, cFontRenderer2.getStringWidth(notification2.getSubText()) + 36.0F);
            NotificationType type = notification2.getType();
            ResourceLocation resourceLocation = type.getResourceLocation();
            int backgroundColor = type.getBackgroundColor();
            int accentColor = type.getAccentColor();
            notification2.offsetY = SmoothingUtils.approach(notification2.offsetY, f2, 10.0F);
            if (notification2.isFinished()) {
               notification2.offsetX = SmoothingUtils.approach(notification2.offsetX, -f, 10.0F);
            } else {
               notification2.offsetX = SmoothingUtils.approach(notification2.offsetX, f4 + f, 10.0F);
               f2 += f3 + 5.0F;
            }

            float f5 = scaledWidth - notification2.offsetX;
            float f6 = scaledHeight - notification2.offsetY;
            if (enabled3) {
               RenderUtils.drawShadow(f5 + 1.0F, f6 + 1.0F, f5 + f4 - 1.0F, f6 + f3 - 1.0F, 3.0F, 1879048192);
            }

            if (enabled) {
               RoundedPolygonRenderer roundedPolygonRenderer = new RoundedPolygonRenderer(scaledResolution.getFloat(), scaledResolution.getFloat2());
               this.blurRenderer.bindResultTexture();
               roundedPolygonRenderer.putRoundedRect(f5, f6, f4, f3, 3.0F, -1337);
               roundedPolygonRenderer.draw();
            }

            FilledPolygonRenderer filledPolygonRenderer = new FilledPolygonRenderer();
            filledPolygonRenderer.putRoundedRect(f5, f6, f4, f3, 3.0F, backgroundColor);
            filledPolygonRenderer.draw();
            filledPolygonRenderer = new FilledPolygonRenderer();
            filledPolygonRenderer.putCustomRoundedRect(f5, f6, 24.0F, f3, 3.0F, 3.0F, 0.0F, 0.0F, accentColor);
            filledPolygonRenderer.draw();
            if (enabled2) {
               this.bloomRenderer.submit(() -> this.drawNotificationContent(notification2, f5, f6, f3, resourceLocation));
            } else {
               this.drawNotificationContent(notification2, f5, f6, f3, resourceLocation);
            }
         }

         if (enabled2) {
            int i2 = (int)notifications2.glowIntensity.getCurrentValue();
            this.bloomRenderer.applyBloom(framebuffer, scaledResolution, "notifications-bloom", -1, i2, 0.6F, 1.0F, 1.5F, 1.0F);
         }

         if (enabled) {
            this.blurRenderer.releaseResult();
         }
      }
   }
}
