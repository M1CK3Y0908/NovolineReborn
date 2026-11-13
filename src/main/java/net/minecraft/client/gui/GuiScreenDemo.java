package net.minecraft.client.gui;

import java.io.IOException;
import java.net.URI;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiScreenDemo extends GuiScreen {
   private static final Logger logger = LogManager.getLogger();
   private static final ResourceLocation resourceLocation = new ResourceLocation("textures/gui/demo_background.png");

   @Override
   public void drawDefaultBackground() {
      super.drawDefaultBackground();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(resourceLocation);
      int i = (this.width - 248) / 2;
      int j = (this.height - 166) / 2;
      this.drawTexturedModalRect(i, j, 0, 0, 248, 166);
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      this.buttonList.add(new GuiButton(1, this.width / 2 - 116, this.height / 2 + 62 + -16, 114, 20, I18n.format("demo.help.buy")));
      this.buttonList.add(new GuiButton(2, this.width / 2 + 2, this.height / 2 + 62 + -16, 114, 20, I18n.format("demo.help.later")));
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
   }

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      switch (button.id) {
         case 1:
            button.enabled = false;

            try {
               Class<?> oclass = Class.forName("java.awt.Desktop");
               Object object = oclass.getMethod("getDesktop").invoke(null);
               oclass.getMethod("browse", URI.class).invoke(object, new URI("http://www.minecraft.net/store?source=demo"));
            } catch (Throwable throwable) {
               logger.error("Couldn't open link", throwable);
            }
            break;
         case 2:
            this.mc.displayGuiScreen((GuiScreen)null);
            this.mc.setIngameFocus();
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      int i2 = (this.width - 248) / 2 + 10;
      int i3 = (this.height - 166) / 2 + 8;
      this.fontRendererObj.getInt(I18n.format("demo.help.title"), i2, i3, 2039583);
      i3 += 12;
      GameSettings gameSettings2 = this.mc.gameSettings;
      this.fontRendererObj
         .getInt(
            I18n.format(
               "demo.help.movementShort",
               GameSettings.getKeyDisplayString(gameSettings2.keyBindForward.getKeyCode()),
               GameSettings.getKeyDisplayString(gameSettings2.keyBindLeft.getKeyCode()),
               GameSettings.getKeyDisplayString(gameSettings2.keyBindBack.getKeyCode()),
               GameSettings.getKeyDisplayString(gameSettings2.keyBindRight.getKeyCode())
            ),
            i2,
            i3,
            5197647
         );
      this.fontRendererObj.getInt(I18n.format("demo.help.movementMouse"), i2, i3 + 12, 5197647);
      this.fontRendererObj
         .getInt(I18n.format("demo.help.jump", GameSettings.getKeyDisplayString(gameSettings2.keyBindJump.getKeyCode())), i2, i3 + 24, 5197647);
      this.fontRendererObj
         .getInt(I18n.format("demo.help.inventory", GameSettings.getKeyDisplayString(gameSettings2.keyBindInventory.getKeyCode())), i2, i3 + 36, 5197647);
      this.fontRendererObj.drawSplitString(I18n.format("demo.help.fullWrapped"), i2, i3 + 68, 218, 2039583);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }
}
