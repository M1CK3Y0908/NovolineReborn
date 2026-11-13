package wtf.novoline.module.misc;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.util.ResourceLocation;
import wtf.novoline.Novoline;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.event.PacketReceiveEvent;
import wtf.novoline.event.RenderScreenEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ColorSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.language.Language;
import wtf.novoline.utils.math.InterpolationUtils;
import wtf.novoline.utils.render.CFontRenderer;
import wtf.novoline.utils.render.RenderUtils;
import wtf.novoline.utils.render.ShadowRenderer;
import wtf.novoline.utils.time.Stopwatch;

public class LagDetector extends Module {
   private final NumberSetting lagDetector = settingManager.register(new NumberSetting(this, "Delay (seconds)", 1, 1, 5));
   private final CheckboxSetting alphaBlink = settingManager.register(new CheckboxSetting(this, "Alpha blink", true));
   private final CheckboxSetting glow = settingManager.register(new CheckboxSetting(this, "Glow", true));
   private static final ResourceLocation ICON = new ResourceLocation("novoline", "images/connection_problem.png");
   private final ColorSetting color = settingManager.register(new ColorSetting(this, "Color", -4776948));
   private final ShadowRenderer shadowRenderer = new ShadowRenderer(Novoline.framebufferManager);
   @EventLink
   public final Listener<PacketReceiveEvent> onPacketReceive = packetReceiveEvent -> lastPacketTimer.reset();
   @EventLink
   public final Listener<RenderScreenEvent> onRenderScreen = renderScreenEvent -> {
      if (lastPacketTimer.hasElapsed((long)(this.lagDetector.getCurrentValue() * 1000.0F)) && !minecraft.isSingleplayer()) {
         float f = (float)(renderScreenEvent.scaledResolution.getScaledWidth_double() / 2.0);
         float f2 = 1.0F;
         if (this.alphaBlink.isEnabled()) {
            f2 = (float)InterpolationUtils.oscillate(0.25, 1.0, 1200L);
         }

         int i = RenderUtils.withAlphaFraction(this.color.getColor(), f2);
         if (this.glow.isEnabled()) {
            this.shadowRenderer.submit(() -> RenderUtils.drawColoredTexture(f - 20.0F, 40.0F, 40.0F, 40.0F, i, ICON));
         } else {
            RenderUtils.drawColoredTexture(f - 20.0F, 40.0F, 40.0F, 40.0F, i, ICON);
         }

         String text = Language.translate("feature.lagdetector.msg");
         CFontRenderer cFontRenderer = Fonts.rubikSemiboldSize16;
         if (this.glow.isEnabled()) {
            this.shadowRenderer.submit(() -> cFontRenderer.drawString(text, f - cFontRenderer.getStringWidth(text) / 2.0F, 85.0F, i));
         } else {
            cFontRenderer.drawString(text, f - cFontRenderer.getStringWidth(text) / 2.0F, 85.0F, i);
         }

         if (this.glow.isEnabled()) {
            this.shadowRenderer.render(minecraft.getFramebuffer(), renderScreenEvent.scaledResolution, i, 10.0F, 3.0F, 1.3F, true);
         }
      }
   };
   private static final Stopwatch lastPacketTimer = new Stopwatch();

   public LagDetector() {
      super(Category.MISC, "LagDetector", "");
   }
}
