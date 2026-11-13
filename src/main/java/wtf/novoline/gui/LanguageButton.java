package wtf.novoline.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import wtf.novoline.utils.language.Language.RuRuType;
import wtf.novoline.utils.math.SmoothingUtils;
import wtf.novoline.utils.render.RenderUtils;

public class LanguageButton extends GuiButton {
   private float alpha;
   private RuRuType ruRuType;

   public LanguageButton(int i, int i2, int i3) {
      super(i, i2, i3, null);
      this.width = 15;
      this.height = 15;
   }

   public void setRuRuType(RuRuType ruRuType2) {
      this.ruRuType = ruRuType2;
   }

   public RuRuType getRuRuType() {
      return this.ruRuType;
   }

   @Override
   public void drawButton(Minecraft minecraft2, int i, int i2) {
      ResourceLocation resourceLocation = new ResourceLocation("novoline", "images/mainmenu/lang_%s.png".formatted(this.ruRuType.getLanguageTag()));
      this.alpha = SmoothingUtils.approach(this.alpha, ButtonUtils.containsMouse(this, i, i2) ? 1.0F : 0.6F, 3.0F);
      RenderUtils.drawTextureWithAlpha(this.xPosition, this.yPosition, this.width, this.height, this.alpha, resourceLocation);
   }
}
