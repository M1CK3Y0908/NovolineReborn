package wtf.novoline.module.hud;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.ServerData;
import wtf.novoline.Novoline;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.module.ModuleManager;
import wtf.novoline.utils.hypixel.HypixelState;
import wtf.novoline.utils.render.CFontRenderer;
import wtf.novoline.utils.render.FilledPolygonRenderer;
import wtf.novoline.utils.render.RenderUtils;

public class WatermarkElement extends HudElement {
   private String watermarkText;
   private float width;

   public WatermarkElement() {
      super("watermark", 5.0F, 5.0F, true, false);
   }

   @Override
   public float getWidth() {
      return this.width;
   }

   @Override
   public void draw(ScaledResolution scaledResolution, float f, boolean flag) {
      if (ModuleManager.watermark.isEnabled()) {
         int currentElement = ModuleManager.watermark.watermark.getCurrentElement();
         if (currentElement == 0) {
            StringBuilder stringBuilder = new StringBuilder("Novoline")
               .append(" ")
               .append("3.0")
               .append(" | ")
               .append(Novoline.username)
               .append(" | ")
               .append(Minecraft.getDebugFPS())
               .append("fps | ");
            ServerData currentServerData = Minecraft.getMinecraft().getCurrentServerData();
            if (currentServerData != null && !currentServerData.isOnLAN()) {
               stringBuilder.append(currentServerData.serverIP.split(":")[0]);
            } else {
               stringBuilder.append("Singleplayer");
            }

            if (ModuleManager.watermark.showPlayTime.isEnabled()) {
               Duration duration = Duration.between(HypixelState.getSessionStart(), Instant.now());
               String text = String.format(Locale.ENGLISH, "%02d:%02d:%02d", duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart());
               stringBuilder.append(" | ").append(text);
            }

            this.watermarkText = stringBuilder.toString();
            CFontRenderer cFontRenderer = Fonts.robotoRegularSize14;
            int color = ModuleManager.watermark.main.getColor();
            RenderUtils.drawShadow(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), 1.5F, 1610612736);
            RenderUtils.drawRect(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), -14606047);
            RenderUtils.drawGradientLine(
               this.getX(),
               this.getY(),
               this.getX() + this.getWidth(),
               this.getY(),
               3.0F,
               RenderUtils.pulseBrightness(color, 0.5F, 1.0F, 1, 3, 2500L),
               RenderUtils.pulseBrightness(color, 0.5F, 1.0F, 1, 1, 2500L),
               false
            );
            cFontRenderer.drawString(
               this.watermarkText,
               this.getX() + this.getWidth() / 2.0F - cFontRenderer.getStringWidth(this.watermarkText) / 2.0F,
               this.getY() + this.getHeight() / 2.0F - cFontRenderer.getHeight() / 2.0F + 1.0F,
               -1
            );
            this.width = cFontRenderer.getStringWidth(this.watermarkText) + 5.0F;
         } else if (currentElement == 1) {
            StringBuilder stringBuilder2 = new StringBuilder("§8|§r ").append(Novoline.username).append(" §8|§r ").append(Minecraft.getDebugFPS()).append("fps §8|§r ");
            ServerData currentServerData2 = Minecraft.getMinecraft().getCurrentServerData();
            if (currentServerData2 != null && !currentServerData2.isOnLAN()) {
               stringBuilder2.append(currentServerData2.serverIP.split(":")[0]);
            } else {
               stringBuilder2.append("Singleplayer");
            }

            if (ModuleManager.watermark.showPlayTime.isEnabled()) {
               Duration duration2 = Duration.between(HypixelState.getSessionStart(), Instant.now());
               String text2 = String.format(Locale.ENGLISH, "%02d:%02d:%02d", duration2.toHours(), duration2.toMinutesPart(), duration2.toSecondsPart());
               stringBuilder2.append(" §8|§r ").append(text2);
            }

            this.watermarkText = stringBuilder2.toString();
            CFontRenderer cFontRenderer2 = Fonts.museoSans900Size16;
            CFontRenderer cFontRenderer3 = Fonts.museoSans700Size14;
            String text3 = "Novoline".toUpperCase(Locale.ROOT);
            this.width = cFontRenderer2.getStringWidth(text3) + cFontRenderer3.getStringWidth(this.watermarkText) + 9.0F;
            RenderUtils.drawShadow(
               this.getX() + 1.0F, this.getY() + 1.0F, this.getX() + this.getWidth() - 1.0F, this.getY() + this.getHeight(), 2.5F, -285212672
            );
            FilledPolygonRenderer filledPolygonRenderer = new FilledPolygonRenderer();
            filledPolygonRenderer.putRoundedRect(this.getX(), this.getY(), this.width, 14.0F, 1.0F, -15395563);
            filledPolygonRenderer.draw();
            cFontRenderer2.drawString(text3, this.getX() + 3.0F, this.getY() + 5.0F, -16611376);
            cFontRenderer2.drawString(text3, this.getX() + 3.0F + 0.5F, this.getY() + 5.0F + 0.5F, -1);
            cFontRenderer3.drawString(this.watermarkText, this.getX() + 3.0F + cFontRenderer2.getStringWidth(text3) + 3.0F, this.getY() + 5.0F + 0.5F, -1);
         }
      }
   }

   @Override
   public float getHeight() {
      return 13.0F;
   }
}
