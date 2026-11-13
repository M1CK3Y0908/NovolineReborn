package wtf.novoline.module.hud;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class HudInputHandler {
   private final HudManager hudManager;

   public HudInputHandler(HudManager hudManager2) {
      this.hudManager = hudManager2;
   }

   public void handleMouseRelease(float f, float f2) {
      float scaleFactor = new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
      float f3 = f * scaleFactor / 2.0F;
      float f4 = f2 * scaleFactor / 2.0F;
      List elements = this.hudManager.getElements();

      for (int i = elements.size() - 1; i >= 0; i--) {
         ((HudElement)elements.get(i)).handleMouseRelease(f3, f4);
      }
   }

   public void handleMouseClick(float f, float f2, int i) {
      float scaleFactor = new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
      float f3 = f * scaleFactor / 2.0F;
      float f4 = f2 * scaleFactor / 2.0F;
      List elements = this.hudManager.getElements();

      for (int i2 = elements.size() - 1; i2 >= 0; i2--) {
         ((HudElement)elements.get(i2)).handleMouseClick(f3, f4, i);
      }
   }
}
