package wtf.novoline.gui;

import java.awt.Color;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import wtf.novoline.Novoline;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.utils.ClientPreferences;
import wtf.novoline.utils.language.Language;
import wtf.novoline.utils.language.Language.RuRuType;
import wtf.novoline.utils.render.BlurRenderer;
import wtf.novoline.utils.render.MenuBackgroundShader;
import wtf.novoline.utils.render.RenderUtils;

public class NovolineMainMenu extends GuiScreen {
   private MenuBackgroundShader menuBackgroundShader = new MenuBackgroundShader();
   private int shaderIndex;
   private final BlurRenderer blurRenderer = new BlurRenderer(Novoline.framebufferManager);

   @Override
   protected void actionPerformed(GuiButton guiButton) throws IOException {
      switch (guiButton.id) {
         case 0:
            this.mc.displayGuiScreen(new GuiSelectWorld(this));
            break;
         case 1:
            this.mc.displayGuiScreen(new GuiMultiplayer(this));
            break;
         case 2:
            this.mc.displayGuiScreen(Novoline.getNovoline().getAltManagerScreen());
            break;
         case 3:
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
            break;
         case 4:
            this.mc.shutdown();
            break;
         case 5:
            Desktop.getDesktop().browse(URI.create("https://docs.novoline.wtf"));
            break;
         case 6:
            LanguageButton languageButton = (LanguageButton)guiButton;
            RuRuType[] ruRuTypes = RuRuType.values();

            for (int i = 0; i < ruRuTypes.length; i++) {
               if (ruRuTypes[i] == languageButton.getRuRuType()) {
                  int i2 = i + 1;
                  i2 = i2 == ruRuTypes.length ? 0 : i2;
                  RuRuType ruRuType = ruRuTypes[i2];
                  languageButton.setRuRuType(ruRuType);
                  Novoline.getNovoline().setRuRuType(ruRuType);
                  Novoline.getNovoline().getClientPreferences().setLanguage(ruRuType);
                  return;
               }
            }
            break;
         case 7:
            if (++this.shaderIndex == 3) {
               this.shaderIndex = 0;
            }

            ClientPreferences clientPreferences = Novoline.getNovoline().getClientPreferences();
            clientPreferences.setShaderId(this.shaderIndex);
            this.menuBackgroundShader = new MenuBackgroundShader(this.shaderIndex);
      }
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      this.buttonList
         .add(new NovolineButton(this.blurRenderer, 0, this.width / 2 - 75, this.height / 2 - 10, 150, 20, Language.translate("main_menu.gui.singleplayer")));
      this.buttonList
         .add(
            new NovolineButton(
               this.blurRenderer, 1, this.width / 2 - 75, this.height / 2 - 10 + 20 + 2, 150, 20, Language.translate("main_menu.gui.multiplayer")
            )
         );
      this.buttonList
         .add(
            new NovolineButton(
               this.blurRenderer, 2, this.width / 2 - 75, this.height / 2 - 10 + 40 + 4, 150, 20, Language.translate("main_menu.gui.alt_manager")
            )
         );
      this.buttonList
         .add(
            new NovolineButton(this.blurRenderer, 3, this.width / 2 - 75, this.height / 2 - 10 + 60 + 6, 150, 20, Language.translate("main_menu.gui.options"))
         );
      this.buttonList
         .add(
            new NovolineButton(this.blurRenderer, 4, this.width / 2 - 75, this.height / 2 - 10 + 80 + 8, 150, 20, Language.translate("main_menu.gui.quit_game"))
         );
      this.buttonList.add(new ColorButton(5, 5, this.height - 10, Language.translate("main_menu.gui.documentation"), Color.WHITE));
      this.buttonList.add(new ColorButton(7, 62, this.height - 10, Language.translate("main_menu.gui.switch_image"), Color.WHITE));
      LanguageButton languageButton = new LanguageButton(6, 5, 5);
      languageButton.setRuRuType(Novoline.getNovoline().getRuRuType());
      this.buttonList.add(languageButton);
      ClientPreferences clientPreferences = Novoline.getNovoline().getClientPreferences();
      this.menuBackgroundShader = new MenuBackgroundShader(clientPreferences.getShaderId());
   }

   @Override
   public void drawScreen(int i, int i2, float f) {
      ScaledResolution scaledResolution = new ScaledResolution(this.mc);
      this.menuBackgroundShader.draw(scaledResolution);
      this.blurRenderer.blur("mm-blur", this.mc.getFramebuffer(), scaledResolution, 10, 0.6F, 1);
      this.mc.getFramebuffer().bindFramebuffer(true);
      ResourceLocation resourceLocation = new ResourceLocation("novoline", "images/novoline_purple.png");
      GlStateManager.enableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      RenderUtils.drawTextureWithAlpha(this.width / 2.0F - 50.0F, this.height / 2.0F - 100.0F, 101, 88, 1.0F, resourceLocation);
      GlStateManager.disableAlpha();
      GlStateManager.disableBlend();
      Fonts.rubikRegularSize14
         .drawString(Language.translate("main_menu.gui.release_build", Novoline.BUILD_DATE), 5.0F, this.height - 20, new Color(255, 255, 255, 150).getRGB());
      super.drawScreen(i, i2, f);
      this.blurRenderer.releaseResult();
   }
}
