package wtf.novoline.gui.overlay;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import wtf.novoline.Novoline.Fonts;
import wtf.novoline.gui.AccountListPanel;
import wtf.novoline.gui.AltManagerScreen;
import wtf.novoline.gui.NovolineButton;
import wtf.novoline.utils.account.MicrosoftAccount;
import wtf.novoline.utils.language.Language;
import wtf.novoline.utils.render.RenderUtils;

public class AccountActionOverlay extends Overlay {
   private final MicrosoftAccount microsoftAccount = new MicrosoftAccount();

   public AccountActionOverlay(GuiScreen guiScreen2) {
      super(guiScreen2);
      this.microsoftAccount.authenticateWithMicrosoft().thenAccept(voidValue -> {
         if (guiScreen2 instanceof AltManagerScreen altManagerScreen) {
            AccountListPanel accountListPanel = altManagerScreen.getAccountListPanel();
            accountListPanel.addEntry(this.microsoftAccount);
            accountListPanel.getAccountManager().add(this.microsoftAccount);
            accountListPanel.refresh();
            altManagerScreen.closeOverlay();
         }
      });
   }

   @Override
   public void drawScreen(int i, int i2, float f) {
      int i3 = this.guiScreen.width / 2;
      int i4 = this.guiScreen.height / 2;
      ScaledResolution scaledResolution = new ScaledResolution(this.minecraft);
      this.blurRenderer.blur("ta-ov-blur", this.minecraft.getFramebuffer(), scaledResolution, 10, 0.6F, 2);
      this.minecraft.getFramebuffer().bindFramebuffer(true);
      this.blurRenderer.bindResultTexture();
      RenderUtils.drawFullscreenQuad(scaledResolution);
      Fonts.rubikSemiboldSize24.drawCenteredString(Language.translate("account_manager.gui.account_action.microsoft_account.title"), i3, i4 - 40, -1);
      Fonts.rubikRegularSize18.drawCenteredString(Language.translate("account_manager.gui.account_action.microsoft_account.body"), i3, i4 - 20, -1);
      RenderUtils.setColor(-1);
      super.drawScreen(i, i2, f);
      this.blurRenderer.releaseResult();
   }

   @Override
   public void init() {
      this.guiScreen
         .buttonList
         .add(
            new NovolineButton(
               this.blurRenderer,
               -1,
               this.guiScreen.width / 2 - 100,
               this.guiScreen.height / 2 + 15,
               Language.translate("account_manager.gui.account_action.microsoft_account.cancel")
            )
         );
   }

   @Override
   public void actionPerformed(GuiButton guiButton) {
      if (guiButton.id == -1) {
         GuiScreen guiScreen2 = this.guiScreen;
         if (guiScreen2 instanceof AltManagerScreen) {
            ((AltManagerScreen)guiScreen2).closeOverlay();
         }
      }
   }

   @Override
   public void onClose() {
      this.microsoftAccount.closeAuthenticator();
   }
}
