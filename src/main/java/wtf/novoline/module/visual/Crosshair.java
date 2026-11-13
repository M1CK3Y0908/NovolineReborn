package wtf.novoline.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.novoline.event.RenderScreenEvent;
import wtf.novoline.module.Category;
import wtf.novoline.module.Module;
import wtf.novoline.setting.CheckboxSetting;
import wtf.novoline.setting.ColorSetting;
import wtf.novoline.setting.NumberSetting;
import wtf.novoline.utils.render.RenderUtils;

public class Crosshair extends Module {
   private final CheckboxSetting crosshair = settingManager.register(new CheckboxSetting(this, "Center dot", false));
   private final CheckboxSetting tStyle = settingManager.register(new CheckboxSetting(this, "T-Style", false));
   private final CheckboxSetting outline = settingManager.register(new CheckboxSetting(this, "Outline", true));
   private final CheckboxSetting dynamic = settingManager.register(new CheckboxSetting(this, "Dynamic", true));
   private final NumberSetting length = settingManager.register(new NumberSetting(this, "Length", 2.65F, 0.0F, 10.0F));
   private final NumberSetting gap = settingManager.register(new NumberSetting(this, "Gap", 2.15F, 0.0F, 10.0F));
   private final ColorSetting color = settingManager.register(new ColorSetting(this, "Color", -8286984));
   @EventLink
   public final Listener<RenderScreenEvent> onRenderScreen = renderScreenEvent -> {
      float f = (float)(renderScreenEvent.scaledResolution.getScaledWidth_double() / 2.0);
      float f2 = (float)(renderScreenEvent.scaledResolution.getScaledHeight_double() / 2.0);
      boolean enabled = this.outline.isEnabled();
      float f3 = enabled ? 0.5F : 0.0F;
      int color2 = this.color.getColor();
      float f4 = ((int)(this.length.getCurrentValue() * 10.0F) + 4) / 5 * 5 / 10.0F;
      float f5 = ((int)(this.gap.getCurrentValue() * 10.0F) + 4) / 5 * 5 / 10.0F;
      int i = this.crosshair.isEnabled() ? 1 : 0;
      if (this.dynamic.isEnabled() && (minecraft.thePlayer.moveForward != 0.0F || minecraft.thePlayer.moveStrafing != 0.0F)) {
         f5 += 2.0F;
      }

      if (this.crosshair.isEnabled()) {
         if (enabled) {
            RenderUtils.drawRect(f - 0.5F - f3, f2 - 0.5F - f3, f + 0.5F + f3, f2 + 0.5F + f3, -16777216);
         }

         RenderUtils.drawRect(f - 0.5F, f2 - 0.5F, f + 0.5F, f2 + 0.5F, color2);
      }

      if (enabled) {
         RenderUtils.drawRect(f - 0.5F - f5 - f4 - i - f3, f2 - 0.5F - f3, f + 0.5F - f5 - i + f3, f2 + 0.5F + f3, -16777216);
         RenderUtils.drawRect(f - 0.5F + f5 + i - f3, f2 - 0.5F - f3, f + 0.5F + f5 + f4 + i + f3, f2 + 0.5F + f3, -16777216);
         RenderUtils.drawRect(f - 0.5F - f3, f2 - 0.5F + f5 + i - f3, f + 0.5F + f3, f2 + 0.5F + f5 + f4 + i + f3, -16777216);
         if (!this.tStyle.isEnabled()) {
            RenderUtils.drawRect(
               f - 0.5F - f3, f2 - 0.5F - f5 - f4 - i - f3, f + 0.5F + f3, f2 + 0.5F - f5 - i + f3, -16777216
            );
         }
      }

      RenderUtils.drawRect(f - 0.5F - f5 - f4 - i, f2 - 0.5F, f + 0.5F - f5 - i, f2 + 0.5F, color2);
      RenderUtils.drawRect(f - 0.5F + f5 + i, f2 - 0.5F, f + 0.5F + f5 + f4 + i, f2 + 0.5F, color2);
      RenderUtils.drawRect(f - 0.5F, f2 - 0.5F + f5 + i, f + 0.5F, f2 + 0.5F + f5 + f4 + i, color2);
      if (!this.tStyle.isEnabled()) {
         RenderUtils.drawRect(f - 0.5F, f2 - 0.5F - f5 - f4 - i, f + 0.5F, f2 + 0.5F - f5 - i, color2);
      }
   };

   public Crosshair() {
      super(Category.VISUAL, "Crosshair", "");
   }
}
