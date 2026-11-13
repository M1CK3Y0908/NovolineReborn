package wtf.novoline.gui.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import wtf.novoline.Novoline;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.gui.AltManagerScreen;
import wtf.novoline.gui.NovolineButton;
import wtf.novoline.gui.TextSelection;
import wtf.novoline.utils.account.AccountManager;
import wtf.novoline.utils.language.Language;
import wtf.novoline.utils.render.BlurRenderer;
import wtf.novoline.utils.render.RenderUtils;

public class AlteningTokenOverlay extends Overlay {
   private TextSelection textSelection;
   private final BlurRenderer overlayBlur = new BlurRenderer(Novoline.framebufferManager);

   @Override
   public void drawScreen(int i, int i2, float f) {
      int i3 = this.guiScreen.width / 2;
      int i4 = this.guiScreen.height / 2;
      Minecraft minecraft2 = Minecraft.getMinecraft();
      ScaledResolution scaledResolution = new ScaledResolution(minecraft2);
      this.overlayBlur.blur("ta-ov-blur", minecraft2.getFramebuffer(), scaledResolution, 10, 0.6F, 2);
      minecraft2.getFramebuffer().bindFramebuffer(true);
      this.overlayBlur.bindResultTexture();
      RenderUtils.drawFullscreenQuad(scaledResolution);
      Fonts.rubikSemiboldSize24.drawCenteredString(Language.translate("account_manager.gui.thealtening.title"), i3, i4 - 40, -1);
      Fonts.rubikRegularSize14.drawString(Language.translate("account_manager.gui.thealtening.token"), i3 - 100, i4, -1);
      this.textSelection.draw(f);
      RenderUtils.setColor(-1);
      super.drawScreen(i, i2, f);
      this.overlayBlur.releaseResult();
   }

   public AlteningTokenOverlay(GuiScreen guiScreen2) {
      super(guiScreen2);
   }

   @Override
   public void keyTyped(char c, int i) {
      this.textSelection.keyTyped(c, i);
      super.keyTyped(c, i);
   }

   @Override
   public void onClose() {
      if (this.guiScreen instanceof AltManagerScreen altManagerScreen) {
         String text = this.textSelection.getTextBuffer().getText();
         altManagerScreen.getAccountListPanel().getAccountManager().setAlteningToken(text);
      }
   }

   @Override
   public void mouseClicked(int i, int i2, int i3) {
      this.textSelection.updateFocus(i, i2);
      super.mouseClicked(i, i2, i3);
   }

   @Override
   public void init() {
      this.textSelection = new TextSelection(Fonts.rubikRegularSize16, this.overlayBlur, this.guiScreen.width / 2 - 100, this.guiScreen.height / 2 + 10)
         .withMask("*", (textSelection2) -> !((TextSelection) textSelection2).isFocused());
      this.guiScreen
         .buttonList
         .add(
            new NovolineButton(
               this.overlayBlur,
               -1,
               this.guiScreen.width / 2 - 100,
               this.guiScreen.height / 2 + 35,
               200,
               20,
               Language.translate("account_manager.gui.thealtening.copy_token")
            )
         );
      GuiScreen guiScreen2 = this.guiScreen;
      if (guiScreen2 instanceof AltManagerScreen) {
         AccountManager accountManager = ((AltManagerScreen)guiScreen2).getAccountListPanel().getAccountManager();
         String alteningToken = accountManager.getAlteningToken();
         if (alteningToken != null) {
            this.textSelection.getSelection().collapse();
            this.textSelection.getTextBuffer().setText(alteningToken);
         }
      }

      super.init();
   }

   @Override
   public void actionPerformed(GuiButton guiButton) {
      switch (guiButton.id) {
         case -1:
            GuiScreen.setClipboardString(System.getProperty("novo.delsyToken"));
      }
   }
}
